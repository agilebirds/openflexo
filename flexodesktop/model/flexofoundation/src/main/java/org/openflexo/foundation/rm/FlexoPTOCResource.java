package org.openflexo.foundation.rm;

/**
 * MOS
 * @author MOSTAFA
 * 
 * TODO_MOS
 */

import org.openflexo.foundation.ptoc.PTOCData;
import org.openflexo.foundation.xml.FlexoPTOCBuilder;

public class FlexoPTOCResource extends FlexoXMLStorageResource<PTOCData> {

	public FlexoPTOCResource(FlexoProject project, PTOCData tocData) {
		this(project);
		_resourceData = tocData;
		tocData.setFlexoResource(this);
	}
	
	public FlexoPTOCResource(FlexoProject project) {
		super(project);
	}
	
	public FlexoPTOCResource(FlexoProjectBuilder projectBuilder) {
		super(projectBuilder.project);
		projectBuilder.notifyResourceLoading(this);
	}
	
	@Override
	public String getName() {
		return project.getName();
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.PTOC;
	}

	@Override
	public Class getResourceDataClass() {
		return PTOCData.class;
	}

	@Override
	public Object instanciateNewBuilder() {
		FlexoPTOCBuilder builder = new FlexoPTOCBuilder(this);
		builder.ptocData = _resourceData;
		return builder;
	}
	
	@Override
	public boolean hasBuilder() {
		return true;
	}
	
	@Override
	protected boolean isDuplicateSerializationIdentifierRepairable() {
		return false;
	}

	@Override
	protected boolean repairDuplicateSerializationIdentifier() {
		return false;
	}

}
