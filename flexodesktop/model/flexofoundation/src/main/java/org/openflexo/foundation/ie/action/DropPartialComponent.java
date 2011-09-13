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
package org.openflexo.foundation.ie.action;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IETopComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.foundation.ie.widget.ButtonedWidgetInterface;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.foundation.ie.widget.IESequenceTR;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.ITableRow;
import org.openflexo.foundation.ie.widget.ITableRowReusableWidget;
import org.openflexo.foundation.ie.widget.InnerBlocReusableWidget;
import org.openflexo.foundation.ie.widget.InnerTableReusableWidget;
import org.openflexo.foundation.ie.widget.TopComponentReusableWidget;
import org.openflexo.foundation.rm.FlexoProject;


public class DropPartialComponent extends FlexoAction<DropPartialComponent,IEObject,IEObject> 
{

    private static final Logger logger = Logger.getLogger(DropPartialComponent.class.getPackage().getName());
    
    public static FlexoActionType<DropPartialComponent,IEObject,IEObject> actionType = new FlexoActionType<DropPartialComponent,IEObject,IEObject> ("drag_ie_element",FlexoActionType.newMenu,FlexoActionType.defaultGroup,FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public DropPartialComponent makeNewAction(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) 
        {
            return new DropPartialComponent(focusedObject, globalSelection,editor);
        }

        @Override
		protected boolean isVisibleForSelection(IEObject object, Vector<IEObject> globalSelection) 
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(IEObject object, Vector<IEObject> globalSelection) 
        {
            return (object != null);
        }
                
    };
    
    DropPartialComponent (IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
        parameters = new Hashtable<String,Integer>();
    }

    private static final String INDEX = "index";
    private static final String ROW = "row";
    private static final String COL = "col";
    private static final String ROW_SPAN = "rowspan";
    private static final String COL_SPAN = "colspan";
          
    private IEReusableWidget droppedWidget; 
    private IEObject container; 
    private Hashtable<String,Integer> parameters;
    public int getIndex()
    {
    	if (parameters.get(INDEX) == null) return 0;
    	return parameters.get(INDEX);
    }
    
    public void setIndex (int index)
    {
    	parameters.put(INDEX,index);
    }

    public int getRow()
    {
       	if (parameters.get(ROW) == null) return -1;
       	return parameters.get(ROW);
    }
    
    public void setRow (int row)
    {
    	parameters.put(ROW,row);
    }

    public int getCol()
    {
    	if (parameters.get(COL) == null) return -1;
    	return parameters.get(COL);
    }
    
   public void setCol (int col)
    {
    	parameters.put(COL,col);
    }

   public int getRowSpan()
   {
      	if (parameters.get(ROW_SPAN) == null) return -1;
      	return parameters.get(ROW_SPAN);
   }
   
   public void setRowSpan (int rowSpan)
   {
	   parameters.put(ROW_SPAN,rowSpan);
   }

   public int getColSpan()
   {
      	if (parameters.get(COL_SPAN) == null) return -1;
      	return parameters.get(COL_SPAN);
   }
   
   public void setColSpan (int colSpan)
   {
	   parameters.put(COL_SPAN,colSpan);
   }

   
   
	public IEObject getContainer() 
	{
		if (container == null) {
			container = getFocusedObject();
			if (container instanceof IEWOComponent) {
				container = ((IEWOComponent)container).getRootSequence();
			}
		}
		return container;
	}

	public void setContainer(IEObject container) 
	{
		this.container = container;
	}

	public FlexoProject getProject()
	{
		if (getContainer() != null) return getContainer().getProject();
		return null;
	}
	
	public IEReusableWidget getDroppedWidget() 
	{
		return droppedWidget;
	}

     @Override
	protected void doAction(Object context) throws InvalidDropException
    {
    	logger.info ("Insert partial component index="+getIndex()+" row="+getRow()+" col="+getCol());
       	//logger.fine ("XML="+getElementXMLRepresentation());
       	
       	if (getContainer() == null) throw new InvalidDropException("Cannot drop on a null container !");
       	
       	if (getPartialComponent() == null) throw new InvalidDropException("Cannot drop a null partial component !");
 
        if (!insertWidget(getContainer())) {
        	throw new InvalidDropException("Cannot drop such a widget ("+partialComponent.getClass().getName()+") on this container("+getContainer().getClass().getName()+") !");
        }
       	
    }

     private boolean insertWidget(IEObject container) throws InvalidDropException
     {
    	 IEWOComponent woComponent = container instanceof IEWidget ? ((IEWidget)container).getWOComponent() : null;
    	 if(woComponent==null)return false;
    	 
     	 if (container instanceof IESequenceWidget && ((IESequenceWidget)container).isTopComponent() && getPartialComponent().getWOComponent().isTopComponent()) {
     		 if (getIndex() == -1) throw new InvalidDropException("Cannot drop element at this index: "+getIndex());
             IEReusableWidget newWidget 
                 = new TopComponentReusableWidget(woComponent, getPartialComponent(), 
                		 (IESequenceWidget)container,
                		 container.getProject());
          		droppedWidget = newWidget;
          		newWidget.setIndex(getIndex());
                 try {
                	 // Do the insertion
                	 ((IESequenceWidget)container).insertElementAt(newWidget, getIndex());
                	 return true;
                 }
                 catch (IndexOutOfBoundsException e) {
                	 throw new InvalidDropException("Cannot drop element at this index: "+getIndex());
                 }
     	 }
     	if ((container instanceof IEBlocWidget) && getPartialComponent().getWOComponent().isASingleHTMLTable()){
     		IEReusableWidget newWidget = new InnerBlocReusableWidget(woComponent,getPartialComponent(), 
           		 (IEBlocWidget)container,
           		 container.getProject());
     		
     		droppedWidget = newWidget;
      		newWidget.setIndex(getIndex());
             try {
            	 // Do the insertion
            	 ((IEBlocWidget)container).setContent((InnerBlocReusableWidget)newWidget);
            	 // Now declare the resources dependancies
            	 woComponent.getFlexoResource().addToDependantResources(partialComponent.getComponentResource());
            	 return true;
             }
             catch (IndexOutOfBoundsException e) {
            	 throw new InvalidDropException("Cannot drop element at this index: "+getIndex());
             }
             
     	}
     	
     	if (getPartialComponent().getWOComponent().isAListOfTableRows()) {
            DropLocationForITableRow loc = findSuitableSequenceTRForDroppedITableRow(container);
            if(loc!=null){
         		IEReusableWidget newWidget = new ITableRowReusableWidget(woComponent, getPartialComponent(),
               		 loc.seq, container.getProject());
         		//compInst.setHTMLTableWidget(loc.seq.htmlTable());
         		droppedWidget = newWidget;
          		newWidget.setIndex(loc.index);
                 try {
                	 // Do the insertion
                	 loc.seq.insertElementAt((ITableRowReusableWidget)newWidget, loc.index);
                	 loc.seq.htmlTable().setTRRowIndex();
                	 //compInst.setHTMLTableWidget(loc.seq.htmlTable());
                	 //loc.seq.setParentOfSingleWidgetComponentInstance(loc.seq.htmlTable());
                	 return true;
                 }
                 catch (IndexOutOfBoundsException e) {
                	 throw new InvalidDropException("Cannot drop element at this index: "+getIndex());
                 }
            }else{
            	return false;
            }
        }
     	if (container instanceof IESequenceWidget) {
			if (getIndex() == -1)
				throw new InvalidDropException("Cannot drop element at this index: " + getIndex());
			IEReusableWidget newWidget = new InnerTableReusableWidget(woComponent, getPartialComponent(),(IESequenceWidget) container, container.getProject());
			droppedWidget = newWidget;
			newWidget.setIndex(getIndex());
			try {
				// Do the insertion
				((IESequenceWidget) container).insertElementAt(newWidget, getIndex());
				// Now declare the resources dependancies
				((IESequenceWidget) container).getWOComponent().getFlexoResource().addToDependantResources(partialComponent.getComponentResource());
				return true;
			} catch (IndexOutOfBoundsException e) {
				throw new InvalidDropException("Cannot drop element at this index: " + getIndex());
			}
		}
     	
       	 return false;
     		
     }

     private ReusableComponentDefinition partialComponent;

	public ReusableComponentDefinition getPartialComponent() 
	{
		return partialComponent;
	}

	public void setPartialComponent(ReusableComponentDefinition partialComponent) 
	{
		this.partialComponent = partialComponent;
	}
	
	   private class DropLocationForITableRow{
	    	public int index;
	    	public IESequenceTR seq;
	    	
	    	public DropLocationForITableRow(int i,IESequenceTR s){
	    		super();
	    		index = i;
	    		seq = s;
	    	}
	    }
	   
	private DropLocationForITableRow findSuitableSequenceTRForDroppedITableRow(IEObject container){
    	DropLocationForITableRow reply = null;
    	if(container instanceof IEWidget){
    		IEObject current = container;
    		while(reply==null && current instanceof IEWidget){
    			if(((IEWidget)current).getParent() instanceof IESequenceTR)
    				reply = new DropLocationForITableRow(((IEWidget)current).getIndex(), (IESequenceTR)((IEWidget)current).getParent());
    			current = ((IEWidget)current).getParent();
    		}
    	}
    	return reply;
    	
    }
    
    private boolean isDropAcceptable(IEWidget rootWidget, IEObject container){
        if ((container instanceof IESequenceWidget) && (rootWidget instanceof IETopComponent) && getIndex()!=-1) {
        	return true;
        }
        if ((container instanceof IEBlocWidget) && (rootWidget instanceof IEHTMLTableWidget)) {
            return ((IEBlocWidget)container).getContent()==null;
        }

        if ((container instanceof ButtonedWidgetInterface) && (rootWidget instanceof IEButtonWidget) && getIndex()!=-1) {
            return true;
        }

        if ((container instanceof IESequenceTab) && (rootWidget instanceof IETabWidget)) {
            return true;
        }

        if (rootWidget instanceof ITableRow) {
            return findSuitableSequenceTRForDroppedITableRow(container)!=null;
        }

        
        if ((container instanceof IESequenceWidget) && getIndex()!=-1) {
//            if (model instanceof IETDWidget) {
//                if (logger.isLoggable(Level.INFO))
//                    logger.info("CASE 9: insert IETDWidget in IESequenceWidget (IESequenceWidgetWidgetView)");
//                IESequenceWidget seq = ((IETDWidget) model).getSequenceWidget();
//                for (int j = 0; j < seq.size(); j++) {
//                    model = seq.get(j);
//                    model.setIndex(getIndex());
//                    ((IESequenceWidget) container).insertElementAt(model, getIndex());
//                }
//            } else if (model instanceof IESequenceWidget && ((IESequenceWidget) model).getOperator() == null) {
//                if (logger.isLoggable(Level.INFO))
//                    logger.info("CASE 10: insert IESequenceWidget in IESequenceWidget (IESequenceWidgetWidgetView)");
//                IESequenceWidget seq = (IESequenceWidget) model;
//                for (int j = 0; j < seq.size(); j++) {
//                    model = seq.get(j);
//                    model.setIndex(getIndex());
//                    ((IESequenceWidget) container).insertElementAt(model, getIndex());
//                }
//            } else {
//                if (logger.isLoggable(Level.INFO))
//                    logger.info("CASE 11: insert IEWidget in IESequenceWidget (IESequenceWidgetWidgetView)");
//                model.setIndex(getIndex());
//                ((IESequenceWidget) container).insertElementAt(model, getIndex());
//            }
            return true;
        }

        return false;
    }
 
}
