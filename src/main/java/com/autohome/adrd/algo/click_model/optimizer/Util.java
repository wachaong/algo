package com.autohome.adrd.algo.click_model.optimizer;

public class Util {
	
	public static final double TOL = 1e-10;

	public static double sigmoid(double x) {
		return 1.0 / (1 + Math.exp(x));
	}
	
	//check if a float number equals zero
	public static boolean equalsZero(double x) {
		return Math.abs(x) < TOL;
	}
	

}
