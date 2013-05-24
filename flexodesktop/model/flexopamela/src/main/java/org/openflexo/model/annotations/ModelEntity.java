package org.openflexo.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openflexo.model.exceptions.ModelExecutionException;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface ModelEntity {
	/**
	 * Defines the different initialization policy of the model entity.
	 * <ul>
	 * <li>NONE: It is not mandatory to invoke any initializer although there are some declared on the entity</li>
	 * <li>WARN_IF_NOT_INVOKED: It is not mandatory to invoke any initializer although there are some declared on the entity, but a warning
	 * will be output if a new instance of the entity is used in the model</li>
	 * <li>REQUIRED: It is mandatory to invoke an initializer of the entity if there is at least on initializer defined in the entity
	 * hierarchy. Using an instance of this entity without having initialized it will throw a ModelExcecutionException @see
	 * {@link ModelExecutionException}</li>
	 * 
	 * @author Guillaume
	 * 
	 */
	public static enum InitPolicy {

		NONE, WARN_IF_NOT_INVOKED, REQUIRED;
	}

	public boolean isAbstract() default false;

	public boolean inheritInitializers() default false;

	public InitPolicy initPolicy() default InitPolicy.REQUIRED;
}
