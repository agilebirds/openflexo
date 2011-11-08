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
package org.openflexo.dm.view.popups;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.openflexo.FlexoCst;
import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.foundation.dm.DMRegExp;
import org.openflexo.foundation.dm.action.CreateDMRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.ModuleLoader;
import org.openflexo.swing.FileSelector;
import org.openflexo.swing.JRadioButtonWithIcon;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ResourceLocator;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;

/**
 * Popup allowing to choose and create a new repository
 * 
 * @author sguerin
 * 
 */
public class AskNewRepositoryDialog extends FlexoDialog implements ActionListener
{

	public static final File EXTERNAL_REPOSITORY_ICON_FILE = new FileResource("Icons/Model/DM/SmallExternalRepository.gif");

	public static final File DM_REPOSITORY_ICON_FILE = new FileResource("Icons/Model/DM/SmallRepository.gif");

	public static final File DM_EOREPOSITORY_ICON_FILE = new FileResource("Icons/Model/DM/SmallEORepository.gif");

	protected JTextField newRepositoryNameTF;

	private JTextArea repositoryDescriptionTA;

	protected FileSelector externalRepositorySelector;

	//protected FileSelector denaliFoundationRepositorySelector;

	protected FileSelector rationalRoseRepositorySelector;

	public static final int CANCEL = 0;

	public static final int VALIDATE = 1;

	protected int returnedStatus = CANCEL;


	protected JPanel choicePanel;

	protected JRadioButton projectRepositoryButton;
	protected JRadioButton projectDatabaseRepositoryButton;
	protected JRadioButton externalRepositoryButton;
	//protected JRadioButton denaliFoundationRepositoryButton;
	protected JRadioButton externalDatabaseRepositoryButton;
	protected JRadioButton rationalRoseRepositoryButton;
	protected JRadioButton thesaurusRepositoryButton;
	protected JRadioButton thesaurusDatabaseRepositoryButton;

	protected JLabel packageNameLabel;
	protected JTextField packageName;

	protected FlexoFrame _flexoFrame;

	protected JButton confirmButton;
	protected JButton cancelButton;
	static File _lastVisitedJarDirectory = new FileResource("Library/JarLibraries");

	private static final boolean isThesaurusCreationAllowed = ModuleLoader.isDevelopperRelease() || ModuleLoader.isMaintainerRelease();
	private static final boolean isRationalRoseCreationAllowed = true;/*ModuleLoader.isDevelopperRelease() || ModuleLoader.isMaintainerRelease();*/
	private String _preselectedType;

	public AskNewRepositoryDialog(CreateDMRepository flexoAction, FlexoFrame owner){
		this(flexoAction,owner,flexoAction.getRepositoryType());
	}

	public AskNewRepositoryDialog(CreateDMRepository flexoAction, FlexoFrame owner, String preselectedType)
	{
		super(owner);
		_preselectedType = preselectedType;
		_flexoFrame = owner;
		_flexoAction = flexoAction;

		setTitle(FlexoLocalization.localizedForKey("creates_new_repository"));
		getContentPane().setLayout(new BorderLayout());

		// Create the radio buttons.
		projectRepositoryButton = new JRadioButtonWithIcon(FlexoLocalization
				.localizedForKey("project_repository"), DM_REPOSITORY_ICON_FILE, true);
		projectRepositoryButton.addActionListener(this);
		projectRepositoryButton.setActionCommand(CreateDMRepository.PROJECT_REPOSITORY);

		projectDatabaseRepositoryButton = new JRadioButtonWithIcon(FlexoLocalization
				.localizedForKey("project_database_repository"), DM_EOREPOSITORY_ICON_FILE);
		projectDatabaseRepositoryButton.addActionListener(this);
		projectDatabaseRepositoryButton
		.setActionCommand(CreateDMRepository.PROJECT_DATABASE_REPOSITORY);

		externalRepositoryButton = new JRadioButtonWithIcon(FlexoLocalization
				.localizedForKey("external_repository"), EXTERNAL_REPOSITORY_ICON_FILE);
		externalRepositoryButton.addActionListener(this);
		externalRepositoryButton.setActionCommand(CreateDMRepository.EXTERNAL_REPOSITORY);

		externalRepositorySelector = new FileSelector(_lastVisitedJarDirectory,
				new FileFilter() {
			@Override
			public boolean accept(File f)
			{
				return f.isDirectory() || f.getName().endsWith(".jar");
			}

			@Override
			public String getDescription()
			{
				return FlexoLocalization.localizedForKey("jar_files");
			}
		}) {
			@Override
			public void fireEditedObjectChanged()
			{
				super.fireEditedObjectChanged();
				if (getEditedObject() != null && newRepositoryNameTF != null) {
					newRepositoryNameTF.setText(getEditedObject().getName());
				}
				externalRepositoryButton.setSelected(true);
				selectRepositoryType(CreateDMRepository.EXTERNAL_REPOSITORY);
				_lastVisitedJarDirectory = getEditedObject().getParentFile();
			}
		};

		/*denaliFoundationRepositoryButton = new JRadioButtonWithIcon(
                FlexoLocalization.localizedForKey("denali_foundation_repository"),
                DM_EOREPOSITORY_ICON_FILE);
        denaliFoundationRepositoryButton.addActionListener(this);
        denaliFoundationRepositoryButton
                .setActionCommand(CreateDMRepository.DENALI_FOUNDATION_REPOSITORY);

        denaliFoundationRepositorySelector = (new FileSelector(new FileResource(
                "Library/DenaliFoundationRepositories"), new FileFilter() {
            public boolean accept(File f)
            {
                return ((f.isDirectory()) && (f.getName().endsWith(".dmrepository")));
            }

            public String getDescription()
            {
                return FlexoLocalization.localizedForKey("denali_foundation_repositories");
            }
        }, JFileChooser.DIRECTORIES_ONLY) {
            public void fireEditedObjectChanged()
            {
                super.fireEditedObjectChanged();
                if ((getEditedObject() != null) && (getEditedObject() instanceof File)
                        && (newRepositoryNameTF != null)) {
                    String newSelectedName = ((File) getEditedObject()).getName();
                    String newName = null;
                    if (newSelectedName.indexOf(".dmrepository") > 0) {
                        newName = newSelectedName.substring(0, newSelectedName
                                .indexOf(".dmrepository"));
                    } else {
                        newName = newSelectedName;
                    }
                    newRepositoryNameTF.setText(newName);
                }
                denaliFoundationRepositoryButton.setSelected(true);
                selectRepositoryType(CreateDMRepository.DENALI_FOUNDATION_REPOSITORY);
            }
        });*/

		externalDatabaseRepositoryButton = new JRadioButtonWithIcon(FlexoLocalization
				.localizedForKey("external_database_repository"), DM_EOREPOSITORY_ICON_FILE);
		externalDatabaseRepositoryButton.addActionListener(this);
		externalDatabaseRepositoryButton
		.setActionCommand(CreateDMRepository.EXTERNAL_DATABASE_REPOSITORY);
		packageNameLabel = new JLabel(FlexoLocalization.localizedForKey("package_name"));
		packageName = new JTextField();
		/*
		 * externalDatabaseRepositorySelector = new FileSelector(null,new
		 * FileFilter() { public boolean accept(File f) { return
		 * (f.getName().endsWith(".eomodeld")); }
		 * 
		 * public String getDescription() { return
		 * FlexoLocalization.localizedForKey("EOMODEL files"); } });
		 */
		if(isRationalRoseCreationAllowed){
			rationalRoseRepositoryButton = new JRadioButtonWithIcon(
					FlexoLocalization.localizedForKey("rational_rose_repository"),
					DM_REPOSITORY_ICON_FILE);
			rationalRoseRepositoryButton.addActionListener(this);
			rationalRoseRepositoryButton
			.setActionCommand(CreateDMRepository.RATIONAL_ROSE_REPOSITORY);

			rationalRoseRepositorySelector = new FileSelector(ResourceLocator.getUserHomeDirectory(), new FileFilter() {
				@Override
				public boolean accept(File f)
				{
					return f.isDirectory() || f.getName().endsWith(".mdl");
				}

				@Override
				public String getDescription()
				{
					return FlexoLocalization.localizedForKey("rational_rose_file");
				}
			}, JFileChooser.FILES_ONLY) {
				@Override
				public void fireEditedObjectChanged()
				{
					super.fireEditedObjectChanged();
					if (getEditedObject() != null && newRepositoryNameTF != null) {
						String newSelectedName = getEditedObject().getName();
						String newName = null;
						if (newSelectedName.indexOf(".mdl") > 0) {
							newName = newSelectedName.substring(0, newSelectedName
									.indexOf(".mdl"));
						} else {
							newName = newSelectedName;
						}
						newRepositoryNameTF.setText(newName);
					}
					rationalRoseRepositoryButton.setSelected(true);
					selectRepositoryType(CreateDMRepository.RATIONAL_ROSE_REPOSITORY);
				}
			};
		}

		if(isThesaurusCreationAllowed){
			thesaurusRepositoryButton = new JRadioButtonWithIcon(FlexoLocalization
					.localizedForKey("thesaurus_repository"), DM_REPOSITORY_ICON_FILE);
			thesaurusRepositoryButton.addActionListener(this);
			thesaurusRepositoryButton.setActionCommand(CreateDMRepository.THESAURUS_REPOSITORY);
			thesaurusRepositoryButton.setEnabled(false);

			thesaurusDatabaseRepositoryButton = new JRadioButtonWithIcon(FlexoLocalization
					.localizedForKey("thesaurus_database_repository"), DM_REPOSITORY_ICON_FILE);
			thesaurusDatabaseRepositoryButton.addActionListener(this);
			thesaurusDatabaseRepositoryButton
			.setActionCommand(CreateDMRepository.THESAURUS_DATABASE_REPOSITORY);
			thesaurusDatabaseRepositoryButton.setEnabled(false);
		}
		ActionListener focusActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() instanceof JRadioButton) {
					if (newRepositoryNameTF.getText()==null || newRepositoryNameTF.getText().trim().length()==0) {
						newRepositoryNameTF.requestFocus();
					} else {
						confirmButton.requestFocus();
					}
				}
			}
		};

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(projectRepositoryButton);
		group.add(projectDatabaseRepositoryButton);
		group.add(externalRepositoryButton);
		//group.add(denaliFoundationRepositoryButton);
		group.add(externalDatabaseRepositoryButton);
		if(isRationalRoseCreationAllowed) {
			group.add(rationalRoseRepositoryButton);
		}
		if(isThesaurusCreationAllowed){
			group.add(thesaurusRepositoryButton);
			group.add(thesaurusDatabaseRepositoryButton);
		}
		projectRepositoryButton.addActionListener(focusActionListener);
		projectDatabaseRepositoryButton.addActionListener(focusActionListener);
		externalRepositoryButton.addActionListener(focusActionListener);
		//group.add(denaliFoundationRepositoryButton);
		externalDatabaseRepositoryButton.addActionListener(focusActionListener);
		if(isRationalRoseCreationAllowed) {
			rationalRoseRepositoryButton.addActionListener(focusActionListener);
		}
		if(isThesaurusCreationAllowed){
			thesaurusRepositoryButton.addActionListener(focusActionListener);
			thesaurusDatabaseRepositoryButton.addActionListener(focusActionListener);
		}
		newRepositoryNameTF = new JTextField(20);
		newRepositoryNameTF.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				performConfirm();
			}

		});

		JPanel topPanel = new JPanel();
		JPanel repositoryNamePanel = new JPanel();
		repositoryNamePanel.setLayout(new FlowLayout());
		repositoryNamePanel.add(new JLabel(FlexoLocalization.localizedForKey("repository_name")));
		repositoryNamePanel.add(newRepositoryNameTF);
		repositoryNamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		repositoryDescriptionTA = new JTextArea(3, 40);
		repositoryDescriptionTA.setLineWrap(true);
		repositoryDescriptionTA.setWrapStyleWord(true);
		repositoryDescriptionTA.setFont(FlexoCst.MEDIUM_FONT);
		repositoryDescriptionTA.setEditable(false);
		repositoryDescriptionTA.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

		topPanel.setLayout(new BorderLayout());
		topPanel.add(repositoryNamePanel, BorderLayout.NORTH);
		topPanel.add(repositoryDescriptionTA, BorderLayout.CENTER);

		init();

		choicePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		confirmButton = new JButton(FlexoLocalization.localizedForKey("validate"));
		cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				returnedStatus = CANCEL;
				dispose();
			}
		});
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				performConfirm();
			}
		});
		if (ToolBox.getPLATFORM()==ToolBox.MACOS) {
			controlPanel.add(cancelButton);
			controlPanel.add(confirmButton);
		} else {
			controlPanel.add(confirmButton);
			controlPanel.add(cancelButton);
		}

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		contentPanel.add(topPanel, BorderLayout.NORTH);
		contentPanel.add(choicePanel, BorderLayout.CENTER);
		contentPanel.add(controlPanel, BorderLayout.SOUTH);

		getContentPane().add(contentPanel, BorderLayout.CENTER);
		getRootPane().setDefaultButton(confirmButton);
		setModal(true);
		validate();
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2 - 100);
		setVisible(true);
	}

	private boolean isUndefinedOrProjectRepository(){
		return _preselectedType==null || _preselectedType.equals(CreateDMRepository.PROJECT_REPOSITORY);
	}

	private boolean isUndefinedOrProjectDBRepository(){
		return _preselectedType==null || _preselectedType.equals(CreateDMRepository.PROJECT_DATABASE_REPOSITORY);
	}

	private boolean isUndefinedOrExternalRepository(){
		return _preselectedType==null || _preselectedType.equals(CreateDMRepository.EXTERNAL_REPOSITORY);
	}
	private boolean isUndefinedOrExternalDBRepository(){
		return _preselectedType==null || _preselectedType.equals(CreateDMRepository.EXTERNAL_DATABASE_REPOSITORY);
	}

	private boolean isUndefinedOrThesaurusRepository(){
		return _preselectedType==null || _preselectedType.equals(CreateDMRepository.THESAURUS_REPOSITORY);
	}

	protected void init()
	{
		choicePanel = new JPanel();
		int lineCount =  _preselectedType==null?4:1;
		if(_preselectedType==null){
			if(isThesaurusCreationAllowed) {
				lineCount=lineCount+2;
			}
		}
		choicePanel.setLayout(new GridLayout(lineCount, 2));
		if(isUndefinedOrProjectRepository()){
			choicePanel.add(projectRepositoryButton);
			choicePanel.add(new JPanel());
		}
		if(isUndefinedOrProjectDBRepository()){
			choicePanel.add(projectDatabaseRepositoryButton);
			choicePanel.add(new JPanel());
		}
		if(isUndefinedOrExternalRepository()){
			choicePanel.add(externalRepositoryButton);
			choicePanel.add(externalRepositorySelector);
		}
		//choicePanel.add(denaliFoundationRepositoryButton);
		//choicePanel.add(denaliFoundationRepositorySelector);
		if(isUndefinedOrExternalDBRepository()){
			choicePanel.add(externalDatabaseRepositoryButton);
			choicePanel.add(new JPanel()/* externalDatabaseRepositorySelector */);
		}
		//choicePanel.add(rationalRoseRepositoryButton);
		//choicePanel.add(rationalRoseRepositorySelector);
		if(isThesaurusCreationAllowed){
			if(isUndefinedOrThesaurusRepository()){
				choicePanel.add(thesaurusRepositoryButton);
				choicePanel.add(new JPanel());
				choicePanel.add(thesaurusDatabaseRepositoryButton);
				choicePanel.add(new JPanel());
			}
		}
		if(_preselectedType==null) {
			selectRepositoryType(CreateDMRepository.PROJECT_REPOSITORY);
		} else {
			selectRepositoryType(_preselectedType);
		}
	}

	public void performConfirm()
	{
		if (checkRepositoryOKForCreation()) {
			returnedStatus = VALIDATE;
			dispose();
		}
		if (returnedStatus == VALIDATE) {
			/*            if (_flexoAction.getRepositoryType().equals(CreateDMRepository.RATIONAL_ROSE_REPOSITORY)) {
                String newPackageName = FlexoController.askForStringMatchingPattern(FlexoLocalization.localizedForKey("please_enter_a_package_name"),DMRegExp.PACKAGE_NAME_PATTERN,FlexoLocalization.localizedForKey("must_start_with_a_letter_followed_by_any_letter_or_number"));
                _flexoAction.setRationalRosePackageName(newPackageName);
            }*/
			if (_flexoAction.getRepositoryType().equals(CreateDMRepository.EXTERNAL_REPOSITORY)) {
				File jarFile = externalRepositorySelector.getEditedFile();
				if (jarFile != null) {
					_flexoAction.makeFlexoProgress(FlexoLocalization
							.localizedForKey("scanning")
							+ " " + jarFile.getName(), 2);
					/*for (Enumeration en=ExternalRepository.getContainedClasses(jarFile,_flexoAction.getFlexoProgress()); en.hasMoreElements();) {
                       System.out.println("Choose or not : "+en.nextElement());
                   }*/
					SelectClassesPopup popup
					= new SelectClassesPopup(FlexoLocalization.localizedForKey("importing")+" "+jarFile.getName(),
							FlexoLocalization.localizedForKey("please_select_classes_to_import"),
							FlexoLocalization.localizedForKey("select_class_for_jar_import_description"),
							jarFile,
							_flexoAction.getProject(),
							_flexoFrame ,
							_flexoAction.getFlexoProgress());
					_flexoAction.hideFlexoProgress();
					popup.setVisible(true);
					if (popup.getStatus() == MultipleObjectSelectorPopup.VALIDATE
							&& popup.getDMSet().getSelectedObjects().size() > 0) {
						_flexoAction.setImportedClassSet(popup.getDMSet());
					}
					else {
						returnedStatus = CANCEL;
					}
				}
			}
		}
	}

	/** Listens to the radio buttons. */

	@Override
	public void actionPerformed(ActionEvent e)
	{
		selectRepositoryType(e.getActionCommand());
	}

	protected void selectRepositoryType(String repType)
	{
		_flexoAction.setRepositoryType(repType);
		if (repType.equals(CreateDMRepository.PROJECT_REPOSITORY)) {
			repositoryDescriptionTA.setText(FlexoLocalization
					.localizedForKey("project_repository_description"));
			projectRepositoryButton.setSelected(true);
		} else if (repType.equals(CreateDMRepository.PROJECT_DATABASE_REPOSITORY)) {
			repositoryDescriptionTA.setText(FlexoLocalization
					.localizedForKey("project_database_repository_description"));
			projectDatabaseRepositoryButton.setSelected(true);
		} else if (repType.equals(CreateDMRepository.EXTERNAL_REPOSITORY)) {
			repositoryDescriptionTA.setText(FlexoLocalization
					.localizedForKey("external_repository_description"));
			externalRepositoryButton.setSelected(true);
		} else if (repType.equals(CreateDMRepository.DENALI_FOUNDATION_REPOSITORY)) {
			repositoryDescriptionTA.setText(FlexoLocalization
					.localizedForKey("denali_foundation_repository_description"));
		} else if (repType.equals(CreateDMRepository.EXTERNAL_DATABASE_REPOSITORY)) {
			repositoryDescriptionTA.setText(FlexoLocalization
					.localizedForKey("external_database_repository_description"));
			externalDatabaseRepositoryButton.setSelected(true);
		} else if (repType.equals(CreateDMRepository.RATIONAL_ROSE_REPOSITORY)) {
			repositoryDescriptionTA.setText(FlexoLocalization
					.localizedForKey("rational_rose_repository_description"));
		} else if (repType.equals(CreateDMRepository.THESAURUS_REPOSITORY)) {
			repositoryDescriptionTA.setText(FlexoLocalization
					.localizedForKey("thesaurus_repository_description"));
			thesaurusRepositoryButton.setSelected(true);
		} else if (repType.equals(CreateDMRepository.THESAURUS_DATABASE_REPOSITORY)) {
			repositoryDescriptionTA.setText(FlexoLocalization
					.localizedForKey("thesaurus_database_repository_description"));
			thesaurusDatabaseRepositoryButton.setSelected(true);
		}
	}

	protected boolean checkRepositoryOKForCreation()
	{
		String newRepositoryName = newRepositoryNameTF.getText();
		if (newRepositoryName == null || newRepositoryName.trim().equals("")) {
			FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_a_valid_name"));
			return false;
		}
		_flexoAction.setNewRepositoryName(newRepositoryName);
		String currentChoice = _flexoAction.getRepositoryType();
		if (currentChoice == null) {
			return false;
		}
		if (_flexoAction.getProject().getDataModel().getRepositoryNamed(newRepositoryName) != null) {
			FlexoController.showError(FlexoLocalization
					.localizedForKey("repository_already_registered"));
			return false;
		}
		if (currentChoice.equals(CreateDMRepository.PROJECT_REPOSITORY)) {
			return true;
		} else if (currentChoice.equals(CreateDMRepository.PROJECT_DATABASE_REPOSITORY)) {
			// FlexoController.showError(FlexoLocalization.localizedForKey("not_implemented_yet"));
			return true;
		} else if (currentChoice.equals(CreateDMRepository.EXTERNAL_REPOSITORY)) {
			File newJarFile = externalRepositorySelector.getEditedFile();
			if (newJarFile != null && newJarFile.exists() && newJarFile.isFile()) {
				_flexoAction.setJarFile(newJarFile);
				return true;
			} else {
				FlexoController.showError(FlexoLocalization
						.localizedForKey("please_supply_a_valid_jar_file"));
				return false;
			}
		} /*else if (currentChoice.equals(CreateDMRepository.DENALI_FOUNDATION_REPOSITORY)) {
            File newDenaliFoundationRepositoryFile = denaliFoundationRepositorySelector
                    .getEditedFile();
            if (newDenaliFoundationRepositoryFile != null) {
                _flexoAction.setDenaliFoundationRepositoryFile(newDenaliFoundationRepositoryFile);
                return true;
            } else {
                FlexoController.showError(FlexoLocalization
                        .localizedForKey("please_supply_a_valid_directory"));
                return false;
            }
        } */else if (currentChoice.equals(CreateDMRepository.EXTERNAL_DATABASE_REPOSITORY)) {
        	// FlexoController.showError(FlexoLocalization.localizedForKey("not_implemented_yet"));
        	return true;
        } else if (currentChoice.equals(CreateDMRepository.RATIONAL_ROSE_REPOSITORY)) {
        	File rrFile = rationalRoseRepositorySelector.getEditedFile();
        	if (rrFile != null && rrFile.exists() && rrFile.isFile()) {
        		if (packageName.getText()!=null && packageName.getText().matches(DMRegExp.PACKAGE_NAME_REGEXP)) {
        			_flexoAction.setRationalRoseFile(rrFile);
        			_flexoAction.setRationalRosePackageName(packageName.getText());
        			// DONT DO IT HERE, OTHERWISE DEADLOCK IN SWING !!!!
        			// Wait after dispose() !!!!
        			//String newPackageName = FlexoController.askForStringMatchingPattern(FlexoLocalization.localizedForKey("please_enter_a_package_name"),DMRegExp.PACKAGE_NAME_PATTERN,FlexoLocalization.localizedForKey("must_start_with_a_letter_followed_by_any_letter_or_number"));
        			//_flexoAction.setRationalRosePackageName(newPackageName);
        			return true;
        		} else {
        			FlexoController.showError(FlexoLocalization
        					.localizedForKey("package_must_start_with_a_letter_followed_by_any_letter_or_number"));
        			return false;
        		}
        	} else {
        		FlexoController.showError(FlexoLocalization
        				.localizedForKey("please_supply_a_valid_mdl_file"));
        		return false;
        	}
        } else if (currentChoice.equals(CreateDMRepository.THESAURUS_REPOSITORY)) {
        	FlexoController.showError(FlexoLocalization.localizedForKey("not_implemented_yet"));
        	return false;
        } else if (currentChoice.equals(CreateDMRepository.THESAURUS_DATABASE_REPOSITORY)) {
        	FlexoController.showError(FlexoLocalization.localizedForKey("not_implemented_yet"));
        	return false;
        }
		return false;
	}

	protected int getStatus()
	{
		return returnedStatus;
	}

	protected CreateDMRepository _flexoAction;

	public static int displayDialog(CreateDMRepository flexoAction, FlexoProject project,
			FlexoFrame owner)
	{
		flexoAction.setProject(project);
		AskNewRepositoryDialog dialog = new AskNewRepositoryDialog(flexoAction, owner);
		return dialog.getStatus();
	}

}
