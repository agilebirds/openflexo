package org.openflexo.antar.binding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * A listener that tracks evalutated value of a DataBinding, given a run-time context given by a {@link BindingEvaluationContext}
 * 
 * @author sylvain
 * 
 */
public abstract class BindingValueChangeListener<T> implements PropertyChangeListener, Observer {

	private static final Logger logger = FlexoLogger.getLogger(BindingValueChangeListener.class.getName());

	private DataBinding<T> dataBinding;
	private BindingEvaluationContext context;
	private List<TargetObject> dependingObjects;
	protected T lastNotifiedValue;

	public BindingValueChangeListener(DataBinding<T> dataBinding, BindingEvaluationContext context) {
		super();
		this.dataBinding = dataBinding;
		this.context = context;
		this.dependingObjects = new ArrayList<TargetObject>();
		lastNotifiedValue = evaluateValue();
		refreshObserving(false);
	}

	public void delete() {
		dataBinding = null;
		context = null;
		dependingObjects.clear();
		dependingObjects = null;
	}

	public List<TargetObject> getChainedBindings(TargetObject object) {
		return null;
	}

	private synchronized void refreshObserving(boolean debug) {

		if (debug) {
			logger.info("-------------> refreshObserving() for " + dataBinding + " context=" + context);
			logger.info("-------------> DependencyBindings:");
		}

		List<TargetObject> updatedDependingObjects = new ArrayList<TargetObject>();
		List<TargetObject> targetObjects = dataBinding.getTargetObjects(context);
		if (targetObjects != null) {
			updatedDependingObjects.addAll(targetObjects);
			if (targetObjects.size() > 0) {
				List<TargetObject> chainedBindings = getChainedBindings(targetObjects.get(targetObjects.size() - 1));
				if (chainedBindings != null) {
					updatedDependingObjects.addAll(chainedBindings);
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
					logger.fine("Observer of " + dataBinding + " remove property change listener: " + o.target + " property:"
							+ o.propertyName);
				}
				if (debug) {
					logger.info("-------------> Observer of " + dataBinding + " remove property change listener: " + o.target
							+ " property:" + o.propertyName);
				}
				pcSupport.removePropertyChangeListener(o.propertyName, this);
			} else if (o.target instanceof Observable) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Observer of " + dataBinding + " remove observable: " + o);
				}
				if (debug) {
					logger.info("Observer of " + dataBinding + " remove observable: " + o);
				}
				((Observable) o.target).deleteObserver(this);
			}
		}
		for (TargetObject o : newDependingObjects) {
			dependingObjects.add(o);
			if (o.target instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o.target).getPropertyChangeSupport();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("-------------> Observer of " + dataBinding + " add property change listener: " + o.target + " property:"
							+ o.propertyName);
				}
				if (debug) {
					logger.info("-------------> Observer of " + dataBinding + " add property change listener: " + o.target + " property:"
							+ o.propertyName);
				}
				pcSupport.addPropertyChangeListener(o.propertyName, this);
			} else if (o.target instanceof Observable) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Observer of " + dataBinding + " add observable: " + o);
				}
				if (debug) {
					logger.info("Observer of " + dataBinding + " add observable: " + o + " for " + o.propertyName);
				}
				((Observable) o.target).addObserver(this);
			}
		}

	}

	public synchronized void startObserving() {
		refreshObserving(false);
	}

	public synchronized void stopObserving() {
		for (TargetObject o : dependingObjects) {
			if (o.target instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o.target).getPropertyChangeSupport();
				// logger.info("Widget "+getWidget()+" remove property change listener: "+o.target+" property:"+o.propertyName);
				pcSupport.removePropertyChangeListener(o.propertyName, this);
			} else if (o.target instanceof Observable) {
				// logger.info("Widget "+getWidget()+" remove observable: "+o);
				((Observable) o.target).deleteObserver(this);
			}
		}
		dependingObjects.clear();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("BindingValueChangeListener for " + dataBinding + " context=" + context + "\n");
		for (TargetObject o : dependingObjects) {
			sb.append("> " + o + "\n");
		}
		return sb.toString();
	}

	public T evaluateValue() {
		try {
			return (T) dataBinding.getBindingValue(context);
		} catch (TypeMismatchException e) {
			logger.warning("Unexpected exception raised. See logs for details.");
			e.printStackTrace();
		} catch (NullReferenceException e) {
			logger.warning("Unexpected exception raised. See logs for details.");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			logger.warning("Unexpected exception raised. See logs for details.");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void update(Observable o, Object arg) {
		fireChange(o);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		fireChange(evt.getSource());
	}

	protected void fireChange(Object source) {
		T newValue = evaluateValue();
		if (newValue != lastNotifiedValue) {
			lastNotifiedValue = newValue;
			bindingValueChanged(source, newValue);
			refreshObserving(false);
		}
	}

	public abstract void bindingValueChanged(Object source, T newValue);
}
