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
import org.apache.hadoop.mapreduce.Reducer;
import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.utility.CommonFunc;

public class LR_L2_IterationReducer extends Reducer<IntWritable, DoubleWritable, Text, DoubleWritable>{

	private static SparseVector weight_map;	
	private static int instance_num;
	private static double C_reg;
	
	public void setup(Context context) {
		instance_num = context.getConfiguration().getInt("instance_num", -1);
		C_reg = context.getConfiguration().getFloat("C_reg", 1.0f);
		weight_map = getWeightParameters();
		//weight_map = CommonFunc.readDoubleMaps(weight_file_name, CommonFunc.TAB, 0, 1, in_encoding);
	}

    protected SparseVector getWeightParameters() {
        //return IterationHelper.readParametersFromHdfs(fs, previousIntermediateOutputLocationPath, iteration);
    	return new SparseVector();
    }
	
	public void reduce(IntWritable key, Iterable<DoubleWritable> values, Context context)
			throws IOException, InterruptedException {
		double sum = 0.0;
		double grad = 0.0;
		double avg = 0.0;

		
		for (DoubleWritable value : values) {
			sum += value.get();
		}
		avg = sum*1.0/instance_num;
		
		if(key.get()==0)
		{
			double reg_loss = -1.0 * weight_map.square() * C_reg / instance_num;
			context.write(new Text("lscore"), new DoubleWritable(-1.0*(avg + reg_loss)));
		}
		else if(weight_map.getData().containsKey(key.get()))
		{
			grad = avg + C_reg * 2.0 * weight_map.getValue(key.get())/instance_num;
			context.write(new Text(key.toString()), new DoubleWritable(grad));	
		}
		
	}
}
