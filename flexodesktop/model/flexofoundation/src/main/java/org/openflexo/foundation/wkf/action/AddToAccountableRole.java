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
import org.openflexo.foundation.wkf.node.AbstractActivityNode;

public class AddToAccountableRole extends AddToXRole<AddToAccountableRole> {

	private static final Logger logger = Logger.getLogger(AddToAccountableRole.class.getPackage().getName());

	public static FlexoActionType<AddToAccountableRole, AbstractActivityNode, AbstractActivityNode> actionType = new FlexoActionType<AddToAccountableRole, AbstractActivityNode, AbstractActivityNode>(
			"add_to_accountable_role") {

		/**
		 * Factory method
		 */
		@Override
		public AddToAccountableRole makeNewAction(AbstractActivityNode focusedObject, Vector<AbstractActivityNode> globalSelection,
				FlexoEditor editor) {
			return new AddToAccountableRole(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(AbstractActivityNode object, Vector<AbstractActivityNode> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(AbstractActivityNode object, Vector<AbstractActivityNode> globalSelection) {
			return object != null && object.getAvailableAccountableRoles().size() > 0;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, AbstractActivityNode.class);
	}

	AddToAccountableRole(AbstractActivityNode focusedObject, Vector<AbstractActivityNode> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateRoleException {
		if (getRole() != null) {
			getFocusedObject().addToAccountableRoles(getRole());
		}
	}

}
