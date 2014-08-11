package com.autohome.adrd.algo.click_model.data;

import java.util.Scanner;



public class Sample extends Features {
	private double label;

	public Sample() {
		super();
		label = 0.0;
	}
	
	
	public Object clone() {
		Sample o = (Sample) super.clone();
		o.setLabel(label);
		return o;
	}
	

	
	public double getLabel() {
		return label;
	}
	
	public void setLabel(double _label) {
		label = _label;
	}
	
	public String toString() {
		return label + "\t" + super.toString();
	}
	
	public static Sample fromString(String str) {
		Sample sample = new Sample();
		Scanner in = new Scanner(str);
		sample.setLabel(in.nextDouble());
		
		int sz = in.nextInt();
		for(int i = 0; i < sz; i++) {
			sample.setFeature(in.next());
		}
		sz = in.nextInt();
		String feature;
		double value;
		for(int i = 0; i < sz; i++) {
			feature = in.next();
			value = in.nextDouble();
			sample.setFeature(feature, value);
		}
		in.close();
		
		return sample;
		
	}
}
