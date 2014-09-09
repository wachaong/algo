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
import com.autohome.adrd.algo.click_model.optimizer.common.LbfgsSearchDirection;
import com.autohome.adrd.algo.click_model.optimizer.common.OneStepBacktrackingLineSearch;
import com.autohome.adrd.algo.click_model.utility.MyPair;

public class OwlqnConvexLossMinimize extends AbstractConvexLossMinimize {

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
	protected HashMap<Integer, MyPair<Double, SparseVector>> calc_grad_loss(Map<Integer, SparseVector> weight, int iter) {
		// TODO Auto-generated method stub
		HashMap<Integer, MyPair<Double, SparseVector>> result = new HashMap<Integer, MyPair<Double, SparseVector>>();
		try {

			// save weight
			System.out.println("save weight begins");
			IterationHelper.writeSparseVectorMapFast(fs, new Path(calc_weight_path), weight);
			System.out.println("save weight ends");

			driver_io.doLbfgsIteration(conf, input_loc, output_loc, calc_weight_path, mapper_class, reduce_class, combine_class, iter, instance_num, 0, sample_freq);

			System.out.println("read weight begins");
			Map<Integer, SparseVector> grads = IterationHelper.readSparseVectorMap(fs, new Path(output_loc));
			System.out.println("read weight ends");
			double normalizedregularizationFactor = regularizationFactor/instance_num;

			Iterator<Entry<Integer, SparseVector>> grads_iter = grads.entrySet().iterator();
			while (grads_iter.hasNext()) {
				Entry<Integer, SparseVector> entry = grads_iter.next();

				
				
				// add l1 regulation
				SparseVector modelweight = weight.get(entry.getKey());

				double loss = entry.getValue().getValue(-1) + modelweight.norm_1() * normalizedregularizationFactor;

				// pseudo gradient
				if (regularizationFactor > 0) {
					for (int i : modelweight.getData().keySet()) {
						double PseudoGrad = 0;
						if (modelweight.getValue(i) < 0) {
							PseudoGrad = entry.getValue().getValue(i) - normalizedregularizationFactor;
							entry.getValue().setValue(i, PseudoGrad);
						} else if (modelweight.getValue(i) > 0) {
							PseudoGrad = entry.getValue().getValue(i) + normalizedregularizationFactor;
							entry.getValue().setValue(i, PseudoGrad);
						} else {
							if (entry.getValue().getValue(i) < normalizedregularizationFactor * (-1)) {
								PseudoGrad = entry.getValue().getValue(i) + normalizedregularizationFactor;
								entry.getValue().setValue(i, PseudoGrad);
							} else if (entry.getValue().getValue(i) > normalizedregularizationFactor) {
								PseudoGrad = entry.getValue().getValue(i) - normalizedregularizationFactor;
								entry.getValue().setValue(i, PseudoGrad);
							} else {
								PseudoGrad = 0;
								entry.getValue().setValue(i, PseudoGrad);
							}

						}

					}

				}

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
	protected SparseVector update_step(int id) {
		SparseVector st = line_search.get(id).getNextPoint();
		
		if(regularizationFactor>0){
			for(int i : st.getData().keySet()){
				if(st.getValue(i)*st.getValue(i)< 0.0){
					st.setValue(i, 0.0);
				}
			}			
		}		
		return st;
	}

	@Override
	protected void init_search_direction(int id) {
		LbfgsSearchDirection d = new LbfgsSearchDirection();
		search_direction.put(id, d);
	}

	@Override
	protected void init_linesearcher(int id, Map<Integer, MyPair<Double, SparseVector>> loss_grad, Map<Integer, SparseVector> weights_map) {

		// OneStepWolfeLineSearch ls = new OneStepWolfeLineSearch();
		OneStepBacktrackingLineSearch ls = new OneStepBacktrackingLineSearch();

		SparseVector grad = loss_grad.get(id).getSecond();
		SparseVector st = search_direction.get(id).calcSearchDirction(grad);
		
		//project Quasi-Newton  direction to minus pseudo gradient
		if(regularizationFactor>0){
			for(int i : st.getData().keySet()){
				if(st.getValue(i)* grad.getValue(i) >= 0){
					st.setValue(i, 0);
				}
			}
		}
		
		ls.set(weights_map.get(id), loss_grad.get(id).getFirst(), grad, st);
		line_search.put(id, ls);
	}

}
