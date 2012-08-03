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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariableImpl;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.WilcardTypeImpl;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.action.DuplicateSectionException;
import org.openflexo.foundation.cg.action.InvalidLevelException;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.toc.ControlSection.ControlSectionBindingAttribute;
import org.openflexo.foundation.toc.ModelObjectSection;
import org.openflexo.foundation.toc.ModelObjectSection.ModelObjectSectionBindingAttribute;
import org.openflexo.foundation.toc.ModelObjectSection.ModelObjectType;
import org.openflexo.foundation.toc.PredefinedSection;
import org.openflexo.foundation.toc.ProcessSection;
import org.openflexo.foundation.toc.TOCDataBinding;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCObject;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.toolbox.StringUtils;

public class AddTOCEntry extends FlexoAction<AddTOCEntry, TOCObject, TOCObject> implements Bindable {

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
		public boolean isVisibleForSelection(TOCObject object, Vector<TOCObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(TOCObject object, Vector<TOCObject> globalSelection) {
			return (object instanceof TOCEntry && ((TOCEntry) object).canHaveChildren());
		}

	};

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
		ConditionalSection {
			@Override
			public String getDescriptionKey() {
				return "conditional_section_description";
			}
		},
		IterationSection {
			@Override
			public String getDescriptionKey() {
				return "iteration_section_description";
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
	public ViewDefinition selectedView;
	public ViewFolder selectedViewFolder;
	public Role selectedRole;
	public DMEntity selectedEntity;
	public OperationComponentDefinition selectedOperationComponent;
	public ERDiagram selectedERDiagram;

	private String iteratorName = "item";

	private TOCDataBinding value;

	private BindingDefinition VALUE = new BindingDefinition("value", Object.class, BindingDefinitionType.GET, false) {
		@Override
		public Type getType() {
			return getModelObjectType().getType();
		};
	};

	private TOCDataBinding condition;

	private BindingDefinition CONDITION = new BindingDefinition("condition", Boolean.class, BindingDefinitionType.GET, false);

	private TOCDataBinding iteration;

	private Type LIST_BINDING_TYPE = new ParameterizedTypeImpl(List.class, new WilcardTypeImpl(Object.class));;

	private BindingDefinition ITERATION = new BindingDefinition("iteration", LIST_BINDING_TYPE, BindingDefinitionType.GET, false);

	private TOCDataBinding iterationCondition;

	private BindingDefinition ITERATION_CONDITION = new BindingDefinition("iterationCondition", Boolean.class, BindingDefinitionType.GET,
			false);

	private ConditionalOwner conditionalOwner;

	public class ConditionalOwner implements Bindable {
		@Override
		public BindingFactory getBindingFactory() {
			return AddTOCEntry.this.getBindingFactory();
		}

		@Override
		public BindingModel getBindingModel() {
			return getInferedBindingModel();
		}
	}

	private ProcessSection.ProcessDocSectionSubType processDocSectionSubType = ProcessSection.ProcessDocSectionSubType.Doc;

	AddTOCEntry(TOCObject focusedObject, Vector<TOCObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		conditionalOwner = new ConditionalOwner();
	}

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
				newEntry = getRepository().createProcessSection(getTocEntryTitle(), selectedProcess, getProcessDocSectionSubType(), null);
				break;
			case View:
				newEntry = getRepository().createViewSection(getTocEntryTitle(), selectedView, null);
				break;
			case ViewFolder:
				newEntry = getRepository().createViewFolderSection(getTocEntryTitle(), selectedViewFolder, null);
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
			if (newEntry != null && getValue() != null && getValue().isSet() && getValue().isValid()) {
				((ModelObjectSection<?>) newEntry).setValue(new TOCDataBinding(getValue().toString()));
			}
			break;
		case ConditionalSection:
			newEntry = getRepository().createConditionalSection(
					StringUtils.isNotEmpty(getTocEntryTitle()) ? getTocEntryTitle() : getCondition().toString(), getCondition());
			break;
		case IterationSection:
			newEntry = getRepository().createIterationSection(
					StringUtils.isNotEmpty(getTocEntryTitle()) ? getTocEntryTitle() : iteratorName + ":" + getIteration().toString(),
					iteratorName, getIteration(), getIterationCondition());
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

	public BindingDefinition getValueBindingDefinition() {
		return VALUE;
	}

	public TOCDataBinding getValue() {
		if (value == null && getFocusedObject() instanceof TOCEntry) {
			value = new TOCDataBinding((TOCEntry) getFocusedObject(), ModelObjectSectionBindingAttribute.value, getValueBindingDefinition());
		}
		return value;
	}

	public void setValue(TOCDataBinding value) {
		if (value != null && getFocusedObject() instanceof TOCEntry) {
			value.setOwner((TOCEntry) getFocusedObject());
			value.setBindingAttribute(ModelObjectSectionBindingAttribute.value);
			value.setBindingDefinition(getValueBindingDefinition());
		}
		this.value = value;
	}

	public BindingDefinition getConditionBindingDefinition() {
		return CONDITION;
	}

	public TOCDataBinding getCondition() {
		if (condition == null && getFocusedObject() instanceof TOCEntry) {
			condition = new TOCDataBinding(((TOCEntry) getFocusedObject()), ControlSectionBindingAttribute.condition,
					getConditionBindingDefinition());
		}
		return condition;
	}

	public void setCondition(TOCDataBinding condition) {
		if (condition != null && getFocusedObject() instanceof TOCEntry) {
			condition.setOwner(((TOCEntry) getFocusedObject()));
			condition.setBindingAttribute(ControlSectionBindingAttribute.condition);
			condition.setBindingDefinition(getConditionBindingDefinition());
			this.condition = condition;
		}
	}

	public String getIteratorName() {
		return iteratorName;
	}

	public void setIteratorName(String iteratorName) {
		this.iteratorName = iteratorName;
		inferedBindingModel = null;
	}

	public BindingDefinition getIterationBindingDefinition() {
		return ITERATION;
	}

	public TOCDataBinding getIteration() {
		if (iteration == null && getFocusedObject() instanceof TOCEntry) {
			iteration = new TOCDataBinding((TOCEntry) getFocusedObject(), ControlSectionBindingAttribute.iteration,
					getIterationBindingDefinition());
		}
		return iteration;
	}

	public void setIteration(TOCDataBinding iteration) {
		if (iteration == null && getFocusedObject() instanceof TOCEntry) {
			iteration.setOwner((TOCEntry) getFocusedObject());
			iteration.setBindingAttribute(ControlSectionBindingAttribute.iteration);
			iteration.setBindingDefinition(getIterationBindingDefinition());
		}
		this.iteration = iteration;
	}

	public BindingDefinition getIterationConditionBindingDefinition() {
		return ITERATION_CONDITION;
	}

	public TOCDataBinding getIterationCondition() {
		if (iterationCondition == null && getFocusedObject() instanceof TOCEntry) {
			iterationCondition = new TOCDataBinding(((TOCEntry) getFocusedObject()), ControlSectionBindingAttribute.condition,
					getIterationConditionBindingDefinition());
		}
		return iterationCondition;
	}

	public void setIterationCondition(TOCDataBinding iterationCondition) {
		if (iterationCondition != null && getFocusedObject() instanceof TOCEntry) {
			iterationCondition.setOwner(((TOCEntry) getFocusedObject()));
			iterationCondition.setBindingAttribute(ControlSectionBindingAttribute.iterationCondition);
			iterationCondition.setBindingDefinition(getIterationConditionBindingDefinition());
			this.iterationCondition = iterationCondition;
			inferedBindingModel = null;
		}
	}

	public ConditionalOwner getConditionalOwner() {
		return conditionalOwner;
	}

	@Override
	public BindingModel getBindingModel() {
		if (getFocusedObject() instanceof TOCEntry) {
			return ((TOCEntry) getFocusedObject()).getInferedBindingModel();
		}
		return null;
	}

	@Override
	public BindingFactory getBindingFactory() {
		if (getFocusedObject() instanceof TOCEntry) {
			return ((TOCEntry) getFocusedObject()).getBindingFactory();
		}
		return null;
	}

	private BindingModel inferedBindingModel = null;

	protected BindingModel getInferedBindingModel() {
		if (inferedBindingModel == null && getFocusedObject() instanceof TOCEntry) {
			inferedBindingModel = new BindingModel(getBindingModel());
			inferedBindingModel.addToBindingVariables(new BindingVariableImpl(this, iteratorName, getItemType()) {
				@Override
				public Type getType() {
					return getItemType();
				}

				@Override
				public String getVariableName() {
					return iteratorName;
				}
			});
		}
		return inferedBindingModel;
	}

	public Type getItemType() {
		if (getIteration() != null && getIteration().hasBinding()) {
			Type accessedType = getIteration().getBinding().getAccessedType();
			if (accessedType instanceof ParameterizedType && ((ParameterizedType) accessedType).getActualTypeArguments().length > 0) {
				return ((ParameterizedType) accessedType).getActualTypeArguments()[0];
			}
		}
		return Object.class;
	}

}
