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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.DuplicateRoleException;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;

public class RemoveFromAccountableRole extends RemoveFromXRole<RemoveFromAccountableRole> {

	private static final Logger logger = Logger.getLogger(RemoveFromAccountableRole.class.getPackage().getName());

	public static FlexoActionType<RemoveFromAccountableRole, Role, AbstractActivityNode> actionType = new FlexoActionType<RemoveFromAccountableRole, Role, AbstractActivityNode>(
	"remove_from_accountable_role") {

		/**
		 * Factory method
		 */
		@Override
		public RemoveFromAccountableRole makeNewAction(Role focusedObject, Vector<AbstractActivityNode> globalSelection,
				FlexoEditor editor) {
			return new RemoveFromAccountableRole(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(Role object, Vector<AbstractActivityNode> globalSelection) {
			return false;
		}

		@Override
		protected boolean isEnabledForSelection(Role object, Vector<AbstractActivityNode> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, Role.class);
	}

	RemoveFromAccountableRole(Role focusedObject, Vector<AbstractActivityNode> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateRoleException {
		for (AbstractActivityNode a : getGlobalSelection()) {
			a.removeFromAccountableRoles(getFocusedObject());
		}
	}

}
