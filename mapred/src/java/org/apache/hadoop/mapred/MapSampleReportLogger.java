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

    public void logDataLocality(TaskInProgress tip, boolean dataLocal, String taskTracker){
        String jobId = tip.getJob().getJobID().toString();
        MapSampleReport report = getReport(tip, null);
        report.dataLocal = dataLocal;

        sampleReports.put(jobId, report);
    }

    public void logStatsUponTaskComplete(TaskInProgress tip){

        String jobId = tip.getJob().getJobID().toString();
        TaskStatus taskStatus = tip.getTaskStatus(tip.getSampleTaskId());
        LOG.info("taskID: " + tip.getSampleTaskId());
        MapSampleReport report = getReport(tip, null);

//        long mapDuration = tip.getExecFinishTime() - tip.getLastDispatchTime();
        long mapDuration = taskStatus.getWriteOutputDoneTime() - taskStatus.getReadInputStartTime();
        report.setMapDurationMilliSec(mapDuration);
        report.setDiskWriteBytes(taskStatus.getOutputSize());
        report.setReadSize(tip.getMapInputSize());
        report.setReadDuration(taskStatus.getReadInputDoneTime() - taskStatus.getReadInputStartTime());
        report.setDiskWriteDurationMilliSec(taskStatus.getWriteOutputDoneTime() - taskStatus.getWriteOutputStartTime());
        report.taskDiskIORate = calculateTaskDiskIORate(report);

        LOG.info(jobId + " map sample report: " + report.toString());
        sampleReports.put(jobId, report);
    }

    private MapSampleReport getReport(TaskInProgress tip, String taskTracker){
        TaskAttemptID taskId = tip.getSampleTaskId();
        String jobId = taskId.getJobID().toString();
        TaskStatus taskStatus = tip.getTaskStatus(taskId);
        String taskTrackerName = taskStatus == null ? taskTracker : taskStatus.getTaskTracker();
        MapSampleReport report = sampleReports.get(jobId);
        if(report == null)
            report = new MapSampleReport(taskTrackerName);
        else if(taskStatus != null && taskStatus.getRunState() == TaskStatus.State.SUCCEEDED)
            report.setTrackerName(taskStatus.getTaskTracker());

        return report;
    }

    private long calculateTaskDiskIORate(MapSampleReport report){
        if(report.getDiskReadBytes()==MapSampleReport.UNAVAILABLE
                || report.getDiskWriteBytes() == MapSampleReport.UNAVAILABLE
                || report.getMapDurationMilliSec() == MapSampleReport.UNAVAILABLE)
            return MapSampleReport.UNAVAILABLE;

        long totalDiskIODurationMilliSec
                = (report.getDiskReadDurationMilliSec() + report.getDiskWriteDurationMilliSec());
        if (totalDiskIODurationMilliSec == 0)
            return MapSampleReport.UNAVAILABLE;

        long bytesPerMillSec = (report.getDiskReadBytes() + report.getDiskWriteBytes()) / totalDiskIODurationMilliSec;

        return (long)(bytesPerMillSec/1.024);  //convert to kB/s
    }
}
