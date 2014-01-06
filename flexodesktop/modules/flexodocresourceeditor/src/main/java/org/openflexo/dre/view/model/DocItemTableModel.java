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
package org.openflexo.dre.view.model;

import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.icon.DREIconLibrary;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DocItemTableModel extends AbstractModel<DocItemFolder, DocItem> {

	protected static final Logger logger = Logger.getLogger(DocItemTableModel.class.getPackage().getName());

	public DocItemTableModel(DocItemFolder folder, FlexoProject project) {
		super(folder, project);
		addToColumns(new IconColumn<DocItem>("item_icon", 30) {
			@Override
			public Icon getIcon(DocItem item) {
				return DREIconLibrary.DOC_ITEM_ICON;
			}
		});

		addToColumns(new EditableStringColumn<DocItem>("documentation_items", 200) {
			@Override
			public String getValue(DocItem item) {
				return item.getIdentifier();
			}

			@Override
			public void setValue(DocItem item, String aValue) {
				item.setIdentifier(aValue);
			}
		});
		addToColumns(new EditableStringColumn<DocItem>("description", 270) {
			@Override
			public String getValue(DocItem item) {
				return item.getDescription();
			}

			@Override
			public void setValue(DocItem item, String aValue) {
				item.setDescription(aValue);
			}
		});

	}

	public DocItemFolder getDocItemFolder() {
		return getModel();
	}

	@Override
	public DocItem elementAt(int row) {
		if (row >= 0 && row < getRowCount()) {
			return getDocItemFolder().getItems().elementAt(row);
		} else {
			return null;
		}
	}

	public DocItem folderAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getDocItemFolder() != null) {
			return getDocItemFolder().getItems().size();
		}
		return 0;
	}

}
