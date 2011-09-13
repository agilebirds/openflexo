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
package org.openflexo.foundation.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.localization.FlexoLocalization;

public abstract class FlexoActionType<A extends FlexoAction<?,T1,T2>, T1 extends FlexoModelObject,T2 extends FlexoModelObject> extends AbstractAction
{
 
    private static final Logger logger = Logger.getLogger(FlexoActionType.class.getPackage().getName());

    public static final ActionGroup inspectGroup = new ActionGroup("inspect",0);
    public static final ActionGroup defaultGroup = new ActionGroup("default",1);
    public static final ActionGroup editGroup = new ActionGroup("edit",2);
    // 3 to 9 are reserved for custom groups
   public static final ActionGroup printGroup = new ActionGroup("print",10);
    public static final ActionGroup helpGroup = new ActionGroup("help",11);
    public static final ActionGroup docGroup = new ActionGroup("documentation",12);
    
    public static final ActionMenu newMenu = new ActionMenu("new",defaultGroup);
    public static final ActionGroup newMenuGroup1 = new ActionGroup("new_group_1",0);
    public static final ActionGroup newMenuGroup2 = new ActionGroup("new_group_2",1);
    public static final ActionGroup newMenuGroup3 = new ActionGroup("new_group_3",2);
    public static final ActionGroup newMenuGroup4 = new ActionGroup("new_group_4",2);
  
    public static final ActionMenu importMenu = new ActionMenu("import",defaultGroup);
    public static final ActionMenu exportMenu = new ActionMenu("export",defaultGroup);
          
    public static final ActionMenu executionModelMenu = new ActionMenu("execution_model",defaultGroup);

    public static final int NORMAL_ACTION_TYPE = 0;
    public static final int ADD_ACTION_TYPE = 1;
    public static final int DELETE_ACTION_TYPE = 2;
    

    private String _actionName;
    //private Icon _smallIcon;
    //private Icon _smallDisabledIcon;
    private KeyStroke _keyStroke;
    private ActionGroup _actionGroup;
    private ActionMenu _actionMenu;
    private int _actionCategory;
    
    //private Vector _modulesWhereActionIsRegistered;
   
    protected FlexoActionType (String actionName)
    {
        this(actionName,null,defaultGroup,NORMAL_ACTION_TYPE);
    }
    
    /*protected FlexoActionType (String actionName, Icon icon)
    {
        this(actionName,null,defaultGroup,icon,NORMAL_ACTION_TYPE);
    }*/
    
    protected FlexoActionType (String actionName, ActionGroup actionGroup)
    {
        this(actionName,null,actionGroup,NORMAL_ACTION_TYPE);
    }
    
    protected FlexoActionType (String actionName, ActionGroup actionGroup, ActionMenu actionMenu)
    {
        this(actionName,actionMenu,actionGroup,NORMAL_ACTION_TYPE);
    }
    
    /*protected FlexoActionType (String actionName, ActionGroup actionGroup, Icon icon)
    {
        this(actionName,null,actionGroup,icon,NORMAL_ACTION_TYPE);
    }*/
    
     protected FlexoActionType (String actionName, ActionMenu actionMenu, ActionGroup actionGroup)
    {
        this(actionName,actionMenu,actionGroup,NORMAL_ACTION_TYPE);
    }
    

    /* protected FlexoActionType (String actionName, ActionMenu actionMenu, ActionGroup actionGroup, Icon icon)
     {
         this(actionName,actionMenu,actionGroup,icon,NORMAL_ACTION_TYPE);
     }*/
 
    protected FlexoActionType (String actionName, int actionCategory)
    {
        this(actionName,defaultGroup,null,actionCategory);
    }
    
   /* protected FlexoActionType (String actionName, Icon icon, int actionCategory)
    {
        this(actionName,null,defaultGroup,icon,actionCategory);
    }*/
    
    protected FlexoActionType (String actionName, ActionGroup actionGroup, int actionCategory)
    {
        this(actionName,actionGroup,null,actionCategory);
    }
    
    protected FlexoActionType (String actionName, ActionGroup actionGroup, ActionMenu actionMenu, int actionCategory)
    {
        this(actionName,actionMenu,actionGroup,actionCategory);
    }
    
    /*protected FlexoActionType (String actionName, ActionGroup actionGroup, Icon icon, int actionCategory)
    {
        this(actionName,null,actionGroup,icon,actionCategory);
    }*/
    
    protected FlexoActionType (String actionName, ActionMenu actionMenu, ActionGroup actionGroup, int actionCategory)
    {
        super();
        _actionCategory = actionCategory;
        _actionName = actionName;
        setActionMenu(actionMenu);
        setActionGroup(actionGroup);
    }
    
 
    /*protected FlexoActionType (String actionName, ActionMenu actionMenu, ActionGroup actionGroup, Icon icon, int actionCategory)
    {
        super();
        _actionCategory = actionCategory;
        _actionName = actionName;
        setActionMenu(actionMenu);
        setActionGroup(actionGroup);
        setSmallIcon(icon);
        //_modulesWhereActionIsRegistered = new Vector();
    }*/
    
    
    /*public void registerInModule(IModule module)
    {
        _modulesWhereActionIsRegistered.add(module);
    }
    
    private boolean hasAtLeastAnAvailableModule()
    {
        for (Enumeration en=_modulesWhereActionIsRegistered.elements(); en.hasMoreElements();) {
            IModule next = (IModule)en.nextElement();
            if (next.isAvailable()) return true;
        }
        if (logger.isLoggable(Level.FINE))
            logger.fine ("FlexoActionType: sorry action not available !");
        return false;
    }*/
    

   public String getUnlocalizedName ()
    {
        return _actionName;
    }

    public String getLocalizedName ()
    {
        return FlexoLocalization.localizedForKey(_actionName);
    }

    public String getLocalizedName (Component component)
    {
        return FlexoLocalization.localizedForKey(_actionName,component);
    }

    public String getLocalizedDescription ()
    {
        return FlexoLocalization.localizedForKey(_actionName+"_description");
    }
    
    /**
     * Deprecated call to an action building outside the context of a FlexoEditor
     * Please DON'T use it anymore !!!
     * 
     * All old implementations using this method must be rewritten
     * 
     * @deprecated
     * @param focusedObject the focused object
     * @param globalSelection a vector of FlexoModelObject, which represent all the selected objects
     * @return
     */
    @Deprecated
	public A makeNewAction (T1 focusedObject, Vector<T2> globalSelection)
    {
    	return makeNewAction(focusedObject, globalSelection,null);
    }

    /**
     * 
     * @param focusedObject the focused object
     * @param globalSelection a vector of FlexoModelObject, which represent all the selected objects
     * @param editor TODO
     * @return
     */
    public abstract A makeNewAction (T1 focusedObject, Vector<T2> globalSelection, FlexoEditor editor);

    /**
     * 
     * @param focusedObject the focused object
     * @param globalSelection a vector of FlexoModelObject, which represent all the selected objects
     * @param editor TODO
     * @return
     */
    public A makeNewEmbeddedAction (T1 focusedObject, Vector<T2> globalSelection, FlexoAction ownerAction)
    {
    	A returned = makeNewAction(focusedObject, globalSelection, ownerAction.getEditor());
    	returned.setOwnerAction(ownerAction);
    	return returned;
    }

    /**
     * Indicates if this action (eventually disabled) might be presented
     * @param object
     * @return
     */
    protected abstract boolean isVisibleForSelection (T1 object, Vector<T2> globalSelection);

    /**
     * Indicates if this action (eventually disabled) is enabled
     * @param object
     * @return
     */
    protected abstract boolean isEnabledForSelection (T1 object, Vector<T2> globalSelection);

    /**
     * Indicates if this action is enabled (assert that action is visible)
     * @param object
     * @return
     */
    public boolean isEnabled (T1 object, Vector<T2> globalSelection, FlexoEditor editor)
    {
    	if (editor == null) {
    		logger.warning("FlexoAction invoked with null editor ");
    		return true;
    	}

    	if ((object!=null) && (object.getActionList().indexOf(this)==-1)) {
			return false;
		}
    	if (editor.isActionEnabled(this)) {
    		if (isEnabledForSelection(object,globalSelection)) {
    			if (getEnableConditionForEditor(editor) != null) {
     				return getEnableConditionForEditor(editor).isEnabled(this,object,globalSelection,editor);
    			}
    			else {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    public String getDisabledReason(T1 object, Vector<T2> globalSelection, FlexoEditor editor) 
    {
    	if ((object!=null) && (object.getActionList().indexOf(this)==-1)) {
			return FlexoLocalization.localizedForKey("action")+" "+getLocalizedName()+" "+FlexoLocalization.localizedForKey("is_not_active_for")+" "+FlexoLocalization.localizedForKey(object.getClassNameKey());
		}
    	if (!editor.isActionEnabled(this)) {
    		return FlexoLocalization.localizedForKey("action")+" "+getLocalizedName()+" "+FlexoLocalization.localizedForKey("is_not_active_for")+" "+FlexoLocalization.localizedForKey("this_module");
    	}
    	if (!isEnabledForSelection(object,globalSelection)) {
    		return FlexoLocalization.localizedForKey("action")+" "+getLocalizedName()+" "+FlexoLocalization.localizedForKey("is_not_active_for_this_selection");
		}
    	if ((getEnableConditionForEditor(editor) != null) && !getEnableConditionForEditor(editor).isEnabled(this,object,globalSelection,editor)) {
    		// TODO: Add getDisableReason() also on FlexoActionEnableCondition
			return FlexoLocalization.localizedForKey("conditions_to_enable_action_are_not_met");
		}
    	return null;
    }

    /**
     * Indicates if this action (eventually disabled) is visible
     * @param object
     * @return
     */
    public boolean isVisible (T1 object, Vector<T2> globalSelection, FlexoEditor editor)
    {
    	if (editor == null) {
    		logger.warning("FlexoAction invoked with null editor ");
    		return true;
    	}
    	if (editor.isActionVisible(this)) {
    		try {
    			if(isVisibleForSelection(object,globalSelection)) {
    				if (getVisibleConditionForEditor(editor) != null) {
         				return getVisibleConditionForEditor(editor).isVisible(this,object,globalSelection,editor);
        			} else {
        				return true;
        			}
    			}
    		} catch (ClassCastException e) {
    			// May happen if wrong types
    		}
    	}
    	return false;
    }

    protected boolean matchesInstanceOf (Class aClass, T1 object, Vector<T2> globalSelection) 
    {
        if (logger.isLoggable(Level.FINE)) {
			logger.fine  ("matchesInstanceOf-object="+object);
		}
        if (logger.isLoggable(Level.FINE)) {
			logger.fine  ("matchesInstanceOf-globalSelection= "+globalSelection.size()+" "+globalSelection);
		}
        return (((object != null) && (aClass.isAssignableFrom(object.getClass())))
                || ((globalSelection != null)
                        && (globalSelection.size() > 0)
                        && (aClass.isAssignableFrom(globalSelection.firstElement().getClass()))));
     }

    protected boolean matchesUniqueInstanceOf (Class aClass, T1 object, Vector<T2> globalSelection) 
    {
        if (logger.isLoggable(Level.FINE)) {
			logger.fine  ("matchesUniqueInstanceOf-object="+object);
		}
        if (logger.isLoggable(Level.FINE)) {
			logger.fine  ("matchesUniqueInstanceOf-globalSelection= "+globalSelection.size()+" "+globalSelection);
		}
        return (((object != null) 
                        && (aClass.isAssignableFrom(object.getClass())) 
                        && ((globalSelection == null) || (globalSelection.size() == 0) || ((globalSelection.size() == 1) && (globalSelection.firstElement() == object))))
                || ((globalSelection != null)
                        && (globalSelection.size() == 1)
                        && (aClass.isAssignableFrom(globalSelection.firstElement().getClass()))
                        && ((object==null) || (object == globalSelection.firstElement()))));
    }

    protected FlexoModelObject getUniqueInstanceOf (Class aClass, T1 object, Vector<T2> globalSelection) 
    {
        if ((object != null) 
                && (aClass.isAssignableFrom(object.getClass())) 
                && ((globalSelection == null) || (globalSelection.size() == 0) || ((globalSelection.size() == 1) && (globalSelection.firstElement() == object)))) {
            return object;
        }
        else if ((globalSelection != null)
                && (globalSelection.size() == 1)
                && (aClass.isAssignableFrom(globalSelection.firstElement().getClass()))
                && ((object==null) || (object == globalSelection.firstElement()))) {
            return globalSelection.firstElement();
        }
        return null;
    }
    
   /* public Icon getSmallIcon() 
    {
        return _smallIcon;
    }

    public void setSmallIcon(Icon smallIcon) 
    {
        _smallIcon = smallIcon;
    }
    
	public Icon getSmallDisabledIcon() 
	{
		return _smallDisabledIcon;
	}

	public void setSmallDisabledIcon(Icon smallDisabledIcon) 
	{
		_smallDisabledIcon = smallDisabledIcon;
	}*/

    public KeyStroke getKeyStroke() 
    {
        return _keyStroke;
    }

    public void setKeyStroke(KeyStroke keyStroke) 
    {
        _keyStroke = keyStroke;
    }

    public FlexoActionFinalizer<? super A> getFinalizerForEditor(FlexoEditor editor) 
    {
    	if (editor == null) {
			return null;
		}
    	return editor.getFinalizerFor(this);
    }

    public FlexoActionInitializer<? super A> getInitializerForEditor(FlexoEditor editor) 
    {
    	if (editor == null) {
			return null;
		}
    	return editor.getInitializerFor(this);
    }

    public FlexoActionUndoFinalizer<? super A> getUndoFinalizerForEditor(FlexoEditor editor) 
    {
    	if (editor == null) {
			return null;
		}
    	return editor.getUndoFinalizerFor(this);
    }

    public FlexoActionUndoInitializer<? super A> getUndoInitializerForEditor(FlexoEditor editor) 
    {
    	if (editor == null) {
			return null;
		}
    	return editor.getUndoInitializerFor(this);
    }

    public FlexoActionRedoFinalizer<? super A> getRedoFinalizerForEditor(FlexoEditor editor) 
    {
    	if (editor == null) {
			return null;
		}
    	return editor.getRedoFinalizerFor(this);
    }

    public FlexoActionRedoInitializer<? super A> getRedoInitializerForEditor(FlexoEditor editor) 
    {
    	if (editor == null) {
			return null;
		}
    	return editor.getRedoInitializerFor(this);
    }

    public FlexoActionEnableCondition getEnableConditionForEditor(FlexoEditor editor) 
    {
    	if (editor == null) {
			return null;
		}
    	return editor.getEnableConditionFor(this);
    }

    public FlexoActionVisibleCondition getVisibleConditionForEditor(FlexoEditor editor) 
    {
    	if (editor == null) {
			return null;
		}
    	return editor.getVisibleConditionFor(this);
    }
    
    public FlexoExceptionHandler<? super A> getExceptionHandlerForEditor(FlexoEditor editor) 
    {
    	if (editor == null) {
			return null;
		}
    	return editor.getExceptionHandlerFor(this);
    }

    public ActionGroup getActionGroup() 
    {
    	return _actionGroup;
    }

    public void setActionGroup(ActionGroup actionGroup) 
    {
    	_actionGroup = actionGroup;
    }
    
    public ActionMenu getActionMenu() 
    {
        return _actionMenu;
    }
    
    public void setActionMenu(ActionMenu actionMenu) 
    {
        _actionMenu = actionMenu;
    }
    
    @Override
	public void actionPerformed(ActionEvent event) 
    {
        A action;
        if (logger.isLoggable(Level.FINE)) {
			logger.fine("event source "+event.getSource());
		}
        if (event.getSource() instanceof FlexoActionSource) {
            try {
            	FlexoActionSource<T1,T2> source = (FlexoActionSource<T1,T2>)event.getSource();
            	if (isEnabled(source.getFocusedObject(),source.getGlobalSelection(), source.getEditor())) {
            		action = makeNewAction (source.getFocusedObject(),source.getGlobalSelection(), source.getEditor());
            		action.setInvoker(event.getSource());
            	}
            	else {
            		logger.info("Action not enabled for this selection "+source.getFocusedObject()+ " "+source.getGlobalSelection());
            		logger.info("Reason: "+getDisabledReason(source.getFocusedObject(),source.getGlobalSelection(), source.getEditor()));
            		return;
            	}
            }
            catch (ClassCastException exception) {
                logger.warning("ClassCastException raised while trying to build FlexoAction "+this+" Exception: "+exception.getMessage());
                return;
            }
        }
        else {
            logger.warning("Executing action "+getUnlocalizedName()+" from a source not implementing FlexoActionSource interface: "+event.getSource());
            DefaultFlexoEditor editor = new DefaultFlexoEditor();
           	if (isEnabled(null,null,editor)) {
           		action = makeNewAction (null,null, editor);
           	}
           	else {
           		logger.info("Action not enabled for null selection");
           		return;
        	}
         }
        if (logger.isLoggable(Level.INFO)) {
			logger.info("Trying to execute action "+action.getActionType().getUnlocalizedName()+" with "+action.getFocusedObject()+" and "+action.getGlobalSelection());
		}
        action.actionPerformed(event);
    }

    public int getActionCategory() 
    {
        return _actionCategory;
    }

    protected String[] getPersistentProperties() 
    {
    	return new String[0];
    }

}
