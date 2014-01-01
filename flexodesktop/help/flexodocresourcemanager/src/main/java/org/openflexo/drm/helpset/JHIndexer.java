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
package org.openflexo.drm.helpset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.java.help.search.Indexer;

public class JHIndexer {

	private static final Logger logger = Logger.getLogger(DRMHelpSet.class.getPackage().getName());

	private static final String JAVA_HELP_SEARCH = "JavaHelpSearch";

	private final File _helpsetDirectory;
	private final Vector<String> indexerFileList = new Vector<String>();

	public JHIndexer(File helpsetDirectory) {
		_helpsetDirectory = helpsetDirectory;
	}

	// Use only one indexer in JVM, otherwise
	// produce anything useful but bunches of “ConfigFile and/or IndexBuilder not set”-exceptions
	private static Indexer ixr = new Indexer();

	/**
	 * create a search database for full text searching
	 */
	public void generate() {
		try {
			File dbDir = new File(_helpsetDirectory.getAbsolutePath() + File.separator + JAVA_HELP_SEARCH);
			if (dbDir.exists()) {
				dbDir.delete();
			}
			File confFile = writeConfigFile(_helpsetDirectory.getAbsolutePath());
			ixr.compile(getIndexerArguments(dbDir, confFile));
			confFile.delete();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * create a configuration file for the Indexer call
	 */
	private File writeConfigFile(String prjDir) {
		String lineSeparator = System.getProperty("line.separator");
		OutputStream fw = null;
		File file = new File(prjDir + File.separator + "ixrConf.txt");
		if (file.exists()) {
			file.delete();
		}
		String[] files = getIndexerFiles(prjDir);
		try {
			fw = new FileOutputStream(file);
			String attribute = "IndexRemove " + prjDir + File.separator + lineSeparator;
			attribute = attribute.replace('\\', '/');
			fw.write(attribute.getBytes());
			for (int i = 0; i < files.length; i++) {
				attribute = "File " + files[i] + lineSeparator;
				fw.write(attribute.getBytes());
			}
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * create a list of files and directories that are targets for a full text search
	 */
	private String[] getIndexerFiles(String dir) {

		File[] files = new File(dir).listFiles();
		String fName = null;
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				fName = files[i].getAbsolutePath();
				if (files[i].isDirectory()) {
					getIndexerFiles(files[i].getAbsolutePath());
				} else if (fName.endsWith(".htm") || fName.endsWith(".html")) {
					if (!fName.endsWith("index.htm") && !fName.endsWith("toc.htm") && !fName.endsWith("images")
							&& !fName.endsWith("JavaHelpSearch")) {
						String added = fName.replace('\\', '/');
						// logger.info("Added: "+added);
						indexerFileList.addElement(added);
					}
				}
			}
		}
		return indexerFileList.toArray(new String[0]);
	}

	/**
	 * create a list of options for the Indexer call
	 */
	private String[] getIndexerArguments(File dbDir, File cFile) {
		Vector<String> list = new Vector<String>(0);
		String arg = null;
		list.addElement("-c");
		arg = cFile.getAbsolutePath();
		list.addElement(arg.replace('\\', '/'));
		list.addElement("-db");
		arg = dbDir.getAbsolutePath() + File.separator;
		list.addElement(arg.replace('\\', '/'));
		if (logger.isLoggable(Level.FINER)) {
			list.addElement("-verbose");
		}
		return list.toArray(new String[0]);
	}
}
