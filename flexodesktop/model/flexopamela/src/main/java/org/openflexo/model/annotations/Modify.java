package org.openflexo.model.annotations;

public @interface Modify {

	public String forward();

	public boolean synchWithForward() default true;
}
