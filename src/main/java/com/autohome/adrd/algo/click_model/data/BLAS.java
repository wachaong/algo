package com.autohome.adrd.algo.click_model.data;

import java.util.HashMap;
import java.util.Map;

public class BLAS {
	public static double dot(final double[] v1, final double[] v2) {
		if(v1.length != v2.length) {
			throw new DimensionNotMatchException();
		}
		double ans = 0;
		for(int i = 0; i < v1.length; ++i) {
			ans += v1[i] * v2[i];
		}
		return ans;
	}
	
	public static double dot(final DenseVector v1, final DenseVector v2) {
		return dot(v1.getData(), v2.getData());
	}
	
	public static double dot(final SparseVector v1, SparseVector v2) {
		return v1.dot(v2);
	}
	
	public static double[] plus(final double[] v1, final double[] v2) {
		if(v1.length != v2.length) {
			throw new DimensionNotMatchException();
		}
		double[] ans = new double[v1.length];
		for(int i = 0; i < v1.length; ++i) {
			ans[i] = v1[i] + v2[i];
		}
		return ans;
	}
	
	public static DenseVector plus(final DenseVector v1, final DenseVector v2) {
		DenseVector ans = new DenseVector();
		ans.setData(plus(v1.getData(), v2.getData()));
		return ans;
	}
	
	public static SparseVector plus(final SparseVector v1, final SparseVector v2) {
		SparseVector ans = new SparseVector(v1);
		ans.plusAssign(1.0, v2);
		return ans;
	}
	
	public static double[] minus(final double[] v1, final double[] v2) {
		if(v1.length != v2.length) {
			throw new DimensionNotMatchException();
		}
		double[] ans = new double[v1.length];
		for(int i = 0; i < v1.length; ++i) {
			ans[i] = v1[i] - v2[i];
		}
		return ans;
	}
	
	public static DenseVector minus(final DenseVector v1, final DenseVector v2) {
		DenseVector ans = new DenseVector();
		ans.setData(minus(v1.getData(), v2.getData()));
		return ans;
	}
	
	public static SparseVector minus(final SparseVector v1, final SparseVector v2) {
		SparseVector ans = new SparseVector(v1);
		ans.minusAssign(v2);
		return ans;
	}
	
	//scale
	public static double[] scale(final double[] v, double alpha) {
		double[] ans = new double[v.length];
		for(int i = 0; i < v.length; ++i) {
			ans[i] *= alpha;
		}
		return ans;
	}
	
	public static DenseVector scale(final DenseVector v, double alpha) {
		DenseVector ans = new DenseVector();
		ans.setData(scale(v.getData(), alpha));
		return ans;
	}
	
	
	public static HashMap<Integer, Double> scale(HashMap<Integer, Double> v, double alpha) {
		HashMap<Integer, Double> ans = new HashMap<Integer, Double>();
		for(Map.Entry<Integer, Double> elem : v.entrySet()) {
			ans.put(elem.getKey(), elem.getValue() * alpha);
		}
		return ans;
	}
	
	public static SparseVector scale(SparseVector v, double alpha) {
		SparseVector ans = new SparseVector();
		ans.setData(scale(v.getData(), alpha));
		return v;
	}
	
}
