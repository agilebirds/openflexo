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
package org.openflexo.inspector.editor;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.HelpDelegate;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.InspectingWidget;
import org.openflexo.inspector.InspectorDelegate;
import org.openflexo.inspector.InspectorModelView;
import org.openflexo.inspector.InspectorWidgetConfiguration;
import org.openflexo.inspector.model.InspectorModel;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.inspector.selection.InspectorSelection;


public class EditedInspectorView extends JPanel {

	public EditedInspectorView(InspectorModel inspector) 
	{
		super(new BorderLayout());
		
		final AbstractController controller = new AbstractController() {
			
			public void notifiedInspectedObjectChange(
					InspectableObject newInspectedObject) {
				// TODO Auto-generated method stub
				
			}
			
			public boolean isTabPanelVisible(TabModel tab, InspectableObject inspectable) {
				// TODO Auto-generated method stub
				return false;
			}
			
			public HelpDelegate getHelpDelegate() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public InspectorDelegate getDelegate() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public InspectorWidgetConfiguration getConfiguration() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean handleException(InspectableObject inspectable,
					String propertyName, Object value, Throwable exception) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		
		InspectorModelView view = new InspectorModelView(inspector,new InspectingWidget() {
			
			public void notifiedInspectedObjectChange(InspectableObject newInspectedObject) {
				// TODO Auto-generated method stub
				
			}
			
			public void notifiedActiveTabChange(String newActiveTabName) {
				// TODO Auto-generated method stub
				
			}
			
			public void newSelection(InspectorSelection selection) {
				// TODO Auto-generated method stub
				
			}
			
			public AbstractController getController() {
				return controller;
			}
		});
		
		add(view,BorderLayout.CENTER);
	}
	
}
