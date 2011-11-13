package org.openflexo.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
public @interface NewNameFinder {

	public String collection();

	public String attribute();
}
