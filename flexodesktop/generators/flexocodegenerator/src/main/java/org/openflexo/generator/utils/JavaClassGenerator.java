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
package org.openflexo.generator.utils;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.javaparser.JavaParseException;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.JavaFileResource;
import org.openflexo.generator.FlexoResourceGenerator;
import org.openflexo.generator.GeneratorFormatter;
import org.openflexo.generator.JavaCodeMerger;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.JavaAppendingException;
import org.openflexo.generator.exception.JavaFormattingException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.generator.rm.UtilJavaFileResource;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public abstract class JavaClassGenerator extends FlexoResourceGenerator<DMEntity, GeneratedTextResource> {
	private static final Logger logger = FlexoLogger.getLogger(JavaClassGenerator.class.getPackage().getName());
	protected JavaFileResource javaResource;
	private DMEntity _entity;
	private String _entityClassName;
	private String _entityPackageName;

	protected JavaClassGenerator(ProjectGenerator projectGenerator, String entityClassName, String entityPackageName) {
		this(projectGenerator, null);
		_entityClassName = entityClassName;
		_entityPackageName = entityPackageName;
	}

	protected JavaClassGenerator(ProjectGenerator projectGenerator, DMEntity entity) {
		super(projectGenerator, entity);
		_entity = entity;
	}

	public final DMEntity getEntity() {
		return _entity;
	}

	public final String getEntityClassName() {
		if (getEntity() != null) {
			return getEntity().getEntityClassName();
		}
		return _entityClassName;
	}

	public final String getEntityPackageName() {
		if (getEntity() != null && getEntity().getPackage() != null) {
			return getEntity().getPackage().getJavaStringRepresentation();
		}
		return _entityPackageName;
	}

	public final String getEntityFolderPath() {
		if (getEntity() != null) {
			return getEntity().getPathForPackage();
		}
		return getEntityPackageName().replace('.', '/');
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		javaResource = (UtilJavaFileResource) resourceForKeyWithCGFile(ResourceType.JAVA_FILE,
				GeneratorUtils.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (javaResource == null) {
			javaResource = GeneratedFileResourceFactory.createNewUtilJavaFileResource(repository, this);
		} else {
			javaResource.setGenerator(this);
		}
		resources.add(javaResource);
	}

	@Override
	public final void generate(boolean forceRegenerate) {
		if (!forceRegenerate && !needsGeneration()) {
			return;
		}
		try {
			startGeneration();
			String javaCode = merge(getTemplateName(), defaultContext());
			javaAppendingException = null;
			if (getEntity() != null) {
				try {
					javaCode = JavaCodeMerger.mergeJavaCode(javaCode, getEntity(), javaResource);
				} catch (JavaParseException e) {
					javaAppendingException = new JavaAppendingException(this, getEntity().getFullQualifiedName(), e);
					logger.warning("Could not parse generated code. Escape java merge.");
				}
			}
			try {
				_javaFormattingException = null;
				// logger.info("Avant formattage: "+javaCode);
				javaCode = GeneratorFormatter.formatJavaCode(javaCode, getEntityPackageName(), getEntityClassName(), this, getProject());
				// logger.info("Apres formattage: "+javaCode);
			} catch (JavaFormattingException javaFormattingException) {
				_javaFormattingException = javaFormattingException;
			}

			generatedCode = new GeneratedTextResource(getEntityClassName() + ".java", javaCode);
		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) {
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		} finally {
			stopGeneration();
		}
	}

	@Override
	public GeneratedTextResource getGeneratedCode() {
		if (generatedCode == null && javaResource != null && javaResource.getJavaFile() != null
				&& javaResource.getJavaFile().hasLastAcceptedContent()) {
			generatedCode = new GeneratedTextResource(getEntityClassName(), javaResource.getJavaFile().getLastAcceptedContent());
		}
		return generatedCode;
	}

	public abstract String getTemplateName();

	public abstract void rebuildDependanciesForResource(JavaFileResource resource);

	@Override
	public final String getIdentifier() {
		return getEntityPackageName() + (getEntityPackageName().length() > 0 ? "." : "") + getEntityClassName();
	}
}
