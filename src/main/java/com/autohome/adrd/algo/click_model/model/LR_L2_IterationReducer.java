package com.autohome.adrd.algo.click_model.model;

/**

 * author : wang chao
 */

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer.Context;

import com.autohome.adrd.algo.click_model.utility.CommonFunc;

public class LR_L2_IterationReducer {
	private static String in_encoding;
	private static Map<String, Double> weight_map;
	private static Map<String, Double> vee_map;		
	private static String weight_file_name;
	private static String vee_file_name;
	private static int instance_num;
	private static double C_reg;
	
	public void setup(Context context) {
		instance_num = context.getConfiguration().getInt("instance_num", -1);
		C_reg = context.getConfiguration().getFloat("C_reg", 1.0f);
		weight_file_name = context.getConfiguration().get("weight_file", "feature_weight.txt");
		vee_file_name = context.getConfiguration().get("vee_file", "vee.txt");
		
		in_encoding = context.getConfiguration().get("in_encoding", "utf-8");

		weight_map = CommonFunc.readDoubleMaps(weight_file_name, CommonFunc.TAB, 0, 1, in_encoding);
		vee_map = CommonFunc.readDoubleMaps(vee_file_name, CommonFunc.TAB, 0, 1, in_encoding);
		
		
	}

	public void reduce(IntWritable key, Iterable<DoubleWritable> values, Context context)
			throws IOException, InterruptedException {
		double sum = 0.0;
		double vee = 0.0;
		double grad = 0.0;
		double vme = 0.0;
		
		for (DoubleWritable value : values) {
			sum += value.get();
		}
		vme = sum*1.0/instance_num;
		
		if(key.get()==0)
		{
			double reg_loss = 0.0;
			Iterator<Map.Entry<String, Double>> iter = weight_map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Double> entry = iter.next();
				reg_loss -= entry.getValue() * entry.getValue() * C_reg;
			}
			reg_loss/=instance_num;
			context.write(new Text("lscore"), new DoubleWritable(-1.0*(vme+reg_loss)));
		}
		
		if(weight_map.containsKey(key.toString()) && vee_map.containsKey(key.toString()))
		{
			vee = vee_map.get(key.toString());
			grad = vme - vee + C_reg * 2.0 * weight_map.get(key.toString())/instance_num;
			context.write(new Text(key.toString()), new DoubleWritable(grad));			
		}
	}
}
