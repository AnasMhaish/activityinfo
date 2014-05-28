package org.activityinfo.core.shared.cube;

/**
 * Created by alex on 5/28/14.
 */
public class SumAggregator implements Aggregator {

    double sum = 0;

    @Override
    public void value(double value) {
        if(!Double.isNaN(value)) {
            sum += value;
        }
    }

    @Override
    public double compute() {
        return sum;
    }
}
