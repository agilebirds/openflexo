package org.openflexo.fme.dialog;

import org.openflexo.fme.model.DataModel;
import org.openflexo.toolbox.StringUtils;

public class CreateConceptDialog {

	private String conceptName;
	private DataModel dataModel;

	public CreateConceptDialog(DataModel dataModel, String name) {
		this.dataModel = dataModel;
		if(name==null||name.equals("")){
			name = "NewConcept";
		}
		conceptName = getCandidateConceptName(name);
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
}
