package com.autohome.adrd.algo.click_model.optimizer.hadoop;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.io.DriverIOHelper;
import com.autohome.adrd.algo.click_model.io.IterationHelper;
import com.autohome.adrd.algo.click_model.utility.MyPair;
import com.autohome.adrd.algo.click_model.optimizer.common.LbfgsSearchDirection;
import com.autohome.adrd.algo.click_model.optimizer.common.OneStepBacktrackingLineSearch;
import com.autohome.adrd.algo.click_model.optimizer.common.OneStepWolfeLineSearch;

/**
 * author : wang chao ; yangmingmin
 */

public class LbfgsConvexLossMinimize extends AbstractConvexLossMinimize {

	private Class<? extends Mapper> mapper_class;
	private Class<? extends Reducer> reduce_class;
	private Class<? extends Reducer> combine_class;
	private int instance_num;
	private DriverIOHelper driver_io = new DriverIOHelper();

	@Override
	public void SetJobEnv(Class<? extends Mapper> mapper_class,
			Class<? extends Reducer> reduce_class, Class<? extends Reducer> combine_class, int instance_num) {
		this.mapper_class = mapper_class;
		this.combine_class = combine_class;
		this.reduce_class = reduce_class;
		this.instance_num = instance_num;		
	}

	@Override
	protected HashMap<String, MyPair<Double, SparseVector>> calc_grad_loss(Map<String, SparseVector> weight, int iter) {
		// TODO Auto-generated method stub
		HashMap<String, MyPair<Double, SparseVector>> result = new HashMap<String, MyPair<Double, SparseVector>>();
		try {
			// save weight
			System.out.println("save weight begins");
			IterationHelper.writeSparseVectorMapFast(fs, new Path(calc_weight_path), weight);
			System.out.println("save weight ends");

			driver_io.doLbfgsIteration(conf, input_loc, output_loc, calc_weight_path, mapper_class, reduce_class, combine_class, iter, instance_num, regularizationFactor,
					sample_freq);

			System.out.println("read weight begins");
			Map<String, SparseVector> grads = IterationHelper.readSparseVectorMap(fs, new Path(output_loc));
			System.out.println("read weight ends");

			Iterator<Entry<String, SparseVector>> grads_iter = grads.entrySet().iterator();
			while (grads_iter.hasNext()) {
				Entry<String, SparseVector> entry = grads_iter.next();
				double loss = entry.getValue().getValue(-1);
				entry.getValue().getData().remove(-1);
				MyPair<Double, SparseVector> pair = new MyPair<Double, SparseVector>(loss, entry.getValue());
				result.put(entry.getKey(), pair);
			}
			return result;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();    
		}

		return null;
	}

	@Override
	protected SparseVector update_step(String id) {
		return line_search.get(id).getNextPoint();
	}

	@Override
	protected void init_search_direction(String id) {
		LbfgsSearchDirection d = new LbfgsSearchDirection();
		search_direction.put(id, d);
	}

	@Override
	protected void init_linesearcher(String id, Map<String, MyPair<Double, SparseVector>> loss_grad, Map<String, SparseVector> weights_map) {

		//OneStepWolfeLineSearch ls = new OneStepWolfeLineSearch();
		// OneStepWolfeLineSearch ls = new OneStepWolfeLineSearch();
		OneStepBacktrackingLineSearch ls = new OneStepBacktrackingLineSearch();
		ls.set(weights_map.get(id), loss_grad.get(id).getFirst(), loss_grad.get(id).getSecond(), search_direction.get(id).calcSearchDirction(loss_grad.get(id).getSecond()));

		line_search.put(id, ls);
	}

}
