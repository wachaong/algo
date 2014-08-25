package com.autohome.adrd.algo.click_model.test;

/**

 * author : wang chao
 */

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class TestlMapper extends Mapper<LongWritable, Text, Text, Text> {
	
	public void setup(Context context) {
	}
	
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		
		context.write(value, new Text(""));
	}
}
