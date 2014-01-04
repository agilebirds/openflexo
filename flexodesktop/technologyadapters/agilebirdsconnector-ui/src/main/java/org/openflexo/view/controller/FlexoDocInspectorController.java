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
package org.openflexo.view.controller;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import javax.swing.BorderFactory;

import org.openflexo.ch.DefaultInspectorHelpDelegate;
import org.openflexo.ch.DocResourceManager;
import org.openflexo.foundation.Inspectors;
import org.openflexo.inspector.InspectorSinglePanel;
import org.openflexo.inspector.model.InspectorModel;
import org.openflexo.inspector.model.TabModel;

public class FlexoDocInspectorController extends FlexoInspectorController {

	private static final Logger logger = Logger.getLogger(FlexoDocInspectorController.class.getPackage().getName());

	protected InspectorSinglePanel _docInspectorPanel;

	protected FlexoDocInspectorController(FlexoController controller) {
		super(controller.new FlexoControllerInspectorDelegate(), new DefaultInspectorHelpDelegate(DocResourceManager.instance()));
		if (getDocTabModel() != null) {
			_docInspectorPanel = createInspectorSinglePanel(getDocTabModel());
			_docInspectorPanel.setBorder(BorderFactory.createEmptyBorder());
		}
	}

	public InspectorSinglePanel getDocInspectorPanel() {
		return _docInspectorPanel;
	}

	private TabModel _docTabModel;

	private TabModel getDocTabModel() {
		if (_docTabModel == null) {
			try {
				InspectorModel docInspectorModel = importInspectorFile(Inspectors.getDocInspectorFile(/*getInspectorDirectory()*/));
				_docTabModel = docInspectorModel.getTabs().elements().nextElement();
			} catch (FileNotFoundException e) {
				logger.warning("DocForModelObject.inspector NOT FOUND");
			}

		}
		return _docTabModel;
	}

}
