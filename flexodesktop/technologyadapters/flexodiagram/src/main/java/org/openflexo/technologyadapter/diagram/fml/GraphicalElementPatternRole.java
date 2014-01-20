package org.openflexo.technologyadapter.diagram.fml;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.ModelObjectActorReference;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.FreeDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.fml.GraphicalElementAction.ActionMask;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;
import org.openflexo.technologyadapter.diagram.model.dm.GraphicalRepresentationChanged;
import org.openflexo.technologyadapter.diagram.model.dm.GraphicalRepresentationModified;

@ModelEntity(isAbstract = true)
@ImplementationClass(GraphicalElementPatternRole.GraphicalElementPatternRoleImpl.class)
public abstract interface GraphicalElementPatternRole<T extends DiagramElement<GR>, GR extends GraphicalRepresentation> extends
		PatternRole<T>, Bindable {

	public static GraphicalFeature<String, GraphicalRepresentation> LABEL_FEATURE = new GraphicalFeature<String, GraphicalRepresentation>(
			"label", GraphicalRepresentation.TEXT) {
		@Override
		public String retrieveFromGraphicalRepresentation(GraphicalRepresentation gr) {
			return gr.getText();
		}

		@Override
		public void applyToGraphicalRepresentation(GraphicalRepresentation gr, String value) {
			gr.setText(value);
		}
	};

	public static GraphicalFeature<Boolean, GraphicalRepresentation> VISIBLE_FEATURE = new GraphicalFeature<Boolean, GraphicalRepresentation>(
			"visible", GraphicalRepresentation.IS_VISIBLE) {
		@Override
		public Boolean retrieveFromGraphicalRepresentation(GraphicalRepresentation gr) {
			return gr.getIsVisible();
		}

		@Override
		public void applyToGraphicalRepresentation(GraphicalRepresentation gr, Boolean value) {
			gr.setIsVisible(value);
		}
	};

	public static GraphicalFeature<Double, GraphicalRepresentation> TRANSPARENCY_FEATURE = new GraphicalFeature<Double, GraphicalRepresentation>(
			"transparency", GraphicalRepresentation.TRANSPARENCY) {
		@Override
		public Double retrieveFromGraphicalRepresentation(GraphicalRepresentation gr) {
			return gr.getTransparency();
		}

		@Override
		public void applyToGraphicalRepresentation(GraphicalRepresentation gr, Double value) {
			gr.setTransparency(value);
		}
	};

	public static GraphicalFeature<?, ?>[] AVAILABLE_FEATURES = { LABEL_FEATURE, VISIBLE_FEATURE, TRANSPARENCY_FEATURE };

	@PropertyIdentifier(type = String.class)
	public static final String EXAMPLE_LABEL_KEY = "exampleLabel";
	@PropertyIdentifier(type = Vector.class)
	public static final String ACTIONS_KEY = "actions";
	@PropertyIdentifier(type = Vector.class)
	public static final String DECLARED_GRSPECIFICATIONS_KEY = "declaredGRSpecifications";

	@Getter(value = EXAMPLE_LABEL_KEY)
	@XMLAttribute
	public String getExampleLabel();

	@Setter(EXAMPLE_LABEL_KEY)
	public void setExampleLabel(String exampleLabel);

	@Getter(value = ACTIONS_KEY, cardinality = Cardinality.LIST, inverse = GraphicalElementAction.GRAPHICAL_ELEMENT_PATTERN_ROLE_KEY)
	@XMLElement
	public List<GraphicalElementAction> getActions();

	@Setter(ACTIONS_KEY)
	public void setActions(List<GraphicalElementAction> actions);

	@Adder(ACTIONS_KEY)
	public void addToActions(GraphicalElementAction aAction);

	@Remover(ACTIONS_KEY)
	public void removeFromActions(GraphicalElementAction aAction);

	@Getter(value = DECLARED_GRSPECIFICATIONS_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	public List<GraphicalElementSpecification<?, GR>> _getDeclaredGRSpecifications();

	@Setter(DECLARED_GRSPECIFICATIONS_KEY)
	public void _setDeclaredGRSpecifications(List<GraphicalElementSpecification<?, GR>> declaredGRSpecifications);

	@Adder(DECLARED_GRSPECIFICATIONS_KEY)
	public void _addToDeclaredGRSpecifications(GraphicalElementSpecification<?, GR> aDeclaredGRSpecification);

	@Remover(DECLARED_GRSPECIFICATIONS_KEY)
	public void _removeFromDeclaredGRSpecifications(GraphicalElementSpecification<?, GR> aDeclaredGRSpecification);

	public void updateGraphicalRepresentation(GR graphicalRepresentation);

	// Convenient method to access spec for label feature
	public DataBinding<String> getLabel();

	// Convenient method to access spec for label feature
	public void setLabel(DataBinding<String> label);

	// Convenient method to access read-only property for spec for label feature
	public boolean getReadOnlyLabel();

	// Convenient method to access read-only property for spec for label feature
	public void setReadOnlyLabel(boolean readOnlyLabel);

	public List<GraphicalElementSpecification<?, ?>> getGrSpecifications();

	public GraphicalElementSpecification<?, ?> getGraphicalElementSpecification(String featureName);

	public GraphicalElementSpecification<?, ?> getGraphicalElementSpecification(GraphicalFeature<?, ?> feature);

	public boolean containsShapes();

	public static abstract class GraphicalElementPatternRoleImpl<T extends DiagramElement<GR>, GR extends GraphicalRepresentation> extends
			PatternRoleImpl<T> implements GraphicalElementPatternRole<T, GR> {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(GraphicalElementPatternRole.class.getPackage().getName());

		// private boolean readOnlyLabel;

		private String exampleLabel = "label";

		protected List<GraphicalElementSpecification<?, ?>> grSpecifications;

		// private Vector<GraphicalElementAction> actions;

		private GR graphicalRepresentation;

		public GraphicalElementPatternRoleImpl() {
			super();
			initDefaultSpecifications();
		}

		protected void initDefaultSpecifications() {
			grSpecifications = new ArrayList<GraphicalElementSpecification<?, ?>>();
			for (GraphicalFeature<?, ?> GF : AVAILABLE_FEATURES) {
				GraphicalElementSpecification newGraphicalElementSpecification = getVirtualModelFactory().newInstance(
						GraphicalElementSpecification.class);
				newGraphicalElementSpecification.setPatternRole(this);
				newGraphicalElementSpecification.setFeature(GF);
				newGraphicalElementSpecification.setReadOnly(false);
				newGraphicalElementSpecification.setMandatory(true);
				grSpecifications.add(newGraphicalElementSpecification);
			}
		}

		public DiagramSpecification getDiagramSpecification() {
			if (getModelSlot() instanceof TypedDiagramModelSlot) {
				try {
					return ((TypedDiagramModelSlot) getModelSlot()).getMetaModelResource().getResourceData(null);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (ResourceLoadingCancelledException e) {
					e.printStackTrace();
				} catch (FlexoException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		public boolean defaultBehaviourIsToBeDeleted() {
			return true;
		}

		public final GR getGraphicalRepresentation() {
			return graphicalRepresentation;
		}

		public final void setGraphicalRepresentation(GR graphicalRepresentation) {
			GR oldGR = this.graphicalRepresentation;
			if (this.graphicalRepresentation != graphicalRepresentation) {
				this.graphicalRepresentation = graphicalRepresentation;
				setChanged();
				notifyObservers(new GraphicalRepresentationChanged(this, graphicalRepresentation));
			}
		}

		@Override
		public final void updateGraphicalRepresentation(GR graphicalRepresentation) {
			if (getGraphicalRepresentation() != null) {
				getGraphicalRepresentation().setsWith(graphicalRepresentation);
				setChanged();
				notifyObservers(new GraphicalRepresentationModified(this, graphicalRepresentation));
			} else {
				setGraphicalRepresentation(graphicalRepresentation);
			}
		}

		protected final void _setGraphicalRepresentationNoNotification(GR graphicalRepresentation) {
			this.graphicalRepresentation = graphicalRepresentation;
		}

		private BindingDefinition LABEL;

		public BindingDefinition getLabelBindingDefinition() {
			if (LABEL == null) {
				LABEL = new BindingDefinition("label", String.class, DataBinding.BindingDefinitionType.GET_SET, false) {
					@Override
					public DataBinding.BindingDefinitionType getBindingDefinitionType() {
						if (getReadOnlyLabel()) {
							return DataBinding.BindingDefinitionType.GET;
						} else {
							return DataBinding.BindingDefinitionType.GET_SET;
						}
					}
				};
			}
			return LABEL;
		}

		// Convenient method to access spec for label feature
		@Override
		public DataBinding<String> getLabel() {
			return getGraphicalElementSpecification(LABEL_FEATURE).getValue();
		}

		// Convenient method to access spec for label feature
		@Override
		public void setLabel(DataBinding<String> label) {
			getGraphicalElementSpecification(LABEL_FEATURE).setValue(label);
		}

		// Convenient method to access read-only property for spec for label feature
		@Override
		public boolean getReadOnlyLabel() {
			return getGraphicalElementSpecification(LABEL_FEATURE).getReadOnly();
		}

		// Convenient method to access read-only property for spec for label feature
		@Override
		public void setReadOnlyLabel(boolean readOnlyLabel) {
			getGraphicalElementSpecification(LABEL_FEATURE).setReadOnly(readOnlyLabel);
		}

		@Override
		public BindingFactory getBindingFactory() {
			return getEditionPattern().getInspector().getBindingFactory();
		}

		/*@Override
		public BindingModel getBindingModel() {
			return getEditionPattern().getInspector().getBindingModel();
		}*/

		/*public boolean getIsPrimaryRepresentationRole() {
			if (getEditionPattern() == null) {
				return false;
			}
			return getEditionPattern().getPrimaryRepresentationRole() == this;
		}

		public void setIsPrimaryRepresentationRole(boolean isPrimary) {
			if (getEditionPattern() == null) {
				return;
			}
			if (isPrimary) {
				getEditionPattern().setPrimaryRepresentationRole(this);
			} else {
				getEditionPattern().setPrimaryRepresentationRole(null);
			}
		}

		public boolean isIncludedInPrimaryRepresentationRole() {
			return getIsPrimaryRepresentationRole();
		}

		@Override
		public boolean getIsPrimaryRole() {
			return getIsPrimaryRepresentationRole();
		}

		@Override
		public void setIsPrimaryRole(boolean isPrimary) {
			setIsPrimaryRepresentationRole(isPrimary);
		}
		*/

		@Override
		public boolean containsShapes() {
			for (ShapePatternRole role : getEditionPattern().getPatternRoles(ShapePatternRole.class)) {
				if (role.getParentShapePatternRole() == this) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String getExampleLabel() {
			return exampleLabel;
		}

		@Override
		public void setExampleLabel(String exampleLabel) {
			this.exampleLabel = exampleLabel;
		}

		/*@Override
		public Vector<GraphicalElementAction> getActions() {
			if (actions == null) {
				actions = new Vector<GraphicalElementAction>();
			}
			return actions;
		}

		public void setActions(Vector<GraphicalElementAction> someActions) {
			actions = someActions;
		}

		@Override
		public void addToActions(GraphicalElementAction anAction) {
			anAction.setGraphicalElementPatternRole(this);
			actions.add(anAction);
			setChanged();
			notifyObservers(new GraphicalElementActionInserted(anAction, this));
		}

		@Override
		public void removeFromActions(GraphicalElementAction anAction) {
			anAction.setGraphicalElementPatternRole(null);
			actions.remove(anAction);
			setChanged();
			notifyObservers(new GraphicalElementActionRemoved(anAction, this));
		}*/

		public List<ActionMask> getReferencedMasks() {
			ArrayList<GraphicalElementAction.ActionMask> returned = new ArrayList<GraphicalElementAction.ActionMask>();
			for (GraphicalElementAction a : getActions()) {
				if (!returned.contains(a.getActionMask())) {
					returned.add(a.getActionMask());
				}
			}
			return returned;
		}

		public List<GraphicalElementAction> getActions(ActionMask mask) {
			ArrayList<GraphicalElementAction> returned = new ArrayList<GraphicalElementAction>();
			for (GraphicalElementAction a : getActions()) {
				if (a.getActionMask() == mask) {
					returned.add(a);
				}
			}
			return returned;
		}

		public GraphicalElementAction createAction() {
			GraphicalElementAction newAction = getVirtualModelFactory().newInstance(GraphicalElementAction.class);
			addToActions(newAction);
			return newAction;
		}

		public GraphicalElementAction deleteAction(GraphicalElementAction anAction) {
			removeFromActions(anAction);
			anAction.delete();
			return anAction;
		}

		@Override
		public List<GraphicalElementSpecification<?, ?>> getGrSpecifications() {
			return grSpecifications;
		}

		@Override
		public GraphicalElementSpecification<?, ?> getGraphicalElementSpecification(String featureName) {
			for (GraphicalElementSpecification<?, ?> spec : grSpecifications) {
				if (spec.getFeatureName().equals(featureName)) {
					return spec;
				}
			}
			return null;
		}

		@Override
		public GraphicalElementSpecification<?, ?> getGraphicalElementSpecification(GraphicalFeature<?, ?> feature) {
			if (feature != null) {
				return getGraphicalElementSpecification(feature.getName());
			}
			return null;
		}

		@Override
		public List<GraphicalElementSpecification<?, GR>> _getDeclaredGRSpecifications() {
			List<GraphicalElementSpecification<?, GR>> returned = new ArrayList<GraphicalElementSpecification<?, GR>>();
			for (GraphicalElementSpecification<?, ?> spec : grSpecifications) {
				if (spec.getValue().isSet()) {
					returned.add((GraphicalElementSpecification<?, GR>) spec);
				}
			}
			return returned;
		}

		@Override
		public void _setDeclaredGRSpecifications(List<GraphicalElementSpecification<?, GR>> someSpecs) {
			for (GraphicalElementSpecification<?, GR> s : someSpecs) {
				_addToDeclaredGRSpecifications(s);
			}
		}

		@Override
		public void _addToDeclaredGRSpecifications(GraphicalElementSpecification<?, GR> aSpec) {
			GraphicalElementSpecification<?, ?> existingSpec = getGraphicalElementSpecification(aSpec.getFeatureName());
			if (existingSpec == null) {
				logger.warning("Cannot find any GraphicalElementSpecification matching " + aSpec.getFeatureName() + ". Ignoring...");
			} else {
				existingSpec.setValue(aSpec.getValue());
				existingSpec.setReadOnly(aSpec.getReadOnly());
			}
		}

		@Override
		public void _removeFromDeclaredGRSpecifications(GraphicalElementSpecification<?, GR> aSpec) {
			GraphicalElementSpecification<?, ?> existingSpec = getGraphicalElementSpecification(aSpec.getFeatureName());
			if (existingSpec == null) {
				logger.warning("Cannot find any GraphicalElementSpecification matching " + aSpec.getFeatureName() + ". Ignoring...");
			} else {
				existingSpec.setValue(null);
			}
		}

		@Override
		public ModelObjectActorReference makeActorReference(T object, EditionPatternInstance epi) {
			// TODO
			// return new ModelObjectActorReference<T>(object, this, epi);
			return null;
		}

		@Override
		public ModelSlot<?> getModelSlot() {
			ModelSlot<?> returned = super.getModelSlot();
			if (returned == null) {
				if (getVirtualModel() != null && getVirtualModel().getModelSlots(TypedDiagramModelSlot.class).size() > 0) {
					return getVirtualModel().getModelSlots(TypedDiagramModelSlot.class).get(0);
				}
				if (getVirtualModel() != null && getVirtualModel().getModelSlots(FreeDiagramModelSlot.class).size() > 0) {
					return getVirtualModel().getModelSlots(FreeDiagramModelSlot.class).get(0);
				}
			}
			return returned;
		}

		/*public void setModelSlot(DiagramModelSlot modelSlot) {
			super.setModelSlot(modelSlot);
		}*/
	}
}
