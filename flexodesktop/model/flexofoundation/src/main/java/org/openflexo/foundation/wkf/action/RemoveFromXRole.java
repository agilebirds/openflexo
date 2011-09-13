/*
 * Created on 16 mar. 2006 by sguerin
 *
 * Flexo Application Suite
 * (c) Denali 2003-2005
 */
package org.openflexo.foundation.wkf.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;

public abstract class RemoveFromXRole<R extends RemoveFromXRole<R>> extends FlexoAction<R, Role, AbstractActivityNode> {

	private static final Logger logger = Logger.getLogger(RemoveFromXRole.class.getPackage().getName());

	RemoveFromXRole(FlexoActionType<R, Role, AbstractActivityNode> actionType, Role focusedObject,
			Vector<AbstractActivityNode> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
