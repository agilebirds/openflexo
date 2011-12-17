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
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexoworkfloweditor";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.wkf.WKFModule";
		}

		@Override
		public String getHelpTopic() {
			return "wkf";
		}

		@Override
		public boolean requireProject() {
			return true;
		}

		@Override
		public ImageIcon getSmallIcon() {
			return WKFIconLibrary.WKF_SMALL_ICON;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return WKFIconLibrary.WKF_MEDIUM_ICON;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return WKFIconLibrary.WKF_MEDIUM_ICON_WITH_HOVER;
		}

		@Override
		public ImageIcon getBigIcon() {
			return WKFIconLibrary.WKF_BIG_ICON;
		}

		public static final String WKF_MODULE_SHORT_NAME = "WKF";

		public static final String WKF_MODULE_NAME = "workflow_editor";

		@Override
		public String getName() {
			return WKF_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return WKF_MODULE_SHORT_NAME;
		}

	}

	protected static class IE extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexointerfaceeditor";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.ie.IEModule";
		}

		@Override
		public String getHelpTopic() {
			return "ie";
		}

		@Override
		public boolean requireProject() {
			return true;
		}

		@Override
		public ImageIcon getSmallIcon() {
			return SEIconLibrary.SE_SMALL_ICON;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return SEIconLibrary.SE_MEDIUM_ICON;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return SEIconLibrary.SE_MEDIUM_ICON_WITH_HOVER;
		}

		@Override
		public ImageIcon getBigIcon() {
			return SEIconLibrary.SE_BIG_ICON;
		}

		public static final String IE_MODULE_SHORT_NAME = "IE";

		public static final String IE_MODULE_NAME = "interface_editor";

		@Override
		public String getName() {
			return IE_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return IE_MODULE_SHORT_NAME;
		}
	}

	protected static class DM extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexodatamodeleditor";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.dm.DMModule";
		}

		@Override
		public String getHelpTopic() {
			return "dm";
		}

		@Override
		public boolean requireProject() {
			return true;
		}

		@Override
		public ImageIcon getSmallIcon() {
			return DMEIconLibrary.DME_SMALL_ICON;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return DMEIconLibrary.DME_MEDIUM_ICON;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return DMEIconLibrary.DME_MEDIUM_ICON_WITH_HOVER;
		}

		@Override
		public ImageIcon getBigIcon() {
			return DMEIconLibrary.DME_BIG_ICON;
		}

		public static final String DM_MODULE_SHORT_NAME = "DM";

		public static final String DM_MODULE_NAME = "data_model_editor";

		@Override
		public String getName() {
			return DM_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return DM_MODULE_SHORT_NAME;
		}
	}

	protected static class CG extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexocodegenerator";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.cgmodule.GeneratorModule";
		}

		@Override
		public String getHelpTopic() {
			return "cg";
		}

		@Override
		public boolean requireProject() {
			return true;
		}

		@Override
		public ImageIcon getSmallIcon() {
			return CGIconLibrary.CG_SMALL_ICON;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return CGIconLibrary.CG_MEDIUM_ICON;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return CGIconLibrary.CG_MEDIUM_ICON_WITH_HOVER;
		}

		@Override
		public ImageIcon getBigIcon() {
			return CGIconLibrary.CG_BIG_ICON;
		}

		public static final String GENERATOR_MODULE_SHORT_NAME = "CG";

		public static final String GENERATOR_MODULE_NAME = "code_generator";

		@Override
		public String getName() {
			return GENERATOR_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return GENERATOR_MODULE_SHORT_NAME;
		}
	}

	protected static class DG extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexodocgenerator";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.dgmodule.DGModule";
		}

		@Override
		public String getHelpTopic() {
			return "dg";
		}

		@Override
		public boolean requireProject() {
			return true;
		}

		@Override
		public ImageIcon getSmallIcon() {
			return DGIconLibrary.DG_SMALL_ICON;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return DGIconLibrary.DG_MEDIUM_ICON;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return DGIconLibrary.DG_MEDIUM_ICON_WITH_HOVER;
		}

		@Override
		public ImageIcon getBigIcon() {
			return DGIconLibrary.DG_BIG_ICON;
		}

		public static final String DG_MODULE_SHORT_NAME = "DG";

		public static final String DG_MODULE_NAME = "doc_generator";

		@Override
		public String getName() {
			return DG_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return DG_MODULE_SHORT_NAME;
		}
	}

	protected static class SG extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexosourcegenerator";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.sgmodule.SGModule";
		}

		@Override
		public String getHelpTopic() {
			return "SG";
		}

		@Override
		public boolean requireProject() {
			return true;
		}

		@Override
		public ImageIcon getSmallIcon() {
			return SGIconLibrary.SG_SMALL_ICON;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return SGIconLibrary.SG_MEDIUM_ICON;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return SGIconLibrary.SG_MEDIUM_ICON_WITH_HOVER;
		}

		@Override
		public ImageIcon getBigIcon() {
			return SGIconLibrary.SG_BIG_ICON;
		}

		public static final String SOURCE_GENERATOR_MODULE_SHORT_NAME = "SG";

		public static final String SOURCE_GENERATOR_MODULE_NAME = "source_generator";

		@Override
		public String getName() {
			return SOURCE_GENERATOR_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return SOURCE_GENERATOR_MODULE_SHORT_NAME;
		}
	}

	protected static class DE extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexodoceditor";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.doceditormodule.DEModule";
		}

		@Override
		public String getHelpTopic() {
			return "de";
		}

		@Override
		public boolean requireProject() {
			return true;
		}

		@Override
		public ImageIcon getSmallIcon() {
			return DEIconLibrary.DE_SMALL_ICON;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return DEIconLibrary.DE_MEDIUM_ICON;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return DEIconLibrary.DE_MEDIUM_ICON_WITH_HOVER;
		}

		@Override
		public ImageIcon getBigIcon() {
			return DEIconLibrary.DE_BIG_ICON;
		}

		public static final String DE_MODULE_SHORT_NAME = "DE";

		public static final String DE_MODULE_NAME = "doc_editor";

		@Override
		public String getName() {
			return DE_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return DE_MODULE_SHORT_NAME;
		}
	}

	protected static class WSE extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexowebserviceeditor";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.wse.WSEModule";
		}

		@Override
		public String getHelpTopic() {
			return "wse";
		}

		@Override
		public boolean requireProject() {
			return true;
		}

		@Override
		public ImageIcon getSmallIcon() {
			return WSEIconLibrary.WS_SMALL_ICON;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return WSEIconLibrary.WS_MEDIUM_ICON;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return WSEIconLibrary.WS_MEDIUM_ICON_WITH_HOVER;
		}

		@Override
		public ImageIcon getBigIcon() {
			return WSEIconLibrary.WS_BIG_ICON;
		}

		public static final String WSE_MODULE_SHORT_NAME = "WSE";

		public static final String WSE_MODULE_NAME = "wse_module_name";

		@Override
		public String getName() {
			return WSE_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return WSE_MODULE_SHORT_NAME;
		}
	}

	protected static class VE extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexovieweditor";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.ve.VEModule";
		}

		@Override
		public String getHelpTopic() {
			return "ve";
		}

		@Override
		public boolean requireProject() {
			return true;
		}

		@Override
		public ImageIcon getSmallIcon() {
			return VEIconLibrary.VE_SMALL_ICON;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return VEIconLibrary.VE_MEDIUM_ICON;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return VEIconLibrary.VE_MEDIUM_ICON_WITH_HOVER;
		}

		@Override
		public ImageIcon getBigIcon() {
			return VEIconLibrary.VE_BIG_ICON;
		}

		public static final String VE_MODULE_SHORT_NAME = "VE";

		public static final String VE_MODULE_NAME = "ve_module_name";

		@Override
		public String getName() {
			return VE_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return VE_MODULE_SHORT_NAME;
		}
	}

	protected static class DRE extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexodocresourceeditor";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.dre.DREModule";
		}

		@Override
		public String getHelpTopic() {
			return "dre";
		}

		@Override
		public boolean requireProject() {
			return false;
		}

		@Override
		public ImageIcon getSmallIcon() {
			return DREIconLibrary.DRE_SMALL_ICON;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return DREIconLibrary.DRE_MEDIUM_ICON;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return DREIconLibrary.DRE_MEDIUM_ICON_WITH_HOVER;
		}

		@Override
		public ImageIcon getBigIcon() {
			return DREIconLibrary.DRE_BIG_ICON;
		}

		public static final String DRE_MODULE_SHORT_NAME = "DRE";

		public static final String DRE_MODULE_NAME = "doc_resource_manager";

		@Override
		public String getName() {
			return DRE_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return DRE_MODULE_SHORT_NAME;
		}
	}

	protected static class FPS extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexoprjsharingmodule";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.fps.FPSModule";
		}

		@Override
		public String getHelpTopic() {
			return "fps";
		}

		@Override
		public boolean requireProject() {
			return false;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getSmallIcon()
		 */
		@Override
		public ImageIcon getSmallIcon() {
			return FPSIconLibrary.FPS_SMALL_ICON;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return FPSIconLibrary.FPS_MEDIUM_ICON;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return FPSIconLibrary.FPS_MEDIUM_ICON_WITH_HOVER;
		}

		@Override
		public ImageIcon getBigIcon() {
			return FPSIconLibrary.FPS_BIG_ICON;
		}

		public static final String FPS_MODULE_SHORT_NAME = "FPS";

		public static final String FPS_MODULE_NAME = "fps_module_name";

		@Override
		public String getName() {
			return FPS_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return FPS_MODULE_SHORT_NAME;
		}
	}

	protected static class VPM extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexoviewpointmodeller";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.vpm.VPMModule";
		}

		@Override
		public String getHelpTopic() {
			return "vpm";
		}

		@Override
		public boolean requireProject() {
			return false;
		}

		@Override
		public ImageIcon getSmallIcon() {
			return VPMIconLibrary.VPM_SMALL_ICON;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return VPMIconLibrary.VPM_MEDIUM_ICON;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return VPMIconLibrary.VPM_MEDIUM_ICON_WITH_HOVER;
		}

		@Override
		public ImageIcon getBigIcon() {
			return VPMIconLibrary.VPM_BIG_ICON;
		}

		public static final String VPM_MODULE_SHORT_NAME = "VPM";

		public static final String VPM_MODULE_NAME = "vpm_module_name";

		@Override
		public String getName() {
			return VPM_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return VPM_MODULE_SHORT_NAME;
		}
	}

	protected static class TEST extends Module {

		@Override
		protected String getRelativeDirectory() {
			return "modules/flexoworkfloweditor";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.TestModule";
		}

		@Override
		public String getHelpTopic() {
			return "test";
		}

		@Override
		public boolean requireProject() {
			return true;
		}

		@Override
		public ImageIcon getSmallIcon() {
			return WKFIconLibrary.WKF_SMALL_ICON;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return WKFIconLibrary.WKF_MEDIUM_ICON;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return WKFIconLibrary.WKF_MEDIUM_ICON_WITH_HOVER;
		}

		@Override
		public ImageIcon getBigIcon() {
			return WKFIconLibrary.WKF_BIG_ICON;
		}

		@Override
		public String getName() {
			return "TestModule";
		}

		@Override
		public String getShortName() {
			return "WKF";
		}
	}

	protected static class XXX extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexonewmodule";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.xxx.XXXModule";
		}

		@Override
		public String getHelpTopic() {
			return "xxx";
		}

		@Override
		public boolean requireProject() {
			return false;
		}

		@Override
		public ImageIcon getSmallIcon() {
			return null;
		}

		@Override
		public ImageIcon getMediumIcon() {
			return null;
		}

		@Override
		public ImageIcon getMediumIconWithHover() {
			return null;
		}

		@Override
		public ImageIcon getBigIcon() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getShortName() {
			return null;
		}
	}

	protected abstract String getRelativeDirectory();

	public abstract String getClassName();

	public abstract String getHelpTopic();

	/**
	 * Return small icon for current module, as 20x20 pixels
	 * 
	 * @return
	 */
	public abstract ImageIcon getSmallIcon();

	/**
	 * Return medium icon for current module, as 32x32 pixels
	 * 
	 * @return
	 */
	public abstract ImageIcon getMediumIcon();

	/**
	 * Return medium icon for current module, as 32x32 pixels and with hover effect
	 * 
	 * @return
	 */
	public abstract ImageIcon getMediumIconWithHover();

	/**
	 * Return big icon for current module, as 64x64 pixels
	 * 
	 * @return
	 */
	public abstract ImageIcon getBigIcon();

	public abstract boolean requireProject();

	protected Module() {
		super();
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

	public abstract String getName();

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	public abstract String getShortName();

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

    private ModuleLoader getModuleLoader(){
        return ModuleLoader.instance();
    }

	public enum ModuleAvailability {
		AVAILABLE, SUBJECT_TO_RESTRICTIONS, NON_AVAILABLE;

		public boolean isAvailable() {
			return ((this == AVAILABLE) || (this == SUBJECT_TO_RESTRICTIONS));
		}
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
        return _constructor!=null;
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