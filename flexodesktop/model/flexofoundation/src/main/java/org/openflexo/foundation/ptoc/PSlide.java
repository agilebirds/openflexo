package org.openflexo.foundation.ptoc;

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.RepresentableFlexoModelObject;
import org.openflexo.foundation.cg.dm.PTOCRepositoryChanged;
import org.openflexo.foundation.cg.utils.DocConstants.DocSection;
import org.openflexo.foundation.cg.utils.DocConstants.ProcessDocSectionSubType;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.utils.FlexoModelObjectReference.ReferenceOwner;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.xml.FlexoPTOCBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;

public class PSlide extends PTOCUnit   {

	private DocSection identifier;
	private ProcessDocSectionSubType subType;
	private String content;
	private boolean isReadOnly = false;
	
	public PSlide(PTOCData data) {
		super(data);
		// TODO_MOS Auto-generated constructor stub
	}
	
    public PSlide(FlexoPTOCBuilder builder)
    {
        this(builder.ptocData);
        initializeDeserialization(builder);
    }

    public PSlide(PTOCData generatedCode, FlexoModelObject modelObject) {
		this(generatedCode);
		this.objectReference = new FlexoModelObjectReference<FlexoModelObject>(generatedCode.getProject(), modelObject);
		this.objectReference.setSerializeClassName(true); // Even if the object is not loaded yet, we need to know its class name.
		this.objectReference.setOwner(this);
		isReadOnly = true;
	}

	public PSlide(PTOCData generatedCode, DocSection identifier) {
		this(generatedCode);
		this.identifier = identifier;
	}
	
	public PSlide(PTOCData data, FlexoModelObject modelObject,
			DocSection identifier) {
		this(data, modelObject);
		this.identifier = identifier;
	}

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) throws IllegalAccessException {
		if (isReadOnly)
			throw new IllegalAccessException("this entry is read only");
		String old = content;
		this.content = content;
		setChanged();
		notifyAttributeModification("content", old, content);
		if (getRepository()!=null)
			getRepository().notifyDocumentChanged(this);
	}
	
	public void setIsReadOnly(boolean isReadOnly) {
		boolean old = this.isReadOnly;
		this.isReadOnly = isReadOnly;
		setChanged();
		notifyAttributeModification("isReadOnly", old, isReadOnly);
	}
	
	public boolean isReadOnly() {
		return isReadOnly;
	}
	
	public int getDepth() {
		
			return getParent().getDepth() + 1;
	}

	
	public boolean isProcessesSection(){
		return identifier!=null && identifier==DocSection.PROCESSES;
	}

	public DocSection getIdentifier() {
		return identifier;
	}

	public void setIdentifier(DocSection identifier) {
		if (!isDeserializing())
			return;
		this.identifier = identifier;
	}

	public ProcessDocSectionSubType getSubType() {
		return subType;
	}

	public void setSubType(ProcessDocSectionSubType subType) {
		this.subType = subType;
	}
	
	public String getFullyQualifiedName() {
		if (getRepository()==null)
			return getTitle();
		if (getParent()==null)
			return "PSLIDE."+getRepository().getTitle()+"."+getTitle();
		else
			return getParent().getFullyQualifiedName()+"."+getTitle();
	}
	
	
	public boolean isDocSubType()
	{
		return getSubType() == ProcessDocSectionSubType.Doc;
	}

	public boolean isRACISubType()
	{
		return getSubType() == ProcessDocSectionSubType.RaciMatrix;
	}

	public boolean isSIPOC2SubType()
	{
		return getSubType() == ProcessDocSectionSubType.SIPOCLevel2;
	}

	public boolean isSIPOC3SubType()
	{
		return getSubType() == ProcessDocSectionSubType.SIPOCLevel3;
	}

	public boolean isOperationTableSubType()
	{
		return getSubType() == ProcessDocSectionSubType.OperationTable;
	}

	public boolean isERDiagram()
	{
		return getIdentifier() == DocSection.ER_DIAGRAM;
	}

	public boolean isIndividualProcessOrProcessFolder()
	{
		return getIdentifier() == null && (getObject() instanceof FlexoProcess || getObject() instanceof ProcessFolder);
	}

	public boolean isIndividualProcess()
	{
		return getIdentifier() == null && getObject() instanceof FlexoProcess;
	}

	public boolean isIndividualProcessFolder()
	{
		return getIdentifier() == null && getObject() instanceof ProcessFolder;
	}

	public boolean isIndividualRole()
	{
		return getIdentifier() == null && getObject() instanceof Role;
	}

	public boolean isIndividualEntity()
	{
		return getIdentifier() == null && getObject() instanceof DMEOEntity;
	}

	public boolean isIndividualComponentDefinition()
	{
		return getIdentifier() == null && getObject() instanceof ComponentDefinition;
	}
	
	public ERDiagram getDocumentedDiagram(){
		if(isERDiagram())
			 return (ERDiagram)getObject();
		return null;
	}

	public Role getDocumentedRole(){
		if(isIndividualRole())
			 return (Role)getObject();
		return null;
	}

	public DMEOEntity getDocumentedDMEOEntity(){
		if(isIndividualEntity())
			 return (DMEOEntity)getObject();
		return null;
	}

	public ComponentDefinition getDocumentedComponentDefinition(){
		if(isIndividualComponentDefinition())
			 return (ComponentDefinition)getObject();
		return null;
	}
	
	
	public RepresentableFlexoModelObject getDocumentedFlexoProcess(){
		if(getObject() instanceof FlexoProcess || getObject() instanceof ProcessFolder)
			 return (RepresentableFlexoModelObject)getObject();
		return null;
	}

	

	public void setDocumentedFlexoProcess(RepresentableFlexoModelObject object){
		if(object!=null && object.equals(getObject()))return;
		if(object==null)return;

		if(!(object instanceof FlexoProcess) && !(object instanceof ProcessFolder))
			throw new IllegalArgumentException("setDocumentedFlexoProcess MUST have either a FlexoProcess or a ProcessFolder");

		setObject(object);
		setChanged();
		notifyAttributeModification("objectReference", null, object);
		if (getRepository()!=null)
			getRepository().notifyDocumentChanged(this);
		setChanged();
		notifyAttributeModification("documentedFlexoProcess", null, object);
	}

	public void setDocumentedRole(Role object){
		if(object!=null && object.equals(getObject()))return;
		if(object==null)return;
		setObject(object);
		setChanged();
		notifyAttributeModification("objectReference", null, object);
		if (getRepository()!=null)
			getRepository().notifyDocumentChanged(this);
		setChanged();
		notifyAttributeModification("documentedRole", null, object);
	}

	public void setDocumentedDMEOEntity(DMEOEntity object){
		if(object!=null && object.equals(getObject()))return;
		if(object==null)return;
		setObject(object);
		setChanged();
		notifyAttributeModification("objectReference", null, object);
		if (getRepository()!=null)
			getRepository().notifyDocumentChanged(this);
		setChanged();
		notifyAttributeModification("documentedDMEOEntity", null, object);
	}

	public void setDocumentedComponentDefinition(ComponentDefinition object){
		if(object!=null && object.equals(getObject()))return;
		if(object==null)return;
		setObject(object);
		setChanged();
		notifyAttributeModification("objectReference", null, object);
		if (getRepository()!=null)
			getRepository().notifyDocumentChanged(this);
		setChanged();
		notifyAttributeModification("documentedComponentDefinition", null, object);
	}

	public void setDocumentedDiagram(ERDiagram object){
		if(object!=null && object.equals(getObject()))return;
		if(object==null)return;
		setObject(object);
		setChanged();
		notifyAttributeModification("objectReference", null, object);
		if (getRepository()!=null)
			getRepository().notifyDocumentChanged(this);
		setChanged();
		notifyAttributeModification("documentedDiagram", null, object);
	}

	public Vector<ERDiagram> availableDiagrams(){
		return getProject().getDataModel().getDiagrams();
	}
	

	
	


	@Override
	public String getInspectorName() {
		// TODO_MOS Auto-generated method stub
		return null;
	}

	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference<?> reference) {
		// TODO_MOS Auto-generated method stub
		
	}

	@Override
	public void objectCantBeFound(FlexoModelObjectReference<?> reference) {
		// TODO_MOS Auto-generated method stub
		
	}

	@Override
	public void objectDeleted(FlexoModelObjectReference<?> reference) {
		// TODO_MOS Auto-generated method stub
		
	}

	@Override
	public void objectSerializationIdChanged(
			FlexoModelObjectReference<?> reference) {
		// TODO_MOS Auto-generated method stub
		
	}


	@Override
	public void setRepository(PTOCRepository repository) {
		PTOCRepository old = this.repository;
		this.repository = repository;
		setChanged();
		notifyObservers(new PTOCRepositoryChanged(old,repository));
	}

	public static PSlide cloneEntryFromTemplate(PTOCEntry father,
			PSlide source) {
		PSlide reply = new PSlide(father.getData(),source.getIdentifier());
		reply.setTitle(source.getTitle());
		reply.content = source.getContent();
		reply.setIsReadOnly(source.isReadOnly());
		father.addToPTocUnits(reply);
		return reply;
	}


	
	


}
