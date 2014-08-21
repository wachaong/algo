package com.autohome.adrd.algo.click_model.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DenseVector implements Vector  {
	
	private double[] _data = null;
	
	public int size() {
		return _data.length;
	}
	
	public DenseVector() {
		_data = new double[0];
	}
	
	public DenseVector(int dim) {
		_data = new double[dim];
	}
	
	public void clear() {
		_data = _data = new double[0];
	}
	public DenseVector(DenseVector v) {
		_data = new double[v._data.length];
		System.arraycopy(v._data, 0, _data, 0, v._data.length);
	}
	
	public double[] getData() {
		return _data;
	}
	
	public void setData(double [] v) {
		_data = v;
	}
	
	public double getValue(int i) {
		return _data[i];
	}
	
	public void setValue(int i, double value) {
		_data[i] = value;
	}
	
	@Override
	public double dot(final Vector v) {
		return BLAS.dot(this, (DenseVector)v);
	}
	

	
	public double norm_1() {
		double ans = 0.0;
		for(double v : _data) {
			ans += Math.abs(v);
		}
		return ans;
	}
	
	public double norm_2() {
		double ans = 0.0;
		for(double v : _data) {
			ans += v * v;
		}
		return Math.sqrt(ans);
	}
	
	public int norm_inf() {
		int idx = -1;
		double max_abs = -1;
		for(int i = 0; i < _data.length; ++i) {
			if(Math.abs(_data[i]) > max_abs) {
				max_abs = _data[i];
				idx = i;
			}
		}
		return idx;
	}
	
	public void assign(final DenseVector v) {
		if(_data.length != v._data.length) {
			_data = new double[v._data.length];
		}
		System.arraycopy(v._data, 0, _data, 0, _data.length);
	}
	
	public void assignTmp(DenseVector v) {
		_data = v._data;
		v._data = null;
	}
	
	public void swap(DenseVector v) {
		double[] tmp = _data;
		_data = v._data;
		v._data = tmp;
	}
	
	public Object clone() {
		DenseVector v = new DenseVector(_data.length);
		System.arraycopy(_data, 0, v._data, 0, _data.length);
		return v;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(_data.length);
		for(double e : _data) {
			sb.append(" " + e);
		}
		return sb.toString();
	}

	//operations
	@Override
	public Vector plus(final Vector v) {
		if(_data.length != ((DenseVector)v)._data.length) {
			throw new DimensionNotMatchException();
		}
		DenseVector ans = new DenseVector(((DenseVector)v)._data.length);
		for(int i = 0; i < ans.getData().length; ++i) {
			ans._data[i] = _data[i] + v.getValue(i);
		}	
		return ans;
	}

	@Override
	public Vector minus(final Vector v) {
		if(_data.length != ((DenseVector)v)._data.length) {
			throw new DimensionNotMatchException();
		}
		DenseVector ans = new DenseVector(((DenseVector)v)._data.length);
		for(int i = 0; i < ans.getData().length; ++i) {
			ans._data[i] = _data[i] - v.getValue(i);
		}	
		return ans;
	}

	@Override
	public Vector scale(double alpha) {
		DenseVector ans = new DenseVector(_data.length);
		for(int i = 0; i < ans.getData().length; ++i) {
			ans._data[i] = _data[i] * alpha;
		}	
		return ans;
	}
	
	//Assignments
	@Override
	public void plusAssign(final Vector v) {
		plusAssign(1.0, v);	
	}
	
	public void plusAssign(double alpha, final Vector v) {
		if(_data.length != ((DenseVector)v)._data.length) {
			throw new DimensionNotMatchException();
		}
		for(int i = 0; i < _data.length; ++i) {
			_data[i] += alpha * ((DenseVector)v)._data[i];
		}
	}
	
	public void minusAssign(final Vector v) {
		if(_data.length != ((DenseVector)v)._data.length) {
			throw new DimensionNotMatchException();
		}
		for(int i = 0; i < _data.length; ++i) {
			_data[i] -= ((DenseVector)v)._data[i];
		}
	}
	
	@Override
	public void minusAssign(double alpha, Vector v) {
		if(_data.length != ((DenseVector)v)._data.length) {
			throw new DimensionNotMatchException();
		}
		for(int i = 0; i < _data.length; ++i) {
			_data[i] -= alpha * ((DenseVector)v)._data[i];
		}
	}
	

	public void scaleAssign(double a) {
		for(int i = 0; i < _data.length; ++i) {
			_data[i] *= a;
		}
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		int sz = in.readInt();
		_data = new double[sz];
		for(int i = 0; i < sz; ++i) {
			_data[i] = in.readDouble();
		}
		
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(_data.length);
		for(int i = 0; i < _data.length; ++i) {
			out.writeDouble(_data[i]);
		}
		
	}

	@Override
	public boolean isDense() {
		return true;
	}

	@Override
	public boolean has_key(int i) {
		// TODO Auto-generated method stub
		return true;
	}

}
