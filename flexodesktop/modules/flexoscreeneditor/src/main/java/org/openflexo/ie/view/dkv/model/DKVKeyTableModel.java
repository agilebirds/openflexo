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
package org.openflexo.ie.view.dkv.model;

import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractColumn;
import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dkv.Language;
import org.openflexo.foundation.dkv.Value;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DKVKeyTableModel extends AbstractModel<Domain, Key> {

	protected static final Logger logger = Logger.getLogger(DKVKeyTableModel.class.getPackage().getName());

	private DKVModel _dkvModel;

	public DKVKeyTableModel(DKVModel dkvModel, Domain domain, FlexoProject project) {
		super(domain, project);
		_dkvModel = dkvModel;
		if (domain != null) {
			domain.getKeyList().addObserver(this);
		}
		addToColumns(new IconColumn<Key>("domain_icon", 30) {
			@Override
			public Icon getIcon(Key object) {
				return DMEIconLibrary.KEY_ICON;
			}
		});
		addToColumns(new StringColumn<Key>("key", 150) {
			@Override
			public String getValue(Key key) {
				return key.getName();
			}

		});
		for (Language language : getDKVModel().getLanguages()) {
			addToColumns(new LanguageValueColumn(language, 150));
		}
		addToColumns(new EditableStringColumn<Key>("description", 250) {
			@Override
			public String getValue(Key key) {
				return key.getDescription();
			}

			@Override
			public void setValue(Key key, String aValue) {
				key.setDescription(aValue);
			}
		});
	}

	protected class LanguageValueColumn extends EditableStringColumn<Key> {
		Language _language;

		public LanguageValueColumn(Language language, int defaultWidth) {
			super("", defaultWidth);
			_language = language;
		}

		@Override
		public String getValue(Key key) {
			if (_language != null && key.getDomain() != null) {
				Value value = key.getDomain().getValue(key, _language);
				if (value != null) {
					return value.getValue();
				}
			}
			return "";
		}

		@Override
		public void setValue(Key key, String aValue) {
			if (_language != null && key.getDomain() != null) {
				key.getDomain().putValueForLanguage(key, aValue, _language);
			}
		}

		@Override
		public String getLocalizedTitle() {
			return _language.getName() + " [" + _language.getIsoCode() + "]";
		}

	}

	public Domain getDomain() {
		return getModel();
	}

	public DKVModel getDKVModel() {
		return _dkvModel;
	}

	@Override
	public Key elementAt(int row) {
		if (row >= 0 && row < getRowCount()) {
			return (Key) getDomain().getKeyList().elementAt(row);
		} else {
			return null;
		}
	}

	public Key keyAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getDomain() != null) {
			return getDomain().getKeyList().getKeyList().length;
		}
		return 0;
	}

	public void notifyLanguageAdded(Language addedLanguage) {
		// logger.info("Add column");
		insertColumnAtIndex(new LanguageValueColumn(addedLanguage, 150), getColumnCount() - 1);
		fireTableStructureChanged();
	}

	public void notifyLanguageRemoved(Language removedLanguage) {
		// logger.info("Remove column");
		LanguageValueColumn searchedColumn = null;
		for (int i = 0; i < getColumnCount(); i++) {
			AbstractColumn col = columnAt(i);
			if (col != null && col instanceof LanguageValueColumn && ((LanguageValueColumn) col)._language == removedLanguage) {
				searchedColumn = (LanguageValueColumn) col;
			}
		}
		if (searchedColumn != null) {
			removeFromColumns(searchedColumn);
			fireTableStructureChanged();
		}
	}

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.components.tabular.model.AbstractModel#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification.propertyName() != null && dataModification.propertyName().equals("index")) {
			int firstRow = 0;
			int lastRow = getRowCount() - 1;
			if (dataModification.oldValue() != null && dataModification.newValue() != null) {
				if ((Integer) dataModification.oldValue() < (Integer) dataModification.newValue()) {
					firstRow = ((Integer) dataModification.oldValue()).intValue() - 1;
					lastRow = ((Integer) dataModification.newValue()).intValue() - 1;
				} else if ((Integer) dataModification.oldValue() > (Integer) dataModification.newValue()) {
					firstRow = ((Integer) dataModification.newValue()).intValue() - 1;
					lastRow = ((Integer) dataModification.oldValue()).intValue() - 1;
				} else {
					// nothing has changed
					return;
				}
			}
			fireTableRowsUpdated(firstRow, lastRow);
			return;
		}
		super.update(observable, dataModification);
	}
}
