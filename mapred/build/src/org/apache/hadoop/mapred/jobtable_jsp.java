package org.apache.hadoop.mapred;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.apache.hadoop.mapred.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.apache.hadoop.util.ServletUtil;

public final class jobtable_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


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

JobTracker tracker = (JobTracker) application.getAttribute("job.tracker");
QueueManager qmgr = tracker.getQueueManager();
String queue = request.getParameter("queue_name");
TaskScheduler scheduler = tracker.getTaskScheduler();
JobQueueInfo queueInfo = tracker.getQueueInfo(queue);

      out.write('\n');

if(queueInfo == null || (queueInfo.getChildren() != null &&
    queueInfo.getChildren().size() != 0) ){

      out.write('\n');
 
} else {

      out.write('\n');

Collection<JobInProgress> jobs = scheduler.getJobs(queue);
String[] queueLabelSplits = queue.split(":");
String queueLabel = 
  queueLabelSplits.length==0?queue:queueLabelSplits[queueLabelSplits.length-1];

if(jobs == null || jobs.isEmpty()) {

      out.write("\n<center>\n<h2><b> No Jobs found for the QueueName:: ");
      out.print(queueLabel);
      out.write(" </b>\n</h2>\n</center>\n");

}else {

      out.write("\n<center>\n<p>\n<h1> Job Summary for the Queue :: ");
      out.print(queueLabel);
      out.write(" </h1>\n(In the order maintained by the scheduler)\n<br/><br/><br/>\n");
      out.print(
  JSPUtil.generateJobTable("Job List", jobs, 30, 5, tracker.conf)
);
      out.write("\n</center>\n");

}

      out.write('\n');
} 
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
