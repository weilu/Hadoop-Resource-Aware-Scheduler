package org.apache.hadoop.mapreduce.jobhistory;

@SuppressWarnings("all")
public class JobUnsuccessfulCompletion extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"JobUnsuccessfulCompletion\",\"namespace\":\"org.apache.hadoop.mapreduce.jobhistory\",\"fields\":[{\"name\":\"jobid\",\"type\":\"string\"},{\"name\":\"finishTime\",\"type\":\"long\"},{\"name\":\"finishedMaps\",\"type\":\"int\"},{\"name\":\"finishedReduces\",\"type\":\"int\"},{\"name\":\"jobStatus\",\"type\":\"string\"}]}");
  public org.apache.avro.util.Utf8 jobid;
  public long finishTime;
  public int finishedMaps;
  public int finishedReduces;
  public org.apache.avro.util.Utf8 jobStatus;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return jobid;
    case 1: return finishTime;
    case 2: return finishedMaps;
    case 3: return finishedReduces;
    case 4: return jobStatus;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: jobid = (org.apache.avro.util.Utf8)value$; break;
    case 1: finishTime = (java.lang.Long)value$; break;
    case 2: finishedMaps = (java.lang.Integer)value$; break;
    case 3: finishedReduces = (java.lang.Integer)value$; break;
    case 4: jobStatus = (org.apache.avro.util.Utf8)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
