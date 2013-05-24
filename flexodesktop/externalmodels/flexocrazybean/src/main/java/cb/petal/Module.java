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
 * Represents module object
 * 
 * @version $Id: Module.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Module extends QuidObject implements Named, StereoTyped {
	public Module(PetalNode parent, Collection params) {
		super(parent, "module", params);
	}

	public Module() {
		super("module");
	}

	@Override
	public void setNameParameter(String o) {
		params.set(0, o);
	}

	@Override
	public String getNameParameter() {
		return params.get(0);
	}

	public void setTypeParameter(String o) {
		params.set(1, o);
	}

	public String getTypeParameter() {
		return params.get(1);
	}

	public void setSpecificationParameter(String o) {
		params.set(2, o);
	}

	public String getSpecificationParameter() {
		return params.get(2);
	}

	public List getAttributes() {
		return (List) getProperty("attributes");
	}

	public void setAttributes(List o) {
		defineProperty("attributes", o);
	}

	@Override
	public String getStereotype() {
		return getPropertyAsString("stereotype");
	}

	@Override
	public void setStereotype(String o) {
		defineProperty("stereotype", o);
	}

	public List getVisibleModules() {
		return (List) getProperty("visible_modules");
	}

	public void setVisibleModules(List o) {
		defineProperty("visible_modules", o);
	}

	public String getPath() {
		return getPropertyAsString("path");
	}

	public void setPath(String o) {
		defineProperty("path", o);
	}

	public String getLanguage() {
		return getPropertyAsString("language");
	}

	public void setLanguage(String o) {
		defineProperty("language", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
