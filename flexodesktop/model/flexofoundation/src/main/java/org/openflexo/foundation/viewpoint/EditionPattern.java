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
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.CustomType;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.DropScheme;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.LinkScheme;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.PatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.viewpoint.dm.EditionSchemeInserted;
import org.openflexo.foundation.viewpoint.dm.EditionSchemeRemoved;
import org.openflexo.foundation.viewpoint.dm.PatternRoleInserted;
import org.openflexo.foundation.viewpoint.dm.PatternRoleRemoved;
import org.openflexo.foundation.viewpoint.inspector.EditionPatternInspector;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ChainedCollection;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;

public class EditionPattern extends EditionPatternObject implements StringConvertable<EditionPattern>, CustomType {

	protected static final Logger logger = FlexoLogger.getLogger(EditionPattern.class.getPackage().getName());

	private Vector<PatternRole> patternRoles;
	private Vector<EditionScheme> editionSchemes;
	private EditionPatternInspector inspector;

	private OntologicObjectPatternRole primaryConceptRole;
	private GraphicalElementPatternRole primaryRepresentationRole;

	private ViewPoint _viewPoint;

	private EditionPattern parentEditionPattern = null;
	private Vector<EditionPattern> childEditionPatterns = new Vector<EditionPattern>();

	private EditionPatternStructuralFacet structuralFacet;
	private EditionPatternBehaviouralFacet behaviouralFacet;

	/**
	 * Stores a chained collections of objects which are involved in validation
	 */
	private ChainedCollection<ViewPointObject> validableObjects = null;

	@Override
	public Collection<ViewPointObject> getEmbeddedValidableObjects() {
		if (validableObjects == null) {
			validableObjects = new ChainedCollection<ViewPointObject>(getPatternRoles(), getEditionSchemes());
			validableObjects.add(inspector);
		}
		return validableObjects;
	}

	public EditionPattern(ViewPointBuilder builder) {
		super(builder);
		patternRoles = new Vector<PatternRole>();
		editionSchemes = new Vector<EditionScheme>();
		if (builder != null) {
			setViewPoint(builder.getViewPoint());
		}
		structuralFacet = new EditionPatternStructuralFacet(this);
		behaviouralFacet = new EditionPatternBehaviouralFacet(this);
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
		if (getViewPoint() != null) {
			getViewPoint().removeFromEditionPatterns(this);
		}
		super.delete();
		deleteObservers();
	}

	@Override
	public String getFullyQualifiedName() {
		return (getViewPoint() != null ? getViewPoint().getFullyQualifiedName() : "null") + "#" + getName();
	}

	@Override
	public String getURI() {
		return getViewPoint().getURI() + "#" + getName();
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
		if (getViewPoint() != null) {
			getViewPoint().notifyEditionSchemeModified();
		}
		setChanged();
		notifyObservers(new EditionSchemeInserted(anEditionScheme, this));
	}

	public void removeFromEditionSchemes(EditionScheme anEditionScheme) {
		anEditionScheme.setEditionPattern(null);
		editionSchemes.remove(anEditionScheme);
		if (getViewPoint() != null) {
			getViewPoint().notifyEditionSchemeModified();
		}
		setChanged();
		notifyObservers(new EditionSchemeRemoved(anEditionScheme, this));
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

	@Deprecated
	public PatternRole createShapePatternRole() {
		ShapePatternRole newPatternRole = new ShapePatternRole(null);
		newPatternRole.setPatternRoleName(getAvailableRoleName("shape"));
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}

	@Deprecated
	public ConnectorPatternRole createConnectorPatternRole() {
		ConnectorPatternRole newPatternRole = new ConnectorPatternRole(null);
		newPatternRole.setPatternRoleName(getAvailableRoleName("connector"));
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}

	@Deprecated
	public DiagramPatternRole createDiagramPatternRole() {
		DiagramPatternRole newPatternRole = new DiagramPatternRole(null);
		newPatternRole.setPatternRoleName(getAvailableRoleName("diagram"));
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}

	@Deprecated
	public FlexoModelObjectPatternRole createFlexoModelObjectPatternRole() {
		FlexoModelObjectPatternRole newPatternRole = new FlexoModelObjectPatternRole(null);
		newPatternRole.setPatternRoleName(getAvailableRoleName("flexoObject"));
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}

	@Deprecated
	public EditionPatternPatternRole createEditionPatternPatternRole() {
		EditionPatternPatternRole newPatternRole = new EditionPatternPatternRole(null);
		newPatternRole.setPatternRoleName(getAvailableRoleName("editionPattern"));
		addToPatternRoles(newPatternRole);
		return newPatternRole;
	}

	@Deprecated
	public PrimitivePatternRole createPrimitivePatternRole() {
		PrimitivePatternRole newPatternRole = new PrimitivePatternRole(null);
		newPatternRole.setPatternRoleName(getAvailableRoleName("primitive"));
		addToPatternRoles(newPatternRole);
		return newPatternRole;
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
		newCreationScheme.setName("creation");
		addToEditionSchemes(newCreationScheme);
		return newCreationScheme;
	}

	public CloningScheme createCloningScheme() {
		CloningScheme newCloningScheme = new CloningScheme(null);
		newCloningScheme.setName("clone");
		addToEditionSchemes(newCloningScheme);
		return newCloningScheme;
	}

	public DropScheme createDropScheme() {
		DropScheme newDropScheme = new DropScheme(null);
		newDropScheme.setName("drop");
		addToEditionSchemes(newDropScheme);
		return newDropScheme;
	}

	public LinkScheme createLinkScheme() {
		LinkScheme newLinkScheme = new LinkScheme(null);
		newLinkScheme.setName("link");
		addToEditionSchemes(newLinkScheme);
		return newLinkScheme;
	}

	public ActionScheme createActionScheme() {
		ActionScheme newActionScheme = new ActionScheme(null);
		newActionScheme.setName("action");
		addToEditionSchemes(newActionScheme);
		return newActionScheme;
	}

	public NavigationScheme createNavigationScheme() {
		NavigationScheme newNavigationScheme = new NavigationScheme(null);
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
			a.setObject(new ViewPointDataBinding(pr.getPatternRoleName()));
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
	public ViewPoint getViewPoint() {
		return _viewPoint;
	}

	public void setViewPoint(ViewPoint viewPoint) {
		_viewPoint = viewPoint;
	}

	@Deprecated
	public void setCalc(ViewPoint viewPoint) {
		setViewPoint(viewPoint);
	}

	public static class EditionPatternConverter extends StringEncoder.Converter<EditionPattern> {
		private final ViewPointLibrary viewPointLibrary;

		public EditionPatternConverter(ViewPointLibrary viewPointLibrary) {
			super(EditionPattern.class);
			this.viewPointLibrary = viewPointLibrary;
		}

		@Override
		public EditionPattern convertFromString(String value) {
			return viewPointLibrary.getEditionPattern(value);
		}

		@Override
		public String convertToString(EditionPattern value) {
			return value.getViewPoint().getViewPointURI() + "#" + value.getName();
		}
	}

	@Override
	public EditionPatternConverter getConverter() {
		return getViewPointLibrary().editionPatternConverter;

		/*if (getProject()!=null)
		   return getProject().getEditionPatternConverter();
		return null;*/
	}

	/* public EditionAction getAction(String patternRole)
	 {
	   return getEditionScheme().getAction(patternRole);
	 }

	 public AddShape getAddShapeAction(String patternRole)
	 {
	   return getEditionScheme().getAddShapeAction(patternRole);
	 }

	 public AddShemaElementAction getAddShemaElementAction(String patternRole)
	 {
	   return getEditionScheme().getAddShemaElementAction(patternRole);
	 }

	 public AddClass getAddClassAction(String patternRole)
	 {
	   return getEditionScheme().getAddClassAction(patternRole);
	 }

	 public AddIndividual getAddIndividualAction(String patternRole)
	 {
	   return getEditionScheme().getAddIndividualAction(patternRole);
	 }

	 public AddConnector getAddConnectorAction(String patternRole)
	 {
	   return getEditionScheme().getAddConnectorAction(patternRole);
	 }
	 */

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
		getViewPoint().save();
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
			BindingVariable<?> bv = PatternRolePathElement.makePatternRolePathElement(role, this);
			if (bv != null) {
				_bindingModel.addToBindingVariables(bv);
			}
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
		getViewPoint().addToEditionPatterns(newEditionPattern);
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
			if (getViewPointLibrary() != null) {
				setParentEditionPattern(getViewPointLibrary().getEditionPattern(parentEditionPatternURI));
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
			if (getViewPoint() != null) {
				getViewPoint().setChanged();
				getViewPoint().notifyObservers(new EditionPatternHierarchyChanged(this));
				getViewPoint().notifyChange("allRootEditionPatterns", null, getViewPoint().getAllRootEditionPatterns());
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
	public String getLanguageRepresentation() {
		// Voir du cote de GeneratorFormatter pour formatter tout ca
		StringBuffer sb = new StringBuffer();
		sb.append("EditionPattern " + getName());
		sb.append(" {" + StringUtils.LINE_SEPARATOR);
		sb.append(StringUtils.LINE_SEPARATOR);
		for (PatternRole pr : getPatternRoles()) {
			sb.append(pr.getLanguageRepresentation());
			sb.append(StringUtils.LINE_SEPARATOR);
		}
		sb.append("}" + StringUtils.LINE_SEPARATOR);
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
