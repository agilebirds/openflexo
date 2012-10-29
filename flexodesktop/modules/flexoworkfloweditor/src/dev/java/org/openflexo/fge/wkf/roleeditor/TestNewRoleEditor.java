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
package org.openflexo.fge.wkf.roleeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.inspector.selection.EmptySelection;
import org.openflexo.inspector.selection.MultipleSelection;
import org.openflexo.inspector.selection.UniqueSelection;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileResource;
import org.openflexo.wkf.roleeditor.RoleListRepresentation;
import org.openflexo.wkf.roleeditor.RolePalette;


public class TestNewRoleEditor {

	private static final Logger logger = FlexoLogger.getLogger(TestNewRoleEditor.class.getPackage().getName());

	protected static final FlexoEditorFactory EDITOR_FACTORY = new FlexoEditorFactory() {
		public DefaultFlexoEditor makeFlexoEditor(FlexoProject project) {
			return new DefaultFlexoEditor(project);
		}
	};

	public static FlexoProject loadProject()
	{
		FlexoProject project = null;
		FlexoEditor editor;
		FileResource projectFile = new FileResource("src/dev/resources/TestRoles.prj");
		logger.info("Found project "+projectFile.getAbsolutePath());
		try {
			editor = FlexoResourceManager.initializeExistingProject(projectFile,EDITOR_FACTORY,null);
			project = editor.getProject();
		} catch (ProjectLoadingCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProjectInitializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Successfully loaded project "+projectFile.getAbsolutePath());

		return project;
	}



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

		FlexoProject project = loadProject();
		showPanel(project);
		
	}


	public static DrawingController<RoleListRepresentation> makeRoleModelRepresentation(RoleList roleList)
	{
		final RoleListRepresentation roleListRepresentation = new RoleListRepresentation(roleList,null);
		DrawingController<RoleListRepresentation> returned = new DrawingController<RoleListRepresentation>(roleListRepresentation) {
			public void addToSelectedObjects(GraphicalRepresentation anObject)
			{
				super.addToSelectedObjects(anObject);
				if (getSelectedObjects().size() == 1) {
					setChanged();
					notifyObservers(new UniqueSelection(getSelectedObjects().firstElement(), null));
				}
				else {
					setChanged();
					notifyObservers(new MultipleSelection());
				}
			}
			public void removeFromSelectedObjects(GraphicalRepresentation anObject)
			{
				super.removeFromSelectedObjects(anObject);
				if (getSelectedObjects().size() == 1) {
					setChanged();
					notifyObservers(new UniqueSelection(getSelectedObjects().firstElement(), null));
				}
				else {
					setChanged();
					notifyObservers(new MultipleSelection());
				}
			}
			public void clearSelection()
			{
				super.clearSelection();
				notifyObservers(new EmptySelection());
			}
			@Override
			public void selectDrawing()
			{
				super.selectDrawing();
				setChanged();
				notifyObservers(new UniqueSelection(roleListRepresentation.getDrawingGraphicalRepresentation(), null));
			}
		};

		// Build and register a role palette
		RolePalette rolePalette = new RolePalette();
		returned.registerPalette(rolePalette);
		returned.activatePalette(rolePalette);

		return returned;
	}

	public static void showPanel(FlexoProject project)
	{
		final TestNewRoleInspector inspector = new TestNewRoleInspector();

		DrawingController<RoleListRepresentation> controller = makeRoleModelRepresentation(project.getWorkflow().getRoleList());
		
		final JDialog palette = new JDialog((Frame)null,false);
		palette.getContentPane().add(controller.getPalettes().firstElement().getPaletteView());
		palette.pack();
		palette.setVisible(true);
		
		final JDialog dialog = new JDialog((Frame)null,false);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(controller.getDrawingView()), BorderLayout.CENTER);
		panel.add(controller.getScalePanel(), BorderLayout.NORTH);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				System.exit(0);
			}
		});

		JButton inspectButton = new JButton("Inspect");
		inspectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inspector.getWindow().setVisible(true);
			}
		});

		JButton logButton = new JButton("Logs");
		logButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingManager.showLoggingViewer();
			}
		});


		JPanel controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(closeButton);
		controlPanel.add(inspectButton);
		controlPanel.add(logButton);

		panel.add(controlPanel,BorderLayout.SOUTH);

		dialog.setPreferredSize(new Dimension(550,600));
		dialog.getContentPane().add(panel);
		dialog.validate();
		dialog.pack();

		dialog.setVisible(true);
		
		controller.addObserver(inspector);
		inspector.getWindow().setVisible(true);


	}
	


	/*public static void showPanel() 
	{
		final JDialog dialog = new JDialog((Frame)null,false);

		JPanel panel = new JPanel(new BorderLayout());

		final Drawing d = makeDrawing();
		DrawingController dc = new DrawingController<Drawing>(d) {
			public void addToSelectedObjects(Drawable anObject)
			{
				super.addToSelectedObjects(anObject);
				if (getSelectedObjects().size() == 1) {
					setChanged();
					notifyObservers(new UniqueSelection(anObject.getGraphicalRepresentation(), null));
				}
				else {
					setChanged();
					notifyObservers(new MultipleSelection());
				}
			}
			public void removeFromSelectedObjects(Drawable anObject)
			{
				super.removeFromSelectedObjects(anObject);
				if (getSelectedObjects().size() == 1) {
					setChanged();
					notifyObservers(new UniqueSelection(anObject.getGraphicalRepresentation(), null));
				}
				else {
					setChanged();
					notifyObservers(new MultipleSelection());
				}
			}
			public void clearSelection()
			{
				super.clearSelection();
				notifyObservers(new EmptySelection());
			}
			@Override
			public void selectDrawing()
			{
				super.selectDrawing();
				setChanged();
				notifyObservers(new UniqueSelection(d.getGraphicalRepresentation(), null));
			}
		};
		panel.add(new JScrollPane(dc.getDrawingView()), BorderLayout.CENTER);
		panel.add(dc.getScalePanel(), BorderLayout.NORTH);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
				System.exit(0);
			}
		});

		JButton logButton = new JButton("Logs");
		logButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingManager.showLoggingViewer();
			}
		});


		JPanel controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(closeButton);
		controlPanel.add(logButton);

		panel.add(controlPanel,BorderLayout.SOUTH);

		dialog.setPreferredSize(new Dimension(550,600));
		dialog.getContentPane().add(panel);
		dialog.validate();
		dialog.pack();

		dialog.setVisible(true);

		DrawingController dc2 = new DrawingController(d);		
		final JDialog dialog2 = new JDialog((Frame)null,false);
		dialog2.getContentPane().add(new JScrollPane(dc2.getDrawingView()));
		dialog2.setPreferredSize(new Dimension(400,400));
		dialog2.setLocation(800,100);
		dialog2.validate();
		dialog2.pack();
		dialog2.setVisible(true);

		TestInspector inspector = new TestInspector();
		dc.addObserver(inspector);
		inspector.getWindow().setVisible(true);
	}

	public static Drawing makeDrawing()
	{
		return new MyFlexoProcess();
	}

	public static class MyFlexoProcess implements Drawing
	{
		private DrawingGraphicalRepresentation gr;
		private Vector<ContainedDrawable> list = new Vector<ContainedDrawable>();

		public MyFlexoProcess()
		{
			gr = new DrawingGraphicalRepresentation(this);

			MyActivity activity1 = new MyActivity("Activity1",100,100);
			MyActivity activity2 = new MyActivity("Activity2",300,200);
			MyActivity activity3 = new MyActivity("Activity3",250,350);

			MyEdge line1 = new MyEdge(activity1,activity2);
			MyEdge line2 = new MyEdge(activity2,activity3);
			MyEdge line3 = new MyEdge(activity1,activity3);

			MyOperationPetriGraph activity1PG = new MyOperationPetriGraph();

			list.add(activity1);
			list.add(activity2);
			list.add(activity3);
			list.add(line1);
			list.add(line2);
			list.add(line3);
			list.add(activity1PG);
		}

		public List<? extends ContainedDrawable> getContainedObjects() {
			return list;
		}

		public DrawingGraphicalRepresentation getGraphicalRepresentation() 
		{
			return gr;
		}

		public class MyActivity implements ShapedDrawable
		{
			private ShapeGraphicalRepresentation gr;

			public MyActivity(String name, int x, int y) 
			{
				gr = new ShapeGraphicalRepresentation(ShapeType.RECTANGLE,this);
				gr.setWidth(150);
				gr.setHeight(50);
				gr.setX(x);
				gr.setY(y);
				((Rectangle)gr.getShape()).setIsRounded(true);
				ForegroundStyle foreground = ForegroundStyle.makeColoredStyle(Color.BLACK);
				foreground.setLineWidth(0.2);
				gr.setForeground(foreground);
				gr.setBackground(BackgroundStyle.makeColorGradientBackground(new Color(230,230,230), Color.WHITE, ColorGradientDirection.SOUTH_WEST_NORTH_EAST));
				gr.setBorder(new ShapeGraphicalRepresentationUtils.ShapeBorder(10,10,10,10));
			}

			public ShapeGraphicalRepresentation getGraphicalRepresentation() 
			{
				return gr;
			}

			public List<? extends ContainedDrawable> getContainedObjects()
			{
				return null;
			}

			public MyFlexoProcess getContainer()
			{
				return MyFlexoProcess.this;
			}

		}

		public class MyEdge implements ConnectorDrawable
		{
			private ConnectorGraphicalRepresentation gr;

			public MyEdge(Drawable d1, Drawable d2)
			{
				gr = new ConnectorGraphicalRepresentation(
						ConnectorType.LINE,
						(ShapeGraphicalRepresentation)d1.getGraphicalRepresentation(),
						(ShapeGraphicalRepresentation)d2.getGraphicalRepresentation(),
						this);
				ForegroundStyle foreground = ForegroundStyle.makeColoredStyle(new Color(50,50,50));
				foreground.setLineWidth(2.0);
				gr.setForeground(foreground);	
				gr.setIsFocusable(true);
			}

			public ConnectorGraphicalRepresentation getGraphicalRepresentation() 
			{
				return gr;
			}

			public List<? extends Drawable> getContainedObjects()
			{
				return null;
			}

			public MyFlexoProcess getContainer()
			{
				return MyFlexoProcess.this;
			}
		}

		public class MyOperationPetriGraph implements ShapedDrawable
		{
			private ShapeGraphicalRepresentation gr;
			MyOperation operation1 = new MyOperation();
			MyOperation operation2 = new MyOperation();
			private Vector<ContainedDrawable> list = new Vector<ContainedDrawable>();

			public MyOperationPetriGraph() {
				gr = new ShapeGraphicalRepresentation(ShapeType.RECTANGLE,this);
				gr.setWidth(200);
				gr.setHeight(100);
				gr.setX(300);
				gr.setY(300);
				gr.setBackground(BackgroundStyle.makeColoredBackground(Color.LIGHT_GRAY));
				list.add(operation1);
				list.add(operation2);
			}

			public ShapeGraphicalRepresentation getGraphicalRepresentation() 
			{
				return gr;
			}

			public List<? extends ContainedDrawable> getContainedObjects()
			{
				return list;
			}

			public MyFlexoProcess getContainer()
			{
				return MyFlexoProcess.this;
			}

			public class MyOperation implements ShapedDrawable
			{
				private ShapeGraphicalRepresentation gr;

				public MyOperation() {
					gr = new ShapeGraphicalRepresentation(ShapeType.RECTANGLE,this);
					gr.setWidth(120);
					gr.setHeight(200);
					gr.setX(30);
					gr.setY(300);
					gr.setBackground(BackgroundStyle.makeColoredBackground(Color.ORANGE));
					gr.setBorder(new ShapeGraphicalRepresentationUtils.ShapeBorder(20,20,20,20));
				}

				public ShapeGraphicalRepresentation getGraphicalRepresentation() 
				{
					return gr;
				}

				public List<? extends ContainedDrawable> getContainedObjects()
				{
					return null;
				}

				public MyOperationPetriGraph getContainer()
				{
					return MyOperationPetriGraph.this;
				}

			}


		}


	}*/
}



