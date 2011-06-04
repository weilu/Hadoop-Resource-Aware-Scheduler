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
import java.text.*;
import org.apache.hadoop.mapreduce.jobhistory.*;
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.CounterGroup;

public final class taskstatshistory_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

 private static SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM HH:mm:ss") ;
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

  String attemptid = request.getParameter("attemptid");
  if(attemptid == null) {
    out.println("No attemptid found! Pass a 'attemptid' parameter in the request.");
    return;
  }
  TaskID tipid = TaskAttemptID.forName(attemptid).getTaskID();
  String logFile = request.getParameter("logFile");

  Format decimal = new DecimalFormat();

  FileSystem fs = (FileSystem) application.getAttribute("fileSys");
  JobTracker jobTracker = (JobTracker) application.getAttribute("job.tracker");
  JobHistoryParser.JobInfo job = JSPUtil.checkAccessAndGetJobInfo(request,
      response, jobTracker, fs, new Path(logFile));
  if (job == null) {
    return;
  }

  Map<TaskID, JobHistoryParser.TaskInfo> tasks = job.getAllTasks();
  JobHistoryParser.TaskInfo task = tasks.get(tipid);

  Map<TaskAttemptID, JobHistoryParser.TaskAttemptInfo> attempts = task.getAllTaskAttempts();
  JobHistoryParser.TaskAttemptInfo attempt = attempts.get(TaskAttemptID.forName(attemptid));

  Counters counters = attempt.getCounters();

      out.write("\n\n<html>\n  <head>\n    <title>Counters for ");
      out.print(attemptid);
      out.write("</title>\n  </head>\n<body>\n<h1>Counters for ");
      out.print(attemptid);
      out.write("</h1>\n\n<hr>\n\n");

  if (counters == null) {

      out.write("\n    <h3>No counter information found for this attempt</h3>\n");

  } else {    

      out.write("\n    <table>\n");

      for (String groupName : counters.getGroupNames()) {
        CounterGroup group = counters.getGroup(groupName);
        String displayGroupName = group.getDisplayName();

      out.write("\n        <tr>\n          <td colspan=\"3\"><br/><b>\n          ");
      out.print(HtmlQuoting.quoteHtmlChars(displayGroupName));
      out.write("</b></td>\n        </tr>\n");

        Iterator<Counter> ctrItr = group.iterator();
        while(ctrItr.hasNext()) {
          Counter counter = ctrItr.next();
          String displayCounterName = counter.getDisplayName();
          long value = counter.getValue();

      out.write("\n          <tr>\n            <td width=\"50\"></td>\n            <td>");
      out.print(HtmlQuoting.quoteHtmlChars(displayCounterName));
      out.write("</td>\n            <td align=\"right\">");
      out.print(decimal.format(value));
      out.write("</td>\n          </tr>\n");

        }
      }

      out.write("\n    </table>\n");

  }

      out.write("\n\n<hr>\n<a href=\"jobdetailshistory.jsp?logFile=");
      out.print(logFile);
      out.write("\">Go back to the job</a><br>\n<a href=\"jobtracker.jsp\">Go back to JobTracker</a><br>\n");

out.println(ServletUtil.htmlFooter());

      out.write('\n');
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
