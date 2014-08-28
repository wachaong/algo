package com.autohome.adrd.algo.click_model.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.data.writable.SingleInstanceWritable;
import com.autohome.adrd.algo.click_model.model.LR_L2_Model;
import com.autohome.adrd.algo.click_model.optimizer.IDifferentiableFunction;
import com.autohome.adrd.algo.click_model.optimizer.SingleOptimizer;
import com.autohome.adrd.algo.click_model.utility.MyPair;

public class TestSingleOptimizer {
	public class F implements IDifferentiableFunction<SparseVector> {
		
		SingleInstanceWritable ins1 = new SingleInstanceWritable();
		SingleInstanceWritable ins2 = new SingleInstanceWritable();
		LR_L2_Model.SingleInstanceLoss<SparseVector> loss = 
				new LR_L2_Model.SingleInstanceLoss<SparseVector>();
		
		public F() {
			ins1.addFloatFea(1, 1.0);
			ins1.addFloatFea(2, 2.0);
			ins1.addFloatFea(3, 3.0);
			ins1.setLabel(1.0);
			loss.setInstance(ins1);
		}

		@Override
		public double calcValue(SparseVector x) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public SparseVector calcGradient(SparseVector x) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MyPair<Double, SparseVector> calcValueGradient(SparseVector x) {
			double f = x.norm_2();
			SparseVector df = (SparseVector) x.scale(2.0);
			return new MyPair<Double, SparseVector>(f, df);
		}
		
	}
	@Test
	public void test() {

		
		SingleInstanceWritable ins1 = new SingleInstanceWritable();
		SingleInstanceWritable ins2 = new SingleInstanceWritable();
		LR_L2_Model.SingleInstanceLoss<SparseVector> loss = 
				new LR_L2_Model.SingleInstanceLoss<SparseVector>();
		SingleOptimizer opt = new SingleOptimizer();
		
		ins1.addFloatFea(1, 1.0);
		ins1.addFloatFea(2, 2.0);
		ins1.addFloatFea(3, 3.0);
		ins1.setLabel(-1.0);
		loss.setInstance(ins1);
		SparseVector weight = new SparseVector();
		weight.setValue(1, 1.0);
		weight.setValue(2, 1.0);
		weight.setValue(3, 5.0);
		
		System.out.println(loss.calcValueGradient(weight));
		int i = opt.minimize(loss, weight);
		
		
	}
	
	@Test
	public void test2() {
		System.out.println("+++++++++++++++++++++++++++++++++++++");
		SingleOptimizer opt = new SingleOptimizer();
		SparseVector x0 = new SparseVector();
		x0.setValue(1, 8);
		x0.setValue(3, 1);
		F f = new F();
		System.out.println(11111);
		System.out.println(x0);
		int i = opt.minimize(f, x0);
		System.out.println(i);
		System.out.println(x0); 
	}
	

}
