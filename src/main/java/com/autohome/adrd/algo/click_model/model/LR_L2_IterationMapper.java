package com.autohome.adrd.algo.click_model.model;

/**

 * author : wang chao
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.data.writable.SingleInstanceWritable;
import com.autohome.adrd.algo.click_model.io.IterationHelper;
import com.autohome.adrd.algo.click_model.utility.CommonFunc;

public class LR_L2_IterationMapper extends Mapper<NullWritable, SingleInstanceWritable, IntWritable, DoubleWritable> {

	private static SparseVector weight_map;
	private static float sample_freq;
	private static long sample_freq_inverse;
	
	public void setup(Context context) {
		
		weight_map = getWeightParameters();
		//weight_map = CommonFunc.readDoubleMaps("feature_weight.txt", CommonFunc.TAB, 0, 1, "utf-8");
		sample_freq = context.getConfiguration().getFloat("sample_freq", 1.0f);
		sample_freq_inverse = Math.round(1.0/sample_freq);
		
	}
	
    protected SparseVector getWeightParameters() {
        //return IterationHelper.readParametersFromHdfs(fs, previousIntermediateOutputLocationPath, iteration);
    	return new SparseVector();
    }
	
	public void map(NullWritable key, SingleInstanceWritable value, Context context)
			throws IOException, InterruptedException {
		
		
		String label =key.toString();
		String[] arr = value.toString().split("\t", -1);
		Pattern pattern = Pattern.compile("[\\+\\-]?[\\d]+([\\.][\\d]*)?([Ee][+-]?[\\d]+)?");
		double ctr0 = 0.0, ctr1 = 0.0, score=0.0, diff = 0.0;
		for (String item : arr) {
			String[] arr_inner = item.split(":");
			if(arr_inner.length==2)
			{
				if(pattern.matcher(arr_inner[1]).matches())
					score += Double.valueOf(weight_map.get(arr_inner[0]))* Double.valueOf(arr_inner[1]);
			}
		}
		if(score<-35.0)
			score=-35.0;
			
		ctr1 = 1-1.0/(1.0+Math.exp(score));
		ctr0 = 1.0 - ctr1;
		diff = sample_freq_inverse * (ctr1 - ctr0);
		
		if (label.equals("0"))
			context.write(new IntWritable(0),new DoubleWritable(sample_freq_inverse * Math.log(ctr0)));
		else
			context.write(new IntWritable(0),new DoubleWritable(Math.log(ctr1)));
		
		for (String item : arr) {
			String[] arr_inner = item.split(":");
			if(arr_inner.length==2)
			{
				if(pattern.matcher(arr_inner[1]).matches())
				{
					if(key.toString().equals("1"))
						context.write(new IntWritable(Integer.valueOf(arr_inner[0])), new DoubleWritable((ctr1)*Double.valueOf(arr_inner[1])));
					else
						context.write(new IntWritable(Integer.valueOf(arr_inner[0])), new DoubleWritable(sample_freq_inverse*ctr1*Double.valueOf(arr_inner[1])));
				}
					
			}
		}
	}
	
}
