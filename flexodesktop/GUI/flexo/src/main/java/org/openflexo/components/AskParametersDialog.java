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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.openflexo.foundation.param.LabelParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParameterDefinition.ValueListener;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.inspector.TabModelView;
import org.openflexo.inspector.widget.DenaliWidget.WidgetLayout;
import org.openflexo.inspector.widget.LabelWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.AskParametersController;

/**
 * Dialog allowing to automatically ask and edit parameters
 * 
 * @author sguerin
 * 
 */
public class AskParametersDialog extends FlexoDialog implements ValueListener {

	static final Logger logger = Logger.getLogger(AskParametersDialog.class.getPackage().getName());

	public static final int CANCEL = 0;

	public static final int VALIDATE = 1;

	int status;
	private ParametersModel _parametersModel;

	/*public AskParametersDialog(FlexoProject project, String windowTitle, String title, ParameterDefinition... parameters)
	{
	    this(project,windowTitle,title, (ParameterDefinition[])parameters);
	}*/

	private JButton _validateButton;

	private static AskParametersDialog _visibleDialog = null;
	// private static Vector<AskParametersDialog> _waitingDialog = new Vector<AskParametersDialog>();

	private TabModelView paramsPanel;

	private static final AskParametersDialog _createInstance(FlexoProject project, Frame owner, String windowTitle, String title,
			ParameterDefinition... parameters) {
		if (owner != null) {
			return new AskParametersDialog(owner, project, windowTitle, title, parameters);
		}
		if (FlexoFrame.getActiveFrame() != null) {
			return new AskParametersDialog(FlexoFrame.getActiveFrame(), project, windowTitle, title, parameters);
		} else if (ProgressWindow.hasInstance()) {
			return new AskParametersDialog(ProgressWindow.instance().initOwner, project, windowTitle, title, parameters);
		}
		return new AskParametersDialog(null, project, windowTitle, title, parameters);
	}

	private static ParameterDefinition[] toArray(Vector<ParameterDefinition> parameters) {
		ParameterDefinition[] returned = new ParameterDefinition[parameters.size()];
		int i = 0;
		for (ParameterDefinition p : parameters) {
			returned[i++] = p;
		}
		return returned;
	}

	public static final AskParametersDialog createAskParametersDialog(FlexoProject project, String windowTitle, String title,
			Vector<ParameterDefinition> parameters) {
		return createAskParametersDialog(project, windowTitle, title, toArray(parameters));
	}

	public static final AskParametersDialog createAskParametersDialog(FlexoProject project, String windowTitle, String title,
			ParameterDefinition... parameters) {
		return createAskParametersDialog(project, null, windowTitle, title, parameters);
	}

	public static final AskParametersDialog createAskParametersDialog(FlexoProject project, Frame owner, String windowTitle, String title,
			Vector<ParameterDefinition> parameters) {
		return createAskParametersDialog(project, owner, windowTitle, title, toArray(parameters));
	}

	public static final AskParametersDialog createAskParametersDialog(FlexoProject project, Frame owner, String windowTitle, String title,
			ParameterDefinition... parameters) {
		AskParametersDialog returned = _createInstance(project, owner, windowTitle, title, parameters);
		returned.setVisible(true);
		return returned;
	}

	public static final AskParametersDialog createAskParametersDialog(FlexoProject project, String windowTitle, String title,
			final ValidationCondition validationCondition, Vector<ParameterDefinition> parameters) {
		return createAskParametersDialog(project, windowTitle, title, validationCondition, toArray(parameters));
	}

	public static final AskParametersDialog createAskParametersDialog(FlexoProject project, String windowTitle, String title,
			final ValidationCondition validationCondition, ParameterDefinition... parameters) {
		return createAskParametersDialog(project, null, windowTitle, title, validationCondition, parameters);
	}

	public static final AskParametersDialog createAskParametersDialog(FlexoProject project, Frame owner, String windowTitle, String title,
			final ValidationCondition validationCondition, Vector<ParameterDefinition> parameters) {
		return createAskParametersDialog(project, owner, windowTitle, title, validationCondition, toArray(parameters));
	}

	public static final AskParametersDialog createAskParametersDialog(FlexoProject project, Frame owner, String windowTitle, String title,
			final ValidationCondition validationCondition, ParameterDefinition... parameters) {
		final AccessToAskParametersDialog access = new AccessToAskParametersDialog();
		ParameterDefinition[] newParams = new ParameterDefinition[parameters.length + 1];
		for (int i = 0; i < parameters.length; i++) {
			newParams[i] = parameters[i];
		}
		StringBuffer sb = new StringBuffer();
		boolean isFirst = true;
		for (ParameterDefinition p : parameters) {
			sb.append((isFirst ? "" : ",") + p.getName());
			isFirst = false;
		}

		LabelParameter labelParameter = new LabelParameter("failure_message", "", "", false) {
			@Override
			public String getValue() {
				if (access.dialog != null) {
					if (access.dialog.isValidateEnabled()) {
						return "";
					} else {
						return "<html><center><font color=\"red\">" + access.dialog._validationCondition.getErrorMessage()
								+ "</font></center></html>";
					}
				}
				return "";
			}
		};
		labelParameter.setAlign(LabelWidget.CENTER);
		labelParameter.setWidgetLayout(WidgetLayout.LABEL_NEXTTO_WIDGET_LAYOUT);
		labelParameter.setExpandHorizontally(true);
		labelParameter.setDepends(sb.toString());

		newParams[parameters.length] = labelParameter;

		access.dialog = _createInstance(project, owner, windowTitle, title, newParams);
		access.dialog.setValidationCondition(validationCondition);
		access.dialog.pack();
		access.dialog.setVisible(true);
		return access.dialog;
	}

	protected static class AccessToAskParametersDialog {
		protected AskParametersDialog dialog;
	}

	private AskParametersDialog(Frame owner, FlexoProject project, String windowTitle, String title, ParameterDefinition... parameters) {
		super(owner, true);
		initDialog(project, windowTitle, title, parameters);
	}

	private void initDialog(FlexoProject project, String windowTitle, String title, ParameterDefinition... parameters) {
		setTitle(windowTitle);

		status = CANCEL;

		_parametersModel = new ParametersModel(project, parameters);

		for (ParameterDefinition p : parameters) {
			p.addValueListener(this);
		}

		getContentPane().setLayout(new BorderLayout());

		JLabel titlePanel = new JLabel(title, SwingConstants.CENTER);
		titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		paramsPanel = new TabModelView(_parametersModel.getTabModel(), null, AskParametersController.instance());
		paramsPanel.performObserverSwitch(_parametersModel);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		controlPanel.setOpaque(true);
		JButton cancelButton = new JButton();
		cancelButton.setOpaque(false);
		cancelButton.setText(FlexoLocalization.localizedForKey("cancel", cancelButton));
		_validateButton = new JButton();
		_validateButton.setOpaque(false);
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			_validateButton.setText(FlexoLocalization.localizedForKey("validate", _validateButton));
		} else {
			_validateButton.setText(FlexoLocalization.localizedForKey("ok", _validateButton));
		}
		_validateButton.setEnabled(isValidateEnabled());
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				status = CANCEL;
				dispose();
			}
		});
		_validateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				status = VALIDATE;
				dispose();
			}
		});
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			controlPanel.add(cancelButton);
			controlPanel.add(_validateButton);
		} else {
			controlPanel.add(_validateButton);
			controlPanel.add(cancelButton);
		}

		getContentPane().add(titlePanel, BorderLayout.NORTH);
		JScrollPane scrollpane = new JScrollPane(paramsPanel);
		scrollpane.setOpaque(false);
		scrollpane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(scrollpane, BorderLayout.CENTER);
		getContentPane().add(controlPanel, BorderLayout.SOUTH);
		// setSize(new Dimension (400,200+parameters.length*30));
		getRootPane().setDefaultButton(_validateButton);
		validate();
		pack();
		// GPO: Forces width of dialog to be at least 250px
		paramsPanel.requestFocusInFirstWidget();
		if (getWidth() < 250) {
			setSize(250, getHeight());
		}
		paramsPanel.valueChange(_parametersModel);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (_visibleDialog == this) {
			_visibleDialog = null;
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!visible && _visibleDialog == this) {
			_visibleDialog = null;
		}
	}

	public int getStatus() {
		return status;
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

	@Override
	public void newValueWasSet(ParameterDefinition param, Object oldValue, Object newValue) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("newValueWasSet() " + param + " oldValue=" + oldValue + " newValue=" + newValue + " isValidateEnabled()="
					+ isValidateEnabled());
		}
		if (_validateButton != null) {
			_validateButton.setEnabled(isValidateEnabled());
		}
		if (!param.getName().equals("failure_message")) {
			update();
		}
		if (isVisible()) {
			// GPO: Crappy code to resize dialog to best fit for param panel
			Dimension prefSize = paramsPanel.getPreferredSize();
			if (paramsPanel.getSize().width < prefSize.width) {
				pack();
			}
			if (paramsPanel.getSize().height < prefSize.height) {
				pack();
			}
		}
	}

	protected final boolean isValidateEnabled() {
		if (_validationCondition != null) {
			return _validationCondition.isValid(_parametersModel);
		}
		return true;
	}

	public void update() {
		paramsPanel.valueChange(_parametersModel);
		paramsPanel.performObserverSwitch(_parametersModel);
	}

	private ValidationCondition _validationCondition = null;

	public static abstract class ValidationCondition {
		protected String errorMessage = FlexoLocalization.localizedForKey("invalid_data");

		public abstract boolean isValid(ParametersModel model);

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String aMessage) {
			errorMessage = aMessage;
		}
	}

	public ValidationCondition getValidationCondition() {
		return _validationCondition;
	}

	public void setValidationCondition(ValidationCondition validationCondition) {
		_validationCondition = validationCondition;
		_validateButton.setEnabled(isValidateEnabled());
		update();
	}

}
