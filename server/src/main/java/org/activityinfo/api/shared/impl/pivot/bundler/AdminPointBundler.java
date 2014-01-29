package org.activityinfo.api.shared.impl.pivot.bundler;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import org.activityinfo.api2.shared.model.AiLatLng;
import org.activityinfo.api.shared.command.result.Bucket;

/**
 * Bundless a point for the bucket, based on the Admin Entity's
 * MBR.
 */
public class AdminPointBundler implements Bundler {

    @Override
    public void bundle(SqlResultSetRow row, Bucket bucket) {
        if (!row.isNull("AX") && !row.isNull("AY")) {
            bucket.setPoint(new AiLatLng(row.getDouble("AY"), row.getDouble("AX")));
        }
    }
}
