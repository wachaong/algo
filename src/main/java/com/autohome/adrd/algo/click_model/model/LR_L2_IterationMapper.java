package com.autohome.adrd.algo.click_model.model;

/**

 * author : wang chao
 */

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.data.writable.SingleInstanceWritable;
import com.autohome.adrd.algo.click_model.utility.MyPair;

public class LR_L2_IterationMapper extends Mapper<NullWritable, SingleInstanceWritable, IntWritable, DoubleWritable> {

	private static SparseVector weight_map;
	private static float sample_freq;
	private static long sample_freq_inverse;
	private static LR_L2_Model.SingleInstanceLoss<SparseVector> loss; 
	
	public void setup(Context context) {
		
		weight_map = getWeightParameters();
		//weight_map = CommonFunc.readDoubleMaps("feature_weight.txt", CommonFunc.TAB, 0, 1, "utf-8");
		loss = new LR_L2_Model.SingleInstanceLoss<SparseVector>();
		sample_freq = context.getConfiguration().getFloat("sample_freq", 1.0f);
		sample_freq_inverse = Math.round(1.0/sample_freq);
		
	}
	
    protected SparseVector getWeightParameters() {
        //return IterationHelper.readParametersFromHdfs(fs, previousIntermediateOutputLocationPath, iteration);
    	return new SparseVector();
    }
	
	public void map(NullWritable key, SingleInstanceWritable value, Context context)
			throws IOException, InterruptedException {
		
		loss.setInstance(value);
		MyPair<Double, SparseVector> loss_grad = loss.calcValueGradient(weight_map);
		SparseVector grad = loss_grad.getSecond();
		if(value.getLabel() > 0.5)
		{
			context.write(new IntWritable(0),new DoubleWritable(loss_grad.getFirst()));
			Iterator<Map.Entry<Integer, Double>> iter = grad.getData().entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<Integer, Double> entry = iter.next();
				context.write(new IntWritable(entry.getKey()), new DoubleWritable(entry.getValue()));
			}
		}
		else
		{
			context.write(new IntWritable(0),new DoubleWritable(sample_freq_inverse * loss_grad.getFirst()));
			Iterator<Map.Entry<Integer, Double>> iter = grad.getData().entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<Integer, Double> entry = iter.next();
				context.write(new IntWritable(entry.getKey()), new DoubleWritable(sample_freq_inverse * entry.getValue()));
			}
		}
	}
}
