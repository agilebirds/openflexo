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

import java.util.Arrays;
import java.util.Iterator;

/**
 * Class category is used for structuring into submodels and -views, it may contain further class categories.
 * 
 * @version $Id: ClassCategory.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class ClassCategory extends QuidObject implements AccessQualified, Named, Documented {
	static final long serialVersionUID = -8273790375346338894L;

	protected ClassCategory(PetalNode parent, String name) {
		super(parent, "Class_Category", Arrays.asList(new String[] { name }));
	}

	public ClassCategory() {
		this(null, "Class Category");
	}

	@Override
	public void setNameParameter(String o) {
		params.set(0, o);
	}

	@Override
	public String getNameParameter() {
		return params.get(0);
	}

	@Override
	public String getDocumentation() {
		return getPropertyAsString("documentation");
	}

	@Override
	public void setDocumentation(String o) {
		defineProperty("documentation", o);
	}

	@Override
	public String getExportControl() {
		return getPropertyAsString("exportControl");
	}

	@Override
	public void setExportControl(String o) {
		defineProperty("exportControl", o);
	}

	public boolean getGlobal() {
		return getPropertyAsBoolean("global");
	}

	public void setGlobal(boolean o) {
		defineProperty("global", o);
	}

	/**
	 * The returned values depend on what kind of class category this is. In the logical view this returns class, association and mechanism
	 * objects. It may of course also contain further ClassCategory objects.
	 */
	public List getLogicalModels() {
		return (List) getProperty("logical_models");
	}

	public void setLogicalModels(List o) {
		defineProperty("logical_models", o);
	}

	/**
	 * This returns a list of diagrams, ClassDiagram objects, e.g.
	 */
	public List getLogicalPresentations() {
		return (List) getProperty("logical_presentations");
	}

	public void setLogicalPresentations(List o) {
		defineProperty("logical_presentations", o);
	}

	/**
	 * Find diagram by given class
	 */
	protected java.lang.Object lookupDiagram(java.lang.Class clazz) {
		List list = getLogicalPresentations();

		if (list == null || list.size() == 0) {
			return null;
		} else {
			for (Iterator i = list.getElements().iterator(); i.hasNext();) {
				java.lang.Object o = i.next();

				if (o.getClass() == clazz) {
					return o;
				}
			}
		}

		return null;
	}

	protected void add(PetalObject obj) {
		getLogicalModels().add(obj);
		obj.setParent(this);
		obj.init();
	}

	protected void remove(PetalObject obj) {
		getLogicalModels().remove(obj);
		obj.setParent(null);
	}

	public void addToModel(Association assoc) {
		add(assoc);
	}

	public void removeFromModel(Association assoc) {
		remove(assoc);
	}

	/**
	 * Add a class to the model. Sets parent and calls init() on class.
	 */
	public void addToModel(Class clazz) {
		add(clazz);
	}

	public void removeFromModel(Class clazz) {
		remove(clazz);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
