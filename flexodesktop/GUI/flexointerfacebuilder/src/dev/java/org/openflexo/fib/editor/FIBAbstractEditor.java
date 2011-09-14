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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.controller.FIBEditorPalette;
import org.openflexo.fib.editor.controller.FIBInspectorController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImplementation;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.DuplicateSerializationIdentifierException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;


public abstract class FIBAbstractEditor implements FIBGenericEditor {

	private static final Logger logger = FlexoLogger.getLogger(FIBAbstractEditor.class.getPackage().getName());

	public static LocalizedDelegate LOCALIZATION = new LocalizedDelegateImplementation(new FileResource("FIBEditorLocalized"));
	
	public static File COMPONENT_LOCALIZATION_FIB = new FileResource("Fib/ComponentLocalization.fib");
	
	private final JFrame frame;
	//private JPanel mainPanel;
	private final FIBEditorPalette palette;
	
	private final FIBInspectorController inspector;
	
	private File fibFile;
	private FIBComponent fibComponent;
	private FIBEditorController editorController;

	private final JMenu actionMenu;
	
	public JFrame getFrame()
	{
		return frame;
	}
	
	public FIBInspectorController getInspector() 
	{
		return inspector;
	}

	public FIBEditorPalette getPalette() 
	{
		return palette;
	}


	public FIBAbstractEditor()
	{
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

		frame = new JFrame();
		frame.setPreferredSize(new Dimension(1200,800));
		
		inspector = new FIBInspectorController(frame);
		
		palette = new FIBEditorPalette(frame);
		palette.setVisible(true);

		frame.setTitle("Flexo Interface Builder Editor");
		//mainPanel = new JPanel();

		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"file"));
		JMenu editMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"edit"));
		actionMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"actions"));
		JMenu toolsMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"tools"));
		JMenu helpMenu = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"help"));
		
		
		JMenuItem saveItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"save_interface"));
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFIB();
			}
		});
		
		JMenuItem quitItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"quit"));
		quitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		
		JMenuItem testInterfaceItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"test_interface"));
		testInterfaceItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				testFIB();
			}
		});
		
		JMenuItem componentLocalizationItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"component_localization"));
		componentLocalizationItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				localizeFIB();
			}
		});
		
		fileMenu.add(saveItem);
		fileMenu.add(testInterfaceItem);
		fileMenu.addSeparator();
		
		fileMenu.add(componentLocalizationItem);
		JMenu languagesItem = new JMenu(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"switch_to_language"));
		for (Language lang : Language.availableValues()) {
			JMenuItem languageItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,lang.getName()));
			final Language language = lang;
			languageItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switchToLanguage(language);
				}
			});
			languagesItem.add(languageItem);
		}
		fileMenu.add(languagesItem);
		fileMenu.addSeparator();
		
		for (Object data : getData()) {
			final Object d = data;
			JMenuItem switchDataItem = new JMenuItem(data.toString());
			switchDataItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switchToData(d);
				}
			});
			fileMenu.add(switchDataItem);
			
		}

		fileMenu.addSeparator();
		
		fileMenu.add(quitItem);
		
		JMenuItem inspectItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"inspect"));
		inspectItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inspector.setVisible(true);
			}
		});

		JMenuItem logsItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"logs"));
		logsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingManager.showLoggingViewer();
			}
		});
		
		JMenuItem localizedItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"localized_editor"));
		localizedItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			       FlexoLocalization.showLocalizedEditor();
			}
		});
		
		JMenuItem displayFileItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,"display_file"));
		displayFileItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					logger.info("Getting this " + XMLCoder.encodeObjectWithMapping(fibComponent, FIBLibrary.getFIBMapping(),StringEncoder.getDefaultInstance()));
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
		mb.add(actionMenu);
		mb.add(toolsMenu);
		mb.add(helpMenu);
		
		frame.setJMenuBar(mb);
		
		//frame.getContentPane().add(mainPanel);
		
	}
	
	public abstract Object[] getData();
	
	public abstract File getFIBFile();

	/**
	 * Override when required
	 * @return
	 */
	public FIBController getController()
	{
		return null;
	}
	
	/**
	 * Override when required
	 * @return
	 */
	public FIBController makeNewController()
	{
		return null;
	}
	
	public void loadFIB()
	{
		fibFile = getFIBFile();
		
		fibComponent = FIBLibrary.instance().retrieveFIBComponent(fibFile);

		if (fibComponent == null) {
			logger.log(Level.SEVERE, "Fib component not found ! Path: '" + fibFile.getAbsolutePath() + "'");
			throw new RuntimeException("Fib component not found ! Path: '" + fibFile.getAbsolutePath() + "'");
		}

		if (getController() != null ) {
			editorController = new FIBEditorController(fibComponent,this,getData()[0],getController());
		}
		else {
			editorController = new FIBEditorController(fibComponent,this,getData()[0]);
		}

		frame.getContentPane().add(editorController.getEditorPanel());

		frame.validate();
		frame.pack();


	}
	
	
	public void switchToData(Object data)
	{
		editorController.setDataObject(data);
	}

	public void saveFIB()
	{
		logger.info("Save to file "+fibFile.getAbsolutePath());		
		FIBLibrary.save(fibComponent, fibFile);
	}

	public void testFIB()
	{
		FIBView view;
		FIBController controller = makeNewController();
		if (controller != null) {
			view = FIBController.makeView(fibComponent,controller);
		}
		else {
			view = FIBController.makeView(fibComponent);
		}
		view.getController().setDataObject(editorController.getDataObject());
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
		if (editorController != null) editorController.switchToLanguage(lang);
	}

	public void quit()
	{
		frame.dispose();
		System.exit(0);
	}

	public void launch()
	{
		logger.info(">>>>>>>>>>> Loading FIB...");
		loadFIB();
		frame.setVisible(true);
	}

	public static Object[] makeArray(Object... o)
	{
		return o;
	}

	public void addAction(String string, ActionListener actionListener) 
	{
		JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey(FIBAbstractEditor.LOCALIZATION,string));
		menuItem.addActionListener(actionListener);		
		actionMenu.add(menuItem);

	}
}

