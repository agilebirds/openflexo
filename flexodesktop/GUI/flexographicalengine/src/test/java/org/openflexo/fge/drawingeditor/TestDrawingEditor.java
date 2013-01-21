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
import org.openflexo.fib.utils.FlexoLoggingViewer;
import org.openflexo.fib.utils.LocalizedDelegateGUIImpl;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.StringEncoder;

public class TestDrawingEditor {

	private static final Logger logger = FlexoLogger.getLogger(TestDrawingEditor.class.getPackage().getName());

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

		TestDrawingEditor editor = new TestDrawingEditor();
		editor.showPanel();
		editor.newDrawing();
	}

	private JFrame frame;
	private JDialog paletteDialog;
	private FlexoFileChooser fileChooser;

	private FIBInspectorController inspector;

	public TestDrawingEditor() {
		super();
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

	private Vector<MyDrawing> _drawings = new Vector<MyDrawing>();
	private JPanel mainPanel;
	private JTabbedPane tabbedPane;

	private MyDrawing currentDrawing;

	private class MyDrawingViewScrollPane extends JScrollPane {
		private MyDrawingView drawingView;

		private MyDrawingViewScrollPane(MyDrawingView v) {
			super(v);
			drawingView = v;
		}
	}

	private void addDrawing(final MyDrawing drawing) {
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
		_drawings.add(drawing);
		tabbedPane.add(drawing.getTitle(), new MyDrawingViewScrollPane(drawing.getEditedDrawing().getController().getDrawingView()));
		switchToDrawing(drawing);

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

	private void removeDrawing(MyDrawing drawing) {

	}

	public void switchToDrawing(MyDrawing drawing) {
		tabbedPane.setSelectedIndex(_drawings.indexOf(drawing));
	}

	private void drawingSwitched(MyDrawing drawing) {
		if (currentDrawing != null) {
			mainPanel.remove(currentDrawing.getEditedDrawing().getController().getScalePanel());
			currentDrawing.getEditedDrawing().getController().deleteObserver(inspector);
		}
		currentDrawing = drawing;

		/*JPanel topPanel = new JPanel(new BorderLayout());
		JPanel topPanel2 = new JPanel(new BorderLayout());
		topPanel2.add(new JLabel("prout"), BorderLayout.CENTER);
		topPanel.add(topPanel2, BorderLayout.CENTER);
		topPanel.add(currentDrawing.getEditedDrawing().getController().getToolbox().getToolboxPanel(), BorderLayout.WEST);
		topPanel2.add(currentDrawing.getEditedDrawing().getController().getScalePanel(), BorderLayout.EAST);*/
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(currentDrawing.getEditedDrawing().getController().getToolbox().getStyleToolBar());
		topPanel.add(currentDrawing.getEditedDrawing().getController().getScalePanel());

		mainPanel.add(topPanel, BorderLayout.NORTH);
		currentDrawing.getEditedDrawing().getController().addObserver(inspector);
		updateFrameTitle();
		mainPanel.revalidate();
		mainPanel.repaint();
		paletteDialog.getContentPane().removeAll();
		paletteDialog.getContentPane().add(drawing.getEditedDrawing().getController().getPalette().getPaletteView());
		paletteDialog.pack();
	}

	private void updateFrameTitle() {
		frame.setTitle("Basic drawing editor - " + currentDrawing.getTitle());
	}

	private void updateTabTitle() {
		tabbedPane.setTitleAt(_drawings.indexOf(currentDrawing), currentDrawing.getTitle());
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
		if (currentDrawing == null) {
			return;
		}
		if (currentDrawing.hasChanged()) {
			int result = JOptionPane.showOptionDialog(frame, "Would you like to save drawing changes?", "Save changes",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			switch (result) {
			case JOptionPane.YES_OPTION:
				if (!currentDrawing.save()) {
					return;
				}
				break;
			case JOptionPane.NO_OPTION:
				break;
			default:
				return;
			}
		}
		_drawings.remove(currentDrawing);
		tabbedPane.remove(tabbedPane.getSelectedIndex());
		if (_drawings.size() == 0) {
			newDrawing();
		}
	}

	public void newDrawing() {
		MyDrawing newDrawing = MyDrawing.makeNewDrawing();
		addDrawing(newDrawing);
	}

	public void loadDrawing() {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			MyDrawing loadedDrawing = MyDrawing.load(file);
			if (loadedDrawing != null) {
				addDrawing(loadedDrawing);
			}
		}
	}

	public boolean saveDrawing() {
		if (currentDrawing == null) {
			return false;
		}
		if (currentDrawing.file == null) {
			return saveDrawingAs();
		} else {
			return currentDrawing.save();
		}
	}

	public boolean saveDrawingAs() {
		if (currentDrawing == null) {
			return false;
		}
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().endsWith(".drw")) {
				file = new File(file.getParentFile(), file.getName() + ".drw");
			}
			currentDrawing.file = file;
			updateFrameTitle();
			updateTabTitle();
			return currentDrawing.save();
		} else {
			return false;
		}
	}
}
