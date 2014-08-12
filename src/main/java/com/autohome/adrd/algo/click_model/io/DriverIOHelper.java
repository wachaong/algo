package com.autohome.adrd.algo.click_model.io;

/**

 * author : wang chao
 */

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class DriverIOHelper {
	
	@SuppressWarnings("rawtypes")
	public void doLbfgsIteration(Job job, Path inputPath, Path outputPath, String distributedFiles, 
			Class<? extends Mapper> mapper_class, Class<? extends Reducer> reduce_class, Class<? extends Reducer> combine_class,
			int iterationNumber, long instance_num, float reg, float sample_freq) throws IOException {

		Configuration conf = job.getConfiguration();
		job.setJobName("LBFGS Optimizer " + iterationNumber);
		conf.set("mapred.child.java.opts", "-Xmx4g");
		conf.setInt("iteration.number", iterationNumber);
		conf.setLong("instance_num", instance_num);
		conf.setFloat("C_reg", reg);
		conf.setFloat("sample_freq", sample_freq);
		conf.set("tmpfiles", distributedFiles);

		job.setMapperClass(mapper_class);
		job.setReducerClass(reduce_class);
		job.setCombinerClass(combine_class);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(DoubleWritable.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(DoubleWritable.class);		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.getConfiguration().set("mapred.job.priority", "VERY_HIGH");
		
		FileInputFormat.setInputPaths(job, inputPath);
		FileSystem fs = FileSystem.get(conf);
		if (fs.exists(outputPath)) {
			fs.delete(outputPath, true);
		}
		FileOutputFormat.setOutputPath(job, outputPath);

		try {
			job.waitForCompletion(true);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
