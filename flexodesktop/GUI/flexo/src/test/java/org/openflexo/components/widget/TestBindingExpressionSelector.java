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
package org.openflexo.components.widget;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.openflexo.swing.DateSelector;
import org.openflexo.swing.DurationSelector;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.toolbox.Duration;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.Duration.DurationUnit;


import org.openflexo.components.widget.DMTypeSelector;
import org.openflexo.components.widget.DomainSelector;
import org.openflexo.components.widget.KeySelector;
import org.openflexo.components.widget.binding.BindingSelector;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;


public class TestBindingExpressionSelector {

	private static final Logger logger = FlexoLogger.getLogger(TestBindingExpressionSelector.class.getPackage().getName());

	private FlexoEditor _editor;
	private FlexoProject _project;

	private FlexoProcess process;
	private IFOperator operatorIF;
	private ActivityNode activity1;
	private OperationNode operation1;

    protected static final FlexoEditorFactory EDITOR_FACTORY = new FlexoEditorFactory() {
		@Override
		public DefaultFlexoEditor makeFlexoEditor(FlexoProject project) {
			return new DefaultFlexoEditor(project);
		}
    };

	public static void main(String[] args)
	{
		try {
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

		TestBindingExpressionSelector test = new TestBindingExpressionSelector();
		test.loadProject();
		test.showPanel();
	}

	public void loadProject()
	{
		FileResource projectFile = new FileResource("src/test/resources/TestExecutionModel.prj");
		logger.info("Found project "+projectFile.getAbsolutePath());
		try {
			_editor = FlexoResourceManager.initializeExistingProject(projectFile,EDITOR_FACTORY,null);
		} catch (ProjectLoadingCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProjectInitializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_project = _editor.getProject();
		logger.info("Successfully loaded project "+projectFile.getAbsolutePath());

		 process = _project.getFlexoWorkflow().getRootProcess();
		 operatorIF = (IFOperator)process.getActivityPetriGraph().getOperatorNodeNamed("IF");
		 activity1 = process.getActivityPetriGraph().getActivityNodeNamed("Activity1");
		 operation1 = activity1.getOperationNodeNamed("Operation1");

		 System.out.println("Operateur: "+operatorIF);

	}

	public void showPanel()
	{
		final JDialog dialog = new JDialog((Frame)null,false);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				System.exit(0);
			}
		});

		JButton logButton = new JButton("Logs");
		logButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingManager.showLoggingViewer();
			}
		});

		AbstractBinding bv = operatorIF.getConditionPrimitive();

		BindingSelector _selector = new BindingSelector(null)
		{
            @Override
			public void apply()
            {
            	super.apply();
            	System.out.println("Apply, getEditedObject()="+getEditedObject());
            }

            @Override
			public void cancel()
            {
               	super.cancel();
               	System.out.println("Cancel, getEditedObject()="+getEditedObject());
           }
		};
		_selector.setBindable((Bindable)bv.getOwner());
		_selector.setBindingDefinition(bv.getBindingDefinition());
		_selector.setEditedObject(bv);
		_selector.setRevertValue(bv.clone());

		AbstractBinding bv1 = operation1.getComponentInstance().getBindings().firstElement().getBindingValue();
		BindingSelector selector1 = new BindingSelector(bv1);
		selector1.setEditedObject(bv1);
		selector1.setRevertValue(bv1.clone());

		BindingDefinition def2 = new BindingDefinition("aBoolean",DMType.makeBooleanDMType(_project),operatorIF,BindingDefinitionType.GET,true);
		BindingValue bv2 = new BindingValue(def2,operatorIF);
		BindingSelector selector2 = new BindingSelector(bv2);

		BindingDefinition def2bis = new BindingDefinition("aMethod",null,operatorIF,BindingDefinitionType.EXECUTE,true);
		BindingValue bv2bis = new BindingValue(def2bis,operatorIF);
		BindingSelector selector2bis = new BindingSelector(bv2bis);
		selector2bis.activateCompoundBindingMode();

		BindingDefinition def2ter = new BindingDefinition("aSetBV",DMType.makeBooleanDMType(_project),operatorIF,BindingDefinitionType.SET,true);
		BindingValue bv2ter = new BindingValue(def2ter,operatorIF);
		BindingSelector selector2ter = new BindingSelector(bv2ter);

		BindingDefinition def3 = new BindingDefinition("anInteger",DMType.makeLongDMType(_project),operatorIF,BindingDefinitionType.GET,true);
		BindingValue bv3 = new BindingValue(def3,operatorIF);
		BindingSelector selector3 = new BindingSelector(bv3);

		BindingDefinition def4 = new BindingDefinition("aFloat",DMType.makeDoubleDMType(_project),operatorIF,BindingDefinitionType.GET,true);
		BindingValue bv4 = new BindingValue(def4,operatorIF);
		BindingSelector selector4 = new BindingSelector(bv4);

		BindingDefinition def5 = new BindingDefinition("aString",DMType.makeStringDMType(_project),operatorIF,BindingDefinitionType.GET,true);
		BindingValue bv5 = new BindingValue(def5,operatorIF);
		BindingSelector selector5 = new BindingSelector(bv5) {
			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return "TEST";
			}
		};

		BindingDefinition def6 = new BindingDefinition("aDate",DMType.makeDateDMType(_project),operatorIF,BindingDefinitionType.GET,true);
		BindingValue bv6 = new BindingValue(def6,operatorIF);
		BindingSelector selector6 = new BindingSelector(bv6);

		BindingDefinition def7 = new BindingDefinition("aDuration",DMType.makeDurationDMType(_project),operatorIF,BindingDefinitionType.GET,true);
		BindingValue bv7 = new BindingValue(def7,operatorIF);
		BindingSelector selector7 = new BindingSelector(bv7);

		BindingDefinition def8 = new BindingDefinition("anObject",DMType.makeObjectDMType(_project),operatorIF,BindingDefinitionType.GET,true);
		BindingValue bv8 = new BindingValue(def8,operatorIF);
		BindingSelector selector8 = new BindingSelector(bv8);

		JPanel panel = new JPanel(new VerticalLayout());
		panel.add(_selector);
		panel.add(selector1);
		panel.add(selector2);
		panel.add(selector2bis);
		panel.add(selector2ter);
		panel.add(selector3);
		panel.add(selector4);
		panel.add(selector5);
		panel.add(selector6);
		panel.add(selector7);
		panel.add(selector8);
		panel.add(new DateSelector(new Date()));
		panel.add(new DurationSelector(new Duration(12,DurationUnit.DAYS)));
		final KeySelector keySelector = new KeySelector(_project,null);
		panel.add(new DomainSelector(_project,null) {
			@Override
			public void apply() {
				super.apply();
				keySelector.setDomain(getEditedObject());
			}
		});

		panel.add(keySelector);
		panel.add(new DMTypeSelector(_project,null,true));

		BindingDefinition def9 = new BindingDefinition("aDKV",DMType.makeDKVDMType(_project.getDKVModel().getDomains().elementAt(1)),operatorIF,BindingDefinitionType.GET,true);
		BindingValue bv9 = new BindingValue(def9,operatorIF);
		BindingSelector selector9 = new BindingSelector(bv9);
		panel.add(selector9);


		panel.add(closeButton);
		panel.add(logButton);

		dialog.setPreferredSize(new Dimension(550,600));
		dialog.getContentPane().add(panel);
		dialog.validate();
		dialog.pack();

		dialog.setVisible(true);

	}


}
