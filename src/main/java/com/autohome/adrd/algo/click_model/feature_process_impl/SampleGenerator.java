package com.autohome.adrd.algo.click_model.feature_process_impl;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;

//import org.apache.hadoop.zebra.mapreduce.TableInputFormat;


import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

import com.autohome.adrd.algo.click_model.data.Sample;
import com.autohome.adrd.algo.click_model.io.AbstractProcessor;

import java.util.*;
import java.io.IOException;


public class SampleGenerator extends AbstractProcessor{
	
	private Map<String, Integer> id_features = new HashMap<String, Integer>();
	
	
	
	public static class SampleGeneratorMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
		private SampleGeneratorHelper helper = new SampleGeneratorHelper();
	
		
		protected void setup(Context context) {
			String config_file = context.getConfiguration().get("configure_file");
			String features_id_file = context.getConfiguration().get("id_features");
			
			helper.setup(config_file);
			//helper.setup("config.xml");
			
		
		}

		public void map(LongWritable k1, Text v1, Context context) throws IOException, InterruptedException {
			//source :
			Sample s = helper.process(v1);
			if(s != null) {
				context.write(k1, new Text(s.toString()));
			}
			
		}
	
	}
	

	@Override
	protected void configJob(Job job) {

		job.setMapperClass(SampleGeneratorMapper.class);
	    job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Text.class);
		
	}


}
