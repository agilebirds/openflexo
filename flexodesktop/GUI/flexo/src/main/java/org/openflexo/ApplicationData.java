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
package org.openflexo;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.openflexo.application.FlexoApplication;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.SplashWindow;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLoggingFormatter;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.UserType;
import org.openflexo.properties.FlexoProperties;
import org.openflexo.ssl.DenaliSecurityProvider;
import org.openflexo.toolbox.ResourceLocator;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.utils.CancelException;
import org.openflexo.utils.TooManyFailedAttemptException;
import org.openflexo.view.FlexoFrame;

/**
 * Class storing general data for application
 * 
 * 
 * @author sguerin
 */
public class ApplicationData {
	private static final Logger logger = Logger.getLogger(ApplicationData.class.getPackage().getName());

	public ApplicationData() {
		if (ModuleLoader.instance() == null) {
			logger.warning("ModuleLoader not initialized, initializing it with default values");
			ModuleLoader.initializeModules(UserType.MAINTAINER, false);
		}
	}
	
	private ModuleLoader getModuleLoader()
	{
		return ModuleLoader.instance();
	}
	
	public Vector<Module> getAvailableModules() 
	{
		return ModuleLoader.availableModules(); 
	}
	
	public UserType getUserType()
	{
		return ModuleLoader.getUserType();
	}

	public String getVersion()
	{
		return "Version " + FlexoCst.BUSINESS_APPLICATION_VERSION+ " (build " + FlexoCst.BUILD_ID+")";
	}
	
	public Vector<File> getLastOpenedProjects() {
		return GeneralPreferences.getLastOpenedProjects();
	}

	public ImageIcon getProjectIcon()
	{
		return IconLibrary.OPENFLEXO_NOTEXT_16;
	}

	public Module moduleToOpen;
	
	public File projectFile;
}
