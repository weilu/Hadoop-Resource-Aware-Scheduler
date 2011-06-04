package org.apache.hadoop.mapreduce.jobhistory;

@SuppressWarnings("all")
public class JobSubmitted extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"JobSubmitted\",\"namespace\":\"org.apache.hadoop.mapreduce.jobhistory\",\"fields\":[{\"name\":\"jobid\",\"type\":\"string\"},{\"name\":\"jobName\",\"type\":\"string\"},{\"name\":\"userName\",\"type\":\"string\"},{\"name\":\"submitTime\",\"type\":\"long\"},{\"name\":\"jobConfPath\",\"type\":\"string\"},{\"name\":\"acls\",\"type\":{\"type\":\"map\",\"values\":\"string\"}}]}");
  public org.apache.avro.util.Utf8 jobid;
  public org.apache.avro.util.Utf8 jobName;
  public org.apache.avro.util.Utf8 userName;
  public long submitTime;
  public org.apache.avro.util.Utf8 jobConfPath;
  public java.util.Map<org.apache.avro.util.Utf8,org.apache.avro.util.Utf8> acls;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return jobid;
    case 1: return jobName;
    case 2: return userName;
    case 3: return submitTime;
    case 4: return jobConfPath;
    case 5: return acls;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: jobid = (org.apache.avro.util.Utf8)value$; break;
    case 1: jobName = (org.apache.avro.util.Utf8)value$; break;
    case 2: userName = (org.apache.avro.util.Utf8)value$; break;
    case 3: submitTime = (java.lang.Long)value$; break;
    case 4: jobConfPath = (org.apache.avro.util.Utf8)value$; break;
    case 5: acls = (java.util.Map<org.apache.avro.util.Utf8,org.apache.avro.util.Utf8>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
