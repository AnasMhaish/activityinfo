package org.activityinfo.core.shared.cube;

import org.activityinfo.legacy.shared.reports.content.DimensionCategory;

import java.util.Arrays;

/**
 * Created by alex on 5/28/14.
 */
public final class BucketKey {
    private Object[] dimensionCategories;

    public BucketKey(Object[] dimensionCategories) {
        this.dimensionCategories = Arrays.copyOf(dimensionCategories, dimensionCategories.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BucketKey bucketKey = (BucketKey) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(dimensionCategories, bucketKey.dimensionCategories)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(dimensionCategories);
    }

    public Object getCategory(int j) {
        return dimensionCategories[j];
    }
}
