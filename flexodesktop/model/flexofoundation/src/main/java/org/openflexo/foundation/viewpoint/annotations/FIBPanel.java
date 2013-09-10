package org.openflexo.foundation.viewpoint.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows to specify a relative path to a FIB encoding a panel representing annotated class
 * 
 * @author sylvain
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface FIBPanel {

	/**
	 * The property identifier of this getter
	 * 
	 * @return the property identifier of this getter
	 */
	public String value();

}
