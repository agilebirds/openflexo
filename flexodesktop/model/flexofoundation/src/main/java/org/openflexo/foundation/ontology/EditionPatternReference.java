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
package org.openflexo.foundation.ontology;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ontology.owl.DataPropertyStatement;
import org.openflexo.foundation.ontology.owl.OWLObject;
import org.openflexo.foundation.ontology.owl.ObjectPropertyStatement;
import org.openflexo.foundation.ontology.owl.OntologyRestrictionClass;
import org.openflexo.foundation.ontology.owl.SubClassStatement;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.foundation.xml.VEShemaBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.technologyadapter.owl.viewpoint.DataPropertyStatementActorReference;
import org.openflexo.technologyadapter.owl.viewpoint.ObjectPropertyStatementActorReference;
import org.openflexo.technologyadapter.owl.viewpoint.SubClassStatementActorReference;
import org.openflexo.xmlcode.XMLMapping;

public class EditionPatternReference extends FlexoModelObject implements DataFlexoObserver, BindingEvaluationContext {

	static final Logger logger = FlexoLogger.getLogger(EditionPatternReference.class.getPackage().toString());

	private EditionPatternInstance _editionPatternInstance;
	private PatternRole patternRole;

	private EditionPattern editionPattern;
	private long instanceId;
	private Hashtable<String, ActorReference> actors;
	private FlexoProject _project;

	private EditionPatternReference() {
		super();
		actors = new Hashtable<String, ActorReference>();
	}

	private EditionPatternReference(FlexoProject project) {
		this();
		_project = project;
	}

	// Constructor used during deserialization
	public EditionPatternReference(VEShemaBuilder builder) {
		this(builder.getProject());
		initializeDeserialization(builder);
	}

	// Constructor used during deserialization
	public EditionPatternReference(FlexoProcessBuilder builder) {
		this(builder.getProject());
		initializeDeserialization(builder);
	}

	// Constructor used during deserialization
	public EditionPatternReference(FlexoWorkflowBuilder builder) {
		this(builder.getProject());
		initializeDeserialization(builder);
	}

	public EditionPatternReference(EditionPatternInstance instance, PatternRole patternRole) {
		this();
		_project = instance.getProject();
		_editionPatternInstance = instance;
		this.patternRole = patternRole;
		_editionPatternInstance.addObserver(this);
		update();
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		logger.fine("Called finalizeDeserialization() for EditionPatternReference ");
		for (ActorReference ref : actors.values()) {
			if (ref instanceof ConceptActorReference) {
				getProject()._addToPendingEditionPatternReferences(((ConceptActorReference) ref)._getObjectURI(),
						(ConceptActorReference) ref);
			}
		}
	}

	/**
	 * Delete the reference (not the instance !!!)
	 */
	@Override
	public void delete() {

		// Why delete the edition pattern instance ????
		// Just dereference myself
		/*if (getEditionPatternInstance() != null && !getEditionPatternInstance().isDeleted()) {
			getEditionPatternInstance().delete();
		}*/

		super.delete();

		actors.clear();
		editionPattern = null;
		patternRole = null;
		_editionPatternInstance = null;

		// deleteObservers();
	}

	private void update() {
		actors.clear();
		if (_editionPatternInstance != null) {
		editionPattern = _editionPatternInstance.getPattern();
		instanceId = _editionPatternInstance.getInstanceId();
		for (String role : _editionPatternInstance.getActors().keySet()) {
			// System.out.println("> role : "+role);
			FlexoModelObject o = _editionPatternInstance.getActors().get(role);
			if (o instanceof OntologyObject) {
				actors.put(role, new ConceptActorReference((OntologyObject) o, role, this));
			} else if (o instanceof ObjectPropertyStatement) {
				actors.put(role, new ObjectPropertyStatementActorReference((ObjectPropertyStatement) o, role, this));
			} else if (o instanceof DataPropertyStatement) {
				actors.put(role, new DataPropertyStatementActorReference((DataPropertyStatement) o, role, this));
			} else if (o instanceof SubClassStatement) {
				actors.put(role, new SubClassStatementActorReference((SubClassStatement) o, role, this));
			} /*else if (o instanceof ObjectRestrictionStatement) {
				actors.put(role, new RestrictionStatementActorReference((ObjectRestrictionStatement) o, role, this));
				}*/else {
				actors.put(role, new ModelObjectActorReference(o, role, this));
			}
		}
	}
	}

	@Override
	public String getClassNameKey() {
		return "edition_pattern_reference";
	}

	@Override
	public String getFullyQualifiedName() {
		return "EditionPatternReference" + "_" + editionPattern.getName() + "_" + instanceId;
	}

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	@Override
	public XMLMapping getXMLMapping() {
		// Not defined in this context, since this is cross-model object
		return null;
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		// Not defined in this context, since this is cross-model object
		return null;
	}

	public static abstract class ActorReference extends FlexoModelObject {
		public String patternRole;
		private ModelSlot<?,?> modelSlot;
		private FlexoProject _project;
		private EditionPatternReference _patternReference;

		protected ActorReference(FlexoProject project) {
			super(project);
			_project = project;
		}

		public ModelSlot<?,?> getModelSlot() {
			return modelSlot;
		}

		public void setModelSlot(ModelSlot<?,?> modelSlot) {
			this.modelSlot = modelSlot;
		}

		@Override
		public FlexoProject getProject() {
			return _project;
		}

		@Override
		public XMLMapping getXMLMapping() {
			// Not defined in this context, since this is cross-model object
			return null;
		}

		@Override
		public XMLStorageResourceData getXMLResourceData() {
			// Not defined in this context, since this is cross-model object
			return null;
		}

		public abstract FlexoModelObject retrieveObject();

		public EditionPatternReference getPatternReference() {
			return _patternReference;
		}

		public void setPatternReference(EditionPatternReference _patternReference) {
			this._patternReference = _patternReference;
		}

	}

	public static class ConceptActorReference extends ActorReference {
		private OntologyObject object;
		private String objectURI;

		public ConceptActorReference(OntologyObject o, String aPatternRole, EditionPatternReference ref) {
			super(ref.getProject());
			setPatternReference(ref);
			patternRole = aPatternRole;
			object = o;
			objectURI = o.getURI();
		}

		// Constructor used during deserialization
		public ConceptActorReference(VEShemaBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		// Constructor used during deserialization
		public ConceptActorReference(FlexoProcessBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		// Constructor used during deserialization
		public ConceptActorReference(FlexoWorkflowBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		@Override
		public String getClassNameKey() {
			return "concept_actor_reference";
		}

		@Override
		public String getFullyQualifiedName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FlexoModelObject retrieveObject() {
			if (object == null) {
				object = getProject().retrieveOntologyObject(objectURI);
			}
			if (object == null) {
				logger.warning("Could not retrieve object " + objectURI);
			}
			return (FlexoModelObject) object;
		}

		public String _getObjectURI() {
			if (object != null) {
				return object.getURI();
			}
			return objectURI;
		}

		public void _setObjectURI(String objectURI) {
			this.objectURI = objectURI;
		}

		public OntologyObject getObject() {
			return object;
		}
	}

	public static class ModelObjectActorReference extends ActorReference {
		public FlexoModelObject object;
		public FlexoModelObjectReference objectReference;

		public ModelObjectActorReference(FlexoModelObject o, String aPatternRole, EditionPatternReference ref) {
			super(o.getProject());
			setPatternReference(ref);
			patternRole = aPatternRole;
			object = o;
			objectReference = new FlexoModelObjectReference(o);
		}

		// Constructor used during deserialization
		public ModelObjectActorReference(VEShemaBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		// Constructor used during deserialization
		public ModelObjectActorReference(FlexoProcessBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		// Constructor used during deserialization
		public ModelObjectActorReference(FlexoWorkflowBuilder builder) {
			super(builder.getProject());
			initializeDeserialization(builder);
		}

		@Override
		public String getClassNameKey() {
			return "model_object_actor_reference";
		}

		@Override
		public String getFullyQualifiedName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FlexoModelObject retrieveObject() {
			if (object == null) {
				object = objectReference.getObject(true);
			}
			if (object == null) {
				logger.warning("Could not retrieve object " + objectReference);
			}
			return object;
		}
	}

	public EditionPatternInstance getEditionPatternInstance() {
		if (_editionPatternInstance != null) {
			return _editionPatternInstance;
		}
		_editionPatternInstance = getProject().getEditionPatternInstance(this);

		// Warning: this is really important to keep synchro between EPInstance and EPReference
		if (_editionPatternInstance != null) {
			_editionPatternInstance.addObserver(this);
		}
		return _editionPatternInstance;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof EditionPatternActorChanged) {
			update();
		}
	}

	private String _patternRoleName;

	public String getPatternRoleName() {
		if (patternRole != null) {
			return patternRole.getPatternRoleName();
		}
		return _patternRoleName;
	}

	public void setPatternRoleName(String patternRoleName) {
		_patternRoleName = patternRoleName;
		if (getEditionPattern() != null) {
			patternRole = getEditionPattern().getPatternRole(_patternRoleName);
		}
	}

	public PatternRole getPatternRole() {
		if (patternRole == null && getEditionPattern() != null) {
			patternRole = getEditionPattern().getPatternRole(_patternRoleName);
		}
		return patternRole;
	}

	public EditionPattern getEditionPattern() {
		return editionPattern;
	}

	public void setEditionPattern(EditionPattern pattern) {
		this.editionPattern = pattern;
	}

	public ViewPoint getViewPoint() {
		if (getEditionPattern() != null) {
			return getEditionPattern().getViewPoint();
		}
		return null;
	}

	public FlexoOntology getViewPointOntology() {
		if (getViewPoint() != null) {
			return getViewPoint().getViewpointOntology();
		}
		return null;
	}

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public Hashtable<String, ActorReference> getActors() {
		return actors;
	}

	public void setActors(Hashtable<String, ActorReference> actors) {
		this.actors = actors;
	}

	public void setActorForKey(ActorReference o, String key) {
		actors.put(key, o);
		o.setPatternReference(this);
	}

	public void removeActorWithKey(String key) {
		actors.remove(key);
	}

	@Override
	public Object getValue(BindingVariable variable) {
		return getEditionPatternInstance().getValue(variable);
	}

	public String debug() {
		StringBuffer sb = new StringBuffer();
		sb.append("Reference to EditionPattern with role : " + getPatternRole() + "\n");
		sb.append("EditionPattern: " + getEditionPatternInstance().getPattern().getName() + "\n");
		sb.append("Instance: " + instanceId + " hash=" + Integer.toHexString(hashCode()) + "\n");
		for (String patternRole : actors.keySet()) {
			FlexoModelObject object = actors.get(patternRole);
			sb.append("Role: " + patternRole + " : " + object + "\n");
		}
		return sb.toString();
	}

	public boolean isPrimaryRole() {
		return getPatternRole() != null && getPatternRole().getIsPrimaryRole();
	}
}
