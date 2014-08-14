package com.autohome.adrd.algo.click_model.optimizer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import com.autohome.adrd.algo.click_model.data.Vector;

/*
 * Limited-Memory BFGS Method
 */
public class LBFGS<V extends Vector> implements Optimizer<V>  {
	
	private int M = 10;
	private int MAX_ITER_NUM = 100;
	private final double TOL = 1e-10;
	private int status = 1;
	
	public void setup(String configure_file) {
		//to do.
	}
	
	public void setM(int _M) {
		M = _M;
	}
	
	public int getM() {
		return M;
	}
	
	public void setMaxIterNum(int max_iter_num) {
		MAX_ITER_NUM = max_iter_num;
	}
	
	public int getMaxIterNum(int max_iter_num) {
		return MAX_ITER_NUM;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void minimize(DifferentiableFunction<V> f, V x0) {
		 double f_x0 = f.calcValue(x0);
		 V df_x0 = f.calcGradient(x0);
		 V xt = (V)x0.clone();
		 V df_xt = (V)df_x0.clone();
	     double f_xt = f_x0;
		
	     
		 LineSearch lineSearch = new WolfeLineSearch();
		 int iter_num = 0;
		 int m = 0;
		 V q = null;
		 V d = null;
		LinkedList<V> s = new LinkedList<V>();
		LinkedList<V> y = new LinkedList<V>();
		LinkedList<Double> rho = new LinkedList<Double>();
		 
		 while(iter_num < MAX_ITER_NUM && df_xt.norm_2() > TOL) {
			 iter_num++;
			 
			 //compute search direction
			 //d = - H0 * dfx0.
			 q = (V)df_x0.clone();
			 d = LBFGSLoop(q, s, y, rho);
			 d.scaleAssign(-1.0);
			 
			 f_xt = lineSearch.search(f, xt, d, f_xt, df_xt);
			 if(m >= M) {
				 s.pop();
				 y.pop();
				 rho.pop();
			 }
			 ++m;
			 s.add((V) xt.minus(x0));
			 y.add((V) df_xt.minus(df_x0));
			 rho.add(1.0 / y.getLast().dot(s.getLast()));
			 
			 x0 = (V)xt.clone();
			 df_x0 = (V)df_xt.clone();
			 f_x0 = f_xt;
		 }
		 
		 if(iter_num == MAX_ITER_NUM) 
			 status = -1;
		 else
			 status = 0;
	 }
	
	public V LBFGSLoop(V q, LinkedList<V> s, LinkedList<V> y, LinkedList<Double> rho) {
		if(s.isEmpty()) {
			return q;
		}
		else {
			Iterator<V> iter1 = s.descendingIterator();
			Iterator<V> iter2 = y.descendingIterator();
			Iterator<Double> iter3 = rho.descendingIterator();
			ListIterator<V> it1 = s.listIterator(0);
			ListIterator<V> it2 = y.listIterator(0);
			ListIterator<Double> it3 = rho.listIterator(0);
			
			LinkedList<Double> alpha = new LinkedList<Double>();
			
			while(iter1.hasNext()) {
				double tmp = iter3.next() *  q.dot(iter1.next()); 
				q.plusAssign(-tmp, iter2.next());
				alpha.addFirst(tmp);
			}
			
			
			double tmp = s.getLast().dot(y.getLast()) / y.getLast().dot(y.getLast());
			
			V r = (V) ((V)q.clone()).scale(tmp);
			
			while(it1.hasNext()) {
				double beta = it3.next() * r.dot(it2.next());
				r.plusAssign(alpha.pollFirst() - beta, it1.next());	
			}
			return r;
		}
	}
}

