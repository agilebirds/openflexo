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
package org.openflexo.foundation.exec;
import java.io.File;
import java.util.logging.Logger;


import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.java.JavaFormattingException;
import org.openflexo.antar.java.JavaPrettyPrinter;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.exec.InvalidModelException;
import org.openflexo.foundation.exec.NodeActivation;
import org.openflexo.foundation.exec.NotSupportedException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.logging.FlexoLogger;


public class FlexoExecutionModelTestCase extends FlexoTestCase {

	private static final Logger logger = FlexoLogger.getLogger(FlexoExecutionModelTestCase.class.getPackage().getName());

	private static FlexoEditor _editor;
	private static FlexoProject _project;

	private JavaPrettyPrinter prettyPrinter;
	
	public FlexoExecutionModelTestCase(String name) 
	{
		super(name);
	}

    @Override
	protected void setUp() throws Exception 
    {
        super.setUp();
        prettyPrinter = new JavaPrettyPrinter();
    }
	
	public void test0LoadProject()
	{
		File projectFile = getResource("TestExecutionModel.prj");
		logger.info("Found project "+projectFile.getAbsolutePath());
		_editor = reloadProject(projectFile);
		_project = _editor.getProject();
		logger.info("Successfully loaded project "+projectFile.getAbsolutePath());
		
		 process = _project.getFlexoWorkflow().getRootProcess();
		 activity1 = (ActivityNode)process.getAbstractActivityNodeNamed("Activity1");
		 activity2 = (ActivityNode)process.getAbstractActivityNodeNamed("Activity2");
		 operation1 = activity1.getOperationNodeNamed("Operation1");
		 operation2 = activity1.getOperationNodeNamed("Operation2");
		 action1 = operation1.getActionNodeNamed("ACTION1");
		 action2 = operation1.getActionNodeNamed("ACTION2");
		 action3 = operation1.getActionNodeNamed("ACTION3");
		 action4 = operation2.getActionNodeNamed("ACTION4");

	}
	
	private static FlexoProcess process;
	private static ActivityNode activity1;
	@SuppressWarnings("unused")
	private static ActivityNode activity2;
	private static OperationNode operation1;
	private static OperationNode operation2;
	private static ActionNode action1;
	private static ActionNode action2;
	private static ActionNode action3;
	private static ActionNode action4;

	private String normalizeAndPrettyPrint(ControlGraph controlGraph)
	{
		String code = prettyPrinter.getStringRepresentation(controlGraph.normalize());
		
		try {
			return JavaPrettyPrinter.formatJavaCodeAsInlineCode(code)+"\n";
		} catch (JavaFormattingException e) {
			e.printStackTrace();
			return code;
		}
	}
	
	public void test1()
	{	
		try {
			logger.info("Activate ACTION1 gives:\n"+normalizeAndPrettyPrint(NodeActivation.activateNode(action1,false)));
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test2()
	{	
		try {
			logger.info("Activate ACTION2 gives:\n"+normalizeAndPrettyPrint(NodeActivation.activateNode(action2,false)));
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test3()
	{	
		try {
			logger.info("Activate ACTION3 gives:\n"+normalizeAndPrettyPrint(NodeActivation.activateNode(action3,false)));
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test4()
	{	
		try {
			logger.info("Activate ACTION4 gives:\n"+normalizeAndPrettyPrint(NodeActivation.activateNode(action4,false)));
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
