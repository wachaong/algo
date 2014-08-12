package com.autohome.adrd.algo.click_model.bucket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class LBFGSIterationDriver {
	
	public static void runIteration(Configuration conf,
									 Path input,
									 Path weight_in,
									 Path weight_out)
	throws IOException, InterruptedException, ClassNotFoundException {
		Job job = new Job(conf, "job1");
	    
		job.setMapOutputKeyClass(LongWritable.class);  
	    job.setMapOutputValueClass(Text.class);  
	    job.setOutputKeyClass(LongWritable.class);  
	    job.setOutputValueClass(Cluster.class);  
	  
	    job.setInputFormatClass(SequenceFileInputFormat.class);  
	    job.setOutputFormatClass(SequenceFileOutputFormat.class);  
	    job.setMapperClass(KMeansMapper.class);  
	    job.setCombinerClass(KMeansCombiner.class);  
	    job.setReducerClass(KMeansReducer.class);  


	}

}
