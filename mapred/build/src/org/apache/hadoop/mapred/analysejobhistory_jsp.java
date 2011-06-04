package org.apache.hadoop.mapred;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.apache.hadoop.http.HtmlQuoting;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.util.*;
import java.text.SimpleDateFormat;
import org.apache.hadoop.mapreduce.jobhistory.*;

public final class analysejobhistory_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

	private static SimpleDateFormat dateFormat 
                              = new SimpleDateFormat("d/MM HH:mm:ss") ; 

	private static final long serialVersionUID = 1L;

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
      out.write("\n<html><body>\n");

  String logFile = request.getParameter("logFile");
  String numTasks = request.getParameter("numTasks");
  int showTasks = 10 ; 
  if (numTasks != null) {
    showTasks = Integer.parseInt(numTasks);  
  }
  FileSystem fs = (FileSystem) application.getAttribute("fileSys");
  JobTracker jobTracker = (JobTracker) application.getAttribute("job.tracker");
  JobHistoryParser.JobInfo job = JSPUtil.checkAccessAndGetJobInfo(request,
      response, jobTracker, fs, new Path(logFile));
  if (job == null) {
    return;
  }

      out.write("\n<h2>Hadoop Job <a href=\"jobdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write('"');
      out.write('>');
      out.print(job.getJobId() );
      out.write(" </a></h2>\n<b>User : </b> ");
      out.print(HtmlQuoting.quoteHtmlChars(job.getUsername()) );
      out.write("<br/>\n<b>JobName : </b> ");
      out.print(HtmlQuoting.quoteHtmlChars(job.getJobname()) );
      out.write("<br/>\n<b>JobConf : </b> ");
      out.print(job.getJobConfPath() );
      out.write("<br/> \n<b>Submitted At : </b> ");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, job.getSubmitTime(), 0 ) );
      out.write("<br/> \n<b>Launched At : </b> ");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, job.getLaunchTime(), job.getSubmitTime()) );
      out.write("<br/>\n<b>Finished At : </b>  ");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, job.getFinishTime(), job.getLaunchTime()) );
      out.write("<br/>\n<b>Status : </b> ");
      out.print( ((job.getJobStatus() == null)?"Incomplete" :job.getJobStatus()) );
      out.write("<br/> \n<hr/>\n<center>\n");

  if (!JobStatus.getJobRunState(JobStatus.SUCCEEDED).equals(job.getJobStatus())) {
    out.print("<h3>No Analysis available as job did not finish</h3>");
    return;
  }
  
  HistoryViewer.AnalyzedJob avg = new HistoryViewer.AnalyzedJob(job);
  JobHistoryParser.TaskAttemptInfo [] mapTasks = avg.getMapTasks();
  JobHistoryParser.TaskAttemptInfo [] reduceTasks = avg.getReduceTasks();

  Comparator<JobHistoryParser.TaskAttemptInfo> cMap = 
    new Comparator<JobHistoryParser.TaskAttemptInfo>() {
    public int compare(JobHistoryParser.TaskAttemptInfo t1, 
        JobHistoryParser.TaskAttemptInfo t2) {
      long l1 = t1.getFinishTime() - t1.getStartTime();
      long l2 = t2.getFinishTime() - t2.getStartTime();
      return (l2 < l1 ? -1 : (l2 == l1 ? 0 : 1));
    }
  };

  Comparator<JobHistoryParser.TaskAttemptInfo> cShuffle = 
    new Comparator<JobHistoryParser.TaskAttemptInfo>() {
    public int compare(JobHistoryParser.TaskAttemptInfo t1, 
        JobHistoryParser.TaskAttemptInfo t2) {
      long l1 = t1.getShuffleFinishTime() - t1.getStartTime();
      long l2 = t2.getShuffleFinishTime() - t2.getStartTime();
      return (l2 < l1 ? -1 : (l2 == l1 ? 0 : 1));
    }
  };

  Comparator<JobHistoryParser.TaskAttemptInfo> cFinishShuffle = 
    new Comparator<JobHistoryParser.TaskAttemptInfo>() {
    public int compare(JobHistoryParser.TaskAttemptInfo t1, 
        JobHistoryParser.TaskAttemptInfo t2) {
      long l1 = t1.getShuffleFinishTime(); 
      long l2 = t2.getShuffleFinishTime();
      return (l2 < l1 ? -1 : (l2 == l1 ? 0 : 1));
    }
  };

  Comparator<JobHistoryParser.TaskAttemptInfo> cFinishMapRed = 
    new Comparator<JobHistoryParser.TaskAttemptInfo>() {
    public int compare(JobHistoryParser.TaskAttemptInfo t1, 
        JobHistoryParser.TaskAttemptInfo t2) {
      long l1 = t1.getFinishTime(); 
      long l2 = t2.getFinishTime();
      return (l2 < l1 ? -1 : (l2 == l1 ? 0 : 1));
    }
  };
  
  Comparator<JobHistoryParser.TaskAttemptInfo> cReduce = 
    new Comparator<JobHistoryParser.TaskAttemptInfo>() {
    public int compare(JobHistoryParser.TaskAttemptInfo t1, 
        JobHistoryParser.TaskAttemptInfo t2) {
      long l1 = t1.getFinishTime() -
                t1.getShuffleFinishTime();
      long l2 = t2.getFinishTime() -
                t2.getShuffleFinishTime();
      return (l2 < l1 ? -1 : (l2 == l1 ? 0 : 1));
    }
  }; 

  if (mapTasks == null || mapTasks.length <= 0) return;
  Arrays.sort(mapTasks, cMap);
  JobHistoryParser.TaskAttemptInfo minMap = mapTasks[mapTasks.length-1] ;

      out.write("\n\n<h3>Time taken by best performing Map task \n<a href=\"taskdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&tipid=");
      out.print(minMap.getAttemptId().getTaskID());
      out.write('"');
      out.write('>');
      out.write('\n');
      out.print(minMap.getAttemptId().getTaskID() );
      out.write("</a> : ");
      out.print(StringUtils.formatTimeDiff(minMap.getFinishTime(), minMap.getStartTime() ) );
      out.write("</h3>\n<h3>Average time taken by Map tasks: \n");
      out.print(StringUtils.formatTimeDiff(avg.getAvgMapTime(), 0) );
      out.write("</h3>\n<h3>Worse performing map tasks</h3>\n<table border=\"2\" cellpadding=\"5\" cellspacing=\"2\">\n<tr><td>Task Id</td><td>Time taken</td></tr>\n");

  for (int i=0;i<showTasks && i<mapTasks.length; i++) {

      out.write("\n    <tr>\n    <td><a href=\"taskdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&tipid=");
      out.print(mapTasks[i].getAttemptId().getTaskID());
      out.write("\">\n        ");
      out.print(mapTasks[i].getAttemptId().getTaskID() );
      out.write("</a></td>\n    <td>");
      out.print(StringUtils.formatTimeDiff(mapTasks[i].getFinishTime(), mapTasks[i].getStartTime()) );
      out.write("</td>\n    </tr>\n");

  }

      out.write("\n</table>\n");
  
  Arrays.sort(mapTasks, cFinishMapRed);
  JobHistoryParser.TaskAttemptInfo lastMap = mapTasks[0] ;

      out.write("\n\n<h3>The last Map task \n<a href=\"taskdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write("\n&tipid=");
      out.print(lastMap.getAttemptId().getTaskID());
      out.write('"');
      out.write('>');
      out.print(lastMap.getAttemptId().getTaskID() );
      out.write("</a> \nfinished at (relative to the Job launch time): \n");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat, 
                              lastMap.getFinishTime(), 
                              job.getLaunchTime()) );
      out.write("</h3>\n<hr/>\n\n");

  if (reduceTasks.length <= 0) return;
  Arrays.sort(reduceTasks, cShuffle); 
  JobHistoryParser.TaskAttemptInfo minShuffle = reduceTasks[reduceTasks.length-1] ;

      out.write("\n<h3>Time taken by best performing shuffle\n<a href=\"taskdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write("\n&tipid=");
      out.print(minShuffle.getAttemptId().getTaskID());
      out.write('"');
      out.write('>');
      out.print(minShuffle.getAttemptId().getTaskID());
      out.write("</a> : \n");
      out.print(StringUtils.formatTimeDiff(minShuffle.getShuffleFinishTime(),
                              minShuffle.getStartTime() ) );
      out.write("</h3>\n<h3>Average time taken by Shuffle: \n");
      out.print(StringUtils.formatTimeDiff(avg.getAvgShuffleTime(), 0) );
      out.write("</h3>\n<h3>Worse performing Shuffle(s)</h3>\n<table border=\"2\" cellpadding=\"5\" cellspacing=\"2\">\n<tr><td>Task Id</td><td>Time taken</td></tr>\n");

  for (int i=0;i<showTasks && i<reduceTasks.length; i++) {

      out.write("\n    <tr>\n    <td><a href=\"taskdetailshistory.jsp?logFile=\n");
      out.print(logFile);
      out.write("&tipid=");
      out.print(reduceTasks[i].getAttemptId().getTaskID());
      out.write('"');
      out.write('>');
      out.write('\n');
      out.print(reduceTasks[i].getAttemptId().getTaskID() );
      out.write("</a></td>\n    <td>");
      out.print(
           StringUtils.formatTimeDiff(
                       reduceTasks[i].getShuffleFinishTime(),
                       reduceTasks[i].getStartTime()) );
      out.write("\n    </td>\n    </tr>\n");

  }

      out.write("\n</table>\n");
  
  Arrays.sort(reduceTasks, cFinishShuffle);
  JobHistoryParser.TaskAttemptInfo lastShuffle = reduceTasks[0] ;

      out.write("\n\n<h3>The last Shuffle  \n<a href=\"taskdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write("\n&tipid=");
      out.print(lastShuffle.getAttemptId().getTaskID());
      out.write('"');
      out.write('>');
      out.print(lastShuffle.getAttemptId().getTaskID());
      out.write("\n</a> finished at (relative to the Job launch time): \n");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat,
                              lastShuffle.getShuffleFinishTime(),
                              job.getLaunchTime() ) );
      out.write("</h3>\n\n");

  Arrays.sort(reduceTasks, cReduce); 
  JobHistoryParser.TaskAttemptInfo minReduce = reduceTasks[reduceTasks.length-1] ;

      out.write("\n<hr/>\n<h3>Time taken by best performing Reduce task : \n<a href=\"taskdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&tipid=");
      out.print(minReduce.getAttemptId().getTaskID());
      out.write('"');
      out.write('>');
      out.write('\n');
      out.print(minReduce.getAttemptId().getTaskID() );
      out.write("</a> : \n");
      out.print(StringUtils.formatTimeDiff(minReduce.getFinishTime(),
    minReduce.getShuffleFinishTime() ) );
      out.write("</h3>\n\n<h3>Average time taken by Reduce tasks: \n");
      out.print(StringUtils.formatTimeDiff(avg.getAvgReduceTime(), 0) );
      out.write("</h3>\n<h3>Worse performing reduce tasks</h3>\n<table border=\"2\" cellpadding=\"5\" cellspacing=\"2\">\n<tr><td>Task Id</td><td>Time taken</td></tr>\n");

  for (int i=0;i<showTasks && i<reduceTasks.length; i++) {

      out.write("\n    <tr>\n    <td><a href=\"taskdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write("&tipid=");
      out.print(reduceTasks[i].getAttemptId().getTaskID());
      out.write("\">\n        ");
      out.print(reduceTasks[i].getAttemptId().getTaskID() );
      out.write("</a></td>\n    <td>");
      out.print(StringUtils.formatTimeDiff(
             reduceTasks[i].getFinishTime(),
             reduceTasks[i].getShuffleFinishTime()) );
      out.write("</td>\n    </tr>\n");

  }

      out.write("\n</table>\n");
  
  Arrays.sort(reduceTasks, cFinishMapRed);
  JobHistoryParser.TaskAttemptInfo lastReduce = reduceTasks[0] ;

      out.write("\n\n<h3>The last Reduce task \n<a href=\"taskdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write("\n&tipid=");
      out.print(lastReduce.getAttemptId().getTaskID());
      out.write('"');
      out.write('>');
      out.print(lastReduce.getAttemptId().getTaskID());
      out.write("\n</a> finished at (relative to the Job launch time): \n");
      out.print(StringUtils.getFormattedTimeWithDiff(dateFormat,
                              lastReduce.getFinishTime(),
                              job.getLaunchTime() ) );
      out.write("</h3>\n</center>\n</body></html>\n");
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
