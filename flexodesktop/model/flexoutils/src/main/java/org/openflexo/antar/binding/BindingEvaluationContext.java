package org.openflexo.antar.binding;

/**
 * This interface is implemented by all classes defining a run-time context for binding evaluation<br>
 * The main purpose of this context is to provide read access to some {@link BindingVariable}
 * 
 * @author sylvain
 * 
 */
public interface BindingEvaluationContext {

	/**
	 * Return the value of symbolic variable {@link BindingVariable} in current run-time context
	 * 
	 * @param variable
	 * @return
	 */
	public Object getValue(BindingVariable variable);

}