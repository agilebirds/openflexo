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
package org.openflexo.technologyadapter.diagram.fml;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.viewpoint.AbstractCreationScheme;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternInstanceType;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddConnector;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;
import org.openflexo.toolbox.StringUtils;

@FIBPanel("Fib/LinkSchemePanel.fib")
@ModelEntity
@ImplementationClass(LinkScheme.LinkSchemeImpl.class)
@XMLElement
public interface LinkScheme extends AbstractCreationScheme,DiagramEditionScheme{

@PropertyIdentifier(type=String.class)
public static final String FROM_TARGET_KEY = "fromTarget";
@PropertyIdentifier(type=String.class)
public static final String TO_TARGET_KEY = "toTarget";
@PropertyIdentifier(type=boolean.class)
public static final String IS_AVAILABLE_WITH_FLOATING_PALETTE_KEY = "isAvailableWithFloatingPalette";
@PropertyIdentifier(type=boolean.class)
public static final String NORTH_DIRECTION_SUPPORTED_KEY = "northDirectionSupported";
@PropertyIdentifier(type=boolean.class)
public static final String EAST_DIRECTION_SUPPORTED_KEY = "eastDirectionSupported";
@PropertyIdentifier(type=boolean.class)
public static final String SOUTH_DIRECTION_SUPPORTED_KEY = "southDirectionSupported";
@PropertyIdentifier(type=boolean.class)
public static final String WEST_DIRECTION_SUPPORTED_KEY = "westDirectionSupported";

@Getter(value=FROM_TARGET_KEY)
@XMLAttribute
public String _getFromTarget();

@Setter(FROM_TARGET_KEY)
public void _setFromTarget(String fromTarget);


@Getter(value=TO_TARGET_KEY)
@XMLAttribute
public String _getToTarget();

@Setter(TO_TARGET_KEY)
public void _setToTarget(String toTarget);


@Getter(value=IS_AVAILABLE_WITH_FLOATING_PALETTE_KEY,defaultValue = "false")
@XMLAttribute
public boolean getIsAvailableWithFloatingPalette();

@Setter(IS_AVAILABLE_WITH_FLOATING_PALETTE_KEY)
public void setIsAvailableWithFloatingPalette(boolean isAvailableWithFloatingPalette);


@Getter(value=NORTH_DIRECTION_SUPPORTED_KEY,defaultValue = "false")
@XMLAttribute
public boolean getNorthDirectionSupported();

@Setter(NORTH_DIRECTION_SUPPORTED_KEY)
public void setNorthDirectionSupported(boolean northDirectionSupported);


@Getter(value=EAST_DIRECTION_SUPPORTED_KEY,defaultValue = "false")
@XMLAttribute
public boolean getEastDirectionSupported();

@Setter(EAST_DIRECTION_SUPPORTED_KEY)
public void setEastDirectionSupported(boolean eastDirectionSupported);


@Getter(value=SOUTH_DIRECTION_SUPPORTED_KEY,defaultValue = "false")
@XMLAttribute
public boolean getSouthDirectionSupported();

@Setter(SOUTH_DIRECTION_SUPPORTED_KEY)
public void setSouthDirectionSupported(boolean southDirectionSupported);


@Getter(value=WEST_DIRECTION_SUPPORTED_KEY,defaultValue = "false")
@XMLAttribute
public boolean getWestDirectionSupported();

@Setter(WEST_DIRECTION_SUPPORTED_KEY)
public void setWestDirectionSupported(boolean westDirectionSupported);


public static abstract  class LinkSchemeImpl extends AbstractCreationSchemeImpl implements LinkScheme
{

	private String fromTarget;
	private String toTarget;

	private boolean northDirectionSupported = true;
	private boolean eastDirectionSupported = true;
	private boolean southDirectionSupported = true;
	private boolean westDirectionSupported = true;

	private boolean isAvailableWithFloatingPalette = true;

	public LinkSchemeImpl() {
		super();
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
				ShapePatternRole fromShapePatternRole = getDefaultShapePatternRole(fromEditionPattern);
				if (fromShapePatternRole != null) {
					newAction.setFromShape(new DataBinding<DiagramShape>("fromTarget." + fromShapePatternRole.getName()));
				}
			}
			EditionPattern toEditionPattern = this.getToTargetEditionPattern();
			if (toEditionPattern != null) {
				ShapePatternRole toShapePatternRole = getDefaultShapePatternRole(toEditionPattern);
				if (toShapePatternRole != null) {
					newAction.setToShape(new DataBinding<DiagramShape>("toTarget." + toShapePatternRole.getName()));
				}
			}
		}
		return returned;
	}

	private ShapePatternRole getDefaultShapePatternRole(EditionPattern ep) {
		if (ep.getPatternRoles(ShapePatternRole.class).size() > 0) {
			return ep.getPatternRoles(ShapePatternRole.class).get(0);
		}
		return null;
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

	@Override
	public DiagramTechnologyAdapter getTechnologyAdapter() {
		return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class);
	}

}
}
