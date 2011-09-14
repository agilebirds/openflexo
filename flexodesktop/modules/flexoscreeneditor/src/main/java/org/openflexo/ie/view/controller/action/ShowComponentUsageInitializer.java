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
package org.openflexo.ie.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.cl.action.ShowComponentUsage;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ShowComponentUsageInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public ShowComponentUsageInitializer(IEControllerActionInitializer actionInitializer)
	{
		super(ShowComponentUsage.actionType,actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() 
	{
		return (IEControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ShowComponentUsage> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<ShowComponentUsage>() {
			@Override
			public boolean run(ActionEvent e, ShowComponentUsage action)
			{
				StringBuilder sb = new StringBuilder();
				ComponentDefinition cd = action.getComponentDefinition();
				Vector<ComponentInstance> cis = action.getComponentDefinition().getComponentInstances();
				for (ComponentInstance ci : cis) {
					if (cd instanceof OperationComponentDefinition) {
						if (ci.getOwner() instanceof OperationNode)
							appendComponentInstance(ci, sb);
					} else if (cd instanceof PopupComponentDefinition) {
						if (ci.getOwner() instanceof IEHyperlinkWidget)
							appendComponentInstance(ci, sb);
					} else if (cd instanceof TabComponentDefinition)
						if (ci.getOwner() instanceof IETabWidget)
							appendComponentInstance(ci, sb);
				}
				if (sb.length()==0)
					sb.append(FlexoLocalization.localizedForKey("component")).append(" ").append(cd.getName()).append(" ").append(FlexoLocalization.localizedForKey("is_not_used"));
				FlexoController.notify(sb.toString());
				return true;
			}
			
			private void appendComponentInstance(ComponentInstance ci, StringBuilder sb) {
				if (ci.getXMLResourceData() instanceof FlexoModelObject) {
					if (sb.length()==0)
						sb.append(FlexoLocalization.localizedForKey("component")).append(" '").append(ci.getComponentName()).append("' ").append(FlexoLocalization.localizedForKey("is_used_in"));
					sb.append("\n");
					sb.append("* ").append(((FlexoModelObject)ci.getOwner()).getDisplayableName()).append(" ");
					sb.append(FlexoLocalization.localizedForKey("in")).append(" ").append(((FlexoModelObject)ci.getXMLResourceData()).getDisplayableName());
				}
			}
		};
	}

	
}
