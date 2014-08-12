package com.autohome.adrd.algo.click_model.model;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;

import com.autohome.adrd.algo.click_model.data.writable.SingleInstanceWritable;

public interface Model {
	public void setup(String param);
	public void load(String param);
	public void save(String param);
	

//	public double predict(SingleInstanceWritable instance);
//	public double[] predict(InstancesWritable instances);

}
