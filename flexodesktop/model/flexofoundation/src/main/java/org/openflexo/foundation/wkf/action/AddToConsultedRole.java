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


public class AddToConsultedRole extends AddToXRole<AddToConsultedRole> {

	private static final Logger logger = Logger.getLogger(AddToConsultedRole.class.getPackage().getName());

	public static FlexoActionType<AddToConsultedRole, AbstractActivityNode, AbstractActivityNode> actionType = new FlexoActionType<AddToConsultedRole, AbstractActivityNode, AbstractActivityNode>(
	"add_to_consulted_role") {

		/**
		 * Factory method
		 */
		@Override
		public AddToConsultedRole makeNewAction(AbstractActivityNode focusedObject, Vector<AbstractActivityNode> globalSelection,
				FlexoEditor editor) {
			return new AddToConsultedRole(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(AbstractActivityNode object, Vector<AbstractActivityNode> globalSelection) {
			return false;
		}

		@Override
		protected boolean isEnabledForSelection(AbstractActivityNode object, Vector<AbstractActivityNode> globalSelection) {
			return object != null && object.getAvailableConsultedRoles().size() > 0;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, AbstractActivityNode.class);
	}

	AddToConsultedRole(AbstractActivityNode focusedObject, Vector<AbstractActivityNode> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateRoleException {
		if (getRole() != null) {
			getFocusedObject().addToConsultedRoles(getRole());
		}
	}

}
