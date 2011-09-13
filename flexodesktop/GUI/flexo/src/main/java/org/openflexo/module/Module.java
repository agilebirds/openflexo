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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.ch.FCH;
import org.openflexo.components.OpenProjectComponent;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
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

	public static final Module OE_MODULE = new OE();

	public static final Module DRE_MODULE = new DRE();

	public static final Module FPS_MODULE = new FPS();

	public static final Module CED_MODULE = new CED();

	public static final Module SG_MODULE = new SG();

	public static final Module TEST_MODULE = new TEST();

	public static final Module XXX_MODULE = new XXX();

	private static final Module[] knownsModules = { WKF_MODULE, IE_MODULE, DE_MODULE, DM_MODULE, CG_MODULE, SG_MODULE, DG_MODULE, WSE_MODULE, OE_MODULE, DRE_MODULE, FPS_MODULE, CED_MODULE /*
	 * ,
	 * XXX_MODULE
	 */};

	private Class _moduleClass;

	private String _moduleName;

	private String _moduleShortName;

	private String _moduleDescription;

	private String _moduleVersion;

	private File _directory;

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

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return WKFIconLibrary.WKF_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return WKFIconLibrary.WKF_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return WKFIconLibrary.WKF_SELECTED_ICON;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return WKFIconLibrary.WKF_BIG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return WKFIconLibrary.WKF_BIG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
			return WKFIconLibrary.WKF_BIG_SELECTED_ICON;
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

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return SEIconLibrary.IE_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return SEIconLibrary.IE_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return SEIconLibrary.IE_SELECTED_ICON;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return SEIconLibrary.IE_BIG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return SEIconLibrary.IE_BIG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
			return SEIconLibrary.IE_BIG_SELECTED_ICON;
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

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return DMEIconLibrary.DM_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return DMEIconLibrary.DM_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return DMEIconLibrary.DM_SELECTED_ICON;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return DMEIconLibrary.DM_BIG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return DMEIconLibrary.DM_BIG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
			return DMEIconLibrary.DM_BIG_SELECTED_ICON;
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

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return CGIconLibrary.CG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return CGIconLibrary.CG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return CGIconLibrary.CG_SELECTED_ICON;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return CGIconLibrary.CG_BIG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return CGIconLibrary.CG_BIG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
			return CGIconLibrary.CG_BIG_SELECTED_ICON;
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

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return DGIconLibrary.DG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return DGIconLibrary.DG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return DGIconLibrary.DG_SELECTED_ICON;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return DGIconLibrary.DG_BIG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return DGIconLibrary.DG_BIG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
			return DGIconLibrary.DG_BIG_SELECTED_ICON;
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

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return SGIconLibrary.SG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return SGIconLibrary.SG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return SGIconLibrary.SG_SELECTED_ICON;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return SGIconLibrary.SG_BIG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return SGIconLibrary.SG_BIG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
			return SGIconLibrary.SG_BIG_SELECTED_ICON;
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

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return DEIconLibrary.DE_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return DEIconLibrary.DE_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return DEIconLibrary.DE_SELECTED_ICON;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return DEIconLibrary.DE_BIG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return DEIconLibrary.DE_BIG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
			return DEIconLibrary.DE_BIG_SELECTED_ICON;
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

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return WSEIconLibrary.WS_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return WSEIconLibrary.WS_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return WSEIconLibrary.WS_SELECTED_ICON;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return WSEIconLibrary.WS_BIG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return WSEIconLibrary.WS_BIG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
			return WSEIconLibrary.WS_BIG_SELECTED_ICON;
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

	protected static class OE extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexoontologyeditor";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.oe.OEModule";
		}

		@Override
		public String getHelpTopic() {
			return "oe";
		}

		@Override
		public boolean requireProject() {
			return true;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return VEIconLibrary.OE_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return VEIconLibrary.OE_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return VEIconLibrary.OE_SELECTED_ICON;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return VEIconLibrary.OE_BIG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return VEIconLibrary.OE_BIG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
			return VEIconLibrary.OE_BIG_SELECTED_ICON;
		}

		public static final String OE_MODULE_SHORT_NAME = "OE";

		public static final String OE_MODULE_NAME = "oe_module_name";

		@Override
		public String getName() {
			return OE_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return OE_MODULE_SHORT_NAME;
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

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return DREIconLibrary.DRE_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return DREIconLibrary.DRE_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return DREIconLibrary.DRE_SELECTED_ICON;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return DREIconLibrary.DRE_BIG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return DREIconLibrary.DRE_BIG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
			return DREIconLibrary.DRE_BIG_SELECTED_ICON;
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
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return FPSIconLibrary.FPS_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return FPSIconLibrary.FPS_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return FPSIconLibrary.FPS_SELECTED_ICON;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return FPSIconLibrary.FPS_BIG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return FPSIconLibrary.FPS_BIG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
			return FPSIconLibrary.FPS_BIG_SELECTED_ICON;
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

	protected static class CED extends Module {
		@Override
		protected String getRelativeDirectory() {
			return "modules/flexocalceditor";
		}

		@Override
		public String getClassName() {
			return "org.openflexo.ced.CEDModule";
		}

		@Override
		public String getHelpTopic() {
			return "ced";
		}

		@Override
		public boolean requireProject() {
			return false;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return VPMIconLibrary.CED_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return VPMIconLibrary.CED_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return VPMIconLibrary.CED_SELECTED_ICON;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return VPMIconLibrary.CED_BIG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return VPMIconLibrary.CED_BIG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
			return VPMIconLibrary.CED_BIG_SELECTED_ICON;
		}

		public static final String CED_MODULE_SHORT_NAME = "CED";

		public static final String CED_MODULE_NAME = "ced_module_name";

		@Override
		public String getName() {
			return CED_MODULE_NAME;
		}

		@Override
		public String getShortName() {
			return CED_MODULE_SHORT_NAME;
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

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return WKFIconLibrary.WKF_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return WKFIconLibrary.WKF_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return WKFIconLibrary.WKF_SELECTED_ICON;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return WKFIconLibrary.WKF_BIG_ACTIVE_ICON;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return WKFIconLibrary.WKF_BIG_UNACTIVE_ICON;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
			return WKFIconLibrary.WKF_BIG_SELECTED_ICON;
		}

		@Override
		public String getName()
		{
			return "TestModule";
		}

		@Override
		public String getShortName()
		{
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

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getActiveIcon()
		 */
		@Override
		public ImageIcon getActiveIcon() {
			return null;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getUnactiveIcon()
		 */
		@Override
		public ImageIcon getUnactiveIcon() {
			return null;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getSelectedIcon()
		 */
		@Override
		public ImageIcon getSelectedIcon() {
			return null;
		}

		/**
		 * Overrides getActiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigActiveIcon()
		 */
		@Override
		public ImageIcon getBigActiveIcon() {
			return null;
		}

		/**
		 * Overrides getUnactiveIcon
		 * 
		 * @see org.openflexo.module.Module#getBigUnactiveIcon()
		 */
		@Override
		public ImageIcon getBigUnactiveIcon() {
			return null;
		}

		/**
		 * Overrides getSelectedIcon
		 * 
		 * @see org.openflexo.module.Module#getBigSelectedIcon()
		 */
		@Override
		public ImageIcon getBigSelectedIcon() {
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

	public abstract ImageIcon getActiveIcon();

	public abstract ImageIcon getUnactiveIcon();

	public abstract ImageIcon getSelectedIcon();

	public abstract ImageIcon getBigActiveIcon();

	public abstract ImageIcon getBigUnactiveIcon();

	public abstract ImageIcon getBigSelectedIcon();

	public abstract boolean requireProject();

	public File getDirectory() {
		if (_directory == null) {
			_directory = new File(ModuleLoader.getWorkspaceDirectory(), getRelativeDirectory());
		}
		return _directory;
	}

	protected Module() {
		super();
	}

	public static Module getModule(Class moduleClass) {
		for (int i = 0; i < knownsModules.length; i++) {
			if ((knownsModules[i].getModuleClass() != null) && (knownsModules[i].getModuleClass().equals(moduleClass))) {
				return knownsModules[i];
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning(("Cannot lookup Module for " + moduleClass.getName()));
		}
		return null;
	}

	public static Module getModule(String moduleName) {
		for (int i = 0; i < knownsModules.length; i++) {
			if (knownsModules[i].getName().equals(moduleName)) {
				return knownsModules[i];
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning(("Cannot lookup Module for " + moduleName));
		}
		return null;
	}

	protected static Vector<Module> allKnownModules() {
		Vector<Module> returned = new Vector<Module>();
		for (int i = 0; i < knownsModules.length; i++) {
			returned.add(knownsModules[i]);
		}
		if (returned.size() == 0) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("No MODULES found. Stopping application.");
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Exiting application...");
			}
			System.exit(-1);
		}
		return returned;
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

	public FlexoModule getInstance() {
		return ModuleLoader.getModuleInstance(this);
	}

	@Override
	public boolean isLoaded() {
		return ModuleLoader.isLoaded(this);
	}

	public enum ModuleAvailability {
		AVAILABLE, SUBJECT_TO_RESTRICTIONS, NON_AVAILABLE;

		public boolean isAvailable() {
			return ((this == AVAILABLE) || (this == SUBJECT_TO_RESTRICTIONS));
		}
	}

	public boolean isActive() {
		return ModuleLoader.isActive(this);
	}

	public boolean isAvailable() {
		return ModuleLoader.isAvailable(this);
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

	private String lookupName() {
		if (getModuleClass() != null) {
			try {
				Method moduleNameMethod = getModuleClass().getDeclaredMethod("getModuleName");
				return (String) moduleNameMethod.invoke(_moduleClass);
			} catch (SecurityException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("SecurityException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
				}
			} catch (NoSuchMethodException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("NoSuchMethodException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
				}
			} catch (IllegalArgumentException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("IllegalArgumentException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
				}
			} catch (IllegalAccessException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("IllegalAccessException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
				}
			} catch (InvocationTargetException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("InvocationTargetException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
				}
			}
		}
		return "UNNAMED";
	}

	private String lookupShortName() {
		try {
			Method moduleNameMethod = getModuleClass().getDeclaredMethod("getModuleShortName");
			return (String) moduleNameMethod.invoke(_moduleClass);
		} catch (SecurityException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("SecurityException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		} catch (NoSuchMethodException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("NoSuchMethodException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		} catch (IllegalArgumentException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("IllegalArgumentException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		} catch (IllegalAccessException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("IllegalAccessException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		} catch (InvocationTargetException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("InvocationTargetException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		}
		return "UNNAMED";
	}

	private String lookupDescription() {
		try {
			Method moduleNameMethod = _moduleClass.getDeclaredMethod("getModuleDescription");
			return (String) moduleNameMethod.invoke(_moduleClass);
		} catch (SecurityException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("SecurityException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		} catch (NoSuchMethodException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("NoSuchMethodException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		} catch (IllegalArgumentException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("IllegalArgumentException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		} catch (IllegalAccessException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("IllegalAccessException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		} catch (InvocationTargetException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("InvocationTargetException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		}
		return "Description not available";
	}

	private String lookupVersion() {
		try {
			Method moduleNameMethod = _moduleClass.getDeclaredMethod("getModuleVersion");
			return (String) moduleNameMethod.invoke(_moduleClass);
		} catch (SecurityException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("SecurityException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		} catch (NoSuchMethodException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("NoSuchMethodException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		} catch (IllegalArgumentException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("IllegalArgumentException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		} catch (IllegalAccessException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("IllegalAccessException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		} catch (InvocationTargetException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("InvocationTargetException raised during module " + _moduleClass.getName() + " name extracting. Aborting.");
			}
		}
		return "N/V";
	}

	private Constructor _constructor;

	public boolean register() {
		_constructor = lookupConstructor();
		return (_constructor == null ? false : true);
	}

	/**
	 * Internally used to lookup constructor
	 * 
	 * @param moduleClass
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

	public FlexoModule load() throws ProjectLoadingCancelledException, ModuleLoadingException {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Loading module " + getName());
		}
		Object[] params;
		if (requireProject()) {
			params = new Object[1];
			while (ModuleLoader.getProject() == null) {
				File projectDirectory = OpenProjectComponent.getProjectDirectory();
				// The following line is the default line to call when we want
				// to open a project from a GUI (Interactive mode) so that
				// resource update handling is properly initialized. Additional
				// small stuffs can be performed in that call so that projects
				// are always opened the same way.
				ModuleLoader.loadProject(projectDirectory);
			}
			params[0] = ModuleLoader.getProject().getResourceManagerInstance().getEditor();
		} else {
			params = new Object[0];
		}
		FlexoModule returned = null;
		try {
			returned = (FlexoModule) _constructor.newInstance(params);
			FCH.ensureHelpEntryForModuleHaveBeenCreated(returned);
		} catch (IllegalArgumentException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not load module " + getName() + " : exception raised " + e.getClass().getName());
			}
			e.printStackTrace();
			/*
			 * if (logger.isLoggable(Level.INFO)) logger.info("Exiting application..."); System.exit(-1);
			 */
			throw new ModuleLoadingException(this);
		} catch (InstantiationException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not load module " + getName() + " : exception raised " + e.getClass().getName());
			}
			e.printStackTrace();
			/*
			 * if (logger.isLoggable(Level.INFO)) logger.info("Exiting application..."); System.exit(-1);
			 */
			throw new ModuleLoadingException(this);
		} catch (IllegalAccessException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not load module " + getName() + " : exception raised " + e.getClass().getName());
			}
			e.printStackTrace();
			/*
			 * if (logger.isLoggable(Level.INFO)) logger.info("Exiting application..."); System.exit(-1);
			 */
			throw new ModuleLoadingException(this);
		} catch (InvocationTargetException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not load module " + getName() + " : exception raised " + e.getClass().getName());
			}
			if (logger.isLoggable(Level.WARNING) && (e.getCause() != null)) {
				logger.warning("Caused by " + e.getCause().getClass().getName() + " " + e.getCause().getMessage());
				e.getCause().printStackTrace();
			}
			e.printStackTrace();
			/*
			 * if (logger.isLoggable(Level.INFO)) logger.info("Exiting application..."); System.exit(-1);
			 */
			throw new ModuleLoadingException(this);
		}
		return returned;
	}

	@Override
	public String toString() {
		return getLocalizedName();
	}
}