package org.openflexo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openflexo.components.ProjectChooserComponent;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoDialog;

public class InteractiveFlexoProjectReferenceLoader implements FlexoProjectReferenceLoader {

	private ApplicationContext applicationContext;

	public InteractiveFlexoProjectReferenceLoader(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public FlexoProject loadProject(FlexoProjectReference reference) throws ProjectLoadingCancelledException {
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel name = new JLabel(FlexoLocalization.localizedForKey("project_name") + ":");
		JTextField nameField = new JTextField(reference.getProjectName());
		nameField.setEditable(false);
		JLabel uri = new JLabel(FlexoLocalization.localizedForKey("project_uri") + ":");
		JTextField uriField = new JTextField(reference.getProjectURI());
		uriField.setEditable(false);
		ProjectChooserComponent projectChooserComponent = new ProjectChooserComponent(null) {
		};
		projectChooserComponent.setOpenMode();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(name, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(nameField, gbc);
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		panel.add(uri, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(uriField, gbc);
		panel.add(projectChooserComponent.getComponent(), gbc);
		FlexoDialog dialog = new FlexoDialog();
		dialog.setModal(true);
		dialog.add(panel);
		dialog.setVisible(true);
		File selectedFile = projectChooserComponent.getSelectedFile();
		if (selectedFile != null) {
			FlexoEditor editor;
			try {
				editor = applicationContext.getProjectLoader().loadProject(selectedFile);
				if (editor != null) {
					return editor.getProject();
				}
			} catch (ProjectLoadingCancelledException e) {
				e.printStackTrace();
			} catch (ProjectInitializerException e) {
				e.printStackTrace();
			}
		}
		throw new ProjectLoadingCancelledException("project_loading_cancelled_by_user");
	}
}
