/**
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

package org.apache.hadoop.mapred;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Comparator;

/**
 * Utility class containing scheduling algorithms used in the resource scheduler.
 */
class ResourceSchedulingAlgorithms {
    public static final Log LOG = LogFactory.getLog(
            ResourceSchedulingAlgorithms.class.getName());

    /**
     * in order of sampleState(unsampled-sampled-scheduled),
     * then priority and then submission time
     */
    public static class FifoWithSampleComparator
            implements Comparator<ResourceJobQueueListener.JobSchedulingInfo> {
        public int compare(ResourceJobQueueListener.JobSchedulingInfo o1,
                           ResourceJobQueueListener.JobSchedulingInfo o2) {
            int res = o1.sampleState().compareTo(o2.sampleState());
            LOG.info("[compare:sampleState] - o1: " + o1.getJobID() + o1.sampleState() + ", o2: " + o2.getJobID() + o2.sampleState() + ", res: " + res);
            if(res == 0 ){
                res = o1.getPriority().compareTo(o2.getPriority());
                LOG.info("[compare:priority] - o1: " + o1.getJobID() + ", o2: " + o2.getJobID() + ", res: " + res);
            }
            if (res == 0) {
                if (o1.getStartTime() < o2.getStartTime()) {
                    res = -1;
                } else {
                    res = (o1.getStartTime() == o2.getStartTime() ? 0 : 1);
                }
                LOG.info("[compare:startTime] - o1: " + o1.getJobID() + ", o2: " + o2.getJobID() + ", res: " + res);
            }
            if (res == 0) {
                res = o1.getJobID().compareTo(o2.getJobID());
                LOG.info("[compare:jobID] - o1: " + o1.getJobID() + ", o2: " + o2.getJobID() + ", res: " + res);
            }
            return res;
        }
    }

    public static class FastestTaskFirstComparator implements Comparator<ResourceScheduler.JobSchedulingInfo> {
        public int compare(ResourceScheduler.JobSchedulingInfo o1,
                           ResourceScheduler.JobSchedulingInfo o2) {
            int res = o1.getPriority().compareTo(o2.getPriority());
            LOG.info("[compare:priority] - o1: " + o1.getJobID() + ", o2: " + o2.getJobID() + ", res: " + res);
            if(res == 0 ){
                if((o2.getTimeEstimated()<0 && 0<o1.getTimeEstimated())
                        || (0<o1.getTimeEstimated() && o1.getTimeEstimated() < o2.getTimeEstimated()))
                    res = -1;
                else
                    res = (o1.getTimeEstimated() == o2.getTimeEstimated() ? 0 : 1);
                LOG.info("[compare:timeEstimated] - o1: " + o1.getJobID() + ", o2: " + o2.getJobID() + ", res: " + res);
            }
            if (res == 0) {
                if (o1.getStartTime() < o2.getStartTime()) {
                    res = -1;
                } else {
                    res = (o1.getStartTime() == o2.getStartTime() ? 0 : 1);
                }
                LOG.info("[compare:startTime] - o1: " + o1.getJobID() + ", o2: " + o2.getJobID() + ", res: " + res);
            }
            if (res == 0) {
                res = o1.getJobID().compareTo(o2.getJobID());
                LOG.info("[compare:jobID] - o1: " + o1.getJobID() + ", o2: " + o2.getJobID() + ", res: " + res);
            }
            return res;
        }
    }

//    private void logDebugInfo(String compared){
//        LOG.info("[compare:priority] - o1: " + o1.getJobID() + ", o2: " + o2.getJobID() + ", res: " + res);
//    }
}
