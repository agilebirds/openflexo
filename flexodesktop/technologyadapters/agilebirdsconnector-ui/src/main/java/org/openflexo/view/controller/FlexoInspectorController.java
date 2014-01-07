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
package org.openflexo.view.controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.commons.io.IOUtils;
import org.openflexo.GeneralPreferences;
import org.openflexo.components.ProgressWindow;
import org.openflexo.drm.DefaultInspectorHelpDelegate;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.HelpDelegate;
import org.openflexo.drm.Language;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.inspector.InspectorController;
import org.openflexo.inspector.InspectorDelegate;
import org.openflexo.inspector.InspectorWidgetConfiguration;
import org.openflexo.inspector.model.InspectorModel;
import org.openflexo.inspector.model.ParamModel;
import org.openflexo.inspector.model.ParametersContainerModelObject;
import org.openflexo.inspector.model.PropertyListAction;
import org.openflexo.inspector.model.PropertyListColumn;
import org.openflexo.inspector.model.PropertyListModel;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.localization.FlexoLocalization;

public abstract class FlexoInspectorController extends InspectorController {

	private static final Logger logger = Logger.getLogger(FlexoInspectorController.class.getPackage().getName());
	private static final InspectorWidgetConfiguration DEFAULT_CONFIGURATION = new InspectorWidgetConfiguration() {

		@Override
		public boolean showViewSourceButtonInWysiwyg() {
			// return UserType.isDevelopperRelease() || UserType.isMaintainerRelease();
			return true;
		}

	};

	protected FlexoInspectorController(FlexoController controller) {
		this(controller.new FlexoControllerInspectorDelegate(), new DefaultInspectorHelpDelegate(DocResourceManager.instance()));
	}

	protected FlexoInspectorController(InspectorDelegate inspectorDelegate, HelpDelegate helpDelegate) {
		super(inspectorDelegate, helpDelegate, DEFAULT_CONFIGURATION);
	}

	/*protected void loadAllCustomInspectors(FlexoProject project) {
		if (project != null) {
			File customInspectorDirectory = project.getCustomInspectorsDirectory();

			// Load custom inspectors
			if (customInspectorDirectory != null) {
				// Load custom inspectors
				ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("load_custom_inspectors"));
				loadInspectors(customInspectorDirectory);
			}

			updateSuperInspectors();
		}
	}*/

	private void loadInspectors(File directory) {
		File[] files = directory.listFiles();

		int steps = files.length;
		ProgressWindow.resetSecondaryProgressInstance(steps);

		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".inspector")) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Loading " + files[i].getName() + "...");
				}
				ProgressWindow
						.setSecondaryProgressInstance(FlexoLocalization.localizedForKey("loading") + " " + files[i].getName() + "...");
				// String inspectorName =
				// files[i].getName().substring(files[i].getName().lastIndexOf(".inspector"));
				try {
					importInspectorFile(files[i]);
				} catch (FileNotFoundException e) {
					logger.warning("File NOT FOUND: " + files[i]);
				}
			}
		}
		updateSuperInspectors();

	}

	protected void loadInspectors(InspectorGroup inspectorGroup) {
		if (inspectorGroup != null) {

			for (InspectorGroup currentInspectorGroup : new InspectorGroup[] { Inspectors.COMMON, inspectorGroup }) {

				List<String> inspectorsToLoad = currentInspectorGroup.getAllInspectorNames();
				Hashtable<String, InspectorModel> loadedInspectors = new Hashtable<String, InspectorModel>();

				int steps = inspectorsToLoad.size();
				ProgressWindow.resetSecondaryProgressInstance(steps);

				for (String inspectorName : inspectorsToLoad) {
					if (inspectorName.endsWith(".inspector")) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Loading " + inspectorName + "...");
						}
						ProgressWindow.setSecondaryProgressInstance(FlexoLocalization.localizedForKey("loading") + " " + inspectorName
								+ "...");
						String cleanedInspectorName = inspectorName.substring(0, inspectorName.length() - ".inspector".length());
						if (loadedInspectors.get(cleanedInspectorName) == null) {
							InputStream inputStream = currentInspectorGroup.getInspectorStream(inspectorName);
							if (inputStream != null) {
								try {
									loadedInspectors.put(cleanedInspectorName, importInspector(inspectorName, inputStream));
								} finally {
									IOUtils.closeQuietly(inputStream);
								}
							}
						}
					}
				}
				updateSuperInspectors();

				for (Enumeration<String> en = loadedInspectors.keys(); en.hasMoreElements();) {
					String inspectorName = en.nextElement();
					InspectorModel inspectorModel = loadedInspectors.get(inspectorName);
					inspectorModel.inspectorName = inspectorName;
					DocResourceManager.instance().importInspector(currentInspectorGroup, inspectorName, inspectorModel);
					cleanInspectorModel(inspectorModel);
				}
			}
		}
	}

	/**
	 * @param inspectorModel
	 */
	public static void cleanInspectorModel(InspectorModel inspectorModel) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("cleanInspectorModel " + inspectorModel.inspectorName);
		}
		Language language = DocResourceManager.instance().getLanguage(GeneralPreferences.getLanguage());
		for (Enumeration<PropertyModel> en2 = inspectorModel.getAllPropertyModel().elements(); en2.hasMoreElements();) {
			PropertyModel propertyModel = en2.nextElement();
			Enumeration<String> en = new Hashtable<String, ParamModel>(propertyModel.parameters).keys();
			while (en.hasMoreElements()) {
				ParamModel p = propertyModel.parameters.get(en.nextElement());
				if (p == null) {
					continue;
				}
				cleanParameters(propertyModel.parameters, p);
			}
			if (propertyModel instanceof PropertyListModel) {
				PropertyListModel plm = (PropertyListModel) propertyModel;
				Iterator<PropertyListAction> i = plm.getActions().iterator();
				while (i.hasNext()) {
					PropertyListAction pla = i.next();
					Enumeration<String> en1 = new Hashtable<String, ParamModel>(pla.parameters).keys();
					while (en1.hasMoreElements()) {
						ParamModel p1 = pla.parameters.get(en1.nextElement());
						if (p1 == null) {
							continue;
						}
						cleanParameters(pla.parameters, p1);
					}
					if (parametersContainerIsDisplayable(pla, UserType.getCurrentUserType())) {
						// Let's keep it
					} else {
						i.remove();
					}
				}
				Iterator<PropertyListColumn> j = plm.getColumns().iterator();
				while (j.hasNext()) {
					PropertyListColumn plc = j.next();
					Enumeration<String> en1 = new Hashtable<String, ParamModel>(plc.parameters).keys();
					while (en1.hasMoreElements()) {
						ParamModel p1 = plc.parameters.get(en1.nextElement());
						if (p1 == null) {
							continue;
						}
						cleanParameters(plc.parameters, p1);
					}
					if (parametersContainerIsDisplayable(plc, UserType.getCurrentUserType())) {
						// Let's keep it
					} else {
						j.remove();
					}
				}
			}
			if (parametersContainerIsDisplayable(propertyModel, UserType.getCurrentUserType())) {
				DocItem propertyModelItem = DocResourceManager.instance().getDocItemFor(propertyModel);
				if (propertyModelItem != null) {
					if (propertyModelItem.getLastApprovedActionForLanguage(language) != null) {
						propertyModel.help = "<html>"
								+ propertyModelItem.getLastApprovedActionForLanguage(language).getVersion().getShortHTMLDescription()
								+ "</html>";
						logger.fine("Set text for" + propertyModel.name + " to be " + propertyModel.help);
					}
				}
			} else {
				// This property should not be displayed
				logger.fine("Remove property " + propertyModel.name);
				propertyModel.getTabModel().removePropertyWithKey(propertyModel.name);
			}
		}
	}

	/**
	 * @param parameters
	 */
	private static void cleanParameters(Hashtable<String, ParamModel> parameters, ParamModel paramModel) {
		Hashtable<String, ParamModel> clone = new Hashtable<String, ParamModel>(paramModel.parameters);
		Enumeration<String> en = clone.keys();
		while (en.hasMoreElements()) {
			ParamModel p = paramModel.parameters.get(en.nextElement());
			if (p == null) {
				continue;
			}
			if (p.parameters.size() > 0) {
				cleanParameters(paramModel.parameters, p);
			}
			if (parametersContainerIsDisplayable(paramModel, UserType.getCurrentUserType())) {
				// Let's keep it
			} else {
				parameters.remove(paramModel.name);
			}
		}
	}

	private static boolean parametersContainerIsDisplayable(ParametersContainerModelObject paramContainer, UserType userType) {
		if (paramContainer.hasValueForParameter("visibleFor")) {
			if (!evaluateVisibleCondition(paramContainer, userType)) {
				return false;
			}
		}
		if (paramContainer.hasValueForParameter("hiddenFor")) {
			if (!evaluateHiddenCondition(paramContainer, userType)) {
				return false;
			}
		}
		return true;
	}

	private static boolean evaluateVisibleCondition(ParametersContainerModelObject paramContainer, UserType userType) {
		String condition = paramContainer.getValueForParameter("visibleFor");
		StringTokenizer st = new StringTokenizer(condition, ",");
		String token;
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (token.equalsIgnoreCase("ALL")) {
				return true;
			}
			UserType nextUT = UserType.getUserTypeNamed(token);
			if (nextUT != null && nextUT.equals(userType)) {
				return true;
			}
		}
		return false;
	}

	private static boolean evaluateHiddenCondition(ParametersContainerModelObject paramContainer, UserType userType) {
		String condition = paramContainer.getValueForParameter("hiddenFor");
		StringTokenizer st = new StringTokenizer(condition, ",");
		String token;
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (token.equalsIgnoreCase("ALL")) {
				return false;
			}
			UserType nextUT = UserType.getUserTypeNamed(token);
			if (nextUT != null && nextUT.equals(userType)) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		final JDialog dialog = new JDialog(frame);
		dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		JButton button = new JButton("Show dialog");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(true);
			}
		});
		JLabel label = new JLabel("coucou");
		dialog.add(label);
		dialog.pack();
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(button);
		panel.setPreferredSize(new Dimension(500, 400));
		frame.add(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
