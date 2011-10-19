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

import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;


// Note: no Pattern Role for this action !!!
public class GoToAction extends EditionAction {

	private static final Logger logger = Logger.getLogger(GoToAction.class.getPackage().getName());
	
	public GoToAction() {
	}
	
	@Override
	public EditionActionType getEditionActionType()
	{
		return EditionActionType.GoToObject;
	}
	
	@Override
	public String getInspectorName() 
	{
		return Inspectors.VPM.GO_TO_OBJECT_INSPECTOR;
	}

	public FlexoModelObject getTargetObject(EditionSchemeAction action)
	{
		return (FlexoModelObject)getTarget().getBindingValue(action);
	}


	/*@Override
	protected void updatePatternRoleType()
	{
		// Not relevant
	}*/

	private ViewPointDataBinding target;
	
	private BindingDefinition TARGET = new BindingDefinition("target", FlexoModelObject.class, BindingDefinitionType.GET, false);
	
	public BindingDefinition getTargetBindingDefinition()
	{
		return TARGET;
	}

	public ViewPointDataBinding getTarget() 
	{
		if (target == null) target = new ViewPointDataBinding(this,EditionActionBindingAttribute.target,getTargetBindingDefinition());
		return target;
	}

	public void setTarget(ViewPointDataBinding target) 
	{
		target.setOwner(this);
		target.setBindingAttribute(EditionActionBindingAttribute.target);
		target.setBindingDefinition(getTargetBindingDefinition());
		this.target = target;
	}
	

}
