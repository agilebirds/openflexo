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

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariableImpl;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.DocType.DefaultDocType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.dm.CGDataModification;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.ImageFile;
import org.openflexo.foundation.toc.PredefinedSection.PredefinedSectionType;
import org.openflexo.foundation.toc.action.RemoveTOCEntry;
import org.openflexo.foundation.toc.action.RemoveTOCRepository;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.xml.FlexoTOCBuilder;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.XMLDecoder;

public class TOCRepository extends TOCEntry {

	private DocType docType;

	private String docTypeAsString;

	private String docTitle;

	private String customer;

	private String version;

	private String author;

	private String reviewer;

	private String systemName;

	private String systemVersion;

	private boolean useEmbeddedEvents = true;

	private ImageFile logo;

	public TOCRepository(TOCData data, DocType docType, TOCRepository tocTemplate) {
		this(data);
		this.docType = docType != null ? docType : data.getProject().getDocTypes().get(0);
		if (tocTemplate == null) {
			TOCRepository defaultTocTemplate = null;
			if (docType.getName().equals(DefaultDocType.Technical.name())) {
				defaultTocTemplate = loadTOCTemplate("SRS");
			} else {
				defaultTocTemplate = loadTOCTemplate("BRS");
			}
			createEntriesFromTemplate(defaultTocTemplate);
		} else {
			createEntriesFromTemplate(tocTemplate);
		}
	}

	private TOCRepository loadTOCTemplate(String templateName) {
		String tocTemplateFileName = templateName + ".xml";
		File tocTemplateFile = new FileResource("Config/TOCTemplates/" + tocTemplateFileName);
		try {
			TOCRepository tocTemplate = (TOCRepository) XMLDecoder.decodeObjectWithMappingFile(new FileInputStream(tocTemplateFile),
					new FileResource("Models/TOCModel/toc_template_0.1.xml"), new FlexoTOCBuilder(null));
			return tocTemplate;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void createEntriesFromTemplate(TOCRepository tocTemplate) {
		Enumeration<TOCEntry> en = tocTemplate.getSortedTocEntries();
		while (en.hasMoreElements()) {
			addToTocEntries(TOCEntry.cloneEntryFromTemplate(this, en.nextElement()));
		}
	}

	private TOCRepository(TOCData data) {
		super(data);
	}

	public TOCRepository(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	@Override
	public void delete() {
		super.delete();
		getData().removeFromRepositories(this);
		docType = null;
	}

	@Override
	public TOCRepository getRepository() {
		return this;
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> v = super.getSpecificActionListForThatClass();
		v.remove(RemoveTOCEntry.actionType);
		v.add(RemoveTOCRepository.actionType);
		return v;
	}

	@Override
	public String getClassNameKey() {
		return "table_of_content";
	}

	@Override
	public String getFullyQualifiedName() {
		return "TOC-" + getTitle();
	}

	public DocType getDocType() {
		if (docType == null && getProject().getDocTypes().size() > 0) {
			docType = getProject().getDocTypes().get(0);
		}
		if (docTypeAsString != null) {
			DocType dt = getProject().getDocTypeNamed(docTypeAsString);
			if (dt != null) {
				docType = dt;
				docTypeAsString = null;
			}
		}
		return docType;
	}

	public void setDocType(DocType docType) {
		if (docType != null) {
			this.docType = docType;
			setChanged();
			notifyObservers(new CGDataModification("docType", null, docType));
		}
	}

	public String getDocTypeAsString() {
		if (getDocType() != null) {
			return getDocType().getName();
		} else {
			return null;
		}
	}

	public void setDocTypeAsString(String docType) {
		this.docTypeAsString = docType;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.DE.DE_TOC_REPOSITORY_INSPECTOR;
	}

	public void notifyDocumentChanged(TOCEntry cause) {
		setChanged();
		notifyObservers(new TOCModification(null, cause));
	}

	public String buildDocument() {
		return buildDocument(null);
	}

	public String buildDocument(File cssFile) {
		StringBuilder sb = new StringBuilder("<HTML>");
		if (cssFile != null) {
			try {
				URL cssURL = cssFile.toURI().toURL();
				sb.append("<HEAD>");
				sb.append("<LINK REL=StyleSheet HREF=\"").append(cssURL).append("\" TITLE=\"Contemporary\" TYPE=\"text/css\">");
				sb.append("</HEAD>");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		sb.append("<BODY>");
		Enumeration<TOCEntry> en = getSortedTocEntries();
		while (en.hasMoreElements()) {
			en.nextElement().printHTML(sb);
		}
		sb.append("</BODY></HTML>");
		return sb.toString();
	}

	public TOCEntry createObjectEntry(FlexoModelObject modelObject) {
		TOCEntry reply = new TOCEntry(getData(), modelObject);
		return reply;
	}

	public TOCEntry createObjectEntry(FlexoModelObject modelObject, PredefinedSection.PredefinedSectionType identifier) {
		TOCEntry reply = new TOCEntry(getData(), modelObject, identifier);
		return reply;
	}

	public TOCEntry createDefaultEntry(PredefinedSection.PredefinedSectionType identifier) {
		TOCEntry entry = new TOCEntry(getData(), identifier);
		entry.setTitle(identifier.getTitle());
		entry.setIsReadOnly(identifier.getIsReadOnly());

		if (!entry.isReadOnly()) {
			try {
				entry.setContent(identifier.getDefaultContent(getDocType().getName()));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("This should not happen! It means somebody has moved the setIsReadOnly(true) above the setContent() call.");
				}
			}
		}

		return entry;
	}

	// private void createDefaultEntriesForRepository() {
	// TOCEntry entry = createDefaultEntry(DocSection.PURPOSE);
	// addToTocEntries(entry);
	//
	// entry = createDefaultEntry(DocSection.PROJECT_CONTEXT);
	// addToTocEntries(entry);
	//
	// entry = createDefaultEntry(DocSection.OBJECTIVES);
	// addToTocEntries(entry);
	//
	// entry = createDefaultEntry(DocSection.SCOPE);
	// addToTocEntries(entry);
	//
	// entry = createDefaultEntry(DocSection.STAKEHOLDERS);
	// addToTocEntries(entry);
	//
	// entry = createDefaultEntry(DocSection.PROCESSES);
	// addToTocEntries(entry);
	//
	// TOCEntry subEntry = createDefaultEntry(DocSection.READERS_GUIDE);
	// entry.addToTocEntries(subEntry);
	//
	// if (getDocType()==null || !DocType.BUSINESS.equals(getDocType().getName())) {
	// entry = createDefaultEntry(DocSection.SCREENS);
	// addToTocEntries(entry);
	//
	// entry = createDefaultEntry(DocSection.DATA_MODEL);
	// addToTocEntries(entry);
	// }
	//
	// entry = createDefaultEntry(DocSection.DEFINITIONS);
	// addToTocEntries(entry);
	//
	// entry = createDefaultEntry(DocSection.NOTES_QUESTIONS);
	// addToTocEntries(entry);
	// }

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
		setChanged();
		notifyObservers(new CGDataModification("author", null, author));
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
		setChanged();
		notifyObservers(new CGDataModification("customer", null, customer));
	}

	public String getDocTitle() {
		return docTitle;
	}

	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
		setChanged();
		notifyObservers(new CGDataModification("docTitle", null, docTitle));
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		String old = this.reviewer;
		this.reviewer = reviewer;
		setChanged();
		notifyObservers(new CGDataModification("reviewer", old, reviewer));
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		String old = this.version;
		this.version = version;
		setChanged();
		notifyObservers(new CGDataModification("version", old, version));
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
		setChanged();
		notifyObservers(new CGDataModification("systemName", null, systemName));
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
		setChanged();
		notifyObservers(new CGDataModification("systemVersion", null, systemVersion));
	}

	public boolean getUseEmbeddedEvents() {
		return useEmbeddedEvents;
	}

	public void setUseEmbeddedEvents(boolean useEmbeddedEvents) {
		this.useEmbeddedEvents = useEmbeddedEvents;
		setChanged();
		notifyObservers(new CGDataModification("useEmbeddedEvents", null, useEmbeddedEvents));
	}

	public ImageFile getLogo() {
		return logo;
	}

	public void setLogo(ImageFile logo) {
		this.logo = logo;
		setChanged();
		notifyObservers(new CGDataModification("logo", null, logo));
	}

	public NormalSection createNormalSection(String title, String content) {
		NormalSection reply = new NormalSection(getData());
		reply.setTitle(title);
		reply.setContent(content);
		return reply;
	}

	public PredefinedSection createPredefinedSection(String title, PredefinedSectionType predefinedSectionType) {
		PredefinedSection reply = new PredefinedSection(getData());
		reply.setTitle(title);
		reply.setType(predefinedSectionType);
		return reply;
	}

	public ConditionalSection createConditionalSection(String title, TOCDataBinding condition) {
		ConditionalSection reply = new ConditionalSection(getData());
		reply.setTitle(title);
		reply.setCondition(condition);
		return reply;
	}

	public IterationSection createIterationSection(String title, String iteratorName, TOCDataBinding iteration, TOCDataBinding condition) {
		IterationSection reply = new IterationSection(getData());
		reply.setTitle(title);
		reply.setIteratorName(iteratorName);
		reply.setIteration(iteration);
		reply.setCondition(condition);
		return reply;
	}

	public ProcessSection createProcessSection(String title, FlexoProcess process, ProcessSection.ProcessDocSectionSubType subType,
			TOCDataBinding value) {
		ProcessSection reply = new ProcessSection(getData());
		reply.setTitle(title);
		reply.setModelObject(process);
		reply.setValue(value);
		reply.setSubType(subType);
		return reply;
	}

	public ViewSection createViewSection(String title, ViewDefinition view, TOCDataBinding value) {
		ViewSection reply = new ViewSection(getData());
		reply.setTitle(title);
		reply.setModelObject(view);
		reply.setValue(value);
		return reply;
	}

	public RoleSection createRoleSection(String title, Role role, TOCDataBinding value) {
		RoleSection reply = new RoleSection(getData());
		reply.setTitle(title);
		reply.setModelObject(role);
		reply.setValue(value);
		return reply;
	}

	public EntitySection createEntitySection(String title, DMEntity entity, TOCDataBinding value) {
		EntitySection reply = new EntitySection(getData());
		reply.setTitle(title);
		reply.setModelObject(entity);
		reply.setValue(value);
		return reply;
	}

	public OperationScreenSection createOperationScreenSection(String title, OperationComponentDefinition operationScreen,
			TOCDataBinding value) {
		OperationScreenSection reply = new OperationScreenSection(getData());
		reply.setTitle(title);
		reply.setModelObject(operationScreen);
		reply.setValue(value);
		return reply;
	}

	public ERDiagramSection createERDiagramSection(String title, ERDiagram diagram, TOCDataBinding value) {
		ERDiagramSection reply = new ERDiagramSection(getData());
		reply.setTitle(title);
		reply.setModelObject(diagram);
		reply.setValue(value);
		return reply;
	}

	@Override
	protected BindingModel buildBindingModel() {
		BindingModel returned = new BindingModel();
		returned.addToBindingVariables(new BindingVariableImpl(this, "project", FlexoProject.class) {
			@Override
			public Object getBindingValue(Object target, BindingEvaluationContext context) {
				logger.info("What should i return for project ? target " + target + " context=" + context);
				return super.getBindingValue(target, context);
			}
		});
		return returned;
	}

	@Override
	public String getDefaultTemplateName() {
		return null;
	}
}
