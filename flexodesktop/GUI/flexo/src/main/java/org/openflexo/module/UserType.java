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
package org.openflexo.module;

import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.InspectorGroup;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.BasicInteractiveProjectLoadingHandler;
import org.openflexo.view.controller.FullInteractiveProjectLoadingHandler;

/**
 * Represents a user type, and is used to determine configuration of Flexo Application Suite, which is also called a package set
 * 
 * @author sguerin
 */
public abstract class UserType extends FlexoObject {

	public static final String PRODUCT_NAME = "OPENFLEXO";

	/*protected static final File CUSTOMER_LICENCE = new FileResource("License/Customer/License.pdf");
	protected static final File ANALYST_LICENCE = new FileResource("License/Analyst/License.pdf");
	protected static final File DEVELOPER_LICENCE = new FileResource("License/Developer/License.pdf");*/

	private static final Logger logger = Logger.getLogger(Module.class.getPackage().getName());

	public static UserType getUserTypeNamed(String userTypeName) {
		if (MAINTAINER.getName().equalsIgnoreCase(userTypeName)) {
			return MAINTAINER;
		}
		if (ANALYST.getName().equalsIgnoreCase(userTypeName)) {
			return ANALYST;
		}
		if (DEVELOPER.getName().equalsIgnoreCase(userTypeName)) {
			return DEVELOPER;
		}
		if (CUSTOMER.getName().equalsIgnoreCase(userTypeName)) {
			return CUSTOMER;
		}
		if (MAINTAINER.getIdentifier().equalsIgnoreCase(userTypeName)) {
			return MAINTAINER;
		}
		if (ANALYST.getIdentifier().equalsIgnoreCase(userTypeName)) {
			return ANALYST;
		}
		if (DEVELOPER.getIdentifier().equalsIgnoreCase(userTypeName)) {
			return DEVELOPER;
		}
		if (CUSTOMER.getIdentifier().equalsIgnoreCase(userTypeName)) {
			return CUSTOMER;
		}
		return MAINTAINER;
	}

	public static final Developer DEVELOPER = new Developer();

	public static final Analyst ANALYST = new Analyst();

	public static final Customer CUSTOMER = new Customer();

	public static final Maintainer MAINTAINER = new Maintainer();

	private Vector<DocItemFolder> documentationFolders = null;

	public Vector<DocItemFolder> getDocumentationFolders() {
		if (documentationFolders == null) {
			documentationFolders = new Vector<DocItemFolder>();
			documentationFolders.add(DocResourceManager.instance().getAbstractModuleItem().getFolder());
			addModelItems();
			for (Module module : ModuleLoader.allKnownModules()) {
				if (module.getModuleClass() != null) {
					addModuleItems(module);
				}
			}
		}
		return documentationFolders;
	}

	protected void addModelItems(InspectorGroup inspectorGroup) {
		DocItemFolder inspectorGroupFolder;
		DocItemFolder modelFolder = DocResourceManager.instance().getDocResourceCenter().getModelFolder();
		String inspectorGroupName = inspectorGroup.getName();
		inspectorGroupFolder = modelFolder.getItemFolderNamed(inspectorGroupName);
		if (inspectorGroupFolder != null) {
			documentationFolders.add(inspectorGroupFolder);
		} else {
			logger.warning("Unable to find DocItemFolder for " + inspectorGroupName);
		}
	}

	protected void addModuleItems(Module module) {
		DocItemFolder moduleItemFolder;
		DocItemFolder ftsFolder = DocResourceManager.instance().getDocResourceCenter().getFTSFolder();
		moduleItemFolder = ftsFolder.getItemFolderNamed(module.getHelpTopic());
		if (moduleItemFolder != null) {
			documentationFolders.add(moduleItemFolder);
		} else {
			logger.warning("Unable to find DocItemFolder for " + module.getHelpTopic());
		}
	}

	protected abstract void addModelItems();

	public abstract ImageIcon getIconImage();

	public static class Developer extends UserType {

		@Override
		public String getName() {
			return "developer_release";
		}

		@Override
		public String getIdentifier() {
			return "DEVELOPER";
		}

		@Override
		protected void addModelItems() {
			addModelItems(Inspectors.COMMON);
			addModelItems(Inspectors.WKF);
			addModelItems(Inspectors.IE);
			addModelItems(Inspectors.DM);
			addModelItems(Inspectors.WSE);
		}

		@Override
		public ProjectLoadingHandler getDefaultLoadingHandler(File projectDirectory) {
			return new FullInteractiveProjectLoadingHandler(projectDirectory);
		}

		@Override
		public String getBusinessName1() {
			return PRODUCT_NAME;
		}

		@Override
		public String getBusinessName2() {
			return "Enterprise edition";
		}

		/*@Override
		public File getLicenceFile() {
			return DEVELOPER_LICENCE;
		}*/

		@Override
		public ImageIcon getIconImage() {
			return IconLibrary.ENTERPRISE_32_ICON;
		}

	}

	protected static class Analyst extends UserType {

		@Override
		public String getName() {
			return "analyst_release";
		}

		@Override
		public String getIdentifier() {
			return "ANALYST";
		}

		@Override
		protected void addModelItems() {
			addModelItems(Inspectors.COMMON);
			addModelItems(Inspectors.WKF);
			addModelItems(Inspectors.IE);
			addModelItems(Inspectors.DM);
		}

		@Override
		public ProjectLoadingHandler getDefaultLoadingHandler(File projectDirectory) {
			return new BasicInteractiveProjectLoadingHandler(projectDirectory);
		}

		@Override
		public String getBusinessName1() {
			return PRODUCT_NAME;
		}

		@Override
		public String getBusinessName2() {
			return "Business+ edition";
		}

		/*@Override
		public File getLicenceFile() {
			return ANALYST_LICENCE;
		}*/

		@Override
		public ImageIcon getIconImage() {
			return IconLibrary.BUSINESS_PLUS_32_ICON;
		}

	}

	protected static class Customer extends UserType {
		@Override
		public String getName() {
			return "customer_release";
		}

		@Override
		public String getIdentifier() {
			return "CUSTOMER";
		}

		@Override
		protected void addModelItems() {
			addModelItems(Inspectors.COMMON);
			addModelItems(Inspectors.WKF);
			addModelItems(Inspectors.IE);
		}

		@Override
		public ProjectLoadingHandler getDefaultLoadingHandler(File projectDirectory) {
			return new BasicInteractiveProjectLoadingHandler(projectDirectory);
		}

		@Override
		public String getBusinessName1() {
			return PRODUCT_NAME;
		}

		@Override
		public String getBusinessName2() {
			return "Business edition";
		}

		/*@Override
		public File getLicenceFile() {
			return CUSTOMER_LICENCE;
		}*/

		@Override
		public ImageIcon getIconImage() {
			return IconLibrary.BUSINESS_32_ICON;
		}

	}

	public static class Maintainer extends UserType {
		@Override
		public String getName() {
			return "maintainer_release";
		}

		@Override
		public String getIdentifier() {
			return "MAINTAINER";
		}

		@Override
		protected void addModelItems() {
			addModelItems(Inspectors.COMMON);
			addModelItems(Inspectors.WKF);
			addModelItems(Inspectors.IE);
			addModelItems(Inspectors.DM);
			addModelItems(Inspectors.WSE);
			addModelItems(Inspectors.DRE);
		}

		@Override
		public ProjectLoadingHandler getDefaultLoadingHandler(File projectDirectory) {
			return new FullInteractiveProjectLoadingHandler(projectDirectory);
		}

		@Override
		public String getBusinessName1() {
			return PRODUCT_NAME;
		}

		@Override
		public String getBusinessName2() {
			return "Enterprise edition";
		}

		/*@Override
		public File getLicenceFile() {
			return DEVELOPER_LICENCE;
		}*/

		@Override
		public ImageIcon getIconImage() {
			return IconLibrary.ENTERPRISE_32_ICON;
		}

	}

	public abstract String getName();

	public abstract String getIdentifier();

	public abstract ProjectLoadingHandler getDefaultLoadingHandler(File projectDirectory);

	public abstract String getBusinessName1();

	public abstract String getBusinessName2();

	// public abstract File getLicenceFile();

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	private static final UserType[] knownsUserType = { CUSTOMER, ANALYST, DEVELOPER, MAINTAINER };

	public static Vector<UserType> allKnownUserType() {
		Vector<UserType> returned = new Vector<UserType>();
		for (int i = 0; i < knownsUserType.length; i++) {
			returned.add(knownsUserType[i]);
		}
		if (returned.size() == 0) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("No USERTYPE found.");
			}
		}
		return returned;
	}

}
