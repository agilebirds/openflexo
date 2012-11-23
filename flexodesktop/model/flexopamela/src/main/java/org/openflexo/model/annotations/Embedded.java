package org.openflexo.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
/**
 * This annotation is associated with a model property. It defines whether a model property should be considered as embedded or not. It also allows
 * to define a set of properties that must be present in the context, in order to consider this model property value as 'embedded'.
 * @author Guillaume
 *
 */
public @interface Embedded {
	/**
	 * The list of model properties that must be included in order to consider this property as embedded. <br/>
	 * <br/>
	 * Let's take the simple example of an entity 'Node' and an entity 'Edge'.<br/>
	 * 
	 * 'Node' has two properties:
	 * <ul>
	 * <li>'incomingEdge' of the type 'Edge'</li>
	 * <li>'outgoingEdge' of the type 'Edge'</li>
	 * </ul>
	 * 'Edge has two properties:
	 * <ul>
	 * <li>'startNode' of the type 'Node'</li>
	 * <li>'endNode' of the type 'Node'</li>
	 * </ul>
	 * 
	 * When cloning a 'Node', we expect that we also clone the 'incomingEdge' only if its 'startNode' is also embedded. In order to indicate
	 * this to PAMELA, we will add a closureCondition: 'startNode'.
	 * 
	 * @return the list of model properties of the corresponding model property value that must be embedded in order to consider this model
	 *         property to be embedded.
	 */
	public String[] closureConditions() default {};

	/**
	 * The list of model properties that must be included in order to also delete this property. <br/>
	 * <br/>
	 * Let's take the simple example of an entity 'Node' and an entity 'Edge'.<br/>
	 * 
	 * 'Node' has two properties:
	 * <ul>
	 * <li>'incomingEdge' of the type 'Edge'</li>
	 * <li>'outgoingEdge' of the type 'Edge'</li>
	 * </ul>
	 * 'Edge has two properties:
	 * <ul>
	 * <li>'startNode' of the type 'Node'</li>
	 * <li>'endNode' of the type 'Node'</li>
	 * </ul>
	 * 
	 * When deleting a 'Node', we could say that we allow an edge to live without a start or end node, but that it must be attached to at
	 * least one of them. If both 'startNode' and 'endNode' are deleted, then we want the edge to also be deleted. In order to indicate this
	 * to PAMELA, we will add the deletionCondition: 'startNode'.
	 * 
	 * @return the list of model properties of the corresponding model property value that must be embedded in order to consider this model
	 *         property to be deleted.
	 */
	public String[] deletionConditions() default {};
}
