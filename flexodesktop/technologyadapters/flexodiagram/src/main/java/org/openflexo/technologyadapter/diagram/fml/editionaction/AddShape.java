/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.technologyadapter.diagram.fml.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.fml.DiagramEditionScheme;
import org.openflexo.technologyadapter.diagram.fml.DropScheme;
import org.openflexo.technologyadapter.diagram.fml.ShapePatternRole;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramContainerElement;
import org.openflexo.technologyadapter.diagram.model.DiagramFactory;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;
import org.openflexo.toolbox.StringUtils;

/**
 * This edition primitive addresses the creation of a new shape in a diagram
 * 
 * @author sylvain
 * 
 */
@FIBPanel("Fib/AddShapePanel.fib")
@ModelEntity
@ImplementationClass(AddShape.AddShapeImpl.class)
@XMLElement
public interface AddShape extends AddDiagramElementAction<DiagramShape> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String CONTAINER_KEY = "container";
	@PropertyIdentifier(type = boolean.class)
	public static final String EXTEND_PARENT_BOUNDS_TO_HOST_THIS_SHAPE_KEY = "extendParentBoundsToHostThisShape";

	@Getter(value = CONTAINER_KEY)
	@XMLAttribute
	public DataBinding<DiagramContainerElement<?>> getContainer();

	@Setter(CONTAINER_KEY)
	public void setContainer(DataBinding<DiagramContainerElement<?>> container);

	@Getter(value = EXTEND_PARENT_BOUNDS_TO_HOST_THIS_SHAPE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getExtendParentBoundsToHostThisShape();

	@Setter(EXTEND_PARENT_BOUNDS_TO_HOST_THIS_SHAPE_KEY)
	public void setExtendParentBoundsToHostThisShape(boolean extendParentBoundsToHostThisShape);

	@Override
	public ShapePatternRole getPatternRole();

	public static abstract class AddShapeImpl extends AddDiagramElementActionImpl<DiagramShape> implements AddShape {

		private static final Logger logger = Logger.getLogger(AddShape.class.getPackage().getName());

		private boolean extendParentBoundsToHostThisShape = false;

		public AddShapeImpl() {
			super();
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			if (getAssignation().isSet()) {
				out.append(getAssignation().toString() + " = (", context);
			}
			out.append(getClass().getSimpleName() + " conformTo ShapeSpecification from " + getModelSlot().getName() + " {"
					+ StringUtils.LINE_SEPARATOR, context);
			out.append(getGraphicalElementSpecificationFMLRepresentation(context), context);
			out.append("}", context);
			if (getAssignation().isSet()) {
				out.append(")", context);
			}
			return out.toString();
		}

		public DiagramContainerElement<?> getContainer(EditionSchemeAction action) {
			if (getPatternRole() != null && !getPatternRole().getParentShapeAsDefinedInAction()) {
				FlexoObject returned = action.getEditionPatternInstance().getPatternActor(getPatternRole().getParentShapePatternRole());
				return action.getEditionPatternInstance().getPatternActor(getPatternRole().getParentShapePatternRole());
			} else {
				try {
					return getContainer().getBindingValue(action);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				return null;
			}
		}

		@Override
		public ShapePatternRole getPatternRole() {
			PatternRole superPatternRole = super.getPatternRole();
			if (superPatternRole instanceof ShapePatternRole) {
				return (ShapePatternRole) superPatternRole;
			} else if (superPatternRole != null) {
				// logger.warning("Unexpected pattern role of type " + superPatternRole.getClass().getSimpleName());
				return null;
			}
			return null;
		}

		private DataBinding<DiagramContainerElement<?>> container;

		@Override
		public DataBinding<DiagramContainerElement<?>> getContainer() {
			if (container == null) {
				container = new DataBinding<DiagramContainerElement<?>>(this, DiagramContainerElement.class, BindingDefinitionType.GET);
				container.setBindingName("container");
			}
			return container;
		}

		@Override
		public void setContainer(DataBinding<DiagramContainerElement<?>> container) {
			if (container != null) {
				container.setOwner(this);
				container.setBindingName("container");
				container.setDeclaredType(DiagramContainerElement.class);
				container.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.container = container;
			notifiedBindingChanged(this.container);
		}

		@Override
		public boolean getExtendParentBoundsToHostThisShape() {
			return extendParentBoundsToHostThisShape;
		}

		@Override
		public void setExtendParentBoundsToHostThisShape(boolean extendParentBoundsToHostThisShape) {
			this.extendParentBoundsToHostThisShape = extendParentBoundsToHostThisShape;
		}

		@Override
		public Type getAssignableType() {
			return DiagramShape.class;
		}

		@Override
		public boolean isAssignationRequired() {
			return true;
		}

		@Override
		public DiagramShape performAction(EditionSchemeAction action) {
			DiagramContainerElement<?> container = getContainer(action);
			Diagram diagram = container.getDiagram();

			DiagramFactory factory = diagram.getDiagramFactory();
			DiagramShape newShape = factory.newInstance(DiagramShape.class);

			GraphicalRepresentation grToUse = null;

			// If an overriden graphical representation is defined, use it
			/*if (action.getOverridingGraphicalRepresentation(getPatternRole()) != null) {
				grToUse = action.getOverridingGraphicalRepresentation(getPatternRole());
			} else*/if (getPatternRole().getGraphicalRepresentation() != null) {
				grToUse = getPatternRole().getGraphicalRepresentation();
			}

			ShapeGraphicalRepresentation newGR = factory.makeShapeGraphicalRepresentation();
			newGR.setsWith(grToUse);
			newShape.setGraphicalRepresentation(newGR);

			// Register reference
			newShape.registerEditionPatternReference(action.getEditionPatternInstance());

			if (container == null) {
				logger.warning("When adding shape, cannot find container for action " + getPatternRole() + " container="
						+ getContainer(action) + " container=" + getContainer());
				return null;
			}

			container.addToShapes(newShape);

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Added shape " + newShape + " under " + container);
			}

			System.out.println("Added shape " + newShape);
			return newShape;
		}

		@Override
		public void finalizePerformAction(EditionSchemeAction action, DiagramShape newShape) {
			super.finalizePerformAction(action, newShape);
			// Be sure that the newly created shape is updated
			// newShape.update();
		}
	}

	public static class AddShapeActionMustAdressAValidShapePatternRole extends
			ValidationRule<AddShapeActionMustAdressAValidShapePatternRole, AddShape> {
		public AddShapeActionMustAdressAValidShapePatternRole() {
			super(AddShape.class, "add_shape_action_must_address_a_valid_shape_pattern_role");
		}

		@Override
		public ValidationIssue<AddShapeActionMustAdressAValidShapePatternRole, AddShape> applyValidation(AddShape action) {
			if (action.getPatternRole() == null) {
				Vector<FixProposal<AddShapeActionMustAdressAValidShapePatternRole, AddShape>> v = new Vector<FixProposal<AddShapeActionMustAdressAValidShapePatternRole, AddShape>>();
				for (ShapePatternRole pr : action.getEditionPattern().getPatternRoles(ShapePatternRole.class)) {
					v.add(new SetsPatternRole(pr));
				}
				return new ValidationError<AddShapeActionMustAdressAValidShapePatternRole, AddShape>(this, action,
						"add_shape_action_does_not_address_a_valid_shape_pattern_role", v);
			}
			return null;
		}

		protected static class SetsPatternRole extends FixProposal<AddShapeActionMustAdressAValidShapePatternRole, AddShape> {

			private final ShapePatternRole patternRole;

			public SetsPatternRole(ShapePatternRole patternRole) {
				super("assign_action_to_pattern_role_($patternRole.patternRoleName)");
				this.patternRole = patternRole;
			}

			public ShapePatternRole getPatternRole() {
				return patternRole;
			}

			@Override
			protected void fixAction() {
				AddShape action = getObject();
				action.setAssignation(new DataBinding<Object>(patternRole.getPatternRoleName()));
			}

		}
	}

	public static class AddShapeActionMustHaveAValidContainer extends ValidationRule<AddShapeActionMustHaveAValidContainer, AddShape> {
		public AddShapeActionMustHaveAValidContainer() {
			super(AddShape.class, "add_shape_action_must_have_a_valid_container");
		}

		@Override
		public ValidationIssue<AddShapeActionMustHaveAValidContainer, AddShape> applyValidation(AddShape action) {
			if (action.getPatternRole() != null && action.getPatternRole().getParentShapeAsDefinedInAction()
					&& !(action.getContainer().isSet() && action.getContainer().isValid())) {
				Vector<FixProposal<AddShapeActionMustHaveAValidContainer, AddShape>> v = new Vector<FixProposal<AddShapeActionMustHaveAValidContainer, AddShape>>();
				if (action.getEditionScheme() instanceof DropScheme) {
					EditionPattern targetEditionPattern = ((DropScheme) action.getEditionScheme()).getTargetEditionPattern();
					if (targetEditionPattern != null) {
						for (ShapePatternRole pr : action.getEditionPattern().getPatternRoles(ShapePatternRole.class)) {
							v.add(new SetsContainerToTargetShape(targetEditionPattern, pr));
						}
					}
				}
				v.add(new SetsContainerToTopLevel());
				for (ShapePatternRole pr : action.getEditionPattern().getPatternRoles(ShapePatternRole.class)) {
					v.add(new SetsContainerToShape(pr));
				}
				return new ValidationError<AddShapeActionMustHaveAValidContainer, AddShape>(this, action,
						"add_shape_action_does_not_have_a_valid_container", v);
			}
			return null;
		}

		protected static class SetsContainerToTopLevel extends FixProposal<AddShapeActionMustHaveAValidContainer, AddShape> {

			public SetsContainerToTopLevel() {
				super("sets_container_to_top_level");
			}

			@Override
			protected void fixAction() {
				AddShape action = getObject();
				action.setContainer(new DataBinding<DiagramContainerElement<?>>(DiagramEditionScheme.TOP_LEVEL));
			}

		}

		protected static class SetsContainerToShape extends FixProposal<AddShapeActionMustHaveAValidContainer, AddShape> {

			private final ShapePatternRole patternRole;

			public SetsContainerToShape(ShapePatternRole patternRole) {
				super("sets_container_to_($patternRole.patternRoleName)");
				this.patternRole = patternRole;
			}

			public ShapePatternRole getPatternRole() {
				return patternRole;
			}

			@Override
			protected void fixAction() {
				AddShape action = getObject();
				action.setContainer(new DataBinding<DiagramContainerElement<?>>(patternRole.getPatternRoleName()));
			}
		}

		protected static class SetsContainerToTargetShape extends FixProposal<AddShapeActionMustHaveAValidContainer, AddShape> {

			private final EditionPattern target;
			private final ShapePatternRole patternRole;

			public SetsContainerToTargetShape(EditionPattern target, ShapePatternRole patternRole) {
				super("sets_container_to_target.($patternRole.patternRoleName)");
				this.target = target;
				this.patternRole = patternRole;
			}

			public ShapePatternRole getPatternRole() {
				return patternRole;
			}

			public EditionPattern getTarget() {
				return target;
			}

			@Override
			protected void fixAction() {
				AddShape action = getObject();
				action.setContainer(new DataBinding<DiagramContainerElement<?>>(DiagramEditionScheme.TARGET + "."
						+ patternRole.getPatternRoleName()));
			}
		}

	}
}
