package com.autohome.adrd.algo.click_model.optimizer;


//V是向量的类型
public interface IGradientFunction<V> {
	public V eval(V x0);
}
