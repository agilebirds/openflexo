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
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ontology.EditionPatternReference.ActorReference;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.action.DeletionSchemeAction;
import org.openflexo.foundation.viewpoint.CloningScheme;
import org.openflexo.foundation.viewpoint.DeletionScheme;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.StatementPatternRole;
import org.openflexo.foundation.viewpoint.binding.PatternRolePathElement;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.logging.FlexoLogger;

public class EditionPatternInstance extends FlexoObservable implements Bindable, BindingEvaluationContext {

	private static final Logger logger = FlexoLogger.getLogger(EditionPatternReference.class.getPackage().toString());

	protected static final String DELETED_PROPERTY = "deleted";

	private FlexoProject _project;
	private EditionPattern pattern;
	private long instanceId;
	private Hashtable<String, FlexoModelObject> actors;

	/**
	 * 
	 * @param aPattern
	 */

	public EditionPatternInstance(EditionPatternReference aPattern) {
		super();
		// logger.info(">>>>>>>> EditionPatternInstance "+Integer.toHexString(hashCode())+" <init1> actors="+actors);
		_project = aPattern.getProject();
		pattern = aPattern.getEditionPattern();
		instanceId = aPattern.getInstanceId();
		actors = new Hashtable<String, FlexoModelObject>();
		for (String patternRole : aPattern.getActors().keySet()) {
			ActorReference actor = aPattern.getActors().get(patternRole);
			FlexoModelObject object = actor.retrieveObject();
			if (object != null) {
				actors.put(patternRole, object);
			}
		}
	}

	public EditionPatternInstance(EditionPattern aPattern, FlexoProject project) {
		super();
		// logger.info(">>>>>>>> EditionPatternInstance "+Integer.toHexString(hashCode())+" <init2> actors="+actors);
		_project = project;
		instanceId = _project.getNewFlexoID();
		this.pattern = aPattern;
		actors = new Hashtable<String, FlexoModelObject>();
	}

	public FlexoModelObject getPatternActor(String patternRole) {
		// logger.info(">>>>>>>> EditionPatternInstance "+Integer.toHexString(hashCode())+" getPatternActor() actors="+actors);
		return actors.get(patternRole);
	}

	public FlexoModelObject getPatternActor(PatternRole patternRole) {
		// logger.info(">>>>>>>> EditionPatternInstance "+Integer.toHexString(hashCode())+" getPatternActor() actors="+actors);
		return actors.get(patternRole.getPatternRoleName());
	}

	public void setPatternActor(FlexoModelObject object, PatternRole patternRole) {
		setObjectForPatternRole(object, patternRole);
	}

	public void nullifyPatternActor(PatternRole patternRole) {
		setObjectForPatternRole(null, patternRole);
	}

	public void setObjectForPatternRole(FlexoModelObject object, PatternRole patternRole) {
		logger.info(">>>>>>>> For patternRole: " + patternRole + " set " + object + " was " + getPatternActor(patternRole));
		FlexoModelObject oldObject = getPatternActor(patternRole);
		if (object != oldObject) {
			// Un-register last reference
			if (oldObject != null) {
				oldObject.unregisterEditionPatternReference(this, patternRole);
			}

			// Un-register last reference
			if (object != null) {
				object.registerEditionPatternReference(this, patternRole);
			}

			if (object != null) {
				actors.put(patternRole.getPatternRoleName(), object);
			} else {
				actors.remove(patternRole.getPatternRoleName());
			}
			setChanged();
			notifyObservers(new EditionPatternActorChanged(this, patternRole, oldObject, object));
			// System.out.println("EditionPatternInstance "+Integer.toHexString(hashCode())+" setObjectForPatternRole() actors="+actors);
			getPropertyChangeSupport().firePropertyChange(patternRole.getPatternRoleName(), oldObject, object);
		}
	}

	public FlexoProject getProject() {
		return _project;
	}

	public String debug() {
		StringBuffer sb = new StringBuffer();
		sb.append("EditionPattern: " + pattern.getName() + "\n");
		sb.append("Instance: " + instanceId + " hash=" + Integer.toHexString(hashCode()) + "\n");
		for (String patternRole : actors.keySet()) {
			FlexoModelObject object = actors.get(patternRole);
			sb.append("Role: " + patternRole + " : " + object + "\n");
		}
		return sb.toString();
	}

	public EditionPattern getEditionPattern() {
		return pattern;
	}

	public EditionPattern getPattern() {
		return getEditionPattern();
	}

	public void setPattern(EditionPattern pattern) {
		this.pattern = pattern;
	}

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public Hashtable<String, FlexoModelObject> getActors() {
		return actors;
	}

	public void setActors(Hashtable<String, FlexoModelObject> actors) {
		this.actors = actors;
	}

	public void setActorForKey(FlexoModelObject o, String key) {
		actors.put(key, o);
	}

	public void removeActorWithKey(String key) {
		actors.remove(key);
	}

	/*public String getStringValue(String inspectorEntryKey)
	{
		return "GET string value for "+inspectorEntryKey;
	}
	
	public void setStringValue(String inspectorEntryKey, String value)
	{
		System.out.println("SET string value for "+inspectorEntryKey+" value: "+value);
	}*/

	public Object evaluate(String expression) {
		ViewPointDataBinding vpdb = new ViewPointDataBinding(expression);
		vpdb.setOwner(getPattern());
		vpdb.setBindingDefinition(new BindingDefinition("epi", Object.class, BindingDefinitionType.GET, false));
		return vpdb.getBindingValue(this);
	}

	public boolean setBindingValue(String binding, Object value) {
		ViewPointDataBinding vpdb = new ViewPointDataBinding(binding);
		vpdb.setOwner(getPattern());
		vpdb.setBindingDefinition(new BindingDefinition("epi", Object.class, BindingDefinitionType.SET, false));
		if (vpdb.getBinding().isBindingValid() && vpdb.getBinding().isSettable()) {
			vpdb.setBindingValue(value, this);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public BindingFactory getBindingFactory() {
		return getPattern().getInspector().getBindingFactory();
	}

	@Override
	public BindingModel getBindingModel() {
		return getPattern().getInspector().getBindingModel();
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable instanceof PatternRolePathElement) {
			return getPatternActor(((PatternRolePathElement) variable).getPatternRole());
		}
		logger.warning("Unexpected " + variable);
		return null;
	}

	private boolean deleted = false;

	public boolean deleted() {
		return deleted;
	}

	public boolean isDeleted() {
		return deleted();
	}

	/**
	 * Delete this EditionPattern instance using default DeletionScheme
	 */
	public void delete() {
		// Also implement properly #getDeletedProperty()
		if (getEditionPattern().getDefaultDeletionScheme() != null) {
			delete(getEditionPattern().getDefaultDeletionScheme());
		} else {
			// Generate on-the-fly default deletion scheme
			delete(getEditionPattern().generateDefaultDeletionScheme());
		}

	}

	/**
	 * Delete this EditionPattern instance using supplied DeletionScheme
	 */
	public void delete(DeletionScheme deletionScheme) {
		logger.warning("NEW EditionPatternInstance deletion !");
		deleted = true;
		DeletionSchemeAction deletionSchemeAction = DeletionSchemeAction.actionType.makeNewAction(getPatternActor(getEditionPattern()
				.getPrimaryRepresentationRole()), null, null);
		deletionSchemeAction.setDeletionScheme(deletionScheme);
		deletionSchemeAction.setEditionPatternInstanceToDelete(this);
		deletionSchemeAction.doAction();
		if (deletionSchemeAction.hasActionExecutionSucceeded()) {
			logger.info("Successfully performed delete EditionPattern instance " + getEditionPattern());
		}
	}

	/**
	 * Clone this EditionPattern instance using default CloningScheme
	 */
	public EditionPatternInstance cloneEditionPatternInstance() {
		/*if (getEditionPattern().getDefaultDeletionScheme() != null) {
			delete(getEditionPattern().getDefaultDeletionScheme());
		} else {
			// Generate on-the-fly default deletion scheme
			delete(getEditionPattern().generateDefaultDeletionScheme());
		}*/
		System.out.println("cloneEditionPatternInstance() in EditionPatternInstance");
		return null;
	}

	/**
	 * Delete this EditionPattern instance using supplied DeletionScheme
	 */
	public EditionPatternInstance cloneEditionPatternInstance(CloningScheme cloningScheme) {
		/*logger.warning("NEW EditionPatternInstance deletion !");
		deleted = true;
		DeletionSchemeAction deletionSchemeAction = DeletionSchemeAction.actionType.makeNewAction(getPatternActor(getEditionPattern()
				.getPrimaryRepresentationRole()), null, null);
		deletionSchemeAction.setDeletionScheme(deletionScheme);
		deletionSchemeAction.setEditionPatternInstanceToDelete(this);
		deletionSchemeAction.doAction();
		if (deletionSchemeAction.hasActionExecutionSucceeded()) {
			logger.info("Successfully performed delete EditionPattern instance " + getEditionPattern());
		}*/
		System.out.println("cloneEditionPatternInstance() in EditionPatternInstance with " + cloningScheme);
		return null;
	}

	/**
	 * Return the list of objects that will be deleted if default DeletionScheme is used
	 */
	public List<FlexoModelObject> objectsThatWillBeDeleted() {
		Vector<FlexoModelObject> returned = new Vector<FlexoModelObject>();
		for (PatternRole pr : getEditionPattern().getPatternRoles()) {
			if (pr instanceof GraphicalElementPatternRole || pr instanceof IndividualPatternRole || pr instanceof StatementPatternRole) {
				returned.add(getPatternActor(pr));
			}
		}
		return returned;
	}

	/**
	 * Delete this EditionPattern instance using supplied DeletionScheme
	 */
	public List<FlexoModelObject> objectsThatWillBeDeleted(DeletionScheme deletionScheme) {
		return null;
	}

	@Override
	public String getDeletedProperty() {
		// when delete will be implemented, a notification will need to be sent and this method should reflect the name of the
		// property of that notification
		return DELETED_PROPERTY;
	}

	public String getDisplayableName() {
		for (GraphicalElementPatternRole pr : getPattern().getGraphicalElementPatternRoles()) {
			if (pr != null && pr.getLabel().isSet() && pr.getLabel().isValid()) {
				return (String) pr.getLabel().getBindingValue(this);
			}
		}
		return getPattern().getName();
	}
}
