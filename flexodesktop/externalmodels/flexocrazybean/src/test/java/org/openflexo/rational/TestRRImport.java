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
package org.openflexo.rational;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.dm.action.ImportRationalRoseRepository;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;

public class TestRRImport extends FlexoTestCase {

	private static final Logger logger = FlexoLogger.getLogger(TestRRImport.class.getPackage().getName());

	private static final File firstModel = new FileResource("aom_ed4.mdl");
	private static final File secondModel = new FileResource("IAABOM Sample.mdl");

	public TestRRImport(String name) {
		super(name);
	}

	public void testRationalRoseImport() throws Exception {
		FlexoEditor editor = createProject("TestRationalRoseImport");
		ImportRationalRoseRepository importRR = ImportRationalRoseRepository.actionType.makeNewAction(editor.getProject().getDataModel(),
				null, editor);
		importRR.setRationalRoseFile(firstModel);
		importRR.setRationalRosePackageName("aom.ed4");
		importRR.setNewRepositoryName(firstModel.getName());
		importRR.doAction();
		assertTrue(importRR.hasActionExecutionSucceeded());
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Imported " + importRR.getNewRepository().getEntities().size() + " entities into "
					+ importRR.getRationalRosePackageName());
		}
		importRR = ImportRationalRoseRepository.actionType.makeNewAction(editor.getProject().getDataModel(), null, editor);
		importRR.setRationalRoseFile(secondModel);
		importRR.setRationalRosePackageName("iaabom");
		importRR.setNewRepositoryName(secondModel.getName());
		importRR.doAction();
		assertTrue(importRR.hasActionExecutionSucceeded());
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Imported " + importRR.getNewRepository().getEntities().size() + " entities into "
					+ importRR.getRationalRosePackageName());
		}
		editor.getProject().close();
		FileUtils.deleteDir(editor.getProject().getProjectDirectory());
	}

}
