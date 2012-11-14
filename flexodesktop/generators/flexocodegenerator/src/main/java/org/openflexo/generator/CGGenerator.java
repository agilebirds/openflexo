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
package org.openflexo.generator;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.JavaAppendingException;
import org.openflexo.generator.exception.JavaFormattingException;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.generator.rm.UtilComponentAPIFileResource;
import org.openflexo.generator.rm.UtilComponentJavaFileResource;
import org.openflexo.generator.rm.UtilComponentWOFileResource;
import org.openflexo.generator.utils.MetaWOGenerator;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ToolBox;

/**
 * @author gpolet
 * 
 */
public abstract class CGGenerator<T extends FlexoModelObject> extends Generator<T, CGRepository> implements DataFlexoObserver {
	static final Logger logger = FlexoLogger.getLogger(CGGenerator.class.getPackage().getName());

	protected JavaFormattingException _javaFormattingException;
	protected JavaAppendingException javaAppendingException;

	public CGGenerator(ProjectGenerator projectGenerator, T object) {
		super(projectGenerator, object);
	}

	public CGGenerator(ProjectGenerator projectGenerator) {
		this(projectGenerator, null);
	}

	/**
	 * This method is very important, because it is the way we must identify or build all resources involved in code generation. After this
	 * list has been built, we just let ResourceManager do the work.
	 * 
	 * @param repository
	 *            : repository where resources should be retrieved or built
	 * @param resources
	 *            : the list of resources we must retrieve or build
	 */
	// public abstract void buildResourcesAndSetGenerators (CGRepository repository, Vector<CGRepositoryFileResource> resources);

	// public abstract ManyGeneratedCode generateCode() throws GenerationException;

	public static String nameForComponentDefinition(ComponentDefinition componentDefinition) {
		if (componentDefinition instanceof PopupComponentDefinition) {
			return componentDefinition.getComponentName() + "Popup";
		}
		return componentDefinition.getComponentName();
	}

	// public static String nameForPopup(PopupComponentDefinition componentDefinition)
	// {
	// return nameForComponentDefinition(componentDefinition)+"Popup";
	// }

	public static String nameForPopupLink(ComponentDefinition componentDefinition) {
		return nameForComponentDefinition(componentDefinition) + "Link";
	}

	/**
	 * 
	 * @param repository
	 * @param resources
	 * @param generator
	 * @return UtilComponentJavaFileResource java file resource
	 */
	protected static UtilComponentJavaFileResource buildGeneratedResourceListForComponentGenerator(CGRepository repository,
			Vector<CGRepositoryFileResource> resources, MetaWOGenerator generator) {
		generator.refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + generator.getIdentifier(), false);

		// Java file
		UtilComponentJavaFileResource javaResource = (UtilComponentJavaFileResource) generator.resourceForKeyWithCGFile(
				ResourceType.JAVA_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (javaResource == null) {
			javaResource = GeneratedFileResourceFactory.createNewUtilComponentJavaFileResource(repository, generator);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Created COMPONENT UTIL JAVA resource " + javaResource.getName());
			}
		} else {
			javaResource.setGenerator(generator);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Successfully retrieved COMPONENT UTIL JAVA resource " + javaResource.getName());
			}
		}
		resources.add(javaResource);

		// WO file
		UtilComponentWOFileResource WOResource = (UtilComponentWOFileResource) generator.resourceForKeyWithCGFile(ResourceType.WO_FILE,
				GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (WOResource == null) {
			WOResource = GeneratedFileResourceFactory.createNewUtilComponentWOFileResource(repository, generator);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Created COMPONENT UTIL WO resource " + WOResource.getName());
			}
		} else {
			WOResource.setGenerator(generator);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Successfully retrieved COMPONENT UTIL WO resource " + WOResource.getName());
			}
		}
		resources.add(WOResource);

		// API file
		UtilComponentAPIFileResource APIResource = (UtilComponentAPIFileResource) generator.getProject().resourceForKey(
				ResourceType.API_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, generator.getIdentifier()));
		if (APIResource == null) {
			APIResource = GeneratedFileResourceFactory.createNewUtilComponentAPIFileResource(repository, generator);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Created COMPONENT UTIL API resource " + APIResource.getName());
			}
		} else {
			APIResource.setGenerator(generator);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Successfully retrieved COMPONENT UTIL API resource " + APIResource.getName());
			}
		}
		resources.add(APIResource);

		return javaResource;
	}

	/**
	 * Generate code related to this generator, using cache scheme if present Equivalent for call generate(false)
	 * 
	 * @throws GenerationException
	 */
	@Override
	public void generate() throws GenerationException {
		generate(false);
	}

	public JavaFormattingException getFormattingException() {
		return _javaFormattingException;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasFormattingException() {
		return _javaFormattingException != null;
	}

	public JavaAppendingException getAppendingException() {
		return javaAppendingException;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasAppendingException() {
		return javaAppendingException != null;
	}

	public String serializeHashtable(Hashtable<String, String> params) {
		return ToolBox.serializeHashtable(params);
	}

	public String escapeStringForJava(String s) {
		return ToolBox.convertStringToJavaString(s);
	}

	public String getHiddenValueAccessorName(OperationNode targetNode, ActionNode startNode) {
		return GeneratorUtils.getHiddenValueAccessorName(targetNode, startNode);
	}

	public String getHiddenValueFieldName(OperationNode targetNode, ActionNode startNode) {
		return GeneratorUtils.getHiddenValueAccessorName(targetNode, startNode).toUpperCase();
	}

	public boolean isPrototype() {
		return getProjectGenerator().getTarget() == CodeType.PROTOTYPE;
	}

	public String getJavaDocString(String s) {
		return ToolBox.getJavaDocString(s);
	}

	public String getJavaString(String s) {
		return ToolBox.getJavaName(s);
	}

	public String getJavaString(String s, boolean keepCase) {
		return ToolBox.getJavaName(s, keepCase);
	}

	public String getWodKeyPath(String s) {
		return ToolBox.getWodKeyPath(s);
	}

	public String cleanLocalizationKey(String key) {
		// remove all break line characters and replace all contiguous spaces by only one space and trim the result.
		return ToolBox.convertStringToJavaString(key.replaceAll("\\s\\s+", " ").trim());
	}
}
