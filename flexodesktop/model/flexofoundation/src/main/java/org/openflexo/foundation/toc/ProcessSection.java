package org.openflexo.foundation.toc;

import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class ProcessSection extends ModelObjectSection<FlexoProcess> {

	public static enum ProcessDocSectionSubType {
		Doc, OperationTable, RaciMatrix, SIPOCLevel2, SIPOCLevel3
	}

	private ProcessDocSectionSubType subType;

	public ProcessSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public ProcessSection(TOCData data) {
		super(data);
	}

	@Override
	public ModelObjectType getModelObjectType() {
		return ModelObjectType.Process;
	}

	@Override
	public ProcessDocSectionSubType getSubType() {
		return subType;
	}

	@Override
	public void setSubType(ProcessDocSectionSubType subType) {
		this.subType = subType;
	}

	public FlexoProcess getProcess() {
		return getModelObject(true);
	}

	public void setProcess(FlexoProcess process) {
		setModelObject(process);
	}

}
