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

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.QueryResult;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.expr.*;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.table.ArrayColumnView;
import org.activityinfo.core.shared.table.ColumnView;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.ui.client.component.table.FieldColumn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yuriyz on 5/29/14.
 */
class CalculatedColumnViewProvider implements ColumnViewProvider {

    // short term form tree cache for "quicker" evaluation (avoid tree rebuilding), cache is valid only within provider instance
    private final Cache<Cuid, FormTree> treeCache = CacheBuilder.newBuilder()
            .maximumSize(100).expireAfterWrite(5, TimeUnit.SECONDS).build();

    private final ResourceLocator resourceLocator;
    private final ColumnViewProvider columnViewProvider;

    public CalculatedColumnViewProvider(ResourceLocator resourceLocator, ColumnViewProvider columnViewProvider) {
        this.resourceLocator = resourceLocator;
        this.columnViewProvider = columnViewProvider;
    }

    private Promise<FormTree> getFormTree(final Cuid formClass) {
        FormTree ifPresent = treeCache.getIfPresent(formClass);
        if (ifPresent != null) {
            return Promise.resolved(ifPresent);
        } else {
            AsyncFormTreeBuilder formTreeBuilder = new AsyncFormTreeBuilder(resourceLocator);
            Promise<FormTree> promise = formTreeBuilder.apply(formClass);
            promise.then(new AsyncCallback<FormTree>() {
                @Override
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                }

                @Override
                public void onSuccess(FormTree result) {
                    treeCache.put(formClass, result);
                }
            });
            return promise;
        }
    }

    @Override
    public Promise<? extends ColumnView> view(final FieldColumn column, final FormClass formClass) {
        final Promise<FormTree> formTreePromise = getFormTree(formClass.getId());
        final Promise<QueryResult<Projection>> queryResultPromise = resourceLocator.queryProjection(new InstanceQuery(column.getFieldPaths(),
                new ClassCriteria(formClass.getId())));
        return Promise.waitAll(formTreePromise, queryResultPromise).join(new Function<Void, Promise<ColumnView>>() {
            @Nullable
            @Override
            public Promise<ColumnView> apply(@Nullable Void input) {
                final String expression = column.getNode().getField().getCalculation();
                final List<Promise<ColumnView>> involvedColumnViews = collectInvolvedColumnViews(formTreePromise.get(), expression, formClass);
                return Promise.waitAll(involvedColumnViews).then(new Function<Void, ColumnView>() {
                    @Nullable
                    @Override
                    public ColumnView apply(@Nullable Void input) {
                        QueryResult<Projection> queryResult = queryResultPromise.get();
                        Object[] columnArray = new Object[queryResult.getTotalCount()];
                        for (int i = 0; i != queryResult.getTotalCount(); ++i) {
                            final int row = i;
                            ExprParser parser = new ExprParser(new ExprLexer(expression), new PlaceholderExprResolver() {
                                @Override
                                public void resolve(PlaceholderExpr placeholderExpr) {
                                    FormTree formTree = formTreePromise.get();
                                    Placeholder placeholder = placeholderExpr.getPlaceholderObj();
                                    if (placeholder.isRowLevel()) {
                                        FormTree.Node rootField = formTree.getRootField(placeholder.getFieldId());
                                        ColumnView view = columnViewProvider.view(new FieldColumn(rootField), formClass).get();
                                        placeholderExpr.setValue(view.getDouble(row));
                                        return;
                                    }
                                    throw new UnsupportedOperationException("Placeholder is not supported: " + placeholder);
                                }
                            });
                            ExprNode exprNode = parser.parse();
                            columnArray[i] = exprNode.evalReal();
                        }
                        ArrayColumnView columnView = new ArrayColumnView(columnArray);
                        columnView.setId(column.getFieldPaths().get(0));
                        columnView.setFormClassCacheId(formClass.getCacheId());
                        return columnView;
                    }
                });
            }
        });
    }

    private List<Promise<ColumnView>> collectInvolvedColumnViews(final FormTree formTree, String expression, final FormClass formClass) {
        final List<Promise<ColumnView>> columnViews = Lists.newArrayList();
        ExprParser parser = new ExprParser(new ExprLexer(expression), new PlaceholderExprResolver() {
            @Override
            public void resolve(PlaceholderExpr placeholderExpr) {
                Placeholder placeholder = placeholderExpr.getPlaceholderObj();
                if (placeholder.isRowLevel()) {
                    FormTree.Node rootField = formTree.getRootField(placeholder.getFieldId());
                    Promise<ColumnView> view = (Promise<ColumnView>) columnViewProvider.view(new FieldColumn(rootField), formClass);
                    columnViews.add(view);
                } else {
                    // parse complex placeholders
                }

                throw new UnsupportedOperationException("Placeholder is not supported: " + placeholder);
            }
        });
        try {
            parser.parse().evalReal();
        } catch (Exception e) {
            // ignore -> our goal is to collect involved column views
        }
        return columnViews;
    }
}
