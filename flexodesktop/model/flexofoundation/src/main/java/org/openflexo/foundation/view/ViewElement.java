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
package org.openflexo.foundation.view;

import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.xml.VEShemaBuilder;
import org.openflexo.localization.FlexoLocalization;

public abstract class ViewElement extends ViewObject implements Bindable {

	private static final Logger logger = Logger.getLogger(ViewElement.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization
	 * 
	 * @param componentDefinition
	 */
	public ViewElement(VEShemaBuilder builder) {
		this(builder.shema);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor for OEShema
	 * 
	 * @param shemaDefinition
	 */
	public ViewElement(View shema) {
		super(shema);
	}

	@Override
	public void delete() {
		if (getEditionPatternInstance() != null) {
			getEditionPatternInstance().delete();
		}
		super.delete();
	}

	@Override
	public String getName() {
		if (isBoundInsideEditionPattern()) {
			return getLabelValue();
		}
		return super.getName();
	}

	@Override
	public void setName(String name) {
		// logger.info("setName of OEShemaElement with "+name);
		if (isBoundInsideEditionPattern()) {
			setLabelValue(name);
		} else {
			try {
				super.setName(name);
			} catch (DuplicateResourceException e) {
				// cannot happen
				e.printStackTrace();
			} catch (InvalidNameException e) {
				// cannot happen
				e.printStackTrace();
			}
		}
	}

	// public abstract AddShemaElementAction getEditionAction();

	/**
	 * Return a flag indicating if current graphical element is bound inside an edition pattern, ie if there is one edition pattern instance
	 * where this element plays a role as primary representation, or is included in an element declared as playing a role as primary
	 * representation
	 * 
	 * @return
	 */
	public boolean isBoundInsideEditionPattern() {
		return (getPatternRole() != null);
	}

	protected String getLabelValue() {
		if (getPatternRole() != null) {
			return (String) getPatternRole().getLabel().getBindingValue(getEditionPatternInstance());
		}
		return null;
	}

	protected void setLabelValue(String aValue) {
		if (getPatternRole() != null && !getPatternRole().getReadOnlyLabel()) {
			getPatternRole().getLabel().setBindingValue(aValue, getEditionPatternInstance());
		}
	}

	public EditionPattern getEditionPattern() {
		if (getEditionPatternInstance() != null) {
			return getEditionPatternInstance().getPattern();
		}
		return null;
	}

	public GraphicalElementPatternRole getPatternRole() {
		EditionPatternReference ref = getEditionPatternReference();
		if ((ref != null) && (ref.getPatternRole() instanceof GraphicalElementPatternRole)) {
			return (GraphicalElementPatternRole) ref.getPatternRole();
		}
		return null;
	}

	/**
	 * Return EditionPatternReference for that object<br>
	 * 
	 * If many EditionPatternReferences are defined for this object, return preferabely an EditionPatternReference where this object plays a
	 * primary role
	 * 
	 * @return
	 */
	public EditionPatternReference getEditionPatternReference() {
		// Default behaviour is to have only one EditionPattern where
		// this graphical element plays a representation primitive role
		// When many, big pbs may happen !!!!

		if (getEditionPatternReferences() != null) {
			if (getEditionPatternReferences().size() > 0) {
				EditionPatternReference returned = null;
				for (EditionPatternReference r : getEditionPatternReferences()) {
					if (r.getPatternRole() instanceof GraphicalElementPatternRole) {
						GraphicalElementPatternRole grPatternRole = (GraphicalElementPatternRole) r.getPatternRole();
						if (grPatternRole.getIsPrimaryRepresentationRole()) {
							if (returned != null) {
								logger.warning("More than one edition pattern reference where element plays a primary role 1 !!!!");
								for (EditionPatternReference r2 : getEditionPatternReferences()) {
									logger.warning("> " + r2.getEditionPatternInstance().debug());
								}
							}
							returned = r;
						} else if (grPatternRole.isIncludedInPrimaryRepresentationRole()) {
							if (returned != null) {
								logger.warning("More than one edition pattern reference where element plays a primary role 2 !!!!");
								for (EditionPatternReference r2 : getEditionPatternReferences()) {
									logger.warning("> " + r2.getEditionPatternInstance().debug());
								}
							}
							returned = r;
						}
					}
				}
				if (returned != null) {
					return returned;
				}
			}
		}

		if (getEditionPatternReferences().size() > 0) {
			return getEditionPatternReferences().get(0);
		}

		return null;
	}

	/**
	 * Return EditionPatternInstance for that object<br>
	 * 
	 * If many EditionPatternInstance are defined for this object, return preferabely an EditionPatternReference where this object plays a
	 * primary role
	 * 
	 * @return
	 */
	public EditionPatternInstance getEditionPatternInstance() {
		if (getEditionPatternReference() != null) {
			return getEditionPatternReference().getEditionPatternInstance();
		}
		if (getEditionPatternReferences().size() > 0) {
			return getEditionPatternReferences().get(0).getEditionPatternInstance();
		}
		return null;
	}

	@Override
	public String getInspectorTitle() {
		for (EditionPatternReference ref : getEditionPatternReferences()) {
			return FlexoLocalization.localizedForKey(ref.getEditionPattern().getName());
		}
		// Otherwise, take default inspector name
		return super.getInspectorTitle();
	}

	@Override
	public BindingFactory getBindingFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BindingModel getBindingModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
