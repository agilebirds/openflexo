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

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.GeneralPreferences;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.Language;
import org.openflexo.icon.CGIconLibrary;
import org.openflexo.icon.DEIconLibrary;
import org.openflexo.icon.DGIconLibrary;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.icon.DREIconLibrary;
import org.openflexo.icon.FPSIconLibrary;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.icon.SGIconLibrary;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.icon.WSEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.external.IModule;
import org.openflexo.view.controller.InteractiveFlexoEditor;

/**
 * Represents a Module a Flexo Application Suite
 * 
 * NB: this represents a module, not a module instance !
 * 
 * @author sguerin
 * 
 */
public abstract class Module implements IModule {

	private static final Logger logger = Logger.getLogger(Module.class.getPackage().getName());

	public static final Module WKF_MODULE = new WKF();

	public static final Module IE_MODULE = new IE();

	public static final Module DM_MODULE = new DM();

	public static final Module CG_MODULE = new CG();

	public static final Module DG_MODULE = new DG();

	public static final Module DE_MODULE = new DE();

	public static final Module WSE_MODULE = new WSE();

	public static final Module VE_MODULE = new VE();

	public static final Module DRE_MODULE = new DRE();

	public static final Module FPS_MODULE = new FPS();

	public static final Module VPM_MODULE = new VPM();

	public static final Module SG_MODULE = new SG();

	public static final Module TEST_MODULE = new TEST();

	public static final Module XXX_MODULE = new XXX();

	private Class _moduleClass;

	public Constructor getConstructor() {
		return _constructor;
	}

	protected static class WKF extends Module {
		public static final String WKF_MODULE_SHORT_NAME = "WKF";

		public static final String WKF_MODULE_NAME = "workflow_editor";

		protected WKF() {
			super(WKF_MODULE_NAME, WKF_MODULE_SHORT_NAME, "org.openflexo.wkf.WKFModule", "modules/flexoworkfloweditor", "10000", "wkf",
					WKFIconLibrary.WKF_SMALL_ICON, WKFIconLibrary.WKF_MEDIUM_ICON, WKFIconLibrary.WKF_MEDIUM_ICON_WITH_HOVER,
					WKFIconLibrary.WKF_BIG_ICON, true);
		}

	}

	protected static class IE extends Module {
		public static final String IE_MODULE_SHORT_NAME = "IE";

		public static final String IE_MODULE_NAME = "interface_editor";

		public IE() {
			super(IE_MODULE_NAME, IE_MODULE_SHORT_NAME, "org.openflexo.ie.IEModule", "modules/flexointerfaceeditor", "10001", "ie",
					SEIconLibrary.SE_SMALL_ICON, SEIconLibrary.SE_MEDIUM_ICON, SEIconLibrary.SE_MEDIUM_ICON_WITH_HOVER,
					SEIconLibrary.SE_BIG_ICON, true);
		}

	}

	protected static class DM extends Module {

		public static final String DM_MODULE_SHORT_NAME = "DM";

		public static final String DM_MODULE_NAME = "data_model_editor";

		public DM() {
			super(DM_MODULE_NAME, DM_MODULE_SHORT_NAME, "org.openflexo.dm.DMModule", "modules/flexodatamodeleditor", "10006", "dm",
					DMEIconLibrary.DME_SMALL_ICON, DMEIconLibrary.DME_MEDIUM_ICON, DMEIconLibrary.DME_MEDIUM_ICON_WITH_HOVER,
					DMEIconLibrary.DME_BIG_ICON, true);
		}

	}

	protected static class CG extends Module {
		public static final String GENERATOR_MODULE_SHORT_NAME = "CG";

		public static final String GENERATOR_MODULE_NAME = "code_generator";

		public CG() {
			super(GENERATOR_MODULE_NAME, GENERATOR_MODULE_SHORT_NAME, "org.openflexo.cgmodule.GeneratorModule",
					"modules/flexocodegenerator", "10002", "cg", CGIconLibrary.CG_SMALL_ICON, CGIconLibrary.CG_MEDIUM_ICON,
					CGIconLibrary.CG_MEDIUM_ICON_WITH_HOVER, CGIconLibrary.CG_BIG_ICON, true);
		}

	}

	protected static class DG extends Module {
		public static final String DG_MODULE_SHORT_NAME = "DG";

		public static final String DG_MODULE_NAME = "doc_generator";

		public DG() {
			super(DG_MODULE_NAME, DG_MODULE_SHORT_NAME, "org.openflexo.dgmodule.DGModule", "modules/flexodocgenerator", "10004", "dg",
					DGIconLibrary.DG_SMALL_ICON, DGIconLibrary.DG_MEDIUM_ICON, DGIconLibrary.DG_MEDIUM_ICON_WITH_HOVER,
					DGIconLibrary.DG_BIG_ICON, true);
		}

	}

	protected static class SG extends Module {

		public static final String SOURCE_GENERATOR_MODULE_SHORT_NAME = "SG";

		public static final String SOURCE_GENERATOR_MODULE_NAME = "source_generator";

		public SG() {
			super(SOURCE_GENERATOR_MODULE_NAME, SOURCE_GENERATOR_MODULE_SHORT_NAME, "org.openflexo.sgmodule.SGModule",
					"modules/flexosourcegenerator", "10004", "sg", SGIconLibrary.SG_SMALL_ICON, SGIconLibrary.SG_MEDIUM_ICON,
					SGIconLibrary.SG_MEDIUM_ICON_WITH_HOVER, SGIconLibrary.SG_BIG_ICON, true);
		}

	}

	protected static class DE extends Module {

		public static final String DE_MODULE_SHORT_NAME = "DE";

		public static final String DE_MODULE_NAME = "doc_editor";

		public DE() {
			super(DE_MODULE_NAME, DE_MODULE_SHORT_NAME, "org.openflexo.doceditormodule.DEModule", "modules/flexodoceditor", "10005", "de",
					DEIconLibrary.DE_SMALL_ICON, DEIconLibrary.DE_MEDIUM_ICON, DEIconLibrary.DE_MEDIUM_ICON_WITH_HOVER,
					DEIconLibrary.DE_BIG_ICON, true);
		}

	}

	protected static class WSE extends Module {

		public static final String WSE_MODULE_SHORT_NAME = "WSE";

		public static final String WSE_MODULE_NAME = "wse_module_name";

		public WSE() {
			super(WSE_MODULE_NAME, WSE_MODULE_SHORT_NAME, "org.openflexo.wse.WSEModule", "modules/flexowebserviceeditor", "10007", "wse",
					WSEIconLibrary.WS_SMALL_ICON, WSEIconLibrary.WS_MEDIUM_ICON, WSEIconLibrary.WS_MEDIUM_ICON_WITH_HOVER,
					WSEIconLibrary.WS_BIG_ICON, true);
		}

	}

	protected static class VE extends Module {

		public static final String VE_MODULE_SHORT_NAME = "VE";

		public static final String VE_MODULE_NAME = "ve_module_name";

		public VE() {
			super(VE_MODULE_NAME, VE_MODULE_SHORT_NAME, "org.openflexo.ve.VEModule", "modules/flexovieweditor", "10008", "ve",
					VEIconLibrary.VE_SMALL_ICON, VEIconLibrary.VE_MEDIUM_ICON, VEIconLibrary.VE_MEDIUM_ICON_WITH_HOVER,
					VEIconLibrary.VE_BIG_ICON, true);
		}

	}

	protected static class DRE extends Module {

		public static final String DRE_MODULE_SHORT_NAME = "DRE";

		public static final String DRE_MODULE_NAME = "doc_resource_manager";

		public DRE() {
			super(DRE_MODULE_NAME, DRE_MODULE_SHORT_NAME, "org.openflexo.dre.DREModule", "modules/flexodocresourceeditor", "10010", "dre",
					DREIconLibrary.DRE_SMALL_ICON, DREIconLibrary.DRE_MEDIUM_ICON, DREIconLibrary.DRE_MEDIUM_ICON_WITH_HOVER,
					DREIconLibrary.DRE_BIG_ICON, false);
		}

	}

	protected static class FPS extends Module {

		public static final String FPS_MODULE_SHORT_NAME = "FPS";

		public static final String FPS_MODULE_NAME = "fps_module_name";

		public FPS() {
			super(FPS_MODULE_NAME, FPS_MODULE_SHORT_NAME, "org.openflexo.fps.FPSModule", "modules/flexoprjsharingmodule", "10011", "fps",
					FPSIconLibrary.FPS_SMALL_ICON, FPSIconLibrary.FPS_MEDIUM_ICON, FPSIconLibrary.FPS_MEDIUM_ICON_WITH_HOVER,
					FPSIconLibrary.FPS_BIG_ICON, false);
		}

	}

	protected static class VPM extends Module {

		public static final String VPM_MODULE_SHORT_NAME = "VPM";

		public static final String VPM_MODULE_NAME = "vpm_module_name";

		public VPM() {
			super(VPM_MODULE_NAME, VPM_MODULE_SHORT_NAME, "org.openflexo.vpm.VPMModule", "modules/flexoviewpointmodeller", "10009", "vpm",
					VPMIconLibrary.VPM_SMALL_ICON, VPMIconLibrary.VPM_MEDIUM_ICON, VPMIconLibrary.VPM_MEDIUM_ICON_WITH_HOVER,
					VPMIconLibrary.VPM_BIG_ICON, false);
		}

	}

	protected static class TEST extends Module {

		public TEST() {
			super("TestModule", "TEST", "org.openflexo.TestModule", "modules/flexoworkfloweditor", null, "test",
					WKFIconLibrary.WKF_SMALL_ICON, WKFIconLibrary.WKF_MEDIUM_ICON, WKFIconLibrary.WKF_MEDIUM_ICON_WITH_HOVER,
					WKFIconLibrary.WKF_BIG_ICON, true);
		}

	}

	protected static class XXX extends Module {

		public XXX() {
			super("XXXModuleName", "xxx", "org.openflexo.xxx.XXXModule", "modules/flexonewmodule", null, "xx", null, null, null, null, true);
		}

	}

	private String name;
	private String shortName;
	private String className;
	private String relativeDirectory;
	private String jiraComponentID;
	private String helpTopic;
	private ImageIcon smallIcon;
	private ImageIcon mediumIcon;
	private ImageIcon mediumIconWithHover;
	private ImageIcon bigIcon;
	private boolean requiresProject;

	protected Module(String name, String shortName, String className, String relativeDirectory, String jiraComponentID, String helpTopic,
			ImageIcon smallIcon, ImageIcon mediumIcon, ImageIcon mediumIconWithHover, ImageIcon bigIcon, boolean requiresProject) {
		super();
		this.name = name;
		this.shortName = shortName;
		this.className = className;
		this.relativeDirectory = relativeDirectory;
		this.jiraComponentID = jiraComponentID;
		this.helpTopic = helpTopic;
		this.smallIcon = smallIcon;
		this.mediumIcon = mediumIcon;
		this.mediumIconWithHover = mediumIconWithHover;
		this.bigIcon = bigIcon;
		this.requiresProject = requiresProject;
	}

	protected Module() {
		super();
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	public String getClassName() {
		return className;
	}

	protected String getRelativeDirectory() {
		return relativeDirectory;
	}

	public String getJiraComponentID() {
		return jiraComponentID;
	}

	public String getHelpTopic() {
		return helpTopic;
	}

	public ImageIcon getSmallIcon() {
		return smallIcon;
	}

	public ImageIcon getMediumIcon() {
		return mediumIcon;
	}

	public ImageIcon getMediumIconWithHover() {
		return mediumIconWithHover;
	}

	public ImageIcon getBigIcon() {
		return bigIcon;
	}

	public boolean requireProject() {
		return requiresProject;
	}

	public boolean isNotFoundNotified() {
		return notFoundNotified;
	}

	/**
	 * @return
	 */
	public Class<?> getModuleClass() {
		if (_moduleClass == null) {
			_moduleClass = searchModuleClass(getClassName());
		}
		return _moduleClass;
	}

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	public final String getDescription() {
		return getName() + "_description";
	}

	public String getLocalizedDescription() {
		return FlexoLocalization.localizedForKey(getDescription());
	}

	@Override
	public boolean isLoaded() {
		return getModuleLoader().isLoaded(this);
	}

	private ModuleLoader getModuleLoader() {
		return ModuleLoader.instance();
	}

	public boolean isActive() {
		return getModuleLoader().isActive(this);
	}

	public boolean isAvailable() {
		return getModuleLoader().isAvailable(this);
	}

	private boolean notFoundNotified = false;

	private Class<?> searchModuleClass(String fullQualifiedModuleName) {
		try {
			return Class.forName(fullQualifiedModuleName);
		} catch (ClassNotFoundException e) {
			if (!notFoundNotified) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find MODULE " + fullQualifiedModuleName);
				}
				notFoundNotified = true;
			}
			return null;
		}

	}

	private Constructor _constructor;

	public boolean register() {
		_constructor = lookupConstructor();
		return _constructor != null;
	}

	/**
	 * Internally used to lookup constructor
	 * 
	 */
	private Constructor lookupConstructor() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Registering module '" + getName() + "'");
		}
		Class[] constructorSigner;
		if (requireProject()) {
			constructorSigner = new Class[1];
			constructorSigner[0] = InteractiveFlexoEditor.class;
		} else {
			constructorSigner = new Class[0];
		}
		try {
			Constructor constructor = getModuleClass().getDeclaredConstructor(constructorSigner);
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("Contructor:" + constructor);
			}
			return constructor;
		} catch (SecurityException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("SecurityException raised during module " + getName() + " registering. Aborting.");
			}
		} catch (NoSuchMethodException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("NoSuchMethodException raised during module " + getName() + " registering. Aborting.");
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return getLocalizedName();
	}

	public String getHTMLDescription() {
		Language language = DocResourceManager.instance().getLanguage(GeneralPreferences.getLanguage());
		DocItem docItem = DocResourceManager.getDocItem(getHelpTopic());
		if (docItem != null) {
			if (docItem.getLastApprovedActionForLanguage(language) != null) {
				String returned = "<html>" + docItem.getLastApprovedActionForLanguage(language).getVersion().getFullHTMLDescription()
						+ "</html>";
				return returned;
			}
		}

		return "<html>No description available for <b>" + getLocalizedName() + "</b>" + "<br>"
				+ "Please submit documentation in documentation resource center" + "<br>" + "</html>";
	}
}