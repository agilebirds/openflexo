/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.ie.cl.action;

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DuplicateEntityName;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.DuplicateComponentName;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.cl.IECLObject;
import org.openflexo.foundation.ie.cl.MonitoringScreenDefinition;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.cl.SingleWidgetComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.util.FolderType;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.StringRepresentable;

public class AddComponent extends FlexoAction<AddComponent, IECLObject, IECLObject> {

	private static final Logger logger = Logger.getLogger(AddComponent.class.getPackage().getName());

	public static FlexoActionType<AddComponent, IECLObject, IECLObject> actionType = new FlexoActionType<AddComponent, IECLObject, IECLObject>(
			"add_new_component", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddComponent makeNewAction(IECLObject focusedObject, Vector<IECLObject> globalSelection, FlexoEditor editor) {
			return new AddComponent(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(IECLObject object, Vector<IECLObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(IECLObject object, Vector<IECLObject> globalSelection) {
			return object instanceof FlexoComponentFolder || object instanceof ComponentDefinition
					|| object instanceof FlexoComponentLibrary;
		}

	};

	static {
		AgileBirdsObject.addActionForClass(actionType, FlexoComponentLibrary.class);
		AgileBirdsObject.addActionForClass(actionType, FlexoComponentFolder.class);
		AgileBirdsObject.addActionForClass(actionType, ComponentDefinition.class);
	}

	public enum ComponentType implements StringRepresentable {
		OPERATION_COMPONENT {
			/**
			 * Overrides getLocalizedName
			 * 
			 * @see org.openflexo.foundation.ie.cl.action.AddComponent.ComponentType#getLocalizedName()
			 */
			@Override
			public String getLocalizedName() {
				return FlexoLocalization.localizedForKey("operation_component");
			}
		},
		POPUP_COMPONENT {
			/**
			 * Overrides getLocalizedName
			 * 
			 * @see org.openflexo.foundation.ie.cl.action.AddComponent.ComponentType#getLocalizedName()
			 */
			@Override
			public String getLocalizedName() {
				return FlexoLocalization.localizedForKey("popup_component");
			}
		},
		PARTIAL_COMPONENT {
			/**
			 * Overrides getLocalizedName
			 * 
			 * @see org.openflexo.foundation.ie.cl.action.AddComponent.ComponentType#getLocalizedName()
			 */
			@Override
			public String getLocalizedName() {
				return FlexoLocalization.localizedForKey("partial_component");
			}
		},
		TAB_COMPONENT {
			/**
			 * Overrides getLocalizedName
			 * 
			 * @see org.openflexo.foundation.ie.cl.action.AddComponent.ComponentType#getLocalizedName()
			 */
			@Override
			public String getLocalizedName() {
				return FlexoLocalization.localizedForKey("tab_component");
			}
		},
		DATA_COMPONENT {
			/**
			 * Overrides getLocalizedName
			 * 
			 * @see org.openflexo.foundation.ie.cl.action.AddComponent.ComponentType#getLocalizedName()
			 */
			@Override
			public String getLocalizedName() {
				return FlexoLocalization.localizedForKey("data_component");
			}
		},
		MONITORING_SCREEN {
			/**
			 * Overrides getLocalizedName
			 * 
			 * @see org.openflexo.foundation.ie.cl.action.AddComponent.ComponentType#getLocalizedName()
			 */
			@Override
			public String getLocalizedName() {
				return FlexoLocalization.localizedForKey("monitoring_screen");
			}
		},
		MONITORING_COMPONENT {
			/**
			 * Overrides getLocalizedName
			 * 
			 * @see org.openflexo.foundation.ie.cl.action.AddComponent.ComponentType#getLocalizedName()
			 */
			@Override
			public String getLocalizedName() {
				return FlexoLocalization.localizedForKey("monitoring_component");
			}
		};

		public abstract String getLocalizedName();

		/**
		 * Overrides toString
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return getLocalizedName();
		}

	};

	private ComponentDefinition _newComponent;

	private String _newComponentName;
	private FlexoComponentFolder _folder;
	private ComponentType _componentType;
	private FlexoProcess _relatedProcess;
	private DMEntity _dataComponentEntity;

	AddComponent(IECLObject focusedObject, Vector<IECLObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParameterException,
			DuplicateComponentName, DuplicateEntityName {
		if (getFolder() == null) {
			throw new InvalidParameterException("folder is undefined");
		}
		if (getComponentType() == null) {
			throw new InvalidParameterException("component type is undefined");
		}
		if (getNewComponentName() == null) {
			throw new InvalidParameterException("component name is undefined");
		}
		if (getProject().getFlexoComponentLibrary().getComponentNamed(getNewComponentName()) != null) {
			throw new DuplicateComponentName(getNewComponentName());
		}
		if (getProject().getDataModel().getEntityNamed(DMPackage.DEFAULT_PACKAGE_NAME + "." + getNewComponentName()) != null) {
			throw new DuplicateEntityName(getNewComponentName());
		}

		logger.info("Add component");
		if (getComponentType() == ComponentType.OPERATION_COMPONENT) {
			_newComponent = new OperationComponentDefinition(getNewComponentName(), getFolder().getComponentLibrary(),
					getFolderUsedToCreateComponent(), getProject());
		} else if (getComponentType() == ComponentType.POPUP_COMPONENT) {
			_newComponent = new PopupComponentDefinition(getNewComponentName(), getFolder().getComponentLibrary(),
					getFolderUsedToCreateComponent(), getProject());
		} else if (getComponentType() == ComponentType.PARTIAL_COMPONENT) {
			_newComponent = new SingleWidgetComponentDefinition(getNewComponentName(), getFolder().getComponentLibrary(),
					getFolderUsedToCreateComponent(), getProject());
		} else if (getComponentType() == ComponentType.TAB_COMPONENT) {
			_newComponent = new TabComponentDefinition(getNewComponentName(), getFolder().getComponentLibrary(),
					getFolderUsedToCreateComponent(), getProject());
		} else if (getComponentType() == ComponentType.DATA_COMPONENT) {
			throw new NotImplementedException("DATA_COMPONENT not implemented yet");
		} else if (getComponentType() == ComponentType.MONITORING_SCREEN) {
			_newComponent = new MonitoringScreenDefinition(getNewComponentName(), getFolder().getComponentLibrary(),
					getFolderUsedToCreateComponent(), getProject(), _relatedProcess);
		} else if (getComponentType() == ComponentType.MONITORING_COMPONENT) {
			throw new NotImplementedException("MONITORING_COMPONENT not implemented yet");
		}
		// Creates the resource here
		_newComponent.getComponentResource();
	}

	public FlexoComponentFolder getFolderUsedToCreateComponent() {
		FlexoComponentFolder f = _folder;
		if (getComponentType() == ComponentType.OPERATION_COMPONENT) {
			f = _folder;
		} else if (getComponentType() == ComponentType.POPUP_COMPONENT) {
			f = _folder.getComponentLibrary().getRootFolder().getFolderTyped(FolderType.POPUP_FOLDER);
		} else if (getComponentType() == ComponentType.PARTIAL_COMPONENT) {
			f = _folder.getComponentLibrary().getRootFolder().getFolderTyped(FolderType.PARTIAL_COMPONENT_FOLDER);
		} else if (getComponentType() == ComponentType.TAB_COMPONENT) {
			f = _folder.getComponentLibrary().getRootFolder().getFolderTyped(FolderType.TAB_FOLDER);
		} else if (getComponentType() == ComponentType.MONITORING_SCREEN) {
			f = _folder.getComponentLibrary().getRootFolder().getFolderTyped(FolderType.MONITORING_SCREEN_FOLDER);
		}
		if (_folder != f && f.isFatherOf(_folder)) {
			return _folder;
		} else {
			return f;
		}
	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProject();
		}
		return null;
	}

	public ComponentDefinition getNewComponent() {
		return _newComponent;
	}

	public ComponentType getComponentType() {
		return _componentType;
	}

	public void setComponentType(ComponentType componentType) {
		_componentType = componentType;
	}

	public FlexoComponentFolder getFolder() {
		if (_folder == null) {
			if (getFocusedObject() != null && getFocusedObject() instanceof ComponentDefinition) {
				_folder = ((ComponentDefinition) getFocusedObject()).getFolder();
			} else if (getFocusedObject() != null && getFocusedObject() instanceof FlexoComponentFolder) {
				_folder = (FlexoComponentFolder) getFocusedObject();
			} else if (getFocusedObject() != null && getFocusedObject() instanceof FlexoComponentLibrary) {
				_folder = ((FlexoComponentLibrary) getFocusedObject()).getRootFolder();
			}

		}
		return _folder;
	}

	public void setFolder(FlexoComponentFolder folder) {
		_folder = folder;
	}

	public String getNewComponentName() {
		return _newComponentName;
	}

	public void setNewComponentName(String newComponentName) {
		_newComponentName = newComponentName;
	}

	public FlexoProcess getRelatedProcess() {
		return _relatedProcess;
	}

	public void setRelatedProcess(FlexoProcess selectedProcess) {
		_relatedProcess = selectedProcess;
	}

	public DMEntity getDataComponentEntity() {
		return _dataComponentEntity;
	}

	public void setDataComponentEntity(DMEntity entity) {
		_dataComponentEntity = entity;
	}

}