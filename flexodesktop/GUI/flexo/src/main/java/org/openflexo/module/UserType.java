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
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
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

	private static final Logger logger = Logger.getLogger(Module.class.getPackage().getName());

	private static UserType currentUserType = null;

	public static final Developer DEVELOPER = new Developer();

	public static final Analyst ANALYST = new Analyst();

	public static final Customer CUSTOMER = new Customer();

	public static final Maintainer MAINTAINER = new Maintainer();

	public static final SemanticsUser SEMANTICS_USER = new SemanticsUser();

	public static final SemanticsPlusUser SEMANTICS_PLUS_USER = new SemanticsPlusUser();

	private static final UserType[] knownUserType = { CUSTOMER, ANALYST, DEVELOPER, MAINTAINER, SEMANTICS_USER, SEMANTICS_PLUS_USER };

	private Vector<DocItemFolder> documentationFolders = null;

	/**
	 * @return the current UserType. Never return null.
	 * @throws IllegalStateException
	 *             if the current userType is not set.
	 */
	public static final UserType getCurrentUserType() {
		if (currentUserType == null) {
			throw new IllegalStateException("currentUserType is null. Did you call setCurrentUserType.");
		}
		return currentUserType;
	}

	/**
	 * Define the global application parameter currentUserType. Once the userType is defined : it cannot be changed.
	 * 
	 * @param userType
	 *            : the userType to set. Cannot be null.
	 * @throws IllegalArgumentException
	 *             if you pass a null userType.
	 * @throws IllegalStateException
	 *             if you try to change the current userType.
	 */
	public static final void setCurrentUserType(UserType userType) {
		if (userType == null) {
			throw new IllegalArgumentException("userType cannot be null.");
		}
		if (currentUserType != null) {
			if (!currentUserType.equals(userType)) {
				throw new IllegalStateException("You cannot change userType. It was " + currentUserType.getName()
						+ " and you try to change it to " + userType.getName() + ". Ignoring this change.");
			} else {
				logger.warning("Trying to set the currentUser " + userType.getName() + ", but it was already set.");
			}
		}
		currentUserType = userType;
	}

	/**
	 * @return if currentUserType is defined.
	 */
	public static boolean isCurrentUserTypeDefined() {
		return currentUserType != null;
	}

	/**
	 * @return if currentUserType is Customer
	 */
	public static boolean isCustomerRelease() {
		return CUSTOMER == currentUserType;
	}

	/**
	 * @return if currentUserType is Analyst
	 */
	public static boolean isAnalystRelease() {
		return ANALYST == currentUserType;
	}

	/**
	 * @return if currentUserType is Developer
	 */
	public static boolean isDevelopperRelease() {
		return DEVELOPER == currentUserType;
	}

	/**
	 * @return if currentUserType is Maintainer
	 */
	public static boolean isMaintainerRelease() {
		return MAINTAINER == currentUserType;
	}

	/**
	 * Search a userType matching userTypeName. Make a case insensitive comparison against known userType name's and known userType id's. If
	 * there is no match it returns Maintainer user type.
	 * 
	 * @param userTypeName
	 *            a string matching either a userType's name, either a userType's id
	 * @return the userType matching userTypeName. Maintainer userType whenever there is no match
	 */
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
		if (SEMANTICS_USER.getName().equalsIgnoreCase(userTypeName)) {
			return SEMANTICS_USER;
		}
		if (SEMANTICS_PLUS_USER.getName().equalsIgnoreCase(userTypeName)) {
			return SEMANTICS_PLUS_USER;
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
		if (SEMANTICS_USER.getIdentifier().equalsIgnoreCase(userTypeName)) {
			return SEMANTICS_USER;
		}
		if (SEMANTICS_PLUS_USER.getIdentifier().equalsIgnoreCase(userTypeName)) {
			return SEMANTICS_PLUS_USER;
		}
		return MAINTAINER;
	}

	public Vector<DocItemFolder> getDocumentationFolders() {
		if (documentationFolders == null) {
			documentationFolders = new Vector<DocItemFolder>();
			documentationFolders.add(DocResourceManager.instance().getAbstractModuleItem().getFolder());
			addModelItems();
			for (Module module : getModules()) {
				if (module.getModuleClass() != null) {
					addModuleItems(module);
				}
			}
		}
		return documentationFolders;
	}

	private ModuleLoader getModuleLoader() {
		return ModuleLoader.instance();
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

	public abstract List<Module> getModules();

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
		public List<Module> getModules() {
			return Arrays.asList(Module.WKF_MODULE, Module.IE_MODULE, Module.DE_MODULE, Module.VE_MODULE, Module.DM_MODULE,
					Module.CG_MODULE, Module.SG_MODULE, Module.DG_MODULE, Module.WSE_MODULE, Module.FPS_MODULE, Module.VPM_MODULE);
		}

		@Override
		protected void addModelItems() {
			addModelItems(Inspectors.COMMON);
			addModelItems(Inspectors.WKF);
			addModelItems(Inspectors.IE);
			addModelItems(Inspectors.DM);
			addModelItems(Inspectors.WSE);
			addModelItems(Inspectors.VE);
		}

		@Override
		public ProjectLoadingHandler getDefaultLoadingHandler(File projectDirectory) {
			return new FullInteractiveProjectLoadingHandler(projectDirectory);
		}

		@Override
		public String getBusinessName2() {
			return "Enterprise edition";
		}

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
		public List<Module> getModules() {
			return Arrays.asList(Module.WKF_MODULE, Module.IE_MODULE, Module.DM_MODULE, Module.DE_MODULE, Module.VE_MODULE);
		}

		@Override
		protected void addModelItems() {
			addModelItems(Inspectors.COMMON);
			addModelItems(Inspectors.WKF);
			addModelItems(Inspectors.IE);
			addModelItems(Inspectors.DM);
			addModelItems(Inspectors.VE);
		}

		@Override
		public ProjectLoadingHandler getDefaultLoadingHandler(File projectDirectory) {
			return new BasicInteractiveProjectLoadingHandler(projectDirectory);
		}

		@Override
		public String getBusinessName2() {
			return "Business+ edition";
		}

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
		public List<Module> getModules() {
			return Arrays.asList(Module.WKF_MODULE, Module.IE_MODULE, Module.DE_MODULE, Module.VE_MODULE);
		}

		@Override
		protected void addModelItems() {
			addModelItems(Inspectors.COMMON);
			addModelItems(Inspectors.WKF);
			addModelItems(Inspectors.IE);
			addModelItems(Inspectors.VE);
		}

		@Override
		public ProjectLoadingHandler getDefaultLoadingHandler(File projectDirectory) {
			return new BasicInteractiveProjectLoadingHandler(projectDirectory);
		}

		@Override
		public String getBusinessName2() {
			return "Business edition";
		}

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
		public List<Module> getModules() {
			return Arrays.asList(Module.WKF_MODULE, Module.IE_MODULE, Module.DE_MODULE, Module.VE_MODULE, Module.DM_MODULE,
					Module.CG_MODULE, Module.SG_MODULE, Module.DG_MODULE, Module.WSE_MODULE, Module.FPS_MODULE, Module.DRE_MODULE,
					Module.VPM_MODULE);
		}

		@Override
		protected void addModelItems() {
			addModelItems(Inspectors.COMMON);
			addModelItems(Inspectors.WKF);
			addModelItems(Inspectors.IE);
			addModelItems(Inspectors.DM);
			addModelItems(Inspectors.WSE);
			addModelItems(Inspectors.DRE);
			addModelItems(Inspectors.VE);
		}

		@Override
		public ProjectLoadingHandler getDefaultLoadingHandler(File projectDirectory) {
			return new FullInteractiveProjectLoadingHandler(projectDirectory);
		}

		@Override
		public String getBusinessName2() {
			return "Enterprise edition";
		}

		@Override
		public ImageIcon getIconImage() {
			return IconLibrary.ENTERPRISE_32_ICON;
		}

	}

	public static class SemanticsUser extends UserType {
		@Override
		public String getName() {
			return "semantics_release";
		}

		@Override
		public String getIdentifier() {
			return "SEMANTICS";
		}

		@Override
		public List<Module> getModules() {
			return Arrays.asList(Module.VE_MODULE);
		}

		@Override
		protected void addModelItems() {
			addModelItems(Inspectors.COMMON);
			addModelItems(Inspectors.VE);
		}

		@Override
		public ProjectLoadingHandler getDefaultLoadingHandler(File projectDirectory) {
			return new FullInteractiveProjectLoadingHandler(projectDirectory);
		}

		@Override
		public String getBusinessName2() {
			return "Semantics edition";
		}

		@Override
		public ImageIcon getIconImage() {
			return IconLibrary.ENTERPRISE_32_ICON;
		}

	}

	public static class SemanticsPlusUser extends UserType {
		@Override
		public String getName() {
			return "semantics_plus_release";
		}

		@Override
		public String getIdentifier() {
			return "SEMANTICSPLUS";
		}

		@Override
		public List<Module> getModules() {
			return Arrays.asList(Module.VE_MODULE, Module.VPM_MODULE);
		}

		@Override
		protected void addModelItems() {
			addModelItems(Inspectors.COMMON);
			addModelItems(Inspectors.VE);
			addModelItems(Inspectors.VPM);
		}

		@Override
		public ProjectLoadingHandler getDefaultLoadingHandler(File projectDirectory) {
			return new FullInteractiveProjectLoadingHandler(projectDirectory);
		}

		@Override
		public String getBusinessName2() {
			return "Semantics+ edition";
		}

		@Override
		public ImageIcon getIconImage() {
			return IconLibrary.ENTERPRISE_32_ICON;
		}

	}

	public abstract String getName();

	public abstract String getIdentifier();

	public abstract ProjectLoadingHandler getDefaultLoadingHandler(File projectDirectory);

	public abstract String getBusinessName2();

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	public static Vector<UserType> allKnownUserType() {
		return new Vector(Arrays.asList(knownUserType));
	}

}
