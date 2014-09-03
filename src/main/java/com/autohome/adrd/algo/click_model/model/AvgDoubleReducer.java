package com.autohome.adrd.algo.click_model.model;

/**

 * author : wang chao
 */

import java.io.IOException;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class AvgDoubleReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
	public void reduce(Text key, Iterable<DoubleWritable> values, Context context) 
	throws IOException, InterruptedException {
		double sum = 0;
		int cnt = 0;
		for (DoubleWritable value : values) {
				sum += value.get();
				cnt += 1;
		}
		context.write(key, new DoubleWritable(sum/cnt));
	}
}