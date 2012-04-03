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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.action.DuplicateSectionException;
import org.openflexo.foundation.cg.action.InvalidLevelException;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.toc.ModelObjectSection.ModelObjectType;
import org.openflexo.foundation.toc.PredefinedSection;
import org.openflexo.foundation.toc.ProcessSection;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCObject;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;

public class AddTOCEntry extends FlexoAction<AddTOCEntry, TOCObject, TOCObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AddTOCEntry.class.getPackage().getName());

	public static FlexoActionType<AddTOCEntry, TOCObject, TOCObject> actionType = new FlexoActionType<AddTOCEntry, TOCObject, TOCObject>(
			"add_toc_entry", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddTOCEntry makeNewAction(TOCObject focusedObject, Vector<TOCObject> globalSelection, FlexoEditor editor) {
			return new AddTOCEntry(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(TOCObject object, Vector<TOCObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(TOCObject object, Vector<TOCObject> globalSelection) {
			return (object instanceof TOCEntry && ((TOCEntry) object).canHaveChildren());
		}

	};

	AddTOCEntry(TOCObject focusedObject, Vector<TOCObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public static enum KindOfTocEntry {
		NormalSection {
			@Override
			public String getDescriptionKey() {
				return "normal_section_description";
			}
		},
		PredefinedSection {
			@Override
			public String getDescriptionKey() {
				return "predefined_section_description";
			}
		},
		ModelObjectSection {
			@Override
			public String getDescriptionKey() {
				return "model_object_section_description";
			}
		},
		ControlSection {
			@Override
			public String getDescriptionKey() {
				return "control_section_description";
			}
		};

		public abstract String getDescriptionKey();
	}

	public KindOfTocEntry kindOfTocEntry = KindOfTocEntry.NormalSection;
	private String tocEntryTitle;
	private TOCEntry newEntry;

	// Normal section
	private String tocEntryContent;

	// Predefined section
	private PredefinedSection.PredefinedSectionType predefinedSectionType;

	// Model object section
	private ModelObjectType modelObjectType = ModelObjectType.Process;
	public FlexoProcess selectedProcess;
	public View selectedView;
	public Role selectedRole;
	public DMEntity selectedEntity;
	public OperationComponentDefinition selectedOperationComponent;
	public ERDiagram selectedERDiagram;

	private ProcessSection.ProcessDocSectionSubType processDocSectionSubType = ProcessSection.ProcessDocSectionSubType.Doc;

	public TOCRepository getRepository() {
		return ((TOCEntry) getFocusedObject()).getRepository();
	}

	@Override
	protected void doAction(Object context) throws DuplicateSectionException, InvalidLevelException {

		switch (kindOfTocEntry) {
		case NormalSection:
			newEntry = getRepository().createNormalSection(getTocEntryTitle(), getTocEntryContent());
			break;
		case PredefinedSection:
			newEntry = getRepository().createPredefinedSection(getTocEntryTitle(), getPredefinedSectionType());
			break;
		case ModelObjectSection:
			switch (getModelObjectType()) {
			case Process:
				newEntry = getRepository().createProcessSection(getTocEntryTitle(), selectedProcess, null);
				break;
			case View:
				newEntry = getRepository().createViewSection(getTocEntryTitle(), selectedView, null);
				break;
			case Role:
				newEntry = getRepository().createRoleSection(getTocEntryTitle(), selectedRole, null);
				break;
			case Entity:
				newEntry = getRepository().createEntitySection(getTocEntryTitle(), selectedEntity, null);
				break;
			case OperationScreen:
				newEntry = getRepository().createOperationScreenSection(getTocEntryTitle(), selectedOperationComponent, null);
				break;
			case ERDiagram:
				newEntry = getRepository().createERDiagramSection(getTocEntryTitle(), selectedERDiagram, null);
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}

		((TOCEntry) getFocusedObject()).addToTocEntries(newEntry);

	}

	public String getTocEntryTitle() {
		return tocEntryTitle;
	}

	public void setTocEntryTitle(String tocEntryTitle) {
		this.tocEntryTitle = tocEntryTitle;
	}

	public PredefinedSection.PredefinedSectionType getPredefinedSectionType() {
		return predefinedSectionType;
	}

	public void setPredefinedSectionType(PredefinedSection.PredefinedSectionType predefinedSectionType) {
		this.predefinedSectionType = predefinedSectionType;
	}

	public ProcessSection.ProcessDocSectionSubType getProcessDocSectionSubType() {
		return processDocSectionSubType;
	}

	public void setProcessDocSectionSubType(ProcessSection.ProcessDocSectionSubType processDocSectionSubType) {
		this.processDocSectionSubType = processDocSectionSubType;
	}

	public String getTocEntryContent() {
		return tocEntryContent;
	}

	public void setTocEntryContent(String tocEntryContent) {
		this.tocEntryContent = tocEntryContent;
	}

	public ModelObjectType getModelObjectType() {
		return modelObjectType;
	}

	public void setModelObjectType(ModelObjectType modelObjectType) {
		this.modelObjectType = modelObjectType;
	}

	/**
	 * Return new entry being created
	 * 
	 * @return
	 */
	public TOCEntry getNewEntry() {
		return newEntry;
	}

}
