package org.openflexo.foundation.view.diagram.viewpoint;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.view.diagram.model.dm.GraphicalRepresentationChanged;
import org.openflexo.foundation.view.diagram.model.dm.GraphicalRepresentationModified;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.localization.FlexoLocalization;

public class ShapePatternRole extends GraphicalElementPatternRole<DiagramShape> {

	private static final Logger logger = Logger.getLogger(ShapePatternRole.class.getPackage().getName());

	private ShapeGraphicalRepresentation _graphicalRepresentation;

	private ShapePatternRole parentShapePatternRole;

	// private List<ShapePatternRole> _possibleParentPatternRole;

	public ShapePatternRole(VirtualModel.VirtualModelBuilder builder) {
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
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		out.append("PatternRole " + getName() + " as ShapeSpecification from " + getVirtualModel().getReflexiveModelSlot().getName() + ";",
				context);
		return out.toString();
	}

	@Override
	public String getPreciseType() {
		return FlexoLocalization.localizedForKey("shape");
	}

	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return _graphicalRepresentation;
	}

	@Override
	public void setGraphicalRepresentation(GraphicalRepresentation graphicalRepresentation) {
		_graphicalRepresentation = (ShapeGraphicalRepresentation) graphicalRepresentation;
		setChanged();
		notifyObservers(new GraphicalRepresentationChanged(this, graphicalRepresentation));
	}

	public void updateGraphicalRepresentation(ShapeGraphicalRepresentation graphicalRepresentation) {
		if (_graphicalRepresentation != null) {
			_graphicalRepresentation.setsWith(graphicalRepresentation);
			setChanged();
			notifyObservers(new GraphicalRepresentationModified(this, graphicalRepresentation));
		} else {
			setGraphicalRepresentation(graphicalRepresentation);
		}
	}

	// No notification
	@Override
	public void _setGraphicalRepresentationNoNotification(GraphicalRepresentation graphicalRepresentation) {
		_graphicalRepresentation = (ShapeGraphicalRepresentation) graphicalRepresentation;
	}

	public void tryToFindAGR() {
		if (getGraphicalRepresentation() == null) {
			// Try to find one somewhere
			for (DiagramPalette palette : getVirtualModel().getPalettes()) {
				for (DiagramPaletteElement e : palette.getElements()) {
					if (e.getEditionPattern() == getEditionPattern()) {
						setGraphicalRepresentation(e.getGraphicalRepresentation());
					}
				}
			}
		}
	}

	@Override
	public Type getType() {
		return DiagramShape.class;
	}

	private boolean detectLoopInParentShapePatternRoleDefinition() {
		List<ShapePatternRole> list = new ArrayList<ShapePatternRole>();
		ShapePatternRole current = this;
		while (!list.contains(current) && current != null) {
			list.add(current);
			current = current.getParentShapePatternRole();
		}
		if (current != null) {
			return true;
		}
		return false;
	}

	public ShapePatternRole getParentShapePatternRole() {
		return parentShapePatternRole;
	}

	public void setParentShapePatternRole(ShapePatternRole parentShapePatternRole) {
		if (parentShapePatternRole != this.parentShapePatternRole) {
			ShapePatternRole oldParentShapePatternRole = this.parentShapePatternRole;
			logger.info(">>>> setParentShapePatternRole() with " + parentShapePatternRole);
			this.parentShapePatternRole = parentShapePatternRole;
			if (detectLoopInParentShapePatternRoleDefinition()) {
				logger.warning("Detecting a loop in parent shape pattern role definition. Resetting parent shape pattern role");
				this.parentShapePatternRole = null;
			}
			setChanged();
			notifyObservers();
		}
	}

	public boolean getParentShapeAsDefinedInAction() {
		return getParentShapePatternRole() == null;
	}

	public void setParentShapeAsDefinedInAction(boolean flag) {
		// System.out.println(">>>> setParentShapeAsDefinedInAction() with " + flag);
		List<ShapePatternRole> possibleParentPatternRole = getPossibleParentShapePatternRoles();
		if (!flag) {
			if (possibleParentPatternRole.size() > 0) {
				setParentShapePatternRole(possibleParentPatternRole.get(0));
			}
		} else {
			// System.out.println("setParentShapePatternRole with null");
			setParentShapePatternRole(null);
			// flag = true;
		}
	}

	private boolean isContainedIn(ShapePatternRole container) {
		if (container == this) {
			return true;
		}
		if (getParentShapePatternRole() != null) {
			return getParentShapePatternRole().isContainedIn(container);
		}
		return false;
	}

	/**
	 * Get the list of shape pattern roles that can be set as parent shape pattern role. This list contains all other shape pattern roles of
	 * current edition pattern which are not already in the containment subtree
	 * 
	 * @return
	 */
	public List<ShapePatternRole> getPossibleParentShapePatternRoles() {
		List<ShapePatternRole> returned = new ArrayList<ShapePatternRole>();
		List<ShapePatternRole> shapesPatternRoles = getEditionPattern().getShapePatternRoles();
		for (ShapePatternRole shapePatternRole : shapesPatternRoles) {
			if (!shapePatternRole.isContainedIn(this)) {
				returned.add(shapePatternRole);
			}
		}
		return returned;
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
		if (getParentShapePatternRole() != null && getParentShapePatternRole() != this) {
			return getParentShapePatternRole().isIncludedInPrimaryRepresentationRole();
		}
		return super.isIncludedInPrimaryRepresentationRole();
	}

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> POS_X_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"x", ShapeGraphicalRepresentation.X) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getX();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setX(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> POS_Y_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"y", ShapeGraphicalRepresentation.Y) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getY();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setY(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> WIDTH_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"width", ShapeGraphicalRepresentation.WIDTH) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getWidth();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setWidth(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> HEIGHT_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"height", ShapeGraphicalRepresentation.HEIGHT) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getHeight();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setHeight(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> RELATIVE_TEXT_X_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"relativeTextX", ShapeGraphicalRepresentation.RELATIVE_TEXT_X) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getRelativeTextX();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setRelativeTextX(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> RELATIVE_TEXT_Y_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"relativeTextY", ShapeGraphicalRepresentation.RELATIVE_TEXT_Y) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getRelativeTextY();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setRelativeTextY(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> ABSOLUTE_TEXT_X_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"absoluteTextX", ShapeGraphicalRepresentation.ABSOLUTE_TEXT_X) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getAbsoluteTextX();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setAbsoluteTextX(value.doubleValue());
		}
	};

	public static GraphicalFeature<Double, ShapeGraphicalRepresentation> ABSOLUTE_TEXT_Y_FEATURE = new GraphicalFeature<Double, ShapeGraphicalRepresentation>(
			"absoluteTextY", ShapeGraphicalRepresentation.ABSOLUTE_TEXT_Y) {
		@Override
		public Double retrieveFromGraphicalRepresentation(ShapeGraphicalRepresentation gr) {
			return gr.getAbsoluteTextY();
		}

		@Override
		public void applyToGraphicalRepresentation(ShapeGraphicalRepresentation gr, Double value) {
			gr.setAbsoluteTextY(value.doubleValue());
		}
	};

	public static GraphicalFeature<?, ?>[] AVAILABLE_FEATURES = { POS_X_FEATURE, POS_Y_FEATURE, WIDTH_FEATURE, HEIGHT_FEATURE,
			RELATIVE_TEXT_X_FEATURE, RELATIVE_TEXT_Y_FEATURE, ABSOLUTE_TEXT_X_FEATURE, ABSOLUTE_TEXT_Y_FEATURE };

}
