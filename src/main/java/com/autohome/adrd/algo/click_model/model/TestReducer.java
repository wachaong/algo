package com.autohome.adrd.algo.click_model.model;

/**

 * author : wang chao
 */

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.io.IterationHelper;
import com.autohome.adrd.algo.click_model.utility.CommonFunc;


public class TestReducer extends Reducer<Text, DoubleWritable, Text, Text> {

	private static Map<Integer, SparseVector> weight_maps;
	private static String weight_loc;
	private FileSystem fs;
	private static int instance_num;
	private static double C_reg;

	public void setup(Context context) {
	
	}

	public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
		double sum = 0.0;
		
		for (DoubleWritable value : values) {
			sum += value.get();
		}
		context.write(new Text(key.toString()), new Text(String.valueOf(sum)));		

	}
}
