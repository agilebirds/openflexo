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
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.DocType;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.DocResourceCenter;
import org.openflexo.foundation.KVCFlexoObject;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;

public class DRMHelpSet extends KVCFlexoObject {

	private static final Logger logger = Logger.getLogger(DRMHelpSet.class.getPackage().getName());

	public static final String TOP_LEVEL_FOLDER = "toplevelfolder";
	public static final String TOP_LEVEL_IMAGE = "images/toplevel.gif";

	public String version = "1.0";
	public String title;
	public Maps maps;
	public Vector views;
	public Vector presentations;

	public View toc;
	public View index;
	public View search;

	private File helpSetDirectory;

	private final DocResourceCenter _drc;
	private final HSIndex _hsIndex;
	private final HSMap _hsMap;
	private final HSToc _hsToc;

	private final String baseName;
	private final HelpSetConfiguration configuration;

	public DRMHelpSet(DocResourceCenter drc, File directory, String baseName, HelpSetConfiguration config) {
		super();
		_drc = drc;
		configuration = config;
		Language language = config.getLanguage();
		org.openflexo.localization.Language lang = org.openflexo.localization.Language.get(language.getIdentifier());

		this.baseName = baseName;
		String helpSetDirectoryName = baseName + "_" + config.getDistributionName() + "_" + language.getIdentifier() + ".helpset";
		helpSetDirectory = new File(directory, helpSetDirectoryName);
		try {
			helpSetDirectory = helpSetDirectory.getCanonicalFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.title = config.getTitle();

		_hsIndex = new HSIndex(_drc, language, getIndexFile(), configuration);
		_hsMap = new HSMap(_drc, language, getMapFile(), configuration);
		_hsToc = new HSToc(_drc, language, getTocFile(), configuration);

		maps = new Maps();
		views = new Vector();
		presentations = new Vector();
		toc = new View("TOC", FlexoLocalization.localizedForKeyAndLanguage("table_of_content", lang), "javax.help.TOCView",
				new View.ViewData(getTocFile().getName(), null));
		index = new View("Index", FlexoLocalization.localizedForKeyAndLanguage("index", lang), "javax.help.IndexView", new View.ViewData(
				getIndexFile().getName(), null));
		search = new View("Search", FlexoLocalization.localizedForKeyAndLanguage("search", lang), "javax.help.SearchView",
				new View.ViewData("JavaHelpSearch", "com.sun.java.help.search.DefaultSearchEngine"));
		views.add(toc);
		views.add(index);
		views.add(search);

		Presentation primaryPresentation = new Presentation("primary", new Presentation.Size(800, 600),
				new Presentation.Location(100, 100), title, TOP_LEVEL_FOLDER);
		primaryPresentation.toolbar = new Presentation.Toolbar();
		primaryPresentation.toolbar.add("javax.help.BackAction");
		primaryPresentation.toolbar.add("javax.help.ForwardAction");
		primaryPresentation.toolbar.add("javax.help.SeparatorAction");
		primaryPresentation.toolbar.add("javax.help.HomeAction");
		primaryPresentation.toolbar.add("javax.help.ReloadAction");
		primaryPresentation.toolbar.add("javax.help.SeparatorAction");
		primaryPresentation.toolbar.add("javax.help.PrintAction");
		primaryPresentation.toolbar.add("javax.help.PrintSetupAction");
		primaryPresentation.isDefault = true;

		Presentation mainPresentation = new Presentation("main", new Presentation.Size(800, 600), new Presentation.Location(100, 100),
				title, null);

		presentations.add(primaryPresentation);
		presentations.add(mainPresentation);

	}

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey("help_set_for_distribution") + " " + configuration.getDistributionName() + " "
				+ FlexoLocalization.localizedForKey("and_language") + " " + configuration.getLanguage().getLocalizedName();
	}

	public String getDistributionName() {
		return configuration.getDistributionName();
	}

	public Language getLanguage() {
		return configuration.getLanguage();
	}

	public class Maps extends KVCFlexoObject {
		public String homeID = _hsToc.getRootEntry().docItem.getIdentifier();
		public MapRef mapref = new MapRef();

		public class MapRef extends KVCFlexoObject {
			public String location = getMapFile().getName();
		}
	}

	public static class View extends KVCFlexoObject {
		public String name;
		public String label;
		public String type;
		public ViewData viewData;

		public static class ViewData extends KVCFlexoObject {
			public String dataType;
			public String engine;

			public ViewData(String dataType, String engine) {
				this.dataType = dataType;
				this.engine = engine;
			}
		}

		public View(String name, String label, String type, ViewData viewData) {
			this.name = name;
			this.label = label;
			this.type = type;
			this.viewData = viewData;
		}
	}

	public static class Presentation extends KVCFlexoObject {
		public boolean isDefault = false;
		public boolean displayviewimages = false;
		public String name;
		public Size size;
		public Location location;
		public String title;
		public String image;
		public Toolbar toolbar;

		public static class Location extends KVCFlexoObject {
			public int x;
			public int y;

			public Location(int x, int y) {
				this.x = x;
				this.y = y;
			}
		}

		public static class Size extends KVCFlexoObject {
			public int width;
			public int height;

			public Size(int width, int height) {
				this.width = width;
				this.height = height;
			}
		}

		public static class Toolbar extends KVCFlexoObject {
			public Vector helpActions;

			public Toolbar() {
				helpActions = new Vector();
			}

			public void add(String helpAction) {
				helpActions.add(new HelpAction(helpAction));
			}

			public static class HelpAction extends KVCFlexoObject {
				public String actionName;

				public HelpAction(String helpAction) {
					actionName = helpAction;
				}
			}
		}

		public Presentation(String name, Size size, Location location, String title, String image) {
			this.name = name;
			this.size = size;
			this.location = location;
			this.title = title;
			this.image = image;
			// toolbar = new Toolbar();
		}

	}

	private File _hsFile = null;
	private File _indexFile = null;
	private File _tocFile = null;
	private File _mapFile = null;

	public String getBaseName() {
		return helpSetDirectory.getName().substring(0, helpSetDirectory.getName().lastIndexOf(".helpset"));
	}

	public File getHSFile() {
		if (_hsFile == null) {
			_hsFile = new File(helpSetDirectory, getBaseName() + ".hs");
		}
		return _hsFile;
	}

	public File getIndexFile() {
		if (_indexFile == null) {
			_indexFile = new File(helpSetDirectory, getBaseName() + "Index.xml");
		}
		return _indexFile;
	}

	public File getTocFile() {
		if (_tocFile == null) {
			_tocFile = new File(helpSetDirectory, getBaseName() + "TOC.xml");
		}
		return _tocFile;
	}

	public File getMapFile() {
		if (_mapFile == null) {
			_mapFile = new File(helpSetDirectory, "Map.jhm");
		}
		return _mapFile;
	}

	public void generate(FlexoProgress progress) {
		if (logger.isLoggable(Level.FINE)) {
			for (DocItemFolder docItemFolder : configuration.getDocItemFolders()) {
				logger.fine("Consider DocItemFolder: " + docItemFolder.getIdentifier());
			}
		}

		if (progress != null) {
			progress.resetSecondaryProgress(9);
		}

		if (progress != null) {
			progress.setSecondaryProgress(FlexoLocalization.localizedForKey("creating_directory"));
		}
		helpSetDirectory.mkdirs();

		if (progress != null) {
			progress.setSecondaryProgress(FlexoLocalization.localizedForKey("generate_helpset_file"));
		}
		generateHSFile();

		if (progress != null) {
			progress.setSecondaryProgress(FlexoLocalization.localizedForKey("generate_table_of_contents"));
		}
		_hsToc.generate();

		if (progress != null) {
			progress.setSecondaryProgress(FlexoLocalization.localizedForKey("generate_index"));
		}
		_hsIndex.generate();

		if (progress != null) {
			progress.setSecondaryProgress(FlexoLocalization.localizedForKey("generate_map"));
		}
		_hsMap.generate();// This will create HTML file also!
		try {
			FileUtils.copyFileToDir(new FileResource("Resources/FlexoHelpMasterStyle.css"), new File(getHelpSetDirectory(), "HTML/"
					+ _drc.getFolder().getIdentifier() + "/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (progress != null) {
			progress.setSecondaryProgress(FlexoLocalization.localizedForKey("indexing_helpset"));
		}
		new JHIndexer(helpSetDirectory).generate();
		if (progress != null) {
			progress.setSecondaryProgress(FlexoLocalization.localizedForKey("copying_images"));
		}
		copyImages();
	}

	private void copyImages() {

		Vector<String> v = new Vector<String>();
		try {
			v.add(_drc.getFolder().getDirectory().getCanonicalPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			scanForImages(_drc.getFolder().getDirectory().listFiles(), _drc.getFolder(), v, 0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (DocItemFolder folder : configuration.getDocItemFolders()) {
			try {
				scanForImages(folder.getDirectory().listFiles(), folder, v, 5);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (File f : _drc.getFolder().getDirectory().listFiles()) {
			if (f.isDirectory()) {
				try {
					v.add(f.getCanonicalPath());
					if (!f.getCanonicalFile().equals(_drc.getFTSFolder().getDirectory().getCanonicalFile())
							&& !f.getCanonicalFile().equals(_drc.getModelFolder().getDirectory().getCanonicalFile())) {
						scanForImages(f.listFiles(), _drc.getFolder(), v, 5);
					} else {
						scanForImages(f.listFiles(), _drc.getFolder(), v, 1);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void scanForImages(File[] files, DocItemFolder f, Vector<String> visitedDirectories, int recursiveDepth) throws IOException {
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.isDirectory() && recursiveDepth > 0 && !visitedDirectories.contains(file.getCanonicalPath())) {
				scanForImages(file.listFiles(), f, visitedDirectories, recursiveDepth - 1);
			} else {
				if (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".jpeg")
						|| file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".gif")) {
					try {
						FileUtils.copyFileToFile(file, new File(getHelpSetDirectory(), "HTML/" + _drc.getFolder().getIdentifier() + "/"
								+ file.getAbsolutePath().substring(_drc.getFolder().getDirectory().getAbsolutePath().length())));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void generate() {
		generate(null);
	}

	protected void generateHSFile() {
		/*try {
			FileOutputStream out = new FileOutputStream(getHSFile());
			XMLCoder.encodeObjectWithMapping(this, getHSMapping(), out, getHSDocType());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	/*private static XMLMapping _hsMapping;

	public static XMLMapping getHSMapping() {
		if (_hsMapping == null) {
			File hsModelFile;
			hsModelFile = new FileResource("Models/HSModel.xml");
			if (!hsModelFile.exists()) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("File " + hsModelFile.getAbsolutePath() + " doesn't exist. Maybe you have to check your paths ?");
				}
				return null;
			} else {
				try {
					_hsMapping = new XMLMapping(hsModelFile);
				} catch (InvalidModelException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (IOException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (SAXException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				}
			}
		}
		return _hsMapping;
	}*/

	public static DocType getHSDocType() {
		return new DocType("helpset", "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN", "../dtd/helpset_2_0.dtd");
	}

	public File getHelpSetDirectory() {
		return helpSetDirectory;
	}

}
