package org.apache.hadoop.mapred;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: wei
 * Date: Aug 9, 2011
 * Time: 12:48:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyJobInProgress{
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
            return jip.obtainNewLocalMapTask(tts, clusterSize, numUniqueHosts);
        else
            return jip.obtainNewNonLocalMapTask(tts, clusterSize, numUniqueHosts);
    }
}
