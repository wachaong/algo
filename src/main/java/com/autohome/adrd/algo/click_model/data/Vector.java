package com.autohome.adrd.algo.click_model.data;
import org.apache.hadoop.io.Writable;

public interface Vector extends Cloneable ,Writable {
	public void clear();
	
	//Vector(int n);
	public double getValue(int i);
	public boolean has_key(int i);
	public void setValue(int i, double val);
	
	public double dot(final Vector v);
	
	
	//operations
	public Vector plus(final Vector v);
	public Vector minus(final Vector v);
	public Vector scale(double alpha);
	
	//assignment
	public void plusAssign(final Vector v);
	public void plusAssign(double alpha, final Vector v); //this = this + alpha * v
	public void minusAssign(final Vector v);
	public void minusAssign(double alpha, Vector v);
	public void scaleAssign(double alpha);
	
	
	//norm
	public double norm_1();
	public double norm_2();
	public int norm_inf();
	public Object clone();
	
	public boolean isDense();
	
	
	
	

}
