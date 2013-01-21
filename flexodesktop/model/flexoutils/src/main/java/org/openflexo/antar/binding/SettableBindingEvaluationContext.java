package org.openflexo.antar.binding;

/**
 * This interface is implemented by all classes defining a run-time context for binding evaluation when a write access to some
 * {@link BindingVariable} is allowed
 * 
 * @author sylvain
 * 
 */
public interface SettableBindingEvaluationContext extends BindingEvaluationContext {

	/**
	 * Sets the value of symbolic variable {@link BindingVariable} in current run-time context
	 * 
	 * @param value
	 * @param variable
	 */
	public void setValue(Object value, BindingVariable variable);
}