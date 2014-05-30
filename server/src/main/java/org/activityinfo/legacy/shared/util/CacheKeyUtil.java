package org.activityinfo.legacy.shared.util;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.query.SqlQuery;
import org.activityinfo.legacy.shared.impl.Tables;

/**
 * @author yuriyz on 5/30/14.
 */
public class CacheKeyUtil {

    private CacheKeyUtil() {
    }

    public static void increaseActivityCacheKeyBySiteId(SqlTransaction tx, int siteId) {
        SqlQuery.select("activityId")
                .from(Tables.SITE)
                .where("siteId")
                .equalTo(siteId)
                .execute(tx, new SqlResultCallback() {
                    @Override
                    public void onSuccess(SqlTransaction tx, SqlResultSet sqlResultSet) {
                        increaseActivityCacheKey(tx, sqlResultSet.getRow(0).getInt("activityId"));
                    }
                });
    }

    public static void increaseActivityCacheKey(SqlTransaction tx, int activityId) {
        tx.executeSql("UPDATE activity SET cacheKey = cacheKey + 1 WHERE ActivityId = " + activityId);
    }
}
