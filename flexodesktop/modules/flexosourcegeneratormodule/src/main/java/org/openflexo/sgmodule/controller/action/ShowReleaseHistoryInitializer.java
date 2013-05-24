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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.openflexo.components.AskParametersPanel;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.version.CGRelease;
import org.openflexo.foundation.cg.version.action.ShowReleaseHistory;
import org.openflexo.foundation.param.InfoLabelParameter;
import org.openflexo.foundation.param.PropertyListParameter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class ShowReleaseHistoryInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ShowReleaseHistoryInitializer(SGControllerActionInitializer actionInitializer) {
		super(ShowReleaseHistory.actionType, actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() {
		return (SGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ShowReleaseHistory> getDefaultInitializer() {
		return new FlexoActionInitializer<ShowReleaseHistory>() {
			@Override
			public boolean run(EventObject e, ShowReleaseHistory action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ShowReleaseHistory> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ShowReleaseHistory>() {
			@Override
			public boolean run(EventObject e, ShowReleaseHistory action) {
				InfoLabelParameter infoLabel = new InfoLabelParameter("info", "info", action.getLocalizedDescription(), false);
				final PropertyListParameter<CGRelease> releasesParam = new PropertyListParameter<CGRelease>("releases", "releases", action
						.getFocusedObject().getReleases(), 20, 10);
				releasesParam.addIconColumn("icon", "", 30, false);
				releasesParam.addReadOnlyTextFieldColumn("versionIdentifier.versionAsString", "version", 100, true);
				releasesParam.addReadOnlyTextFieldColumn("name", "name", 150, true);
				releasesParam.addReadOnlyTextFieldColumn("date", "date", 200, true);
				releasesParam.addReadOnlyTextFieldColumn("userId", "user", 100, true);
				InfoLabelParameter descriptionLabel = new InfoLabelParameter("description", "description", "", false) {
					@Override
					public String getValue() {
						return releasesParam.getSelectedObject() != null ? releasesParam.getSelectedObject().getDescription()
								: FlexoLocalization.localizedForKey("no_selection");
					}
				};
				descriptionLabel.setDepends("releases");

				AskParametersPanel panel = new AskParametersPanel(getProject(), infoLabel, releasesParam, descriptionLabel);

				final FlexoDialog dialog = new FlexoDialog(getControllerActionInitializer().getSGController().getFlexoFrame(),
						FlexoLocalization.localizedForKey("release_history_for") + " " + action.getFocusedObject().getName(), false);
				dialog.getContentPane().setLayout(new BorderLayout());
				dialog.getContentPane().add(panel, BorderLayout.CENTER);
				JPanel controlPanel = new JPanel(new FlowLayout());
				JButton button = new JButton();
				button.setText(FlexoLocalization.localizedForKey("close", button));
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}
				});
				controlPanel.add(button);
				dialog.getContentPane().add(controlPanel, BorderLayout.SOUTH);
				dialog.validate();
				dialog.pack();
				dialog.setVisible(true);
				return true;
			}
		};
	}

}
