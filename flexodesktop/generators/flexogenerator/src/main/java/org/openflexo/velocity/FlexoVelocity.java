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
package org.openflexo.velocity;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeSingleton;

import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileResource;

/**
 * @author gpolet
 * 
 */
public class FlexoVelocity {
	protected static final Logger logger = FlexoLogger.getLogger(FlexoVelocity.class.getPackage().getName());
	private static boolean isInitialized = false;

	private static FlexoVelocityResourceCache resourceCache;

	static {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Error while initializing Velocity");
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	public synchronized static void addToVelocimacro(TemplateLocator templateLocator, CGTemplate[] templates)
			throws ResourceNotFoundException, ParseErrorException, Exception {
		if (logger.isLoggable(Level.INFO))
			logger.info("Adding macros: " + templates);
		if (resourceCache != null)
			resourceCache.clearCache();
		Velocity.setApplicationAttribute("templateLocator", templateLocator);
		for (CGTemplate template : templates) {
			if (template != null)
				Velocity.getTemplate(template.getRelativePath());
		}
		Velocity.setApplicationAttribute("templateLocator", null);
	}

	/**
	 * @throws Exception
	 * 
	 */
	public synchronized static void init() throws Exception {
		if (!isInitialized) {
			// 1. We load properties with the Java object because it loads property files correctly unlike ExtendedProperties (which does
			// not handle properly "\ " as value " ")
			Properties p = new Properties();
			p.load(new FileInputStream(new FileResource("Config/velocity.properties")));
			// 2. We convert properties to extended properties (this conversion only handles values of type String (i.e., a VelocityLogger
			// cannot be set directly in the Properties, see 3.)
			ExtendedProperties ep = ExtendedProperties.convertProperties(p);
			VelocityLogger vl = new VelocityLogger();
			// 3. We set our logger so that it does not try to use its own
			ep.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, vl);
			// 4. We force our configuration to be loaded
			RuntimeSingleton.getRuntimeServices().setConfiguration(ep);
			// 5. We initialize properly Velocity passing no properties at all!
			Velocity.init();
			isInitialized = true;
			if (logger.isLoggable(Level.FINE))
				logger.fine("Velocity Engine started");
		}
	}

	public static void setResourceCache(FlexoVelocityResourceCache resourceCache) {
		if (logger.isLoggable(Level.INFO))
			logger.info("Setting Velocity resource cache");
		FlexoVelocity.resourceCache = resourceCache;
	}
}
