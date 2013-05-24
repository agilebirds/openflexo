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
package org.openflexo.foundation.dm.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.JDKRepository;
import org.openflexo.foundation.dm.LoadableDMEntity;

public class ImportJDKEntity extends FlexoAction<ImportJDKEntity, DMObject, DMObject> {

	private static final Logger logger = Logger.getLogger(ImportJDKEntity.class.getPackage().getName());

	public static FlexoActionType<ImportJDKEntity, DMObject, DMObject> actionType = new FlexoActionType<ImportJDKEntity, DMObject, DMObject>(
			"import_class", FlexoActionType.importMenu, FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public ImportJDKEntity makeNewAction(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
			return new ImportJDKEntity(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return object != null
					&& (object instanceof JDKRepository || object instanceof DMPackage
							&& ((DMPackage) object).getRepository() instanceof JDKRepository);
		}

		@Override
		public boolean isEnabledForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return isVisibleForSelection(object, globalSelection);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, JDKRepository.class);
		FlexoModelObject.addActionForClass(actionType, DMPackage.class);
	}

	ImportJDKEntity(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private String packageName;
	private String className;
	private boolean _importGetOnlyProperties = false;
	private boolean _importMethods = false;

	private DMEntity _newEntity;

	@Override
	protected void doAction(Object context) {
		logger.info("ImportJDKEntity");
		if (getJDKRepository() != null) {
			Class<?> importedClass = getClassToImport();
			if (importedClass != null) {
				_newEntity = LoadableDMEntity.createLoadableDMEntity(getJDKRepository(), importedClass, getImportGetOnlyProperties(),
						getImportMethods());
			}
		}
	}

	public Class<?> getClassToImport() {
		try {
			return Class.forName(packageName + "." + className);
		} catch (ClassNotFoundException e) {
			return null;
		}

	}

	public JDKRepository getJDKRepository() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getDMModel().getJDKRepository();
		}
		return null;
	}

	public DMEntity getNewEntity() {
		return _newEntity;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean getImportGetOnlyProperties() {
		return _importGetOnlyProperties;
	}

	public void setImportGetOnlyProperties(boolean importGetOnlyProperties) {
		_importGetOnlyProperties = importGetOnlyProperties;
	}

	public boolean getImportMethods() {
		return _importMethods;
	}

	public void setImportMethods(boolean importMethods) {
		_importMethods = importMethods;
	}

}
