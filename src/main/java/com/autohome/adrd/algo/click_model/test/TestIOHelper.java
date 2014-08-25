package com.autohome.adrd.algo.click_model.test;

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
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import com.autohome.adrd.algo.click_model.utility.CommonFunc;

public class TestIOHelper {
	
	/*
	 * inputPath includes dataset
	 * distributedFiles includes initial weight file, model->feas map file, 
	 */
	@SuppressWarnings("rawtypes")
	public void doLbfgsIteration(Configuration conf, String input_loc, String output_loc, 
			Class<? extends Mapper> mapper_class) throws IOException {

		Job job = new Job(conf);
		job.setJarByClass(this.getClass());
		job.setJobName("LBFGS Optimizer ");
		conf.set("mapred.child.java.opts", "-Xmx4g");
		
		
		Set<String> distributed_files = new HashSet<String>();
		//conf.set("tmpfiles", CommonFunc.join(distributed_files,",").toString());

		job.setMapperClass(mapper_class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.getConfiguration().set("mapred.job.priority", "VERY_HIGH");
		job.setNumReduceTasks(0);
		
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
