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
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.util.StringUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

/**************************************************
 * Describes the current status of a sampled task.
 *
 **************************************************/
@InterfaceAudience.Private
@InterfaceStability.Unstable
public class SampleTaskStatus implements Writable, Cloneable {
    static final Log LOG =
            LogFactory.getLog(SampleTaskStatus.class.getName());

    private TaskAttemptID sampleMapTaskId = new TaskAttemptID();
    private long readInputStartTime;
    private long readInputDoneTime;
    private long writeOutputStartTime;
    private long writeOutputDoneTime;
    private long networkSampleMapCopyDurationMilliSec;

    protected SampleTaskStatus() {
    }

    protected SampleTaskStatus(TaskAttemptID sampleMapTaskId, long readInputStartTime, long readInputDoneTime,
                               long writeOutputStartTime, long writeOutputDoneTime,
                               long networkSampleMapCopyDurationMilliSec) {
        this.sampleMapTaskId = sampleMapTaskId;
        this.readInputStartTime = readInputStartTime;
        this.readInputDoneTime = readInputDoneTime;
        this.writeOutputStartTime = writeOutputStartTime;
        this.writeOutputDoneTime = writeOutputDoneTime;
        this.networkSampleMapCopyDurationMilliSec = networkSampleMapCopyDurationMilliSec;
    }

    public long getReadInputStartTime() {
        return readInputStartTime;
    }

    public void setReadInputStartTime(long readInputStartTime) {
        this.readInputStartTime = readInputStartTime;
    }

    public long getReadInputDoneTime() {
        return readInputDoneTime;
    }

    public void setReadInputDoneTime(long readInputDoneTime) {
        this.readInputDoneTime = readInputDoneTime;
    }

    public long getWriteOutputStartTime() {
        return writeOutputStartTime;
    }

    public void setWriteOutputStartTime(long writeOutputStartTime) {
        this.writeOutputStartTime = writeOutputStartTime;
    }

    public long getWriteOutputDoneTime() {
        return writeOutputDoneTime;
    }

    public void setWriteOutputDoneTime(long writeOutputDoneTime) {
        this.writeOutputDoneTime = writeOutputDoneTime;
    }

    public long getNetworkSampleMapCopyDurationMilliSec() {
        return networkSampleMapCopyDurationMilliSec;
    }

    public void setNetworkSampleMapCopyDurationMilliSec(long networkSampleMapCopyDurationMilliSec) {
        this.networkSampleMapCopyDurationMilliSec = networkSampleMapCopyDurationMilliSec;
    }

    public TaskAttemptID getSampleMapTaskId() {
        return sampleMapTaskId;
    }

    public void setSampleMapTaskId(TaskAttemptID sampleMapTaskId) {
        this.sampleMapTaskId = sampleMapTaskId;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cnse) {
            // Shouldn't happen since we do implement Clonable
            throw new InternalError(cnse.toString());
        }
    }

    //////////////////////////////////////////////
    // Writable
    //////////////////////////////////////////////
    public void write(DataOutput out) throws IOException {
        sampleMapTaskId.write(out);
        out.writeLong(readInputStartTime);
        out.writeLong(readInputDoneTime);
        out.writeLong(writeOutputStartTime);
        out.writeLong(writeOutputDoneTime);
        out.writeLong(networkSampleMapCopyDurationMilliSec);
    }

    public void readFields(DataInput in) throws IOException {

        this.sampleMapTaskId.readFields(in);
        readInputStartTime = in.readLong();
        readInputDoneTime = in.readLong();
        writeOutputStartTime = in.readLong();
        writeOutputDoneTime = in.readLong();
        networkSampleMapCopyDurationMilliSec = in.readLong();
    }

    static SampleTaskStatus readTaskStatus(DataInput in) throws IOException {
        SampleTaskStatus taskStatus = new SampleTaskStatus();
        taskStatus.readFields(in);
        return taskStatus;
    }

    static void writeTaskStatus(DataOutput out, SampleTaskStatus taskStatus)
            throws IOException {
        taskStatus.write(out);
    }
}

