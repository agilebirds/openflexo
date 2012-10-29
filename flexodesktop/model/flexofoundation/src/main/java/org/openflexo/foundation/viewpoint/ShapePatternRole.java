package org.openflexo.foundation.viewpoint;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.localization.FlexoLocalization;

public class ShapePatternRole extends GraphicalElementPatternRole {

	// We dont want to import graphical engine in foundation
	// But you can assert graphical representation is a org.openflexo.fge.ShapeGraphicalRepresentationUtils.
	private Object _graphicalRepresentation;

	private ShapePatternRole parentShapePatternRole;

	public ShapePatternRole(ViewPointBuilder builder) {
		super(builder);
	}

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

	public static GraphicalFeature<Number, ShapeGraphicalRepresentation<?>> POS_X_FEATURE = new GraphicalFeature<Number, ShapeGraphicalRepresentation<?>>(
			"x", ShapeGraphicalRepresentation.ShapeParameters.x, Number.class) {
		@Override
		public Number retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr) {
			return gr.getX();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr, Number value) {
			gr.setX(value.doubleValue());
		}
	};

	public static GraphicalFeature<Number, ShapeGraphicalRepresentation<?>> POS_Y_FEATURE = new GraphicalFeature<Number, ShapeGraphicalRepresentation<?>>(
			"y", ShapeGraphicalRepresentation.ShapeParameters.y, Number.class) {
		@Override
		public Number retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr) {
			return gr.getY();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr, Number value) {
			gr.setY(value.doubleValue());
		}
	};

	public static GraphicalFeature<Number, ShapeGraphicalRepresentation<?>> WIDTH_FEATURE = new GraphicalFeature<Number, ShapeGraphicalRepresentation<?>>(
			"width", ShapeGraphicalRepresentation.ShapeParameters.width, Number.class) {
		@Override
		public Number retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr) {
			return gr.getWidth();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr, Number value) {
			gr.setWidth(value.doubleValue());
		}
	};

	public static GraphicalFeature<Number, ShapeGraphicalRepresentation<?>> HEIGHT_FEATURE = new GraphicalFeature<Number, ShapeGraphicalRepresentation<?>>(
			"height", ShapeGraphicalRepresentation.ShapeParameters.height, Number.class) {
		@Override
		public Number retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr) {
			return gr.getHeight();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr, Number value) {
			gr.setHeight(value.doubleValue());
		}
	};

	public static GraphicalFeature<Number, ShapeGraphicalRepresentation<?>> RELATIVE_TEXT_X_FEATURE = new GraphicalFeature<Number, ShapeGraphicalRepresentation<?>>(
			"relativeTextX", ShapeGraphicalRepresentation.ShapeParameters.x, Number.class) {
		@Override
		public Number retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr) {
			return gr.getRelativeTextX();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr, Number value) {
			gr.setRelativeTextX(value.doubleValue());
		}
	};

	public static GraphicalFeature<Number, ShapeGraphicalRepresentation<?>> RELATIVE_TEXT_Y_FEATURE = new GraphicalFeature<Number, ShapeGraphicalRepresentation<?>>(
			"relativeTextY", ShapeGraphicalRepresentation.ShapeParameters.x, Number.class) {
		@Override
		public Number retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr) {
			return gr.getRelativeTextY();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr, Number value) {
			gr.setRelativeTextY(value.doubleValue());
		}
	};

	public static GraphicalFeature<Number, ShapeGraphicalRepresentation<?>> ABSOLUTE_TEXT_X_FEATURE = new GraphicalFeature<Number, ShapeGraphicalRepresentation<?>>(
			"absoluteTextX", ShapeGraphicalRepresentation.ShapeParameters.x, Number.class) {
		@Override
		public Number retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr) {
			return gr.getAbsoluteTextX();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr, Number value) {
			gr.setAbsoluteTextX(value.doubleValue());
		}
	};

	public static GraphicalFeature<Number, ShapeGraphicalRepresentation<?>> ABSOLUTE_TEXT_Y_FEATURE = new GraphicalFeature<Number, ShapeGraphicalRepresentation<?>>(
			"absoluteTextY", ShapeGraphicalRepresentation.ShapeParameters.x, Number.class) {
		@Override
		public Number retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr) {
			return gr.getAbsoluteTextY();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation<?> gr, Number value) {
			gr.setAbsoluteTextY(value.doubleValue());
		}
	};

	public static GraphicalFeature<?, ?>[] AVAILABLE_FEATURES = { POS_X_FEATURE, POS_Y_FEATURE, WIDTH_FEATURE, HEIGHT_FEATURE,
			RELATIVE_TEXT_X_FEATURE, RELATIVE_TEXT_Y_FEATURE, ABSOLUTE_TEXT_X_FEATURE, ABSOLUTE_TEXT_Y_FEATURE };

}
