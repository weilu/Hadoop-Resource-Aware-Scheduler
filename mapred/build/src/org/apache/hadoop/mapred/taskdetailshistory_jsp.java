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
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.jobhistory.*;

public final class taskdetailshistory_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM HH:mm:ss") ; 
	private static final long serialVersionUID = 1L;


  private void printTaskAttempt(JobHistoryParser.TaskAttemptInfo taskAttempt,
                                TaskType type, JspWriter out, String logFile) 
  throws IOException {
    out.print("<tr>"); 
    out.print("<td>" + taskAttempt.getAttemptId() + "</td>");
    out.print("<td>" + StringUtils.getFormattedTimeWithDiff(dateFormat,
              taskAttempt.getStartTime(), 0 ) + "</td>"); 
    if (TaskType.REDUCE.equals(type)) {
      out.print("<td>" + 
                StringUtils.getFormattedTimeWithDiff(dateFormat, 
                taskAttempt.getShuffleFinishTime(),
                taskAttempt.getStartTime()) + "</td>"); 
      out.print("<td>" + StringUtils.getFormattedTimeWithDiff(dateFormat, 
                taskAttempt.getSortFinishTime(),
                taskAttempt.getShuffleFinishTime()) + "</td>"); 
    }
    out.print("<td>"+ StringUtils.getFormattedTimeWithDiff(dateFormat,
              taskAttempt.getFinishTime(),
              taskAttempt.getStartTime()) + "</td>"); 
    out.print("<td>" + taskAttempt.getHostname() + "</td>");
    out.print("<td>" + HtmlQuoting.quoteHtmlChars(taskAttempt.getError()) +
              "</td>");

    // Print task log urls
    out.print("<td>");	
    String taskLogsUrl = HistoryViewer.getTaskLogsUrl(taskAttempt);
    if (taskLogsUrl != null) {
	    String tailFourKBUrl = taskLogsUrl + "&start=-4097";
	    String tailEightKBUrl = taskLogsUrl + "&start=-8193";
	    String entireLogUrl = taskLogsUrl + "&all=true";
	    out.print("<a href=\"" + tailFourKBUrl + "\">Last 4KB</a><br/>");
	    out.print("<a href=\"" + tailEightKBUrl + "\">Last 8KB</a><br/>");
	    out.print("<a href=\"" + entireLogUrl + "\">All</a><br/>");
    } else {
        out.print("n/a");
    }
    out.print("</td>");
    Counters counters = taskAttempt.getCounters();
    if (counters != null) {
      TaskAttemptID attemptId = taskAttempt.getAttemptId();
      out.print("<td>" 
       + "<a href=\"/taskstatshistory.jsp?attemptid=" + attemptId
           + "&logFile=" + logFile + "\">"
           + counters.countCounters() + "</a></td>");
    } else {
      out.print("<td></td>");
    }
    out.print("</tr>"); 
  }

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
      out.write('\n');
	
  String logFile = request.getParameter("logFile");
  String tipid = request.getParameter("tipid"); 
  FileSystem fs = (FileSystem) application.getAttribute("fileSys");
  JobTracker jobTracker = (JobTracker) application.getAttribute("job.tracker");
  JobHistoryParser.JobInfo job = JSPUtil.checkAccessAndGetJobInfo(request,
      response, jobTracker, fs, new Path(logFile));
  if (job == null) {
    return;
  }
  JobHistoryParser.TaskInfo task = job.getAllTasks().get(TaskID.forName(tipid)); 
  TaskType type = task.getTaskType();

      out.write("\n<html>\n<body>\n<h2>");
      out.print(tipid );
      out.write(" attempts for <a href=\"jobdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write('"');
      out.write('>');
      out.write(' ');
      out.print(job.getJobId() );
      out.write(" </a></h2>\n<center>\n<table border=\"2\" cellpadding=\"5\" cellspacing=\"2\">\n<tr><td>Task Id</td><td>Start Time</td>\n");
	
  if (TaskType.REDUCE.equals(type)) {

      out.write("\n    <td>Shuffle Finished</td><td>Sort Finished</td>\n");

  }

      out.write("\n<td>Finish Time</td><td>Host</td><td>Error</td><td>Task Logs</td>\n<td>Counters</td></tr>\n\n");

  for (JobHistoryParser.TaskAttemptInfo attempt : task.getAllTaskAttempts().values()) {
    printTaskAttempt(attempt, type, out, logFile);
  }

      out.write("\n</table>\n</center>\n");
	
  if (TaskType.MAP.equals(type)) {

      out.write("\n<h3>Input Split Locations</h3>\n<table border=\"2\" cellpadding=\"5\" cellspacing=\"2\">\n");

    for (String split : StringUtils.split(task.getSplitLocations()))
    {
      out.println("<tr><td>" + split + "</td></tr>");
    }

      out.write("\n</table>    \n");

  }

      out.write('\n');
      out.write("\n</body>\n</html>\n");
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
