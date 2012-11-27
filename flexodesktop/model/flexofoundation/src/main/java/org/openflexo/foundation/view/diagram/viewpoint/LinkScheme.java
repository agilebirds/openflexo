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
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddConnector;
import org.openflexo.foundation.viewpoint.AbstractCreationScheme;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.EditionPatternPathElement;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.toolbox.StringUtils;

public class LinkScheme extends AbstractCreationScheme {

	private String fromTarget;
	private String toTarget;

	private boolean northDirectionSupported = true;
	private boolean eastDirectionSupported = true;
	private boolean southDirectionSupported = true;
	private boolean westDirectionSupported = true;

	private boolean isAvailableWithFloatingPalette = true;

	public LinkScheme(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public EditionSchemeType getEditionSchemeType() {
		return EditionSchemeType.LinkScheme;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.LINK_SCHEME_INSPECTOR;
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
		if (getViewPointLibrary() != null) {
			return getViewPointLibrary().getEditionPattern(_getFromTarget());
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
		return getFromTargetEditionPattern().isAssignableFrom(actualFromTarget)
				&& getToTargetEditionPattern().isAssignableFrom(actualToTarget);
	}

	@Override
	protected void appendContextualBindingVariables(BindingModel bindingModel) {
		super.appendContextualBindingVariables(bindingModel);
		bindingModelNeedToBeRecomputed = false;
		if (getFromTargetEditionPattern() != null) {
			bindingModel.addToBindingVariables(new EditionPatternPathElement<LinkScheme>(EditionScheme.FROM_TARGET,
					getFromTargetEditionPattern(), this));
		} else if (_getFromTarget() != null && !StringUtils.isEmpty(_getFromTarget())) {
			bindingModelNeedToBeRecomputed = true;
		}
		if (getToTargetEditionPattern() != null) {
			bindingModel.addToBindingVariables(new EditionPatternPathElement<LinkScheme>(EditionScheme.TO_TARGET,
					getToTargetEditionPattern(), this));
		} else if (_getToTarget() != null && !StringUtils.isEmpty(_getToTarget())) {
			bindingModelNeedToBeRecomputed = true;
		}
	}

	private boolean bindingModelNeedToBeRecomputed = false;

	@Override
	public BindingModel getBindingModel() {
		if (bindingModelNeedToBeRecomputed) {
			bindingModelNeedToBeRecomputed = false;
			updateBindingModels();
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
	 * Overrides {@link #createAction(Class, ModelSlot)} by providing default value for top level container
	 * 
	 * @return newly created {@link EditionAction}
	 */
	@Override
	public <A extends EditionAction<MS, M, MM, ?>, MS extends ModelSlot<M, MM>, M extends FlexoModel<MM>, MM extends FlexoMetaModel> A createAction(
			Class<A> actionClass, MS modelSlot) {
		A returned = super.createAction(actionClass, modelSlot);
		if (returned instanceof AddConnector) {
			AddConnector newAction = (AddConnector) returned;
			EditionPattern fromEditionPattern = this.getFromTargetEditionPattern();
			if (fromEditionPattern != null) {
				ShapePatternRole fromShapePatternRole = fromEditionPattern.getDefaultShapePatternRole();
				if (fromShapePatternRole != null) {
					newAction.setFromShape(new ViewPointDataBinding("fromTarget." + fromShapePatternRole.getName()));
				}
			}
			EditionPattern toEditionPattern = this.getToTargetEditionPattern();
			if (toEditionPattern != null) {
				ShapePatternRole toShapePatternRole = toEditionPattern.getDefaultShapePatternRole();
				if (toShapePatternRole != null) {
					newAction.setToShape(new ViewPointDataBinding("toTarget." + toShapePatternRole.getName()));
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
