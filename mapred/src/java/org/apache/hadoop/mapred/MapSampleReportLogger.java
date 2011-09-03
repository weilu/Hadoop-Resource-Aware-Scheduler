package org.apache.hadoop.mapred;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

import java.util.HashMap;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class MapSampleReportLogger {
    public static final Log LOG = LogFactory.getLog(MapSampleReportLogger.class);

    HashMap<String, MapSampleReport> sampleReports = new HashMap<String, MapSampleReport>();

    public void logNetworkCopyDurationAndReduceTracker (String jobId, long duration, String reduceTracker){
        if(duration==0)
            return;
        MapSampleReport report = sampleReports.get(jobId);
        if(report.getNetworkWriteDurationMilliSec()>0 || report.getTrackerName().equals(reduceTracker))
            return;
        
        report.setReduceTrackerName(reduceTracker);
        report.setNetworkWriteDurationMilliSec(duration);
        report.taskNetworkIORate = calculateTaskNetworkIORate(report);

        LOG.info(jobId + " map sample report: " + report.toString());
    }

    public void logLocalReducesPercentage(TaskInProgress tip, float localReducesPercentage){
        MapSampleReport report = getReport(tip, null);
        report.localReducesPercentage = localReducesPercentage;

        long estimatedNetworkWriteBites = (long)((1-report.localReducesPercentage)*report.getDiskWriteBytes());
        report.setNetworkWriteBytes(estimatedNetworkWriteBites);
    }

    public void logStatsUponMapSampleTaskComplete(TaskInProgress tip){

        TaskStatus taskStatus = tip.getTaskStatus(tip.getSampleTaskId());
        SampleTaskStatus sampleStatus = taskStatus.getSampleStatus();
        MapSampleReport report = getReport(tip, null);
        LOG.info("taskID: " + tip.getSampleTaskId());
        report.setSampleMapTaskId(tip.getSampleTaskId());    //log again in case the first sample task fails.

//        long mapDuration = tip.getExecFinishTime() - tip.getLastDispatchTime();
        long mapDuration = sampleStatus.getWriteOutputDoneTime() - sampleStatus.getReadInputStartTime();
        report.setMapDurationMilliSec(mapDuration);
        report.setReadDuration(sampleStatus.getReadInputDoneTime() - sampleStatus.getReadInputStartTime());
        report.setDiskWriteDurationMilliSec(sampleStatus.getWriteOutputDoneTime() - sampleStatus.getWriteOutputStartTime());
        report.setAdditionalSpillDurationMilliSec(sampleStatus.getAdditionalSpillDurationMilliSec());

        report.setDiskWriteBytes(taskStatus.getOutputSize());
        report.setReadSize(tip.getMapInputSize());
        report.setAdditionalSpillBytes(sampleStatus.getAdditionalSpillSize());

        report.taskDiskIORate = calculateTaskDiskIORate(report);

    }

    public void logTrackerStatus(TaskInProgress tip, TaskTrackerStatus trackerStatus){
        MapSampleReport report = getReport(tip, null);
        TaskTrackerStatus.ResourceStatus resourceStatus = trackerStatus.getResourceStatus();
        LOG.info(resourceStatus.toString());

        report.setTrackerCPUScore(resourceStatus.calculateCPUScore());
        report.setTrackerDiskIOScore(resourceStatus.getDiskScore());
        report.setTrackerNetworkIOScore(resourceStatus.getNetworkScore());
    }

    public void logDataLocality(TaskInProgress tip, boolean dataLocal, String taskTracker){
        String jobId = tip.getJob().getJobID().toString();
        MapSampleReport report = getReport(tip, taskTracker);
        report.dataLocal = dataLocal;
        report.setSampleMapTaskId(tip.getSampleTaskId());

        sampleReports.put(jobId, report);  //first method called in the logging sequence, therefore need to put
    }

    private MapSampleReport getReport(TaskInProgress tip, String taskTracker){
        TaskAttemptID taskId = tip.getSampleTaskId();
        String jobId = taskId.getJobID().toString();
        TaskStatus taskStatus = tip.getTaskStatus(taskId);
        String taskTrackerName = taskStatus == null ? taskTracker : taskStatus.getTaskTracker();
        MapSampleReport report = sampleReports.get(jobId);
        if(report == null)
            report = new MapSampleReport(taskTrackerName);
//        else if(taskStatus != null && taskStatus.getRunState() == TaskStatus.State.SUCCEEDED)
//            report.setTrackerName(taskStatus.getTaskTracker());

        return report;
    }

    public TaskAttemptID getSampleMapTaskId(JobID jobId){
        MapSampleReport report = sampleReports.get(jobId.toString());
        if(report != null)
            return report.getSampleMapTaskId();

        return null;
    }

    public String getSampleMapTracker(JobID jobId){
        MapSampleReport report = sampleReports.get(jobId.toString());
        if(report != null)
            return report.getTrackerName();

        return null;
    }

    private long calculateTaskDiskIORate(MapSampleReport report){
        if(report.getDiskReadBytes()==MapSampleReport.UNAVAILABLE
                || report.getDiskWriteBytes() == MapSampleReport.UNAVAILABLE
                || report.getDiskReadDurationMilliSec() == MapSampleReport.UNAVAILABLE
                || report.getDiskWriteDurationMilliSec() == MapSampleReport.UNAVAILABLE)
            return MapSampleReport.UNAVAILABLE;

        long totalDiskIODurationMilliSec
                = (report.getDiskReadDurationMilliSec() + report.getDiskWriteDurationMilliSec());
        if (totalDiskIODurationMilliSec == 0)
            return MapSampleReport.UNAVAILABLE;

        long bytesPerMillSec = (report.getDiskReadBytes() + report.getDiskWriteBytes()) / totalDiskIODurationMilliSec;

        return (long)(bytesPerMillSec/1.024);  //convert to kB/s
    }

    private long calculateTaskNetworkIORate(MapSampleReport report){
        if(report.getNetworkReadBytes()==MapSampleReport.UNAVAILABLE
                || report.getNetworkWriteBytes() == MapSampleReport.UNAVAILABLE
                || report.getNetworkReadDurationMilliSec() == MapSampleReport.UNAVAILABLE
                || report.getNetworkWriteDurationMilliSec() == MapSampleReport.UNAVAILABLE)
            return MapSampleReport.UNAVAILABLE;

        long totalNetworkIODurationMilliSec
                = (report.getNetworkReadDurationMilliSec() + report.getNetworkWriteDurationMilliSec());
        if (totalNetworkIODurationMilliSec == 0)
            return MapSampleReport.UNAVAILABLE;

        long bytesPerMillSec = (report.getNetworkReadBytes() + report.getNetworkWriteBytes()) / totalNetworkIODurationMilliSec;

        return (long)(bytesPerMillSec/1.024);  //convert to kB/s
    }
}
