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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openflexo.dg.DGGenerator;
import org.openflexo.dg.rm.HTMLFileResource;
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
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.toolbox.HTMLUtils;

/**
 * @author gpolet
 * 
 */
public class DGHTMLGenerator<T extends FlexoModelObject> extends DGGenerator<T> implements IFlexoResourceGenerator {
	protected static final String UPPER_CASE_REGEXP = "[A-Z]+";

	protected static final Pattern UPPER_CASE_PATTERN = Pattern.compile(UPPER_CASE_REGEXP);

	protected static final String LATEX_BACKSLASH = HTMLUtils.LATEX_BACKSLASH;

	protected static final String JAVA_BACKSLASH = "\\";

	protected static final String LATEX_TAG_REGEXP = "^\\\\[^ {]+(\\{[^}]*\\}\\s*)*";

	protected static final Pattern LATEX_TAG_PATTERN = Pattern.compile(LATEX_TAG_REGEXP);

	protected static final String CHARS_TO_ESCAPE_REGEXP = "[\\\\{}_&%#~]";

	protected static final Pattern CHARS_TO_ESCAPE_PATTERN = Pattern.compile(CHARS_TO_ESCAPE_REGEXP);

	public static String getHTMLFileExtension() {
		return ".html";
	}

	public static String nameForObject(FlexoModelObject object, DGRepository repository) {
		return nameForObjectNoExt(object, repository) + getHTMLFileExtension();
	}

	public static String nameForProject(DGRepository repository) {
		return nameForProjectNoExt(repository) + getHTMLFileExtension();
	}

	public static String nameForProcess(FlexoProcess process, DGRepository repository) {
		return nameForProcessNoExt(process, repository) + getHTMLFileExtension();
	}

	public static String nameForDKV(DGRepository repository) {
		return nameForDKVNoExt(repository) + getHTMLFileExtension();
	}

	public static String nameForOperation(OperationNode operation, DGRepository repository) {
		return nameForOperationNoExt(operation, repository) + getHTMLFileExtension();
	}

	public static String nameForMenu(DGRepository repository) {
		return nameForMenuNoExt(repository) + getHTMLFileExtension();
	}

	public static String nameForComponent(ComponentDefinition cd, DGRepository repository) {
		return nameForComponentNoExt(cd, repository) + getHTMLFileExtension();
	}

	public static String nameForDataModel(DGRepository repository) {
		return nameForDataModelNoExt(repository) + getHTMLFileExtension();
	}

	public static String nameForComponentLibrary(DGRepository repository) {
		return nameForComponentLibraryNoExt(repository) + getHTMLFileExtension();
	}

	public static String nameForWorkflow(DGRepository repository) {
		return nameForWorkflowNoExt(repository) + getHTMLFileExtension();
	}

	public static String nameForReadersGuide(DGRepository repository) {
		return nameForReadersGuideNoExt(repository) + getHTMLFileExtension();
	}

	public static String nameForDefinitions(DGRepository repository) {
		return nameForDefinitionsNoExt(repository) + getHTMLFileExtension();
	}

	public static String nameForRepository(DMRepository dmr, DGRepository repository) {
		return nameForRepositoryNoExt(dmr, repository) + getHTMLFileExtension();
	}

	public static String nameForEntity(DMEntity entity, DGRepository repository) {
		return nameForEntityNoExt(entity, repository) + getHTMLFileExtension();
	}

	private HTMLFileResource<DGHTMLGenerator<T>> htmlResource;

	protected DGHTMLGenerator(ProjectDocHTMLGenerator projectGenerator, T source) {
		super(projectGenerator, source);
	}

	public DGHTMLGenerator(ProjectDocHTMLGenerator projectGenerator, T source, String templateName) {
		super(projectGenerator, source, templateName);
	}

	public DGHTMLGenerator(ProjectDocHTMLGenerator projectGenerator, T source, String templateName, String identifier, String fileName,
			TOCEntry entry) {
		super(projectGenerator, source, templateName, identifier, fileName, entry);
	}

	@Override
	public boolean isCodeAlreadyGenerated() {
		return getGeneratedCode() != null;
	}

	public static String splitOnUpperCase(String s) {
		if (s == null || s.trim().length() == 0) {
			return "";
		}
		Matcher m = UPPER_CASE_PATTERN.matcher(s);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			if (sb.length() == 0 && m.start() == 0) {
				m.appendReplacement(sb, "$0");
			} else {
				m.appendReplacement(sb, "&#8203;$0");
			}
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
		return (repository).getHTMLSymbolicDirectory();
	}

	/**
	 * Overrides getFileExtension
	 * 
	 * @see org.openflexo.dg.DGGenerator#getFileExtension()
	 */
	@Override
	public String getFileExtension() {
		return getHTMLFileExtension();
	}

	public static String getValidReference(String label) {
		return BAD_CHARACTERS_PATTERN.matcher(label).replaceAll("-");
	}

	protected FlexoCSS getCss() {
		return getObject().getProject().getCssSheet();
	}

	@Override
	public GeneratedCodeResult getGeneratedCode() {
		if (generatedCode == null && htmlResource != null && htmlResource.getASCIIFile() != null
				&& htmlResource.getASCIIFile().hasLastAcceptedContent()) {
			generatedCode = new GeneratedTextResource(getFileName(), htmlResource.getASCIIFile().getLastAcceptedContent());
		}
		return super.getGeneratedCode();
	}

	public void setHtmlResource(HTMLFileResource<DGHTMLGenerator<T>> htmlResource) {
		this.htmlResource = htmlResource;
	}

}
