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
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;
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
public class SelectEditionPatternInstance extends FetchRequest<EditionPatternInstance> {

	protected static final Logger logger = FlexoLogger.getLogger(SelectEditionPatternInstance.class.getPackage().getName());

	private EditionPattern editionPatternType;
	private String editionPatternTypeURI;

	public SelectEditionPatternInstance(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		if (getAssignation().isSet()) {
			out.append(getAssignation().toString() + " = (", context);
		}
		out.append(getClass().getSimpleName() + (getModelSlot() != null ? " from " + getModelSlot().getName() : " ") + " as "
				+ getEditionPatternType().getName() + (getConditions().size() > 0 ? " " + getWhereClausesFMLRepresentation(context) : ""),
				context);
		if (getAssignation().isSet()) {
			out.append(")", context);
		}
		return out.toString();
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

	// private boolean isUpdatingBindingModels = false;

	public EditionPattern getEditionPatternType() {
		// System.out.println("getEditionPatternType() for " + editionPatternTypeURI);
		// System.out.println("vm=" + getVirtualModel());
		// System.out.println("ep=" + getEditionPattern());
		// System.out.println("ms=" + getModelSlot());
		// if (getModelSlot() instanceof VirtualModelModelSlot) {
		// System.out.println("ms.vm=" + ((VirtualModelModelSlot) getModelSlot()).getAddressedVirtualModel());
		// }
		if (editionPatternType == null && editionPatternTypeURI != null && getVirtualModel() != null) {
			editionPatternType = getVirtualModel().getEditionPattern(editionPatternTypeURI);
			/*if (!isUpdatingBindingModels) {
				isUpdatingBindingModels = true;
				for (EditionScheme s : getEditionPattern().getEditionSchemes()) {
					s.updateBindingModels();
				}
				isUpdatingBindingModels = false;
			}*/
		}
		if (editionPatternType == null && editionPatternTypeURI != null && getEditionPattern() instanceof VirtualModel) {
			editionPatternType = ((VirtualModel) getEditionPattern()).getEditionPattern(editionPatternTypeURI);
		}
		if (editionPatternType == null && editionPatternTypeURI != null && getModelSlot() instanceof VirtualModelModelSlot) {
			if (((VirtualModelModelSlot) getModelSlot()).getAddressedVirtualModel() != null) {
				editionPatternType = ((VirtualModelModelSlot) getModelSlot()).getAddressedVirtualModel().getEditionPattern(
						editionPatternTypeURI);
			}
		}
		// System.out.println("return " + editionPatternType);
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
		VirtualModelInstance vmi = null;
		if (getModelSlot() instanceof VirtualModelModelSlot) {
			ModelSlotInstance modelSlotInstance = action.getVirtualModelInstance().getModelSlotInstance(getModelSlot());
			if (modelSlotInstance != null) {
				// System.out.println("modelSlotInstance=" + modelSlotInstance + " model=" + modelSlotInstance.getModel());
				vmi = (VirtualModelInstance) modelSlotInstance.getResourceData();
			} else {
				logger.warning("Cannot find ModelSlotInstance for " + getModelSlot());
			}
		} else {
			vmi = action.getVirtualModelInstance();
		}
		if (vmi != null) {
			System.out.println("Returning " + vmi.getEPInstances(getEditionPatternType()));
			return filterWithConditions(vmi.getEPInstances(getEditionPatternType()), action);
		} else {
			logger.warning(getStringRepresentation()
					+ " : Cannot find virtual model instance on which to apply SelectEditionPatternInstance");
			// logger.warning("Additional info: getModelSlot()=" + getModelSlot());
			// logger.warning("Additional info: action.getVirtualModelInstance()=" + action.getVirtualModelInstance());
			// logger.warning("Additional info: action.getVirtualModelInstance().getModelSlotInstance(getModelSlot())="
			// + action.getVirtualModelInstance().getModelSlotInstance(getModelSlot()));
			return null;
		}
	}
}
