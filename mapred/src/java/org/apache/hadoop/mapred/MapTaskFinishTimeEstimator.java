package org.apache.hadoop.mapred;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

import java.util.HashMap;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class MapTaskFinishTimeEstimator {
    public static final Log LOG = LogFactory.getLog(MapTaskFinishTimeEstimator.class);
    public static final long UNAVAILABLE = -1L;

    MapSampleReport sampleReport;

    long estimatedCpuTime = UNAVAILABLE;
    long estimatedDiskTime = UNAVAILABLE;
    long estimatedNetworkTime = UNAVAILABLE;
    long estimatedFinishTime = UNAVAILABLE;

    long currentTrackerCpuScore = UNAVAILABLE;
    long currentTrackerDiskScore = UNAVAILABLE;
    long currentTrackerNetworkScore = UNAVAILABLE;

    long currentDiskReadSize = UNAVAILABLE;
    long currentDiskWriteSize = UNAVAILABLE;
    long currentNetworkReadSize = UNAVAILABLE;
    long currentNetworkWriteSize = UNAVAILABLE;

    boolean readLocal;
    float localReducePercent;

    //task info
    public MapTaskFinishTimeEstimator(MapSampleReport sampleReport) {
        this.sampleReport = sampleReport;
    }

    //tracker info
    public void setCurrentTrackerResourceScores(TaskTrackerStatus.ResourceStatus resourceStatus) {
        currentTrackerCpuScore = resourceStatus.calculateCPUScore();
        currentTrackerDiskScore = resourceStatus.calculateDiskScore();
        currentTrackerNetworkScore = resourceStatus.calculateNetworkScore();
    }

    //tracker dependent task info
    public void setLocalReducePercent(float localReducePercent) {
        this.localReducePercent = localReducePercent;
    }

    public void setReadSizes(long inputSize, boolean readLocal){
        this.readLocal = readLocal;
        if(this.readLocal){
            currentDiskReadSize = inputSize;
            currentNetworkReadSize = 0;
        }
        else{
            currentNetworkReadSize = inputSize;
            currentDiskReadSize = 0;
        }
    }

    //disk IO estimation
    private void estimateDiskWriteSize(){
        long sampleReadBytes = sampleReport.getDiskReadBytes() + sampleReport.getNetworkReadBytes();
        long currentReadBytes = currentDiskReadSize + currentNetworkReadSize;
        currentDiskWriteSize = sampleReport.getDiskWriteBytes() * (currentReadBytes/sampleReadBytes);
    }

    private void estimateDiskTime(){
        long sampleIOTime = sampleReport.getDiskReadDurationMilliSec() + sampleReport.getDiskWriteDurationMilliSec()
                + sampleReport.getAdditionalSpillDurationMilliSec();
        long sampleIOSize = sampleReport.getDiskReadBytes() + sampleReport.getDiskWriteBytes();
        long currentIOSize = currentDiskReadSize + currentDiskWriteSize;
        estimatedDiskTime = sampleIOTime * (currentIOSize/sampleIOSize)
                * (sampleReport.getTrackerDiskIOScore()/currentTrackerDiskScore);
    }

    //network IO estimation
    private void estimateNetworkWriteSize(){
        if(currentDiskWriteSize == UNAVAILABLE)
            estimateDiskWriteSize();

        currentNetworkWriteSize = (long)((1-localReducePercent) * currentDiskWriteSize);
    }

    private void estimateNetworkTime(){
        long sampleIOTime = sampleReport.getNetworkReadDurationMilliSec() + sampleReport.getNetworkWriteDurationMilliSec();
        long sampleIOSize = sampleReport.getNetworkReadBytes() + sampleReport.getNetworkWriteBytes();
        long currentIOSize = currentNetworkReadSize + currentNetworkWriteSize;
        estimatedNetworkTime = sampleIOTime * (currentIOSize/sampleIOSize) * (sampleReport.getTrackerNetworkIOScore()/currentTrackerNetworkScore);
    }

    //cpu estimation
    private void estimateCPUTime(){
        long sampleCPUTime = sampleReport.getMapDurationMilliSec()
                - sampleReport.getDiskReadDurationMilliSec() - sampleReport.getDiskWriteDurationMilliSec()
                - sampleReport.getNetworkReadDurationMilliSec() - sampleReport.getNetworkWriteDurationMilliSec();
        estimatedCpuTime = (sampleCPUTime<0?0:sampleCPUTime) * (sampleReport.getTrackerCPUScore()/currentTrackerCpuScore);
    }

    private void sumEstimatedTime(){
        estimatedFinishTime = estimatedCpuTime + estimatedDiskTime + estimatedNetworkTime;
    }

    public void estimate(){
        if(localReducePercent==UNAVAILABLE || currentDiskReadSize==UNAVAILABLE || currentNetworkReadSize==UNAVAILABLE)
            return;

        if(currentTrackerCpuScore==UNAVAILABLE || currentTrackerDiskScore==UNAVAILABLE || currentTrackerNetworkScore==UNAVAILABLE)
            return;

        estimateCPUTime();
        estimateDiskWriteSize();  //important: needs to be before the rest of the estimations
        estimateDiskTime();
        estimateNetworkWriteSize();
        estimateNetworkTime();
        sumEstimatedTime();
    }
}
