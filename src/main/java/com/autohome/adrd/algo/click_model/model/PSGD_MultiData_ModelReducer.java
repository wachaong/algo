package com.autohome.adrd.algo.click_model.model;

/**

 * author : wang chao
 */

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import com.autohome.adrd.algo.click_model.data.SparseVector;

public class PSGD_MultiData_ModelReducer extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		
		SparseVector sum = new SparseVector();
		int cnt = 0;
				
		for (Text value : values) {
			cnt += 1;        	
        	SparseVector tmp = SparseVector.fromString(value.toString());
        	sum.plusAssign(tmp);
		}
		sum.scaleAssign(1.0/cnt);
		
		context.write(new Text(key.toString()), new Text(sum.toString()));
				
	}
}
