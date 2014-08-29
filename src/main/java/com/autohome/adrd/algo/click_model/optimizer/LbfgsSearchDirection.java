package com.autohome.adrd.algo.click_model.optimizer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import com.autohome.adrd.algo.click_model.data.SparseVector;


public class LbfgsSearchDirection implements ISearchDirection {
	
	private int M = 10;
	private int iter_num = 0;
	private LinkedList<SparseVector> s = new LinkedList<SparseVector>();
	private LinkedList<SparseVector> y = new LinkedList<SparseVector>();
	private LinkedList<Double> rho = new LinkedList<Double>();
	//private SparseVector df_x0 = null;

	public LbfgsSearchDirection() {
		s = new LinkedList<SparseVector>();
		y = new LinkedList<SparseVector>();
	}
	
	public LbfgsSearchDirection(int m) {
		M = m;
	}

	public int getM() {
		return M;
	}

	public void setM(int m) {
		M = m;
	}


	public SparseVector calcSearchDirction(SparseVector grad) {
		SparseVector direction = null;
		
		//if(s.isEmpty())
		if(iter_num < 3)
			return (SparseVector)grad.scale(-1);
		else {
			
			if(s.size() > M) {
				s.pop();
				y.pop();
				rho.pop();
			}

			Iterator<SparseVector> iter1 = s.descendingIterator();
			Iterator<SparseVector> iter2 = y.descendingIterator();
			Iterator<Double> iter3 = rho.descendingIterator();
			ListIterator<SparseVector> it1 = s.listIterator(0);
			ListIterator<SparseVector> it2 = y.listIterator(0);
			ListIterator<Double> it3 = rho.listIterator(0);

			LinkedList<Double> alpha = new LinkedList<Double>();

			SparseVector q = (SparseVector)grad.clone();
			while(iter1.hasNext()) {
				double tmp = iter3.next() *  q.dot(iter1.next()); 
				q.plusAssign(-tmp, iter2.next());
				alpha.addFirst(tmp);
			}

			double norm = y.getLast().dot(y.getLast());
			if(norm < 1e-7)
				norm = 1e-7;
			double tmp = s.getLast().dot(y.getLast()) / norm;
			q.scaleAssign(tmp);
			//q.scaleAssign(1e-3);

			//V r = (V) ((V)q.clone()).scale(tmp);

			while(it1.hasNext()) {
				double beta = it3.next() * q.dot(it2.next());
				q.plusAssign(alpha.pollFirst() - beta, it1.next());	
			}
			q.scaleAssign(-1.0);
			return q;
		}
	}

	public void update(SparseVector x0, SparseVector xt, 
			double f_x0, double f_xt,
			SparseVector df_x0, SparseVector df_xt) {
		
		iter_num++;

		s.add((SparseVector) xt.minus(x0));
		y.add((SparseVector) df_xt.minus(df_x0));
		double tmp = y.getLast().dot(s.getLast());
		if(Math.abs(tmp) < 1e-7)
		{
			if(tmp > 0)
				tmp = 1e-7;
			else
				tmp = -1e-7;
		}
		rho.add(1.0 / tmp);
	}

}
