package com.autohome.adrd.algo.click_model.bucket;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.data.writable.SingleInstanceWritable;
import com.autohome.adrd.algo.click_model.model.LR_L2_Model;

public class LBFGSIteration {
	
	public static class LBFGSIterationMapper extends Mapper<Object, SingleInstanceWritable, Text, Text> {
		private SparseVector weight = new SparseVector();
		
		protected void setup(Context context) {
			//load weight
			String weight_input_file = context.getConfiguration().get("output_last");
			// return IterationHelper.readParametersFromHdfs(fs, previousIntermediateOutputLocationPath, iteration);
			
		}
		
		public void map(Object id, SingleInstanceWritable instance, Context context) {
			LR_L2_Model.SingleInstanceLoss<SparseVector> loss = new LR_L2_Model.SingleInstanceLoss<SparseVector>();
			loss.setInstance(instance);
			double single_loss = loss.calcValue(weight);
			SparseVector single_grad = loss.calcGradient(weight);
			String ans = new Double(single_loss).toString() + "###" + single_grad.toString();
			//context.write(id, );
		}

	}



}
