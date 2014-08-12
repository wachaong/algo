package com.autohome.adrd.algo.click_model.model;

/**

 * author : wang chao
 */

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class SumCombiner extends Reducer<IntWritable, DoubleWritable, IntWritable, DoubleWritable> {
	public void reduce(IntWritable key, Iterable<DoubleWritable> values, Context context) 
	throws IOException, InterruptedException {
		double sum = 0;
		for (DoubleWritable value : values) {
				sum += value.get();
		}
		context.write(key, new DoubleWritable(sum));
	}
}