package org.openflexo.foundation.ontology.calc;

import org.openflexo.localization.FlexoLocalization;

public class ShapePatternRole extends GraphicalElementPatternRole {

    // We dont want to import graphical engine in foundation
    // But you can assert graphical representation is a org.openflexo.fge.ShapeGraphicalRepresentation.
	private Object _graphicalRepresentation;

    @Override
	public PatternRoleType getType()
	{
		return PatternRoleType.Shape;
	}


	@Override
	public String getPreciseType()
	{
		return FlexoLocalization.localizedForKey("shape");
	}

	@Override
	public Object getGraphicalRepresentation() 
	{
		return _graphicalRepresentation;
	}

	@Override
	public void setGraphicalRepresentation(Object graphicalRepresentation) 
	{
		_graphicalRepresentation = graphicalRepresentation;
		setChanged();
		notifyObservers(new GraphicalRepresentationChanged(this, graphicalRepresentation));
	}
	
	// No notification
	@Override
	public void _setGraphicalRepresentationNoNotification(Object graphicalRepresentation) 
	{
		_graphicalRepresentation = graphicalRepresentation;
	}
	
	public void tryToFindAGR()
	{
		if (getGraphicalRepresentation() == null) {
			// Try to find one somewhere
			for (CalcPalette palette : getCalc().getPalettes()) {
				for (CalcPaletteElement e : palette.getElements()) {
					if (e.getEditionPattern() == getEditionPattern()) {
						setGraphicalRepresentation(e.getGraphicalRepresentation());
					}
				}
			}
		}
	}


}
