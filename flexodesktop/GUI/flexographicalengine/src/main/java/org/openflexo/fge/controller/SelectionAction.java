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
package org.openflexo.fge.controller;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.layout.ForceDirectedPlacementLayout;
import org.openflexo.fge.layout.Layout;
import org.openflexo.fge.view.FGEView;

public class SelectionAction extends MouseClickControlAction {
	
	private GraphicalRepresentation<?> g;
	
	@Override
	public MouseClickControlActionType getActionType() {
		return MouseClickControlActionType.SELECTION;
	}

	@Override
	public boolean handleClick(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event) {
		if (controller.getDrawingView() == null) {
			return false;
		}

		/*
		 * TEMP
		 */
		this.g = graphicalRepresentation;
		
		ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	runAction();
            	}
            };
        Timer timer = new Timer( 1 , taskPerformer);
        timer.setRepeats(true);
        timer.start();

        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() 
		{
			@Override
			protected Void doInBackground() throws Exception {
				runAction();
				return null;
			}

			@Override
			protected void done() {
				super.done();
				try {
					get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			};
		worker.execute();
	*/
		/*
		 * END
		 */
		
		if (graphicalRepresentation.getIsSelectable()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Select " + graphicalRepresentation);
			}
			controller.setSelectedObject(graphicalRepresentation);
			if (controller.getDrawingView() == null) {
				return false;
			}
			FGEView<?> view = controller.getDrawingView().viewForObject(graphicalRepresentation);
			Point newPoint = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), (Component) view);
			controller.setLastClickedPoint(new FGEPoint(newPoint.x / controller.getScale(), newPoint.y / controller.getScale()));
			controller.setLastSelectedGR(graphicalRepresentation);
			return false;
		} else {
			return false;
		}
	}
	
	private void runAction(){
		Layout l = new ForceDirectedPlacementLayout(g);
		l.runLayout();
	}
		
}