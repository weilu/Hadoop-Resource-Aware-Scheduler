
package org.apache.hadoop.mapred;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class MapSampleReport{
    public static final long UNAVAILABLE = -1L;
    public static final Log LOG = LogFactory.getLog(MapSampleReport.class);

    private String trackerName;
    private long trackerDiskIORate = UNAVAILABLE;
    private long trackerNetworkIORate = UNAVAILABLE;

    private long diskReadBytes = UNAVAILABLE;
    private long diskWriteBytes = UNAVAILABLE;
    private long networkReadBytes = UNAVAILABLE;
    private long networkWriteBytes = UNAVAILABLE;

    private long mapDurationMilliSec = UNAVAILABLE;
    private long diskReadDurationMilliSec = UNAVAILABLE;
    private long diskWriteDurationMilliSec = UNAVAILABLE;
    private long networkReadDurationMilliSec = UNAVAILABLE;
    private long networkWriteDurationMilliSec = UNAVAILABLE;

    long taskDiskIORate = UNAVAILABLE;
    long taskNetworkIORate = UNAVAILABLE;

    boolean dataLocal = false;

    public MapSampleReport(String trackerName) {
        this.trackerName = trackerName;
    }

    //getters and setters
    public String getTrackerName() {
        return trackerName;
    }

    public void setTrackerName(String trackerName) {
        this.trackerName = trackerName;
    }

    public long getTrackerDiskIORate() {
        return trackerDiskIORate;
    }

    public void setTrackerDiskIORate(long trackerDiskIORate) {
        this.trackerDiskIORate = trackerDiskIORate;
    }

    public long getTrackerNetworkIORate() {
        return trackerNetworkIORate;
    }

    public void setTrackerNetworkIORate(long trackerNetworkIORate) {
        this.trackerNetworkIORate = trackerNetworkIORate;
    }

    public long getDiskReadBytes() {
        return diskReadBytes;
    }

    public void setDiskReadBytes(long diskReadBytes) {
        this.diskReadBytes = diskReadBytes;
    }

    public long getDiskWriteBytes() {
        return diskWriteBytes;
    }

    public void setDiskWriteBytes(long diskWriteBytes) {
        this.diskWriteBytes = diskWriteBytes;
    }

    public long getNetworkReadBytes() {
        return networkReadBytes;
    }

    public void setNetworkReadBytes(long networkReadBytes) {
        this.networkReadBytes = networkReadBytes;
    }

    public long getNetworkWriteBytes() {
        return networkWriteBytes;
    }

    public void setNetworkWriteBytes(long networkWriteBytes) {
        this.networkWriteBytes = networkWriteBytes;
    }

    public long getMapDurationMilliSec() {
        return mapDurationMilliSec;
    }

    public void setMapDurationMilliSec(long mapDurationMilliSec) {
        this.mapDurationMilliSec = mapDurationMilliSec;
    }

    public long getDiskReadDurationMilliSec() {
        return diskReadDurationMilliSec;
    }

    public long getDiskWriteDurationMilliSec() {
        return diskWriteDurationMilliSec;
    }

    public void setDiskWriteDurationMilliSec(long diskWriteDurationMilliSec) {
        this.diskWriteDurationMilliSec = diskWriteDurationMilliSec;
    }

    public long getNetworkReadDurationMilliSec() {
        return networkReadDurationMilliSec;
    }

    public long getNetworkWriteDurationMilliSec() {
        return networkWriteDurationMilliSec;
    }

    public void setNetworkWriteDurationMilliSec(long networkWriteDurationMilliSec) {
        this.networkWriteDurationMilliSec = networkWriteDurationMilliSec;
    }

    public void setReadDuration(long readDuration) {
        if(dataLocal){
            diskReadDurationMilliSec = readDuration;
            networkReadDurationMilliSec = 0;
        }
        else {
            diskReadDurationMilliSec = 0;
            networkReadDurationMilliSec = readDuration;
        }
    }

    public void setReadSize(long sizeBytes) {
        diskReadBytes = sizeBytes;       //TODO:assume always read from disk: need to investigate
        networkReadBytes = dataLocal?0:sizeBytes;

    }

    @Override
    public String toString() {
        return "MapSampleReport{" +
                "trackerName='" + trackerName + '\'' +
                ", trackerDiskIORate=" + trackerDiskIORate +
                ", trackerNetworkIORate=" + trackerNetworkIORate +
                ", diskReadBytes=" + diskReadBytes +
                ", diskWriteBytes=" + diskWriteBytes +
                ", networkReadBytes=" + networkReadBytes +
                ", networkWriteBytes=" + networkWriteBytes +
                ", mapDurationMilliSec=" + mapDurationMilliSec +
                ", diskReadDurationMilliSec=" + diskReadDurationMilliSec +
                ", diskWriteDurationMilliSec=" + diskWriteDurationMilliSec +
                ", networkReadDurationMilliSec=" + networkReadDurationMilliSec +
                ", networkWriteDurationMilliSec=" + networkWriteDurationMilliSec +
                ", taskDiskIORate=" + taskDiskIORate +
                ", taskNetworkIORate=" + taskNetworkIORate +
                ", dataLocal=" + dataLocal +
                '}';
    }
}
