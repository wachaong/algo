package com.autohome.adrd.algo.click_model.optimizer;

import com.autohome.adrd.algo.click_model.data.SparseVector;

/**
 * Calculate the search Direction
 * @author Yang Mingmin
 *
 */
public  interface SearchDirectionGenerator {
	public SparseVector calcSearchDirction();
}
