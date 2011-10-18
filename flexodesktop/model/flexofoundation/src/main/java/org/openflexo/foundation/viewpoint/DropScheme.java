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
import org.openflexo.foundation.viewpoint.inspector.EditionPatternPathElement;
import org.openflexo.foundation.viewpoint.inspector.InspectorDataBinding;
import org.openflexo.toolbox.StringUtils;


public class DropScheme extends EditionScheme {

	private String target;

	public DropScheme() 
	{
		super();
	}

	@Override
	public EditionSchemeType getEditionSchemeType()
	{
		return EditionSchemeType.DropScheme;
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.VPM.DROP_SCHEME_INSPECTOR;
	}

	public String _getTarget() 
	{
		return target;
	}

	public void _setTarget(String target) 
	{
		this.target = target;
	}

	public EditionPattern getTargetEditionPattern()
	{
		if (StringUtils.isEmpty(_getTarget())) {
			return null;
		}
		if (getViewPointLibrary() != null)
			return getViewPointLibrary().getEditionPattern(_getTarget());
		return null;
	}
	
	public void setTargetEditionPattern(EditionPattern targetEditionPattern)
	{
		_setTarget(targetEditionPattern != null ? targetEditionPattern.getURI() : null);
		updateBindingModels();
	}

	public boolean isTopTarget()
	{
		return getTopTarget();
	}

	public boolean getTopTarget()
	{
		if (StringUtils.isEmpty(_getTarget())) {
			return false;
		}
		return _getTarget().equalsIgnoreCase("top");
	}
	
	public void setTopTarget(boolean flag)
	{
		if (flag) {
			_setTarget("top");
		}
		else {
			_setTarget("");
		}
	}
	
	public boolean isValidTarget(EditionPattern aTarget)
	{
		return getTargetEditionPattern() == aTarget;
		
	}
	
	
	@Override
	protected void appendContextualBindingVariables(BindingModel bindingModel)
	{
		bindingModelNeedToBeRecomputed = false;
		if (getTargetEditionPattern() != null)
			bindingModel.addToBindingVariables(new EditionPatternPathElement<DropScheme>("target",getTargetEditionPattern(),this));
		else if (_getTarget() != null && !_getTarget().equals("top")) {
			logger.warning("Cannot find edition pattern "+_getTarget()+" !!!!!!!!!!!!!!");
			bindingModelNeedToBeRecomputed = true;
		}
	}
	
	private boolean bindingModelNeedToBeRecomputed = false;

	@Override
	public BindingModel getBindingModel() 
	{
		if (bindingModelNeedToBeRecomputed) updateBindingModels();
		return super.getBindingModel();
	}
		
	@Override
	public AddShape createAddShapeAction()
	{
		AddShape newAction = super.createAddShapeAction();
		if (isTopTarget()) newAction.setContainer(new InspectorDataBinding("topLevel"));
		return newAction;
	}


}
