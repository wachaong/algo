package com.autohome.adrd.algo.click_model.data;

import java.util.HashMap;
import java.util.Map;

public class SparseVector implements Vector {
	private HashMap<Integer, Double> _data = null; 
	
	public SparseVector() {
		_data = new HashMap<Integer, Double>();
	}
	
	public SparseVector(final SparseVector v) {
		_data = new HashMap<Integer, Double>();
		_data.putAll(v._data);

	}
	
	public HashMap<Integer, Double> getData() {
		return _data;
	}
	
	public void setData(HashMap<Integer, Double> data) {
		_data = data;
	}

	public double getValue(int i) {
		if(this.getData().containsKey(i)) {
			return _data.get(i);
		}
		else
			return 0.0;
	}
	
	public void setValue(int i, double value) {
		_data.put(i, value);
	}
	
	
	// this = this * alpha
	public Vector scale(double alpha) {
		SparseVector ans = new SparseVector();
		Integer i = null;
		Double val = null;
		for(Map.Entry<Integer, Double> elem : _data.entrySet()) {
			i = elem.getKey();
			val = elem.getValue();
			ans._data.put(i, val * alpha);
		}
		return ans;
	}
	
	//Assignments
	@Override
	public void plusAssign(Vector v) {
		plusAssign(1.0, v);
	}
	
	// this = this + alpha * v
	public void plusAssign(double alpha, final Vector v) {
		Integer i = null;
		Double val = null;
		for(Map.Entry<Integer, Double> elem : ((SparseVector)v)._data.entrySet()) {
			i = elem.getKey();
			val = elem.getValue();
			_data.put(i, _data.get(i) + alpha * val);
		}
	}

	@Override
	public void minusAssign(Vector v) {
		plusAssign(-1.0, v);
		
	}

	@Override
	public void minusAssign(double alpha, Vector v) {
		plusAssign(-alpha, v);
		
	}

	@Override
	public void scaleAssign(double alpha) {
		Integer i = null;
		Double val = null;
		for(Map.Entry<Integer, Double> elem : _data.entrySet()) {
			i = elem.getKey();
			val = elem.getValue();
			_data.put(i, val * alpha);
		}
		
	}
	

	
	// this^T * v
	public double dot(final Vector v)  {
		double ans = 0.0;
		Integer i = null;
		Double val = null;
		if(_data.size() < ((SparseVector)v)._data.size()) {
			for(Map.Entry<Integer, Double> elem : _data.entrySet()) {
				i = elem.getKey();
				val = elem.getValue();
				ans += val * ((SparseVector)v)._data.get(i);
			}
		}
		else {
			for(Map.Entry<Integer, Double> elem : ((SparseVector)v)._data.entrySet()) {
				i = elem.getKey();
				val = elem.getValue();
				ans += val * _data.get(i);
			}
		}
		return ans;
	}
	
	public double norm_1() {
		double ans = 0.0;
		for(Double val : _data.values()) {
			ans += Math.abs(val);
		}
		return ans;
	}
	
	public double norm_2() {
		double ans = 0.0;
		for(Double val : _data.values()) {
			ans += val * val;
		}
		return Math.sqrt(ans);
	}
	
	public int norm_inf_index() {
		Integer ans = -1;
		Double max_abs_value = -1.0;
		Integer i = null;
		Double val = null;
		for(Map.Entry<Integer, Double> elem : _data.entrySet()) {
			i = elem.getKey();
			val = elem.getValue();
			if(Math.abs(val) > max_abs_value) {
				max_abs_value = val;
				ans = i;
			}
		}
		return ans;
	}
	
	public void assign(final SparseVector v) {
		_data.clear();
		_data.putAll(v._data);
	}
	
	
	public void assignTmp(SparseVector v) {
		_data = v._data;
		v._data = null;
	}
	
	public void swap(SparseVector v) {
		HashMap<Integer, Double> tmp = _data;
		_data = v._data;
		v._data = tmp;
	}
	
	public Object clone() {
		SparseVector ans = new SparseVector(this);
		return ans;
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		for(int i : _data.keySet()) {
			str.append(i + ":" + _data.get(i) + " ");
		}
		return str.toString();
	}

	@Override
	public int norm_inf() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vector plus(Vector v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector minus(Vector v) {
		// TODO Auto-generated method stub
		return null;
	}

}
