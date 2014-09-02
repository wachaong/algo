package com.autohome.adrd.algo.click_model.model;

/**

 * author : wangchao yangmingmin
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.BitSet;

import com.autohome.adrd.algo.click_model.optimizer.abstract_def.IDifferentiableFunction;
import com.autohome.adrd.algo.click_model.optimizer.common.*;
import com.autohome.adrd.algo.click_model.data.DenseVector;
import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.data.Vector;
import com.autohome.adrd.algo.click_model.data.writable.InstancesWritable;
import com.autohome.adrd.algo.click_model.data.writable.SingleInstanceWritable;
import com.autohome.adrd.algo.click_model.utility.MyPair;

public class LR_L2_Model {
	private SparseVector weight = new SparseVector();
	private double regular_coeff = 0;
	
	public static class SingleInstanceLoss<V extends Vector> implements IDifferentiableFunction<V> {
		
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
			double ctr1 = Util.sigmoid(weight_dot_instance);
			double loglikelihood = 0.0;
			
			if(instance.getLabel() > 0.5)
			{
				loglikelihood = Math.log(ctr1);
			}
			else
			{
				loglikelihood = Math.log(1 - ctr1);
			}
			return -loglikelihood;
		}

		@SuppressWarnings("unchecked")
		@Override
		public V calcGradient(V weight) {
			Vector ans = null;
			if(weight.isDense())
				ans = new DenseVector();
			else
				ans = new SparseVector();

			double weight_dot_instance = dot(weight, instance);
			double grad = Util.sigmoid(weight_dot_instance);
			
			if(instance.getLabel() > 0.5)
			{
				grad = grad - 1.0;
			}
			for(int i : instance.getId_fea_vec()) {
				ans.setValue(i, grad);
			}
			for(MyPair<Integer, Double> pair : instance.getFloat_fea_vec()) {
				ans.setValue(pair.getFirst(), pair.getSecond() * grad);
			}
			return (V) ans;
		}

		@Override
		public MyPair<Double, V> calcValueGradient(V weight) {
			Vector ans = null;
			if(weight.isDense())
				ans = new DenseVector();
			else
				ans = new SparseVector();
			double weight_dot_instance = dot(weight, instance);
			double ctr1 = Util.sigmoid(weight_dot_instance);
			double loglikelihood = 0.0;
			
			if(instance.getLabel() > 0.5)
			{
				loglikelihood = Math.log(ctr1);
				ctr1 = ctr1 - 1.0;
			}
			else
			{
				loglikelihood = Math.log(1 - ctr1);
			}
			for(int i : instance.getId_fea_vec()) {
				if(weight.has_key(i))
					ans.setValue(i, ctr1);
			}
			for(MyPair<Integer, Double> pair : instance.getFloat_fea_vec()) {
				if(weight.has_key(pair.getFirst()))
					ans.setValue(pair.getFirst(), pair.getSecond() * ctr1);
			}
			@SuppressWarnings("unchecked")
			MyPair<Double,V> result = new MyPair<Double,V>(-1.0 * loglikelihood, (V)ans);
			
			return result;			
			
		}
		
		/*
		 * iterator by weight
		 */
		private double dot(V _weight, SingleInstanceWritable _instance) {
			double weight_dot_instance = 0.0;						
			
			for(int i : _instance.getId_fea_vec()) {
				if(_weight.has_key(i))
					weight_dot_instance += _weight.getValue(i);
			}
			for(MyPair<Integer, Double> pair : _instance.getFloat_fea_vec()) {
				if(_weight.has_key(pair.getFirst()))
					weight_dot_instance += pair.getSecond() * _weight.getValue(pair.getFirst());
			}
			
			return weight_dot_instance;
		}
		
	}
	
	public static class MiniBatchLoss<V extends Vector> implements IDifferentiableFunction<V> {
		
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
