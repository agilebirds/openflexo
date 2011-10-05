package org.openflexo.foundation.viewpoint;


public abstract class GraphicalElementPatternRole extends PatternRole {

	private LabelRepresentation labelRepresentation;
	private String boundPatternRoleName;

 	public abstract Object getGraphicalRepresentation(); 

	public abstract void setGraphicalRepresentation(Object graphicalRepresentation); 
	
	public abstract void _setGraphicalRepresentationNoNotification(Object graphicalRepresentation); 

	public String getBoundPatternRoleName()
	{
		return boundPatternRoleName;
	}

	public void setBoundPatternRoleName(String aPatternRoleName) 
	{
		boundPatternRoleName = aPatternRoleName;
	}

	public PatternRole getBoundPatternRole()
	{
		return getEditionPattern().getPatternRole(boundPatternRoleName);
	}

	public void setBoundPatternRole(PatternRole aPatternRole)
	{
		boundPatternRoleName = (aPatternRole != null ? aPatternRole.getPatternRoleName() : null);
	}

	public LabelRepresentation retrieveLabelRepresentation() 
	{
		if (getLabelRepresentation() == null) {
			setLabelRepresentation(new LabelRepresentation());
		}
		return getLabelRepresentation();
	}

	public LabelRepresentation getLabelRepresentation() 
	{
		return labelRepresentation;
	}

	public void setLabelRepresentation(LabelRepresentation aLabelRepresentation) 
	{
		aLabelRepresentation.setPatternRole(this);
		labelRepresentation = aLabelRepresentation;
	}

	


}
