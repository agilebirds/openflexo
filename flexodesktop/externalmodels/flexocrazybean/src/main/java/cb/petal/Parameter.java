/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package cb.petal;

import java.util.Collection;

/**
 * Represents Parameter object for operations (methods).
 * 
 * @version $Id: Parameter.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Parameter extends PetalObject implements Named {
	public Parameter(PetalNode parent, Collection params) {
		super(parent, "Parameter", params);
	}

	public Parameter() {
		super("Parameter");
	}

	@Override
	public void setNameParameter(String o) {
		params.set(0, o);
	}

	@Override
	public String getNameParameter() {
		return (String) params.get(0);
	}

	public String getType() {
		return getPropertyAsString("type");
	}

	public void setType(String o) {
		defineProperty("type", o);
	}

	public String getInitialValue() {
		return getPropertyAsString("initv");
	}

	public void setInitialValue(String o) {
		defineProperty("initv", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
