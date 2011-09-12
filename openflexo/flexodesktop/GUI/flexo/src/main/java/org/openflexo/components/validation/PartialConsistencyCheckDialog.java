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
package org.openflexo.components.validation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JTabbedPane;

import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.controller.ConsistencyCheckingController;
import org.openflexo.view.controller.FlexoController;

/**
 * Defines a modal window allowing to perform check consistency, edit validation
 * report, and fix issues. This window is generally used to make partial consistency
 * checking on the model after a refactoring action.
 * 
 * @author sguerin
 * 
 */
public class PartialConsistencyCheckDialog extends FlexoDialog implements ConsistencyCheckDialogInterface
{

    private ConsistencyCheckingController _controller;

    private ValidationReportEditor _validationReportEditor;

    private ValidationModelViewer _validationModelViewer;

    public PartialConsistencyCheckDialog(String title, ConsistencyCheckingController controller,ValidationReport validationReport)
    {
        super(FlexoController.getActiveFrame(), true);
        setTitle(title);
        getContentPane().setLayout(new BorderLayout());
        _validationReportEditor = new ValidationReportEditor(this, validationReport)
        {
            @Override
			public String getCloseButtonName()
            {
                return "done";
            }
            
            @Override
			public void close()
            {
                _consistencyCheckDialog.dispose();       
            }
            
            @Override
			public void checkAgain()
            {
                if (_validationReport.getRootObject() != null) {
                    consistencyCheckWithValidationModel(_validationReport.getRootObject(),_validationReport.getValidationModel());
                    if (_validationReport.getRowCount() > 0) {
                        setCurrentIssue(_validationReport.getIssueAt(_validationReport.getRowCount() - 1));
                    }
                }       
            }           
        };
        _validationModelViewer = new ValidationModelViewer(this, validationReport.getValidationModel());
        _controller = controller;
        _validationModelViewer.setValidationModel(validationReport.getValidationModel());
                
        JTabbedPane contentPanel = new JTabbedPane();
        contentPanel.add(FlexoLocalization.localizedForKey("validation_report"), _validationReportEditor);
        contentPanel.add(FlexoLocalization.localizedForKey("validation_model"), _validationModelViewer);

        getContentPane().add(contentPanel, BorderLayout.CENTER);

        setSize(600, 500);
        validate();
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
    }

    @Override
	public ConsistencyCheckingController getController()
    {
        return _controller;
    }

    public void setValidationReport(ValidationReport validationReport)
    {
        _validationReportEditor.setValidationReport(validationReport);
        _validationModelViewer.setValidationModel(validationReport.getValidationModel());
    }

    public ValidationReport getValidationReport()
    {
        return _validationReportEditor.getValidationReport();
    }

    public void consistencyCheck(Validable objectToValidate)
    {
        _validationReportEditor.consistencyCheckWithDefaultValidationModel(objectToValidate);
        _validationModelViewer.setValidationModel(_validationReportEditor.getValidationReport().getValidationModel());

    }

}
