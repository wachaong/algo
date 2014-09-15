package com.autohome.adrd.algo.click_model.io;

/**

 * author : wang chao
 */

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class DriverIOHelper {
	
	/*
	 * inputPath includes dataset
	 * distributedFiles includes initial weight file, model->feas map file, 
	 */
	@SuppressWarnings("rawtypes")
	public void doLbfgsIteration(Configuration conf, String input_loc, String output_loc, 
			String calc_weight_path,
			Class<? extends Mapper> mapper_class, Class<? extends Reducer> reduce_class, Class<? extends Reducer> combine_class,
			int iter,
			long instance_num, 
			double reg, 
			float sample_freq) throws IOException {

		Job job = new Job(conf);
		job.setJarByClass(this.getClass());
		job.setJobName("LBFGS Optimizer " + String.valueOf(iter));

		job.setMapperClass(mapper_class);
		job.setReducerClass(reduce_class);
		job.setCombinerClass(combine_class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DoubleWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);		
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setNumReduceTasks(40);

		job.getConfiguration().set("mapreduce.map.java.opts", "-Xmx10g");
		job.getConfiguration().set("mapred.child.java.opts", "-Xmx20g");
		job.getConfiguration().set("calc_weight_path", calc_weight_path);
		job.getConfiguration().setLong("instance_num", instance_num);
		job.getConfiguration().setDouble("C_reg", reg);
		job.getConfiguration().setFloat("sample_freq", sample_freq);
		job.getConfiguration().set("mapred.job.priority", "VERY_HIGH");
		
		String value = Long.toString(32 * 67108864L);
		job.getConfiguration().set("mapred.min.split.size", value);
		job.getConfiguration().set("table.input.split.minSize", value);
		
		Path inputPath = new Path(input_loc);
		Path outputPath = new Path(output_loc);
		
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

	
	@SuppressWarnings("rawtypes")
	public void doPSGD(Configuration conf, String input_loc, String output_loc, 
			String calc_weight_path,
			Class<? extends Mapper> mapper_class, Class<? extends Reducer> reduce_class, 
			float sample_freq) throws IOException {

		Job job = new Job(conf);
		job.setJarByClass(this.getClass());

		job.setMapperClass(mapper_class);
		job.setReducerClass(reduce_class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);		
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setNumReduceTasks(40);
		
		job.getConfiguration().set("mapred.child.java.opts", "-Xmx4g");
		job.getConfiguration().set("calc_weight_path", calc_weight_path);
		job.getConfiguration().setFloat("sample_freq", sample_freq);
		job.getConfiguration().set("mapred.job.priority", "VERY_HIGH");
		
		String value = Long.toString(32 * 67108864L);
		job.getConfiguration().set("mapred.min.split.size", value);
		job.getConfiguration().set("table.input.split.minSize", value);
		
		Path inputPath = new Path(input_loc);
		Path outputPath = new Path(output_loc);
		
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
