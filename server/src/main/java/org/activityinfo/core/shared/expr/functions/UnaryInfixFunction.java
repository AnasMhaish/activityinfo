package org.activityinfo.core.shared.expr.functions;
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

import org.activityinfo.core.shared.expr.ExprFunction;
import org.activityinfo.core.shared.expr.ExprNode;

import java.util.List;

/**
 * @author yuriyz on 6/4/14.
 */
public abstract class UnaryInfixFunction extends ExprFunction {

    private String symbol;

    public UnaryInfixFunction(String symbol) {
        super();
        this.symbol = symbol;
    }

    @Override
    public final String getName() {
        return symbol;
    }

    @Override
    public double applyReal(List<ExprNode> arguments) {
        return applyReal(arguments.get(0).evalReal());
    }

    public abstract double applyReal(double x);
}