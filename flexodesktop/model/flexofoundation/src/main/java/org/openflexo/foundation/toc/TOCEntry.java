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
package org.openflexo.foundation.toc;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DefaultBindingFactory;
import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.RepresentableFlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.dm.TOCRepositoryChanged;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.toc.TOCDataBinding.TOCBindingAttribute;
import org.openflexo.foundation.toc.action.AddTOCEntry;
import org.openflexo.foundation.toc.action.DeprecatedAddTOCEntry;
import org.openflexo.foundation.toc.action.MoveTOCEntry;
import org.openflexo.foundation.toc.action.RemoveTOCEntry;
import org.openflexo.foundation.toc.action.RepairTOCEntry;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.FlexoModelObjectReference.ReferenceOwner;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.foundation.xml.FlexoTOCBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

// Please rewrite this class conforming to new hierarchy
@Deprecated
public class TOCEntry extends TOCObject implements Sortable, InspectableObject, ReferenceOwner, Bindable {

	public static final int MAXIMUM_DEPTH = 10;

	private Date lastUpdateDate;
	private TOCRepository repository;
	private String title;
	private int index = -1;
	private boolean isReadOnly = false;
	private String content;
	private TOCEntry parent;
	protected Vector<TOCEntry> tocEntries;
	private FlexoModelObjectReference<?> objectReference;
	private boolean startOnANewPage = false;
	private boolean recursionEnabled = true;
	private boolean includeStatusList = true;
	private PredefinedSection.PredefinedSectionType identifier;
	private ProcessSection.ProcessDocSectionSubType subType;

	/**
	 * Create a new GeneratedCodeRepository.
	 */
	public TOCEntry(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public TOCEntry(TOCData data) {
		super(data);
		tocEntries = new Vector<TOCEntry>();
	}

	public TOCEntry(TOCData generatedCode, PredefinedSection.PredefinedSectionType identifier) {
		this(generatedCode);
		this.identifier = identifier;
	}

	public TOCEntry(TOCData generatedCode, FlexoModelObject modelObject) {
		this(generatedCode);
		this.objectReference = new FlexoModelObjectReference<FlexoModelObject>(generatedCode.getProject(), modelObject);
		this.objectReference.setSerializeClassName(true); // Even if the object is not loaded yet, we need to know its class name.
		this.objectReference.setOwner(this);
		isReadOnly = true;
	}

	public TOCEntry(TOCData generatedCode, FlexoModelObject modelObject, PredefinedSection.PredefinedSectionType identifier) {
		this(generatedCode, modelObject);
		this.identifier = identifier;
	}

	public static TOCEntry cloneEntryFromTemplate(TOCEntry father, TOCEntry source) {
		TOCEntry reply = new TOCEntry(father.getData(), source.getIdentifier());
		reply.setTitle(source.getTitle());
		reply.content = source.getContent();
		reply.setStartOnANewPage(source.getStartOnANewPage());
		reply.setIsReadOnly(source.isReadOnly());
		father.addToTocEntries(reply);
		Enumeration<TOCEntry> en = source.getSortedTocEntries();
		while (en.hasMoreElements()) {
			reply.addToTocEntries(TOCEntry.cloneEntryFromTemplate(reply, en.nextElement()));
		}
		return reply;
	}

	@Override
	public void delete() {
		for (TOCEntry entry : new ArrayList<TOCEntry>(tocEntries)) {
			entry.delete();
		}
		if (objectReference != null) {
			objectReference.delete();
		}
		super.delete();
		if (getParent() != null) {
			getParent().removeFromTocEntries(this);
		} else if (getRepository() != null) {
			getRepository().removeFromTocEntries(this);
		}
		deleteObservers();
		repository = null;
		content = null;
	}

	@Override
	public void setChanged() {
		lastUpdateDate = new Date();
		super.setChanged();
	}

	@Override
	public String getClassNameKey() {
		return "toc_entry";
	}

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

	@Override
	public String getFullyQualifiedName() {
		if (getRepository() == null) {
			return getTitle();
		}
		if (getParent() == null) {
			return "TOC_ENTRY." + getRepository().getTitle() + "." + getTitle();
		} else {
			return getParent().getFullyQualifiedName() + "." + getTitle();
		}
	}

	public void notifyAttributeModification(String attributeName, Object oldValue, Object newValue) {
		notifyObservers(new AttributeDataModification(attributeName, oldValue, newValue));
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> v = super.getSpecificActionListForThatClass();
		v.add(DeprecatedAddTOCEntry.actionType);
		v.add(AddTOCEntry.actionType);
		v.add(RemoveTOCEntry.actionType);
		v.add(RepairTOCEntry.actionType);
		v.add(MoveTOCEntry.actionType);
		return v;
	}

	public boolean acceptsEntryAsChild(TOCEntry entry) {
		return entry != this && !isChildOf(entry) && !(entry instanceof TOCRepository) && this.canHaveChildrenWithDepth(entry.getDepth())
				&& (entry.getIdentifier() == null || entry.getPreferredLevel() == -1 || entry.getPreferredLevel() == getLevel() + 1);
	}

	public TOCRepository getRepository() {
		return repository;
	}

	public void setRepository(TOCRepository repository) {
		TOCRepository old = this.repository;
		this.repository = repository;
		for (TOCEntry entry : tocEntries) {
			entry.setRepository(repository);
		}
		setChanged();
		notifyObservers(new TOCRepositoryChanged(old, repository));
	}

	@Override
	public String getName() {
		return getTitle();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		String old = this.title;
		this.title = title;
		setChanged();
		notifyAttributeModification("title", old, title);
		if (getRepository() != null) {
			getRepository().notifyDocumentChanged(this);
		}
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setIsReadOnly(boolean isReadOnly) {
		boolean old = this.isReadOnly;
		this.isReadOnly = isReadOnly;
		setChanged();
		notifyAttributeModification("isReadOnly", old, isReadOnly);
	}

	// Deprecated:
	// This implementation is only temporary, please remove it when TOCEntry becomes abstract
	public String getDefaultTemplateName() {
		if (getIdentifier() != null) {
			return getIdentifier().getDefaultTemplateName();
		} else if (isIndividualProcessFolder()) {
			return ProcessFolderSection.DOC_TEMPLATE;
		} else if (isIndividualProcess()) {
			if (isSIPOC2SubType()) {
				return ProcessSection.SIPOC_LEVEL2_TEMPLATE;
			} else if (isSIPOC3SubType()) {
				return ProcessSection.SIPOC_LEVEL3_TEMPLATE;
			} else if (isRACISubType()) {
				return ProcessSection.RACI_MATRIX_TEMPLATE;
			}
			if (isOperationTableSubType()) {
				return ProcessSection.OPERATION_TABLE_TEMPLATE;
			} else {
				return ProcessSection.DOC_TEMPLATE;
			}
		} else if (isIndividualRole()) {
			return RoleSection.DOC_TEMPLATE;
		} else if (isIndividualEntity()) {
			return EntitySection.DOC_TEMPLATE;
		} else if (isIndividualComponentDefinition()) {
			return OperationScreenSection.DOC_TEMPLATE;
		}
		return null;
	}

	@Deprecated
	public String getContent() {
		return content;
	}

	@Deprecated
	public void setContent(String content) throws IllegalAccessException {
		if (isReadOnly) {
			throw new IllegalAccessException("this entry is read only");
		}
		String old = content;
		this.content = content;
		setChanged();
		notifyAttributeModification("content", old, content);
		if (getRepository() != null) {
			getRepository().notifyDocumentChanged(this);
		}
	}

	@Deprecated
	public boolean isProcessesSection() {
		return identifier != null && identifier == PredefinedSection.PredefinedSectionType.PROCESSES;
	}

	@Deprecated
	public PredefinedSection.PredefinedSectionType getIdentifier() {
		return identifier;
	}

	@Deprecated
	public void setIdentifier(PredefinedSection.PredefinedSectionType identifier) {
		if (!isDeserializing()) {
			return;
		}
		this.identifier = identifier;
	}

	@Deprecated
	public ProcessSection.ProcessDocSectionSubType getSubType() {
		return subType;
	}

	@Deprecated
	public void setSubType(ProcessSection.ProcessDocSectionSubType subType) {
		this.subType = subType;
	}

	@Deprecated
	public String getValidReference() {
		return "TOC-" + getLevel() + "-" + getTitle();
	}

	public TOCEntry getParent() {
		return parent;
	}

	public void setParent(TOCEntry parent) {
		TOCEntry old = this.parent;
		if (this.parent != null && this.parent != parent) {
			this.parent.removeFromTocEntries(this);
		} else if (getRepository() != null && parent != getRepository()) {
			getRepository().removeFromTocEntries(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.addToTocEntries(this);
			setRepository(parent.getRepository());
		}
		setChanged();
		notifyAttributeModification("parent", old, parent);
	}

	public Vector<TOCEntry> getTocEntries() {
		return tocEntries;
	}

	public Enumeration<TOCEntry> getSortedTocEntries() {
		disableObserving();
		TOCEntry[] o = FlexoIndexManager.sortArray(getTocEntries().toArray(new TOCEntry[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public void setTocEntries(Vector<TOCEntry> tocEntries) {
		this.tocEntries = tocEntries;
	}

	public void addToTocEntries(TOCEntry entry) {
		if (!tocEntries.contains(entry)) {
			tocEntries.add(entry);
			entry.setRepository(getRepository());
			if (!isDeserializing()) {
				entry.setIndexValue(tocEntries.size());
			}
			entry.setParent(this);
			if (!isDeserializing() && !isCreatedByCloning()) {
				FlexoIndexManager.reIndexObjectOfArray(getTocEntries().toArray(new TOCEntry[0]));
			}
			if (getRepository() != null) {
				getRepository().notifyDocumentChanged(entry);
			}
			setChanged();
			notifyObservers(new TOCModification("tocEntries", null, entry));
			rebuildBindingModel();
			entry.rebuildBindingModel();
		}
	}

	public void removeFromTocEntries(TOCEntry entry) {
		if (tocEntries.contains(entry)) {
			tocEntries.remove(entry);
			entry.setParent(null);
			entry.setRepository(null);
			if (!isDeserializing() && !isCreatedByCloning()) {
				FlexoIndexManager.reIndexObjectOfArray(getTocEntries().toArray(new TOCEntry[0]));
			}
			if (getRepository() != null) {
				getRepository().notifyDocumentChanged(entry);
			}
			setChanged();
			notifyObservers(new TOCModification("tocEntries", entry, null));
			rebuildBindingModel();
			entry.rebuildBindingModel();
		}
	}

	public TOCEntry getTOCEntryWithID(PredefinedSection.PredefinedSectionType id) {
		if (id == null) {
			return null;
		}
		for (TOCEntry entry : tocEntries) {
			if (id == entry.getIdentifier()) {
				return entry;
			}
		}
		for (TOCEntry entry : tocEntries) {
			TOCEntry returned = entry.getTOCEntryWithID(id);
			if (returned != null) {
				return returned;
			}
		}
		return null;
	}

	public TOCEntry getTOCEntryForObject(FlexoModelObject object) {
		if (object == null) {
			return null;
		}
		for (TOCEntry entry : tocEntries) {
			if (object == entry.getObject()) {
				return entry;
			}
		}
		for (TOCEntry entry : tocEntries) {
			TOCEntry returned = entry.getTOCEntryForObject(object);
			if (returned != null) {
				return returned;
			}
		}
		return null;
	}

	@Override
	public int getIndex() {
		if (isBeingCloned()) {
			return -1;
		}
		if (index == -1 && getCollection() != null) {
			index = getCollection().length;
			FlexoIndexManager.reIndexObjectOfArray(getCollection());
		}
		return index;
	}

	@Override
	public void setIndex(int index) {
		if (isDeserializing() || isCreatedByCloning()) {
			setIndexValue(index);
			return;
		}
		FlexoIndexManager.switchIndexForKey(this.index, index, this);
		if (getParent() != null) {
			getParent().setChanged();
		} else {
			getRepository().setChanged();
		}
		if (getIndex() != index) {
			setChanged();
			AttributeDataModification dm = new AttributeDataModification("index", null, getIndex());
			dm.setReentrant(true);
			notifyObservers(dm);
		}
	}

	@Override
	public int getIndexValue() {
		return getIndex();
	}

	@Override
	public void setIndexValue(int index) {
		if (this.index == index) {
			return;
		}
		int old = this.index;
		this.index = index;
		setChanged();
		notifyObservers(new AttributeDataModification("index", old, index));
		if (!isDeserializing() && !isCreatedByCloning()) {
			if (getRepository() != null) {
				getRepository().notifyDocumentChanged(this);
			}
			if (getParent() != null) {
				getParent().setChanged();
				getParent().notifyObservers(new ChildrenOrderChanged());
			} else {
				if (getRepository() != null) {
					getRepository().setChanged();
					getRepository().notifyObservers(new ChildrenOrderChanged());
				}
			}
		}
	}

	/**
	 * Overrides getCollection
	 * 
	 * @see org.openflexo.foundation.utils.Sortable#getCollection()
	 */
	@Override
	public TOCEntry[] getCollection() {
		if (getParent() != null) {
			return getParent().getTocEntries().toArray(new TOCEntry[0]);
		}
		return getRepository().getTocEntries().toArray(new TOCEntry[0]);
	}

	public int getLevel() {
		if (getParent() == null) {
			return 0;
		} else {
			return getParent().getLevel() + 1;
		}
	}

	public boolean canHaveChildren() {
		return getLevel() < MAXIMUM_DEPTH;
	}

	public boolean canHaveChildrenWithDepth(int depth) {
		return getLevel() + depth < MAXIMUM_DEPTH + 1;
	}

	public int getDepth() {
		if (tocEntries.size() > 0) {
			int deepest = 0;
			for (TOCEntry entry : tocEntries) {
				int dl = entry.getDepth();
				if (dl > deepest) {
					deepest = dl;
				}
			}
			return deepest + 1;
		} else {
			return 1;
		}
	}

	@Override
	public String getInspectorName() {
		return Inspectors.DE.DE_TOC_ENTRY_INSPECTOR;
	}

	public String getTitleNumber() {
		if (getParent() != null && getParent() != getRepository()) {
			return getParent().getTitleNumber() + "." + getIndex();
		}
		return String.valueOf(getIndex());
	}

	public String getDisplayString() {
		return getTitleNumber() + " " + getTitle() + (getRepository() != null ? "[" + getRepository().getTitle() + "]" : "");
	}

	public void printHTML(StringBuilder sb) {
		if (getTitle() != null) {
			sb.append("<H").append(getLevel()).append(">");
			sb.append(getTitleNumber()).append(" ");
			sb.append(getTitle());
			sb.append("</H").append(getLevel()).append(">");
		}
		if (isReadOnly()) {
			sb.append(FlexoLocalization.localizedForKey("documentation_from_your_project_will_be_automatically_inserted_here"));
		} else {
			if (getContent() != null) {
				sb.append(getContent());
			}
		}
		Enumeration<TOCEntry> en = getSortedTocEntries();
		while (en.hasMoreElements()) {
			en.nextElement().printHTML(sb);
		}
	}

	public boolean isChildOf(TOCEntry entry) {
		TOCEntry aParent = getParent();
		while (aParent != null) {
			if (aParent == entry) {
				return true;
			}
			aParent = aParent.getParent();
		}
		return false;
	}

	public Date getLastUpdateDate() {
		if (lastUpdateDate == null) {
			lastUpdateDate = getLastUpdate();
		}
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	@Deprecated
	public FlexoModelObject getObject() {
		return getObject(false);
	}

	@Deprecated
	public FlexoModelObject getObject(boolean forceResourceLoad) {
		if (getObjectReference() != null) {
			return getObjectReference().getObject(forceResourceLoad);
		} else {
			return null;
		}
	}

	@Deprecated
	public void setObject(FlexoModelObject object) {
		if (objectReference != null) {
			objectReference.delete();
			objectReference = null;
		}
		if (object != null) {
			objectReference = new FlexoModelObjectReference<FlexoModelObject>(getProject(), object);
		} else {
			objectReference = null;
		}
		if (objectReference != null) {
			objectReference.setOwner(this);
			objectReference.setSerializeClassName(true);
		}
	}

	@Deprecated
	public boolean isDocSubType() {
		return getSubType() == ProcessSection.ProcessDocSectionSubType.Doc;
	}

	@Deprecated
	public boolean isRACISubType() {
		return getSubType() == ProcessSection.ProcessDocSectionSubType.RaciMatrix;
	}

	@Deprecated
	public boolean isSIPOC2SubType() {
		return getSubType() == ProcessSection.ProcessDocSectionSubType.SIPOCLevel2;
	}

	@Deprecated
	public boolean isSIPOC3SubType() {
		return getSubType() == ProcessSection.ProcessDocSectionSubType.SIPOCLevel3;
	}

	@Deprecated
	public boolean isOperationTableSubType() {
		return getSubType() == ProcessSection.ProcessDocSectionSubType.OperationTable;
	}

	@Deprecated
	public boolean isERDiagram() {
		return getIdentifier() == PredefinedSection.PredefinedSectionType.ER_DIAGRAM;
	}

	@Deprecated
	public boolean isIndividualProcessOrProcessFolder() {
		return getIdentifier() == null && (getObject() instanceof FlexoProcess || getObject() instanceof ProcessFolder);
	}

	@Deprecated
	public boolean isIndividualProcess() {
		return getIdentifier() == null && getObject() instanceof FlexoProcess;
	}

	@Deprecated
	public boolean isIndividualProcessFolder() {
		return getIdentifier() == null && getObject() instanceof ProcessFolder;
	}

	@Deprecated
	public boolean isIndividualRole() {
		return getIdentifier() == null && getObject() instanceof Role;
	}

	@Deprecated
	public boolean isIndividualEntity() {
		return getIdentifier() == null && getObject() instanceof DMEOEntity;
	}

	@Deprecated
	public boolean isIndividualComponentDefinition() {
		return getIdentifier() == null && getObject() instanceof ComponentDefinition;
	}

	public int getPreferredLevel() {
		// with docx : all templates adapte themself immediatly :)
		return -1;
	}

	private static int preferredLevelForModelObjectClass(Class<?> klass) {
		if (klass.equals(FlexoProcess.class)) {
			return 2;
		}
		if (klass.equals(DMEOEntity.class)) {
			return 3;
		}
		return -1;
	}

	@Deprecated
	public FlexoModelObjectReference<?> getObjectReference() {
		return objectReference;
	}

	@Deprecated
	public void setObjectReference(FlexoModelObjectReference<?> objectReference) {
		if (this.objectReference != null) {
			this.objectReference = null;
		}
		this.objectReference = objectReference;
		if (this.objectReference != null) {
			this.objectReference.setOwner(this);
		}
	}

	@Deprecated
	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference<?> reference) {
	}

	@Deprecated
	@Override
	public void objectCantBeFound(FlexoModelObjectReference<?> reference) {
		setChanged();
		notifyObservers(new TOCModification(reference, null));
	}

	@Deprecated
	@Override
	public void objectDeleted(FlexoModelObjectReference<?> reference) {
		setChanged();
		notifyObservers(new TOCModification(reference, null));
	}

	public void setStartOnANewPage(boolean v) {
		if (v == startOnANewPage) {
			return;
		}
		startOnANewPage = v;
		setChanged();
		notifyObservers(new TOCModification("startOnANewPage", !v, v));
	}

	public boolean getStartOnANewPage() {
		return startOnANewPage;
	}

	public void setRecursionEnabled(boolean v) {
		if (v == recursionEnabled) {
			return;
		}
		recursionEnabled = v;
		setChanged();
		notifyObservers(new TOCModification("recursionEnabled", !v, v));
		setChanged();
		notifyAttributeModification("recursionEnabled", !v, v);
	}

	public boolean getRecursionEnabled() {
		return recursionEnabled;
	}

	public void setIncludeStatusList(boolean v) {
		if (v == includeStatusList) {
			return;
		}
		includeStatusList = v;
		setChanged();
		notifyObservers(new TOCModification("includeStatusList", !v, v));

		setChanged();
		notifyAttributeModification("includeStatusList", !v, v);
	}

	public boolean getIncludeStatusList() {
		return includeStatusList;
	}

	@Deprecated
	public RepresentableFlexoModelObject getDocumentedFlexoProcess() {
		if (getObject() instanceof FlexoProcess || getObject() instanceof ProcessFolder) {
			return (RepresentableFlexoModelObject) getObject();
		}
		return null;
	}

	@Deprecated
	public ERDiagram getDocumentedDiagram() {
		if (isERDiagram()) {
			return (ERDiagram) getObject();
		}
		return null;
	}

	@Deprecated
	public Role getDocumentedRole() {
		if (isIndividualRole()) {
			return (Role) getObject();
		}
		return null;
	}

	@Deprecated
	public DMEOEntity getDocumentedDMEOEntity() {
		if (isIndividualEntity()) {
			return (DMEOEntity) getObject();
		}
		return null;
	}

	@Deprecated
	public ComponentDefinition getDocumentedComponentDefinition() {
		if (isIndividualComponentDefinition()) {
			return (ComponentDefinition) getObject();
		}
		return null;
	}

	@Deprecated
	public void setDocumentedFlexoProcess(RepresentableFlexoModelObject object) {
		if (object != null && object.equals(getObject())) {
			return;
		}
		if (object == null) {
			return;
		}

		if (!(object instanceof FlexoProcess) && !(object instanceof ProcessFolder)) {
			throw new IllegalArgumentException("setDocumentedFlexoProcess MUST have either a FlexoProcess or a ProcessFolder");
		}

		setObject(object);
		setChanged();
		notifyAttributeModification("objectReference", null, object);
		if (getRepository() != null) {
			getRepository().notifyDocumentChanged(this);
		}
		setChanged();
		notifyAttributeModification("documentedFlexoProcess", null, object);
	}

	@Deprecated
	public void setDocumentedRole(Role object) {
		if (object != null && object.equals(getObject())) {
			return;
		}
		if (object == null) {
			return;
		}
		setObject(object);
		setChanged();
		notifyAttributeModification("objectReference", null, object);
		if (getRepository() != null) {
			getRepository().notifyDocumentChanged(this);
		}
		setChanged();
		notifyAttributeModification("documentedRole", null, object);
	}

	@Deprecated
	public void setDocumentedDMEOEntity(DMEOEntity object) {
		if (object != null && object.equals(getObject())) {
			return;
		}
		if (object == null) {
			return;
		}
		setObject(object);
		setChanged();
		notifyAttributeModification("objectReference", null, object);
		if (getRepository() != null) {
			getRepository().notifyDocumentChanged(this);
		}
		setChanged();
		notifyAttributeModification("documentedDMEOEntity", null, object);
	}

	@Deprecated
	public void setDocumentedComponentDefinition(ComponentDefinition object) {
		if (object != null && object.equals(getObject())) {
			return;
		}
		if (object == null) {
			return;
		}
		setObject(object);
		setChanged();
		notifyAttributeModification("objectReference", null, object);
		if (getRepository() != null) {
			getRepository().notifyDocumentChanged(this);
		}
		setChanged();
		notifyAttributeModification("documentedComponentDefinition", null, object);
	}

	@Deprecated
	public void setDocumentedDiagram(ERDiagram object) {
		if (object != null && object.equals(getObject())) {
			return;
		}
		if (object == null) {
			return;
		}
		setObject(object);
		setChanged();
		notifyAttributeModification("objectReference", null, object);
		if (getRepository() != null) {
			getRepository().notifyDocumentChanged(this);
		}
		setChanged();
		notifyAttributeModification("documentedDiagram", null, object);
	}

	@Deprecated
	public Vector<ERDiagram> availableDiagrams() {
		return getProject().getDataModel().getDiagrams();
	}

	@Deprecated
	@Override
	public void objectSerializationIdChanged(FlexoModelObjectReference<?> reference) {
		setChanged();
	}

	public void notifyBindingChanged(TOCDataBinding binding) {
	}

	public void notifyChange(TOCBindingAttribute bindingAttribute, AbstractBinding oldValue, AbstractBinding value) {
	}

	private BindingModel bindingModel = null;

	@Override
	public BindingModel getBindingModel() {
		if (bindingModel == null) {
			rebuildBindingModel();
		}
		return bindingModel;
	}

	protected void rebuildBindingModel() {
		bindingModel = buildBindingModel();
		for (TOCEntry entry : getTocEntries()) {
			entry.rebuildBindingModel();
		}
	}

	protected BindingModel buildBindingModel() {
		BindingModel returned;
		if (getParent() == null) {
			returned = new BindingModel();
		} else {
			returned = new BindingModel(getParent().getBindingModel());
		}
		return returned;
	}

	@Override
	public BindingFactory getBindingFactory() {
		return DEFAULT_BINDING_FACTORY;
	}

	public static BindingFactory DEFAULT_BINDING_FACTORY = new DefaultBindingFactory();

}
