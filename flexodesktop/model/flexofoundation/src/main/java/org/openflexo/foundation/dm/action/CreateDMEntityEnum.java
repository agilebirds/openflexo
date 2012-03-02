package org.openflexo.foundation.dm.action;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.ComponentRepository;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.ProcessInstanceRepository;
import org.openflexo.foundation.dm.WORepository;

import java.util.Vector;
import java.util.logging.Logger;

public class CreateDMEntityEnum extends FlexoAction {

	private static final Logger logger = Logger.getLogger(CreateDMEntityEnum.class.getPackage().getName());

	public static FlexoActionType actionType = new FlexoActionType("add_enum", FlexoActionType.newMenu, FlexoActionType.defaultGroup,
			FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new CreateDMEntityEnum(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) {
			return ((object != null) && (object instanceof DMPackage) && ((DMPackage) object).getRepository() != null)
					&& (!(((DMPackage) object).getRepository() instanceof ComponentRepository))
					&& (!(((DMPackage) object).getRepository() instanceof WORepository))
					&& (!(((DMPackage) object).getRepository() instanceof ProcessInstanceRepository))
					&& (!((DMPackage) object).getRepository().isReadOnly());
		}

	};

	private DMPackage _package;
	private String _newEntityName;
	private DMEntity _newEntity;

	CreateDMEntityEnum(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("CreateDMEntity");
		if (getPackage() != null) {
			_newEntityName = _package.getDMModel().getNextDefautEnumName(_package);
			_newEntity = new DMEntity(_package.getDMModel(), _newEntityName, _package.getName(), _newEntityName, null);
			_newEntity.setIsEnumeration(true);
            getRepository().registerEntity(_newEntity);
		}
	}

	public String getNewEntityName() {
		return _newEntityName;
	}

	public DMPackage getPackage() {
		if (_package == null) {
			if ((getFocusedObject() != null) && (getFocusedObject() instanceof DMPackage)) {
				_package = (DMPackage) getFocusedObject();
			}
		}
		return _package;
	}

	public DMRepository getRepository() {
		return getPackage().getRepository();
	}

	public DMEntity getNewEntity() {
		return _newEntity;
	}

}
