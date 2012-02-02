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
package org.openflexo.dgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Logger;


import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.PresentationRepository;
import org.openflexo.foundation.cg.action.ConnectCGRepository;
import org.openflexo.foundation.param.DirectoryParameter;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.ptoc.PTOCRepository;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ConnectCGRepositoryInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ConnectCGRepositoryInitializer(DGControllerActionInitializer actionInitializer)
	{
		super(ConnectCGRepository.actionType,actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() 
	{
		return (DGControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ConnectCGRepository> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<ConnectCGRepository>() {
			@Override
			public boolean run(ActionEvent e, ConnectCGRepository action)
			{
				if (!(action.getFocusedObject() instanceof DGRepository))
					return false;
				DGRepository repository = (DGRepository) action.getFocusedObject();
				if (repository == null)
					return false;
				if (repository.getSourceCodeRepository().getDirectory() == null) {
					repository.getSourceCodeRepository().setDirectory(new File(System.getProperty("user.home")));
				}
				if (repository.getPostBuildRepository().getDirectory() == null) {
					repository.getPostBuildRepository().setDirectory(new File(System.getProperty("user.home")));
				}
				TextFieldParameter paramName = new TextFieldParameter("name", "dg_repository_name", repository.getDisplayName());
				DirectoryParameter paramDir = new DirectoryParameter("directory", "source_directory", repository.getSourceCodeRepository()
						.getDirectory());
				TextFieldParameter paramWarName = new TextFieldParameter("pdfName", "pdf_name", repository.getPostProductName());
				DirectoryParameter paramWarDir = new DirectoryParameter("pdfDirectory", "pdf_directory", repository.getPostBuildRepository()
						.getDirectory());
				DynamicDropDownParameter<TOCRepository> repositoryParam = new DynamicDropDownParameter<TOCRepository>("repository","table_of_content",repository.getTocRepository());
				repositoryParam.setAvailableValues(getProject().getTOCData().getRepositories());
				repositoryParam.setFormatter("title");
				//MOS				
				repositoryParam.setDepends("format");
				repositoryParam.setConditional("format!=HTML&&format!=PPTX");
				
				DynamicDropDownParameter<PTOCRepository> paramPToc = new DynamicDropDownParameter<PTOCRepository>("toc","toc",getProject().getPTOCData().getRepositories(),getProject().getPTOCData().getRepositories().firstElement());
				paramPToc.setFormatter("title");
				paramPToc.setDepends("format");
				paramPToc.setConditional("format!=HTML&&format!=DOCX&&format!=LATEX&&format!=PDF");
				ParameterDefinition<?>[] pd = new ParameterDefinition<?>[5];
				pd[0] = paramName;
				pd[1] = paramDir;
				pd[2] = paramWarName;
				pd[3] = paramWarDir;
				pd[4] = repository.getFormat() == Format.PPTX?paramPToc:repositoryParam;
				
				//
				
				AskParametersDialog dialog =AskParametersDialog.createAskParametersDialog(getProject(), null, FlexoLocalization
								        		.localizedForKey("connect_repository_to_local_file_system"), FlexoLocalization
								        		.localizedForKey("enter_parameters_for_connecting_repository_to_the_local_file_system"),
								        		pd);
				System.setProperty("apple.awt.fileDialogForDirectories", "false");
                if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
                	if(repository instanceof PresentationRepository){
                		((PresentationRepository)repository).setPTocRepository(paramPToc.getValue());
                	}else{
                	repository.setTocRepository(repositoryParam.getValue());
                	}
					//try {
						repository.setDisplayName(paramName.getValue());
						repository.setDirectory(paramDir.getValue());
						repository.setPostProductName(paramWarName.getValue());
						repository.setPostBuildDirectory(paramWarDir.getValue());
//					} catch (DuplicateCodeRepositoryNameException e2) {
//						e2.printStackTrace();
//						FlexoController.notify(FlexoLocalization.localizedForKey("wrong_name"));
//						return false;
//					}
					if (!repository.getSourceCodeRepository().isConnected()) {
						FlexoController.notify(FlexoLocalization.localizedForKey("sorry_invalid_directory"));
						return false;
					}
					
					//MOS
					if(repository.getFormat()==Format.PPTX)
						try {
							return ((PresentationRepository) repository).getPTocRepository()!=null; 
						}catch(Exception exc){
							return false;
						}
					if (!(repository.getFormat()==Format.HTML || (repository.getFormat()!=Format.HTML && repository.getTocRepository()!=null) )) {
						FlexoController.notify(FlexoLocalization.localizedForKey("you_must_choose_a_toc"));
						return false;
					}
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ConnectCGRepository> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<ConnectCGRepository>() {
			@Override
			public boolean run(ActionEvent e, ConnectCGRepository action)
			{
				return true;
			}
		};
	}


}
