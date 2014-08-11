package com.autohome.adrd.algo.click_model.sample_generator;






import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;

//import org.apache.hadoop.zebra.mapreduce.TableInputFormat;


import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

import com.autohome.adrd.algo.click_model.io.AbstractProcessor;

import java.util.*;
import java.io.IOException;
//import com.autohome.adrd.algo.kaggle.*;

public class SampleGenerator extends AbstractProcessor{
	
	
	public static class SampleGeneratorMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
		private SampleGeneratorHelper helper = new SampleGeneratorHelper();
	
		
		protected void setup(Context context) {
			//String config_file = context.getConfiguration().get("config_file");
			//helper.setup(config_file);
			helper.setup("config.xml");

			
		}

		public void map(LongWritable k1, Text v1, Context context) throws IOException, InterruptedException {
			//source :
			Sample s = helper.process(v1);
/*			
			//interaction and transformation
			ArrayList<Sample> s1 = new ArrayList<Sample>();
			s1.add(s);
			for(ArrayList<Transformer> trans_list : helper.getTransformers()) {
				ArrayList<Sample> s2 = new ArrayList<Sample>();
				for(Sample sample_in : s1) {
					for(Transformer trans_tmp : trans_list) {
						s2.add(trans_tmp.transform(sample_in));
					}
				}
				s1 = s2;
			}*/
			
			//assemble all the samples
			if(s != null) {
				context.write(k1, new Text(s.toString()));
			}
			
		}
	
	}
	
/*	public static class SampleGeneratorRecucer  
    	extends Reducer<LongWritable,Text,IntWritable, Text> {
		
		public void reduce(LongWritable key, Iterable<Text> values,
				           Context context) throws IOException, InterruptedException {
			IntWritable k = new IntWritable();
			String v = new String();
			
			for(Text value : values) {
				Sample s = Sample.fromString(value.toString());
				k.set((int)s.getLabel());
				v = Sample2VM.sample2vm(s);
				context.write(k, new Text(v));
				
			}
		}
		
	}*/

	@Override
	protected void configJob(Job job) {

		job.setMapperClass(SampleGeneratorMapper.class);
		//job.setReducerClass(SampleGeneratorRecucer.class);
	    job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Text.class);
		
	}


}
