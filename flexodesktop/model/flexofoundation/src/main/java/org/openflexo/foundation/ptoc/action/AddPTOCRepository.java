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
package org.openflexo.foundation.ptoc.action;

import java.util.Vector;

import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InvalidArgumentException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ptoc.PTOCData;
import org.openflexo.foundation.ptoc.PTOCObject;
import org.openflexo.foundation.ptoc.PTOCRepository;
import org.openflexo.localization.FlexoLocalization;

public class AddPTOCRepository extends FlexoAction<AddPTOCRepository, FlexoModelObject, PTOCObject> {

	public static final FlexoActionType<AddPTOCRepository, FlexoModelObject, PTOCObject> actionType
	= new FlexoActionType<AddPTOCRepository, FlexoModelObject, PTOCObject>("add_toc", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object,
				Vector<PTOCObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object,
				Vector<PTOCObject> globalSelection) {
			return true;
		}

		@Override
		public AddPTOCRepository makeNewAction(FlexoModelObject focusedObject,
				Vector<PTOCObject> globalSelection, FlexoEditor editor) {
			return new AddPTOCRepository(focusedObject,globalSelection,editor);
		}

	};

	private String repositoryName;

	private DocType docType;

	private PTOCRepository newRepository;
	private PTOCRepository ptocTemplate;

	public PTOCRepository getTocTemplate() {
		return ptocTemplate;
	}

	public void setTocTemplate(PTOCRepository ptocTemplate) {
		this.ptocTemplate = ptocTemplate;
	}

	protected AddPTOCRepository(FlexoModelObject focusedObject,
			Vector<PTOCObject> globalSelection, FlexoEditor editor){
		super(actionType,focusedObject,globalSelection,editor);

	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getRepositoryName()==null || getRepositoryName().trim().length()==0)
			throw new InvalidArgumentException(FlexoLocalization.localizedForKey("name_cannot_be_empty"),"name_cannot_be_empty");
		String attempt = getRepositoryName();
		int i = 1;
		while (getData().getRepositoryWithTitle(attempt)!=null)
			attempt=getRepositoryName()+"-"+i++;
		
		
		newRepository = new PTOCRepository(getData(), getDocType(), getTocTemplate());
		newRepository.setTitle(attempt);
		getData().addToRepositories(newRepository);
	}

	private PTOCData getData() {
		return getFocusedObject().getProject().getPTOCData();
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public DocType getDocType() {
		return docType;
	}

	public void setDocType(DocType docType) {
		this.docType = docType;
	}

	public PTOCRepository getNewRepository() {
		return newRepository;
	}

}
