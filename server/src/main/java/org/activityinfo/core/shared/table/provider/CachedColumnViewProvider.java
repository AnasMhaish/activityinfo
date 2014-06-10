package org.activityinfo.core.shared.table.provider;
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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.datamodel.shared.table.view.ColumnView;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.datamodel.shared.table.FieldColumn;

import java.util.Set;

/**
 * @author yuriyz on 5/29/14.
 */
public class CachedColumnViewProvider implements ColumnViewProvider {

    public static final int MAX_CACHE_SIZE = 10000;

    private Cache<CacheKey, CacheValue> cache = CacheBuilder.newBuilder()
            .maximumSize(MAX_CACHE_SIZE).build();

    private final ResourceLocator resourceLocator;

    CachedColumnViewProvider(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }

    @Override
    public Promise<? extends ColumnView> view(FieldColumn column, final FormClass formClass) {
        final CacheValue ifPresent = cache.getIfPresent(new CacheKey(formClass.getId(), column.getFieldPaths().get(0)));
        if (ifPresent != null) {

            // validate cache id
            final int cacheId = ifPresent.getCacheId();
            if (formClass.getCacheId() == cacheId) {
                return Promise.resolved(ifPresent.getColumnView()); // luckily cache id is not changed
            }
            CachedColumnViewProvider.this.invalidateFormClassCache(formClass.getId()); // invalidate cache if cache id was changed
            return null;
        }
        return Promise.resolved(null);
    }

    public void put(FieldPath columnPath, Cuid formClassId, ColumnView columnView, int cacheId) {
        cache.put(new CacheKey(formClassId, columnPath), new CacheValue(columnView, cacheId));
    }

    public void invalidateFormClassCache(Cuid formClassId) {
        Set<CacheKey> toInvalidate = Sets.newHashSet();
        for (CacheKey key : cache.asMap().keySet()) {
            if (key.getFormClassId().equals(formClassId)) {
                toInvalidate.add(key);
            }
        }
        cache.invalidateAll(toInvalidate);
    }
}
