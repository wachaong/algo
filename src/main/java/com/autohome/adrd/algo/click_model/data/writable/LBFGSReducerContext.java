package com.autohome.adrd.algo.click_model.data.writable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.codehaus.jackson.annotate.JsonProperty;

import com.autohome.adrd.algo.click_model.data.SparseVector;
import com.autohome.adrd.algo.click_model.io.IterationHelper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
/**
 * Hadoop Task Launcher
 * author : wang chao
 */
public class LBFGSReducerContext implements Writable {

    @JsonProperty("xInitial")
    private SparseVector weight;

    @JsonProperty("loss")
    private double loss;
    
    @JsonProperty("lambdaValue")
    private double lambdaValue;

    public LBFGSReducerContext(SparseVector weight, double loss, double lambdaValue) {
        this.weight = weight;
        this.loss = loss;
        this.lambdaValue = lambdaValue;
    }

    public LBFGSReducerContext() {
    }

    public void setLBFGSReducerContext(LBFGSReducerContext context) {
        this.weight = context.weight;
        this.loss = context.loss;
        this.lambdaValue = context.lambdaValue;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        Text contextJson = new Text(IterationHelper.lbfgsReducerContextToJson(this));
        contextJson.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        Text contextJson = new Text();
        contextJson.readFields(in);
        setLBFGSReducerContext(IterationHelper.jsonToLbfgsReducerContext(contextJson.toString()));
    }

    @JsonProperty("uInitial")
    public SparseVector getWeight() {
        return weight;
    }

    @JsonProperty("loss")
    public double getLoss() {
        return loss;
    }

    @JsonProperty("lambdaValue")
    public double getLambdaValue() {
        return lambdaValue;
    }
}
