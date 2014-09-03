package com.autohome.adrd.algo.click_model.model;

/**

 * author : wang chao
 */

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class SumDoubleReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
	public void reduce(Text key, Iterable<DoubleWritable> values, Context context) 
	throws IOException, InterruptedException {
		double sum = 0;
		for (DoubleWritable value : values) {
				sum += value.get();
		}
		context.write(key, new DoubleWritable(sum));
	}
}