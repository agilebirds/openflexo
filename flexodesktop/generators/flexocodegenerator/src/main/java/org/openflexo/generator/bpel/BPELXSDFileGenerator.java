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
package org.openflexo.generator.bpel;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.bpel.BPELWriter;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.generator.utils.MetaFileGenerator;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.wsdl.Exporter;

public class BPELXSDFileGenerator extends MetaFileGenerator {
	private final Logger logger = FlexoLogger.getLogger(BPELXSDFileGenerator.class.getPackage().getName());

	private static final String IDENTIFIER = "BPEL_XSD_FILE";

	public BPELXSDFileGenerator(ProjectGenerator projectGenerator) {
		super(projectGenerator, FileFormat.XSD, ResourceType.XSD, ToolBox
				.cleanStringForJava(projectGenerator.getProject().getProjectName()) + ".xsd", IDENTIFIER + "_"
				+ ToolBox.cleanStringForJava(projectGenerator.getProject().getProjectName()));
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	public void generate(boolean forceRegenerate) {
		if (!forceRegenerate && !needsGeneration()) {
			return;
		}
		try {
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + getIdentifier(), false);
			startGeneration();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Generating " + getFileName());
			}
			Exporter exp = new Exporter();

			FlexoProcess toTranslate = null;
			for (FlexoProcess p : this.getProject().getAllLocalFlexoProcesses()) {
				if ((p.getPortRegistery().getAllPorts().size() >= 1) && !p.getIsWebService()) {
					toTranslate = p;
				}
			}

			BPELWriter writer = getProjectGenerator().getInstance(toTranslate, false);

			// String generatedXSD="Generated XSD";
			String generatedXSD = exp.export(writer, writer.getExportedPartnerLink());

			if (generatedXSD == null) {
				throw new GenerationException(
						"The Web Service Definition could not be created. Check the Interface Definition of your Flexo Process");
			}

			generatedCode = new GeneratedTextResource(getFileName(), generatedXSD);
			stopGeneration();
		} catch (GenerationException e) {
			e.printStackTrace();
			setGenerationException(e);
		} catch (Exception e) {
			e.printStackTrace();
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		}
	}

	private BPELXSDFileResource xsdFileResource;

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		xsdFileResource = (BPELXSDFileResource) resourceForKeyWithCGFile(ResourceType.WSDL,
				BPELXSDFileResource.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (xsdFileResource == null) {
			xsdFileResource = GeneratedFileResourceFactory.createXSDFileResource(repository, this);
			logger.info("Created BPEL resource " + xsdFileResource.getName());
		} else {
			xsdFileResource.setGenerator(this);
			logger.info("Successfully retrieved BPEL-XSD resource " + xsdFileResource.getName());
		}
		xsdFileResource.setFileType(ResourceType.WSDL);
		xsdFileResource.registerObserverWhenRequired();
		resources.add(xsdFileResource);
		textResource = xsdFileResource;
	}

	@Override
	public String getRelativePath() {
		return "";
	}

	@Override
	public CGSymbolicDirectory getSymbolicDirectory(CGRepository repository) {
		return repository.getProjectSymbolicDirectory();
	}

}
