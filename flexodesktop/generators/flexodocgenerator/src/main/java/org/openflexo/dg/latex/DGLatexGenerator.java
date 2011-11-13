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
package org.openflexo.dg.latex;

import java.awt.Color;
import java.io.File;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.openflexo.dg.DGGenerator;
import org.openflexo.dg.rm.LatexFileResource;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.generator.GeneratedCodeResult;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.ie.widget.IEDynamicImage;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.TemplateNotFoundException;
import org.openflexo.toolbox.HTMLUtils;
import org.openflexo.toolbox.LatexUtils;

/**
 * @author gpolet
 * 
 */
public class DGLatexGenerator<T extends FlexoModelObject> extends DGGenerator<T> implements IFlexoResourceGenerator {
	protected static final String UPPER_CASE_REGEXP = "[A-Z]+";

	protected static final Pattern UPPER_CASE_PATTERN = Pattern.compile(UPPER_CASE_REGEXP);

	protected static final String LATEX_BACKSLASH = HTMLUtils.LATEX_BACKSLASH;

	protected static final String JAVA_BACKSLASH = "\\";

	protected static final String LATEX_TAG_REGEXP = "^\\\\[^ {]+(\\{[^}]*\\}\\s*)*";

	protected static final Pattern LATEX_TAG_PATTERN = Pattern.compile(LATEX_TAG_REGEXP);

	protected static final String CHARS_TO_ESCAPE_REGEXP = "[\\\\{}_&%#~]";

	protected static final Pattern CHARS_TO_ESCAPE_PATTERN = Pattern.compile(CHARS_TO_ESCAPE_REGEXP);

	protected static final Logger logger = Logger.getLogger(DGLatexGenerator.class.getPackage().getName());

	protected LatexFileResource<DGLatexGenerator<T>> latexResource;

	public static String getLatexFileExtension() {
		return ".tex";
	}

	public static String nameForObject(FlexoModelObject object, DGRepository repository) {
		return nameForObjectNoExt(object, repository) + getLatexFileExtension();
	}

	public static String nameForProject(DGRepository repository) {
		return nameForProjectNoExt(repository) + getLatexFileExtension();
	}

	public static String nameForProcess(FlexoProcess process, DGRepository repository) {
		return nameForProcessNoExt(process, repository) + getLatexFileExtension();
	}

	public static String nameForDKV(DGRepository repository) {
		return nameForDKVNoExt(repository) + getLatexFileExtension();
	}

	public static String nameForOperation(OperationNode operation, DGRepository repository) {
		return nameForOperationNoExt(operation, repository) + getLatexFileExtension();
	}

	public static String nameForMenu(DGRepository repository) {
		return nameForMenuNoExt(repository) + getLatexFileExtension();
	}

	public static String nameForComponent(ComponentDefinition cd, DGRepository repository) {
		return nameForComponentNoExt(cd, repository) + getLatexFileExtension();
	}

	public static String nameForDataModel(DGRepository repository) {
		return nameForDataModelNoExt(repository) + getLatexFileExtension();
	}

	public static String nameForComponentLibrary(DGRepository repository) {
		return nameForComponentLibraryNoExt(repository) + getLatexFileExtension();
	}

	public static String nameForWorkflow(DGRepository repository) {
		return nameForWorkflowNoExt(repository) + getLatexFileExtension();
	}

	public static String nameForReadersGuide(DGRepository repository) {
		return nameForReadersGuideNoExt(repository) + getLatexFileExtension();
	}

	public static String nameForDefinitions(DGRepository repository) {
		return nameForDefinitionsNoExt(repository) + getLatexFileExtension();
	}

	public static String nameForRepository(DMRepository dmr, DGRepository repository) {
		return nameForRepositoryNoExt(dmr, repository) + getLatexFileExtension();
	}

	public static String nameForEntity(DMEntity entity, DGRepository repository) {
		return nameForEntityNoExt(entity, repository) + getLatexFileExtension();
	}

	protected DGLatexGenerator(ProjectDocLatexGenerator projectGenerator, T source) {
		super(projectGenerator, source);
	}

	public DGLatexGenerator(ProjectDocLatexGenerator projectGenerator, T source, String templateName) {
		super(projectGenerator, source, templateName);
	}

	public DGLatexGenerator(ProjectDocLatexGenerator projectGenerator, T source, String templateName, String identifier, String fileName,
			TOCEntry entry) {
		super(projectGenerator, source, templateName, identifier, fileName, entry);
	}

	@Override
	public ProjectDocLatexGenerator getProjectGenerator() {
		return (ProjectDocLatexGenerator) super.getProjectGenerator();
	}

	public String getBlockTemplatePath() {
		try {
			return templateWithName("block.tex.vm").getRelativePath();
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
			return "block.tex.vm";
		}
	}

	public static String convertHTML2Latex(String htmlString) {
		String converted = HTMLUtils.convertHTML2Latex(htmlString);
		if (converted == null)
			return "";
		else
			return converted;
	}

	public static String prepareJavaStringForLatex(String javaString) {
		return LatexUtils.prepareJavaStringForLatex(javaString);
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
				m.appendReplacement(sb, "\\\\-$0");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public CGSymbolicDirectory getSymbolicDirectory(DGRepository repository) {
		return repository.getLatexSymbolicDirectory();
	}

	/**
	 * Overrides getFileExtension
	 * 
	 * @see org.openflexo.dg.DGGenerator#getFileExtension()
	 */
	@Override
	public String getFileExtension() {
		return getLatexFileExtension();
	}

	public String getSystemActions(IEHyperlinkWidget button) {
		if (button == null)
			return "";
		StringBuilder sb = new StringBuilder();
		if (button.getPopupComponentDefinition() != null) {
			sb.append("opens popup " + "\\hyperlink{" + getReference(button.getPopupComponentDefinition()) + "}{"
					+ button.getPopupComponentDefinition().getComponentName() + "}");
		}
		if (button.getLink() != null && button.getLink().trim().length() > 0) {
			if (sb.length() > 0)
				sb.append("\\par ");
			String lnk = LatexUtils.prepareJavaStringForLatex(button.getLink());
			if (button.getFuncName() != null && button.getFuncName().trim().length() > 0)
				sb.append("\\href{" + lnk + "}{" + button.getFuncName() + "}");
			else
				sb.append("\\href{" + lnk + "}{" + lnk + "}");
		}
		return sb.toString();
	}

	protected FlexoCSS getCss() {
		return getObject().getProject().getCssSheet();
	}

	public String getImageString(IEHyperlinkWidget button) {
		if (button == null)
			return "";
		if (button.isCustomButton()) {
			StringBuilder sb = new StringBuilder();
			Color c = getCss().getTextColor();
			sb.append(Math.round(c.getRed() * 10000 / 255) / 10000d);
			sb.append(',');
			sb.append(Math.round(c.getGreen() * 10000 / 255) / 10000d);
			sb.append(',');
			sb.append(Math.round(c.getBlue() * 10000d / 255d) / 10000d);
			return "\\custombutton[" + sb.toString() + "]{" + button.getValue() + "}";
		} else if (button instanceof IEDynamicImage) {
			return "\\emph{A dynamic image}";
		} else if (button instanceof IEButtonWidget) {
			String fullName = ((IEButtonWidget) button).getImageName();
			String fileName = cleanFileName(fullName.substring(0, fullName.lastIndexOf('.'))) + ".jpg";
			while (fileName.startsWith("-") && fileName.length() > 2) {
				fileName = fileName.substring(1);
			}
			File file = new File(getRepository().getFiguresSymbolicDirectory().getDirectory().getFile().getAbsolutePath() + "/" + fileName);
			fileName = getRepository().getFiguresSymbolicDirectory().getName() + "/" + fileName;
			((IEButtonWidget) button).getFile().createButton(file);
			StringBuilder sb = new StringBuilder();
			sb.append("\\buttonfigure{").append(fileName).append("}");
			return sb.toString();
		} else {
			IEHyperlinkWidget link = button;
			return "\\bluehyperlink{" + link.getValue() + "}";
		}
	}

	@Override
	protected VelocityContext defaultContext() {
		VelocityContext vc = super.defaultContext();
		TOCEntry entry = getTOCEntry();
		if (entry != null)
			vc.put("entry", entry);
		return vc;
	}

	/**
	 * Overrides merge
	 * 
	 * @throws Exception
	 * @throws ParseErrorException
	 * @throws ResourceNotFoundException
	 * @see org.openflexo.dg.DGGenerator#merge(java.lang.String, org.apache.velocity.VelocityContext)
	 */
	@Override
	public String merge(String templateName, VelocityContext velocityContext) throws GenerationException {
		String result = super.merge(templateName, velocityContext);
		return result.replaceAll("\\s*?\n\\s*?\n\\s*", "\n\n");
	}

	@Override
	public GeneratedCodeResult getGeneratedCode() {
		if (generatedCode == null && latexResource != null && latexResource.getASCIIFile() != null
				&& latexResource.getASCIIFile().hasLastAcceptedContent()) {
			generatedCode = new GeneratedTextResource(getFileName(), latexResource.getASCIIFile().getLastAcceptedContent());
		}
		return super.getGeneratedCode();
	}

	public void setLatexResource(LatexFileResource<DGLatexGenerator<T>> latexResource) {
		this.latexResource = latexResource;
	}

}
