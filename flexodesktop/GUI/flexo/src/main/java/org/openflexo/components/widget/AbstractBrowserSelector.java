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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.components.widget.AbstractSelectorPanel.AbstractSelectorPanelOwner;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.swing.TextFieldCustomPopup;


/**
 * Widget allowing to select a object while browsing in a project browser
 *
 * @author sguerin
 *
 */
public abstract class AbstractBrowserSelector<T extends FlexoModelObject>
extends TextFieldCustomPopup<T>
implements AbstractSelectorPanelOwner<T>, FIBCustomComponent<T,TextFieldCustomPopup<T>>
{

    static final Logger logger = Logger.getLogger(AbstractBrowserSelector.class.getPackage().getName());

    FlexoProject _project;
    FlexoModelObject _rootObject = null;
    private T _revertValue;
    Class<T> _selectableClass;
    protected KeyAdapter completionListKeyAdapter;

    protected AbstractSelectorPanel<T> _selectorPanel;

    private FlexoEditor _editor;

    private Integer defaultWidth = null;
    private Integer defaultHeight = null;

    public AbstractBrowserSelector(FlexoProject project, T editedObject, Class<T> selectableClass)
    {
    	this(project, editedObject, selectableClass, -1);
    }

    public AbstractBrowserSelector(FlexoProject project, T editedObject, Class<T> selectableClass, int cols)
    {
        super(editedObject, cols);
        _project = project;
        _revertValue = editedObject;
        _selectableClass = selectableClass;
        setFocusable(true);
        getTextField().setFocusable(true);
        getTextField().setEditable(true);
        getTextField().addMouseWheelListener(new BrowserSelectorMouseWheelListener());
        completionListKeyAdapter = new KeyAdapter() {
            @Override
			public void keyPressed(KeyEvent e)
            {
            	//logger.info("Hop "+e);
                 if (_selectorPanel != null) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        _selectorPanel.processEnterPressed();
                        e.consume();
                   }
                    else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                        _selectorPanel.processTabPressed();
                        e.consume();
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        _selectorPanel.processUpPressed();
                        e.consume();
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        _selectorPanel.processDownPressed();
                        e.consume();
                    }
                }
                else if ((!e.isActionKey())
                        && (e.getKeyCode() != KeyEvent.VK_SHIFT)
                        && (e.getKeyCode() != KeyEvent.VK_ENTER)
                        && (e.getKeyCode() != KeyEvent.VK_TAB)) {
                    getCustomPanel();
                    _selectorPanel._completionListModel.textWasChanged();
                }
            }
        };
        getTextField().addKeyListener(completionListKeyAdapter);
        _downButton.addMouseWheelListener(new BrowserSelectorMouseWheelListener());
    }

    @Override
	public Class<T> getRepresentedType()
    {
    	return _selectableClass;
    }

    @Override
	public TextFieldCustomPopup<T> getJComponent()
    {
    	return this;
    }

    public void setText(String text)
    {
     	getTextField().setText(text);
    	getCustomPanel();
        _selectorPanel._completionListModel.textWasChanged();
        SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				_popup.requestFocus();
		    	getTextField().requestFocus();
			}
        });
     }


    @Override
	public void setRevertValue(T oldValue)
    {
        _revertValue = oldValue;
    }

    @Override
	public T getRevertValue()
    {
        return _revertValue;
    }

    @Override
	public FlexoProject getProject()
    {
        return _project;
    }

    public void setProject(FlexoProject project)
    {
        _project = project;
    }

    @Override
	protected ResizablePanel createCustomPanel(T editedObject)
    {
        _selectorPanel = makeCustomPanel(editedObject);
        _selectorPanel.setRootObject(_rootObject);
        _selectorPanel.init();
        return _selectorPanel;
    }

    @Override
	public FlexoModelObject getRootObject()
    {
    	return _rootObject;
    }

    public void setRootObject(FlexoModelObject aRootObject)
    {
    	_rootObject = aRootObject;
    	if (_selectorPanel != null) {
    		_selectorPanel.setRootObject(_rootObject);
    	}
    }

    protected abstract AbstractSelectorPanel<T> makeCustomPanel(T editedObject);

    @Override
	public void updateCustomPanel(T editedObject)
    {
        if (_selectorPanel != null) {
            _selectorPanel.update();
            _selectorPanel.hideCompletionList();
        }
    }

    @Override
	public abstract String renderedString(T editedObject);

    public FlexoModelObject getSelectedObject()
    {
        if (_selectorPanel != null) {
            return _selectorPanel.getSelectedObject();
        }
        return null;
    }

    @Override
	public boolean isSelectable (FlexoModelObject object)
    {
        return _selectableClass.isAssignableFrom(object.getClass());
    }

    private class BrowserSelectorMouseWheelListener implements MouseWheelListener {

        /**
         *
         */
        protected BrowserSelectorMouseWheelListener()
        {
        }

        @Override
		public void mouseWheelMoved(MouseWheelEvent e)
        {
            if (_selectorPanel==null)
                return;
            if (e.getScrollType()==MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                if (_selectorPanel._browserView.getTreeScrollPane().getVerticalScrollBar()!=null){
                    _selectorPanel._browserView.getTreeScrollPane().getVerticalScrollBar().setValue(_selectorPanel._browserView.getTreeScrollPane().getVerticalScrollBar().getValue()+e.getUnitsToScroll());
                }
            } else {
                if (_selectorPanel._browserView.getTreeScrollPane().getVerticalScrollBar()!=null){
                    _selectorPanel._browserView.getTreeScrollPane().getVerticalScrollBar().setValue(_selectorPanel._browserView.getTreeScrollPane().getVerticalScrollBar().getValue()+e.getWheelRotation()*_selectorPanel._browserView.getTreeScrollPane().getVerticalScrollBar().getBlockIncrement());
                }
            }
        }

    }

    @Override
	public void apply()
    {
       _revertValue = getEditedObject();
        closePopup();
        super.apply();
     }

    @Override
	public void cancel()
    {
        setEditedObject(_revertValue);
        closePopup();
        super.cancel();
    }

    @Override
	public void openPopup()
    {
        super.openPopup();
        getTextField().requestFocus();
    }

    @Override
	public void closePopup()
    {
        super.closePopup();
        deletePopup();
    }

    @Override
	protected void deletePopup()
    {
    	if (_selectorPanel!=null)
    		_selectorPanel.delete();
        _selectorPanel = null;
        super.deletePopup();
    }

    @Override
	protected void pointerLeavesPopup()
    {
        cancel();
    }

	public AbstractSelectorPanel<T> getSelectorPanel()
	{
		return _selectorPanel;
	}

	@Override
	public FlexoEditor getEditor()
	{
		return _editor;
	}

	/**
	 * Sets an editor if you want FlexoAction available on browser
	 * @param editor
	 */
	public void setEditor(FlexoEditor editor)
	{
		_editor = editor;
	}

	@Override
	public KeyAdapter getCompletionListKeyAdapter()
	{
		return completionListKeyAdapter;
	}

    @Override
	public Integer getDefaultWidth()
    {
    	return defaultWidth;
    }

    @Override
	public Integer getDefaultHeight()
    {
    	return defaultHeight;
    }

    public void setDefaultWidth(Integer defaultWidth)
    {
    	this.defaultWidth = defaultWidth;
    }

    public void setDefaultHeight(Integer defaultHeight)
    {
    	this.defaultHeight = defaultHeight;
    }

    @Override
    public void init(FIBCustom component, FIBController controller) {
    	// TODO Auto-generated method stub

    }
}
