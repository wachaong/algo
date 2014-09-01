package com.autohome.adrd.algo.click_model.model;

/**

 * author : wang chao
 */

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.data.writable.SingleInstanceWritable;
import com.autohome.adrd.algo.click_model.io.IterationHelper;
import com.autohome.adrd.algo.click_model.utility.CommonFunc;
import com.autohome.adrd.algo.click_model.utility.MyPair;

public class TestMapper extends Mapper<SingleInstanceWritable, NullWritable, Text, DoubleWritable> {

	private static Map<Integer, SparseVector> weight_maps;
	private static float sample_freq;
	private static long sample_freq_inverse;
	private static String weight_loc;
	private static LR_L2_Model.SingleInstanceLoss<SparseVector> loss;
	private FileSystem fs;

	public void setup(Context context) {

		try {
			fs = FileSystem.get(context.getConfiguration());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		weight_loc = context.getConfiguration().get("calc_weight_path");		
		weight_maps = IterationHelper.readSparseVectorMapFast(fs, new Path(weight_loc));

		loss = new LR_L2_Model.SingleInstanceLoss<SparseVector>();
		sample_freq = context.getConfiguration().getFloat("sample_freq", 1.0f);
		sample_freq_inverse = Math.round(1.0 / sample_freq);
	}

	public void map(SingleInstanceWritable key, NullWritable value, Context context) throws IOException, InterruptedException {

		// share instance, using bitmap to refer mapping from model to features
		
		
		if(key.getLabel() > 0.5)
		{
			for ( int i :key.getId_fea_vec())
			{
				context.write(new Text("pos" + String.valueOf(i)), new DoubleWritable(1.0));
			}
			context.write(new Text("pos"), new DoubleWritable(1.0));
		}
		else
		{
			for ( int i :key.getId_fea_vec())
			{
				context.write(new Text("neg" + String.valueOf(i)), new DoubleWritable(1.0));
			}
			context.write(new Text("neg"), new DoubleWritable(1.0));
		}
		
	}
}
