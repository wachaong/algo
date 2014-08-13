package com.autohome.adrd.algo.click_model.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.autohome.adrd.algo.click_model.optimizer.*;
import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.data.Vector;
import com.autohome.adrd.algo.click_model.data.writable.InstancesWritable;
import com.autohome.adrd.algo.click_model.data.writable.SingleInstanceWritable;
import com.autohome.adrd.algo.click_model.utility.MyPair;

public class LR_L2_Model {
	private SparseVector weight = new SparseVector();
	private double regular_coeff = 0;
	
	public static class SingleInstanceLoss<V extends Vector> implements DifferentiableFunction<V> {
		
		private SingleInstanceWritable instance = new SingleInstanceWritable();
		
		public SingleInstanceLoss() {
		}
		
		public SingleInstanceLoss(SingleInstanceWritable _instance) {
			instance = _instance;
		}
		
		public void setInstance(SingleInstanceWritable _instance) {
			instance = _instance;
		}

		@Override
		public double calcValue(V weight) {
			double weight_dot_instance = dot(weight, instance);
			double label = 1.0;
			if(instance.getLabel() < 0.5)
				label = -1.0;
			double loglikelihood = Math.log(Util.sigmoid(weight_dot_instance * label));
			return -loglikelihood;
		}

		@Override
		public V calcGradient(V weight) {
			Constructor<V>[] construct = (Constructor<V>[]) weight.getClass().getConstructors();
			V ans = null;
			try {
				ans = construct[0].newInstance();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			double weight_dot_instance = dot(weight, instance);
			double label = 1.0;
			if(instance.getLabel() < 0.5)
				label = -1.0;
			double sigma = Util.sigmoid(weight_dot_instance * label);
			double tmp = (sigma - 1.0) * label;
			
			for(int i : instance.getId_fea_vec()) {
				ans.setValue(i, tmp);
			}
			for(MyPair<Integer, Double> pair : instance.getFloat_fea_vec()) {
				ans.setValue(pair.getFirst(), pair.getSecond() * tmp);
			}
			return ans;
		}

		@Override
		public MyPair<Double, V> calcValueGradient(V x) {
			return null;
			// TODO Auto-generated method stub
			
			
		}
		
		private double dot(V _weight, SingleInstanceWritable _instance) {
			double weight_dot_instance = 0.0;
			for(int i : _instance.getId_fea_vec()) {
				weight_dot_instance += _weight.getValue(i);
			}
			for(MyPair<Integer, Double> pair : _instance.getFloat_fea_vec()) {
				weight_dot_instance += pair.getSecond() * _weight.getValue(pair.getFirst());
			}
			
			return weight_dot_instance;
		}
		
	}
	
	public static class MiniBatchLoss<V extends Vector> implements DifferentiableFunction<V> {
		
		private InstancesWritable instances = new InstancesWritable();

		public MiniBatchLoss() {
		}
		
		public MiniBatchLoss(InstancesWritable _instances) {
			instances = _instances;
		}
		
		public void setInstance(InstancesWritable _instances) {
			instances = _instances;
		}

		@Override
		public MyPair<Double, V> calcValueGradient(V x) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public double calcValue(V x) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public V calcGradient(V x) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	//public 
	
}
