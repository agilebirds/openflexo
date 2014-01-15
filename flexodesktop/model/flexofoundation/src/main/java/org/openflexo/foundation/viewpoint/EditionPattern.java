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
package org.openflexo.foundation.viewpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.binding.PatternRoleBindingVariable;
import org.openflexo.foundation.viewpoint.inspector.EditionPatternInspector;
import org.openflexo.foundation.viewpoint.inspector.EditionPatternInspector.EditionPatternInspectorImpl;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.DeserializationFinalizer;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.ChainedCollection;
import org.openflexo.toolbox.StringUtils;

/**
 * An EditionPattern aggregates modelling elements from different modelling element resources (models, metamodels, graphical representation,
 * GUI, etcâ¦). Each such element is associated with a {@link PatternRole}.
 * 
 * A PatternRole is an abstraction of the manipulation roles played in the {@link EditionPattern} by modelling element potentially in
 * different metamodels.
 * 
 * An {@link EditionPatternInstance} is an instance of an {@link EditionPattern}.
 * 
 * Instances of modelling elements in an {@link EditionPatternInstance} are called Pattern Actors. They play given Pattern Roles.
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(EditionPattern.EditionPatternImpl.class)
@XMLElement
public interface EditionPattern extends EditionPatternObject {

	@PropertyIdentifier(type = VirtualModel.class)
	public static final String VIRTUAL_MODEL_KEY = "virtualModel";
	@PropertyIdentifier(type = String.class)
	public static final String NAME_KEY = "name";
	@PropertyIdentifier(type = String.class)
	public static final String DESCRIPTION_KEY = "description";
	@PropertyIdentifier(type = Vector.class)
	public static final String EDITION_SCHEMES_KEY = "editionSchemes";
	@PropertyIdentifier(type = Vector.class)
	public static final String PATTERN_ROLES_KEY = "patternRoles";
	@PropertyIdentifier(type = EditionPatternInspector.class)
	public static final String INSPECTOR_KEY = "inspector";
	@PropertyIdentifier(type = List.class)
	public static final String PARENT_EDITION_PATTERNS_KEY = "parentEditionPatterns";
	@PropertyIdentifier(type = List.class)
	public static final String CHILD_EDITION_PATTERNS_KEY = "childEditionPatterns";
	@PropertyIdentifier(type = List.class)
	public static final String EDITION_PATTERN_CONSTRAINTS_KEY = "editionPatternConstraints";

	@Override
	@Getter(value = VIRTUAL_MODEL_KEY, inverse = VirtualModel.EDITION_PATTERNS_KEY)
	public VirtualModel getVirtualModel();

	@Setter(VIRTUAL_MODEL_KEY)
	public void setVirtualModel(VirtualModel virtualModel);

	@Override
	@Getter(value = NAME_KEY)
	@XMLAttribute
	public String getName();

	@Override
	@Setter(NAME_KEY)
	public void setName(String name);

	@Override
	@Getter(value = DESCRIPTION_KEY)
	@XMLElement
	public String getDescription();

	@Override
	@Setter(DESCRIPTION_KEY)
	public void setDescription(String description);

	@Getter(value = EDITION_SCHEMES_KEY, cardinality = Cardinality.LIST, inverse = EditionScheme.EDITION_PATTERN_KEY)
	@XMLElement
	public List<EditionScheme> getEditionSchemes();

	@Setter(EDITION_SCHEMES_KEY)
	public void setEditionSchemes(List<EditionScheme> editionSchemes);

	@Adder(EDITION_SCHEMES_KEY)
	public void addToEditionSchemes(EditionScheme aEditionScheme);

	@Remover(EDITION_SCHEMES_KEY)
	public void removeFromEditionSchemes(EditionScheme aEditionScheme);

	@Finder(collection = EDITION_SCHEMES_KEY, attribute = EditionScheme.NAME_KEY)
	public EditionScheme getEditionScheme(String editionSchemeName);

	@Getter(value = PATTERN_ROLES_KEY, cardinality = Cardinality.LIST, inverse = PatternRole.EDITION_PATTERN_KEY)
	@XMLElement
	public List<PatternRole<?>> getPatternRoles();

	@Setter(PATTERN_ROLES_KEY)
	public void setPatternRoles(List<PatternRole<?>> patternRoles);

	@Adder(PATTERN_ROLES_KEY)
	public void addToPatternRoles(PatternRole<?> aPatternRole);

	@Remover(PATTERN_ROLES_KEY)
	public void removeFromPatternRoles(PatternRole<?> aPatternRole);

	@Finder(collection = PATTERN_ROLES_KEY, attribute = PatternRole.NAME_KEY)
	public PatternRole<?> getPatternRole(String patternRoleName);

	@Getter(value = INSPECTOR_KEY, inverse = EditionPatternInspector.EDITION_PATTERN_KEY)
	@XMLElement(xmlTag = "Inspector")
	public EditionPatternInspector getInspector();

	@Setter(INSPECTOR_KEY)
	public void setInspector(EditionPatternInspector inspector);

	@Getter(value = PARENT_EDITION_PATTERNS_KEY, cardinality = Cardinality.LIST, inverse = CHILD_EDITION_PATTERNS_KEY)
	@XMLElement(context = "Parent")
	public List<EditionPattern> getParentEditionPatterns();

	@Setter(PARENT_EDITION_PATTERNS_KEY)
	public void setParentEditionPatterns(List<EditionPattern> parentEditionPatterns);

	@Adder(PARENT_EDITION_PATTERNS_KEY)
	public void addToParentEditionPatterns(EditionPattern parentEditionPattern);

	@Remover(PARENT_EDITION_PATTERNS_KEY)
	public void removeFromParentEditionPatterns(EditionPattern parentEditionPattern);

	@Getter(value = CHILD_EDITION_PATTERNS_KEY, cardinality = Cardinality.LIST, inverse = CHILD_EDITION_PATTERNS_KEY)
	@XMLElement(context = "Child")
	public List<EditionPattern> getChildEditionPatterns();

	@Setter(CHILD_EDITION_PATTERNS_KEY)
	public void setChildEditionPatterns(List<EditionPattern> childEditionPatterns);

	@Adder(CHILD_EDITION_PATTERNS_KEY)
	public void addToChildEditionPatterns(EditionPattern childEditionPattern);

	@Remover(CHILD_EDITION_PATTERNS_KEY)
	public void removeFromChildEditionPatterns(EditionPattern childEditionPattern);

	@Getter(value = EDITION_PATTERN_CONSTRAINTS_KEY, cardinality = Cardinality.LIST, inverse = EditionPatternConstraint.EDITION_PATTERN_KEY)
	@XMLElement
	public List<EditionPatternConstraint> getEditionPatternConstraints();

	@Setter(EDITION_PATTERN_CONSTRAINTS_KEY)
	public void setEditionPatternConstraints(List<EditionPatternConstraint> editionPatternConstraints);

	@Adder(EDITION_PATTERN_CONSTRAINTS_KEY)
	public void addToEditionPatternConstraints(EditionPatternConstraint aEditionPatternConstraint);

	@Remover(EDITION_PATTERN_CONSTRAINTS_KEY)
	public void removeFromEditionPatternConstraints(EditionPatternConstraint aEditionPatternConstraint);

	@DeserializationFinalizer
	public void finalizeEditionPatternDeserialization();

	public boolean isRoot();

	public <ES extends EditionScheme> List<ES> getEditionSchemes(Class<ES> editionSchemeClass);

	public List<AbstractActionScheme> getAbstractActionSchemes();

	public List<ActionScheme> getActionSchemes();

	/**
	 * Only one synchronization scheme is allowed
	 * 
	 * @return
	 */
	public SynchronizationScheme getSynchronizationScheme();

	public List<DeletionScheme> getDeletionSchemes();

	public List<NavigationScheme> getNavigationSchemes();

	public List<AbstractCreationScheme> getAbstractCreationSchemes();

	public List<CreationScheme> getCreationSchemes();

	public boolean hasActionScheme();

	public boolean hasCreationScheme();

	public boolean hasSynchronizationScheme();

	public boolean hasNavigationScheme();

	public CreationScheme createCreationScheme();

	public CloningScheme createCloningScheme();

	public ActionScheme createActionScheme();

	public NavigationScheme createNavigationScheme();

	public DeletionScheme createDeletionScheme();

	public EditionScheme deleteEditionScheme(EditionScheme editionScheme);

	public DeletionScheme getDefaultDeletionScheme();

	public DeletionScheme generateDefaultDeletionScheme();

	public List<IndividualPatternRole> getIndividualPatternRoles();

	public List<ClassPatternRole> getClassPatternRoles();

	public EditionPatternInstanceType getInstanceType();

	public EditionPatternStructuralFacet getStructuralFacet();

	public EditionPatternBehaviouralFacet getBehaviouralFacet();

	public boolean isAssignableFrom(EditionPattern editionPattern);

	public String getAvailableRoleName(String baseName);

	/**
	 * Duplicates this EditionPattern, given a new name<br>
	 * Newly created EditionPattern is added to ViewPoint
	 * 
	 * @param newName
	 * @return
	 */
	public EditionPattern duplicate(String newName);

	public static abstract class EditionPatternImpl extends EditionPatternObjectImpl implements EditionPattern {

		protected static final Logger logger = FlexoLogger.getLogger(EditionPattern.class.getPackage().getName());

		// private List<PatternRole<?>> patternRoles;
		// private List<EditionScheme> editionSchemes;
		// private List<EditionPatternConstraint> editionPatternConstraints;
		private EditionPatternInspector inspector;

		// private OntologicObjectPatternRole primaryConceptRole;
		// private GraphicalElementPatternRole primaryRepresentationRole;

		private VirtualModel virtualModel;

		private final EditionPattern parentEditionPattern = null;
		// private final Vector<EditionPattern> childEditionPatterns = new Vector<EditionPattern>();

		private final EditionPatternStructuralFacet structuralFacet;
		private final EditionPatternBehaviouralFacet behaviouralFacet;

		private final EditionPatternInstanceType instanceType = new EditionPatternInstanceType(this);

		/**
		 * Stores a chained collections of objects which are involved in validation
		 */
		private final ChainedCollection<ViewPointObject> validableObjects = null;

		public EditionPatternImpl() {
			super();
			/*if (builder != null) {
				virtualModel = builder.getVirtualModel();
			}*/
			// patternRoles = new ArrayList<PatternRole<?>>();
			// editionSchemes = new Vector<EditionScheme>();
			// editionPatternConstraints = new Vector<EditionPatternConstraint>();
			structuralFacet = getVirtualModelFactory().newEditionPatternStructuralFacet(this);
			behaviouralFacet = getVirtualModelFactory().newEditionPatternBehaviouralFacet(this);
		}

		@Override
		public EditionPatternInstanceType getInstanceType() {
			return instanceType;
		}

		@Override
		public EditionPatternStructuralFacet getStructuralFacet() {
			return structuralFacet;
		}

		@Override
		public EditionPatternBehaviouralFacet getBehaviouralFacet() {
			return behaviouralFacet;
		}

		@Override
		public EditionPatternImpl getEditionPattern() {
			return this;
		}

		@Override
		public boolean delete() {
			if (getVirtualModel() != null) {
				getVirtualModel().removeFromEditionPatterns(this);
			}
			super.delete();
			deleteObservers();
			return true;
		}

		@Override
		public String getStringRepresentation() {
			return (getVirtualModel() != null ? getVirtualModel().getStringRepresentation() : "null") + "#" + getName();
		}

		/**
		 * Return the URI of the {@link EditionPattern}<br>
		 * The convention for URI are following: <viewpoint_uri>/<virtual_model_name>#<edition_pattern_name>.<edition_scheme_name> <br>
		 * eg<br>
		 * http://www.mydomain.org/MyViewPoint/MyVirtualModel#MyEditionPattern.MyEditionScheme
		 * 
		 * @return String representing unique URI of this object
		 */
		@Override
		public String getURI() {
			return getVirtualModel().getURI() + "#" + getName();
		}

		@Override
		public void setName(String name) {
			if (name != null) {
				// We prevent ',' so that we can use it as a delimiter in tags.
				super.setName(name.replace(",", ""));
			}
		}

		/*@Override
		public EditionScheme getEditionScheme(String editionSchemeName) {
			for (EditionScheme es : editionSchemes) {
				if (es.getName().equals(editionSchemeName)) {
					return es;
				}
			}
			logger.warning("Not found EditionScheme:" + editionSchemeName);
			return null;
		}*/

		/*@Override
		public List<EditionScheme> getEditionSchemes() {
			return editionSchemes;
		}

		public void setEditionSchemes(Vector<EditionScheme> someEditionScheme) {
			editionSchemes = someEditionScheme;
		}

		@Override
		public void addToEditionSchemes(EditionScheme anEditionScheme) {
			anEditionScheme.setEditionPattern(this);
			editionSchemes.add(anEditionScheme);
			if (getVirtualModel() != null) {
				getVirtualModel().notifyEditionSchemeModified();
			}
			setChanged();
			notifyObservers(new EditionSchemeInserted(anEditionScheme, this));
			anEditionScheme.updateBindingModels();
		}

		@Override
		public void removeFromEditionSchemes(EditionScheme anEditionScheme) {
			anEditionScheme.setEditionPattern(null);
			editionSchemes.remove(anEditionScheme);
			if (getVirtualModel() != null) {
				getVirtualModel().notifyEditionSchemeModified();
			}
			setChanged();
			notifyObservers(new EditionSchemeRemoved(anEditionScheme, this));
		}*/

		/*@Override
		public Vector<EditionPatternConstraint> getEditionPatternConstraints() {
			return editionPatternConstraints;
		}

		public void setEditionPatternConstraints(Vector<EditionPatternConstraint> someEditionPatternConstraint) {
			editionPatternConstraints = someEditionPatternConstraint;
		}

		@Override
		public void addToEditionPatternConstraints(EditionPatternConstraint anEditionPatternConstraint) {
			anEditionPatternConstraint.setEditionPattern(this);
			editionPatternConstraints.add(anEditionPatternConstraint);
			setChanged();
			notifyObservers(new EditionPatternConstraintInserted(anEditionPatternConstraint, this));
		}

		@Override
		public void removeFromEditionPatternConstraints(EditionPatternConstraint anEditionPatternConstraint) {
			anEditionPatternConstraint.setEditionPattern(null);
			editionPatternConstraints.remove(anEditionPatternConstraint);
			setChanged();
			notifyObservers(new EditionPatternConstraintRemoved(anEditionPatternConstraint, this));
		}*/

		public void createConstraint() {
			EditionPatternConstraint constraint = getVirtualModelFactory().newEditionPatternConstraint();
			addToEditionPatternConstraints(constraint);
		}

		public void deleteConstraint(EditionPatternConstraint constraint) {
			removeFromEditionPatternConstraints(constraint);
		}

		/*@Override
		public List<PatternRole<?>> getPatternRoles() {
			return patternRoles;
		}*/

		@Override
		public void setPatternRoles(List<PatternRole<?>> somePatternRole) {
			// patternRoles = somePatternRole;
			performSuperSetter(PATTERN_ROLES_KEY, somePatternRole);
			availablePatternRoleNames = null;
		}

		@Override
		public void addToPatternRoles(PatternRole<?> aPatternRole) {
			availablePatternRoleNames = null;
			performSuperAdder(PATTERN_ROLES_KEY, aPatternRole);
			if (_bindingModel != null) {
				updateBindingModel();
			}
		}

		@Override
		public void removeFromPatternRoles(PatternRole aPatternRole) {
			availablePatternRoleNames = null;
			performSuperRemover(PATTERN_ROLES_KEY, aPatternRole);
			if (_bindingModel != null) {
				updateBindingModel();
			}
		}

		public <R> List<R> getPatternRoles(Class<R> type) {
			List<R> returned = new ArrayList<R>();
			for (PatternRole<?> r : getPatternRoles()) {
				if (TypeUtils.isTypeAssignableFrom(type, r.getClass())) {
					returned.add((R) r);
				}
			}
			return returned;
		}

		@Override
		public List<IndividualPatternRole> getIndividualPatternRoles() {
			return getPatternRoles(IndividualPatternRole.class);
		}

		@Override
		public List<ClassPatternRole> getClassPatternRoles() {
			return getPatternRoles(ClassPatternRole.class);
		}

		/*public List<GraphicalElementPatternRole> getGraphicalElementPatternRoles() {
			return getPatternRoles(GraphicalElementPatternRole.class);
		}

		public List<ShapePatternRole> getShapePatternRoles() {
			return getPatternRoles(ShapePatternRole.class);
		}

		public List<ConnectorPatternRole> getConnectorPatternRoles() {
			return getPatternRoles(ConnectorPatternRole.class);
		}*/

		/*public ShapePatternRole getDefaultShapePatternRole() {
			List<ShapePatternRole> l = getShapePatternRoles();
			if (l.size() > 0) {
				return l.get(0);
			}
			return null;
		}

		public ConnectorPatternRole getDefaultConnectorPatternRole() {
			List<ConnectorPatternRole> l = getConnectorPatternRoles();
			if (l.size() > 0) {
				return l.get(0);
			}
			return null;
		}*/

		private Vector<String> availablePatternRoleNames = null;

		public Vector<String> getAvailablePatternRoleNames() {
			if (availablePatternRoleNames == null) {
				availablePatternRoleNames = new Vector<String>();
				for (PatternRole r : getPatternRoles()) {
					availablePatternRoleNames.add(r.getName());
				}
			}
			return availablePatternRoleNames;
		}

		@Override
		public String getAvailableRoleName(String baseName) {
			String testName = baseName;
			int index = 2;
			while (getPatternRole(testName) != null) {
				testName = baseName + index;
				index++;
			}
			return testName;
		}

		public PatternRole<?> deletePatternRole(PatternRole<?> aPatternRole) {
			removeFromPatternRoles(aPatternRole);
			aPatternRole.delete();
			return aPatternRole;
		}

		/*public PatternRole<?> getPatternRole(String patternRole) {
			for (PatternRole pr : patternRoles) {
				if (pr.getPatternRoleName() != null && pr.getPatternRoleName().equals(patternRole)) {
					return pr;
				}
			}
			return null;
		}*/

		@Override
		@SuppressWarnings("unchecked")
		public <ES extends EditionScheme> List<ES> getEditionSchemes(Class<ES> editionSchemeClass) {
			List<ES> returned = new ArrayList<ES>();
			for (EditionScheme es : getEditionSchemes()) {
				if (editionSchemeClass.isAssignableFrom(es.getClass())) {
					returned.add((ES) es);
				}
			}
			return returned;
		}

		@Override
		public List<AbstractActionScheme> getAbstractActionSchemes() {
			return getEditionSchemes(AbstractActionScheme.class);
		}

		@Override
		public List<ActionScheme> getActionSchemes() {
			return getEditionSchemes(ActionScheme.class);
		}

		/**
		 * Only one synchronization scheme is allowed
		 * 
		 * @return
		 */
		@Override
		public SynchronizationScheme getSynchronizationScheme() {
			for (EditionScheme es : getEditionSchemes()) {
				if (es instanceof SynchronizationScheme) {
					return (SynchronizationScheme) es;
				}
			}
			return null;
		}

		@Override
		public List<DeletionScheme> getDeletionSchemes() {
			return getEditionSchemes(DeletionScheme.class);
		}

		@Override
		public List<NavigationScheme> getNavigationSchemes() {
			return getEditionSchemes(NavigationScheme.class);
		}

		@Override
		public List<AbstractCreationScheme> getAbstractCreationSchemes() {
			return getEditionSchemes(AbstractCreationScheme.class);
		}

		@Override
		public List<CreationScheme> getCreationSchemes() {
			return getEditionSchemes(CreationScheme.class);
		}

		@Override
		public boolean hasActionScheme() {
			for (EditionScheme es : getEditionSchemes()) {
				if (es instanceof ActionScheme) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean hasCreationScheme() {
			for (EditionScheme es : getEditionSchemes()) {
				if (es instanceof CreationScheme) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean hasSynchronizationScheme() {
			for (EditionScheme es : getEditionSchemes()) {
				if (es instanceof SynchronizationScheme) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean hasNavigationScheme() {
			for (EditionScheme es : getEditionSchemes()) {
				if (es instanceof NavigationScheme) {
					return true;
				}
			}
			return false;
		}

		@Override
		public CreationScheme createCreationScheme() {
			CreationScheme newCreationScheme = getVirtualModelFactory().newCreationScheme();
			newCreationScheme.setEditionPattern(this);
			newCreationScheme.setName("creation");
			addToEditionSchemes(newCreationScheme);
			return newCreationScheme;
		}

		@Override
		public CloningScheme createCloningScheme() {
			CloningScheme newCloningScheme = getVirtualModelFactory().newCloningScheme();
			newCloningScheme.setEditionPattern(this);
			newCloningScheme.setName("clone");
			addToEditionSchemes(newCloningScheme);
			return newCloningScheme;
		}

		@Override
		public ActionScheme createActionScheme() {
			ActionScheme newActionScheme = getVirtualModelFactory().newActionScheme();
			newActionScheme.setEditionPattern(this);
			newActionScheme.setName("action");
			addToEditionSchemes(newActionScheme);
			return newActionScheme;
		}

		@Override
		public NavigationScheme createNavigationScheme() {
			NavigationScheme newNavigationScheme = getVirtualModelFactory().newNavigationScheme();
			newNavigationScheme.setEditionPattern(this);
			newNavigationScheme.setName("navigation");
			addToEditionSchemes(newNavigationScheme);
			return newNavigationScheme;
		}

		@Override
		public DeletionScheme createDeletionScheme() {
			DeletionScheme newDeletionScheme = generateDefaultDeletionScheme();
			return newDeletionScheme;
		}

		@Override
		public EditionScheme deleteEditionScheme(EditionScheme editionScheme) {
			removeFromEditionSchemes(editionScheme);
			editionScheme.delete();
			return editionScheme;
		}

		@Override
		public DeletionScheme getDefaultDeletionScheme() {
			if (getDeletionSchemes().size() > 0) {
				return getDeletionSchemes().get(0);
			}
			return null;
		}

		@Override
		public DeletionScheme generateDefaultDeletionScheme() {
			DeletionScheme newDeletionScheme = getVirtualModelFactory().newDeletionScheme();
			newDeletionScheme.setName("deletion");
			Vector<PatternRole> rolesToDelete = new Vector<PatternRole>();
			for (PatternRole pr : getPatternRoles()) {
				if (/*pr instanceof GraphicalElementPatternRole ||*/pr instanceof IndividualPatternRole /*|| pr instanceof StatementPatternRole*/) {
					rolesToDelete.add(pr);
				}
			}
			Collections.sort(rolesToDelete, new Comparator<PatternRole>() {
				@Override
				public int compare(PatternRole o1, PatternRole o2) {
					/*if (o1 instanceof ShapePatternRole && o2 instanceof ConnectorPatternRole) {
						return 1;
					} else if (o1 instanceof ConnectorPatternRole && o2 instanceof ShapePatternRole) {
						return -1;
					}*/

					/*if (o1 instanceof ShapePatternRole) {
						if (o2 instanceof ShapePatternRole) {
							if (((ShapePatternRole) o1).isEmbeddedIn((ShapePatternRole) o2)) {
								return -1;
							}
							if (((ShapePatternRole) o2).isEmbeddedIn((ShapePatternRole) o1)) {
								return 1;
							}
							return 0;
						}
					}*/
					return 0;
				}

			});
			for (PatternRole pr : rolesToDelete) {
				DeleteAction a = getVirtualModelFactory().newDeleteAction();
				a.setObject(new DataBinding<Object>(pr.getPatternRoleName()));
				newDeletionScheme.addToActions(a);
			}
			addToEditionSchemes(newDeletionScheme);
			return newDeletionScheme;
		}

		@Override
		public EditionPatternInspector getInspector() {
			if (inspector == null) {
				inspector = EditionPatternInspectorImpl.makeEditionPatternInspector(this);
			}
			return inspector;
		}

		@Override
		public void setInspector(EditionPatternInspector inspector) {
			inspector.setEditionPattern(this);
			this.inspector = inspector;
		}

		@Override
		public VirtualModel getVirtualModel() {
			return virtualModel;
		}

		@Override
		public void setVirtualModel(VirtualModel virtualModel) {
			this.virtualModel = virtualModel;
		}

		@Override
		public String toString() {
			return "EditionPattern:" + getName();
		}

		@Override
		public void finalizeEditionPatternDeserialization() {
			createBindingModel();
			for (EditionScheme es : getEditionSchemes()) {
				es.finalizeEditionSchemeDeserialization();
			}
			for (PatternRole pr : getPatternRoles()) {
				pr.finalizePatternRoleDeserialization();
			}
			updateBindingModel();
		}

		/*public void finalizeParentEditionPatternDeserialization() {
			if (StringUtils.isNotEmpty(parentEditionPatternURI)) {
				setParentEditionPattern(getParentEditionPattern());
			}
		}*/

		public void debug() {
			System.out.println(getStringRepresentation());
		}

		@Deprecated
		public void save() {
			try {
				getVirtualModel().getResource().save(null);
			} catch (SaveResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private BindingModel _bindingModel;

		@Override
		public BindingModel getBindingModel() {
			if (_bindingModel == null) {
				createBindingModel();
			}
			return _bindingModel;
		}

		public void updateBindingModel() {
			logger.fine("updateBindingModel()");
			_bindingModel = null;
			createBindingModel();
			for (EditionScheme es : getEditionSchemes()) {
				es.updateBindingModels();
			}
		}

		private void createBindingModel() {
			_bindingModel = new BindingModel();
			for (PatternRole role : getPatternRoles()) {
				_bindingModel.addToBindingVariables(new PatternRoleBindingVariable(role));
			}
			notifyBindingModelChanged();
		}

		@Override
		public void notifyBindingModelChanged() {
			super.notifyBindingModelChanged();
			// SGU: as all pattern roles share the edition pattern binding model, they should
			// all notify change of their binding models
			for (PatternRole pr : getPatternRoles()) {
				pr.notifyBindingModelChanged();
			}
			getInspector().notifyBindingModelChanged();
		}

		/*public OntologicObjectPatternRole getDefaultPrimaryConceptRole() {
			List<OntologicObjectPatternRole> roles = getPatternRoles(OntologicObjectPatternRole.class);
			if (roles.size() > 0) {
				return roles.get(0);
			}
			return null;
		}*/

		/*public GraphicalElementPatternRole getDefaultPrimaryRepresentationRole() {
			List<GraphicalElementPatternRole> roles = getPatternRoles(GraphicalElementPatternRole.class);
			if (roles.size() > 0) {
				return roles.get(0);
			}
			return null;
		}*/

		/*public OntologicObjectPatternRole getPrimaryConceptRole() {
			if (primaryConceptRole == null) {
				return getDefaultPrimaryConceptRole();
			}
			return primaryConceptRole;
		}

		public void setPrimaryConceptRole(OntologicObjectPatternRole primaryConceptRole) {
			this.primaryConceptRole = primaryConceptRole;
		}

		public GraphicalElementPatternRole getPrimaryRepresentationRole() {
			if (primaryRepresentationRole == null) {
				return getDefaultPrimaryRepresentationRole();
			}
			return primaryRepresentationRole;
		}

		public void setPrimaryRepresentationRole(GraphicalElementPatternRole primaryRepresentationRole) {
			this.primaryRepresentationRole = primaryRepresentationRole;
		}*/

		/*@Override
		public String simpleRepresentation() {
			return "EditionPattern:" + FlexoLocalization.localizedForKey(getLocalizedDictionary(), getName());
		}

		@Override
		public String fullQualifiedRepresentation() {
			return simpleRepresentation();
		}

		@Override
		public Class getBaseClass() {
			return EditionPattern.class;
		}

		@Override
		public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
			if (aType instanceof EditionPattern) {
				return isAssignableFrom((EditionPattern) aType);
			}
			return aType == this;
		}*/

		/**
		 * Duplicates this EditionPattern, given a new name<br>
		 * Newly created EditionPattern is added to ViewPoint
		 * 
		 * @param newName
		 * @return
		 */
		@Override
		public EditionPattern duplicate(String newName) {
			EditionPattern newEditionPattern = (EditionPattern) cloneObject();
			newEditionPattern.setName(newName);
			getVirtualModel().addToEditionPatterns(newEditionPattern);
			return newEditionPattern;
		}

		@Override
		public boolean isRoot() {
			return getParentEditionPatterns().size() == 0;
		}

		/*private String parentEditionPatternURI;

		@Override
		public String getParentEditionPatternURI() {
			if (getParentEditionPattern() != null) {
				return getParentEditionPattern().getURI();
			}
			return parentEditionPatternURI;
		}

		@Override
		public void _setParentEditionPatternURI(String aParentEditionPatternURI) {
			parentEditionPatternURI = aParentEditionPatternURI;
		}

		@Override
		public EditionPattern getParentEditionPattern() {
			if (parentEditionPattern == null && StringUtils.isNotEmpty(parentEditionPatternURI)) {
				if (getVirtualModel() != null) {
					setParentEditionPattern(getVirtualModel().getEditionPattern(parentEditionPatternURI));
				}
			}
			return parentEditionPattern;
		}

		public void setParentEditionPattern(EditionPattern aParentEP) {
			if (this.parentEditionPattern != aParentEP) {
				if (aParentEP == null) {
					this.parentEditionPattern.childEditionPatterns.remove(this);
					this.parentEditionPattern = aParentEP;
					if (this.parentEditionPattern != null) {
						this.parentEditionPattern.setChanged();
						this.parentEditionPattern.notifyObservers(new EditionPatternHierarchyChanged(this));
						this.parentEditionPattern.notifyChange("childEditionPatterns", null, getChildEditionPatterns());
					}
				} else {
					aParentEP.childEditionPatterns.add(this);
					this.parentEditionPattern = aParentEP;
					aParentEP.setChanged();
					aParentEP.notifyObservers(new EditionPatternHierarchyChanged(this));
					aParentEP.notifyChange("childEditionPatterns", null, getChildEditionPatterns());
				}
				if (getVirtualModel() != null) {
					getVirtualModel().setChanged();
					getVirtualModel().notifyObservers(new EditionPatternHierarchyChanged(this));
					getVirtualModel().notifyChange("allRootEditionPatterns", null, getVirtualModel().getAllRootEditionPatterns());
				}
			}
		}*/

		@Override
		public void setParentEditionPatterns(List<EditionPattern> parentEditionPatterns) {
			performSuperSetter(PARENT_EDITION_PATTERNS_KEY, parentEditionPatterns);
			/*if (getVirtualModel() != null) {
				getVirtualModel().setChanged();
				getVirtualModel().notifyObservers(new EditionPatternHierarchyChanged(this));
				getVirtualModel().notifyChange("allRootEditionPatterns", null, getVirtualModel().getAllRootEditionPatterns());
			}*/
		}

		@Override
		public void addToParentEditionPatterns(EditionPattern parentEditionPattern) {
			performSuperAdder(PARENT_EDITION_PATTERNS_KEY, parentEditionPattern);
			/*parentEditionPattern.setChanged();
			parentEditionPattern.notifyObservers(new EditionPatternHierarchyChanged(this));
			parentEditionPattern.notifyChange("childEditionPatterns", null, getChildEditionPatterns());*/
		}

		@Override
		public void removeFromParentEditionPatterns(EditionPattern parentEditionPattern) {
			performSuperRemover(PARENT_EDITION_PATTERNS_KEY, parentEditionPattern);
			/*parentEditionPattern.setChanged();
			parentEditionPattern.notifyObservers(new EditionPatternHierarchyChanged(this));
			parentEditionPattern.notifyChange("childEditionPatterns", null, getChildEditionPatterns());*/
		}

		/*@Override
		public Vector<EditionPattern> getChildEditionPatterns() {
			return childEditionPatterns;
		}*/

		@Override
		public boolean isAssignableFrom(EditionPattern editionPattern) {
			if (editionPattern == this) {
				return true;
			}
			for (EditionPattern parent : editionPattern.getParentEditionPatterns()) {
				if (isAssignableFrom(parent)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			// Voir du cote de GeneratorFormatter pour formatter tout ca

			FMLRepresentationOutput out = new FMLRepresentationOutput(context);
			out.append("EditionPattern " + getName(), context);
			if (getParentEditionPatterns().size() > 0) {
				out.append(" extends ", context);
				for (EditionPattern parent : getParentEditionPatterns()) {
					out.append(parent.getName() + ",", context);
				}

			}
			out.append(" {" + StringUtils.LINE_SEPARATOR, context);

			if (getPatternRoles().size() > 0) {
				out.append(StringUtils.LINE_SEPARATOR, context);
				for (PatternRole pr : getPatternRoles()) {
					out.append(pr.getFMLRepresentation(context), context, 1);
					out.append(StringUtils.LINE_SEPARATOR, context);
				}
			}

			if (getEditionSchemes().size() > 0) {
				out.append(StringUtils.LINE_SEPARATOR, context);
				for (EditionScheme es : getEditionSchemes()) {
					out.append(es.getFMLRepresentation(context), context, 1);
					out.append(StringUtils.LINE_SEPARATOR, context);
				}
			}

			out.append("}" + StringUtils.LINE_SEPARATOR, context);
			return out.toString();
		}

	}

	public static class EditionPatternShouldHaveRoles extends ValidationRule<EditionPatternShouldHaveRoles, EditionPattern> {
		public EditionPatternShouldHaveRoles() {
			super(EditionPattern.class, "edition_pattern_should_have_roles");
		}

		@Override
		public ValidationIssue<EditionPatternShouldHaveRoles, EditionPattern> applyValidation(EditionPattern editionPattern) {
			if (!(editionPattern instanceof VirtualModel) && editionPattern.getPatternRoles().size() == 0) {
				return new ValidationWarning<EditionPatternShouldHaveRoles, EditionPattern>(this, editionPattern,
						"edition_pattern_role_has_no_role");
			}
			return null;
		}
	}

	public static class EditionPatternShouldHaveEditionSchemes extends
			ValidationRule<EditionPatternShouldHaveEditionSchemes, EditionPattern> {
		public EditionPatternShouldHaveEditionSchemes() {
			super(EditionPattern.class, "edition_pattern_should_have_edition_scheme");
		}

		@Override
		public ValidationIssue<EditionPatternShouldHaveEditionSchemes, EditionPattern> applyValidation(EditionPattern editionPattern) {
			if (editionPattern.getEditionSchemes().size() == 0) {
				return new ValidationWarning<EditionPatternShouldHaveEditionSchemes, EditionPattern>(this, editionPattern,
						"edition_pattern_has_no_edition_scheme");
			}
			return null;
		}
	}

	public static class EditionPatternShouldHaveDeletionScheme extends
			ValidationRule<EditionPatternShouldHaveDeletionScheme, EditionPattern> {
		public EditionPatternShouldHaveDeletionScheme() {
			super(EditionPattern.class, "edition_pattern_should_have_deletion_scheme");
		}

		@Override
		public ValidationIssue<EditionPatternShouldHaveDeletionScheme, EditionPattern> applyValidation(EditionPattern editionPattern) {
			if (editionPattern.getDeletionSchemes().size() == 0) {
				CreateDefaultDeletionScheme fixProposal = new CreateDefaultDeletionScheme(editionPattern);
				return new ValidationWarning<EditionPatternShouldHaveDeletionScheme, EditionPattern>(this, editionPattern,
						"edition_pattern_has_no_deletion_scheme", fixProposal);
			}
			return null;
		}

		protected static class CreateDefaultDeletionScheme extends FixProposal<EditionPatternShouldHaveDeletionScheme, EditionPattern> {

			private final EditionPattern editionPattern;
			private DeletionScheme newDefaultDeletionScheme;

			public CreateDefaultDeletionScheme(EditionPattern anEditionPattern) {
				super("create_default_deletion_scheme");
				this.editionPattern = anEditionPattern;
			}

			public EditionPattern getEditionPattern() {
				return editionPattern;
			}

			public DeletionScheme getDeletionScheme() {
				return newDefaultDeletionScheme;
			}

			@Override
			protected void fixAction() {
				newDefaultDeletionScheme = editionPattern.createDeletionScheme();
				// AddIndividual action = getObject();
				// action.setAssignation(new ViewPointDataBinding(patternRole.getPatternRoleName()));
			}

		}

	}

}
