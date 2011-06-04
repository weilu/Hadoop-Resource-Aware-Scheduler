package org.apache.hadoop.mapred;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.http.HtmlQuoting;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import java.text.*;
import org.apache.hadoop.mapreduce.jobhistory.*;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser.JobInfo;
import org.apache.hadoop.mapreduce.JobACL;
import org.apache.hadoop.security.authorize.AccessControlList;

public final class jobdetailshistory_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

private static final long serialVersionUID = 1L;

 static SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMM-yyyy HH:mm:ss") ; 
  private static java.util.List _jspx_dependants;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    JspFactory _jspxFactory = null;
    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      _jspxFactory = JspFactory.getDefaultFactory();
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;


/*
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

      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');

    String logFile = request.getParameter("logFile");
    final Path jobFile = new Path(logFile);
    String jobid = JobHistory.getJobIDFromHistoryFilePath(jobFile).toString();

    final FileSystem fs = (FileSystem) application.getAttribute("fileSys");
    final JobTracker jobTracker = (JobTracker) application.getAttribute("job.tracker");
    JobInfo job = JSPUtil.checkAccessAndGetJobInfo(request, response,
        jobTracker, fs, jobFile);
    if (job == null) {
      return;
    }

      out.write("\n\n<html>\n<head>\n<title>Hadoop Job ");
      out.print(jobid);
      out.write(" on History Viewer</title>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"/static/hadoop.css\">\n</head>\n<body>\n\n<h2>Hadoop Job ");
      out.print(jobid );
      out.write(" on <a href=\"jobhistory.jsp\">History Viewer</a></h2>\n\n<b>User: </b> ");
      out.print(HtmlQuoting.quoteHtmlChars(job.getUsername()) );
      out.write("<br/>\n<b>JobName: </b> ");
      out.print(HtmlQuoting.quoteHtmlChars(job.getJobname()) );
      out.write("<br/>\n<b>JobConf: </b> <a href=\"jobconf_history.jsp?logFile=");
      out.print(logFile);
      out.write("\"> \n                 ");
      out.print(job.getJobConfPath() );
      out.write("</a><br/> \n");
         
  Map<JobACL, AccessControlList> jobAcls = job.getJobACLs();
  JSPUtil.printJobACLs(jobTracker, jobAcls, out);

      out.write("\n<b>Submitted At: </b> ");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, job.getSubmitTime(), 0 )  );
      out.write("<br/> \n<b>Launched At: </b> ");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, job.getLaunchTime(), job.getSubmitTime()) );
      out.write("<br/>\n<b>Finished At: </b>  ");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, job.getFinishTime(), job.getLaunchTime()) );
      out.write("<br/>\n<b>Status: </b> ");
      out.print( ((job.getJobStatus()) == null ? "Incomplete" :job.getJobStatus()) );
      out.write("<br/> \n");

    HistoryViewer.SummarizedJob sj = new HistoryViewer.SummarizedJob(job);

      out.write("\n<b><a href=\"analysejobhistory.jsp?logFile=");
      out.print(logFile);
      out.write("\">Analyse This Job</a></b> \n<hr/>\n<center>\n<table border=\"2\" cellpadding=\"5\" cellspacing=\"2\">\n<tr>\n<td>Kind</td><td>Total Tasks(successful+failed+killed)</td><td>Successful tasks</td><td>Failed tasks</td><td>Killed tasks</td><td>Start Time</td><td>Finish Time</td>\n</tr>\n<tr>\n<td>Setup</td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=JOB_SETUP&status=all\">\n        ");
      out.print(sj.getTotalSetups());
      out.write("</a></td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=JOB_SETUP&status=SUCCEEDED\">\n        ");
      out.print(sj.getNumFinishedSetups());
      out.write("</a></td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=JOB_SETUP&status=FAILED\">\n        ");
      out.print(sj.getNumFailedSetups());
      out.write("</a></td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=JOB_SETUP&status=KILLED\">\n        ");
      out.print(sj.getNumKilledSetups());
      out.write("</a></td>  \n    <td>");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, sj.getSetupStarted(), 0) );
      out.write("</td>\n    <td>");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, sj.getSetupFinished(), sj.getSetupStarted()) );
      out.write("</td>\n</tr>\n<tr>\n<td>Map</td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=MAP&status=all\">\n        ");
      out.print(sj.getTotalMaps());
      out.write("</a></td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=MAP&status=SUCCEEDED\">\n        ");
      out.print(job.getFinishedMaps() );
      out.write("</a></td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=MAP&status=FAILED\">\n        ");
      out.print(sj.getNumFailedMaps());
      out.write("</a></td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=MAP&status=KILLED\">\n        ");
      out.print(sj.getNumKilledMaps());
      out.write("</a></td>\n    <td>");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, sj.getMapStarted(), 0) );
      out.write("</td>\n    <td>");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, sj.getMapFinished(), sj.getMapStarted()) );
      out.write("</td>\n</tr>\n<tr>\n<td>Reduce</td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=REDUCE&status=all\">\n        ");
      out.print(sj.getTotalReduces());
      out.write("</a></td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=REDUCE&status=SUCCEEDED\">\n        ");
      out.print(job.getFinishedReduces());
      out.write("</a></td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=REDUCE&status=FAILED\">\n        ");
      out.print(sj.getNumFailedReduces());
      out.write("</a></td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=REDUCE&status=KILLED\">\n        ");
      out.print(sj.getNumKilledReduces());
      out.write("</a></td>  \n    <td>");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, sj.getReduceStarted(), 0) );
      out.write("</td>\n    <td>");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, sj.getReduceFinished(), sj.getReduceStarted()) );
      out.write("</td>\n</tr>\n<tr>\n<td>Cleanup</td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=JOB_CLEANUP&status=all\">\n        ");
      out.print(sj.getTotalCleanups());
      out.write("</a></td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=JOB_CLEANUP&status=SUCCEEDED\">\n        ");
      out.print(sj.getNumFinishedCleanups());
      out.write("</a></td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=JOB_CLEANUP&status=FAILED\">\n        ");
      out.print(sj.getNumFailedCleanups());
      out.write("</a></td>\n    <td><a href=\"jobtaskshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&taskType=JOB_CLEANUP&status=KILLED>\">\n        ");
      out.print(sj.getNumKilledCleanups());
      out.write("</a></td>  \n    <td>");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, sj.getCleanupStarted(), 0) );
      out.write("</td>\n    <td>");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, sj.getCleanupFinished(), sj.getCleanupStarted()) );
      out.write("</td>\n</tr>\n</table>\n\n<br>\n<br>\n\n<table border=2 cellpadding=\"5\" cellspacing=\"2\">\n  <tr>\n  <th><br/></th>\n  <th>Counter</th>\n  <th>Map</th>\n  <th>Reduce</th>\n  <th>Total</th>\n</tr>\n\n");
  

 Counters totalCounters = job.getTotalCounters();
 Counters mapCounters = job.getMapCounters();
 Counters reduceCounters = job.getReduceCounters();

 if (totalCounters != null) {
   for (String groupName : totalCounters.getGroupNames()) {
     CounterGroup totalGroup = totalCounters.getGroup(groupName);
     CounterGroup mapGroup = mapCounters.getGroup(groupName);
     CounterGroup reduceGroup = reduceCounters.getGroup(groupName);
  
     Format decimal = new DecimalFormat();
  
     boolean isFirst = true;
     Iterator<Counter> ctrItr = totalGroup.iterator();
     while(ctrItr.hasNext()) {
       Counter counter = ctrItr.next();
       String name = counter.getName();
       String mapValue = 
        decimal.format(mapGroup.findCounter(name).getValue());
       String reduceValue = 
        decimal.format(reduceGroup.findCounter(name).getValue());
       String totalValue = 
        decimal.format(counter.getValue());

      out.write("\n       <tr>\n");

       if (isFirst) {
         isFirst = false;

      out.write("\n         <td rowspan=\"");
      out.print(totalGroup.size());
      out.write("\">\n         ");
      out.print(HtmlQuoting.quoteHtmlChars(totalGroup.getDisplayName()));
      out.write("</td>\n");

       }

      out.write("\n       <td>");
      out.print(HtmlQuoting.quoteHtmlChars(counter.getDisplayName()));
      out.write("</td>\n       <td align=\"right\">");
      out.print(mapValue);
      out.write("</td>\n       <td align=\"right\">");
      out.print(reduceValue);
      out.write("</td>\n       <td align=\"right\">");
      out.print(totalValue);
      out.write("</td>\n     </tr>\n");

      }
    }
  }

      out.write("\n</table>\n<br>\n\n<br/>\n ");

    HistoryViewer.FilteredJob filter = new HistoryViewer.FilteredJob(job,TaskStatus.State.FAILED.toString()); 
    Map<String, Set<TaskID>> badNodes = filter.getFilteredMap(); 
    if (badNodes.size() > 0) {
 
      out.write("\n<h3>Failed tasks attempts by nodes </h3>\n<table border=\"1\">\n<tr><td>Hostname</td><td>Failed Tasks</td></tr>\n ");
	  
      for (Map.Entry<String, Set<TaskID>> entry : badNodes.entrySet()) {
        String node = entry.getKey();
        Set<TaskID> failedTasks = entry.getValue();

      out.write("\n        <tr>\n        <td>");
      out.print(node );
      out.write("</td>\n        <td>\n");

          boolean firstId = true;
          for (TaskID tid : failedTasks) {
             if (firstId) {
              firstId = false;

      out.write("\n            <a href=\"taskdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&tipid=");
      out.print(tid );
      out.write('"');
      out.write('>');
      out.print(tid );
      out.write("</a>\n");
		  
          } else {

      out.write("\t\n            ,&nbsp<a href=\"taskdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&tipid=");
      out.print(tid );
      out.write('"');
      out.write('>');
      out.print(tid );
      out.write("</a>\n");
		  
          }
        }

      out.write("\t\n        </td>\n        </tr>\n");
	  
      }
	}
 
      out.write("\n</table>\n<br/>\n\n ");

    filter = new HistoryViewer.FilteredJob(job, TaskStatus.State.KILLED.toString());
    badNodes = filter.getFilteredMap(); 
    if (badNodes.size() > 0) {
 
      out.write("\n<h3>Killed tasks attempts by nodes </h3>\n<table border=\"1\">\n<tr><td>Hostname</td><td>Killed Tasks</td></tr>\n ");
	  
      for (Map.Entry<String, Set<TaskID>> entry : badNodes.entrySet()) {
        String node = entry.getKey();
        Set<TaskID> killedTasks = entry.getValue();

      out.write("\n        <tr>\n        <td>");
      out.print(node );
      out.write("</td>\n        <td>\n");

        boolean firstId = true;
        for (TaskID tid : killedTasks) {
             if (firstId) {
              firstId = false;

      out.write("\n            <a href=\"taskdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&tipid=");
      out.print(tid );
      out.write('"');
      out.write('>');
      out.print(tid );
      out.write("</a>\n");
		  
          } else {

      out.write("\t\n            ,&nbsp<a href=\"taskdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&tipid=");
      out.print(tid );
      out.write('"');
      out.write('>');
      out.print(tid );
      out.write("</a>\n");
		  
          }
        }

      out.write("\t\n        </td>\n        </tr>\n");
	  
      }
    }

      out.write("\n</table>\n</center>\n</body></html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      if (_jspxFactory != null) _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
