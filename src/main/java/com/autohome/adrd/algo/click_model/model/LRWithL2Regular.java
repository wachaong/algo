package com.autohome.adrd.algo.click_model.model;

import com.autohome.adrd.algo.click_model.optimizer.*;
import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.data.Vector;
import com.autohome.adrd.algo.click_model.data.writable.InstancesWritable;
import com.autohome.adrd.algo.click_model.data.writable.SingleInstanceWritable;
import com.autohome.adrd.algo.click_model.utility.MyPair;

public class LRWithL2Regular {
	private SparseVector weight = new SparseVector();
	private double regular_coeff = 0;
	
	public class SingleInstanceLoss<V extends Vector> implements DifferentiableFunction<V> {
		
		private SingleInstanceWritable instance = new SingleInstanceWritable();
		
		public SingleInstanceLoss(SingleInstanceWritable _instance) {
			instance = _instance;
		}
		
		public void setInstance(SingleInstanceWritable _instance) {
			instance = _instance;
		}

		@Override
		public double eval(V weight) {
			double tmp = 0.0;
			for(int i : instance.getId_fea_vec()) {
				tmp += weight.getValue(i);
			}
			for(MyPair<Integer, Double> pair : instance.getFloat_fea_vec()) {
				tmp += pair.getSecond() * weight.getValue(pair.getFirst());
			}
			if(instance.getLabel() < 0.5)
				tmp *= -1;
			double ans = Math.log(Util.sigmoid(tmp));
			ans += regular_coeff * weight.dot(weight); 
			return ans;
		}

		@Override
		public V diff(V x) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public class MiniBatchLoss<V extends Vector> implements DifferentiableFunction<V> {

		@Override
		public double eval(V x) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public V diff(V x) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
}
