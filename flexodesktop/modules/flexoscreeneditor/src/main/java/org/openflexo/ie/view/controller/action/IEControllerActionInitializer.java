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
package org.openflexo.ie.view.controller.action;

import java.util.logging.Logger;

import org.openflexo.action.ImportImageInitializer;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.controller.IESelectionManager;
import org.openflexo.view.controller.ControllerActionInitializer;

public class IEControllerActionInitializer extends ControllerActionInitializer {

	protected static final Logger logger = Logger.getLogger(IEControllerActionInitializer.class.getPackage().getName());

	public IEControllerActionInitializer(IEController controller) {
		super(controller);
	}

	protected IEController getIEController() {
		return (IEController) getController();
	}

	protected IESelectionManager getIESelectionManager() {
		return getIEController().getIESelectionManager();
	}

	@Override
	public void initializeActions() {
		super.initializeActions();

		new IESetPropertyInitializer(this);

		new IECopyInitializer(this);
		new IECutInitializer(this);
		new IEPasteInitializer(this);
		new IEDeleteInitializer(this);
		new IESelectAllInitializer(this);

		new AddTabInitializer(this);
		new MoveTabLeftInitializer(this);
		new MoveTabRightInitializer(this);

		// (new MakePartialComponentInitializer(this));

		new InsertRowAfterInitializer(this);
		new InsertRowBeforeInitializer(this);
		new InsertColAfterInitializer(this);
		new InsertColBeforeInitializer(this);

		new AddComponentInitializer(this);
		new AddComponentFolderInitializer(this);
		new DuplicateComponentInitializer(this);

		new IncreaseColSpanInitializer(this);
		new IncreaseRowSpanInitializer(this);
		new DecreaseRowSpanInitializer(this);
		new DecreaseColSpanInitializer(this);

		new SuroundWithConditionalInitializer(this);
		new SuroundWithRepetitionInitializer(this);
		new UnwrapConditionalInitializer(this);
		new UnwrapRepetitionInitializer(this);

		new DeleteRowInitializer(this);
		new DeleteColInitializer(this);

		new TopComponentUpInitializer(this);
		new TopComponentDownInitializer(this);

		new AddMenuInitializer(this);
		new MoveMenuUpperInitializer(this);
		new MoveMenuUpInitializer(this);
		new MoveMenuDownInitializer(this);

		new ExportToPaletteInitializer(this);

		new DropIEElementInitializer(this);
		new MoveIEElementInitializer(this);
		new LabelizeComponentInitializer(this);

		new PrintComponentInitializer(this);

		// DKV
		new AddLanguageInitializer(this);
		new AddDomainInitializer(this);
		new AddKeyInitializer(this);
		new DKVDeleteInitializer(this);

		new ImportImageInitializer(this);

		new EditUserHelpInitializer(this);

		new GenerateEntityFromSelectionInitializer(this);

		// GUI action
		new ShowComponentUsageInitializer(this);

	}

}
