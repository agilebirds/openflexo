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
package org.openflexo.fib.editor;

import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.controller.FIBEditorPalette;
import org.openflexo.fib.editor.controller.FIBInspectorController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.utils.FlexoLoggingViewer;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.swing.ComponentBoundSaver;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ToolBox;

// TODO: switch to the right editor controller when switching tab
// 		getPalette().setEditorController(editorController);
public class FIBEditor implements FIBGenericEditor {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditor.class.getPackage().getName());

	public static File COMPONENT_LOCALIZATION_FIB = new FileResource("Fib/ComponentLocalization.fib");

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		/*DefaultExpressionParser parser = new DefaultExpressionParser();
			try {
				//Expression test = parser.parse("data.name.substring(0,1).toUpperCase()+data.name.substring(1,6)");
				//Expression test = parser.parse("data.name.substring().a()+2");
				//Expression test = parser.parse("a.b().c()+2");
				Expression test = parser.parse("a.c()+2");
				System.out.println("test="+test);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			System.exit(-1);*/

		/*try {
			Class.forName("org.openflexo.Flexo");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/

		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
			FlexoLocalization.initWith(FIBAbstractEditor.LOCALIZATION);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				FIBEditor editor = new FIBEditor();
				editor.showPanel();
			}
		});

	}

	private final JFrame frame;
	private final FIBEditorPalette palette;
	private final FlexoFileChooser fileChooser;

	private final FIBInspectorController inspector;

	private FIBEditorController editorController;

	private ValidationWindow validationWindow;

	@Override
	public FIBInspectorController getInspector() {
		return inspector;
	}

	@Override
	public FIBEditorPalette getPalette() {
		return palette;
	}

	public class EditedFIB {
		private String title;
		private File fibFile;
		private final FIBComponent fibComponent;
		// This is the factory used to edit the FIB component
		private final FIBModelFactory factory;

		public EditedFIB(String title, File fibFile, FIBComponent fibComponent, FIBModelFactory factory) {
			super();
			this.title = title;
			this.fibFile = fibFile;
			this.fibComponent = fibComponent;
			this.factory = factory;

		}

		public FIBComponent getFIBComponent() {
			return fibComponent;
		}

		public FIBModelFactory getFactory() {
			return factory;
		}

		public void save() {
			logger.info("Save to file " + fibFile.getAbsolutePath());

			FIBLibrary.save(fibComponent, fibFile);
		}

	}

	private EditedFIB editedFIB;

	public FIBEditor() {
		super();
		frame = new JFrame();
		frame.setBounds(FIBPreferences.getFrameBounds());
		new ComponentBoundSaver(frame) {

			@Override
			public void saveBounds(Rectangle bounds) {
				FIBPreferences.setFrameBounds(bounds);
			}
		};
		fileChooser = new FlexoFileChooser(frame);
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "*.fib *.inspector";
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".fib") || f.getName().endsWith(".inspector");
			}
		});
		fileChooser.setCurrentDirectory(FIBPreferences.getLastDirectory());

		inspector = new FIBInspectorController(frame);

		palette = new FIBEditorPalette(frame);
		palette.setVisible(true);
	}

	private MainPanel mainPanel;

	private MenuBar menuBar;

	private void updateFrameTitle() {
		frame.setTitle("Flexo Interface Builder Editor");
	}

	public void showPanel() {
		frame.setTitle("Flexo Interface Builder Editor");
		mainPanel = new MainPanel();

		menuBar = new MenuBar();

		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(mainPanel);
		mainPanel.revalidate();
		frame.setVisible(true);
	}

	public void quit() {

		// TODO: Check if a component needs to be saved.
		/*for(FIBEditor.EditedFIB c: mainPanel.editedComponents) {
		}*/
		frame.dispose();
		System.exit(0);
	}

	public void closeFIB() {
		logger.warning("Not implemented yet");
	}

	public void newFIB() {

		FIBModelFactory factory = null;

		try {
			factory = new FIBModelFactory();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			return;
		}

		FIBPanel fibComponent = factory.newInstance(FIBPanel.class);
		fibComponent.setLayout(Layout.border);
		fibComponent.finalizeDeserialization();
		EditedFIB newEditedFIB = new EditedFIB("New.fib", new File("NewComponent.fib"), fibComponent, factory);

		editorController = new FIBEditorController(factory, fibComponent, this);
		getPalette().setEditorController(editorController);

		mainPanel.newEditedComponent(newEditedFIB, editorController);

	}

	public void loadFIB() {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File fibFile = fileChooser.getSelectedFile();
			loadFIB(fibFile);
		}
	}

	public void loadFIB(File fibFile) {
		if (!fibFile.exists()) {
			JOptionPane.showMessageDialog(frame, "File " + fibFile.getAbsolutePath() + " does not exist anymore");
			return;
		}
		FIBPreferences.setLastFile(fibFile);

		FIBModelFactory factory = null;
		try {
			factory = new FIBModelFactory(fibFile.getParentFile());
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			return;
		}
		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(fibFile, false, factory);
		EditedFIB newEditedFIB = new EditedFIB(fibFile.getName(), fibFile, fibComponent, factory);
		editorController = new FIBEditorController(factory, fibComponent, this);
		getPalette().setEditorController(editorController);
		mainPanel.newEditedComponent(newEditedFIB, editorController);
	}

	public void saveFIB() {
		if (editedFIB == null) {
			return;
		}
		if (editedFIB.fibFile == null) {
			saveFIBAs();
		} else {
			editedFIB.save();
		}
	}

	public void saveFIBAs() {
		if (editedFIB == null) {
			return;
		}
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().endsWith(".fib")) {
				file = new File(file.getParentFile(), file.getName() + ".fib");
			}
			FIBPreferences.setLastFile(file);
			editedFIB.fibFile = file;
			editedFIB.title = file.getName();
			mainPanel.setTitleAt(mainPanel.getSelectedIndex(), editedFIB.title);
			updateFrameTitle();
			editedFIB.save();
		} else {
			return;
		}
	}

	public void testFIB() {
		FIBView view = FIBController.makeView(editedFIB.fibComponent, FIBAbstractEditor.LOCALIZATION);

		// Class testClass = null;
		if (editedFIB.fibComponent.getDataClass() != null) {
			try {
				// testClass = Class.forName(editedFIB.fibComponent.getDataClassName());
				Object testData = editedFIB.fibComponent.getDataClass().newInstance();
				view.getController().setDataObject(testData);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			view.getController().updateWithoutDataObject();
		}

		JDialog testInterface = new JDialog(frame, "Test", false);
		testInterface.getContentPane().add(view.getResultingJComponent());
		testInterface.pack();
		testInterface.setVisible(true);
	}

	public void localizeFIB() {

		if (editorController == null) {
			return;
		}

		FIBComponent componentLocalizationComponent = FIBLibrary.instance().retrieveFIBComponent(COMPONENT_LOCALIZATION_FIB);

		FIBView view = FIBController.makeView(componentLocalizationComponent, FIBAbstractEditor.LOCALIZATION);
		view.getController().setDataObject(editorController.getController());
		JDialog localizationInterface = new JDialog(frame, FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,
				"component_localization"), false);
		localizationInterface.getContentPane().add(view.getResultingJComponent());
		localizationInterface.pack();
		localizationInterface.setVisible(true);
	}

	public void validateFIB() {
		if (editedFIB != null && editedFIB.fibComponent != null) {
			getValidationWindow().validateAndDisplayReportForComponent(editedFIB.fibComponent);
		}
	}

	protected ValidationWindow getValidationWindow() {
		if (validationWindow == null) {
			validationWindow = new ValidationWindow(frame, editorController);
		}
		return validationWindow;
	}

	public void switchToLanguage(Language lang) {
		FlexoLocalization.setCurrentLanguage(lang);
		if (editorController != null) {
			editorController.switchToLanguage(lang);
		}
	}

	@Override
	public JFrame getFrame() {
		return frame;
	}

	@Override
	public File getEditedComponentFile() {
		return editedFIB.fibFile;
	}

	class LAFMenuItem extends JCheckBoxMenuItem {
		private final LookAndFeelInfo laf;

		public LAFMenuItem(LookAndFeelInfo laf) {
			super(laf.getName(), UIManager.getLookAndFeel().getClass().getName().equals(laf.getClassName()));
			this.laf = laf;
		}

		public void updateState() {
			setState(UIManager.getLookAndFeel().getClass().getName().equals(laf.getClassName()));
		}
	}

	public class MenuBar extends JMenuBar implements PreferenceChangeListener {
		private final JMenu fileMenu;

		private final JMenu editMenu;

		private final JMenu toolsMenu;

		private final JMenu helpMenu;

		private final JMenuItem newItem;

		private final JMenuItem loadItem;

		private final JMenuItem saveItem;

		private final JMenuItem saveAsItem;

		private final JMenuItem closeItem;

		private final JMenuItem quitItem;

		private final JMenu languagesItem;

		private final JMenuItem inspectItem;

		private final JMenuItem logsItem;

		private final JMenuItem localizedItem;

		private final JMenuItem displayFileItem;

		private final JMenuItem testInterfaceItem;

		private final JMenuItem componentLocalizationItem;
		private final JMenuItem componentValidationItem;

		private final JMenu openRecent;

		private final JMenuItem showPaletteItem;

		public MenuBar() {
			fileMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "file"));
			editMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "edit"));
			toolsMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "tools"));
			helpMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "help"));

			newItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "new_interface"));
			newItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					newFIB();
				}
			});

			loadItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "open_interface"));
			loadItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					loadFIB();
				}
			});

			openRecent = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "open_recent"));
			FIBPreferences.addPreferenceChangeListener(this);
			updateOpenRecent();
			saveItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "save_interface"));
			saveItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveFIB();
				}
			});

			saveAsItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "save_interface_as"));
			saveAsItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveFIBAs();
				}
			});

			closeItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "close_interface"));
			closeItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					closeFIB();
				}
			});

			quitItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "quit"));
			quitItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					quit();
				}
			});
			testInterfaceItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "test_interface"));
			testInterfaceItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					testFIB();
				}
			});

			componentLocalizationItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,
					"component_localization"));
			componentLocalizationItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					localizeFIB();
				}
			});

			final JMenu lafsItem = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "look_and_feel"));
			final Vector<LAFMenuItem> lafsItems = new Vector<LAFMenuItem>();
			for (final LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
				LAFMenuItem lafItem = new LAFMenuItem(laf);
				lafsItems.add(lafItem);
				lafItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							UIManager.setLookAndFeel(laf.getClassName());
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InstantiationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IllegalAccessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (UnsupportedLookAndFeelException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Window windows[] = frame.getOwnedWindows();
						for (int j = 0; j < windows.length; j++) {
							SwingUtilities.updateComponentTreeUI(windows[j]);
						}
						SwingUtilities.updateComponentTreeUI(frame);
						for (LAFMenuItem me : lafsItems) {
							me.updateState();
						}
					}
				});
				lafsItem.add(lafItem);
			}

			fileMenu.add(newItem);
			fileMenu.add(loadItem);
			fileMenu.add(openRecent);
			fileMenu.add(saveItem);
			fileMenu.add(saveAsItem);
			fileMenu.add(closeItem);
			fileMenu.addSeparator();
			fileMenu.add(testInterfaceItem);
			fileMenu.add(componentLocalizationItem);

			fileMenu.add(lafsItem);
			languagesItem = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "switch_to_language"));
			for (Language lang : Language.availableValues()) {
				JMenuItem languageItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, lang.getName()));
				final Language language = lang;
				languageItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						switchToLanguage(language);
					}
				});
				languagesItem.add(languageItem);
			}
			fileMenu.add(languagesItem);
			fileMenu.addSeparator();
			fileMenu.add(quitItem);

			inspectItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "inspect"));
			inspectItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getInspector().setVisible(true);
				}
			});
			inspectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ToolBox.getPLATFORM() != ToolBox.MACOS ? InputEvent.CTRL_MASK
					: InputEvent.META_MASK));
			showPaletteItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "show_palette"));
			showPaletteItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPalette().setVisible(true);
				}
			});
			logsItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "logs"));
			logsItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), frame);
				}
			});

			localizedItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "localized_editor"));
			localizedItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FIBAbstractEditor.LOCALIZATION.showLocalizedEditor(frame);
				}
			});

			displayFileItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "display_file"));
			displayFileItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					logger.info("Getting this " + editedFIB.getFactory().stringRepresentation(editedFIB.fibComponent));
				}
			});

			componentValidationItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION, "validate_component"));
			componentValidationItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					validateFIB();
				}
			});

			toolsMenu.add(inspectItem);
			toolsMenu.add(showPaletteItem);
			toolsMenu.add(logsItem);
			toolsMenu.add(localizedItem);
			toolsMenu.add(displayFileItem);
			toolsMenu.addSeparator();
			toolsMenu.add(componentValidationItem);

			add(fileMenu);
			add(editMenu);
			add(toolsMenu);
			add(helpMenu);
		}

		private boolean willUpdate = false;

		@Override
		public void preferenceChange(PreferenceChangeEvent evt) {
			if (evt.getKey().startsWith(FIBPreferences.LAST_FILE)) {
				if (willUpdate) {
					return;
				} else {
					willUpdate = true;
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						willUpdate = false;
						updateOpenRecent();
					}
				});
			}
		}

		private void updateOpenRecent() {
			openRecent.removeAll();
			List<File> files = FIBPreferences.getLastFiles();
			openRecent.setEnabled(files.size() != 0);
			for (final File file : files) {
				JMenuItem item = new JMenuItem(file.getName());
				item.setToolTipText(file.getAbsolutePath());
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						loadFIB(file);
					}
				});
				openRecent.add(item);
			}
		}
	}

	public class MainPanel extends JTabbedPane implements ChangeListener {
		private final Vector<EditedFIB> editedComponents;
		private final Hashtable<EditedFIB, FIBEditorController> controllers;

		public MainPanel() {
			super();
			editedComponents = new Vector<EditedFIB>();
			controllers = new Hashtable<EditedFIB, FIBEditorController>();
			addChangeListener(this);
		}

		public void newEditedComponent(EditedFIB edited, FIBEditorController controller) {
			editedComponents.add(edited);
			controllers.put(edited, controller);
			add(controller.getEditorPanel(), edited.title);
			revalidate();
			setSelectedIndex(getComponentCount() - 1);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			logger.info("Change for " + e);
			int index = getSelectedIndex();
			editedFIB = editedComponents.get(index);
		}
	}
}
