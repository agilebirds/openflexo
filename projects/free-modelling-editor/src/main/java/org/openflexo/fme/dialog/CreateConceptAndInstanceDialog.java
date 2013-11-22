package org.openflexo.fme.dialog;

import org.openflexo.fme.model.DataModel;
import org.openflexo.toolbox.StringUtils;

public class CreateConceptAndInstanceDialog {

	private String conceptName;
	private DataModel dataModel;
	private String instanceName;

	public CreateConceptAndInstanceDialog(DataModel dataModel, String instanceName) {
		this.dataModel = dataModel;
		this.instanceName = instanceName;
		conceptName = getCandidateConceptName("NewConcept");
	}

	public DataModel getDataModel() {
		return dataModel;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getConceptName() {
		return conceptName;
	}

	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}

	public boolean isValidConceptName() {
		return isValidConceptName(conceptName);
	}

	private boolean isValidConceptName(String proposedName) {
		if (StringUtils.isEmpty(proposedName)) {
			return false;
		}
		return dataModel.getConceptNamed(proposedName) == null;
	}

	private String getCandidateConceptName(String proposedName) {
		if (isValidConceptName(proposedName)) {
			return proposedName;
		}
		int i = 1;
		String aName = proposedName + i;
		while (!isValidConceptName(aName)) {
			i++;
			aName = proposedName + i;
		}
		return aName;
	}

	public boolean isValid() {
		return StringUtils.isNotEmpty(getInstanceName()) && isValidConceptName();
	}

}
