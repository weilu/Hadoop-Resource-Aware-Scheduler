/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapred;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.JobSampleState;
import org.apache.hadoop.mapreduce.server.jobtracker.JTConfig;
import org.apache.hadoop.mapreduce.server.jobtracker.TaskTracker;

import java.io.IOException;
import java.util.*;

/**
 * A {@link TaskScheduler} that keeps jobs in a queue in priority order (FIFO
 * by default).
 */
class ResourceFifoScheduler extends TaskScheduler {

    private static final int MIN_CLUSTER_SIZE_FOR_PADDING = 3;
    public static final Log LOG = LogFactory.getLog(ResourceFifoScheduler.class);

    protected EagerTaskInitializationListener eagerTaskInitializationListener;
    protected ResourceJobQueueListener resourceJobQueueListener;
    private float padFraction;

    private HashMap<String, Long> taskIdToTimeEstimated = new HashMap<String, Long>();

    public ResourceFifoScheduler() {
        this.resourceJobQueueListener = new ResourceJobQueueListener();
    }

    @Override
    public synchronized void start() throws IOException {
        super.start();
        taskTrackerManager.addJobInProgressListener(resourceJobQueueListener);
        eagerTaskInitializationListener.setTaskTrackerManager(taskTrackerManager);
        eagerTaskInitializationListener.start();
        taskTrackerManager.addJobInProgressListener(
                eagerTaskInitializationListener);
    }

    @Override
    public synchronized void terminate() throws IOException {
        if (resourceJobQueueListener != null) {
            taskTrackerManager.removeJobInProgressListener(
                    resourceJobQueueListener);
        }
        if (eagerTaskInitializationListener != null) {
            taskTrackerManager.removeJobInProgressListener(
                    eagerTaskInitializationListener);
            eagerTaskInitializationListener.terminate();
        }
        super.terminate();
    }

    @Override
    public synchronized void setConf(Configuration conf) {
        super.setConf(conf);
        padFraction = conf.getFloat(JTConfig.JT_TASK_ALLOC_PAD_FRACTION,
                0.01f);
        this.eagerTaskInitializationListener =
                new EagerTaskInitializationListener(conf);
    }

    @Override
    public synchronized List<Task> assignTasks(TaskTracker taskTracker)
            throws IOException {
        TaskTrackerStatus taskTrackerStatus = taskTracker.getStatus();
        ClusterStatus clusterStatus = taskTrackerManager.getClusterStatus();
        final int numTaskTrackers = clusterStatus.getTaskTrackers();
        final int clusterMapCapacity = clusterStatus.getMaxMapTasks();
        final int clusterReduceCapacity = clusterStatus.getMaxReduceTasks();

        Collection<JobInProgress> jobQueue =
                resourceJobQueueListener.getJobQueue();
        ArrayList<JobInProgress> unsampledJobs = new ArrayList<JobInProgress>();
        ArrayList<JobInProgress> sampledJobs = new ArrayList<JobInProgress>();
        ArrayList<JobInProgress> sampleScheduledJobs = new ArrayList<JobInProgress>();

        //
        // Get map + reduce counts for the current tracker.
        //
        final int trackerMapCapacity = taskTrackerStatus.getMaxMapSlots();
        final int trackerReduceCapacity = taskTrackerStatus.getMaxReduceSlots();
        final int trackerRunningMaps = taskTrackerStatus.countMapTasks();
        final int trackerRunningReduces = taskTrackerStatus.countReduceTasks();

        // Assigned tasks
        List<Task> assignedTasks = new ArrayList<Task>();

        //
        // 1. Compute (running + pending) map and reduce task numbers across pool
        // 2. partition the queue based on sample state
        //
        int remainingReduceLoad = 0;
        int remainingMapLoad = 0;
        synchronized (jobQueue) {
            for (JobInProgress job : jobQueue) {
                if (job.getStatus().getRunState() == JobStatus.RUNNING) {
                    remainingMapLoad += (job.desiredMaps() - job.finishedMaps());
                    if (job.scheduleReduces()) {
                        remainingReduceLoad +=
                                (job.desiredReduces() - job.finishedReduces());
                    }
                }

                JobSampleState sampleState = job.getStatus().getSampleState();
                if (sampleState == JobSampleState.WAITING)
                    unsampledJobs.add(job);
                else if(sampleState == JobSampleState.DONE)
                    sampledJobs.add(job);
                else
                    sampleScheduledJobs.add(job);
            }
        }

        ArrayList<MyJobInProgress> myJobs = parseJobInProgress(unsampledJobs, false, taskTrackerStatus);
        myJobs.addAll(parseJobInProgress(sampledJobs, true, taskTrackerStatus));
        myJobs.addAll(parseJobInProgress(sampleScheduledJobs, false, taskTrackerStatus));
        LOG.debug("myJobs size: " + myJobs.size());


        // Compute the 'load factor' for maps and reduces
        double mapLoadFactor = 0.0;
        if (clusterMapCapacity > 0) {
            mapLoadFactor = (double)remainingMapLoad / clusterMapCapacity;
        }
        double reduceLoadFactor = 0.0;
        if (clusterReduceCapacity > 0) {
            reduceLoadFactor = (double)remainingReduceLoad / clusterReduceCapacity;
        }

        //
        // In the below steps, we allocate first map tasks (if appropriate),
        // and then reduce tasks if appropriate.  We go through all jobs
        // in order of job arrival; jobs only get serviced if their
        // predecessors are serviced, too.
        //

        //
        // We assign tasks to the current taskTracker if the given machine
        // has a workload that's less than the maximum load of that kind of
        // task.
        // However, if the cluster is close to getting loaded i.e. we don't
        // have enough _padding_ for speculative executions etc., we only
        // schedule the "highest priority" task i.e. the task from the job
        // with the highest priority.
        //

        final int trackerCurrentMapCapacity =
                Math.min((int)Math.ceil(mapLoadFactor * trackerMapCapacity),
                        trackerMapCapacity);
        int availableMapSlots = trackerCurrentMapCapacity - trackerRunningMaps;
        boolean exceededMapPadding = false;
        if (availableMapSlots > 0) {
            exceededMapPadding =
                    exceededPadding(true, clusterStatus, trackerMapCapacity);
        }

        int numLocalMaps = 0;
        int numRackLocalMaps = 0;
        int numRackOrMachineLocalMaps = 0;
        int numNonLocalMaps = 0;
        scheduleMaps:
        for (int i=0; i < availableMapSlots; ++i) {
            synchronized (myJobs) {
                for (MyJobInProgress myJob : myJobs) {
                    JobInProgress job = myJob.getJip();

                    LOG.debug("job ID: " + job.getJobID() + ", job state: " + job.getStatus().getRunState());
                    if (job.getStatus().getRunState() != JobStatus.RUNNING) {
                        continue;
                    }

                    Task t = null;
                    // Try to schedule a node-local or rack-local Map task
                    t = myJob.obtainNewLocalMapTask(taskTrackerStatus, numTaskTrackers,
                            taskTrackerManager.getNumberOfUniqueHosts());
                    if (t != null) {
                        assignedTasks.add(t);
                        ++numRackOrMachineLocalMaps;

                        MapTaskFinishTimeEstimator estimator = myJobToTimeEstimated.get(myJob);
                        if(estimator!=null){
                            Long estimatedTime = estimator.estimatedFinishTime;
                            if(estimatedTime != null){
                                taskIdToTimeEstimated.put(t.getTaskID().toString(), estimatedTime);
                                LOG.debug(t.getTaskID() + ", " + estimatedTime + ", estimated time");
                                LOG.debug(t.getTaskID() + ", " + estimator.estimatedCpuTime + ", estimated cpu time");
                                LOG.debug(t.getTaskID() + ", " + estimator.estimatedDiskTime + ", estimated disk time");
                                LOG.debug(t.getTaskID() + ", " + estimator.estimatedNetworkTime + ", estimated network time");
                            }
                        }

                        // Don't assign map tasks to the hilt!
                        // Leave some free slots in the cluster for future task-failures,
                        // speculative tasks etc. beyond the highest priority job
                        if (exceededMapPadding) {
                            break scheduleMaps;
                        }

                        // Try all jobs again for the next Map task
                        break;
                    }

                    // Try to schedule a rack-local Map task
                    t = myJob.obtainNewRackLocalMapTask(taskTrackerStatus, numTaskTrackers,
                            taskTrackerManager.getNumberOfUniqueHosts());

                    if (t != null) {
                        assignedTasks.add(t);
                        ++numNonLocalMaps;
                        LOG.debug(t.getTaskID() + " none local task scheduled on: " +  taskTracker.getTrackerName());


                        // We assign at most 1 off-switch or speculative task
                        // This is to prevent TaskTrackers from stealing local-tasks
                        // from other TaskTrackers.
                        break scheduleMaps;
                    }
                }
                LOG.debug("Remaining map slots: " + (availableMapSlots-i));
            }
        }
        int assignedMaps = assignedTasks.size();

        //
        // Same thing, but for reduce tasks
        // However we _never_ assign more than 1 reduce task per heartbeat
        //
        final int trackerCurrentReduceCapacity =
                Math.min((int)Math.ceil(reduceLoadFactor * trackerReduceCapacity),
                        trackerReduceCapacity);
        final int availableReduceSlots =
                Math.min((trackerCurrentReduceCapacity - trackerRunningReduces), 1);
        boolean exceededReducePadding = false;
        if (availableReduceSlots > 0) {
            //avoid schedule reduce task on the machine where sample task is scheduled
            exceededReducePadding = exceededPadding(false, clusterStatus,
                    trackerReduceCapacity);
            synchronized (sampledJobs) {
                for (JobInProgress job : sampledJobs) {

                    if (job.getStatus().getRunState() != JobStatus.RUNNING ||
                            job.numReduceTasks == 0) {
                        continue;
                    }

                    Task t =
                            job.obtainNewReduceTask(taskTrackerStatus, numTaskTrackers,
                                    taskTrackerManager.getNumberOfUniqueHosts()
                            );
                    if (t != null) {
                        assignedTasks.add(t);
                        break;
                    }

                    // Don't assign reduce tasks to the hilt!
                    // Leave some free slots in the cluster for future task-failures,
                    // speculative tasks etc. beyond the highest priority job
                    if (exceededReducePadding) {
                        break;
                    }
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Task assignments for " + taskTrackerStatus.getTrackerName() + " --> " +
                    "[" + mapLoadFactor + ", " + trackerMapCapacity + ", " +
                    trackerCurrentMapCapacity + ", " + trackerRunningMaps + "] -> [" +
                    (trackerCurrentMapCapacity - trackerRunningMaps) + ", " +
                    assignedMaps + " (" + numLocalMaps + ", " + numNonLocalMaps +
                    ")] [" + reduceLoadFactor + ", " + trackerReduceCapacity + ", " +
                    trackerCurrentReduceCapacity + "," + trackerRunningReduces +
                    "] -> [" + (trackerCurrentReduceCapacity - trackerRunningReduces) +
                    ", " + (assignedTasks.size()-assignedMaps) + "]");
        }

        return assignedTasks;
    }

    private boolean exceededPadding(boolean isMapTask,
                                    ClusterStatus clusterStatus,
                                    int maxTaskTrackerSlots) {
        int numTaskTrackers = clusterStatus.getTaskTrackers();
        int totalTasks =
                (isMapTask) ? clusterStatus.getMapTasks() :
                        clusterStatus.getReduceTasks();
        int totalTaskCapacity =
                isMapTask ? clusterStatus.getMaxMapTasks() :
                        clusterStatus.getMaxReduceTasks();

        Collection<JobInProgress> jobQueue =
                resourceJobQueueListener.getJobQueue();

        boolean exceededPadding = false;
        synchronized (jobQueue) {
            int totalNeededTasks = 0;
            for (JobInProgress job : jobQueue) {
                if (job.getStatus().getRunState() != JobStatus.RUNNING ||
                        job.numReduceTasks == 0) {
                    continue;
                }

                //
                // Beyond the highest-priority task, reserve a little
                // room for failures and speculative executions; don't
                // schedule tasks to the hilt.
                //
                totalNeededTasks +=
                        isMapTask ? job.desiredMaps() : job.desiredReduces();
                int padding = 0;
                if (numTaskTrackers > MIN_CLUSTER_SIZE_FOR_PADDING) {
                    padding =
                            Math.min(maxTaskTrackerSlots,
                                    (int) (totalNeededTasks * padFraction));
                }
                if (totalTasks + padding >= totalTaskCapacity) {
                    exceededPadding = true;
                    break;
                }
            }
        }

        return exceededPadding;
    }

    @Override
    public synchronized Collection<JobInProgress> getJobs(String queueName) {
        return resourceJobQueueListener.getJobQueue();
    }

    public static class JobSchedulingInfo {
        private JobPriority priority;
        private long timeEstimated;
        private long startTime;
        private JobID id;
        private MapTaskFinishTimeEstimator estimator;

        public JobSchedulingInfo(MyJobInProgress myJob, TaskTrackerStatus trackerStatus,
                                 MapSampleReport sampleReport) {
            JobInProgress jip = myJob.getJip();
            JobStatus status = jip.getStatus();
            priority = status.getJobPriority();
            startTime = status.getStartTime();
            id = status.getJobID();

            estimator = new MapTaskFinishTimeEstimator(sampleReport);
            estimator.setCurrentTrackerResourceScores(trackerStatus.getResourceStatus());
            estimator.setLocalReducePercent(jip.getLocalReduceRateForTaskTracker(trackerStatus.getTrackerName()));

            long inputSize = jip.getInputLength()/jip.desiredMaps();
            estimator.setReadSizes(inputSize, myJob.getLocal());

            estimator.estimate();
            timeEstimated = estimator.estimatedFinishTime;
            LOG.debug(id + " timeEstimated: " + timeEstimated);
        }

        JobPriority getPriority() {return priority;}
        long getTimeEstimated() {return timeEstimated;}
        long getStartTime() {return startTime;}
        JobID getJobID() {return id;}

        public MapTaskFinishTimeEstimator getEstimator() {
            return estimator;
        }
    }

    private HashMap<MyJobInProgress, MapTaskFinishTimeEstimator> myJobToTimeEstimated
            = new HashMap<MyJobInProgress, MapTaskFinishTimeEstimator>();

    private ArrayList<MyJobInProgress> parseJobInProgress(ArrayList<JobInProgress> jobs,
                                                          boolean reorder, TaskTrackerStatus trackerStatus){
        ArrayList<MyJobInProgress> myJobs = new ArrayList<MyJobInProgress>();
        if(!reorder){
            for(JobInProgress job : jobs){
                myJobs.add(new MyJobInProgress(job, null));
            }
        }else{
            if(!(taskTrackerManager instanceof JobTracker))
                return parseJobInProgress(jobs, false, trackerStatus);
            JobTracker jobtracker = (JobTracker)taskTrackerManager;

            final Comparator<JobSchedulingInfo> FASTEST_TASK_FIRST_COMPARATOR
                    = new ResourceSchedulingAlgorithms.FifoComparator();
            TreeMap<JobSchedulingInfo, MyJobInProgress> infoToJob
                    = new TreeMap<JobSchedulingInfo, MyJobInProgress>(FASTEST_TASK_FIRST_COMPARATOR);

            for(JobInProgress job : jobs){
                MapSampleReport sampleReport = jobtracker.mapLogger.sampleReports.get(job.getJobID().toString());
                MyJobInProgress j1 = new MyJobInProgress(job, true);
                JobSchedulingInfo info1 = new JobSchedulingInfo(j1, trackerStatus, sampleReport);
                infoToJob.put(info1, j1);
                myJobToTimeEstimated.put(j1, info1.getEstimator());

                MyJobInProgress j2 = new MyJobInProgress(job, false);
                JobSchedulingInfo info2 = new JobSchedulingInfo(j2, trackerStatus, sampleReport);
                infoToJob.put(info2, j2);
                myJobToTimeEstimated.put(j2, info2.getEstimator());
            }

            myJobs.addAll(infoToJob.values());
        }

        return myJobs;
    }

}
