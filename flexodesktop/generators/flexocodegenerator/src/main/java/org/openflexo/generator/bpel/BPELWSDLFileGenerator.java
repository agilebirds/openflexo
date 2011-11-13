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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.bpel.BPELPartnerLink;
import org.openflexo.foundation.bpel.BPELPartnerLinkSet;
import org.openflexo.foundation.bpel.BPELWriter;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.generator.utils.MetaFileGenerator;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.ToolBox;

public class BPELWSDLFileGenerator extends MetaFileGenerator {
	private final Logger logger = FlexoLogger.getLogger(BPELWSDLFileGenerator.class.getPackage().getName());

	private static final String IDENTIFIER = "BPEL_WSDL_FILE";
	private final FlexoProcess _webService;

	public BPELWSDLFileGenerator(ProjectGenerator projectGenerator, FlexoProcess webService) {
		super(projectGenerator, FileFormat.WSDL, ResourceType.WSDL, ToolBox.cleanStringForJava(webService.getName()) + ".wsdl", IDENTIFIER
				+ "_" + ToolBox.cleanStringForJava(webService.getName()));
		_webService = webService;
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	public FlexoProcess getWebService() {
		return _webService;
	}

	@Override
	public void generate(boolean forceRegenerate) {
		/*
		 * Note that the method we're using here is ugly :
		 * We just open the file, go to the </definition> end tag,
		 * and insert some other XML just in front of it...
		 * So if the partnerLinks had already been defined, or that the same prefixes have already been used,
		 * we may be in troubles. Fortunately, in those unlikely cases, the wsdl can 
		 * be edited by hand and the modifications roundtripped.
		 */

		if (!forceRegenerate && !needsGeneration()) {
			return;
		}
		try {
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + getIdentifier(), false);
			startGeneration();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Generating " + getFileName() + " as " + wsdlFileResource);
			}

			// the generated WSDL is the original WSDL file to which we've added
			// the partner link type definitions.
			String generatedWSDL = new String();

			FlexoProcess toTranslate = null;
			for (FlexoProcess p : this.getProject().getAllLocalFlexoProcesses()) {
				if ((p.getPortRegistery().getAllPorts().size() >= 1) && !p.getIsWebService()) {
					toTranslate = p;
				}
			}

			BPELWriter writer = getProjectGenerator().getInstance(toTranslate, false);
			BPELPartnerLinkSet pLinks = writer.getPartnerLinkSet();
			BPELPartnerLink pLink = pLinks.getPartnerLink(this.getWebService().getName());

			if (pLink == null) {
				throw new GenerationException("No PartnerLink definition could be found for webService : " + this.getWebService().getName());
			}

			ServiceInterface si = pLink.getServiceInterface();

			File currentWSDLFile = si.getParentService().getWSDLFile().getFile();
			FileReader fr = new FileReader(currentWSDLFile);
			BufferedReader bfr = new BufferedReader(fr);

			StringBuffer WSDLContent = new StringBuffer();
			String currentLine;
			while ((currentLine = bfr.readLine()) != null) {
				WSDLContent.append(currentLine);
				WSDLContent.append("\n");
			}

			String WSDLContentString = new String(WSDLContent);
			int placeToInsert = WSDLContentString.lastIndexOf("definitions>");
			placeToInsert = WSDLContentString.lastIndexOf("</");

			String firstPart = WSDLContentString.substring(0, placeToInsert);

			String secondPart = "<!-- PartnerLink definition added by Flexo --> \n"
					+ writer.getStringPartnerLinkTypeDefinition(pLink.getName()) + "\n";
			String thirdPart = WSDLContentString.substring(placeToInsert);

			generatedWSDL = firstPart + secondPart + thirdPart;

			generatedCode = new GeneratedTextResource(getFileName(), generatedWSDL);
			stopGeneration();
		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) {
			e.printStackTrace();
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		}
	}

	private BPELWSDLFileResource wsdlFileResource;

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		wsdlFileResource = (BPELWSDLFileResource) resourceForKeyWithCGFile(ResourceType.WSDL,
				BPELWSDLFileResource.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (wsdlFileResource == null) {
			wsdlFileResource = GeneratedFileResourceFactory.createWSDLFileResource(repository, this);
			logger.info("Created BPEL resource " + wsdlFileResource.getName());
		} else {
			wsdlFileResource.setGenerator(this);
			logger.info("Successfully retrieved BPEL-WSDL resource " + wsdlFileResource.getName());
		}
		wsdlFileResource.setFileType(ResourceType.WSDL);
		wsdlFileResource.registerObserverWhenRequired();
		resources.add(wsdlFileResource);
		textResource = wsdlFileResource;
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
