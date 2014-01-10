package org.openflexo.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicates that annotated method should be called after the whole graph of objects has been deserialized<br>
 * Order of calls of these methods just respect the order where objects were created
 * 
 * @author sylvain
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DeserializationFinalizer {

}
