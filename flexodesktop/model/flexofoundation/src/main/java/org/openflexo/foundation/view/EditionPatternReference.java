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
package org.openflexo.foundation.view;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.foundation.xml.VEShemaBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.xmlcode.XMLMapping;

public class EditionPatternReference extends FlexoModelObject implements DataFlexoObserver, BindingEvaluationContext {

	static final Logger logger = FlexoLogger.getLogger(EditionPatternReference.class.getPackage().toString());

	private EditionPatternInstance _editionPatternInstance;
	private PatternRole<?> patternRole;

	private EditionPattern editionPattern;
	private long instanceId;
	private Hashtable<PatternRole<?>, ActorReference<?>> actors;
	private FlexoProject _project;

	private EditionPatternReference() {
		super();
		actors = new Hashtable<PatternRole<?>, ActorReference<?>>();
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
			for (PatternRole role : _editionPatternInstance.getEditionPattern().getPatternRoles()) {
				// System.out.println("> role : "+role);
				FlexoModelObject o = _editionPatternInstance.getPatternActor(role);
				actors.put(role, role.makeActorReference(o, this));
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

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public Hashtable<PatternRole<?>, ActorReference<?>> getActors() {
		return actors;
	}

	public void setActors(Hashtable<PatternRole<?>, ActorReference<?>> actors) {
		this.actors = actors;
	}

	public void setActorForKey(ActorReference<?> o, PatternRole<?> key) {
		actors.put(key, o);
		o.setPatternReference(this);
	}

	public void removeActorWithKey(PatternRole<?> key) {
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
		for (PatternRole<?> patternRole : actors.keySet()) {
			ActorReference<?> actorReference = actors.get(patternRole);
			sb.append("Role: " + patternRole + " : " + actorReference.retrieveObject() + "\n");
		}
		return sb.toString();
	}

	public boolean isPrimaryRole() {
		return getPatternRole() != null && getPatternRole().getIsPrimaryRole();
	}
}
