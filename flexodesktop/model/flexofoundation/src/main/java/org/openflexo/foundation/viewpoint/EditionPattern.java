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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.CustomType;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.DropScheme;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.LinkScheme;
import org.openflexo.foundation.view.diagram.viewpoint.NavigationScheme;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.binding.PatternRoleBindingVariable;
import org.openflexo.foundation.viewpoint.dm.EditionPatternConstraintInserted;
import org.openflexo.foundation.viewpoint.dm.EditionPatternConstraintRemoved;
import org.openflexo.foundation.viewpoint.dm.EditionPatternHierarchyChanged;
import org.openflexo.foundation.viewpoint.dm.EditionSchemeInserted;
import org.openflexo.foundation.viewpoint.dm.EditionSchemeRemoved;
import org.openflexo.foundation.viewpoint.dm.PatternRoleInserted;
import org.openflexo.foundation.viewpoint.dm.PatternRoleRemoved;
import org.openflexo.foundation.viewpoint.inspector.EditionPatternInspector;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ChainedCollection;
import org.openflexo.toolbox.StringUtils;

/**
 * An EditionPattern aggregates modelling elements from different modelling element resources (models, metamodels, graphical representation,
 * GUI, etc…). Each such element is associated with a {@link PatternRole}.
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
public class EditionPattern extends EditionPatternObject implements CustomType {

	protected static final Logger logger = FlexoLogger.getLogger(EditionPattern.class.getPackage().getName());

	private Vector<PatternRole> patternRoles;
	private Vector<EditionScheme> editionSchemes;
	private Vector<EditionPatternConstraint> editionPatternConstraints;
	private EditionPatternInspector inspector;

	private OntologicObjectPatternRole primaryConceptRole;
	private GraphicalElementPatternRole primaryRepresentationRole;

	private VirtualModel virtualModel;

	private EditionPattern parentEditionPattern = null;
	private Vector<EditionPattern> childEditionPatterns = new Vector<EditionPattern>();

	private EditionPatternStructuralFacet structuralFacet;
	private EditionPatternBehaviouralFacet behaviouralFacet;

	private EditionPatternInstanceType instanceType = new EditionPatternInstanceType(this);

	/**
	 * Stores a chained collections of objects which are involved in validation
	 */
	private ChainedCollection<ViewPointObject> validableObjects = null;

	/**
	 * Represent the type of a EditionPatternInstance of a given EditionPattern
	 * 
	 * @author sylvain
	 * 
	 */
	public static class EditionPatternInstanceType implements CustomType {

		public static EditionPatternInstanceType getEditionPatternInstanceType(EditionPattern anEditionPattern) {
			if (anEditionPattern == null) {
				return null;
			}
			return anEditionPattern.getInstanceType();
		}

		private EditionPattern editionPattern;

		public EditionPatternInstanceType(EditionPattern anEditionPattern) {
			this.editionPattern = anEditionPattern;
		}

		public EditionPattern getEditionPattern() {
			return editionPattern;
		}

		@Override
		public Class getBaseClass() {
			return EditionPatternInstance.class;
		}

		@Override
		public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
			// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
			if (aType instanceof EditionPatternInstanceType) {
				return editionPattern.isAssignableFrom(((EditionPatternInstanceType) aType).getEditionPattern());
			}
			return false;
		}

		@Override
		public String simpleRepresentation() {
			return "EditionPatternInstanceType" + ":" + editionPattern;
		}

		@Override
		public String fullQualifiedRepresentation() {
			return "EditionPatternInstanceType" + ":" + editionPattern;
		}

		@Override
		public String toString() {
			return simpleRepresentation();
		}
	}

	public EditionPattern(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
		if (builder != null) {
			virtualModel = builder.getVirtualModel();
		}
		patternRoles = new Vector<PatternRole>();
		editionSchemes = new Vector<EditionScheme>();
		editionPatternConstraints = new Vector<EditionPatternConstraint>();
		structuralFacet = new EditionPatternStructuralFacet(this);
		behaviouralFacet = new EditionPatternBehaviouralFacet(this);
	}

	@Override
	public Collection<ViewPointObject> getEmbeddedValidableObjects() {
		if (validableObjects == null) {
			validableObjects = new ChainedCollection<ViewPointObject>(getPatternRoles(), getEditionSchemes());
			validableObjects.add(inspector);
		}
		return validableObjects;
	}

	private EditionPatternInstanceType getInstanceType() {
		return instanceType;
	}

	public EditionPatternStructuralFacet getStructuralFacet() {
		return structuralFacet;
	}

	public EditionPatternBehaviouralFacet getBehaviouralFacet() {
		return behaviouralFacet;
	}

	@Override
	public EditionPattern getEditionPattern() {
		return this;
	}

	@Override
	public void delete() {
		if (getVirtualModel() != null) {
			getVirtualModel().removeFromEditionPatterns(this);
		}
		super.delete();
		deleteObservers();
	}

	@Override
	public String getFullyQualifiedName() {
		return (getVirtualModel() != null ? getVirtualModel().getFullyQualifiedName() : "null") + "#" + getName();
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

	public EditionScheme getEditionScheme(String editionSchemeName) {
		for (EditionScheme es : editionSchemes) {
			if (es.getName().equals(editionSchemeName)) {
				return es;
			}
		}
		logger.warning("Not found EditionScheme:" + editionSchemeName);
		return null;
	}

	public Vector<EditionScheme> getEditionSchemes() {
		return editionSchemes;
	}

	public void setEditionSchemes(Vector<EditionScheme> someEditionScheme) {
		editionSchemes = someEditionScheme;
	}

	public void addToEditionSchemes(EditionScheme anEditionScheme) {
		anEditionScheme.setEditionPattern(this);
		editionSchemes.add(anEditionScheme);
		if (getVirtualModel() != null) {
			getVirtualModel().notifyEditionSchemeModified();
		}
		setChanged();
		notifyObservers(new EditionSchemeInserted(anEditionScheme, this));
	}

	public void removeFromEditionSchemes(EditionScheme anEditionScheme) {
		anEditionScheme.setEditionPattern(null);
		editionSchemes.remove(anEditionScheme);
		if (getVirtualModel() != null) {
			getVirtualModel().notifyEditionSchemeModified();
		}
		setChanged();
		notifyObservers(new EditionSchemeRemoved(anEditionScheme, this));
	}

	public Vector<EditionPatternConstraint> getEditionPatternConstraints() {
		return editionPatternConstraints;
	}

	public void setEditionPatternConstraints(Vector<EditionPatternConstraint> someEditionPatternConstraint) {
		editionPatternConstraints = someEditionPatternConstraint;
	}

	public void addToEditionPatternConstraints(EditionPatternConstraint anEditionPatternConstraint) {
		anEditionPatternConstraint.setEditionPattern(this);
		editionPatternConstraints.add(anEditionPatternConstraint);
		setChanged();
		notifyObservers(new EditionPatternConstraintInserted(anEditionPatternConstraint, this));
	}

	public void removeFromEditionPatternConstraints(EditionPatternConstraint anEditionPatternConstraint) {
		anEditionPatternConstraint.setEditionPattern(null);
		editionPatternConstraints.remove(anEditionPatternConstraint);
		setChanged();
		notifyObservers(new EditionPatternConstraintRemoved(anEditionPatternConstraint, this));
	}

	public void createConstraint() {
		EditionPatternConstraint constraint = new EditionPatternConstraint(null);
		addToEditionPatternConstraints(constraint);
	}

	public void deleteConstraint(EditionPatternConstraint constraint) {
		removeFromEditionPatternConstraints(constraint);
	}

	public Vector<PatternRole> getPatternRoles() {
		return patternRoles;
	}

	public void setPatternRoles(Vector<PatternRole> somePatternRole) {
		patternRoles = somePatternRole;
		availablePatternRoleNames = null;
	}

	public void addToPatternRoles(PatternRole aPatternRole) {
		availablePatternRoleNames = null;
		aPatternRole.setEditionPattern(this);
		patternRoles.add(aPatternRole);
		setChanged();
		notifyObservers(new PatternRoleInserted(aPatternRole, this));
		if (_bindingModel != null) {
			updateBindingModel();
		}
	}

	public void removeFromPatternRoles(PatternRole aPatternRole) {
		availablePatternRoleNames = null;
		aPatternRole.setEditionPattern(null);
		patternRoles.remove(aPatternRole);
		setChanged();
		notifyObservers(new PatternRoleRemoved(aPatternRole, this));
		if (_bindingModel != null) {
			updateBindingModel();
		}
	}

	public <R> List<R> getPatternRoles(Class<R> type) {
		List<R> returned = new ArrayList<R>();
		for (PatternRole r : patternRoles) {
			if (TypeUtils.isTypeAssignableFrom(type, r.getClass())) {
				returned.add((R) r);
			}
		}
		return returned;
	}

	public List<IndividualPatternRole> getIndividualPatternRoles() {
		return getPatternRoles(IndividualPatternRole.class);
	}

	public List<ClassPatternRole> getClassPatternRoles() {
		return getPatternRoles(ClassPatternRole.class);
	}

	public List<GraphicalElementPatternRole> getGraphicalElementPatternRoles() {
		return getPatternRoles(GraphicalElementPatternRole.class);
	}

	public List<ShapePatternRole> getShapePatternRoles() {
		return getPatternRoles(ShapePatternRole.class);
	}

	public List<ConnectorPatternRole> getConnectorPatternRoles() {
		return getPatternRoles(ConnectorPatternRole.class);
	}

	public ShapePatternRole getDefaultShapePatternRole() {
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
	}

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

	public String getAvailableRoleName(String baseName) {
		String testName = baseName;
		int index = 2;
		while (getPatternRole(testName) != null) {
			testName = baseName + index;
			index++;
		}
		return testName;
	}

	public PatternRole deletePatternRole(PatternRole aPatternRole) {
		removeFromPatternRoles(aPatternRole);
		aPatternRole.delete();
		return aPatternRole;
	}

	public PatternRole getPatternRole(String patternRole) {
		for (PatternRole pr : patternRoles) {
			if (pr.getPatternRoleName() != null && pr.getPatternRoleName().equals(patternRole)) {
				return pr;
			}
		}
		return null;
	}

	public Vector<DropScheme> getDropSchemes() {
		Vector<DropScheme> returned = new Vector<DropScheme>();
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof DropScheme) {
				returned.add((DropScheme) es);
			}
		}
		return returned;
	}

	public Vector<LinkScheme> getLinkSchemes() {
		Vector<LinkScheme> returned = new Vector<LinkScheme>();
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof LinkScheme) {
				returned.add((LinkScheme) es);
			}
		}
		return returned;
	}

	public Vector<AbstractActionScheme> getAbstractActionSchemes() {
		Vector<AbstractActionScheme> returned = new Vector<AbstractActionScheme>();
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof AbstractActionScheme) {
				returned.add((AbstractActionScheme) es);
			}
		}
		return returned;
	}

	public Vector<ActionScheme> getActionSchemes() {
		Vector<ActionScheme> returned = new Vector<ActionScheme>();
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof ActionScheme) {
				returned.add((ActionScheme) es);
			}
		}
		return returned;
	}

	public Vector<SynchronizationScheme> getSynchronizationSchemes() {
		Vector<SynchronizationScheme> returned = new Vector<SynchronizationScheme>();
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof SynchronizationScheme) {
				returned.add((SynchronizationScheme) es);
			}
		}
		return returned;
	}

	public Vector<DeletionScheme> getDeletionSchemes() {
		Vector<DeletionScheme> returned = new Vector<DeletionScheme>();
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof DeletionScheme) {
				returned.add((DeletionScheme) es);
			}
		}
		return returned;
	}

	public Vector<NavigationScheme> getNavigationSchemes() {
		Vector<NavigationScheme> returned = new Vector<NavigationScheme>();
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof NavigationScheme) {
				returned.add((NavigationScheme) es);
			}
		}
		return returned;
	}

	public Vector<AbstractCreationScheme> getAbstractCreationSchemes() {
		Vector<AbstractCreationScheme> returned = new Vector<AbstractCreationScheme>();
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof AbstractCreationScheme) {
				returned.add((AbstractCreationScheme) es);
			}
		}
		return returned;
	}

	public Vector<CreationScheme> getCreationSchemes() {
		Vector<CreationScheme> returned = new Vector<CreationScheme>();
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof CreationScheme) {
				returned.add((CreationScheme) es);
			}
		}
		return returned;
	}

	public boolean hasDropScheme() {
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof DropScheme) {
				return true;
			}
		}
		return false;
	}

	public boolean hasLinkScheme() {
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof LinkScheme) {
				return true;
			}
		}
		return false;
	}

	public boolean hasActionScheme() {
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof ActionScheme) {
				return true;
			}
		}
		return false;
	}

	public boolean hasSynchronizationScheme() {
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof SynchronizationScheme) {
				return true;
			}
		}
		return false;
	}

	public boolean hasNavigationScheme() {
		for (EditionScheme es : getEditionSchemes()) {
			if (es instanceof NavigationScheme) {
				return true;
			}
		}
		return false;
	}

	public CreationScheme createCreationScheme() {
		CreationScheme newCreationScheme = new CreationScheme(null);
		newCreationScheme.setEditionPattern(this);
		newCreationScheme.setName("creation");
		addToEditionSchemes(newCreationScheme);
		return newCreationScheme;
	}

	public CloningScheme createCloningScheme() {
		CloningScheme newCloningScheme = new CloningScheme(null);
		newCloningScheme.setEditionPattern(this);
		newCloningScheme.setName("clone");
		addToEditionSchemes(newCloningScheme);
		return newCloningScheme;
	}

	public DropScheme createDropScheme() {
		DropScheme newDropScheme = new DropScheme(null);
		newDropScheme.setEditionPattern(this);
		newDropScheme.setName("drop");
		addToEditionSchemes(newDropScheme);
		return newDropScheme;
	}

	public LinkScheme createLinkScheme() {
		LinkScheme newLinkScheme = new LinkScheme(null);
		newLinkScheme.setEditionPattern(this);
		newLinkScheme.setName("link");
		addToEditionSchemes(newLinkScheme);
		return newLinkScheme;
	}

	public ActionScheme createActionScheme() {
		ActionScheme newActionScheme = new ActionScheme(null);
		newActionScheme.setEditionPattern(this);
		newActionScheme.setName("action");
		addToEditionSchemes(newActionScheme);
		return newActionScheme;
	}

	public SynchronizationScheme createSynchronizationScheme() {
		SynchronizationScheme newSynchronizationScheme = new SynchronizationScheme(null);
		newSynchronizationScheme.setEditionPattern(this);
		newSynchronizationScheme.setName("synchronization");
		addToEditionSchemes(newSynchronizationScheme);
		return newSynchronizationScheme;
	}

	public NavigationScheme createNavigationScheme() {
		NavigationScheme newNavigationScheme = new NavigationScheme(null);
		newNavigationScheme.setEditionPattern(this);
		newNavigationScheme.setName("navigation");
		addToEditionSchemes(newNavigationScheme);
		return newNavigationScheme;
	}

	public DeletionScheme createDeletionScheme() {
		DeletionScheme newDeletionScheme = generateDefaultDeletionScheme();
		return newDeletionScheme;
	}

	public EditionScheme deleteEditionScheme(EditionScheme editionScheme) {
		removeFromEditionSchemes(editionScheme);
		editionScheme.delete();
		return editionScheme;
	}

	public DeletionScheme getDefaultDeletionScheme() {
		if (getDeletionSchemes().size() > 0) {
			return getDeletionSchemes().firstElement();
		}
		return null;
	}

	public DeletionScheme generateDefaultDeletionScheme() {
		DeletionScheme newDeletionScheme = new DeletionScheme(null);
		newDeletionScheme.setName("deletion");
		Vector<PatternRole> rolesToDelete = new Vector<PatternRole>();
		for (PatternRole pr : getPatternRoles()) {
			if (pr instanceof GraphicalElementPatternRole || pr instanceof IndividualPatternRole /*|| pr instanceof StatementPatternRole*/) {
				rolesToDelete.add(pr);
			}
		}
		Collections.sort(rolesToDelete, new Comparator<PatternRole>() {
			@Override
			public int compare(PatternRole o1, PatternRole o2) {
				if (o1 instanceof ShapePatternRole && o2 instanceof ConnectorPatternRole) {
					return 1;
				} else if (o1 instanceof ConnectorPatternRole && o2 instanceof ShapePatternRole) {
					return -1;
				}

				if (o1 instanceof ShapePatternRole) {
					if (o2 instanceof ShapePatternRole) {
						if (((ShapePatternRole) o1).isEmbeddedIn((ShapePatternRole) o2)) {
							return -1;
						}
						if (((ShapePatternRole) o2).isEmbeddedIn((ShapePatternRole) o1)) {
							return 1;
						}
						return 0;
					}
				}
				return 0;
			}

		});
		for (PatternRole pr : rolesToDelete) {
			DeleteAction a = new DeleteAction(null);
			a.setObject(new DataBinding<Object>(pr.getPatternRoleName()));
			newDeletionScheme.addToActions(a);
		}
		addToEditionSchemes(newDeletionScheme);
		return newDeletionScheme;
	}

	public EditionPatternInspector getInspector() {
		if (inspector == null) {
			inspector = EditionPatternInspector.makeEditionPatternInspector(this);
		}
		return inspector;
	}

	public void setInspector(EditionPatternInspector inspector) {
		inspector.setEditionPattern(this);
		this.inspector = inspector;
	}

	@Override
	public VirtualModel getVirtualModel() {
		return virtualModel;
	}

	public void setVirtualModel(VirtualModel virtualModel) {
		this.virtualModel = virtualModel;
	}

	@Override
	public String toString() {
		return "EditionPattern:" + getName();
	}

	public void finalizeEditionPatternDeserialization() {
		createBindingModel();
		for (EditionScheme es : getEditionSchemes()) {
			es.finalizeEditionSchemeDeserialization();
		}
		for (PatternRole pr : getPatternRoles()) {
			pr.finalizePatternRoleDeserialization();
		}
	}

	public void finalizeParentEditionPatternDeserialization() {
		if (StringUtils.isNotEmpty(parentEditionPatternURI)) {
			setParentEditionPattern(getParentEditionPattern());
		}
	}

	public void debug() {
		System.out.println(getXMLRepresentation());
	}

	public void save() {
		getVirtualModel().save();
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

	public OntologicObjectPatternRole getDefaultPrimaryConceptRole() {
		List<OntologicObjectPatternRole> roles = getPatternRoles(OntologicObjectPatternRole.class);
		if (roles.size() > 0) {
			return roles.get(0);
		}
		return null;
	}

	public GraphicalElementPatternRole getDefaultPrimaryRepresentationRole() {
		List<GraphicalElementPatternRole> roles = getPatternRoles(GraphicalElementPatternRole.class);
		if (roles.size() > 0) {
			return roles.get(0);
		}
		return null;
	}

	public OntologicObjectPatternRole getPrimaryConceptRole() {
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
	}

	@Override
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
	}

	/**
	 * Duplicates this EditionPattern, given a new name<br>
	 * Newly created EditionPattern is added to ViewPoint
	 * 
	 * @param newName
	 * @return
	 */
	public EditionPattern duplicate(String newName) {
		EditionPattern newEditionPattern = (EditionPattern) cloneUsingXMLMapping();
		newEditionPattern.setName(newName);
		getVirtualModel().addToEditionPatterns(newEditionPattern);
		return newEditionPattern;
	}

	public boolean isRoot() {
		return getParentEditionPattern() == null;
	}

	private String parentEditionPatternURI;

	public String getParentEditionPatternURI() {
		if (getParentEditionPattern() != null) {
			return getParentEditionPattern().getURI();
		}
		return parentEditionPatternURI;
	}

	public void _setParentEditionPatternURI(String aParentEditionPatternURI) {
		parentEditionPatternURI = aParentEditionPatternURI;
	}

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
				this.parentEditionPattern.setChanged();
				this.parentEditionPattern.notifyObservers(new EditionPatternHierarchyChanged(this));
				this.parentEditionPattern.notifyChange("childEditionPatterns", null, getChildEditionPatterns());
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
	}

	public Vector<EditionPattern> getChildEditionPatterns() {
		return childEditionPatterns;
	}

	public boolean isAssignableFrom(EditionPattern editionPattern) {
		if (editionPattern == this) {
			return true;
		}
		if (editionPattern.getParentEditionPattern() != null) {
			return isAssignableFrom(editionPattern.getParentEditionPattern());
		}
		return false;
	}

	@Override
	public String getLanguageRepresentation(LanguageRepresentationContext context) {
		// Voir du cote de GeneratorFormatter pour formatter tout ca
		StringBuilder sb = new StringBuilder();
		sb.append("EditionPattern ").append(getName());
		sb.append(" {").append(StringUtils.LINE_SEPARATOR);
		sb.append(StringUtils.LINE_SEPARATOR);
		for (PatternRole pr : getPatternRoles()) {
			sb.append(pr.getLanguageRepresentation());
			sb.append(StringUtils.LINE_SEPARATOR);
		}
		sb.append("}").append(StringUtils.LINE_SEPARATOR);
		return sb.toString();
	}

	public static class EditionPatternShouldHaveRoles extends ValidationRule<EditionPatternShouldHaveRoles, EditionPattern> {
		public EditionPatternShouldHaveRoles() {
			super(EditionPattern.class, "edition_pattern_should_have_roles");
		}

		@Override
		public ValidationIssue<EditionPatternShouldHaveRoles, EditionPattern> applyValidation(EditionPattern editionPattern) {
			if (editionPattern.getPatternRoles().size() == 0) {
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

			private EditionPattern editionPattern;
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
