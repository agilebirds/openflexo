package org.openflexo.antar.binding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.HasPropertyChangeSupport;

public class DependingObjects {

	private static final Logger logger = FlexoLogger.getLogger(DependingObjects.class.getName());

	public static interface HasDependencyBinding extends Observer, PropertyChangeListener {
		public List<DataBinding<?>> getDependencyBindings();

		public List<TargetObject> getChainedBindings(DataBinding<?> binding, TargetObject object);
	}

	private List<TargetObject> dependingObjects;
	private HasDependencyBinding observerObject;
	private boolean dependingObjectsAreComputed;

	public DependingObjects(HasDependencyBinding observerObject) {
		super();
		this.observerObject = observerObject;
		this.dependingObjects = new ArrayList<TargetObject>();
		this.dependingObjectsAreComputed = false;
	}

	protected void addToDependingObjects(TargetObject object) {
		dependingObjects.add(object);
	}

	public synchronized void refreshObserving(BindingEvaluationContext context/*, boolean debug*/) {

		/*if (debug) {
			logger.info("refreshObserving() for " + observerObject);
		}*/

		if (observerObject == null) {
			return;
		}

		List<TargetObject> updatedDependingObjects = new ArrayList<TargetObject>();
		for (DataBinding<?> binding : observerObject.getDependencyBindings()) {
			List<TargetObject> targetObjects = binding.getTargetObjects(context);
			if (targetObjects != null) {
				updatedDependingObjects.addAll(targetObjects);
				if (targetObjects.size() > 0) {
					List<TargetObject> chainedBindings = observerObject.getChainedBindings(binding,
							targetObjects.get(targetObjects.size() - 1));
					if (chainedBindings != null) {
						updatedDependingObjects.addAll(chainedBindings);
					}
				}
			}
		}
		Set<HasPropertyChangeSupport> set = new HashSet<HasPropertyChangeSupport>();
		for (TargetObject o : updatedDependingObjects) {
			if (o.target instanceof HasPropertyChangeSupport) {
				set.add((HasPropertyChangeSupport) o.target);
			}
		}
		for (HasPropertyChangeSupport hasPCSupport : set) {
			if (hasPCSupport.getDeletedProperty() != null) {
				updatedDependingObjects.add(new TargetObject(hasPCSupport, hasPCSupport.getDeletedProperty()));
			}
		}

		List<TargetObject> newDependingObjects = new ArrayList<TargetObject>();
		List<TargetObject> oldDependingObjects = new ArrayList<TargetObject>(dependingObjects);
		for (TargetObject o : updatedDependingObjects) {
			if (oldDependingObjects.contains(o)) {
				oldDependingObjects.remove(o);
			} else {
				newDependingObjects.add(o);
			}
		}
		for (TargetObject o : oldDependingObjects) {
			dependingObjects.remove(o);
			if (o.target instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o.target).getPropertyChangeSupport();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Observer " + observerObject + " remove property change listener: " + o.target + " property:"
							+ o.propertyName);
				}
				/*if (debug) {
					logger.info("Observer " + observerObject + " remove property change listener: " + o.target + " property:"
							+ o.propertyName);
				}*/
				pcSupport.removePropertyChangeListener(o.propertyName, observerObject);
			} else if (o.target instanceof Observable) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Widget " + observerObject + " remove observable: " + o);
				}
				/*if (debug) {
					logger.info("Widget " + observerObject + " remove observable: " + o);
				}*/
				((Observable) o.target).deleteObserver(observerObject);
			}
		}
		for (TargetObject o : newDependingObjects) {
			dependingObjects.add(o);
			if (o.target instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o.target).getPropertyChangeSupport();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Observer " + observerObject + " add property change listener: " + o.target + " property:" + o.propertyName);
				}
				/*if (debug) {
					logger.info("Observer " + observerObject + " add property change listener: " + o.target + " property:" + o.propertyName);
				}*/
				pcSupport.addPropertyChangeListener(o.propertyName, observerObject);
			} else if (o.target instanceof Observable) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Observer " + observerObject + " add observable: " + o);
				}
				/*if (debug) {
					logger.info("Observer " + observerObject + " add observable: " + o + " for " + o.propertyName);
				}*/
				((Observable) o.target).addObserver(observerObject);
			}
		}

		dependingObjectsAreComputed = true;
	}

	public synchronized void stopObserving() {
		for (TargetObject o : dependingObjects) {
			if (o.target instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o.target).getPropertyChangeSupport();
				// logger.info("Widget "+getWidget()+" remove property change listener: "+o.target+" property:"+o.propertyName);
				pcSupport.removePropertyChangeListener(o.propertyName, observerObject);
			} else if (o.target instanceof Observable) {
				// logger.info("Widget "+getWidget()+" remove observable: "+o);
				((Observable) o.target).deleteObserver(observerObject);
			}
		}
		dependingObjects.clear();
		dependingObjectsAreComputed = false;
	}

	public boolean areDependingObjectsComputed() {
		return dependingObjectsAreComputed;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("DependingObjects for " + observerObject + "\n");
		for (TargetObject o : dependingObjects) {
			sb.append("> " + o + "\n");
		}
		return sb.toString();
	}

}
