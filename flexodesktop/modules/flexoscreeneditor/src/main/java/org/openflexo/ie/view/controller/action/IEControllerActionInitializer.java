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

	private IEController _ieController;

	public IEControllerActionInitializer(IEController controller) {
		super(controller);
		_ieController = controller;
	}

	protected IEController getIEController() {
		return _ieController;
	}

	protected IESelectionManager getIESelectionManager() {
		return getIEController().getIESelectionManager();
	}

	@Override
	public void initializeActions() {
		super.initializeActions();

		new IESetPropertyInitializer(this).init();

		new IECopyInitializer(this).init();
		new IECutInitializer(this).init();
		new IEPasteInitializer(this).init();
		new IEDeleteInitializer(this).init();
		new IESelectAllInitializer(this).init();

		new AddTabInitializer(this).init();
		new MoveTabLeftInitializer(this).init();
		new MoveTabRightInitializer(this).init();

		// (new MakePartialComponentInitializer(this)).init();

		new InsertRowAfterInitializer(this).init();
		new InsertRowBeforeInitializer(this).init();
		new InsertColAfterInitializer(this).init();
		new InsertColBeforeInitializer(this).init();

		new AddComponentInitializer(this).init();
		new AddComponentFolderInitializer(this).init();
		new DuplicateComponentInitializer(this).init();

		new IncreaseColSpanInitializer(this).init();
		new IncreaseRowSpanInitializer(this).init();
		new DecreaseRowSpanInitializer(this).init();
		new DecreaseColSpanInitializer(this).init();

		new SuroundWithConditionalInitializer(this).init();
		new SuroundWithRepetitionInitializer(this).init();
		new UnwrapConditionalInitializer(this).init();
		new UnwrapRepetitionInitializer(this).init();

		new DeleteRowInitializer(this).init();
		new DeleteColInitializer(this).init();

		new TopComponentUpInitializer(this).init();
		new TopComponentDownInitializer(this).init();

		new AddMenuInitializer(this).init();
		new MoveMenuUpperInitializer(this).init();
		new MoveMenuUpInitializer(this).init();
		new MoveMenuDownInitializer(this).init();

		new ExportToPaletteInitializer(this).init();

		new DropIEElementInitializer(this).init();
		new MoveIEElementInitializer(this).init();
		new LabelizeComponentInitializer(this).init();

		new PrintComponentInitializer(this).init();

		// DKV
		new AddLanguageInitializer(this).init();
		new AddDomainInitializer(this).init();
		new AddKeyInitializer(this).init();
		new DKVDeleteInitializer(this).init();

		new ImportImageInitializer(this).init();

		new EditUserHelpInitializer(this).init();

		new GenerateEntityFromSelectionInitializer(this).init();

		// GUI action
		new ShowComponentUsageInitializer(this).init();
	}

}
