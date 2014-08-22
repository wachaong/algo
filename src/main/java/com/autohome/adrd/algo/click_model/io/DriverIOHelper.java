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
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import com.autohome.adrd.algo.click_model.utility.CommonFunc;

public class DriverIOHelper {
	
	/*
	 * inputPath includes dataset
	 * distributedFiles includes initial weight file, model->feas map file, 
	 */
	@SuppressWarnings("rawtypes")
	public void doLbfgsIteration(Configuration conf, String input_loc, String output_loc, 
			String init_weight_path, 
			Class<? extends Mapper> mapper_class, Class<? extends Reducer> reduce_class, Class<? extends Reducer> combine_class,
			int iter,
			long instance_num, 
			double reg, 
			double sample_freq) throws IOException {

		Job job = new Job(conf);
		job.setJarByClass(this.getClass());
		job.setJobName("LBFGS Optimizer ");
		conf.set("mapred.child.java.opts", "-Xmx4g");
		conf.set("output_loc", output_loc);
		conf.setLong("instance_num", instance_num);
		conf.setDouble("C_reg", reg);
		conf.setDouble("sample_freq", sample_freq);
		
		Set<String> distributed_files = new HashSet<String>();
		conf.setInt("iteration_number", iter);
		if( iter ==1 )
		{
			distributed_files.add(init_weight_path);
		}		
		conf.set("tmpfiles", CommonFunc.join(distributed_files,",").toString());

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
