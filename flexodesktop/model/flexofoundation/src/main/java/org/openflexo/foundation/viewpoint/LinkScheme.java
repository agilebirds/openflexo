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

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.viewpoint.binding.EditionPatternPathElement;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.toolbox.StringUtils;

public class LinkScheme extends AbstractCreationScheme {

	private String fromTarget;
	private String toTarget;

	private boolean isAvailableWithFloatingPalette = true;

	public LinkScheme() {
		super();
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
			updateBindingModels();
		}
		return super.getBindingModel();
	}

	@Override
	public AddConnector createAddConnectorAction() {
		AddConnector newAction = super.createAddConnectorAction();
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
		return newAction;
	}

	public boolean getIsAvailableWithFloatingPalette() {
		return isAvailableWithFloatingPalette;
	}

	public void setIsAvailableWithFloatingPalette(boolean isAvailableWithFloatingPalette) {
		this.isAvailableWithFloatingPalette = isAvailableWithFloatingPalette;
	}

}
