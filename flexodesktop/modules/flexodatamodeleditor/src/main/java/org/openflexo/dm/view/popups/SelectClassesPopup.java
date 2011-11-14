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
package org.openflexo.dm.view.popups;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMSet;
import org.openflexo.foundation.dm.DMSet.PackageReference;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoFrame;

public class SelectClassesPopup extends MultipleObjectSelectorPopup {

	static final Logger logger = Logger.getLogger(SelectClassesPopup.class.getPackage().getName());

	public SelectClassesPopup(String title, String label, String description, File jarFile, FlexoProject project, FlexoFrame owner,
			FlexoProgress progress) {
		super(title, label, description, new SelectClassPopupBrowserConfiguration(jarFile, project, progress), project, owner, owner
				.getController().getEditor());
	}

	public SelectClassesPopup(String title, String label, String description, ExternalRepository externalRepository, FlexoProject project,
			DMController controller, FlexoProgress progress) {
		super(title, label, description, new SelectClassPopupBrowserConfiguration(externalRepository, project, progress), project,
				controller.getFlexoFrame(), controller.getEditor());
		choicePanel.setSelectedObjects(getDMSet().getSelectedObjects());
	}

	public SelectClassesPopup(String title, String label, String description, Vector entities, FlexoProject project,
			DMController controller, FlexoProgress progress) {
		super(title, label, description, new SelectClassPopupBrowserConfiguration(entities, project, progress), project, controller
				.getFlexoFrame(), controller.getEditor());
		choicePanel.setSelectedObjects(getDMSet().getSelectedObjects());
		/*for (Enumeration en=entities.elements(); en.hasMoreElements();) {
		    LoadableDMEntity next = (LoadableDMEntity)en.nextElement();
		      ClassReference classReference = getDMSet().getClassReference(next.getType());
		      if (classReference.isSelected()) {
		          logger.info("expand:  "+classReference);
		          choicePanel.getTreeTable().getProjectBrowser().expand(classReference);
		          choicePanel.addToSelected(classReference);
		      }
		 }*/
	}

	private JPanel _additionalPanel;
	JCheckBox _importGetOnlyProperties;
	JCheckBox _importMethods;

	@Override
	public JPanel getAdditionalPanel() {
		if (_additionalPanel == null) {
			_additionalPanel = new JPanel();
			_additionalPanel.setLayout(new BorderLayout());
			_importGetOnlyProperties = new JCheckBox(FlexoLocalization.localizedForKey("import_get_only_properties"));
			_importGetOnlyProperties.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getDMSet().setImportGetOnlyProperties(_importGetOnlyProperties.isSelected());
				}
			});
			_importMethods = new JCheckBox(FlexoLocalization.localizedForKey("import_methods"));
			_importMethods.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getDMSet().setImportMethods(_importMethods.isSelected());
				}
			});
			_additionalPanel.add(_importGetOnlyProperties, BorderLayout.NORTH);
			_additionalPanel.add(_importMethods, BorderLayout.SOUTH);
			_additionalPanel.setBorder(BorderFactory.createEmptyBorder(10, 150, 10, 10));
		}
		return _additionalPanel;
	}

	public DMSet getDMSet() {
		return ((SelectClassPopupBrowserConfiguration) getBrowserConfiguration()).dmSet;
	}

	protected static class SelectClassPopupBrowserConfiguration implements BrowserConfiguration {
		protected DMSet dmSet;
		private SelectClassBrowserElementFactory _factory;
		private FlexoProject _project;

		protected SelectClassPopupBrowserConfiguration(File jarFile, FlexoProject project, FlexoProgress progress) {
			_project = project;
			dmSet = new DMSet(project, jarFile, false, progress);
			_factory = new SelectClassBrowserElementFactory();
		}

		protected SelectClassPopupBrowserConfiguration(ExternalRepository externalRepository, FlexoProject project, FlexoProgress progress) {
			_project = project;
			dmSet = new DMSet(project, externalRepository, false, progress);
			_factory = new SelectClassBrowserElementFactory();
		}

		protected SelectClassPopupBrowserConfiguration(Vector entities, FlexoProject project, FlexoProgress progress) {
			_project = project;
			dmSet = new DMSet(project, "classes_selection", entities, false, progress);
			_factory = new SelectClassBrowserElementFactory();
		}

		@Override
		public FlexoProject getProject() {
			return _project;
		}

		@Override
		public void configure(ProjectBrowser browser) {
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return dmSet;
		}

		@Override
		public BrowserElementFactory getBrowserElementFactory() {
			return _factory;
		}

		class SelectClassBrowserElementFactory implements BrowserElementFactory {
			@Override
			public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent) {
				if (object instanceof DMSet) {
					return new ImportedClassSetElement((DMSet) object, browser, parent);
				} else if (object instanceof PackageReference) {
					return new ImportedPackageElement((PackageReference) object, browser, parent);
				} else if (object instanceof ClassReference) {
					return new ImportedClassElement((ClassReference) object, browser, parent);
				}
				return null;
			}

			private class ImportedClassSetElement extends BrowserElement {
				public ImportedClassSetElement(DMSet classSet, ProjectBrowser browser, BrowserElement parent) {
					super(classSet, BrowserElementType.DM_REPOSITORY, browser, parent);
				}

				@Override
				protected void buildChildrenVector() {
					for (Enumeration e = ((DMSet) getObject()).getPackages(); e.hasMoreElements();) {
						PackageReference next = (PackageReference) e.nextElement();
						addToChilds(next);
					}
				}

				@Override
				public String getName() {
					return ((DMSet) getObject()).getName();
				}
			}

			private class ImportedPackageElement extends BrowserElement {
				public ImportedPackageElement(PackageReference aPackage, ProjectBrowser browser, BrowserElement parent) {
					super(aPackage, BrowserElementType.DM_PACKAGE, browser, parent);
				}

				@Override
				protected void buildChildrenVector() {
					for (Enumeration e = ((PackageReference) getObject()).getClassesEnumeration(); e.hasMoreElements();) {
						ClassReference next = (ClassReference) e.nextElement();
						addToChilds(next);
					}
				}

				@Override
				public String getName() {
					return ((PackageReference) getObject()).getName();
				}
			}

			private class ImportedClassElement extends BrowserElement {
				public ImportedClassElement(ClassReference aClass, ProjectBrowser browser, BrowserElement parent) {
					super(aClass, BrowserElementType.DM_ENTITY, browser, parent);
				}

				@Override
				protected void buildChildrenVector() {
				}

				@Override
				public String getName() {
					return ((ClassReference) getObject()).getName();
				}

			}

		}

	}

	@Override
	public void performConfirm() {
		super.performConfirm();
		getDMSet().setSelectedObjects(choicePanel.getSelectedObjects());
	}

}
