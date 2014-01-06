package org.openflexo.fme.dialog;

import org.openflexo.fme.model.Concept;
import org.openflexo.toolbox.StringUtils;

public class RenameConceptDialog {

	private String conceptName;
	
	private Concept concept;

	public RenameConceptDialog(Concept concept) {
		conceptName = concept.getName();
		this.concept = concept;
	}

	public String getConceptName() {
		return conceptName;
	}

	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}

	public boolean isLocked(){
		return concept.getReadOnly();
	}
	
	private boolean isValidConceptName(String proposedName) {
		if (StringUtils.isEmpty(proposedName)) {
			return false;
		}
		if (concept.getReadOnly()) {
			return false;
		}
		return concept.getDataModel().getConceptNamed(proposedName) == null;
	}
	
	
	
}
