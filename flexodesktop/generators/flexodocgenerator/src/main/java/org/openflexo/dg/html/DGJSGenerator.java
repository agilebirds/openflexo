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
package org.openflexo.dg.html;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.velocity.VelocityContext;
import org.openflexo.dg.DGGenerator;
import org.openflexo.dg.rm.JSFileResource;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.generator.GeneratedCodeResult;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.module.external.ExternalModuleDelegater;


/**
 * @author ndaniels
 * 
 */
public class DGJSGenerator<T extends FlexoModelObject> extends DGGenerator<T> implements IFlexoResourceGenerator {
	protected static final String UPPER_CASE_REGEXP = "[A-Z]+";

	protected static final Pattern UPPER_CASE_PATTERN = Pattern.compile(UPPER_CASE_REGEXP);

	protected static final Logger logger = Logger.getLogger(DGJSGenerator.class.getPackage().getName());

	public static String getJSFileExtension() {
		return ".js";
	}

	public static String nameForObject(FlexoModelObject object, DGRepository repository) {
		return nameForObjectNoExt(object, repository) + getJSFileExtension();
	}

	public static String nameForProject(DGRepository repository) {
		return nameForProjectNoExt(repository) + getJSFileExtension();
	}

	public static String nameForProcess(FlexoProcess process, DGRepository repository) {
		return nameForProcessNoExt(process, repository) + getJSFileExtension();
	}

	public static String nameForProcessFolder(ProcessFolder processFolder, DGRepository repository) {
		return nameForProcessFolderNoExt(processFolder, repository) + getJSFileExtension();
	}

	public static String nameForDKV(DGRepository repository) {
		return nameForDKVNoExt(repository) + getJSFileExtension();
	}

	public static String nameForOperation(OperationNode operation, DGRepository repository) {
		return nameForOperationNoExt(operation, repository) + getJSFileExtension();
	}

	public static String nameForMenu(DGRepository repository) {
		return nameForMenuNoExt(repository) + getJSFileExtension();
	}

	public static String nameForComponent(ComponentDefinition cd, DGRepository repository) {
		return nameForComponentNoExt(cd, repository) + getJSFileExtension();
	}

	public static String nameForDataModel(DGRepository repository) {
		return nameForDataModelNoExt(repository) + getJSFileExtension();
	}

	public static String nameForComponentLibrary(DGRepository repository) {
		return nameForComponentLibraryNoExt(repository) + getJSFileExtension();
	}

	public static String nameForWorkflow(DGRepository repository) {
		return nameForWorkflowNoExt(repository) + getJSFileExtension();
	}

	public static String nameForReadersGuide(DGRepository repository) {
		return nameForReadersGuideNoExt(repository) + getJSFileExtension();
	}

	public static String nameForDefinitions(DGRepository repository) {
		return nameForDefinitionsNoExt(repository) + getJSFileExtension();
	}

	public static String nameForRepository(DMRepository dmr, DGRepository repository) {
		return nameForRepositoryNoExt(dmr, repository) + getJSFileExtension();
	}

	public static String nameForEntity(DMEntity entity, DGRepository repository) {
		return nameForEntityNoExt(entity, repository) + getJSFileExtension();
	}

	private JSFileResource<DGJSGenerator<T>> jsResource;

	protected DGJSGenerator(ProjectDocHTMLGenerator projectGenerator, T source) {
		super(projectGenerator, source);
	}

	public DGJSGenerator(ProjectDocHTMLGenerator projectGenerator, T source, String templateName) {
		super(projectGenerator, source, templateName);
	}

	public DGJSGenerator(ProjectDocHTMLGenerator projectGenerator, T source, String templateName, String identifier, String fileName, TOCEntry entry) {
		super(projectGenerator, source, templateName, identifier, fileName, entry);
	}

	@Override
	public boolean isCodeAlreadyGenerated() {
		return getGeneratedCode() != null;
	}

	public static String splitOnUpperCase(String s) {
		if (s == null || s.trim().length() == 0)
			return "";
		Matcher m = UPPER_CASE_PATTERN.matcher(s);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			if (sb.length() == 0 && m.start() == 0)
				m.appendReplacement(sb, "$0");
			else
				m.appendReplacement(sb, "&#8203;$0");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Overrides getSymbolicDirectory
	 * 
	 * @see org.openflexo.dg.FlexoLatexResourceGenerator#getSymbolicDirectory(org.openflexo.foundation.cg.GenerationRepository)
	 */
	public CGSymbolicDirectory getSymbolicDirectory(DGRepository repository) {
		return repository.getJSSymbolicDirectory();
	}

	/**
	 * Overrides getFileExtension
	 * 
	 * @see org.openflexo.dg.DGGenerator#getFileExtension()
	 */
	@Override
	public String getFileExtension() {
		return getJSFileExtension();
	}

	public static String getValidReference(String label) {
		return BAD_CHARACTERS_PATTERN.matcher(label).replaceAll("-");
	}

	protected FlexoCSS getCss() {
		return getObject().getProject().getCssSheet();
	}

	@Override
	public GeneratedCodeResult getGeneratedCode() {
		if (generatedCode == null && jsResource != null && jsResource.getASCIIFile() != null && jsResource.getASCIIFile().hasLastAcceptedContent()) {
			generatedCode = new GeneratedTextResource(getFileName(), jsResource.getASCIIFile().getLastAcceptedContent());
		}
		return super.getGeneratedCode();
	}

	public void setJSResource(JSFileResource<DGJSGenerator<T>> jsResource) {
		this.jsResource = jsResource;
	}

	@Override
	public void generate(boolean forceRegenerate) {
		if (!forceRegenerate && !needsGeneration())
			return;
		startGeneration();
		try {
			VelocityContext context = defaultContext();
			if (getObject() instanceof FlexoProcess) {
				if (ExternalModuleDelegater.getModuleLoader() != null && ExternalModuleDelegater.getModuleLoader().getWKFModuleInstance() != null)
					context.put("processRepresentation", ExternalModuleDelegater.getModuleLoader().getWKFModuleInstance().getProcessRepresentation((FlexoProcess) getObject(), true));
			}
			generatedCode = new GeneratedTextResource(getFileName().endsWith(getFileExtension()) ? getFileName() : getFileName() + getFileExtension(), merge(getTemplateName(), context));
		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Unexpected exception occured: " + e.getMessage() + " for " + getObject().getFullyQualifiedName());
			e.printStackTrace();
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		}
		stopGeneration();
	}
}
