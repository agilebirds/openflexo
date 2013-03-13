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

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NotSettableContextException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoProjectObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.view.action.DeletionSchemeAction;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.CloningScheme;
import org.openflexo.foundation.viewpoint.DeletionScheme;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

/**
 * A {@link EditionPatternInstance} is the run-time concept (instance) of an {@link EditionPattern}.<br>
 * 
 * As such, a {@link EditionPatternInstance} is instantiated inside a {@link VirtualModelInstance} (only {@link VirtualModelInstance}
 * objects might leave outside an other {@link VirtualModelInstance}).<br>
 * 
 * @author sylvain
 * 
 */
public class EditionPatternInstance extends VirtualModelInstanceObject implements Bindable, BindingEvaluationContext {

	private static final Logger logger = FlexoLogger.getLogger(EditionPatternInstance.class.getPackage().toString());

	protected static final String DELETED_PROPERTY = "deleted";

	private EditionPattern editionPattern;
	private Hashtable<PatternRole<?>, ActorReference<?>> actors;
	private VirtualModelInstance<?, ?> vmInstance;

	private Vector<ActorReference<?>> deserializedActorList;

	/**
	 * 
	 * @param aPattern
	 */

	/*public EditionPatternInstance(EditionPatternReference aPattern) {
		super(aPattern.getProject());
		// logger.info(">>>>>>>> EditionPatternInstance "+Integer.toHexString(hashCode())+" <init1> actors="+actors);
		editionPattern = aPattern.getEditionPattern();
		instanceId = aPattern.getInstanceId();
		actors = new Hashtable<PatternRole<?>, ActorReference<?>>();
		for (PatternRole<?> patternRole : aPattern.getActors().keySet()) {
			ActorReference<?> actor = aPattern.getActors().get(patternRole);
			Object object = actor.retrieveObject();
			if (object instanceof FlexoModelObject) {
				actors.put(patternRole, (FlexoModelObject) object);
			}
		}
	}*/

	/**
	 * Constructor invoked during deserialization
	 */
	public EditionPatternInstance(VirtualModelInstanceBuilder builder) {
		super(builder.getProject());
		vmInstance = builder.vmInstance;
		actors = new Hashtable<PatternRole<?>, ActorReference<?>>();
		// actorList = new Vector<ActorReference<?>>();
		initializeDeserialization(builder);
	}

	public EditionPatternInstance(EditionPattern aPattern, VirtualModelInstance vmInstance, FlexoProject project) {
		super(vmInstance != null ? vmInstance.getProject() : project);
		// logger.info(">>>>>>>> EditionPatternInstance "+Integer.toHexString(hashCode())+" <init2> actors="+actors);
		this.vmInstance = vmInstance;
		this.editionPattern = aPattern;
		actors = new Hashtable<PatternRole<?>, ActorReference<?>>();
		// actorList = new Vector<ActorReference<?>>();
	}

	public <T> T getPatternActor(PatternRole<T> patternRole) {
		// logger.info(">>>>>>>> EditionPatternInstance "+Integer.toHexString(hashCode())+" getPatternActor() actors="+actors);
		ActorReference<T> actorReference = (ActorReference<T>) actors.get(patternRole);
		if (actorReference != null) {
			return actorReference.retrieveObject();
		}
		return null;
	}

	public <T> void setPatternActor(T object, PatternRole<T> patternRole) {
		setObjectForPatternRole(object, patternRole);
	}

	public <T> void nullifyPatternActor(PatternRole<T> patternRole) {
		setObjectForPatternRole(null, patternRole);
	}

	public <T> PatternRole<T> getRoleForActor(T actor) {
		for (PatternRole<?> role : actors.keySet()) {
			if (getPatternActor(role) == actor) {
				return (PatternRole<T>) role;
			}
		}
		return null;
	}

	public <T> void setObjectForPatternRole(T object, PatternRole<T> patternRole) {
		logger.info(">>>>>>>> For patternRole: " + patternRole + " set " + object + " was " + getPatternActor(patternRole));
		T oldObject = getPatternActor(patternRole);
		if (object != oldObject) {
			// Un-register last reference
			if (oldObject instanceof FlexoProjectObject) {
				((FlexoProjectObject) oldObject).unregisterEditionPatternReference(this);
			}

			// Un-register last reference
			if (object instanceof FlexoProjectObject) {
				((FlexoProjectObject) object).registerEditionPatternReference(this);
			}

			ActorReference<T> actorReference = patternRole.makeActorReference(object, this);

			if (object != null) {
				actors.put(patternRole, actorReference);
			} else {
				actors.remove(patternRole);
			}
			setChanged();
			notifyObservers(new EditionPatternActorChanged(this, patternRole, oldObject, object));
			// System.out.println("EditionPatternInstance "+Integer.toHexString(hashCode())+" setObjectForPatternRole() actors="+actors);
			getPropertyChangeSupport().firePropertyChange(patternRole.getPatternRoleName(), oldObject, object);
		}
	}

	public String debug() {
		StringBuffer sb = new StringBuffer();
		sb.append("EditionPattern: " + editionPattern.getName() + "\n");
		sb.append("Instance: " + getFlexoID() + " hash=" + Integer.toHexString(hashCode()) + "\n");
		for (PatternRole<?> patternRole : actors.keySet()) {
			FlexoModelObject object = actors.get(patternRole);
			sb.append("Role: " + patternRole + " : " + object + "\n");
		}
		return sb.toString();
	}

	private String editionPatternURI;

	public EditionPattern getEditionPattern() {
		if (getVirtualModelInstance() != null && editionPattern == null && StringUtils.isNotEmpty(editionPatternURI)) {
			editionPattern = getVirtualModelInstance().getVirtualModel().getEditionPattern(editionPatternURI);
		}
		return editionPattern;
	}

	// Serialization/deserialization only, do not use
	public String getEditionPatternURI() {
		if (getEditionPattern() != null) {
			return getEditionPattern().getURI();
		}
		return editionPatternURI;
	}

	// Serialization/deserialization only, do not use
	public void setEditionPatternURI(String editionPatternURI) {
		this.editionPatternURI = editionPatternURI;
	}

	public Hashtable<PatternRole<?>, ActorReference<?>> getActors() {
		return actors;
	}

	/*public void setActors(Hashtable<PatternRole<?>, ActorReference<?>> actors) {
		this.actors = actors;
	}

	public void setActorForKey(ActorReference<?> o, PatternRole<?> key) {
		actors.put(key, o);
		ActorReference removeThis = null;
		for (ActorReference ref : actorList) {
			if (ref.getPatternRole() == o.getPatternRole()) {
				removeThis = ref;
			}
		}
		if (removeThis != null) {
			actorList.remove(removeThis);
		}
	}

	public void removeActorWithKey(PatternRole<?> key) {
		actors.remove(key);
		ActorReference removeThis = null;
		for (ActorReference ref : actorList) {
			if (ref.getPatternRole() == key) {
				removeThis = ref;
			}
		}
		if (removeThis != null) {
			actorList.remove(removeThis);
		}
	}*/

	/*public String getStringValue(String inspectorEntryKey)
	{
		return "GET string value for "+inspectorEntryKey;
	}
	
	public void setStringValue(String inspectorEntryKey, String value)
	{
		System.out.println("SET string value for "+inspectorEntryKey+" value: "+value);
	}*/

	// WARNING: do no use outside context of serialization/deserialization (performance issues)
	public Vector<ActorReference<?>> getActorList() {
		return new Vector<ActorReference<?>>(actors.values());
	}

	// WARNING: do no use outside context of serialization/deserialization
	public void setActorList(Vector<ActorReference<?>> deserializedActors) {
		for (ActorReference<?> ar : deserializedActors) {
			addToActorList(ar);
		}
	}

	// WARNING: do no use outside context of serialization/deserialization
	public void addToActorList(ActorReference actorReference) {
		actorReference.setEditionPatternInstance(this);
		if (actorReference.getPatternRole() != null) {
			actors.put(actorReference.getPatternRole(), actorReference);
		} else {
			if (deserializedActorList == null) {
				deserializedActorList = new Vector<ActorReference<?>>();
			}
			deserializedActorList.add(actorReference);
		}
	}

	// WARNING: do no use outside context of serialization/deserialization
	public void removeFromActorList(ActorReference actorReference) {
		actorReference.setEditionPatternInstance(null);
		if (actorReference.getPatternRole() != null) {
			actors.remove(actorReference.getPatternRole());
		}
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		finalizeActorsDeserialization();
	}

	private void finalizeActorsDeserialization() {
		if (getEditionPattern() != null && deserializedActorList != null) {
			for (ActorReference actorRef : deserializedActorList) {
				// System.out.println("Actor: " + actorRef.getPatternRoleName() + " pattern role = " + actorRef.getPatternRole() + " name="
				// + actorRef.getPatternRoleName() + " ep=" + getEditionPattern());
				if (actorRef.getPatternRole() != null) {
					actors.put(actorRef.getPatternRole(), actorRef);
				}
			}
		}
	}

	public Object evaluate(String expression) {
		DataBinding<Object> vpdb = new DataBinding<Object>(expression);
		vpdb.setOwner(getEditionPattern());
		vpdb.setDeclaredType(Object.class);
		vpdb.setBindingDefinitionType(BindingDefinitionType.GET);
		try {
			return vpdb.getBindingValue(this);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean setBindingValue(String expression, Object value) {
		DataBinding<Object> vpdb = new DataBinding<Object>(expression);
		vpdb.setOwner(getEditionPattern());
		vpdb.setDeclaredType(Object.class);
		vpdb.setBindingDefinitionType(BindingDefinitionType.SET);
		if (vpdb.isValid()) {
			try {
				vpdb.setBindingValue(value, this);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NotSettableContextException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public BindingFactory getBindingFactory() {
		return getEditionPattern().getInspector().getBindingFactory();
	}

	@Override
	public BindingModel getBindingModel() {
		return getEditionPattern().getInspector().getBindingModel();
	}

	@Override
	public Object getValue(BindingVariable variable) {
		PatternRole pr = getEditionPattern().getPatternRole(variable.getVariableName());
		if (pr != null) {
			return getPatternActor(pr);
		}
		logger.warning("Unexpected " + variable);
		return null;
	}

	private boolean deleted = false;

	public boolean deleted() {
		return deleted;
	}

	@Override
	public boolean isDeleted() {
		return deleted();
	}

	/**
	 * Delete this EditionPattern instance using default DeletionScheme
	 */
	@Override
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
		Object primaryPatternActor = getPatternActor(getEditionPattern().getPrimaryRepresentationRole());
		if (primaryPatternActor instanceof FlexoModelObject) {
			DeletionSchemeAction deletionSchemeAction = DeletionSchemeAction.actionType.makeNewAction(
					(FlexoModelObject) primaryPatternActor, null, null);
			deletionSchemeAction.setDeletionScheme(deletionScheme);
			deletionSchemeAction.setEditionPatternInstanceToDelete(this);
			deletionSchemeAction.doAction();
			if (deletionSchemeAction.hasActionExecutionSucceeded()) {
				logger.info("Successfully performed delete EditionPattern instance " + getEditionPattern());
			}
		} else {
			logger.warning("Actor for role " + getEditionPattern().getPrimaryRepresentationRole() + " is not a FlexoModelObject: is "
					+ primaryPatternActor);
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
		for (PatternRole<?> pr : getEditionPattern().getPatternRoles()) {
			if (pr.defaultBehaviourIsToBeDeleted() && getPatternActor(pr) instanceof FlexoModelObject) {
				returned.add((FlexoModelObject) getPatternActor(pr));
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

	@Override
	public String getDisplayableName() {
		for (GraphicalElementPatternRole pr : getEditionPattern().getGraphicalElementPatternRoles()) {
			if (pr != null && pr.getLabel().isSet() && pr.getLabel().isValid()) {
				try {
					return (String) pr.getLabel().getBindingValue(this);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return getEditionPattern().getName();
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
	}

	@Override
	public String getFullyQualifiedName() {
		return getVirtualModelInstance().getFullyQualifiedName() + "." + getEditionPattern().getURI() + "." + getFlexoID();
	}

	@Override
	public String getClassNameKey() {
		return "edition_pattern_instance";
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return getVirtualModelInstance();
	}

	@Override
	public VirtualModelInstance<?, ?> getVirtualModelInstance() {
		return vmInstance;
	}

	public void setVirtualModelInstance(VirtualModelInstance<?, ?> vmInstance) {
		this.vmInstance = vmInstance;
	}
}
