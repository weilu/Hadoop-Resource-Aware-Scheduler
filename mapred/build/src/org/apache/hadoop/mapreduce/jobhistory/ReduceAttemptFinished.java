package org.apache.hadoop.mapreduce.jobhistory;

@SuppressWarnings("all")
public class ReduceAttemptFinished extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"ReduceAttemptFinished\",\"namespace\":\"org.apache.hadoop.mapreduce.jobhistory\",\"fields\":[{\"name\":\"taskid\",\"type\":\"string\"},{\"name\":\"attemptId\",\"type\":\"string\"},{\"name\":\"taskType\",\"type\":\"string\"},{\"name\":\"taskStatus\",\"type\":\"string\"},{\"name\":\"shuffleFinishTime\",\"type\":\"long\"},{\"name\":\"sortFinishTime\",\"type\":\"long\"},{\"name\":\"finishTime\",\"type\":\"long\"},{\"name\":\"hostname\",\"type\":\"string\"},{\"name\":\"state\",\"type\":\"string\"},{\"name\":\"counters\",\"type\":{\"type\":\"record\",\"name\":\"JhCounters\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"groups\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"JhCounterGroup\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"displayName\",\"type\":\"string\"},{\"name\":\"counts\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"JhCounter\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"displayName\",\"type\":\"string\"},{\"name\":\"value\",\"type\":\"long\"}]}}}]}}}]}}]}");
  public org.apache.avro.util.Utf8 taskid;
  public org.apache.avro.util.Utf8 attemptId;
  public org.apache.avro.util.Utf8 taskType;
  public org.apache.avro.util.Utf8 taskStatus;
  public long shuffleFinishTime;
  public long sortFinishTime;
  public long finishTime;
  public org.apache.avro.util.Utf8 hostname;
  public org.apache.avro.util.Utf8 state;
  public org.apache.hadoop.mapreduce.jobhistory.JhCounters counters;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return taskid;
    case 1: return attemptId;
    case 2: return taskType;
    case 3: return taskStatus;
    case 4: return shuffleFinishTime;
    case 5: return sortFinishTime;
    case 6: return finishTime;
    case 7: return hostname;
    case 8: return state;
    case 9: return counters;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: taskid = (org.apache.avro.util.Utf8)value$; break;
    case 1: attemptId = (org.apache.avro.util.Utf8)value$; break;
    case 2: taskType = (org.apache.avro.util.Utf8)value$; break;
    case 3: taskStatus = (org.apache.avro.util.Utf8)value$; break;
    case 4: shuffleFinishTime = (java.lang.Long)value$; break;
    case 5: sortFinishTime = (java.lang.Long)value$; break;
    case 6: finishTime = (java.lang.Long)value$; break;
    case 7: hostname = (org.apache.avro.util.Utf8)value$; break;
    case 8: state = (org.apache.avro.util.Utf8)value$; break;
    case 9: counters = (org.apache.hadoop.mapreduce.jobhistory.JhCounters)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
