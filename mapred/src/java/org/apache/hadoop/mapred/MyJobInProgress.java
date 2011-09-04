package org.apache.hadoop.mapred;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.net.Node;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: wei
 * Date: Aug 9, 2011
 * Time: 12:48:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyJobInProgress{
    static final Log LOG = LogFactory.getLog(MyJobInProgress.class);

    private static final int LOCAL_LEVEL = 1;
    private static final int RACK_LOCAL_LEVEL = 2;
    JobInProgress jip;
    Boolean local = null;

    public MyJobInProgress(JobInProgress jip, Boolean local) {
        this.jip = jip;
        this.local = local;
    }

    public Boolean getLocal() {
        return local;
    }

    public void setLocal(Boolean local) {
        this.local = local;
    }

    public JobInProgress getJip() {
        return jip;
    }

    public void setJip(JobInProgress jip) {
        this.jip = jip;
    }

    public synchronized Task obtainNewMapTask(TaskTrackerStatus tts,
                                              int clusterSize,
                                              int numUniqueHosts)
            throws IOException {
        if(local)
            return obtainNewLocalMapTask(tts, clusterSize, numUniqueHosts);
        else if(!local)
            return obtainNewRackLocalMapTask(tts, clusterSize, numUniqueHosts);
        else
            throw new IOException("This should never be evoked!");
    }

    public synchronized Task obtainNewLocalMapTask(TaskTrackerStatus tts,
                                                   int clusterSize,
                                                   int numUniqueHosts)
            throws IOException {
        if (!jip.tasksInited.get()) {
            LOG.info("Cannot create task split for " + jip.profile.getJobID());
            return null;
        }

        return obtainNewMapTask(tts, clusterSize, numUniqueHosts, LOCAL_LEVEL);
    }

    public synchronized Task obtainNewRackLocalMapTask(TaskTrackerStatus tts,
                                                       int clusterSize,
                                                       int numUniqueHosts)
            throws IOException {
        if (!jip.tasksInited.get()) {
            LOG.info("Cannot create task split for " + jip.profile.getJobID());
            return null;
        }

        return obtainNewMapTask(tts, clusterSize, numUniqueHosts,RACK_LOCAL_LEVEL);
    }

    public synchronized Task obtainNewMapTask(TaskTrackerStatus tts,
                                            int clusterSize,
                                            int numUniqueHosts,
                                            int maxCacheLevel
                                           ) throws IOException {
    if (jip.status.getRunState() != JobStatus.RUNNING) {
      LOG.info("Cannot create task split for " + jip.profile.getJobID());
      return null;
    }

    int target = findNewMapTask(tts, clusterSize, numUniqueHosts,
        maxCacheLevel);
    if (target == -1) {
      return null;
    }

    Task result = jip.maps[target].getTaskToRun(tts.getTrackerName());
    if (result != null) {
      if(!jip.maps[target].isJobSetupTask()){
        jip.markSampleScheduled(jip.maps[target], result.getTaskID());
      }

      jip.addRunningTaskToTIP(jip.maps[target], result.getTaskID(), tts, true);
      if(jip.maps[target].getIsSample(result.getTaskID())){
        SampleTaskStatus sampleStatus = result.taskStatus.getSampleStatus();
        sampleStatus.setSampleMapTaskId(result.getTaskID());
        sampleStatus.setSampleMapTracker(tts.getTrackerName());
      }
    }

    return result;
  }

    //TODO: allow 
    private synchronized int findNewMapTask(final TaskTrackerStatus tts,
                                          final int clusterSize,
                                          final int numUniqueHosts,
                                          final int maxCacheLevel) {
    String taskTrackerName = tts.getTrackerName();
    String taskTrackerHost = tts.getHost();
    if (jip.numMapTasks == 0) {
      if(LOG.isDebugEnabled()) {
        LOG.debug("No maps to schedule for " + jip.profile.getJobID());
      }
      return -1;
    }

    TaskInProgress tip = null;

    //
    // Update the last-known clusterSize
    //
    jip.clusterSize = clusterSize;

    if (!jip.shouldRunOnTaskTracker(taskTrackerName)) {
      return -1;
    }

    // Check to ensure this TaskTracker has enough resources to
    // run tasks from this job
    long outSize = jip.resourceEstimator.getEstimatedMapOutputSize();
    long availSpace = tts.getResourceStatus().getAvailableSpace();
    if(availSpace < outSize) {
      LOG.warn("No room for map task. Node " + tts.getHost() +
               " has " + availSpace +
               " bytes free; but we expect map to take " + outSize);

      return -1; //see if a different TIP might work better.
    }


    // For scheduling a map task, we have two caches and a list (optional)
    //  I)   one for non-running task
    //  II)  one for running task (this is for handling speculation)

    // First a look up is done on the non-running cache and on a miss, a look
    // up is done on the running cache. The order for lookup within the cache:
    // from local node to root [bottom up]

    // we only schedule local/rack local tasks at the specified level

    Node node = jip.jobtracker.getNode(tts.getHost());

    //
    // I) Non-running TIP :
    //

    // 1. check from local node to the root [bottom up cache lookup]
    //    i.e if the cache is available and the host has been resolved
    //    (node!=null)
    if (node != null) {
      Node key = node;
      int level = 0;
      int maxLevelToSchedule = maxCacheLevel;
      for (level = 0;level < maxLevelToSchedule; ++level) {
        List<TaskInProgress> cacheForLevel = jip.nonRunningMapCache.get(key);
        if (cacheForLevel != null && level == maxLevelToSchedule-1) {
          tip = jip.findTaskFromList(cacheForLevel, tts,
              numUniqueHosts,level == 0);
          if (tip != null) {
            // Add to running cache
            jip.scheduleMap(tip);

            // remove the cache if its empty
            if (cacheForLevel.size() == 0) {
              jip.nonRunningMapCache.remove(key);
            }

            return tip.getIdWithinJob();
          }
        }
        key = key.getParent();
      }

      // Check if we need to only schedule a local task (node-local/rack-local)
      if (level == maxCacheLevel) {
        return -1;
      }
    }

    //
    // II) Running TIP :
    //

    if (jip.hasSpeculativeMaps()) {
      tip = jip.getSpeculativeMap(taskTrackerName, taskTrackerHost);
      if (tip != null) {
        return tip.getIdWithinJob();
      }
    }
   return -1;
  }
}
