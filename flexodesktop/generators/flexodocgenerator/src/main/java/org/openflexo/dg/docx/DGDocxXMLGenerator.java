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
package org.openflexo.dg.docx;

import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.velocity.VelocityContext;

import org.openflexo.dg.latex.DocGeneratorConstants;
import org.openflexo.dg.rm.DocxXmlFileResource;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.cg.utils.DocConstants;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.ie.IEPageComponent;
import org.openflexo.foundation.ie.IEPopupComponent;
import org.openflexo.foundation.ie.IETabComponent;
import org.openflexo.foundation.ie.menu.FlexoNavigationMenu;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.generator.Generator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.logging.FlexoLogger;

public class DGDocxXMLGenerator<T extends FlexoModelObject> extends Generator<T, DGRepository> implements IFlexoResourceGenerator {
	protected static final String BAD_CHARACTERS_REG_EXP = "['\"&}%#~\\s_?+:/\\\\]";
	protected static final Pattern BAD_CHARACTERS_PATTERN = Pattern.compile(BAD_CHARACTERS_REG_EXP);

	protected static final Logger logger = FlexoLogger.getLogger(DGDocxXMLGenerator.class.getPackage().getName());

	private String identifier;
	private LinkedHashMap<DocxXmlFileResource<DGDocxXMLGenerator<T>>, DocxTemplatesEnum> docxResources;

	public DGDocxXMLGenerator(ProjectDocDocxGenerator projectGenerator, T source, String identifier) {
		super(projectGenerator, source);
		this.docxResources = new LinkedHashMap<DocxXmlFileResource<DGDocxXMLGenerator<T>>, DocxTemplatesEnum>();
		this.identifier = identifier;
	}

	public CGSymbolicDirectory getSymbolicDirectory(DGRepository repository) {
		return repository.getDocxSymbolicDirectory();
	}

	@Override
	public GeneratedDocxXmlResource getGeneratedCode() {
		if (generatedCode == null) {
			GeneratedDocxXmlResource generatedCodeTmp = new GeneratedDocxXmlResource(getIdentifier());
			for (DocxXmlFileResource<DGDocxXMLGenerator<T>> resource : docxResources.keySet()) {
				if (resource.getASCIIFile() == null || !resource.getASCIIFile().hasLastAcceptedContent()) {
					generatedCodeTmp = null;
					break;
				}
				generatedCodeTmp.addCode(getDocxTemplateForResource(resource), resource.getASCIIFile().getLastAcceptedContent());
			}

			generatedCode = generatedCodeTmp;
		}
		return (GeneratedDocxXmlResource) generatedCode;
	}

	@Override
	public void buildResourcesAndSetGenerators(DGRepository repository, Vector<CGRepositoryFileResource> resources) {
		getProjectGenerator().refreshConcernedResources();
	}

	@Override
	public void generate(boolean forceRegenerate) {
		if (!forceRegenerate && !needsGeneration())
			return;

		startGeneration();
		try {
			VelocityContext context = defaultContext();

			generatedCode = new GeneratedDocxXmlResource(getIdentifier());
			for (DocxXmlFileResource<DGDocxXMLGenerator<T>> resource : docxResources.keySet())
				((GeneratedDocxXmlResource) generatedCode).addCode(getDocxTemplateForResource(resource),
						merge(getTemplateNameForResource(resource), context));
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

	@Override
	protected VelocityContext defaultContext() {
		VelocityContext context = super.defaultContext();
		context.put("DocSection", DocConstants.DocSection.class);
		return context;
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	public void addDocxResource(DocxXmlFileResource<DGDocxXMLGenerator<T>> docxResource, DocxTemplatesEnum docxTemplate) {
		docxResources.put(docxResource, docxTemplate);
	}

	public DocxTemplatesEnum getDocxTemplateForResource(DocxXmlFileResource<DGDocxXMLGenerator<T>> docxResource) {
		return docxResources.get(docxResource);
	}

	public String getGenerationResultKeyForResource(DocxXmlFileResource<DGDocxXMLGenerator<T>> docxResource) {
		return docxResources.get(docxResource).toString();
	}

	public String getFileNameForResource(DocxXmlFileResource<DGDocxXMLGenerator<T>> docxResource) {
		return docxResources.get(docxResource).getFilePath();
	}

	public String getTemplateNameForResource(DocxXmlFileResource<DGDocxXMLGenerator<T>> docxResource) {
		return docxResources.get(docxResource).getTemplatePath();
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
}
