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
package org.openflexo.sgmodule.controller.action;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.generator.action.OverrideWithVersion;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sgmodule.view.popup.SelectFilesPopup;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class OverrideWithVersionInitializer extends ActionInitializer<OverrideWithVersion, CGObject, CGObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	OverrideWithVersionInitializer(FlexoActionType<OverrideWithVersion, CGObject, CGObject> actionType,
			SGControllerActionInitializer actionInitializer) {
		super(actionType, actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() {
		return (SGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<OverrideWithVersion> getDefaultInitializer() {
		return new FlexoActionInitializer<OverrideWithVersion>() {
			@Override
			public boolean run(EventObject e, OverrideWithVersion action) {
				if (action.getFilesToOverride().size() == 0) {
					FlexoController.notify(FlexoLocalization.localizedForKey("no_files_selected"));
					return false;
				} else if (action.getFilesToOverride().size() > 1 || !(action.getFocusedObject() instanceof CGFile)) {

					SelectFilesPopup popup = new SelectFilesPopup(action.getActionType().getLocalizedName(), action.getActionType()
							.getLocalizedDescription(), "override_files_on_disk", action.getFilesToOverride(), action.getFocusedObject()
							.getProject(), getControllerActionInitializer().getSGController()) {
						@Override
						public JPanel getAdditionalPanel() {
							if (_additionalPanel == null) {
								_additionalPanel = new JPanel();
								_additionalPanel.setLayout(new FlowLayout());
								setParam("DO_IT_NOW", Boolean.FALSE);
								final JCheckBox doItNowCheckBox = new JCheckBox(
										FlexoLocalization
												.localizedForKey("check_this_box_if_you_want_override_to_be_immediately_performed"));
								doItNowCheckBox.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										setParam("DO_IT_NOW", doItNowCheckBox.isSelected());
									}
								});
								_additionalPanel.add(doItNowCheckBox);
								_additionalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
							}
							return _additionalPanel;
						}

					};
					popup.setVisible(true);
					if (popup.getStatus() == MultipleObjectSelectorPopup.VALIDATE && popup.getFileSet().getSelectedFiles().size() > 0) {
						action.setDoItNow((Boolean) popup.getParam("DO_IT_NOW"));
						action.setFilesToOverride(popup.getFileSet().getSelectedFiles());
					} else {
						return false;
					}
				} else {
					// 1 occurence, ask confirmation
					String cancelOption = FlexoLocalization.localizedForKey("cancel");
					String doItNowOption = FlexoLocalization.localizedForKey("override_now");
					String reviewOption = FlexoLocalization.localizedForKey("review");
					int choice = FlexoController.selectOption(
							action.getActionType().getLocalizedName() + "\n"
									+ FlexoLocalization.localizedForKey("would_you_like_to_do_it_now"), reviewOption, reviewOption,
							doItNowOption, cancelOption);
					if (choice == 2) {
						return false;
					}
					if (choice == 1) {
						action.setDoItNow(true);
					}
					if (choice == 0) {
						action.setDoItNow(false);
					}
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<OverrideWithVersion> getDefaultFinalizer() {
		return new FlexoActionFinalizer<OverrideWithVersion>() {
			@Override
			public boolean run(EventObject e, OverrideWithVersion action) {
				return true;
			}
		};
	}

}
