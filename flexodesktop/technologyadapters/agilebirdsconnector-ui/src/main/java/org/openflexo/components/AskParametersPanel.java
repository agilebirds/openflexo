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
package org.openflexo.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.inspector.TabModelView;
import org.openflexo.inspector.widget.DenaliWidget;
import org.openflexo.view.controller.AskParametersController;

/**
 * Dialog allowing to automatically ask and edit parameters
 * 
 * @author sguerin
 * 
 */
public class AskParametersPanel extends JPanel {

	static final Logger logger = Logger.getLogger(AskParametersPanel.class.getPackage().getName());

	private ParametersModel _parametersModel;
	private TabModelView paramsPanel;

	public AskParametersPanel(FlexoProject project, ParameterDefinition... parameters) {
		super();
		setLayout(new BorderLayout());
		_parametersModel = new ParametersModel(project, parameters);
		paramsPanel = new TabModelView(_parametersModel.getTabModel(), null, AskParametersController.instance());
		paramsPanel.performObserverSwitch(_parametersModel);
		paramsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		paramsPanel.valueChange(_parametersModel);
		add(paramsPanel, BorderLayout.CENTER);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				paramsPanel.requestFocusInFirstWidget();
			}
		});
	}

	public DenaliWidget getInspectorWidgetForParameter(ParameterDefinition parameterDefinition) {
		return paramsPanel.getInspectorWidgetFor(parameterDefinition.getName());
	}

	public void update() {
		paramsPanel.valueChange(_parametersModel);
		paramsPanel.performObserverSwitch(_parametersModel);
	}

	@Override
	public void setBackground(Color aColor) {
		super.setBackground(aColor);
		if (paramsPanel != null) {
			paramsPanel.setBackground(aColor);
		}
	}

	public Object parameterValueWithName(String paramName) {
		return _parametersModel.objectForKey(paramName);
	}

	public void setParameterValueWithName(Object value, String paramName) {
		_parametersModel.setObjectForKey(value, paramName);
	}

	public boolean booleanParameterValueWithName(String paramName) {
		return _parametersModel.booleanValueForKey(paramName);
	}

	public void setBooleanParameterValueWithName(boolean value, String paramName) {
		_parametersModel.setBooleanValueForKey(value, paramName);
	}

	public int integerParameterValueWithName(String paramName) {
		return _parametersModel.integerValueForKey(paramName);
	}

	public void setIntegerParameterValueWithName(int value, String paramName) {
		_parametersModel.setIntegerValueForKey(value, paramName);
	}

	public ParametersModel getParametersModel() {
		return _parametersModel;
	}

}
