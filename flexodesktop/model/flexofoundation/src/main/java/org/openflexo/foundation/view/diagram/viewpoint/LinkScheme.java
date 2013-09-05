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
package org.openflexo.foundation.view.diagram.viewpoint;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddConnector;
import org.openflexo.foundation.viewpoint.AbstractCreationScheme;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternInstanceType;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.toolbox.StringUtils;

public class LinkScheme extends AbstractCreationScheme implements DiagramEditionScheme {

	private String fromTarget;
	private String toTarget;

	private boolean northDirectionSupported = true;
	private boolean eastDirectionSupported = true;
	private boolean southDirectionSupported = true;
	private boolean westDirectionSupported = true;

	private boolean isAvailableWithFloatingPalette = true;

	public LinkScheme(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	public String _getFromTarget() {
		return fromTarget;
	}

	public void _setFromTarget(String fromTarget) {
		this.fromTarget = fromTarget;
	}

	public String _getToTarget() {
		return toTarget;
	}

	public void _setToTarget(String toTarget) {
		this.toTarget = toTarget;
	}

	public EditionPattern getFromTargetEditionPattern() {
		if (StringUtils.isEmpty(_getFromTarget())) {
			return null;
		}
		if (getVirtualModel() != null) {
			return getVirtualModel().getEditionPattern(_getFromTarget());
		}
		return null;
	}

	public void setFromTargetEditionPattern(EditionPattern targetEditionPattern) {
		_setFromTarget(targetEditionPattern != null ? targetEditionPattern.getURI() : null);
		updateBindingModels();
	}

	public EditionPattern getToTargetEditionPattern() {
		if (StringUtils.isEmpty(_getToTarget())) {
			return null;
		}
		if (getVirtualModel() != null) {
			return getVirtualModel().getEditionPattern(_getToTarget());
		}
		if (getViewPointLibrary() != null) {
			return getViewPointLibrary().getEditionPattern(_getToTarget());
		}
		return null;
	}

	public void setToTargetEditionPattern(EditionPattern targetEditionPattern) {
		_setToTarget(targetEditionPattern != null ? targetEditionPattern.getURI() : null);
		updateBindingModels();
	}

	public boolean isValidTarget(EditionPattern actualFromTarget, EditionPattern actualToTarget) {
		// TODO: improved this so that we can take into account adressed models restrictions. See also
		// LinkScheme.isValidTarget on branch 1.5.1
		return getFromTargetEditionPattern().isAssignableFrom(actualFromTarget)
				&& getToTargetEditionPattern().isAssignableFrom(actualToTarget);
	}

	@Override
	protected void appendContextualBindingVariables(BindingModel bindingModel) {
		super.appendContextualBindingVariables(bindingModel);
		bindingModelNeedToBeRecomputed = false;
		if (getFromTargetEditionPattern() != null) {
			bindingModel.addToBindingVariables(new BindingVariable(DiagramEditionScheme.FROM_TARGET, EditionPatternInstanceType
					.getEditionPatternInstanceType(getFromTargetEditionPattern())));
		} else if (_getFromTarget() != null && !StringUtils.isEmpty(_getFromTarget())) {
			bindingModelNeedToBeRecomputed = true;
		}
		if (getToTargetEditionPattern() != null) {
			bindingModel.addToBindingVariables(new BindingVariable(DiagramEditionScheme.TO_TARGET, EditionPatternInstanceType
					.getEditionPatternInstanceType(getToTargetEditionPattern())));
		} else if (_getToTarget() != null && !StringUtils.isEmpty(_getToTarget())) {
			bindingModelNeedToBeRecomputed = true;
		}
	}

	private boolean bindingModelNeedToBeRecomputed = false;
	private boolean isUpdatingBindingModel = false;

	@Override
	public BindingModel getBindingModel() {
		if (bindingModelNeedToBeRecomputed && !isUpdatingBindingModel) {
			isUpdatingBindingModel = true;
			bindingModelNeedToBeRecomputed = false;
			updateBindingModels();
			isUpdatingBindingModel = false;
		}
		return super.getBindingModel();
	}

	@Override
	protected void rebuildActionsBindingModel() {
		if (!bindingModelNeedToBeRecomputed) {
			super.rebuildActionsBindingModel();
		}
	}

	/**
	 * Overrides {@link #createAction(Class, ModelSlot)} by providing default value for from and to targets
	 * 
	 * @return newly created {@link EditionAction}
	 */
	@Override
	public <A extends EditionAction<?, ?>> A createAction(Class<A> actionClass, ModelSlot<?> modelSlot) {
		A returned = super.createAction(actionClass, modelSlot);
		if (returned instanceof AddConnector) {
			AddConnector newAction = (AddConnector) returned;
			EditionPattern fromEditionPattern = this.getFromTargetEditionPattern();
			if (fromEditionPattern != null) {
				ShapePatternRole fromShapePatternRole = fromEditionPattern.getDefaultShapePatternRole();
				if (fromShapePatternRole != null) {
					newAction.setFromShape(new DataBinding<DiagramShape>("fromTarget." + fromShapePatternRole.getName()));
				}
			}
			EditionPattern toEditionPattern = this.getToTargetEditionPattern();
			if (toEditionPattern != null) {
				ShapePatternRole toShapePatternRole = toEditionPattern.getDefaultShapePatternRole();
				if (toShapePatternRole != null) {
					newAction.setToShape(new DataBinding<DiagramShape>("toTarget." + toShapePatternRole.getName()));
				}
			}
		}
		return returned;
	}

	public boolean getIsAvailableWithFloatingPalette() {
		return isAvailableWithFloatingPalette;
	}

	public void setIsAvailableWithFloatingPalette(boolean isAvailableWithFloatingPalette) {
		this.isAvailableWithFloatingPalette = isAvailableWithFloatingPalette;
	}

	public boolean getNorthDirectionSupported() {
		return northDirectionSupported;
	}

	public void setNorthDirectionSupported(boolean northDirectionSupported) {
		this.northDirectionSupported = northDirectionSupported;
	}

	public boolean getEastDirectionSupported() {
		return eastDirectionSupported;
	}

	public void setEastDirectionSupported(boolean eastDirectionSupported) {
		this.eastDirectionSupported = eastDirectionSupported;
	}

	public boolean getSouthDirectionSupported() {
		return southDirectionSupported;
	}

	public void setSouthDirectionSupported(boolean southDirectionSupported) {
		this.southDirectionSupported = southDirectionSupported;
	}

	public boolean getWestDirectionSupported() {
		return westDirectionSupported;
	}

	public void setWestDirectionSupported(boolean westDirectionSupported) {
		this.westDirectionSupported = westDirectionSupported;
	}

}
