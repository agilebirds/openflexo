package org.openflexo.foundation.toc;

import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class ProcessSection extends ModelObjectSection<FlexoProcess> {

	protected static final String DOC_TEMPLATE = "docx_tocentry_process.xml.vm";
	protected static final String OPERATION_TABLE_TEMPLATE = "docx_tocentry_operationtable.xml.vm";
	protected static final String RACI_MATRIX_TEMPLATE = "docx_tocentry_raci.xml.vm";
	protected static final String SIPOC_LEVEL2_TEMPLATE = "docx_tocentry_sipoc2.xml.vm";
	protected static final String SIPOC_LEVEL3_TEMPLATE = "docx_tocentry_sipoc3.xml.vm";

	public static enum ProcessDocSectionSubType {
		Doc {
			@Override
			public String getDefaultTemplateName() {
				return DOC_TEMPLATE;
			}
		},
		OperationTable {
			@Override
			public String getDefaultTemplateName() {
				return OPERATION_TABLE_TEMPLATE;
			}
		},
		RaciMatrix {
			@Override
			public String getDefaultTemplateName() {
				return RACI_MATRIX_TEMPLATE;
			}
		},
		SIPOCLevel2 {
			@Override
			public String getDefaultTemplateName() {
				return SIPOC_LEVEL2_TEMPLATE;
			}
		},
		SIPOCLevel3 {
			@Override
			public String getDefaultTemplateName() {
				return SIPOC_LEVEL3_TEMPLATE;
			}
		};

		public abstract String getDefaultTemplateName();
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

	@Override
	public String getDefaultTemplateName() {
		if (getSubType() != null) {
			return getSubType().getDefaultTemplateName();
		}
		return null;
	}

	@Override
	public boolean isProcessesSection() {
		return true;
	}
}
