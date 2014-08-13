package com.autohome.adrd.algo.click_model.bucket;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.data.writable.SingleInstanceWritable;
import com.autohome.adrd.algo.click_model.model.LR_L2_Model;
import com.autohome.adrd.algo.click_model.utility.MyPair;

public class LBFGSIteration {
	
	public static class LBFGSIterationMapper extends 
			Mapper<LongWritable, SingleInstanceWritable, IntWritable, SparseVector> {
		private SparseVector weight = new SparseVector();
		
		protected void setup(Context context) {
			//load weight
			String weight_input_file = context.getConfiguration().get("output_last");
			// weight = IterationHelper.readParametersFromHdfs(fs, previousIntermediateOutputLocationPath, iteration);
			
		}
		
		public void map(LongWritable id, SingleInstanceWritable instance, Context context)
				throws IOException, InterruptedException {
			LR_L2_Model.SingleInstanceLoss<SparseVector> loss = new LR_L2_Model.SingleInstanceLoss<SparseVector>();
			loss.setInstance(instance);
			MyPair<Double, SparseVector> loss_grad = loss.calcValueGradient(weight);
			SparseVector grad = loss_grad.getSecond();
			grad.setValue(-1, loss_grad.getFirst());
			context.write(new IntWritable(0), grad);
		}		
		

	}
	
	public static class LBFGSIterationReducer extends 
			Reducer<IntWritable, SparseVector, IntWritable, SparseVector> {
		
		public void reduce(IntWritable key, Iterable<SparseVector> values, Context context) 
				throws IOException, InterruptedException {
			SparseVector ans = new SparseVector();
			for(SparseVector grad : values) {
				ans.plusAssign(grad);
			}
			context.write(key, ans);
		}
		
	}

}
