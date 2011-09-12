/*
 * Created on 16 mar. 2006 by sguerin
 *
 * Flexo Application Suite
 * (c) Denali 2003-2005
 */
package org.openflexo.foundation.wkf.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;

public abstract class AddToXRole<A extends AddToXRole<A>> extends FlexoAction<A, AbstractActivityNode, AbstractActivityNode> {

	private Role role;

	AddToXRole(FlexoActionType<A, AbstractActivityNode, AbstractActivityNode> actionType, AbstractActivityNode focusedObject,
			Vector<AbstractActivityNode> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
