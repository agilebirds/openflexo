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
package org.openflexo.inspector.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.openflexo.fib.utils.FlexoLoggingViewer;
import org.openflexo.fib.utils.LocalizedDelegateGUIImpl;
import org.openflexo.inspector.model.InspectorModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.FileResource;

public class InspectorEditor {

	private static final Logger logger = FlexoLogger.getLogger(InspectorEditor.class.getPackage().getName());

	// Instanciate a new localizer in directory src/dev/resources/FIBEditorLocalizer
	// linked to parent localizer (which is Openflexo main localizer)
	public static LocalizedDelegateGUIImpl LOCALIZATION = new LocalizedDelegateGUIImpl(new FileResource("FIBEditorLocalized"),
			new LocalizedDelegateGUIImpl(new FileResource("Localized"), null, false), true);

	public static void main(String[] args) {
		try {
			Class.forName("org.openflexo.Flexo");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			// ToolBox.setPlatform();
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InspectorEditor editor = new InspectorEditor();
		editor.showPanel();

		/*(new Thread(new Runnable() {
			public void run()
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logger.info("Stopping application");
				System.exit(-1);
			}
		})).start();*/
	}

	private JFrame frame;
	private JDialog paletteDialog;
	private FlexoFileChooser fileChooser;
	private EditorController controller;

	private InspectorInspector inspector;

	public class EditedInspector {
		public File inspectorFile;
		public InspectorModel inspectorModel;

		public void save() {
			// TODO Auto-generated method stub

		}
	}

	private EditedInspector currentInspector;

	public InspectorEditor() {
		super();
		frame = new JFrame();
		frame.setPreferredSize(new Dimension(1000, 800));
		fileChooser = new FlexoFileChooser(frame);
		fileChooser.setFileFilterAsString("*.inspector");
		fileChooser.setCurrentDirectory(new FileResource("Inspectors"));

		inspector = new InspectorInspector();

		controller = new EditorController();

		paletteDialog = new JDialog(frame, "Palette", false);
		JPanel emptyContent = new JPanel();
		emptyContent.setPreferredSize(new Dimension(300, 300));
		paletteDialog.getContentPane().add(emptyContent);
		paletteDialog.setLocation(1010, 0);
		paletteDialog.pack();
		paletteDialog.setVisible(true);
	}

	private JPanel mainPanel;
	private JTabbedPane tabbedPane;

	private void updateFrameTitle() {
		frame.setTitle("Inspector editor");
	}

	public void showPanel() {
		frame.setTitle("Inspector editor");
		mainPanel = new JPanel(new BorderLayout());

		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu(FlexoLocalization.localizedForKey("file"));
		JMenu editMenu = new JMenu(FlexoLocalization.localizedForKey("edit"));
		JMenu toolsMenu = new JMenu(FlexoLocalization.localizedForKey("tools"));
		JMenu helpMenu = new JMenu(FlexoLocalization.localizedForKey("help"));

		JMenuItem newItem = new JMenuItem(FlexoLocalization.localizedForKey("new_inspector"));
		newItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newInspector();
			}
		});

		JMenuItem loadItem = new JMenuItem(FlexoLocalization.localizedForKey("open_inspector"));
		loadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadInspector();
			}
		});

		JMenuItem saveItem = new JMenuItem(FlexoLocalization.localizedForKey("save_inspector"));
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveInspector();
			}
		});

		JMenuItem saveAsItem = new JMenuItem(FlexoLocalization.localizedForKey("save_inspector_as"));
		saveAsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveInspectorAs();
			}
		});

		JMenuItem closeItem = new JMenuItem(FlexoLocalization.localizedForKey("close_inspector"));
		closeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeInspector();
			}
		});

		JMenuItem quitItem = new JMenuItem(FlexoLocalization.localizedForKey("quit"));
		quitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});

		fileMenu.add(newItem);
		fileMenu.add(loadItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(closeItem);
		fileMenu.addSeparator();
		fileMenu.add(quitItem);

		JMenuItem inspectItem = new JMenuItem(FlexoLocalization.localizedForKey("inspect"));
		inspectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inspector.getWindow().setVisible(true);
			}
		});

		JMenuItem logsItem = new JMenuItem(FlexoLocalization.localizedForKey("logs"));
		logsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), frame);
			}
		});

		JMenuItem localizedItem = new JMenuItem(FlexoLocalization.localizedForKey("localized_editor"));
		localizedItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LOCALIZATION.showLocalizedEditor(frame);
			}
		});

		toolsMenu.add(inspectItem);
		toolsMenu.add(logsItem);
		toolsMenu.add(localizedItem);

		mb.add(fileMenu);
		mb.add(editMenu);
		mb.add(toolsMenu);
		mb.add(helpMenu);

		frame.setJMenuBar(mb);

		frame.getContentPane().add(mainPanel);
		frame.validate();
		frame.pack();

		inspector.getWindow().setLocation(1010, 400);
		inspector.getWindow().setPreferredSize(new Dimension(300, 300));
		inspector.getWindow().setVisible(true);

		frame.setVisible(true);

	}

	public void quit() {
		frame.dispose();
		System.exit(0);
	}

	public void closeInspector() {
		logger.warning("Not implemented yet");
	}

	public void newInspector() {
		// MyDrawing newDrawing = MyDrawing.makeNewDrawing();
		// addDrawing(newDrawing);
	}

	public void loadInspector() {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			InspectorModel inspector = null;
			try {
				inspector = controller.importInspectorFile(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Inspector: " + inspector);
			mainPanel.add(new EditedInspectorView(inspector), BorderLayout.CENTER);
			frame.validate();
		}
	}

	public void saveInspector() {
		if (currentInspector == null)
			return;
		if (currentInspector.inspectorFile == null) {
			saveInspectorAs();
		} else {
			currentInspector.save();
		}
	}

	public void saveInspectorAs() {
		if (currentInspector == null)
			return;
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().endsWith(".drw"))
				file = new File(file.getParentFile(), file.getName() + ".drw");
			currentInspector.inspectorFile = file;
			updateFrameTitle();
			currentInspector.save();
		} else {
			return;
		}
	}
}
