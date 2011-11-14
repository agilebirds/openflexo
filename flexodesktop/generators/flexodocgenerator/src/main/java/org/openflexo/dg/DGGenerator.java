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
package org.openflexo.dg;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.dg.latex.DocGeneratorConstants;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.ie.IEPageComponent;
import org.openflexo.foundation.ie.IEPopupComponent;
import org.openflexo.foundation.ie.IETabComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.menu.FlexoNavigationMenu;
import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.generator.Generator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public abstract class DGGenerator<T extends FlexoModelObject> extends Generator<T, DGRepository> implements FlexoObserver {

	protected static final Logger logger = FlexoLogger.getLogger(DGGenerator.class.getPackage().getName());

	protected static final String BAD_CHARACTERS_REG_EXP = "[\"'&}%#~\\s_?+:/\\\\]";

	protected static final Pattern BAD_CHARACTERS_PATTERN = Pattern.compile(BAD_CHARACTERS_REG_EXP);

	protected static final String BAD_LATEX_FILE_NAME_CHARACTERS_REG_EXP = "[^-A-Za-z0-9.]";

	protected static final Pattern BAD_LATEX_FILE_NAME_CHARACTERS_PATTERN = Pattern.compile(BAD_LATEX_FILE_NAME_CHARACTERS_REG_EXP);

	public static final double FULL_PAGE_RATIO = 654.0d / 458.0d;

	public static void main(String[] args) {
		String dirty = "Coucou-'blabla-'\"\"--And---oiuzeoi-------13558";
		String clean = getValidReference(dirty);
		System.out.println(dirty + "\n" + clean);
	}

	public static double FULL_PAGE_RATIO() {
		return FULL_PAGE_RATIO;
	}

	public static double getImageRatio(Icon image) {
		return ((double) image.getIconHeight()) / ((double) image.getIconWidth());
	}

	private boolean isGenerating = false;

	private String templateName;

	private String identifier;

	private String fileName;

	private TOCEntry entry;

	public static String cleanFileName(String fileName) {
		return BAD_LATEX_FILE_NAME_CHARACTERS_PATTERN.matcher(fileName).replaceAll("-");
	}

	public static String removeNewLines(String text) {
		if (text == null) {
			return null;
		}
		String replaced = text.replaceAll("(\r*\n)+", " ");
		return replaced;
	}

	public static String nameForObjectNoExt(FlexoModelObject object, DGRepository repository) {
		if (object instanceof ComponentDefinition) {
			return nameForComponentNoExt((ComponentDefinition) object, repository);
		} else if (object instanceof OperationNode) {
			return nameForOperationNoExt((OperationNode) object, repository);
		} else if (object instanceof FlexoProcess) {
			return nameForProcessNoExt((FlexoProcess) object, repository);
		} else if (object instanceof ProcessFolder) {
			return nameForProcessFolderNoExt((ProcessFolder) object, repository);
		} else if (object instanceof DMEntity) {
			return nameForEntityNoExt((DMEntity) object, repository);
		} else if (object instanceof DMRepository) {
			return nameForRepositoryNoExt((DMRepository) object, repository);
		} else if (object instanceof DMModel) {
			return nameForDataModelNoExt(repository);
		} else if (object instanceof DKVModel) {
			return nameForDKVNoExt(repository);
		} else if (object instanceof FlexoWorkflow) {
			return nameForWorkflowNoExt(repository);
		} else if (object instanceof FlexoComponentLibrary) {
			return nameForComponentLibraryNoExt(repository);
		} else if (object instanceof FlexoNavigationMenu) {
			return nameForMenuNoExt(repository);
		} else if (object instanceof FlexoProject) {
			return nameForProjectNoExt(repository);
		} else if (logger.isLoggable(Level.WARNING)) {
			logger.warning("So far there is no known way to name a " + object);
		}
		return null;
	}

	public static String nameForProjectNoExt(DGRepository repository) {
		return "main";
	}

	public static String nameForProcessNoExt(FlexoProcess process, DGRepository repository) {
		return cleanFileName(repository.getName() + ".process." + process.getName());
	}

	public static String nameForProcessFolderNoExt(ProcessFolder processFolder, DGRepository repository) {
		return cleanFileName(repository.getName() + ".processfolder." + processFolder.getName() + "-" + processFolder.getFlexoID());
	}

	public static String nameForDKVNoExt(DGRepository repository) {
		return cleanFileName(repository.getName() + ".dkv");
	}

	public static String nameForOperationNoExt(OperationNode operation, DGRepository repository) {
		return cleanFileName(repository.getName() + ".operation." + operation.getProcess().getName() + "."
				+ operation.getAbstractActivityNode().getName() + "." + operation.getName());
	}

	public static String nameForMenuNoExt(DGRepository repository) {
		return cleanFileName(repository.getName() + ".menu");
	}

	public static String nameForComponentNoExt(ComponentDefinition cd, DGRepository repository) {
		if (cd instanceof OperationComponentDefinition) {
			return cleanFileName(repository.getName() + ".screen." + cd.getName());
		} else if (cd instanceof PopupComponentDefinition) {
			return cleanFileName(repository.getName() + ".popup." + cd.getName());
		} else if (cd instanceof TabComponentDefinition) {
			return cleanFileName(repository.getName() + ".tab." + cd.getName());
		} else {
			return cleanFileName(repository.getName() + ".component." + cd.getName());
		}
	}

	public static String nameForDataModelNoExt(DGRepository repository) {
		return cleanFileName(repository.getName() + ".dm");
	}

	public static String nameForComponentLibraryNoExt(DGRepository repository) {
		return cleanFileName(repository.getName() + ".cl");
	}

	public static String nameForWorkflowNoExt(DGRepository repository) {
		return cleanFileName(repository.getName() + ".wkf");
	}

	public static String nameForReadersGuideNoExt(DGRepository repository) {
		return cleanFileName(repository.getName() + ".readersguide");
	}

	public static String nameForDefinitionsNoExt(DGRepository repository) {
		return cleanFileName(repository.getName() + ".definitions");
	}

	public static String nameForRepositoryNoExt(DMRepository dmr, DGRepository repository) {
		return cleanFileName(repository.getName() + ".repository." + dmr.getName());
	}

	public static String nameForEntityNoExt(DMEntity entity, DGRepository repository) {
		if (entity instanceof DMEOEntity) {
			return cleanFileName(repository.getName() + ".eoentity." + entity.getName());
		} else {
			return cleanFileName(repository.getName() + ".entity." + entity.getName());
		}
	}

	public String realNameForProjectNoExt(DGRepository repository) {
		CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> r = getProjectGenerator()
				.getResourceForObject(repository.getProject());
		if (r != null) {
			String fileName = r.getFileName();
			if (fileName.endsWith(getFileExtension())) {
				return fileName.substring(0, fileName.length() - getFileExtension().length());
			}
			return fileName;
		}
		return nameForProjectNoExt(repository);
	}

	public String realNameForProcessNoExt(FlexoProcess process, DGRepository repository) {
		CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> r = getProjectGenerator()
				.getResourceForObject(process);
		if (r != null) {
			String fileName = r.getFileName();
			if (fileName.endsWith(getFileExtension())) {
				return fileName.substring(0, fileName.length() - getFileExtension().length());
			}
			return fileName;
		}
		return nameForProcessNoExt(process, repository);
	}

	public String realNameForDKVNoExt(DGRepository repository) {
		CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> r = getProjectGenerator()
				.getResourceForObject(repository.getProject().getDKVModel());
		if (r != null) {
			String fileName = r.getFileName();
			if (fileName.endsWith(getFileExtension())) {
				return fileName.substring(0, fileName.length() - getFileExtension().length());
			}
			return fileName;
		}
		return nameForDKVNoExt(repository);
	}

	public String realNameForOperationNoExt(OperationNode operation, DGRepository repository) {
		CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> r = getProjectGenerator()
				.getResourceForObject(operation);
		if (r != null) {
			String fileName = r.getFileName();
			if (fileName.endsWith(getFileExtension())) {
				return fileName.substring(0, fileName.length() - getFileExtension().length());
			}
			return fileName;
		}
		return nameForOperationNoExt(operation, repository);
	}

	public String realNameForMenuNoExt(DGRepository repository) {
		CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> r = getProjectGenerator()
				.getResourceForObject(repository.getProject().getFlexoNavigationMenu());
		if (r != null) {
			String fileName = r.getFileName();
			if (fileName.endsWith(getFileExtension())) {
				return fileName.substring(0, fileName.length() - getFileExtension().length());
			}
			return fileName;
		}
		return nameForMenuNoExt(repository);
	}

	public String realNameForComponentNoExt(ComponentDefinition cd, DGRepository repository) {
		CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> r = getProjectGenerator()
				.getResourceForObject(cd);
		if (r != null) {
			String fileName = r.getFileName();
			if (fileName.endsWith(getFileExtension())) {
				return fileName.substring(0, fileName.length() - getFileExtension().length());
			}
			return fileName;
		}
		return nameForComponentNoExt(cd, repository);
	}

	public String realNameForDataModelNoExt(DGRepository repository) {
		CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> r = getProjectGenerator()
				.getResourceForObject(repository.getProject().getDataModel());
		if (r != null) {
			String fileName = r.getFileName();
			if (fileName.endsWith(getFileExtension())) {
				return fileName.substring(0, fileName.length() - getFileExtension().length());
			}
			return fileName;
		}
		return nameForDataModelNoExt(repository);
	}

	public String realNameForRepositoryNoExt(DMRepository dmr, DGRepository repository) {
		CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> r = getProjectGenerator()
				.getResourceForObject(dmr);
		if (r != null) {
			String fileName = r.getFileName();
			if (fileName.endsWith(getFileExtension())) {
				return fileName.substring(0, fileName.length() - getFileExtension().length());
			}
			return fileName;
		}
		return nameForRepositoryNoExt(dmr, repository);
	}

	public String realNameForEntityNoExt(DMEntity entity, DGRepository repository) {
		CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> r = getProjectGenerator()
				.getResourceForObject(entity);
		if (r != null) {
			String fileName = r.getFileName();
			if (fileName.endsWith(getFileExtension())) {
				return fileName.substring(0, fileName.length() - getFileExtension().length());
			}
			return fileName;
		}
		return nameForEntityNoExt(entity, repository);
	}

	public static String screenshotName(FlexoModelObject o) {
		return CGSymbolicDirectory.FIGURES
				+ "/"
				+ o.getProject().getScreenshotResource(o, true).getFile().getName()
						.substring(0, o.getProject().getScreenshotResource(o).getFile().getName().length() - 4);
	}

	public static String screenshotName(FlexoModelObject o, ProjectDocGenerator pdg) {
		FlexoCopiedResource r = pdg.getScreenshot(o);
		if (r != null) {
			return CGSymbolicDirectory.FIGURES + "/" + r.getFile().getName().substring(0, r.getFile().getName().length() - 4);
		} else {
			return CGSymbolicDirectory.FIGURES + "/" + "GenerateScreenshot";
		}
	}

	public static Icon screenshot(FlexoModelObject o, ProjectDocGenerator pdg) {
		FlexoCopiedResource r = pdg.getScreenshot(o);
		if (r != null) {
			if (r.getFile().exists()) {
				return new ImageIcon(r.getFile().getAbsolutePath());
			} else {
				return new ImageIcon(r.getResourceToCopy().getFile().getAbsolutePath());
			}
		} else {
			return new ImageIcon(o.getProject().getScreenshotResource(o, true).getFile().getAbsolutePath());
		}
	}

	public static String getReference(FlexoModelObject object) {
		String s = "";
		if (object instanceof FlexoProcess) {
			s = "PROCESS-" + ((FlexoProcess) object).getName();
		} else if (object instanceof DMModel) {
			s = "DMMODEL-" + ((DMModel) object).getName();
		} else if (object instanceof DMEOEntity) {
			s = "DMEOENTITY-" + ((DMEOEntity) object).getName();
		} else if (object instanceof AbstractActivityNode) {
			s = "ACTIVITY-" + ((AbstractActivityNode) object).getName() + "-" + object.getFlexoID();
		} else if (object instanceof OperationNode) {
			s = "OPERATION-" + ((OperationNode) object).getName() + "-" + object.getFlexoID();
		} else if (object instanceof DKVModel) {
			s = "DKVMODEL-" + ((DKVModel) object).getName();
		} else if (object instanceof FlexoNavigationMenu) {
			s = "MENU-" + ((FlexoNavigationMenu) object).getName();
		} else if (object instanceof IEPopupComponent) {
			s = "POPUP-" + ((IEPopupComponent) object).getName();
		} else if (object instanceof IETabComponent) {
			s = "TAB-" + ((IETabComponent) object).getName();
		} else if (object instanceof IEPageComponent) {
			s = "PAGE-" + ((IEPageComponent) object).getName();
		} else {
			s = object.getFullyQualifiedName();
		}
		return getValidReference(DocGeneratorConstants.DG_LABEL_PREFIX + s);
	}

	public static String getValidReference(String label) {
		return BAD_CHARACTERS_PATTERN.matcher(label).replaceAll("-");
	}

	protected DGGenerator(ProjectDocGenerator projectGenerator, T object) {
		super(projectGenerator, object);
	}

	public DGGenerator(ProjectDocGenerator projectGenerator, T source, String templateName) {
		this(projectGenerator, source, templateName, source.getFullyQualifiedName(), nameForObjectNoExt(source,
				projectGenerator.getRepository()), null);
	}

	public DGGenerator(ProjectDocGenerator projectGenerator, T source, String templateName, String identifier, String fileName,
			TOCEntry entry) {
		this(projectGenerator, source);
		this.templateName = templateName;
		this.identifier = identifier;
		this.fileName = fileName;
		this.entry = entry;
	}

	@Override
	public ProjectDocGenerator getProjectGenerator() {
		return (ProjectDocGenerator) super.getProjectGenerator();
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	public abstract String getFileExtension();

	/**
	 * Generate code related to this generator. If this generator may store result, setting forceGenerate flag to false will result in
	 * giving the already generated code (cache scheme).
	 * 
	 * @param forceRegenerate
	 * @throws GenerationException
	 */
	@Override
	public void generate(boolean forceRegenerate) {
		if (!forceRegenerate && !needsGeneration()) {
			return;
		}
		startGeneration();
		try {
			generatedCode = new GeneratedTextResource(getFileName().endsWith(getFileExtension()) ? getFileName() : getFileName()
					+ getFileExtension(), merge(getTemplateName()));
		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unexpected exception occured: " + e.getMessage() + " for " + getObject().getFullyQualifiedName());
			}
			e.printStackTrace();
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		}
		stopGeneration();
	}

	public TOCEntry getTOCEntry() {
		TOCEntry reply = getRepository().getTOCEntryForObject(getObject());
		if (reply == null) {
			return entry;
		}
		return reply;
	}

	@Override
	public void buildResourcesAndSetGenerators(DGRepository repository, Vector<CGRepositoryFileResource> resources) {
		getProjectGenerator().refreshConcernedResources();
	}

	/**
	 * The name of the generated file without the extension.
	 * 
	 * @return the name of the generated file.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * The name of the template to use to generate the associated file of this generator
	 * 
	 * @return the name of the template to use
	 */
	public String getTemplateName() {
		return templateName;
	}

	public String getIdentifier() {
		return identifier;
	}

	@Override
	public DocType getTarget() {
		return (DocType) super.getTarget();
	}
}
