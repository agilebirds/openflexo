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
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.fml.editionaction.AddShape;
import org.openflexo.technologyadapter.diagram.model.DiagramContainerElement;
import org.openflexo.toolbox.StringUtils;

@FIBPanel("Fib/DropSchemePanel.fib")
@ModelEntity
@ImplementationClass(DropScheme.DropSchemeImpl.class)
@XMLElement
public interface DropScheme extends AbstractCreationScheme,DiagramEditionScheme{

@PropertyIdentifier(type=String.class)
public static final String TARGET_KEY = "target";
@PropertyIdentifier(type=ShapePatternRole.class)
public static final String TARGET_PATTERN_ROLE_KEY = "targetPatternRole";

@Getter(value=TARGET_KEY)
@XMLAttribute
public String _getTarget();

@Setter(TARGET_KEY)
public void _setTarget(String target);


@Getter(value=TARGET_PATTERN_ROLE_KEY)
@XMLElement
public ShapePatternRole getTargetPatternRole();

@Setter(TARGET_PATTERN_ROLE_KEY)
public void setTargetPatternRole(ShapePatternRole targetPatternRole);


public static abstract  class DropSchemeImpl extends AbstractCreationSchemeImpl implements DropScheme
{

	private String target;
	private ShapePatternRole targetPatternRole;

	public DropSchemeImpl() {
		super();
	}

	public String _getTarget() {
		return target;
	}

	public void _setTarget(String target) {
		this.target = target;
	}

	public EditionPattern getTargetEditionPattern() {
		if (StringUtils.isEmpty(_getTarget())) {
			return null;
		}
		if (isTopTarget()) {
			return null;
		}
		if (getVirtualModel() != null) {
			return getVirtualModel().getEditionPattern(_getTarget());
		}
		return null;
	}

	public void setTargetEditionPattern(EditionPattern targetEditionPattern) {
		_setTarget(targetEditionPattern != null ? targetEditionPattern.getURI() : null);
		updateBindingModels();
	}

	public boolean isTopTarget() {
		return getTopTarget();
	}

	public boolean getTopTarget() {
		if (StringUtils.isEmpty(_getTarget())) {
			return false;
		}
		return _getTarget().equalsIgnoreCase("top");
	}

	public void setTopTarget(boolean flag) {
		if (flag) {
			_setTarget("top");
		} else {
			_setTarget("");
		}
	}

	@Deprecated
	public boolean targetHasMultipleRoles() {
		// return getTargetEditionPattern() != null && getTargetEditionPattern().getShapePatternRoles().size() > 1;
		return false;
	}

	public ShapePatternRole getTargetPatternRole() {
		return targetPatternRole;
	}

	public void setTargetPatternRole(ShapePatternRole targetPatternRole) {
		this.targetPatternRole = targetPatternRole;
	}

	public boolean isValidTarget(EditionPattern aTarget, PatternRole contextRole) {
		if (getTargetEditionPattern() != null && getTargetEditionPattern().isAssignableFrom(aTarget)) {
			if (targetHasMultipleRoles()) {
				// TODO make proper implementation when role inheritance will be in use !!!
				return getTargetPatternRole() == null
						|| getTargetPatternRole().getPatternRoleName().equals(contextRole.getPatternRoleName());
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void appendContextualBindingVariables(BindingModel bindingModel) {
		super.appendContextualBindingVariables(bindingModel);
		bindingModelNeedToBeRecomputed = false;
		if (getTargetEditionPattern() != null) {
			bindingModel.addToBindingVariables(new BindingVariable(DiagramEditionScheme.TARGET, EditionPatternInstanceType
					.getEditionPatternInstanceType(getTargetEditionPattern())));
		} else if (_getTarget() != null && !_getTarget().equals("top")) {
			// logger.warning("Cannot find edition pattern " + _getTarget() + " !!!!!!!!!!!!!!");
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
	 * Overrides {@link #createAction(Class, ModelSlot)} by providing default value for top level container
	 * 
	 * @return newly created {@link EditionAction}
	 */
	@Override
	public <A extends EditionAction<?, ?>> A createAction(Class<A> actionClass, ModelSlot<?> modelSlot) {
		A newAction = super.createAction(actionClass, modelSlot);
		if (newAction instanceof AddShape) {
			if (isTopTarget()) {
				((AddShape) newAction).setContainer(new DataBinding<DiagramContainerElement<?>>(DiagramEditionScheme.TOP_LEVEL));
			}
		}
		return newAction;
	}

	@Override
	public DiagramTechnologyAdapter getTechnologyAdapter() {
		return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class);
	}
}
}
