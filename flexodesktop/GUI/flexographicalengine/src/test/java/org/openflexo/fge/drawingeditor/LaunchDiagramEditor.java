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
package org.openflexo.fge.drawingeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.drawingeditor.model.Diagram;
import org.openflexo.fge.drawingeditor.model.DiagramFactory;
import org.openflexo.fib.utils.FlexoLoggingViewer;
import org.openflexo.fib.utils.LocalizedDelegateGUIImpl;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.StringEncoder;

public class LaunchDiagramEditor {

	private static final Logger logger = FlexoLogger.getLogger(LaunchDiagramEditor.class.getPackage().getName());

	// Retrieve default Openflexo locales
	public static final String LOCALIZATION_DIRNAME = "Localized";
	private static LocalizedDelegateGUIImpl MAIN_LOCALIZER = new LocalizedDelegateGUIImpl(new FileResource(LOCALIZATION_DIRNAME), null,
			false);

	// Instanciate a new localizer in directory src/dev/resources/FIBEditorLocalizer
	// linked to parent localizer (which is Openflexo main localizer)
	public static LocalizedDelegateGUIImpl LOCALIZATION = new LocalizedDelegateGUIImpl(new FileResource("FGEEditorLocalized"),
			MAIN_LOCALIZER, true);

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				init();
			}
		});
	}

	private static void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			FlexoLoggingManager.initialize(-1, true, null, Level.INFO, null);
			FlexoLocalization.initWith(LOCALIZATION);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringEncoder.getDefaultInstance()._addConverter(DataBinding.CONVERTER);

		LaunchDiagramEditor editor = new LaunchDiagramEditor();
		editor.showPanel();
		editor.newDiagramEditor();
	}

	private JFrame frame;
	private JDialog paletteDialog;
	private FlexoFileChooser fileChooser;

	private FIBInspectorController inspector;

	private DiagramFactory factory;

	// private Injector injector;

	public LaunchDiagramEditor() {
		super();

		try {
			factory = new DiagramFactory();
			// System.out.println("factory: " + factory.debug());
			// FGEPamelaInjectionModule injectionModule = new FGEPamelaInjectionModule(factory);
			// injector = Guice.createInjector(injectionModule);

			// factory = new DiagramFactory();
		} catch (ModelDefinitionException e1) {
			e1.printStackTrace();
		}

		frame = new JFrame();
		frame.setPreferredSize(new Dimension(1000, 800));
		fileChooser = new FlexoFileChooser(frame);
		fileChooser.setFileFilterAsString("*.drw");
		fileChooser.setCurrentDirectory(new FileResource("DrawingExamples"));

		inspector = new FIBInspectorController(frame);

		paletteDialog = new JDialog(frame, "Palette", false);
		JPanel emptyContent = new JPanel();
		emptyContent.setPreferredSize(new Dimension(300, 300));
		paletteDialog.getContentPane().add(emptyContent);
		paletteDialog.setLocation(1010, 0);
		paletteDialog.pack();
		paletteDialog.setVisible(true);

	}

	private Vector<DiagramEditor> diagramEditors = new Vector<DiagramEditor>();
	private JPanel mainPanel;
	private JTabbedPane tabbedPane;

	private DiagramEditor currentDiagramEditor;

	private class MyDrawingViewScrollPane extends JScrollPane {
		private DiagramEditorView drawingView;

		private MyDrawingViewScrollPane(DiagramEditorView v) {
			super(v);
			drawingView = v;
		}
	}

	public DiagramFactory getFactory() {
		return factory;
	}

	/*public Injector getInjector() {
		return injector;
	}*/

	private void addDiagramEditor(DiagramEditor diagramEditor) {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					MyDrawingViewScrollPane c = (MyDrawingViewScrollPane) tabbedPane.getSelectedComponent();
					if (c != null) {
						drawingSwitched(c.drawingView.getDrawing().getModel());
					}
				}
			});
			mainPanel.add(tabbedPane, BorderLayout.CENTER);
			// mainPanel.add(drawing.getEditedDrawing().getPalette().getPaletteView(),BorderLayout.EAST);
		}
		diagramEditors.add(diagramEditor);

		// DiagramEditorController controller = new DiagramEditorController(diagramEditor.getEditedDrawing(), diagramEditor.getFactory());
		// DrawingController<DiagramDrawing> controller = new DrawingController<DiagramDrawing>(aDrawing, factory)

		tabbedPane.add(diagramEditor.getTitle(), new MyDrawingViewScrollPane(diagramEditor.getController().getDrawingView()));
		switchToDiagramEditor(diagramEditor);

		/*frame.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) 
			{
				if (event.getKeyCode() == KeyEvent.VK_UP) {
					drawing.getEditedDrawing().getController().upKeyPressed();
					event.consume();
					return;
				}
				else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
					drawing.getEditedDrawing().getController().downKeyPressed();
					event.consume();
					return;
				}
				else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
					drawing.getEditedDrawing().getController().rightKeyPressed();
					event.consume();
					return;
				}
				else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
					drawing.getEditedDrawing().getController().leftKeyPressed();
					event.consume();
					return;
				}
				super.keyPressed(event);
			}
		});*/
	}

	private void removeDiagramEditor(DiagramEditor diagramEditor) {

	}

	public void switchToDiagramEditor(DiagramEditor diagramEditor) {
		tabbedPane.setSelectedIndex(diagramEditors.indexOf(diagramEditor));
	}

	private void drawingSwitched(Diagram diagram) {
		for (DiagramEditor editor : diagramEditors) {
			if (editor.getDiagram() == diagram) {
				drawingSwitched(editor);
				return;
			}
		}
	}

	private void drawingSwitched(DiagramEditor diagramEditor) {
		if (currentDiagramEditor != null) {
			mainPanel.remove(currentDiagramEditor.getController().getScalePanel());
			currentDiagramEditor.getController().deleteObserver(inspector);
		}
		currentDiagramEditor = diagramEditor;

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(currentDiagramEditor.getController().getToolbox().getStyleToolBar());
		topPanel.add(currentDiagramEditor.getController().getScalePanel());

		mainPanel.add(topPanel, BorderLayout.NORTH);
		currentDiagramEditor.getController().addObserver(inspector);
		updateFrameTitle();
		mainPanel.revalidate();
		mainPanel.repaint();
		paletteDialog.getContentPane().removeAll();
		paletteDialog.getContentPane().add(currentDiagramEditor.getController().getPalette().getPaletteView());
		paletteDialog.pack();
	}

	private void updateFrameTitle() {
		frame.setTitle("Basic drawing editor - " + currentDiagramEditor.getTitle());
	}

	private void updateTabTitle() {
		tabbedPane.setTitleAt(diagramEditors.indexOf(currentDiagramEditor), currentDiagramEditor.getTitle());
	}

	public void showPanel() {
		frame.setTitle("Basic drawing editor");
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
				newDiagramEditor();
			}
		});

		JMenuItem loadItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "open_drawing"));
		loadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadDiagramEditor();
			}
		});

		JMenuItem saveItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "save_drawing"));
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDiagramEditor();
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

		JMenuItem paletteItem = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "show_palette"));
		paletteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paletteDialog.setVisible(true);
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
		toolsMenu.add(paletteItem);
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

		frame.setVisible(true);

	}

	public void quit() {
		frame.dispose();
		System.exit(0);
	}

	public void closeDrawing() {
		if (currentDiagramEditor == null) {
			return;
		}
		if (currentDiagramEditor.getDiagram().hasChanged()) {
			int result = JOptionPane.showOptionDialog(frame, "Would you like to save drawing changes?", "Save changes",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			switch (result) {
			case JOptionPane.YES_OPTION:
				if (!currentDiagramEditor.save()) {
					return;
				}
				break;
			case JOptionPane.NO_OPTION:
				break;
			default:
				return;
			}
		}
		diagramEditors.remove(currentDiagramEditor);
		tabbedPane.remove(tabbedPane.getSelectedIndex());
		if (diagramEditors.size() == 0) {
			newDiagramEditor();
		}
	}

	public void newDiagramEditor() {
		DiagramEditor newDiagramEditor = DiagramEditor.newDiagramEditor(factory);
		addDiagramEditor(newDiagramEditor);
	}

	public void loadDiagramEditor() {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			DiagramEditor loadedDiagramEditor = DiagramEditor.loadDiagramEditor(file, factory);
			if (loadedDiagramEditor != null) {
				addDiagramEditor(loadedDiagramEditor);
			}
		}
	}

	public boolean saveDiagramEditor() {
		if (currentDiagramEditor == null) {
			return false;
		}
		if (currentDiagramEditor.getFile() == null) {
			return saveDrawingAs();
		} else {
			return currentDiagramEditor.save();
		}
	}

	public boolean saveDrawingAs() {
		if (currentDiagramEditor == null) {
			return false;
		}
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().endsWith(".drw")) {
				file = new File(file.getParentFile(), file.getName() + ".drw");
			}
			currentDiagramEditor.setFile(file);
			updateFrameTitle();
			updateTabTitle();
			return currentDiagramEditor.save();
		} else {
			return false;
		}
	}
}
