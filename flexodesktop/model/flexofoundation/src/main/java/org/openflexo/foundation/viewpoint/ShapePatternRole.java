package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.view.ViewShape;
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
			for (ViewPointPalette palette : getCalc().getPalettes()) {
				for (ViewPointPaletteElement e : palette.getElements()) {
					if (e.getEditionPattern() == getEditionPattern()) {
						setGraphicalRepresentation(e.getGraphicalRepresentation());
					}
				}
			}
		}
	}

	@Override
	public Class<?> getAccessedClass()
	{
		return ViewShape.class;
	}

}
