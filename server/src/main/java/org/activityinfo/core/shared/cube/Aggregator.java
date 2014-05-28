package org.activityinfo.core.shared.cube;

/**
 * Created by alex on 5/28/14.
 */
public interface Aggregator {
    void value(double value);
    double compute();
}
