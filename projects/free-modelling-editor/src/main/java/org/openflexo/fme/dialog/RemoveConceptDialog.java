package org.openflexo.fme.dialog;

import org.openflexo.fme.model.Concept;

public class RemoveConceptDialog {

	private String conceptName;
	
	private Concept concept;

	public RemoveConceptDialog(Concept concept) {
		conceptName = concept.getName();
		this.concept = concept;
	}

	public String getConceptName() {
		return conceptName;
	}

	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}
	
	public boolean isUnusedConcept() {
		if(concept.getInstances().isEmpty()){
			return false;
		}
		else{
			return true;
		}
	}

}
