package org.apache.hadoop.mapreduce.jobhistory;

@SuppressWarnings("all")
public class JobFinished extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"JobFinished\",\"namespace\":\"org.apache.hadoop.mapreduce.jobhistory\",\"fields\":[{\"name\":\"jobid\",\"type\":\"string\"},{\"name\":\"finishTime\",\"type\":\"long\"},{\"name\":\"finishedMaps\",\"type\":\"int\"},{\"name\":\"finishedReduces\",\"type\":\"int\"},{\"name\":\"failedMaps\",\"type\":\"int\"},{\"name\":\"failedReduces\",\"type\":\"int\"},{\"name\":\"totalCounters\",\"type\":{\"type\":\"record\",\"name\":\"JhCounters\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"groups\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"JhCounterGroup\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"displayName\",\"type\":\"string\"},{\"name\":\"counts\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"JhCounter\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"displayName\",\"type\":\"string\"},{\"name\":\"value\",\"type\":\"long\"}]}}}]}}}]}},{\"name\":\"mapCounters\",\"type\":\"JhCounters\"},{\"name\":\"reduceCounters\",\"type\":\"JhCounters\"}]}");
  public org.apache.avro.util.Utf8 jobid;
  public long finishTime;
  public int finishedMaps;
  public int finishedReduces;
  public int failedMaps;
  public int failedReduces;
  public org.apache.hadoop.mapreduce.jobhistory.JhCounters totalCounters;
  public org.apache.hadoop.mapreduce.jobhistory.JhCounters mapCounters;
  public org.apache.hadoop.mapreduce.jobhistory.JhCounters reduceCounters;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return jobid;
    case 1: return finishTime;
    case 2: return finishedMaps;
    case 3: return finishedReduces;
    case 4: return failedMaps;
    case 5: return failedReduces;
    case 6: return totalCounters;
    case 7: return mapCounters;
    case 8: return reduceCounters;
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
    case 4: failedMaps = (java.lang.Integer)value$; break;
    case 5: failedReduces = (java.lang.Integer)value$; break;
    case 6: totalCounters = (org.apache.hadoop.mapreduce.jobhistory.JhCounters)value$; break;
    case 7: mapCounters = (org.apache.hadoop.mapreduce.jobhistory.JhCounters)value$; break;
    case 8: reduceCounters = (org.apache.hadoop.mapreduce.jobhistory.JhCounters)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
