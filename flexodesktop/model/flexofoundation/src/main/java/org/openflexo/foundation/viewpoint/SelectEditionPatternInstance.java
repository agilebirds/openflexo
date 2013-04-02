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
package org.openflexo.foundation.viewpoint;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

/**
 * Generic {@link FetchRequest} allowing to retrieve a selection of some {@link EditionPatternInstance} matching some conditions and a given
 * {@link EditionPattern}.<br>
 * 
 * @author sylvain
 * 
 * @param <M>
 * @param <MM>
 * @param <T>
 */
public class SelectEditionPatternInstance<VMI extends VirtualModelInstance<VMI, VM>, VM extends VirtualModel<VM>> extends
		FetchRequest<VMI, VM, EditionPatternInstance> {

	protected static final Logger logger = FlexoLogger.getLogger(SelectEditionPatternInstance.class.getPackage().getName());

	private EditionPattern editionPatternType;
	private String editionPatternTypeURI;

	public SelectEditionPatternInstance(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.SelectEditionPatternInstance;
	}

	@Override
	public EditionPatternInstanceType getFetchedType() {
		return EditionPatternInstanceType.getEditionPatternInstanceType(getEditionPatternType());
	}

	public String _getEditionPatternTypeURI() {
		if (editionPatternType != null) {
			return editionPatternType.getURI();
		}
		return editionPatternTypeURI;
	}

	public void _setEditionPatternTypeURI(String editionPatternURI) {
		this.editionPatternTypeURI = editionPatternURI;
	}

	public EditionPattern getEditionPatternType() {
		if (editionPatternType == null && editionPatternTypeURI != null && getVirtualModel() != null) {
			editionPatternType = getVirtualModel().getEditionPattern(editionPatternTypeURI);
			for (EditionScheme s : getEditionPattern().getEditionSchemes()) {
				s.updateBindingModels();
			}
		}
		return editionPatternType;
	}

	public void setEditionPatternType(EditionPattern editionPatternType) {
		if (editionPatternType != this.editionPatternType) {
			this.editionPatternType = editionPatternType;
			for (EditionScheme s : getEditionPattern().getEditionSchemes()) {
				s.updateBindingModels();
			}
		}
	}

	@Override
	public String getStringRepresentation() {
		return getClass().getSimpleName() + (getEditionPatternType() != null ? " : " + getEditionPatternType().getName() : "")
				+ (StringUtils.isNotEmpty(getAssignation().toString()) ? " (" + getAssignation().toString() + ")" : "");
	}

	@Override
	public List<EditionPatternInstance> performAction(EditionSchemeAction action) {
		VirtualModelInstance<VMI, VM> vmi = action.getVirtualModelInstance();
		System.out.println("Returning " + vmi.getEPInstances(getEditionPatternType()));
		return filterWithConditions(vmi.getEPInstances(getEditionPatternType()), action);
	}
}
