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
package org.openflexo.sg.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.javaparser.JavaParseException;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.sg.exception.JavaAppendingException;
import org.openflexo.sg.file.SGJavaFileResource;
import org.openflexo.sg.generationdef.FileEntry;
import org.openflexo.sg.utils.JavaCodeMerger;
import org.openflexo.toolbox.JavaUtils;

/**
 * @author sylvain
 */
public class SGJavaClassGenerator extends SGGenerator<DMEntity, GeneratedTextResource> {
	private static final Logger logger = FlexoLogger.getLogger(SGJavaClassGenerator.class.getPackage().getName());

	protected SGJavaFileResource javaResource;

	protected JavaAppendingException javaAppendingException;

	private String classPackage;
	private Set<String> neededImports = new HashSet<String>();

	protected SGJavaClassGenerator(ModuleGenerator moduleGenerator, FileEntry fileEntry) {
		this(moduleGenerator, fileEntry, null);
	}

	/*
	 * TODO: DMEntity should be replaced by a more generic class used to allow java class merge !
	 */
	protected SGJavaClassGenerator(ModuleGenerator moduleGenerator, FileEntry fileEntry, DMEntity entity) {
		super(moduleGenerator, fileEntry, entity);
	}

	public DMEntity getEntity() {
		return getObject();
	}

	public final String getEntityClassName() {
		if (getEntity() != null) {
			return getEntity().getEntityClassName();
		}
		return null;
	}

	public final String getEntityPackageName() {
		if (getEntity() != null && getEntity().getPackage() != null) {
			return getEntity().getPackage().getJavaStringRepresentation();
		}
		return null;
	}

	public final String getEntityFolderPath() {
		if (getEntity() != null) {
			return getEntity().getPathForPackage();
		}
		return getEntityPackageName().replace('.', '/');
	}

	@Override
	public final void generate(boolean forceRegenerate) {
		if (!needGeneration(forceRegenerate)) {
			return;
		}
		try {
			startGeneration();
			String javaCode = merge(getTemplateName(), defaultContext());
			javaCode = addJavaImports(javaCode);
			javaAppendingException = null;
			if (getEntity() != null) {
				try {
					javaCode = JavaCodeMerger.mergeJavaCode(javaCode, getEntity(), javaResource);
				} catch (JavaParseException e) {
					javaAppendingException = new JavaAppendingException(this, getEntity().getFullQualifiedName(), e);
					logger.warning("Could not parse generated code. Escape java merge.");
				}
			}

			javaCode = formatGeneration(javaCode);

			generatedCode = new GeneratedTextResource(getEntityClassName() + ".java", javaCode);
		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) {
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		} finally {
			stopGeneration();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startGeneration() {
		super.startGeneration();
		neededImports.clear();
		initializeClassPackage();
	}

	private String addJavaImports(String javaCode) {
		StringBuilder imports = new StringBuilder();
		List<String> sortedImports = new ArrayList<String>(neededImports);
		Collections.sort(sortedImports);
		for (String neededImport : sortedImports) {
			imports.append("import " + neededImport + ";");
			imports.append("\n");
		}

		return (!StringUtils.isEmpty(getClassPackage()) ? "package " + getClassPackage() + ";" : "") + imports + javaCode;
	}

	/**
	 * Set the class package to a default value based on the file generation path. This can be overwritten by calling macro
	 * #setPackage("a.b.c") in velocity template.
	 */
	private void initializeClassPackage() {
		setClassPackage(getFileEntry().relativePath);
	}

	@Override
	public GeneratedTextResource getGeneratedCode() {
		if (generatedCode == null && javaResource != null && javaResource.getJavaFile() != null
				&& javaResource.getJavaFile().hasLastAcceptedContent()) {
			generatedCode = new GeneratedTextResource(getEntityClassName(), javaResource.getJavaFile().getLastAcceptedContent());
		}
		return generatedCode;
	}

	@Override
	@Deprecated
	public final String getIdentifier() {
		return "<don't_use_this>";
		// return getEntityPackageName()+(getEntityPackageName().length()>0?".":"")+getEntityClassName();
	}

	public static String makeIdentifier(String fileName, String symbolicPathName, String relativePathName) {
		return symbolicPathName + File.separator + relativePathName + File.separator + fileName;
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void buildResourcesAndSetGenerators(SourceRepository repository, Vector<CGRepositoryFileResource> resources) {
		// Nothing to do, performed in ModuleGenerator
	}

	public void rebuildDependanciesForResource(SGJavaFileResource resource) {
		logger.warning("TODO: rebuildDependanciesForResource() !!!");
	}

	public JavaAppendingException getAppendingException() {
		return javaAppendingException;
	}

	@Override
	public boolean hasAppendingException() {
		return (javaAppendingException != null);
	}

	public void addImport(Class<?> neededImport) {
		if (!neededImport.isPrimitive()) {
			addImport(neededImport.getName());
		}
	}

	public void addImport(String neededImport) {

		if (!StringUtils.isEmpty(neededImport)) {
			if (!StringUtils.isEmpty(getClassPackage())) {
				int lastDot = neededImport.lastIndexOf('.');
				if (lastDot != -1 && neededImport.substring(0, lastDot).equals(getClassPackage())) {
					return; // Skip add import in the same package than this class.
				}
			}

			this.neededImports.add(neededImport);
		}
	}

	public String getClassPackage() {
		return classPackage;
	}

	public void setClassPackage(String classPackage) {
		this.classPackage = JavaUtils.getPackageName(classPackage);
	}
}
