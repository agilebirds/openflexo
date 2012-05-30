package org.openflexo.foundation.viewpoint;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.localization.FlexoLocalization;

public class ShapePatternRole extends GraphicalElementPatternRole {

	// We dont want to import graphical engine in foundation
	// But you can assert graphical representation is a org.openflexo.fge.ShapeGraphicalRepresentation.
	private Object _graphicalRepresentation;

	private ShapePatternRole parentShapePatternRole;

	@Override
	protected void initDefaultSpecifications() {
		super.initDefaultSpecifications();
		for (GraphicalFeature<?, ?> GF : AVAILABLE_FEATURES) {
			grSpecifications.add(new GraphicalElementSpecification(this, GF, false, true));
		}
	}

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.Shape;
	}

	@Override
	public String getPreciseType() {
		return FlexoLocalization.localizedForKey("shape");
	}

	@Override
	public Object getGraphicalRepresentation() {
		return _graphicalRepresentation;
	}

	@Override
	public void setGraphicalRepresentation(Object graphicalRepresentation) {
		_graphicalRepresentation = graphicalRepresentation;
		setChanged();
		notifyObservers(new GraphicalRepresentationChanged(this, graphicalRepresentation));
	}

	public void updateGraphicalRepresentation(Object graphicalRepresentation) {
		if (_graphicalRepresentation != null) {
			((ShapeGraphicalRepresentation<?>) _graphicalRepresentation).setsWith((GraphicalRepresentation<?>) graphicalRepresentation);
			setChanged();
			notifyObservers(new GraphicalRepresentationModified(this, graphicalRepresentation));
		} else {
			setGraphicalRepresentation(graphicalRepresentation);
		}
	}

	// No notification
	@Override
	public void _setGraphicalRepresentationNoNotification(Object graphicalRepresentation) {
		_graphicalRepresentation = graphicalRepresentation;
	}

	public void tryToFindAGR() {
		if (getGraphicalRepresentation() == null) {
			// Try to find one somewhere
			for (ViewPointPalette palette : getViewPoint().getPalettes()) {
				for (ViewPointPaletteElement e : palette.getElements()) {
					if (e.getEditionPattern() == getEditionPattern()) {
						setGraphicalRepresentation(e.getGraphicalRepresentation());
					}
				}
			}
		}
	}

	@Override
	public Class<?> getAccessedClass() {
		return ViewShape.class;
	}

	public ShapePatternRole getParentShapePatternRole() {
		return parentShapePatternRole;
	}

	public void setParentShapePatternRole(ShapePatternRole parentShapePatternRole) {
		this.parentShapePatternRole = parentShapePatternRole;
		setChanged();
		notifyObservers();
	}

	public boolean getParentShapeAsDefinedInAction() {
		return getParentShapePatternRole() == null;
	}

	public void setParentShapeAsDefinedInAction(boolean flag) {
		if (!flag && getEditionPattern().getShapePatternRoles().size() > 0) {
			setParentShapePatternRole(getEditionPattern().getShapePatternRoles().get(0));
		} else {
			System.out.println("setParentShapePatternRole with null");
			setParentShapePatternRole(null);
		}
	}

	public boolean isEmbeddedIn(ShapePatternRole aPR) {
		if (getParentShapePatternRole() != null) {
			if (getParentShapePatternRole() == aPR) {
				return true;
			} else {
				return getParentShapePatternRole().isEmbeddedIn(aPR);
			}
		}
		return false;
	}

	@Override
	public boolean isIncludedInPrimaryRepresentationRole() {
		if (getParentShapePatternRole() != null) {
			return getParentShapePatternRole().isIncludedInPrimaryRepresentationRole();
		}
		return super.isIncludedInPrimaryRepresentationRole();
	}

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation<?>> POS_X_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation<?>>(
			"x", ShapeGraphicalRepresentation.Parameters.x, Double.class) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr) {
			return gr.getX();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr, Double value) {
			gr.setX(value);
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation<?>> POS_Y_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation<?>>(
			"y", ShapeGraphicalRepresentation.Parameters.y, Double.class) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr) {
			return gr.getY();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr, Double value) {
			gr.setY(value);
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation<?>> WIDTH_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation<?>>(
			"width", ShapeGraphicalRepresentation.Parameters.width, Double.class) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr) {
			return gr.getWidth();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr, Double value) {
			gr.setWidth(value);
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation<?>> HEIGHT_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation<?>>(
			"height", ShapeGraphicalRepresentation.Parameters.height, Double.class) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr) {
			return gr.getHeight();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr, Double value) {
			gr.setHeight(value);
		}
	};

	public static GraphicalFeature<?, ?>[] AVAILABLE_FEATURES = { POS_X_FEATURE, POS_Y_FEATURE, WIDTH_FEATURE, HEIGHT_FEATURE };

}
