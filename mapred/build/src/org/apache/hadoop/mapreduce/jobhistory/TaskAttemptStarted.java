package org.apache.hadoop.mapreduce.jobhistory;

@SuppressWarnings("all")
public class TaskAttemptStarted extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"TaskAttemptStarted\",\"namespace\":\"org.apache.hadoop.mapreduce.jobhistory\",\"fields\":[{\"name\":\"taskid\",\"type\":\"string\"},{\"name\":\"taskType\",\"type\":\"string\"},{\"name\":\"attemptId\",\"type\":\"string\"},{\"name\":\"startTime\",\"type\":\"long\"},{\"name\":\"trackerName\",\"type\":\"string\"},{\"name\":\"httpPort\",\"type\":\"int\"}]}");
  public org.apache.avro.util.Utf8 taskid;
  public org.apache.avro.util.Utf8 taskType;
  public org.apache.avro.util.Utf8 attemptId;
  public long startTime;
  public org.apache.avro.util.Utf8 trackerName;
  public int httpPort;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return taskid;
    case 1: return taskType;
    case 2: return attemptId;
    case 3: return startTime;
    case 4: return trackerName;
    case 5: return httpPort;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: taskid = (org.apache.avro.util.Utf8)value$; break;
    case 1: taskType = (org.apache.avro.util.Utf8)value$; break;
    case 2: attemptId = (org.apache.avro.util.Utf8)value$; break;
    case 3: startTime = (java.lang.Long)value$; break;
    case 4: trackerName = (org.apache.avro.util.Utf8)value$; break;
    case 5: httpPort = (java.lang.Integer)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
