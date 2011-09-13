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
package org.openflexo.fib.editor.view.container;

import java.awt.BorderLayout;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate.FIBDropTarget;
import org.openflexo.fib.model.BorderLayoutConstraints;
import org.openflexo.fib.model.BoxLayoutConstraints;
import org.openflexo.fib.model.ComponentConstraints;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBModelNotification;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FlowLayoutConstraints;
import org.openflexo.fib.model.GridBagLayoutConstraints;
import org.openflexo.fib.model.GridLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.view.container.FIBTabView;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableTabView extends FIBTabView<FIBTab> implements FIBEditableView<FIBTab,JPanel> {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditableTabView.class.getPackage().getName());

	private final FIBEditableViewDelegate<FIBTab,JPanel> delegate;
	
	private Vector<PlaceHolder> placeholders;
	
	private final FIBEditorController editorController;
	
	@Override
	public FIBEditorController getEditorController() 
	{
		return editorController;
	}
	
	public FIBEditableTabView(final FIBTab model, FIBEditorController editorController)
	{
		super(model,editorController.getController());
		this.editorController = editorController;
		//logger.info("************ Created FIBEditablePanelView for "+model);
		
		delegate = new FIBEditableViewDelegate<FIBTab,JPanel>(this);
		
		//getJComponent().setBorder(BorderFactory.createMatteBorder(10,10,10,10,Color.yellow));
		
		model.addObserver(this);
	}
	
	@Override
	public void delete() 
	{
		getComponent().deleteObserver(this);
		if (placeholders != null) placeholders.clear();
		placeholders = null;
		delegate.delete();
		super.delete();
	}


	@Override
	protected void retrieveContainedJComponentsAndConstraints()
	{
		if (placeholders == null) placeholders = new Vector<PlaceHolder>();
		placeholders.removeAllElements();

		super.retrieveContainedJComponentsAndConstraints();
		
		if (!getComponent().getProtectContent()) {
		
		// FlowLayout
		if (getComponent().getLayout() == Layout.flow) {
			final FlowLayoutConstraints beginPlaceHolderConstraints = new FlowLayoutConstraints(-1);
			PlaceHolder beginPlaceHolder = new PlaceHolder(this,"<begin>") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent,beginPlaceHolderConstraints);
				}
			};
			registerComponentWithConstraints(beginPlaceHolder, beginPlaceHolderConstraints);
			placeholders.add(beginPlaceHolder);
			beginPlaceHolder.setVisible(false);	
			final FlowLayoutConstraints endPlaceHolderConstraints = new FlowLayoutConstraints(getComponent().getSubComponents().size());
			PlaceHolder endPlaceHolder = new PlaceHolder(this,"<end>") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent,endPlaceHolderConstraints);
				}
			};
			registerComponentWithConstraints(endPlaceHolder,endPlaceHolderConstraints);
			placeholders.add(endPlaceHolder);
			endPlaceHolder.setVisible(false);	
		}
		
		// BoxLayout
		
		if (getComponent().getLayout() == Layout.box) {
			final BoxLayoutConstraints beginPlaceHolderConstraints = new BoxLayoutConstraints(-1);
			PlaceHolder beginPlaceHolder = new PlaceHolder(this,"<begin>") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent,beginPlaceHolderConstraints);
				}
			};
			registerComponentWithConstraints(beginPlaceHolder, beginPlaceHolderConstraints);
			placeholders.add(beginPlaceHolder);
			beginPlaceHolder.setVisible(false);	
			final BoxLayoutConstraints endPlaceHolderConstraints = new BoxLayoutConstraints(getComponent().getSubComponents().size());
			PlaceHolder endPlaceHolder = new PlaceHolder(this,"<end>") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent,endPlaceHolderConstraints);
				}
			};
			registerComponentWithConstraints(endPlaceHolder);
			placeholders.add(endPlaceHolder);
			endPlaceHolder.setVisible(false);	
		}
		
		// BorderLayout
		if (getComponent().getLayout() == Layout.border) {
			BorderLayout bl = (BorderLayout)getJComponent().getLayout();
			BorderLayoutLocation[] placeholderLocations = { BorderLayoutLocation.north, BorderLayoutLocation.south, BorderLayoutLocation.center, BorderLayoutLocation.east, BorderLayoutLocation.west}; 
			for (final BorderLayoutLocation l : placeholderLocations) {
				boolean found = false;
				for (FIBComponent subComponent : getComponent().getSubComponents()) {
					BorderLayoutConstraints blc = (BorderLayoutConstraints)subComponent.getConstraints();
					if (blc.getLocation() == l) found=true;
				}
				if (!found) {
					PlaceHolder newPlaceHolder = new PlaceHolder(this,"<"+l.getConstraint()+">") {
						@Override
						public void insertComponent(FIBComponent newComponent) {
							BorderLayoutConstraints blConstraints = new BorderLayoutConstraints(l);
							newComponent.setConstraints(blConstraints);
							FIBEditableTabView.this.getComponent().addToSubComponents(newComponent);
						}
					};
					registerComponentWithConstraints(newPlaceHolder,l.getConstraint());
					newPlaceHolder.setVisible(false);	
					placeholders.add(newPlaceHolder);
					logger.fine("Made placeholder for "+l.getConstraint());
				}
			}
		}
		
		// TwoColsLayout
		
		if (getComponent().getLayout() == Layout.twocols) {
			final TwoColsLayoutConstraints beginCenterPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.center,true,false,-3);
			PlaceHolder beginCenterPlaceHolder = new PlaceHolder(this,"<center>") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent,beginCenterPlaceHolderConstraints);
				}
			};
			registerComponentWithConstraints(beginCenterPlaceHolder,beginCenterPlaceHolderConstraints);
			placeholders.add(beginCenterPlaceHolder);
			beginCenterPlaceHolder.setVisible(false);	

			final TwoColsLayoutConstraints beginLeftPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.left,true,false,-2);
			final TwoColsLayoutConstraints beginRightPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.right,true,false,-1);
			PlaceHolder beginLeftPlaceHolder = new PlaceHolder(this,"<left>") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					FIBEditableTabView.this.getComponent().addToSubComponents(new FIBLabel("<right>"),beginRightPlaceHolderConstraints);
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent,beginLeftPlaceHolderConstraints);
				}
			};
			registerComponentWithConstraints(beginLeftPlaceHolder,beginLeftPlaceHolderConstraints);
			placeholders.add(beginLeftPlaceHolder);
			beginLeftPlaceHolder.setVisible(false);	
			
			PlaceHolder beginRightPlaceHolder = new PlaceHolder(this,"<right>") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent,beginRightPlaceHolderConstraints);
					FIBEditableTabView.this.getComponent().addToSubComponents(new FIBLabel("<left>"),beginLeftPlaceHolderConstraints);
				}
			};
			registerComponentWithConstraints(beginRightPlaceHolder,beginRightPlaceHolderConstraints);
			placeholders.add(beginRightPlaceHolder);
			beginRightPlaceHolder.setVisible(false);	

			final TwoColsLayoutConstraints endCenterPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.center,true,false,getComponent().getSubComponents().size()+3);
			PlaceHolder endCenterPlaceHolder = new PlaceHolder(this,"<center>") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent,endCenterPlaceHolderConstraints);
				}
			};
			registerComponentWithConstraints(endCenterPlaceHolder,endCenterPlaceHolderConstraints);
			placeholders.add(endCenterPlaceHolder);
			endCenterPlaceHolder.setVisible(false);	

			final TwoColsLayoutConstraints endLeftPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.left,true,false,getComponent().getSubComponents().size()+1);
			final TwoColsLayoutConstraints endRightPlaceHolderConstraints = new TwoColsLayoutConstraints(TwoColsLayoutLocation.right,true,false,getComponent().getSubComponents().size()+2);
			PlaceHolder endLeftPlaceHolder = new PlaceHolder(this,"<left>") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent,endLeftPlaceHolderConstraints);
					FIBEditableTabView.this.getComponent().addToSubComponents(new FIBLabel("<right>"),endRightPlaceHolderConstraints);
				}
			};
			registerComponentWithConstraints(endLeftPlaceHolder,endLeftPlaceHolderConstraints);
			placeholders.add(endLeftPlaceHolder);
			endLeftPlaceHolder.setVisible(false);	
			
			PlaceHolder endRightPlaceHolder = new PlaceHolder(this,"<right>") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					FIBEditableTabView.this.getComponent().addToSubComponents(new FIBLabel("<left>"),endLeftPlaceHolderConstraints);
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent,endRightPlaceHolderConstraints);
				}
			};
			registerComponentWithConstraints(endRightPlaceHolder,endRightPlaceHolderConstraints);
			placeholders.add(endRightPlaceHolder);
			endRightPlaceHolder.setVisible(false);	

		}
		
		// GridBagLayout
		
		if (getComponent().getLayout() == Layout.gridbag) {
			final GridBagLayoutConstraints beginPlaceHolderConstraints = new GridBagLayoutConstraints(-1);
			PlaceHolder beginPlaceHolder = new PlaceHolder(this,"<begin>") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent,beginPlaceHolderConstraints);
				}
			};
			registerComponentWithConstraints(beginPlaceHolder, beginPlaceHolderConstraints);
			placeholders.add(beginPlaceHolder);
			beginPlaceHolder.setVisible(false);	
			final GridBagLayoutConstraints endPlaceHolderConstraints = new GridBagLayoutConstraints(getComponent().getSubComponents().size());
			PlaceHolder endPlaceHolder = new PlaceHolder(this,"<end>") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent,endPlaceHolderConstraints);
				}
			};
			registerComponentWithConstraints(endPlaceHolder);
			placeholders.add(endPlaceHolder);
			endPlaceHolder.setVisible(false);	
		}

		// Now, we sort again subComponents, since we may have added some placeholders
		if (getComponent().getLayout() == Layout.flow
				|| getComponent().getLayout() == Layout.box
				|| getComponent().getLayout() == Layout.twocols
				|| getComponent().getLayout() == Layout.gridbag) 
		{
			Collections.sort(getSubComponents(),new Comparator<JComponent>() {
				public int compare(JComponent o1, JComponent o2) {
					Object c1 = getConstraints().get(o1);
					Object c2 = getConstraints().get(o2);
					if (c1 instanceof ComponentConstraints 
							&& c2 instanceof ComponentConstraints) {
						ComponentConstraints cc1 = (ComponentConstraints)c1;
						ComponentConstraints cc2 = (ComponentConstraints)c2;
						return cc1.getIndex()-cc2.getIndex();		
					}
					return 0;
				}
			});
		}

		
		//logger.info("******** Set DropTargets");
		if (getEditorController() != null) {
			for (PlaceHolder ph : placeholders) {
				logger.fine("Set DropTarget for "+ph);
				// Put right drop target
				new FIBDropTarget(ph);
			}
		}
		/*else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					updateLayout();
				}
			});
		}*/
		}
	}
	
	

	// Special case for GridLayout
	@Override
	protected JComponent _getJComponent(final int col, final int row)
	{
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			GridLayoutConstraints glc = (GridLayoutConstraints)subComponent.getConstraints();
			if (glc.getX() == col && glc.getY() == row) return getController().viewForComponent(subComponent).getResultingJComponent();
		}
		
		if (!getComponent().getProtectContent()) {
			// Otherwise, it's a PlaceHolder
			PlaceHolder newPlaceHolder = new PlaceHolder(this,"<"+col+","+row+">") {
				@Override
				public void insertComponent(FIBComponent newComponent) {
					GridLayoutConstraints glConstraints = new GridLayoutConstraints(col,row);
					newComponent.setConstraints(glConstraints);
					FIBEditableTabView.this.getComponent().addToSubComponents(newComponent);
				}
			};
			newPlaceHolder.setVisible(false);	
			placeholders.add(newPlaceHolder);

			return newPlaceHolder;
		}
		else {
			// Otherwise, it's an empty cell
			return new JPanel();
		}
	}
	
	public Vector<PlaceHolder> getPlaceHolders() 
	{
		return placeholders;
	}
	
	public FIBEditableViewDelegate<FIBTab, JPanel> getDelegate()
	{
		return delegate;
	}
	
	public void update(Observable o, Object dataModification) 
	{
		//System.out.println("alignmentX="+getJComponent().getAlignmentX());
		//System.out.println("alignmentY="+getJComponent().getAlignmentY());
		if (dataModification instanceof FIBModelNotification) {
			delegate.receivedModelNotifications(o, (FIBModelNotification)dataModification);
		}		
	}

}
