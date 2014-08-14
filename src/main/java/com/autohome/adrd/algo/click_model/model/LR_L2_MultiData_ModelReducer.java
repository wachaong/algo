package com.autohome.adrd.algo.click_model.model;

/**

 * author : wang chao
 */

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.io.IterationHelper;
import com.autohome.adrd.algo.click_model.utility.CommonFunc;

public class LR_L2_MultiData_ModelReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable>{

	private static Map<Integer, SparseVector> weight_maps;
	private static String weight_loc;
	private static int iteration_number;
	private FileSystem fs;
	private static int instance_num;
	private static double C_reg;
	
	public void setup(Context context) {
		instance_num = context.getConfiguration().getInt("instance_num", -1);
		C_reg = context.getConfiguration().getFloat("C_reg", 1.0f);
		iteration_number = context.getConfiguration().getInt("iteration_number", -1);

		if (iteration_number == 1) {
			// load init weight
			weight_maps = CommonFunc.readSparseVectorMap("feature_weight.txt");
		} else {
			try {
				fs = FileSystem.get(context.getConfiguration());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			weight_loc = context.getConfiguration().get("output_loc");
			weight_maps = IterationHelper.readSparseVectorMap(fs, new Path(weight_loc));
		}	
	}
	
	public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
			throws IOException, InterruptedException {
		double sum = 0.0;
		double grad = 0.0;
		double avg = 0.0;
		
		for (DoubleWritable value : values) {
			sum += value.get();
		}
		avg = sum*1.0/instance_num;
		int model_id = Integer.parseInt(key.toString().split("_")[0]);
		String part = key.toString().split("_")[1];
		if(part.equals("loss"))
		{				
			double reg_loss = -1.0 * weight_maps.get(model_id).square() * C_reg / instance_num;
			context.write(key, new DoubleWritable(-1.0*(avg + reg_loss)));
		}
		else
		{
			int fea_id = Integer.parseInt(part);
			grad = avg + C_reg * 2.0 * weight_maps.get(model_id).getValue(fea_id)/instance_num;
			context.write(new Text(key.toString()), new DoubleWritable(grad));	
		}
		
	}
}