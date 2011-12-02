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
package org.openflexo.cgmodule.view;

import org.openflexo.cgmodule.controller.GeneratorController;
import org.openflexo.cgmodule.controller.browser.GeneratorBrowser;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.components.tabularbrowser.TabularBrowserModel;
import org.openflexo.components.tabularbrowser.TabularBrowserView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.version.AbstractCGFileVersion;

public class FileHistoryBrowserView extends TabularBrowserView {

	private CGFile _cgFile;

	public FileHistoryBrowserView(GeneratorController controller, CGFile cgFile, int visibleRowCount) {
		super(controller, makeTabularBrowserModel(cgFile), visibleRowCount, controller.getEditor());
		_cgFile = cgFile;
		setVisibleRowCount(visibleRowCount);
		setSynchronizeWithSelectionManager(true);
	}

	private static TabularBrowserModel makeTabularBrowserModel(CGFile cgFile) {
		TabularBrowserModel model = new TabularBrowserModel(GeneratorBrowser.makeBrowserConfigurationForFileHistory(cgFile), " ", 300);
		model.addToColumns(new StringColumn<CGObject>("kind", 150) {
			@Override
			public String getValue(CGObject object) {
				if (object instanceof AbstractCGFileVersion) {
					return ((AbstractCGFileVersion) object).getVersionId().typeAsString();
				}
				return "";
			}
		});
		model.addToColumns(new StringColumn<CGObject>("date", 200) {
			@Override
			public String getValue(CGObject object) {
				if (object instanceof AbstractCGFileVersion) {
					return ((AbstractCGFileVersion) object).getDateAsString();
				}
				return "";
			}
		});
		model.addToColumns(new StringColumn<CGObject>("user", 80) {
			@Override
			public String getValue(CGObject object) {
				if (object instanceof AbstractCGFileVersion) {
					return ((AbstractCGFileVersion) object).getUserId();
				}
				return "";
			}
		});
		model.addToColumns(new StringColumn<CGObject>("description", 400) {
			@Override
			public String getValue(CGObject object) {
				return object.getDescription();
			}
		});
		return model;
	}

	@Override
	public boolean mayRepresents(FlexoModelObject anObject) {
		if (anObject instanceof CGFile) {
			return (anObject == _cgFile);
		}
		if (anObject instanceof AbstractCGFileVersion) {
			return (((AbstractCGFileVersion) anObject).getCGFile() == _cgFile);
		}
		return false;
	}

}
