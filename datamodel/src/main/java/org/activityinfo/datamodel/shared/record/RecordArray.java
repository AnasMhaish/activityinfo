package org.activityinfo.datamodel.shared.record;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An interface to an array of {@link org.activityinfo.datamodel.shared.record.Record}s
 *
 * Note that this interface does not extend any of the java.util.Collection
 * interfaces in order to support compilation to JavaScript/JSON on the client.
 */
public interface RecordArray<T extends Record> {

    int size();

    /**
     *
     * @return the Record at the given {@code index}
     */
    @Nonnull
    T get(int index);

    /**
     * Adds the given {@code record} to the end of this
     * {@code RecordArray}
     */
    void add(@Nonnull T record);

    /**
     *
     * @return an unmodifiable List interface to this array
     */
    List<T> asList();

}
