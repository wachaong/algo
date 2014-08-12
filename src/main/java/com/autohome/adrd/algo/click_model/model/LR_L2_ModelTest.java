package com.autohome.adrd.algo.click_model.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.data.writable.SingleInstanceWritable;
import com.autohome.adrd.algo.click_model.optimizer.*;

public class LR_L2_ModelTest {
	SingleInstanceWritable instance1 = new SingleInstanceWritable();
	SingleInstanceWritable instance2 = new SingleInstanceWritable();

	@Before
	public void setUp() throws Exception {
		instance1.addFloatFea(1, 1.1);
		instance1.addFloatFea(2, 1.2);
		instance1.addIdFea(3);
		
		instance1.addFloatFea(1, 1.3);
		instance1.addFloatFea(2, 1.4);
		instance1.addIdFea(4);
		
		
	}

	@Test
	public void test() {
		
		LR_L2_Model.SingleInstanceLoss<SparseVector> loss = new LR_L2_Model.SingleInstanceLoss<SparseVector>(instance1);
		loss.setInstance(instance1);
		SparseVector weight = new SparseVector();
		weight.setValue(1, 0.1);
		weight.setValue(2, 0.2);
		weight.setValue(3, 0.3);
		weight.setValue(1, 0.4);
		System.out.println(loss.calcValue(weight));
		//System.out.println(loss.calcGradient(weight));
		//System.out.println(loss.calcValueGradient(weight));
		
		loss.setInstance(instance2);
		System.out.println(loss.calcValue(weight));
		//System.out.println(loss.calcGradient(weight));
		//System.out.println(loss.calcValueGradient(weight));
		
		fail("Not yet implemented");
	}

}
