package com.autohome.adrd.algo.click_model.driver;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;

//import org.apache.hadoop.zebra.mapreduce.TableInputFormat;


import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

import com.autohome.adrd.algo.click_model.data.Sample;
import com.autohome.adrd.algo.click_model.data.writable.SingleInstanceWritable;
import com.autohome.adrd.algo.click_model.io.AbstractProcessor;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;


public class SampleGenerator extends AbstractProcessor{
	
	
	public static class SampleGeneratorMapper extends Mapper<LongWritable, Text, LongWritable, SingleInstanceWritable> {
		private SampleGeneratorHelper helper = new SampleGeneratorHelper();
		private Map<String, Integer> feature_id_map = new HashMap<String, Integer>();
	
		
		protected void setup(Context context) throws FileNotFoundException {
			String config_file = context.getConfiguration().get("configure_file");
			String features_id_file = context.getConfiguration().get("id_features");
			
			//helper.setup(config_file);
			helper.setup("config-hadoop.xml");
			//feature_id_map = helper.readMaps(features_id_file);	
			feature_id_map = helper.readMaps("feature_id_map.txt");
		}

		public void map(LongWritable k1, Text v1, Context context) throws IOException, InterruptedException {
			//source :
			Sample s = helper.process(v1);
			SingleInstanceWritable instance = new SingleInstanceWritable();
			
			for(String fea : s.getIdFeatures()) {
				if(feature_id_map.containsKey(fea))
					instance.addIdFea(feature_id_map.get(fea));
			}
			
			for(Map.Entry<String, Double> ent : s.getFloatFeatures().entrySet()) {
				if(feature_id_map.containsKey(ent.getKey()))
					instance.addFloatFea(feature_id_map.get(ent.getKey()), ent.getValue());
			}
			
			if(s != null) {
				context.write(k1, instance);
			}	
		}
	}
	

	@Override
	protected void configJob(Job job) {

		job.setMapperClass(SampleGeneratorMapper.class);
	    //job.setOutputKeyClass(IntWritable.class);
		//job.setOutputValueClass(Text.class);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(SingleInstanceWritable.class);
		
	}
	

}
