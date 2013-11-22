package org.openflexo.fme.dialog;

import org.openflexo.fme.model.Concept;
import org.openflexo.fme.model.DataModel;
import org.openflexo.toolbox.StringUtils;

public class CreateInstanceDialog {

	private Concept concept;
	private DataModel dataModel;
	private String instanceName;

	public CreateInstanceDialog(DataModel dataModel, String instanceName) {
		this.dataModel = dataModel;
		this.instanceName = instanceName;
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
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

	public boolean isValidInstanceName() {
		return isValidInstanceName(instanceName);
	}

	private boolean isValidInstanceName(String proposedName) {
		if (StringUtils.isEmpty(proposedName)) {
			return false;
		}
		return concept != null && concept.getInstanceNamed(proposedName) == null;
	}

	public boolean isValid() {
		System.out.println("Valid ???? concept=" + concept + " ? " + concept != null && isValidInstanceName());
		return concept != null && isValidInstanceName();
	}

}
