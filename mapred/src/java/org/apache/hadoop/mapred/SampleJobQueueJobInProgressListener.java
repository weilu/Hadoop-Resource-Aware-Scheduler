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
import org.apache.hadoop.mapred.JobStatusChangeEvent.EventType;
import org.apache.hadoop.mapreduce.JobSampleState;

import java.util.*;

class SampleJobQueueJobInProgressListener extends JobInProgressListener {

    public static final Log LOG = LogFactory.getLog(SampleJobQueueJobInProgressListener.class);

    /** A class that groups all the information from a {@link org.apache.hadoop.mapred.JobInProgress} that
     * is necessary for scheduling a job.
     */
    static class JobSchedulingInfoWithSampledFlag extends JobQueueJobInProgressListener.JobSchedulingInfo{
        private JobSampleState sampleState;

        public JobSchedulingInfoWithSampledFlag(JobInProgress jip) {
            this(jip.getStatus());
        }

        public JobSchedulingInfoWithSampledFlag(JobStatus status) {
            super(status);
            sampleState = status.getSampleState();
        }

        JobSampleState sampleState(){
            return sampleState;
        }

    }

    static final Comparator<JobSchedulingInfoWithSampledFlag> FIFO_JOB_QUEUE_COMPARATOR
            = new Comparator<JobSchedulingInfoWithSampledFlag>() {
        public int compare(JobSchedulingInfoWithSampledFlag o1, JobSchedulingInfoWithSampledFlag o2) {

            int res = o1.sampleState().compareTo(o2.sampleState());
            LOG.debug("[compare:sampleState] - o1: " + o1.getJobID() + o1.sampleState() + ", o2: " + o2.getJobID() + o2.sampleState() + ", res: " + res);
            if(res == 0 ){
                res = o1.getPriority().compareTo(o2.getPriority());
                LOG.debug("[compare:priority] - o1: " + o1.getJobID() + ", o2: " + o2.getJobID() + ", res: " + res);
            }
            if (res == 0) {
                if (o1.getStartTime() < o2.getStartTime()) {
                    res = -1;
                } else {
                    res = (o1.getStartTime() == o2.getStartTime() ? 0 : 1);
                }
                LOG.debug("[compare:startTime] - o1: " + o1.getJobID() + ", o2: " + o2.getJobID() + ", res: " + res);
            }
            if (res == 0) {
                res = o1.getJobID().compareTo(o2.getJobID());
                LOG.debug("[compare:jobID] - o1: " + o1.getJobID() + ", o2: " + o2.getJobID() + ", res: " + res);
            }
            return res;
        }
    };

    private Map<JobSchedulingInfoWithSampledFlag, JobInProgress> jobQueue;

    public SampleJobQueueJobInProgressListener() {
        this(new TreeMap<JobSchedulingInfoWithSampledFlag,
                JobInProgress>(FIFO_JOB_QUEUE_COMPARATOR));
    }

    /**
     * For clients that want to provide their own job priorities.
     * @param jobQueue A collection whose iterator returns jobs in priority order.
     */
    protected SampleJobQueueJobInProgressListener(Map<JobSchedulingInfoWithSampledFlag,
            JobInProgress> jobQueue) {
        this.jobQueue = Collections.synchronizedMap(jobQueue);
    }

    /**
     * Returns a synchronized view of the job queue.
     */
    public Collection<JobInProgress> getJobQueue() {
        return jobQueue.values();
    }

    @Override
    public void jobAdded(JobInProgress job) {
        jobQueue.put(new JobSchedulingInfoWithSampledFlag(job.getStatus()), job);
    }

    // Job will be removed once the job completes
    @Override
    public void jobRemoved(JobInProgress job) {}

    private void jobCompleted(JobSchedulingInfoWithSampledFlag oldInfo) {
        jobQueue.remove(oldInfo);
    }

    @Override
    public synchronized void jobUpdated(JobChangeEvent event) {
        JobInProgress job = event.getJobInProgress();
        if (event instanceof JobStatusChangeEvent) {
            // Check if the ordering of the job has changed
            // For now priority and start-time can change the job ordering
            JobStatusChangeEvent statusEvent = (JobStatusChangeEvent)event;
            JobSchedulingInfoWithSampledFlag oldInfo =
                    new JobSchedulingInfoWithSampledFlag(statusEvent.getOldStatus());
            if (statusEvent.getEventType() == EventType.PRIORITY_CHANGED
                    || statusEvent.getEventType() == EventType.START_TIME_CHANGED
                    || statusEvent.getEventType() == EventType.SAMPLE_STATE_CHANGED) {
                // Make a priority change or sample state change
                reorderJobs(job, oldInfo);
            } else if (statusEvent.getEventType() == EventType.RUN_STATE_CHANGED) {
                // Check if the job is complete
                int runState = statusEvent.getNewStatus().getRunState();
                if (runState == JobStatus.SUCCEEDED
                        || runState == JobStatus.FAILED
                        || runState == JobStatus.KILLED) {
                    jobCompleted(oldInfo);
                }
            }
        }
    }

    private void reorderJobs(JobInProgress job, JobSchedulingInfoWithSampledFlag oldInfo) {
        synchronized (jobQueue) {
            LOG.debug("[Reordering job queue...] before remove size: " + jobQueue.size());
            jobQueue.remove(oldInfo);
            LOG.debug("[Reordering job queue...] after remove size: " + jobQueue.size());
            JobSchedulingInfoWithSampledFlag newInfo = new JobSchedulingInfoWithSampledFlag(job);
            jobQueue.put(newInfo, job);
            LOG.debug("[Reordering job queue...] after put size: " + jobQueue.size());
        }
    }

}
