package org.openflexo.model.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

import org.openflexo.model.ModelEntity;
import org.openflexo.model.ModelProperty;
import org.openflexo.model.PamelaUtils;
import org.openflexo.model.exceptions.ModelExecutionException;
import org.openflexo.model.undo.AddCommand;
import org.openflexo.model.undo.RemoveCommand;
import org.openflexo.model.undo.SetCommand;

/**
 * Represents a partial delegate implementation, associated to a master {@link ProxyMethodHandler}<br>
 * 
 * Many partial delegate implementations might be defined for a given {@link ProxyMethodHandler}. Multiple inheritance is here implemented
 * by a composition scheme (dynamic binding at run-time)
 * 
 * 
 * @author sylvain
 * 
 * @param <I>
 */
public class DelegateImplementation<I> extends ProxyFactory implements MethodHandler {

	private final I delegateObject;
	private final Class<I> delegateImplementationClass;
	private final Set<Method> implementedMethods;
	private final ProxyMethodHandler<I> masterMethodHandler;

	/**
	 * Build a new {@link DelegateImplementation} for supplied master {@link ProxyMethodHandler}
	 * 
	 * @param masterMethodHandler
	 * @param delegateImplementationClass
	 * @param implementedMethods
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DelegateImplementation(ProxyMethodHandler<I> masterMethodHandler, Class<I> delegateImplementationClass,
			Set<Method> implementedMethods) throws IllegalArgumentException, NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		this.masterMethodHandler = masterMethodHandler;
		this.delegateImplementationClass = delegateImplementationClass;
		setSuperclass(delegateImplementationClass);
		this.implementedMethods = implementedMethods;
		Class<?>[] interfaces = { getModelEntity().getImplementedInterface() };
		setInterfaces(interfaces);

		delegateObject = (I) create(new Class<?>[0], new Object[0], this);

		// System.out.println("Created " + delegateObject + " for " + delegateImplementationClass);
	}

	/**
	 * Indicates if this {@link DelegateImplementation} provides implementation for supplied method
	 * 
	 * @param method
	 * @return
	 */
	// TODO: perf issues: optimize this !!!
	public boolean handleMethod(Method method) {
		for (Method m : implementedMethods) {
			if (PamelaUtils.methodIsEquivalentTo(m, method)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returned local method of delegate implementation handling local implementation for supplied method
	 * 
	 * @param method
	 * @return
	 */
	public Method localImplementationFor(Method method) {
		for (Method m : implementedMethods) {
			if (PamelaUtils.methodIsEquivalentTo(m, method)) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Called when a method was invoked on delegated implementation<br>
	 * This method is strongly involved in master object dynamic binding, when partial implementations are defined.
	 * 
	 */
	@Override
	public Object invoke(Object self, Method method, Method proceed, Object[] args) throws Throwable {

		// In this case, we address an existing method in delegated implementation
		// We should invoke it, but before that, we might intercept the call to notify UndoManager of the execution of a new UndoableEdit
		if (proceed != null) {
			ModelProperty<? super I> property = getModelEntity().getPropertyForMethod(method);
			if (property != null) {
				I masterObject = masterMethodHandler.getObject();
				if (PamelaUtils.methodIsEquivalentTo(method, property.getSetterMethod())) {
					// System.out.println("DETECTS SET with " + proceed + " insteadof " + method);
					Object oldValue = masterMethodHandler.invokeGetter(property);
					if (getModelFactory().getUndoManager() != null) {
						if (oldValue != args[0]) {
							getModelFactory().getUndoManager().addEdit(
									new SetCommand<I>(masterObject, getModelEntity(), property, oldValue, args[0], getModelFactory()));
						}
					}
				}
				if (PamelaUtils.methodIsEquivalentTo(method, property.getAdderMethod())) {
					// System.out.println("DETECTS ADD with " + proceed + " insteadof " + method);
					if (getModelFactory().getUndoManager() != null) {
						getModelFactory().getUndoManager().addEdit(
								new AddCommand<I>(masterObject, getModelEntity(), property, args[0], getModelFactory()));
					}
				}
				if (PamelaUtils.methodIsEquivalentTo(method, property.getRemoverMethod())) {
					// System.out.println("DETECTS REMOVE with " + proceed + " insteadof " + method);
					if (getModelFactory().getUndoManager() != null) {
						getModelFactory().getUndoManager().addEdit(
								new RemoveCommand<I>(masterObject, getModelEntity(), property, args[0], getModelFactory()));
					}
				}
			}
			try {
				// Now we really invoke the method (which is a real implementation in delegated implementation)
				return proceed.invoke(self, args);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new ModelExecutionException(e.getCause());
			}
		}

		// Otherwise, we should check if this delegated implementation has a real implementation of supplied method
		else if (handleMethod(method)) {
			// (The answer is yes)
			try {
				// Now, we must find which method in delegated implementation really implements supplied method
				Method localImplementation = localImplementationFor(method);
				// System.out.println("Using local implementation " + localImplementation + " in " +
				// localImplementation.getDeclaringClass());
				// We just have now to call that method
				// (Note that we will be redirected in this invoke(...) method, but the argument proceed will carry the method to invoke)
				return localImplementation.invoke(delegateObject, args);
				// return method.invoke(self, args);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new ModelExecutionException(e.getCause());
			}
		}

		// Otherwise this method has no local implementation, forward it to the master
		else {
			I masterObject = masterMethodHandler.getObject();
			if (masterObject != null) {
				// System.out.println("I should invoke in MASTER: " + method);
				try {
					return method.invoke(masterObject, args);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					throw new ModelExecutionException(e.getCause());
				}
			} else {
				// Master object is not yet set, silently returns null
				System.err.println("Called method " + method + " on uninitialized ProxyMethodHandler " + getModelEntity());
				return null;
			}
		}
	}

	// private int prout = 0;

	public I getMasterObject() {
		return masterMethodHandler.getObject();
	}

	public I getDelegateObject() {
		return delegateObject;
	}

	public Class<I> getDelegateImplementationClass() {
		return delegateImplementationClass;
	}

	public ModelEntity<I> getModelEntity() {
		return masterMethodHandler.getPamelaProxyFactory().getModelEntity();
	}

	public ModelFactory getModelFactory() {
		return masterMethodHandler.getPamelaProxyFactory().getModelFactory();
	}

}
