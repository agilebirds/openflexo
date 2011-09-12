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
package org.openflexo.dgmodule.controller.action;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;


import org.openflexo.dgmodule.view.DGTemplateFileModuleView;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.templates.action.OpenTemplateFileInNewWindow;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class OpenTemplateFileInNewWindowInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	OpenTemplateFileInNewWindowInitializer(DGControllerActionInitializer actionInitializer)
	{
		super(OpenTemplateFileInNewWindow.actionType,actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() 
	{
		return (DGControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<OpenTemplateFileInNewWindow> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<OpenTemplateFileInNewWindow>() {
			@Override
			public boolean run(ActionEvent e, OpenTemplateFileInNewWindow action)
			{
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<OpenTemplateFileInNewWindow> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<OpenTemplateFileInNewWindow>() {
			@Override
			public boolean run(ActionEvent e, OpenTemplateFileInNewWindow action)
			{
				DGTemplateFileModuleView view 
				= (DGTemplateFileModuleView)getControllerActionInitializer().getDGController().
				createModuleViewForObjectAndPerspective(action.getFocusedObject(), null);
				view.setOpenedInSeparateWindow(true);
				final FlexoDialog dialog = new FlexoDialog(getControllerActionInitializer().getDGController().getFlexoFrame(), action.getFocusedObject().getTemplateName(), false);
				dialog.getContentPane().setLayout(new BorderLayout());
				dialog.getContentPane().add(view,BorderLayout.CENTER);
				JPanel controlPanel = new JPanel(new FlowLayout());
				JButton button = new JButton();
				button.setText(FlexoLocalization.localizedForKey("close",button));
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}            		
				});
				controlPanel.add(button);
				dialog.getContentPane().add(controlPanel,BorderLayout.SOUTH);
				dialog.validate();
				dialog.pack();
				dialog.setVisible(true);
				return true;
			}
		};
	}

}
