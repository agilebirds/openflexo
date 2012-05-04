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
package org.openflexo.fge.geomedit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fib.utils.FlexoLoggingViewer;
import org.openflexo.fib.utils.LocalizedDelegateGUIImpl;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.FileResource;

public class GeomEdit {

	private static final Logger logger = FlexoLogger.getLogger(GeomEdit.class.getPackage().getName());

	// Retrieve default Openflexo locales
	public static final String LOCALIZATION_DIRNAME = "Localized";
	private static LocalizedDelegateGUIImpl MAIN_LOCALIZER = new LocalizedDelegateGUIImpl(new FileResource(LOCALIZATION_DIRNAME), null);

	// Instanciate a new localizer in directory src/dev/resources/FIBEditorLocalizer
	// linked to parent localizer (which is Openflexo main localizer)
	public static LocalizedDelegateGUIImpl LOCALIZATION = new LocalizedDelegateGUIImpl(new FileResource("GeomEditLocalized"),
			MAIN_LOCALIZER);

	public static void main(String[] args) {
		try {
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
			FlexoLocalization.initWith(LOCALIZATION);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GeomEdit editor = new GeomEdit();
		editor.showPanel();

	}

	private JFrame frame;
	private FlexoFileChooser fileChooser;
	private FIBInspectorController inspector;

	public GeomEdit() {
		super();
		frame = new JFrame();
		fileChooser = new FlexoFileChooser(frame);
		fileChooser.setFileFilterAsString("*.drw");
		fileChooser.setCurrentDirectory(new FileResource("GeomEditExamples"));
		inspector = new FIBInspectorController(frame);
	}

	private Vector<GeometricSet> _drawings = new Vector<GeometricSet>();
	private JPanel mainPanel;
	private JTabbedPane tabbedPane;

	private GeometricSet currentDrawing;

	private class DrawingViewPanel extends JPanel {
		private JPanel controlPanel;
		private JLabel editionLabel;
		private JLabel positionLabel;
		private JScrollPane scrollPane;

		private GeometricDrawingView drawingView;

		private JSplitPane splitPane;

		private DrawingViewPanel(GeometricDrawingView v) {
			super();
			drawingView = v;

			setLayout(new BorderLayout());
			scrollPane = new JScrollPane(v);
			controlPanel = v.getController().getControlPanel();
			editionLabel = v.getController().getEditionLabel();
			positionLabel = v.getController().getPositionLabel();

			add(controlPanel, BorderLayout.NORTH);

			JScrollPane browser = new JScrollPane(v.getController().getTree());
			// browser.setPreferredSize(new Dimension(200,200));
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, browser, scrollPane);
			splitPane.setDividerLocation(150);
			splitPane.setResizeWeight(0);

			add(splitPane, BorderLayout.CENTER);

			JPanel bottom = new JPanel(new BorderLayout());
			bottom.add(editionLabel, BorderLayout.WEST);
			bottom.add(positionLabel, BorderLayout.EAST);

			add(bottom, BorderLayout.SOUTH);

			validate();
		}
	}

	private void addDrawing(GeometricSet drawing) {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					DrawingViewPanel c = (DrawingViewPanel) tabbedPane.getSelectedComponent();
					drawingSwitched(c.drawingView.getDrawing().getModel());
				}
			});
			mainPanel.add(tabbedPane, BorderLayout.CENTER);
		}
		_drawings.add(drawing);
		tabbedPane.add(drawing.getTitle(), new DrawingViewPanel(drawing.getEditedDrawing().getController().getDrawingView()));
		switchToDrawing(drawing);
	}

	private void removeDrawing(GeometricSet drawing) {

	}

	public void switchToDrawing(GeometricSet drawing) {
		tabbedPane.setSelectedIndex(_drawings.indexOf(drawing));
	}

	private void drawingSwitched(GeometricSet drawing) {
		if (currentDrawing != null) {
			mainPanel.remove(currentDrawing.getEditedDrawing().getController().getScalePanel());
			currentDrawing.getEditedDrawing().getController().deleteObserver(inspector);
		}
		currentDrawing = drawing;
		mainPanel.add(currentDrawing.getEditedDrawing().getController().getScalePanel(), BorderLayout.NORTH);
		currentDrawing.getEditedDrawing().getController().addObserver(inspector);
		updateFrameTitle();
		mainPanel.revalidate();
		mainPanel.repaint();
	}

	private void updateFrameTitle() {
		frame.setTitle("GeomEdit - " + currentDrawing.getTitle());
	}

	private void updateTabTitle() {
		tabbedPane.setTitleAt(_drawings.indexOf(currentDrawing), currentDrawing.getTitle());
	}

	public void showPanel() {
		frame.setTitle("GeomEdit");
		mainPanel = new JPanel(new BorderLayout());

		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "file"));
		JMenu editMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "edit"));
		JMenu toolsMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "tools"));
		JMenu helpMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "help"));

		JMenuItem newItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "new_drawing"));
		newItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newDrawing();
			}
		});

		JMenuItem loadItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "open_drawing"));
		loadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadDrawing();
			}
		});

		JMenuItem saveItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "save_drawing"));
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDrawing();
			}
		});

		JMenuItem saveAsItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "save_drawing_as"));
		saveAsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDrawingAs();
			}
		});

		JMenuItem closeItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "close_drawing"));
		closeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDrawing();
			}
		});

		JMenuItem quitItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "quit"));
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

		JMenuItem inspectItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "inspect"));
		inspectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inspector.setVisible(true);
			}
		});

		JMenuItem logsItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "logs"));
		logsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), frame);
			}
		});

		JMenuItem localizedItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "localized_editor"));
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

		/*EditedDrawing ed = drawing.getEditedDrawing();
		DrawingController<EditedDrawing> dc = new MyDrawingController(ed);
		panel.add(new JScrollPane(dc.getDrawingView()), BorderLayout.CENTER);
		panel.add(dc.getScalePanel(), BorderLayout.NORTH);*/

		/*JButton newButton = new JButton("New");
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newDrawing();
			}
		});

		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadDrawing();
			}
		});

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveDrawing();
			}
		});

		JButton saveAsButton = new JButton("Save as");
		saveAsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveDrawingAs();
			}
		});*/

		/*JButton inspectButton = new JButton("Inspect");
		inspectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inspector.getWindow().setVisible(true);
			}
		});

		JButton logButton = new JButton("Logs");
		logButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingManager.showLoggingViewer();
			}
		});

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});*/

		/*JPanel controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(newButton);
		controlPanel.add(loadButton);
		controlPanel.add(saveButton);
		controlPanel.add(saveAsButton);
		controlPanel.add(inspectButton);
		controlPanel.add(logButton);
		controlPanel.add(closeButton);

		mainPanel.add(controlPanel,BorderLayout.SOUTH);*/

		frame.setPreferredSize(new Dimension(1000, 800));
		frame.getContentPane().add(mainPanel);
		frame.validate();
		frame.pack();

		frame.setVisible(true);

	}

	public void quit() {
		frame.dispose();
		System.exit(0);
	}

	public void closeDrawing() {
		logger.warning("Not implemented yet");
	}

	public void newDrawing() {
		/*(new Thread(new Runnable() {
			public void run()
			{
				logger.info("Will stop in 60 seconds");
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logger.info("Stopping application");
				System.exit(-1);
			}
		})).start();*/

		GeometricSet newDrawing = GeometricSet.makeNewDrawing();
		addDrawing(newDrawing);

	}

	public void loadDrawing() {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			GeometricSet loadedDrawing = GeometricSet.load(file);
			if (loadedDrawing != null) {
				addDrawing(loadedDrawing);
			}
		}
	}

	public void saveDrawing() {
		if (currentDrawing == null) {
			return;
		}
		if (currentDrawing.file == null) {
			saveDrawingAs();
		} else {
			currentDrawing.save();
		}
	}

	public void saveDrawingAs() {
		if (currentDrawing == null) {
			return;
		}
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().endsWith(".drw")) {
				file = new File(file.getParentFile(), file.getName() + ".drw");
			}
			currentDrawing.file = file;
			updateFrameTitle();
			updateTabTitle();
			currentDrawing.save();
		} else {
			return;
		}
	}
}
