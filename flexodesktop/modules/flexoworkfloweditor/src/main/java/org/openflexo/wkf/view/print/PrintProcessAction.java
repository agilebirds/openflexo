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
package org.openflexo.wkf.view.print;

import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.print.FlexoPrintableComponent;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.processeditor.ProcessEditorController;
import org.openflexo.wkf.processeditor.ProcessRepresentation;
import org.openflexo.wkf.swleditor.SwimmingLaneEditorController;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;


public class PrintProcessAction extends FlexoGUIAction<PrintProcessAction,FlexoProcess,WKFObject>
{

    protected static final Logger logger = Logger.getLogger(PrintProcessAction.class.getPackage().getName());

    public static FlexoActionType<PrintProcessAction,FlexoProcess,WKFObject> actionType = new FlexoActionType<PrintProcessAction,FlexoProcess,WKFObject> ("print_process",FlexoActionType.printGroup,FlexoActionType.NORMAL_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public PrintProcessAction makeNewAction(FlexoProcess focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor)
        {
            return new PrintProcessAction(focusedObject, globalSelection,editor);
        }

        @Override
		protected boolean isVisibleForSelection(FlexoProcess object, Vector<WKFObject> globalSelection)
        {
            return (object!=null) && !object.isImported();
        }

        @Override
		protected boolean isEnabledForSelection(FlexoProcess object, Vector<WKFObject> globalSelection)
        {
            return (object != null);
        }

    };

    static {
        FlexoModelObject.addActionForClass (PrintProcessAction.actionType, FlexoProcess.class);
    }

    protected FlexoProcess processToPrint;

    PrintProcessAction (FlexoProcess focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection,editor);
    }

    public static void initWithController (final WKFController controller)
    {
    	controller.getEditor().registerInitializerFor(actionType, new FlexoActionInitializer<PrintProcessAction>() {
            @Override
			public boolean run(ActionEvent e, PrintProcessAction anAction)
            {
                if (anAction.getFocusedObject() == null) {
                    anAction.setFocusedObject(controller.getCurrentFlexoProcess());
                }
                return (anAction.getFocusedObject() != null);
            }
        }, controller.getModule());

    	controller.getEditor().registerFinalizerFor(actionType, new FlexoActionFinalizer<PrintProcessAction>() {
            @Override
			public boolean run(ActionEvent e, final PrintProcessAction anAction)
            {
                anAction.processToPrint = anAction.getFocusedObject();

                FlexoPrintableComponent printableView = null;
                if (controller.getCurrentPerspective()==controller.SWIMMING_LANE_PERSPECTIVE) {
	                SwimmingLaneEditorController printController = new SwimmingLaneEditorController(controller,anAction.processToPrint) {
	                	@Override
	                	public DrawingView<SwimmingLaneRepresentation> makeDrawingView(SwimmingLaneRepresentation drawing) {
	                		return new PrintableSwimimingLaneView(drawing,this,getWKFController());
	                	}
	                };
                	printController.getDrawingView().getPaintManager().disablePaintingCache();
                	printController.getDrawingGraphicalRepresentation().setDrawWorkingArea(false);
                	printableView = (FlexoPrintableComponent)printController.getDrawingView();
                } else {
	                ProcessEditorController printController = new ProcessEditorController(anAction.processToPrint,anAction.getEditor(),null) {
	                	@Override
	                	public DrawingView<ProcessRepresentation> makeDrawingView(ProcessRepresentation drawing) {
	                		return new PrintableProcessView(drawing,this,getWKFController());
	                	}
	                };
                	printController.getDrawingView().getPaintManager().disablePaintingCache();
                	printController.getDrawingGraphicalRepresentation().setDrawWorkingArea(false);
                	printableView = (FlexoPrintableComponent)printController.getDrawingView();
                }
				PrintProcessPreviewDialog dialog = new PrintProcessPreviewDialog(controller,printableView);
                return (dialog.getStatus() == PrintProcessPreviewDialog.ReturnedStatus.CONTINUE_PRINTING);
            }
        }, controller.getModule());

    }


}
