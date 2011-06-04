package org.apache.hadoop.mapreduce.jobhistory;

@SuppressWarnings("all")
public class TaskFailed extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"TaskFailed\",\"namespace\":\"org.apache.hadoop.mapreduce.jobhistory\",\"fields\":[{\"name\":\"taskid\",\"type\":\"string\"},{\"name\":\"taskType\",\"type\":\"string\"},{\"name\":\"finishTime\",\"type\":\"long\"},{\"name\":\"error\",\"type\":\"string\"},{\"name\":\"failedDueToAttempt\",\"type\":[\"null\",\"string\"]},{\"name\":\"status\",\"type\":\"string\"}]}");
  public org.apache.avro.util.Utf8 taskid;
  public org.apache.avro.util.Utf8 taskType;
  public long finishTime;
  public org.apache.avro.util.Utf8 error;
  public org.apache.avro.util.Utf8 failedDueToAttempt;
  public org.apache.avro.util.Utf8 status;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return taskid;
    case 1: return taskType;
    case 2: return finishTime;
    case 3: return error;
    case 4: return failedDueToAttempt;
    case 5: return status;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: taskid = (org.apache.avro.util.Utf8)value$; break;
    case 1: taskType = (org.apache.avro.util.Utf8)value$; break;
    case 2: finishTime = (java.lang.Long)value$; break;
    case 3: error = (org.apache.avro.util.Utf8)value$; break;
    case 4: failedDueToAttempt = (org.apache.avro.util.Utf8)value$; break;
    case 5: status = (org.apache.avro.util.Utf8)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
