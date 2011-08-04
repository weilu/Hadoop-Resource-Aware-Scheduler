
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

    private TaskAttemptID sampleMapTaskId;
    private String trackerName;
    private String reduceTrackerName;
    private long trackerDiskIOScore = UNAVAILABLE;
    private long trackerNetworkIOScore = UNAVAILABLE;
    private long trackerCPUScore = UNAVAILABLE;

    private long diskReadBytes = UNAVAILABLE;
    private long diskWriteBytes = UNAVAILABLE;
    private long networkReadBytes = UNAVAILABLE;
    private long networkWriteBytes = UNAVAILABLE;
    private long additionalSpillBytes = UNAVAILABLE;

    private long mapDurationMilliSec = UNAVAILABLE;
    private long diskReadDurationMilliSec = UNAVAILABLE;
    private long diskWriteDurationMilliSec = UNAVAILABLE;
    private long networkReadDurationMilliSec = UNAVAILABLE;
    private long networkWriteDurationMilliSec = UNAVAILABLE;
    private long additionalSpillDurationMilliSec = UNAVAILABLE;

    long taskDiskIORate = UNAVAILABLE;
    long taskNetworkIORate = UNAVAILABLE;

    boolean dataLocal = false;
    float localReducesPercentage = 0f;

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

    public long getTrackerDiskIOScore() {
        return trackerDiskIOScore;
    }

    public void setTrackerDiskIOScore(long trackerDiskIOScore) {
        this.trackerDiskIOScore = trackerDiskIOScore;
    }

    public long getTrackerNetworkIOScore() {
        return trackerNetworkIOScore;
    }

    public void setTrackerNetworkIOScore(long trackerNetworkIOScore) {
        this.trackerNetworkIOScore = trackerNetworkIOScore;
    }

    public long getTrackerCPUScore() {
        return trackerCPUScore;
    }

    public void setTrackerCPUScore(long trackerCPUScore) {
        this.trackerCPUScore = trackerCPUScore;
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

    public long getAdditionalSpillBytes() {
        return additionalSpillBytes;
    }

    public void setAdditionalSpillBytes(long additionalSpillBytes) {
        this.additionalSpillBytes = additionalSpillBytes;
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

    public long getAdditionalSpillDurationMilliSec() {
        return additionalSpillDurationMilliSec;
    }

    public void setAdditionalSpillDurationMilliSec(long additionalSpillDurationMilliSec) {
        this.additionalSpillDurationMilliSec = additionalSpillDurationMilliSec;
    }

    public String getReduceTrackerName() {
        return reduceTrackerName;
    }

    public void setReduceTrackerName(String reduceTrackerName) {
        this.reduceTrackerName = reduceTrackerName;
    }

    public TaskAttemptID getSampleMapTaskId() {
        return sampleMapTaskId;
    }

    public void setSampleMapTaskId(TaskAttemptID sampleMapTaskId) {
        this.sampleMapTaskId = sampleMapTaskId;
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
        if(dataLocal){
            diskReadBytes = sizeBytes;    
            networkReadBytes = 0;
        }
        else{
            networkReadBytes = sizeBytes;
            diskReadBytes = 0;
        }

    }

    @Override
    public String toString() {
        return "MapSampleReport{" +
                "sampleMapTaskId=" + sampleMapTaskId +
                ", trackerName='" + trackerName + '\'' +
                ", reduceTrackerName='" + reduceTrackerName + '\'' +
                ", trackerDiskIOScore=" + trackerDiskIOScore +
                ", trackerNetworkIOScore=" + trackerNetworkIOScore +
                ", trackerCPUScore=" + trackerCPUScore +
                ", diskReadBytes=" + diskReadBytes +
                ", diskWriteBytes=" + diskWriteBytes +
                ", networkReadBytes=" + networkReadBytes +
                ", networkWriteBytes=" + networkWriteBytes +
                ", additionalSpillBytes=" + additionalSpillBytes +
                ", mapDurationMilliSec=" + mapDurationMilliSec +
                ", diskReadDurationMilliSec=" + diskReadDurationMilliSec +
                ", diskWriteDurationMilliSec=" + diskWriteDurationMilliSec +
                ", networkReadDurationMilliSec=" + networkReadDurationMilliSec +
                ", networkWriteDurationMilliSec=" + networkWriteDurationMilliSec +
                ", additionalSpillDurationMilliSec=" + additionalSpillDurationMilliSec +
                ", taskDiskIORate=" + taskDiskIORate +
                ", taskNetworkIORate=" + taskNetworkIORate +
                ", dataLocal=" + dataLocal +
                ", localReducesPercentage=" + localReducesPercentage +
                '}';
    }
}
