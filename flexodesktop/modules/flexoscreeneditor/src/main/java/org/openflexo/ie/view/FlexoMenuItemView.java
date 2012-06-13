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
package org.openflexo.ie.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.JTextFieldRegExp;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

public class FlexoMenuItemView extends JPanel implements ModuleView<FlexoItemMenu> {

	private static final Logger logger = Logger.getLogger(FlexoMenuItemView.class.getPackage().getName());

	protected FlexoItemMenu _model;

	protected boolean isUpdatingModel = false;

	protected IEController controller;

	private TargetOperationSelector operationSelector;

	public FlexoMenuItemView(FlexoItemMenu model, IEController ctrl) {
		super(new VerticalLayout(5, 0, 0));
		_model = model;
		this.controller = ctrl;
		add(new MenuLabelPanel());
		add(operationSelector = new TargetOperationSelector(this));
	}

	private class MenuLabelPanel extends JPanel implements DocumentListener, FlexoObserver {
		protected JTextField labelField;

		private boolean isUpdatingMenuLabel = false;

		protected String oldValue;

		public MenuLabelPanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			add(new JLabel("Label"));
			oldValue = _model.getMenuLabel() != null ? _model.getMenuLabel() : FlexoLocalization.localizedForKey("menu_item");
			labelField = new JTextField(_model.getMenuLabel());
			labelField.setPreferredSize(new Dimension(200, 24));
			labelField.getDocument().addDocumentListener(this);
			labelField.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {

				}

				@Override
				public void focusLost(FocusEvent e) {
					if (labelField.getText() == null || labelField.getText().trim().length() == 0) {
						String menuLabel = FlexoController.askForStringMatchingPattern(
								FlexoLocalization.localizedForKey("enter_label_for_the_new_menu"), Pattern.compile("\\S.*"),
								FlexoLocalization.localizedForKey("cannot_be_empty"));
						if (menuLabel == null) {
							_model.setMenuLabel(oldValue);
						} else {
							_model.setMenuLabel(menuLabel);
						}
						labelField.setText(_model.getMenuLabel());

					} else if (_model.getNavigationMenu().getMenuLabeled(labelField.getText()) != null
							&& _model.getNavigationMenu().getMenuLabeled(labelField.getText()) != _model) {
						FlexoController.notify(FlexoLocalization.localizedForKey("a_menu_with_such_label_already_exists"));
					}
				}

			});
			add(labelField);
			_model.addObserver(this);
			TitledBorder b = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder());
			b.setTitle(FlexoLocalization.localizedForKey("label", b));
			setBorder(b);
		}

		private void updateMenuLabel() {
			isUpdatingMenuLabel = true;
			_model.setMenuLabel(labelField.getText());
			isUpdatingMenuLabel = false;
		}

		private void validateLabel() {
			if (labelField.getText() == null || labelField.getText().trim().length() == 0) {
				labelField.setBackground(JTextFieldRegExp.ERROR_BORDER);
			} else {
				labelField.setBackground(JTextFieldRegExp.DEFAULT_BORDER);
			}
		}

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			updateMenuLabel();
			validateLabel();
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			updateMenuLabel();
			validateLabel();
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			updateMenuLabel();
			validateLabel();
		}

		/**
		 * Overrides update
		 * 
		 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
		 *      org.openflexo.foundation.DataModification)
		 */
		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			if (observable == _model) {
				if (dataModification.propertyName() != null && dataModification.propertyName().equals("menuLabel")) {
					if (!isUpdatingMenuLabel) {
						labelField.setText(_model.getMenuLabel());
					}
				}
			}
		}
	}

	@Override
	public FlexoItemMenu getRepresentedObject() {
		return _model;
	}

	@Override
	public void deleteModuleView() {
		controller.removeModuleView(this);
		logger.warning("Not implemented !");
	}

	@Override
	public FlexoPerspective getPerspective() {
		return controller.MENU_EDITOR_PERSPECTIVE;
	}

	/**
	 * Overrides willShow
	 * 
	 * @see org.openflexo.view.ModuleView#willShow()
	 */
	@Override
	public void willShow() {
		operationSelector.refresh();
	}

	/**
	 * Overrides willHide
	 * 
	 * @see org.openflexo.view.ModuleView#willHide()
	 */
	@Override
	public void willHide() {

	}

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management When not, Flexo will manage it's own scrollbar for
	 * you
	 * 
	 * @return
	 */
	@Override
	public boolean isAutoscrolled() {
		return false;
	}

}
