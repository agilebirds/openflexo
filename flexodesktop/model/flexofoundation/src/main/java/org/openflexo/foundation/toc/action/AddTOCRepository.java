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
package org.openflexo.foundation.toc.action;

import java.io.File;
import java.util.Vector;

import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.GeneratedDoc;
import org.openflexo.foundation.cg.action.AddDocType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.toc.TOCObject;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.localization.FlexoLocalization;

public class AddTOCRepository extends FlexoAction<AddTOCRepository, FlexoModelObject, TOCObject> {

	public static final FlexoActionType<AddTOCRepository, FlexoModelObject, TOCObject> actionType = new FlexoActionType<AddTOCRepository, FlexoModelObject, TOCObject>(
			"add_toc", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<TOCObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<TOCObject> globalSelection) {
			return true;
		}

		@Override
		public AddTOCRepository makeNewAction(FlexoModelObject focusedObject, Vector<TOCObject> globalSelection, FlexoEditor editor) {
			return new AddTOCRepository(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, TOCData.class);
		FlexoModelObject.addActionForClass(actionType, GeneratedDoc.class);
	}

	private String repositoryName;

	private DocType docType;

	private TOCRepository newRepository;
	private File tocTemplate;

	public File getTocTemplate() {
		return tocTemplate;
	}

	public void setTocTemplate(File tocTemplate) {
		this.tocTemplate = tocTemplate;
	}

	protected AddTOCRepository(FlexoModelObject focusedObject, Vector<TOCObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (tocTemplate != null && tocTemplate.exists()) {
			newRepository = TOCRepository.createTOCRepositoryFromTemplate(getData(), tocTemplate);
		} else {
			newRepository = TOCRepository.createTOCRepositoryForDocType(getData(), getDocType());
		}
		if (getDocType() != null) {
			newRepository.setDocType(getDocType());
		}
		String attempt = newRepository.getTitle();
		int i = 1;
		boolean updateTitle = false;
		while (getData().getRepositoryWithTitle(attempt) != null) {
			updateTitle = true;
			attempt = newRepository.getTitle() + "-" + i++;
		}
		if (updateTitle) {
			newRepository.setTitle(attempt);
		}
		if (docType == null) {
			String docTypeName = newRepository.getDocTypeAsString();
			DocType docType = getProject().getDocTypeNamed(docTypeName);
			if (docType == null) {
				AddDocType addDocType = AddDocType.actionType.makeNewEmbeddedAction(getProject(), null, this);
				addDocType.setNewName(docTypeName);
				addDocType.doAction();
				if (!addDocType.hasActionExecutionSucceeded()) {
					throw new FlexoException(FlexoLocalization.localizedForKey("could_not_add_doc_type"));
				}
				docType = addDocType.getNewDocType();
			}
		}
		getData().addToRepositories(newRepository);
	}

	private TOCData getData() {
		return getProject().getTOCData();
	}

	private FlexoProject getProject() {
		return getFocusedObject().getProject();
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public DocType getDocType() {
		if (docType == null) {
			return getProject().getDocTypes().get(0);
		}
		return docType;
	}

	public void setDocType(DocType docType) {
		this.docType = docType;
	}

	public TOCRepository getNewRepository() {
		return newRepository;
	}

}
