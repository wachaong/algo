package com.autohome.adrd.algo.click_model.optimizer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.utility.Context;

public class LbfgsSearchDirection {
	private static Context context = new Context();
	LinkedList<SparseVector> s = new LinkedList<SparseVector>();
	LinkedList<SparseVector> y = new LinkedList<SparseVector>();
	LinkedList<Double> rho = new LinkedList<Double>();
	//DifferentiableFunction<SparseVector> objectFun = null;  
	//SparseVector x0 = null;
	SparseVector df_x0 = null;
	//SparseVector direction = null;

	public LbfgsSearchDirection() {
		//context.setDouble("c1", 0.1);
		//context.setDouble("c2",	0.9);
		context.setInteger("M", 10);
	}

	public void setGradient(SparseVector _df_x0) {
		df_x0 = _df_x0;
	}

	public SparseVector calcSearchDirection() {
		SparseVector direction = null;
		if(s.isEmpty())
		{
			direction = (SparseVector)df_x0.scale(-1);
			return direction;
		}
		else{
			if(s.size() > context.getInteger("M", 10)) {
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

			SparseVector q = (SparseVector)df_x0.clone();
			while(iter1.hasNext()) {
				double tmp = iter3.next() *  q.dot(iter1.next()); 
				q.plusAssign(-tmp, iter2.next());
				alpha.addFirst(tmp);
			}


			double tmp = s.getLast().dot(y.getLast()) / y.getLast().dot(y.getLast());
			q.scaleAssign(tmp);

			//V r = (V) ((V)q.clone()).scale(tmp);

			while(it1.hasNext()) {
				double beta = it3.next() * q.dot(it2.next());
				q.plusAssign(alpha.pollFirst() - beta, it1.next());	
			}
			q.scaleAssign(-1);
			direction = q;
			return direction;
		}
	}

	public void update(SparseVector x0, SparseVector xt, 
			double f_x0, double f_xt,
			SparseVector _df_x0, SparseVector df_xt) {
		s.add((SparseVector) xt.minus(x0));
		y.add((SparseVector) df_xt.minus(_df_x0));
		rho.add(1.0 / y.getLast().dot(s.getLast()));
		//df_x0 = df_xt;
	}





}
