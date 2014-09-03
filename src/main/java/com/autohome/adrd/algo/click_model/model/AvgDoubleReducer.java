package com.autohome.adrd.algo.click_model.model;

/**

 * author : wang chao
 */

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class AvgDoubleReducer extends Reducer<Text, Text, Text, Text> {
	public void reduce(Text key, Iterable<Text> values, Context context) 
	throws IOException, InterruptedException {
		double sum = 0;
		int cnt = 0;
		for (Text value : values) {
				double tmp = Double.valueOf(value.toString());
				sum += tmp;
				cnt += 1;
		}
		context.write(key, new Text(String.valueOf(sum/cnt)));
	}
}