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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.controller.FIBEditorPalette;
import org.openflexo.fib.editor.controller.FIBInspectorController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImplementation;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.DuplicateSerializationIdentifierException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;


public class FIBEditor implements FIBGenericEditor {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditor.class.getPackage().getName());

	public static LocalizedDelegate LOCALIZATION = new LocalizedDelegateImplementation(new FileResource("FIBEditorLocalized"));

	public static File COMPONENT_LOCALIZATION_FIB = new FileResource("Fib/ComponentLocalization.fib");


	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
	UnsupportedLookAndFeelException
	{
		//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
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
			ToolBox.setPlatform();
			FlexoLoggingManager.initialize();
			FlexoLoggingManager.setKeepLogTrace(true);
			FlexoLoggingManager.setLogCount(-1);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FIBEditor editor = new FIBEditor();
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

	private final JFrame frame;
	private final FIBEditorPalette palette;
	private final FlexoFileChooser fileChooser;

	private final FIBInspectorController inspector;

	private FIBEditorController editorController;

	@Override
	public FIBInspectorController getInspector() {
		return inspector;
	}

	@Override
	public FIBEditorPalette getPalette() {
		return palette;
	}

	public class EditedFIB
	{
		private String title;
		private File fibFile;
		private final FIBComponent fibComponent;

		public EditedFIB(String title, File fibFile, FIBComponent fibComponent)
		{
			super();
			this.title = title;
			this.fibFile = fibFile;
			this.fibComponent = fibComponent;
		}

		public void save()
		{
			logger.info("Save to file "+fibFile.getAbsolutePath());

			FIBLibrary.save(fibComponent, fibFile);

		}
	}

	private EditedFIB editedFIB;

	public FIBEditor()
	{
		super();
		frame = new JFrame();
		frame.setPreferredSize(new Dimension(1000,800));
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
		fileChooser.setCurrentDirectory(new FileResource("TestFIB"));

		inspector = new FIBInspectorController(frame);

		palette = new FIBEditorPalette(frame);
		palette.setVisible(true);
	}

	private MainPanel mainPanel;

	private void updateFrameTitle()
	{
		frame.setTitle("Flexo Interface Builder Editor");
	}

	public void showPanel()
	{
		frame.setTitle("Flexo Interface Builder Editor");
		mainPanel = new MainPanel();

		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"file"));
		JMenu editMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"edit"));
		JMenu toolsMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"tools"));
		JMenu helpMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"help"));


		JMenuItem newItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"new_interface"));
		newItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFIB();
			}
		});

		JMenuItem loadItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"open_interface"));
		loadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFIB();
			}
		});

		JMenuItem saveItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"save_interface"));
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFIB();
			}
		});

		JMenuItem saveAsItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"save_interface_as"));
		saveAsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFIBAs();
			}
		});

		JMenuItem closeItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"close_interface"));
		closeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeFIB();
			}
		});


		JMenuItem quitItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"quit"));
		quitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});

		JMenuItem testInterfaceItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"test_interface"));
		testInterfaceItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testFIB();
			}
		});

		JMenuItem componentLocalizationItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"component_localization"));
		componentLocalizationItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				localizeFIB();
			}
		});




		fileMenu.add(newItem);
		fileMenu.add(loadItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(closeItem);
		fileMenu.addSeparator();
		fileMenu.add(testInterfaceItem);
		fileMenu.add(componentLocalizationItem);
		JMenu languagesItem = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"switch_to_language"));
		for (Language lang : Language.availableValues()) {
			JMenuItem languageItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,lang.getName()));
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

		JMenuItem inspectItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"inspect"));
		inspectItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getInspector().setVisible(true);
			}
		});

		JMenuItem logsItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"logs"));
		logsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingManager.showLoggingViewer();
			}
		});

		JMenuItem localizedItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"localized_editor"));
		localizedItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLocalization.showLocalizedEditor();
			}
		});

		JMenuItem displayFileItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"display_file"));
		displayFileItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					logger.info("Getting this " + XMLCoder.encodeObjectWithMapping(editedFIB.fibComponent, FIBLibrary.getFIBMapping(),StringEncoder.getDefaultInstance()));
				} catch (InvalidObjectSpecificationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvalidModelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (AccessorInvocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DuplicateSerializationIdentifierException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		toolsMenu.add(inspectItem);
		toolsMenu.add(logsItem);
		toolsMenu.add(localizedItem);
		toolsMenu.add(displayFileItem);

		mb.add(fileMenu);
		mb.add(editMenu);
		mb.add(toolsMenu);
		mb.add(helpMenu);

		frame.setJMenuBar(mb);


		frame.getContentPane().add(mainPanel);

		frame.validate();
		frame.pack();

		/*inspector.getWindow().setLocation(1010,400);
		inspector.getWindow().setPreferredSize(new Dimension(300,300));
		inspector.getWindow().setVisible(true);*/

		frame.setVisible(true);

	}

	public void quit()
	{
		frame.dispose();
		System.exit(0);
	}

	public void closeFIB()
	{
		logger.warning("Not implemented yet");
	}

	public void newFIB()
	{
		FIBPanel fibComponent = new FIBPanel();
		fibComponent.setLayout(Layout.border);

		EditedFIB newEditedFIB = new EditedFIB("New.fib", new File("NewComponent.fib"), fibComponent);

		editorController = new FIBEditorController(fibComponent,this);

		mainPanel.newEditedComponent(newEditedFIB, editorController);

	}

	public void loadFIB()
	{
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File fibFile = fileChooser.getSelectedFile();

			FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(fibFile,false);

			EditedFIB newEditedFIB = new EditedFIB(fibFile.getName(), fibFile, fibComponent);

			editorController = new FIBEditorController(fibComponent,this);

			mainPanel.newEditedComponent(newEditedFIB, editorController);

		}
	}

	public void saveFIB()
	{
		if (editedFIB == null) {
			return;
		}
		if (editedFIB.fibFile == null) {
			saveFIBAs();
		}
		else {
			editedFIB.save();
		}
	}

	public void saveFIBAs()
	{
		if (editedFIB == null) {
			return;
		}
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().endsWith(".fib")) {
				file = new File(file.getParentFile(),file.getName()+".fib");
			}
			editedFIB.fibFile = file;
			editedFIB.title = file.getName();
			mainPanel.setTitleAt(mainPanel.getSelectedIndex(), editedFIB.title);
			updateFrameTitle();
			editedFIB.save();
		}
		else {
			return;
		}
	}

	public void testFIB()
	{
		FIBView view = FIBController.makeView(editedFIB.fibComponent);

		//Class testClass = null;
		if (editedFIB.fibComponent.getDataClass() != null) {
			try {
				//testClass = Class.forName(editedFIB.fibComponent.getDataClassName());
				Object testData = editedFIB.fibComponent.getDataClass().newInstance();
				view.getController().setDataObject(testData);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			view.getController().updateWithoutDataObject();
		}

		JDialog testInterface = new JDialog(frame,"Test",false);
		testInterface.getContentPane().add(view.getResultingJComponent());
		testInterface.validate();
		testInterface.pack();
		testInterface.setVisible(true);
	}

	public void localizeFIB()
	{
		FIBComponent componentLocalizationComponent = FIBLibrary.instance().retrieveFIBComponent(COMPONENT_LOCALIZATION_FIB);

		FIBView view = FIBController.makeView(componentLocalizationComponent);
		view.getController().setDataObject(editorController.getController());
		JDialog localizationInterface = new JDialog(frame,FlexoLocalization.localizedForKey(LOCALIZATION,"component_localization"),false);
		localizationInterface.getContentPane().add(view.getResultingJComponent());
		localizationInterface.validate();
		localizationInterface.pack();
		localizationInterface.setVisible(true);
	}

	public void switchToLanguage(Language lang)
	{
		FlexoLocalization.setCurrentLanguage(lang);
		if (editorController != null) {
			editorController.switchToLanguage(lang);
		}
	}


	@Override
	public JFrame getFrame()
	{
		return frame;
	}

	public class MainPanel extends JTabbedPane implements ChangeListener
	{
		private final Vector<EditedFIB> editedComponents;
		private final Hashtable<EditedFIB,FIBEditorController> controllers;

		public MainPanel()
		{
			super();
			editedComponents = new Vector<EditedFIB>();
			controllers = new Hashtable<EditedFIB, FIBEditorController>();
			addChangeListener(this);
		}

		public void newEditedComponent(EditedFIB edited, FIBEditorController controller)
		{
			editedComponents.add(edited);
			controllers.put(edited,controller);
			add(controller.getEditorPanel(),edited.title);
			frame.validate();
			setSelectedIndex(getComponentCount()-1);
		}

		@Override
		public void stateChanged(ChangeEvent e)
		{
			logger.info("Change for "+e);
			int index = getSelectedIndex();
			editedFIB = editedComponents.get(index);
		}
	}
}

