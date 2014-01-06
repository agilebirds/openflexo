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
package org.openflexo.fme;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.exceptions.CopyException;
import org.openflexo.fge.control.exceptions.CutException;
import org.openflexo.fge.control.exceptions.PasteException;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.swing.control.tools.JDianaInspectors;
import org.openflexo.fge.swing.control.tools.JDianaLayoutWidget;
import org.openflexo.fge.swing.control.tools.JDianaPalette;
import org.openflexo.fge.swing.control.tools.JDianaScaleSelector;
import org.openflexo.fge.swing.control.tools.JDianaStyles;
import org.openflexo.fge.swing.control.tools.JDianaToolSelector;
import org.openflexo.fib.utils.FlexoLoggingViewer;
import org.openflexo.fib.utils.LocalizedDelegateGUIImpl;
import org.openflexo.fme.model.BusinessDiagramFactory;
import org.openflexo.fme.model.Diagram;
import org.openflexo.fme.model.DiagramFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.undo.UndoManager;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.swing.layout.JXMultiSplitPane;
import org.openflexo.swing.layout.MultiSplitLayout;
import org.openflexo.swing.layout.MultiSplitLayout.Divider;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.Split;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.toolbox.PropertyChangeListenerRegistrationManager;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents the FreeModellingEditor application
 * 
 * @author sylvain
 * 
 */
public class FreeModellingEditorApplication {

	private static final Logger logger = FlexoLogger.getLogger(FreeModellingEditorApplication.class.getPackage().getName());

	// Retrieve default Openflexo locales
	public static final String LOCALIZATION_DIRNAME = "Localized";
	private static LocalizedDelegateGUIImpl MAIN_LOCALIZER = new LocalizedDelegateGUIImpl(new FileResource(LOCALIZATION_DIRNAME), null,
			false);

	// Instanciate a new localizer in directory src/dev/resources/FIBEditorLocalizer
	// linked to parent localizer (which is Openflexo main localizer)
	public static LocalizedDelegateGUIImpl LOCALIZATION = new LocalizedDelegateGUIImpl(new FileResource("FGEEditorLocalized"),
			MAIN_LOCALIZER, true);

	private static final int META_MASK = ToolBox.getPLATFORM() == ToolBox.MACOS ? InputEvent.META_MASK : InputEvent.CTRL_MASK;

	private JFrame frame;
	// private JDialog paletteDialog;
	private FlexoFileChooser fileChooser;
	private SwingToolFactory toolFactory;

	private DiagramFactory factory;

	private Vector<DiagramEditor> diagramEditors = new Vector<DiagramEditor>();
	private JXMultiSplitPane splitPanel;
	private JPanel mainPanel;
	private JTabbedPane tabbedPane;

	private DiagramEditor currentDiagramEditor;

	private JDianaToolSelector toolSelector;
	private JDianaScaleSelector scaleSelector;
	private JDianaLayoutWidget layoutWidget;
	private JDianaStyles stylesWidget;
	private JDianaPalette commonPalette;
	private CommonPalette commonPaletteModel;
	private JDianaPalette dynamicPalette;
	private DynamicPalette dynamicPaletteModel;
	private JDianaInspectors inspectors;

	private FIBInspectorController inspector;
	private ConceptBrowser conceptBrowser;
	private RepresentedConceptBrowser representedConceptBrowser;

	protected PropertyChangeListenerRegistrationManager manager;

	private SynchronizedMenuItem copyItem;
	private SynchronizedMenuItem cutItem;
	private SynchronizedMenuItem pasteItem;
	private SynchronizedMenuItem undoItem;
	private SynchronizedMenuItem redoItem;

	@SuppressWarnings("serial")
	public FreeModellingEditorApplication() {
		super();

		try {
			factory = new BusinessDiagramFactory();
			// System.out.println("factory: " + factory.debug());
			// FGEPamelaInjectionModule injectionModule = new FGEPamelaInjectionModule(factory);
			// injector = Guice.createInjector(injectionModule);

			// factory = new DiagramFactory();
		} catch (ModelDefinitionException e1) {
			e1.printStackTrace();
		}

		frame = new JFrame();
		frame.setPreferredSize(new Dimension(1100, 800));
		fileChooser = new FlexoFileChooser(frame);
		fileChooser.setFileFilterAsString("*.fm");
		fileChooser.setCurrentDirectory(new FileResource("FreeModellingExamples"));

		toolFactory = new SwingToolFactory(frame);

		inspector = new FIBInspectorController(this);

		mainPanel = new JPanel(new BorderLayout());

		toolSelector = toolFactory.makeDianaToolSelector(null);
		stylesWidget = toolFactory.makeDianaStyles();
		scaleSelector = toolFactory.makeDianaScaleSelector(null);
		layoutWidget = toolFactory.makeDianaLayoutWidget();
		inspectors = toolFactory.makeDianaInspectors();

		inspectors.getForegroundStyleInspector().setLocation(1000, 100);
		inspectors.getTextStyleInspector().setLocation(1000, 300);
		inspectors.getShadowStyleInspector().setLocation(1000, 400);
		inspectors.getBackgroundStyleInspector().setLocation(1000, 500);
		inspectors.getShapeInspector().setLocation(1000, 600);
		inspectors.getConnectorInspector().setLocation(1000, 700);
		inspectors.getLocationSizeInspector().setLocation(1000, 50);

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(toolSelector.getComponent());
		topPanel.add(stylesWidget.getComponent());
		topPanel.add(layoutWidget.getComponent());

		JPanel bottomPanel = new JPanel(new BorderLayout());
		JLabel dialogLabel = new JLabel("Free Modelling Editor");
		bottomPanel.add(dialogLabel, BorderLayout.CENTER);
		bottomPanel.add(scaleSelector.getComponent(), BorderLayout.EAST);

		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		commonPaletteModel = new CommonPalette();
		commonPalette = toolFactory.makeDianaPalette(commonPaletteModel);

		dynamicPaletteModel = new DynamicPalette();
		dynamicPalette = toolFactory.makeDianaPalette(dynamicPaletteModel);

		/*paletteDialog = new JDialog(frame, "Palette", false);
		paletteDialog.getContentPane().add(commonPalette.getComponent());
		paletteDialog.setLocation(1010, 0);
		paletteDialog.pack();
		paletteDialog.setVisible(true);
		paletteDialog.setFocusableWindowState(false);*/

		conceptBrowser = new ConceptBrowser(null);
		representedConceptBrowser = new RepresentedConceptBrowser(null);

		Split all = new Split();

		Split leftColumn = new Split();
		leftColumn.setWeight(0.0);
		leftColumn.setRowLayout(false);
		Leaf leftTop = new Leaf("left.top");
		Leaf leftBottom = new Leaf("left.bottom");
		leftTop.setWeight(1.0);
		leftBottom.setWeight(1.0);
		leftColumn.setChildren(leftTop, new Divider(), leftBottom);

		Split rightColumn = new Split();
		rightColumn.setWeight(0.0);
		rightColumn.setRowLayout(false);
		Leaf rightTop = new Leaf("right.top");
		Leaf rightMiddle = new Leaf("right.middle");
		Leaf rightBottom = new Leaf("right.bottom");
		rightTop.setWeight(0.0);
		rightMiddle.setWeight(0.0);
		rightBottom.setWeight(1.0);
		rightColumn.setChildren(rightTop, new Divider(), rightMiddle, new Divider(), rightBottom);

		Leaf middle = new Leaf("middle");
		middle.setWeight(1.0);
		all.setChildren(leftColumn, new Divider(), middle, new Divider(), rightColumn);

		// Once the layout is done, the code is easy
		MultiSplitLayout layout = new MultiSplitLayout(all);

		layout.setLayoutByWeight(false);
		layout.setFloatingDividers(true);

		dynamicPalette.getComponent().setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0), dynamicPalette.getComponent().getBorder()));
		inspector.getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		conceptBrowser.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		splitPanel = new JXMultiSplitPane(layout);
		splitPanel.add(representedConceptBrowser, "left.top");
		splitPanel.add(conceptBrowser, "left.bottom");
		splitPanel.add(dynamicPalette.getComponent(), "right.top");
		splitPanel.add(commonPalette.getComponent(), "right.middle");
		splitPanel.add(inspector.getRootPane(), "right.bottom");
		splitPanel.add(mainPanel, "middle");

		splitPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		manager = new PropertyChangeListenerRegistrationManager();

		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "file"));
		JMenu editMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "edit"));
		JMenu viewMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "view"));
		JMenu toolsMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "tools"));
		JMenu helpMenu = new JMenu(FlexoLocalization.localizedForKey(LOCALIZATION, "help"));

		JMenuItem newItem = makeJMenuItem("new_drawing", NEW_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_N, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newDiagramEditor();
			}
		});

		JMenuItem loadItem = makeJMenuItem("open_drawing", OPEN_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_O, META_MASK),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						loadDiagramEditor();
					}
				});

		JMenuItem saveItem = makeJMenuItem("save_drawing", SAVE_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_S, META_MASK),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						saveDiagramEditor();
					}
				});

		JMenuItem saveAsItem = makeJMenuItem("save_drawing_as", SAVE_AS_ICON, null, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDrawingAs();
			}
		});

		JMenuItem closeItem = makeJMenuItem("close_drawing", null, null, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDrawing();
			}
		});

		JMenuItem quitItem = makeJMenuItem("quit", null, KeyStroke.getKeyStroke(KeyEvent.VK_Q, META_MASK), new AbstractAction() {
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

		copyItem = makeSynchronizedMenuItem("copy", COPY_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_C, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("copy");
				try {
					currentDiagramEditor.getController().copy();
				} catch (CopyException e1) {
					e1.printStackTrace();
				}
			}
		}, new Synchronizer() {
			@Override
			public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
				if (observable instanceof DianaDrawingEditor) {
					menuItem.setEnabled(((DianaDrawingEditor) observable).isCopiable());
				}
			}
		});

		cutItem = makeSynchronizedMenuItem("cut", CUT_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_X, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("cut");
				try {
					currentDiagramEditor.getController().cut();
				} catch (CutException e1) {
					e1.printStackTrace();
				}
			}
		}, new Synchronizer() {
			@Override
			public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
				if (observable instanceof DianaDrawingEditor) {
					menuItem.setEnabled(((DianaDrawingEditor) observable).isCutable());
				}
			}
		});

		pasteItem = makeSynchronizedMenuItem("paste", PASTE_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_V, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("paste");
				if (currentDiagramEditor != null) {
					try {
						currentDiagramEditor.getController().paste();
					} catch (PasteException e1) {
						e1.printStackTrace();
					}
				}
			}
		}, new Synchronizer() {
			@Override
			public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
				if (observable instanceof DianaInteractiveViewer) {
					menuItem.setEnabled(currentDiagramEditor.getController().isPastable());
				}
			}
		});

		undoItem = makeSynchronizedMenuItem("undo", UNDO_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_Z, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("undo");
				currentDiagramEditor.getController().undo();
			}
		}, new Synchronizer() {
			@Override
			public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
				if (observable instanceof UndoManager) {
					menuItem.setEnabled(currentDiagramEditor.getController().canUndo());
					if (currentDiagramEditor.getController().canUndo()) {
						menuItem.setText(currentDiagramEditor.getController().getFactory().getUndoManager().getUndoPresentationName());
					} else {
						menuItem.setText(FlexoLocalization.localizedForKey(LOCALIZATION, "undo"));
					}
				}
			}
		});

		redoItem = makeSynchronizedMenuItem("redo", REDO_ICON, KeyStroke.getKeyStroke(KeyEvent.VK_R, META_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("redo");
				currentDiagramEditor.getController().redo();
			}
		}, new Synchronizer() {
			@Override
			public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem) {
				if (observable instanceof UndoManager) {
					menuItem.setEnabled(currentDiagramEditor.getController().canRedo());
					if (currentDiagramEditor.getController().canRedo()) {
						menuItem.setText(currentDiagramEditor.getController().getFactory().getUndoManager().getRedoPresentationName());
					} else {
						menuItem.setText(FlexoLocalization.localizedForKey(LOCALIZATION, "redo"));
					}
				}
			}
		});

		editMenu.add(copyItem);
		editMenu.add(cutItem);
		editMenu.add(pasteItem);
		editMenu.addSeparator();
		editMenu.add(undoItem);
		editMenu.add(redoItem);

		WindowMenuItem foregroundInspectorItem = new WindowMenuItem(
				FlexoLocalization.localizedForKey(LOCALIZATION, "foreground_inspector"), inspectors.getForegroundStyleInspector());
		WindowMenuItem backgroundInspectorItem = new WindowMenuItem(
				FlexoLocalization.localizedForKey(LOCALIZATION, "background_inspector"), inspectors.getBackgroundStyleInspector());
		WindowMenuItem textInspectorItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "text_inspector"),
				inspectors.getTextStyleInspector());
		WindowMenuItem shapeInspectorItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "shape_inspector"),
				inspectors.getShapeInspector());
		WindowMenuItem connectorInspectorItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "connector_inspector"),
				inspectors.getConnectorInspector());
		WindowMenuItem shadowInspectorItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "shadow_inspector"),
				inspectors.getShadowStyleInspector());
		WindowMenuItem locationSizeInspectorItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION,
				"location_size_inspector"), inspectors.getLocationSizeInspector());

		// WindowMenuItem paletteItem = new WindowMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, "palette"), paletteDialog);

		viewMenu.add(foregroundInspectorItem);
		viewMenu.add(backgroundInspectorItem);
		viewMenu.add(textInspectorItem);
		viewMenu.add(shapeInspectorItem);
		viewMenu.add(connectorInspectorItem);
		viewMenu.add(shadowInspectorItem);
		viewMenu.add(locationSizeInspectorItem);
		// viewMenu.addSeparator();
		// viewMenu.add(paletteItem);

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

		toolsMenu.add(logsItem);
		toolsMenu.add(localizedItem);

		mb.add(fileMenu);
		mb.add(editMenu);
		mb.add(viewMenu);
		mb.add(toolsMenu);
		mb.add(helpMenu);

		frame.setJMenuBar(mb);

		frame.getContentPane().add(splitPanel);
		frame.validate();
		frame.pack();

	}

	public JFrame getFrame() {
		return frame;
	}

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

	public SwingToolFactory getToolFactory() {
		return toolFactory;
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

		// DianaEditor controller = new DianaEditor(diagramEditor.getEditedDrawing(), diagramEditor.getFactory());
		// AbstractDianaEditor<DiagramDrawing> controller = new AbstractDianaEditor<DiagramDrawing>(aDrawing, factory)

		tabbedPane.add(diagramEditor.getTitle(), new MyDrawingViewScrollPane(diagramEditor.getController().getDrawingView()));
		// diagramEditor.getController().getToolbox().getForegroundInspector().setVisible(true);
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
		currentDiagramEditor = diagramEditor;
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

		logger.info("Switch to editor " + diagramEditor);

		/*if (currentDiagramEditor != null) {
			// mainPanel.remove(currentDiagramEditor.getController().getScalePanel());
			currentDiagramEditor.getController().deleteObserver(inspector);
		}*/

		conceptBrowser.setDiagramEditor(diagramEditor);
		representedConceptBrowser.setDiagramEditor(diagramEditor);

		currentDiagramEditor = diagramEditor;
		toolSelector.attachToEditor(diagramEditor.getController());
		stylesWidget.attachToEditor(diagramEditor.getController());
		scaleSelector.attachToEditor(diagramEditor.getController());
		layoutWidget.attachToEditor(diagramEditor.getController());
		commonPaletteModel.setEditor(diagramEditor);
		commonPalette.attachToEditor(diagramEditor.getController());
		dynamicPaletteModel.setEditor(diagramEditor);
		inspectors.attachToEditor(diagramEditor.getController());

		/*JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(currentDiagramEditor.getController().getToolbox().getStyleToolBar());
		topPanel.add(currentDiagramEditor.getController().getScalePanel());

		mainPanel.add(topPanel, BorderLayout.NORTH);*/

		inspector.setDiagramEditor(diagramEditor);
		inspector.switchToEmptyContent();
		conceptBrowser.setDataObject(diagramEditor.getDiagram().getDataModel());
		representedConceptBrowser.setDataObject(diagramEditor.getDiagram().getDataModel());

		copyItem.synchronizeWith(diagramEditor.getController());
		cutItem.synchronizeWith(diagramEditor.getController());
		pasteItem.synchronizeWith(diagramEditor.getController());

		undoItem.synchronizeWith(diagramEditor.getController().getFactory().getUndoManager());
		redoItem.synchronizeWith(diagramEditor.getController().getFactory().getUndoManager());

		// currentDiagramEditor.getController().addObserver(inspector);
		updateFrameTitle();
		mainPanel.revalidate();
		mainPanel.repaint();
	}

	private void updateFrameTitle() {
		frame.setTitle("Free Modelling Editor - " + currentDiagramEditor.getTitle());
	}

	private void updateTabTitle() {
		tabbedPane.setTitleAt(diagramEditors.indexOf(currentDiagramEditor), currentDiagramEditor.getTitle());
	}

	public void showMainPanel() {

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
		DiagramEditor newDiagramEditor = DiagramEditor.newDiagramEditor(factory, this);
		addDiagramEditor(newDiagramEditor);
	}

	public void loadDiagramEditor() {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			DiagramEditor loadedDiagramEditor = DiagramEditor.loadDiagramEditor(file, factory, this);
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
			if (!file.getName().endsWith(".fm")) {
				file = new File(file.getParentFile(), file.getName() + ".fm");
			}
			currentDiagramEditor.setFile(file);
			updateFrameTitle();
			updateTabTitle();
			return currentDiagramEditor.save();
		} else {
			return false;
		}
	}

	public FIBInspectorController getInspector() {
		return inspector;
	}

	public DiagramEditor getCurrentDiagramEditor() {
		return currentDiagramEditor;
	}

	public DynamicPalette getDynamicPaletteModel() {
		return dynamicPaletteModel;
	}

	public ConceptBrowser getConceptBrowser() {
		return conceptBrowser;
	}

	public RepresentedConceptBrowser getRepresentedConceptBrowser() {
		return representedConceptBrowser;
	}

	private JMenuItem makeJMenuItem(String actionName, Icon icon, KeyStroke accelerator, AbstractAction action) {

		JMenuItem returned = new JMenuItem(FlexoLocalization.localizedForKey(LOCALIZATION, actionName));
		returned.addActionListener(action);
		returned.setIcon(icon);
		returned.setAccelerator(accelerator);
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(accelerator, actionName);
		frame.getRootPane().getActionMap().put(actionName, action);
		return returned;
	}

	private SynchronizedMenuItem makeSynchronizedMenuItem(String actionName, Icon icon, KeyStroke accelerator, AbstractAction action,
			Synchronizer synchronizer) {

		String localizedName = FlexoLocalization.localizedForKey(LOCALIZATION, actionName);
		SynchronizedMenuItem returned = new SynchronizedMenuItem(localizedName, synchronizer);
		action.putValue(Action.NAME, localizedName);
		returned.setAction(action);
		returned.setIcon(icon);
		returned.setAccelerator(accelerator);
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(accelerator, actionName);
		frame.getRootPane().getActionMap().put(actionName, action);
		returned.setEnabled(false);
		return returned;
	}

	public interface Synchronizer {
		public void synchronize(HasPropertyChangeSupport observable, SynchronizedMenuItem menuItem);
	}

	public class SynchronizedMenuItem extends JMenuItem implements PropertyChangeListener {

		private HasPropertyChangeSupport observable;
		private Synchronizer synchronizer;

		public SynchronizedMenuItem(String menuName, Synchronizer synchronizer) {
			super(menuName);
			this.synchronizer = synchronizer;
		}

		public void synchronizeWith(HasPropertyChangeSupport anObservable) {
			if (this.observable != null) {
				manager.removeListener(this, this.observable);
			}
			manager.addListener(this, anObservable);
			observable = anObservable;
			synchronizer.synchronize(observable, this);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			synchronizer.synchronize(observable, this);
		}

		@Override
		public void setEnabled(boolean b) {
			super.setEnabled(b);
			getAction().setEnabled(b);
		}

	}

	public class WindowMenuItem extends JCheckBoxMenuItem implements WindowListener {

		private Window window;

		public WindowMenuItem(String menuName, Window aWindow) {
			super(menuName);
			this.window = aWindow;
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					window.setVisible(!window.isVisible());
				}
			});
			aWindow.addWindowListener(this);
		}

		@Override
		public void windowOpened(WindowEvent e) {
			setState(true);
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// setState(false);
		}

		@Override
		public void windowClosing(WindowEvent e) {
			setState(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			setState(false);
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// setState(true);
		}

	}

	// Actions icons
	public static final ImageIcon UNDO_ICON = new ImageIconResource("Icons/Undo.png");
	public static final ImageIcon REDO_ICON = new ImageIconResource("Icons/Redo.png");
	public static final ImageIcon COPY_ICON = new ImageIconResource("Icons/Copy.png");
	public static final ImageIcon PASTE_ICON = new ImageIconResource("Icons/Paste.png");
	public static final ImageIcon CUT_ICON = new ImageIconResource("Icons/Cut.png");
	public static final ImageIcon DELETE_ICON = new ImageIconResource("Icons/Delete.png");
	public static final ImageIcon HELP_ICON = new ImageIconResource("Icons/Help.png");
	public static final ImageIcon IMPORT_ICON = new ImageIconResource("Icons/Import.png");
	public static final ImageIcon EXPORT_ICON = new ImageIconResource("Icons/Export.png");
	public static final ImageIcon OPEN_ICON = new ImageIconResource("Icons/Open.png");
	public static final ImageIcon NEW_ICON = new ImageIconResource("Icons/New.png");
	public static final ImageIcon PRINT_ICON = new ImageIconResource("Icons/Print.png");
	public static final ImageIcon SAVE_ICON = new ImageIconResource("Icons/Save.png");
	public static final ImageIcon SAVE_DISABLED_ICON = new ImageIconResource("Icons/Save-disabled.png");
	public static final ImageIcon SAVE_AS_ICON = new ImageIconResource("Icons/Save-as.png");
	public static final ImageIcon SAVE_ALL_ICON = new ImageIconResource("Icons/Save-all.png");
	public static final ImageIcon NETWORK_ICON = new ImageIconResource("Icons/Network.png");
	public static final ImageIcon INFO_ICON = new ImageIconResource("Icons/Info.png");
	public static final ImageIcon INSPECT_ICON = new ImageIconResource("Icons/Inspect.png");
	public static final ImageIcon REFRESH_ICON = new ImageIconResource("Icons/Refresh.png");
	public static final ImageIcon REFRESH_DISABLED_ICON = new ImageIconResource("Icons/Refresh-disabled.png");

}
