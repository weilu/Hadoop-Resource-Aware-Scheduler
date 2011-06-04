package org.apache.hadoop.mapred;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.apache.hadoop.mapred.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public final class queueinfo_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

private static final long serialVersionUID = 1L;

  public static String getTree(String parent, JobQueueInfo[] rootQueues) {
    List<JobQueueInfo> rootQueueList = new ArrayList<JobQueueInfo>();
    for (JobQueueInfo queue : rootQueues) {
      rootQueueList.add(queue);
    }
    return getTree(parent, rootQueueList);
  }

  private static String getTree(String parent, List<JobQueueInfo> children) {
    StringBuilder str = new StringBuilder();
    if (children == null) {
      return "";
    }
    for (JobQueueInfo queueInfo : children) {
      String variableName = queueInfo.getQueueName().replace(":", "_");
      String label = queueInfo.getQueueName().split(":")[queueInfo
          .getQueueName().split(":").length - 1];
      str.append(String.format(
          "var %sTreeNode = new YAHOO.widget.MenuNode(\"%s\", %s, false);\n",
          variableName, label, parent));
      str.append(String.format("%sTreeNode.data=\"%s\";\n", variableName,
          queueInfo.getSchedulingInfo().replaceAll("\n", "<br/>")));
      str.append(String.format("%sTreeNode.name=\"%s\";\n", variableName,
          queueInfo.getQueueName()));
      str.append(getTree(variableName + "TreeNode", queueInfo.getChildren()));
    }
    return str.toString();
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
      out.write("\n\n<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<title>Job Queue Information page</title>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"/static/hadoop.css\">\n");

  JobTracker tracker = (JobTracker) application.getAttribute("job.tracker");
  QueueManager qmgr = tracker.getQueueManager();
  JobQueueInfo[] rootQueues = qmgr.getRootQueues();

      out.write('\n');
      out.write("\n<style type=\"text/css\">\n  /*margin and padding on body element\n    can introduce errors in determining\n    element position and are not recommended;\n    we turn them off as a foundation for YUI\n    CSS treatments. */\n  body {\n    margin:0;\n    padding:0;\n  }\n</style>\n<!-- Combo-handled YUI CSS files: --> \n<link rel=\"stylesheet\" type=\"text/css\" href=\"http://yui.yahooapis.com/combo?2.7.0/build/fonts/fonts-min.css&2.7.0/build/grids/grids-min.css&2.7.0/build/base/base-min.css&2.7.0/build/assets/skins/sam/skin.css\"> \n<!-- Combo-handled YUI JS files: --> \n<script type=\"text/javascript\" src=\"http://yui.yahooapis.com/combo?2.7.0/build/utilities/utilities.js&2.7.0/build/layout/layout-min.js&2.7.0/build/container/container_core-min.js&2.7.0/build/menu/menu-min.js&2.7.0/build/stylesheet/stylesheet-min.js&2.7.0/build/treeview/treeview-min.js\"></script>\n</head>\n<body class=\"yui-skin-sam\">\n<div id=\"left\">\n<div id=\"queue_tree\"></div>\n</div>\n<div id=\"right\">\n  <div id=\"right_top\" width=\"100%\"></div>\n  <div style=\"text-align: center;\"><h2><a href=\"jobtracker.jsp\">Job Tracker</a>\n");
      out.write("  </h2></div>\n</div>\n<script type = \"text/javascript\">\nif (typeof(YAHOO) == \"undefined\") {\n  window.location = \"queuetable.jsp\";\n}\nelse {\n  (function() {\n    var tree;\n    YAHOO.util.Event.onDOMReady(function() {\n      var layout = new YAHOO.widget.Layout({\n        units : [\n          { position: 'center', body: 'right', scroll: true},\n          { position: 'left', width: 150, gutter: '5px', resize: true, \n            body:'left',scroll: true, collapse:true,\n            header: '<center>Queues</center>'\n          }\n        ]\n      });\n      layout.on('render', function() {\n        function onLabelClick(node) {\n          var schedulingInfoDiv = document.getElementById('right_top');\n          schedulingInfoDiv.innerHTML = \n            \"<font size=\\\"+3\\\"><b><u>Scheduling Information for queue: \" +\n             node.label + \"</u></b></font><br/><br/>\" + node.data + \"<hr/>\";\n          var surl = 'jobtable.jsp?queue_name='+node.name;\n          var callback = \n            {success: handleSuccess, failure: handleFailure, arguments: {}};\n");
      out.write("          var request = YAHOO.util.Connect.asyncRequest('GET', surl, callback); \n        }       \n        function handleSuccess(o) {\n    \t  var jobtablediv = document.getElementById('right_top');\n          jobtablediv.innerHTML += o.responseText;\n        }\n        function handleFailure(o) {\n    \t  var jobtablediv = document.getElementById('right_top');\n    \t  jobtablediv.innerHTML = 'unable to retrieve jobs for the queue'; \n        }\n        tree = new YAHOO.widget.TreeView(\"queue_tree\");\n        ");
      out.print(getTree("tree.getRoot()", rootQueues));
      out.write("\n        tree.subscribe(\"labelClick\", onLabelClick);\n        tree.draw();\n        onLabelClick(tree.getRoot().children[0]);\n      });\n      layout.render();\n    });\n  })();\n}\n</script>\n</body>\n</html>\n");
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
