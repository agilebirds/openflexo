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
import org.openflexo.module.FlexoModule;
import org.openflexo.module.ModuleLoader;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.controller.ConsistencyCheckingController;

/**
 * Defines the window allowing to perform check consistency, edit validation
 * report, and fix issues. This window is shared by all the modules.
 *
 * @author sguerin
 *
 */
public class ConsistencyCheckDialog extends FlexoDialog implements ConsistencyCheckDialogInterface
{

    private ConsistencyCheckingController _controller;

    private ValidationReportEditor _validationReportEditor;

    private ValidationModelViewer _validationModelViewer;

    public ConsistencyCheckDialog(ConsistencyCheckingController controller)
    {
        this(controller,new ValidationReport(controller.getDefaultValidationModel()));
    }

    public ConsistencyCheckDialog(ConsistencyCheckingController controller, ValidationReport validationReport)
    {
    	this(controller,validationReport,FlexoLocalization.localizedForKey("consistency_check"));
    }

   public ConsistencyCheckDialog(ConsistencyCheckingController controller, ValidationReport validationReport, String title)
    {
        super(controller!=null?controller.getFlexoFrame():FlexoModule.getActiveModule().getFlexoFrame(),false);
        setController(controller);
        //setIconImage(IconLibrary.FLEXO_ICON.getImage());
        setTitle(title);
        getContentPane().setLayout(new BorderLayout());
        _validationReportEditor = new ValidationReportEditor(this, validationReport);
        _validationModelViewer = new ValidationModelViewer(this, validationReport.getValidationModel());

        JTabbedPane contentPanel = new JTabbedPane();
        contentPanel.add(FlexoLocalization.localizedForKey("validation_report"), _validationReportEditor);
        if (ModuleLoader.isDevelopperRelease() || ModuleLoader.isMaintainerRelease())
            contentPanel.add(FlexoLocalization.localizedForKey("validation_model"), _validationModelViewer);

        getContentPane().add(contentPanel, BorderLayout.CENTER);

        setSize(600, 500);
        //setFocusableWindowState(false);
        validate();
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
    }

    public void setController(ConsistencyCheckingController controller)
    {
        _controller = controller;
        if (controller!=null && _validationModelViewer!=null)
            _validationModelViewer.setValidationModel(controller.getDefaultValidationModel());
    }

    @Override
	public ConsistencyCheckingController getController()
    {
        return _controller;
    }

    public void setValidationReport(ValidationReport validationReport)
    {
    	if(_validationReportEditor.getValidationReport()!=null){
    		_validationReportEditor.getValidationReport().delete();
    	}
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

    /**
     * Overrides dispose
     * @see java.awt.Window#dispose()
     */
    @Override
    public void dispose()
    {
    	if (_validationReportEditor!=null && _validationReportEditor.getValidationReport()!=null){
    		_validationReportEditor.getValidationReport().delete();
    	}
        if (_validationModelViewer!=null && _validationModelViewer.getParent()!=null)
            _validationModelViewer.getParent().remove(_validationModelViewer);
        if (_validationReportEditor!=null && _validationReportEditor.getParent()!=null)
            _validationReportEditor.getParent().remove(_validationReportEditor);
        super.dispose();
        if (_controller!=null)
        	_controller.cleanUpValidationModel();
        _controller = null;
        _validationModelViewer = null;
        _validationReportEditor = null;
        isDisposed = true;
    }
    
    private boolean isDisposed = false;

	public boolean isDisposed() 
	{
		return isDisposed;
	}

}
