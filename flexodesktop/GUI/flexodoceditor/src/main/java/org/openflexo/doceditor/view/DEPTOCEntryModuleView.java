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
package org.openflexo.doceditor.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.FlexoCst;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.listener.FlexoActionButton;
import org.openflexo.wysiwyg.FlexoWysiwyg;
import org.openflexo.wysiwyg.FlexoWysiwygLight;


import org.openflexo.doceditor.DECst;
import org.openflexo.doceditor.controller.DEController;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.ptoc.PTOCEntry;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.icon.DEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;



/**
 * MOS
 * @author MOSTAFA
 */
public class DEPTOCEntryModuleView extends JPanel implements ModuleView<PTOCEntry>, FlexoActionSource, FlexoObserver
{

    private Logger logger = FlexoLogger.getLogger(DETOCEntryModuleView.class.getPackage().getName());

    protected PTOCEntry _entry;

    protected DEController _controller;

    protected FlexoWysiwyg entryEditor;

    protected boolean updatingModel = false;

    private ViewHeader _header;

    private FlexoPerspective perspective;

    public DEPTOCEntryModuleView(PTOCEntry entry, DEController controller, FlexoPerspective perspective)
    {
        super(new BorderLayout());
        this.perspective = perspective;
        _entry = entry;
        entry.addObserver(this);
        _controller = controller;
        rebuildView();
    }

    private void rebuildView() {
    	removeAll();
//    	if (!_entry.isReadOnly()){
//    		boolean showViewSourceButtonInWysiwyg = _controller.getDocInspectorController().getConfiguration()!=null && _controller.getDocInspectorController().getConfiguration().showViewSourceButtonInWysiwyg();
//			entryEditor = new FlexoWysiwygLight(_entry.getContent(), getController().getProject().getDocumentationCssResource().getFile(), showViewSourceButtonInWysiwyg) {
//
//				@Override
//				public void notifyTextChanged() {
//					try {
//						updatingModel = true;
//						_entry.setContent(entryEditor.getBodyContent());
//					} catch (IllegalAccessException e) {
//						e.printStackTrace();
//						SwingUtilities.invokeLater(new Runnable(){
//							@Override
//							public void run() {
//								// entryEditor.setContent(newText); FIXME what should be called here ?
//							}
//						});
//					} finally {
//						updatingModel = false;
//					}
//				}
//	    	};
//	    	entryEditor.addSupportForInsertedObjects(_entry.getProject().getImportedImagesDir());
	    	/*
	    	entryEditor.setPreferredImagePath(_entry.getProject().getImportedImagesDir());
	    	RelativeImageView.addToImagePaths(_entry.getProject().getImportedImagesDir());
	    	try {
				entryEditor.getEkitCore().setBase(_entry.getProject().getImportedImagesDir().toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			entryEditor.getEkitCore().setImageImporterDelegate(new EkitCore.ImageImporterDelegate() {

				public File importImage(File imageFile) {
					ImportImage importImage = ImportImage.actionType.makeNewAction(null, null, getController().getEditor());
					importImage.setFileToImport(imageFile);
					importImage.doAction();
					if (importImage.getCreatedResource()!=null) FIXME what was the use of this ?
						return importImage.getCreatedResource().getFile();
					else
						return null;
				}}
			);*/
//	    	add(entryEditor,BorderLayout.CENTER);
//    	} else {
    		JPanel panel = new JPanel(new BorderLayout());

    		JLabel label = new JLabel();
    		label.setHorizontalAlignment(SwingConstants.CENTER);
    		label.setText(FlexoLocalization.localizedForKey("documentation_from_your_project_will_be_automatically_inserted_here",label));
    		panel.add(label,BorderLayout.NORTH);
    		JTextArea textarea = new JTextArea();
    		textarea.setEditable(false);
    		textarea.setEnabled(false);
    		String key = "description_"+(_entry.getTitle()!=null?_entry.getTitle():(
    				_entry.getObjectReference()!=null?_entry.getObjectReference().getKlass().getSimpleName():"unknown_section"));
    		textarea.setText(FlexoLocalization.localizedForKey(key, textarea));
    		panel.add(textarea);
    		add(panel,BorderLayout.CENTER);
//    	}
    	_header = new ViewHeader();
    	add(_header,BorderLayout.NORTH);

	}

	public DEController getController()
    {
        return _controller;
    }

    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
    	if (updatingModel)
    		return;
    	if ("title".equals(dataModification.propertyName())) {
    		_header.update();
    	}
    	if ("content".equals(dataModification.propertyName())) {
	        if (logger.isLoggable(Level.FINE))
	            logger.fine("CGFileModuleView : RECEIVED " + dataModification + " for " + observable);
//	        if (entryEditor!=null)
//	        	entryEditor.setContent(_entry.getContent());
    	}
    }

    @Override
	public void deleteModuleView()
    {
        logger.info("CGFileModuleView view deleted");
        getController().removeModuleView(this);
        _entry.deleteObserver(this);
    }

    @Override
	public FlexoPerspective getPerspective()
    {
        return perspective;
    }

    @Override
	public PTOCEntry getRepresentedObject()
    {
        return _entry;
    }

     @Override
	public void willHide()
    {
    }

    @Override
	public void willShow()
    {
    	// FIXME AJA what was the use of this ?
    	// RelativeImageView.addToImagePaths(_entry.getProject().getImportedImagesDir());
    }

    /**
     * Returns flag indicating if this view is itself responsible for scroll
     * management When not, Flexo will manage it's own scrollbar for you
     *
     * @return
     */
    @Override
	public boolean isAutoscrolled()
    {
        return true;
    }

    @Override
	public FlexoModelObject getFocusedObject()
    {
        return getRepresentedObject();
    }

    @Override
	public Vector getGlobalSelection()
    {
        return null;
    }

	@Override
	public FlexoEditor getEditor() {
		return _controller.getEditor();
	}

	protected class ViewHeader extends JPanel
    {
        JLabel icon;

        JLabel title;

        JPanel controlPanel;

        Vector<FlexoActionButton> actionButtons = new Vector<FlexoActionButton>();

        protected ViewHeader()
        {
            super(new BorderLayout());
            icon = new JLabel(DEIconLibrary.TOC_ENTRY_BIG);
            icon.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            add(icon, BorderLayout.WEST);
            title = new JLabel();
            title.setText(_entry.getTitle()!=null?_entry.getTitle():FlexoLocalization.localizedForKey("untitled",title));
            // title.setVerticalAlignment(JLabel.BOTTOM);
            title.setFont(DECst.HEADER_FONT);
            title.setForeground(Color.BLACK);
            title.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
            JPanel labelsPanel = new JPanel(new GridLayout(2, 1));
            labelsPanel.add(title);
            add(labelsPanel, BorderLayout.CENTER);

            controlPanel = new JPanel(new FlowLayout());

                /*FlexoActionButton saveAction = new FlexoActionButton(SaveGeneratedFile.actionType, "save", DGFileModuleView.this,_controller.getEditor());
                FlexoActionButton revertToSavedAction = new FlexoActionButton(RevertToSavedGeneratedFile.actionType, "revert_to_saved",
                        DGFileModuleView.this,_controller.getEditor());
                actionButtons.add(saveAction);
                actionButtons.add(revertToSavedAction);
                controlPanel.add(saveAction);
                controlPanel.add(revertToSavedAction);*/

            controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            add(controlPanel, BorderLayout.EAST);


            update();
            validate();
        }

        private void addInfoPanel(Icon icon, String titleString, String textString)
        {
            JLabel regenerateIcon = new JLabel(icon);
            JLabel title = new JLabel(titleString, SwingConstants.LEFT);
            title.setFont(FlexoCst.BOLD_FONT);
            JTextArea text = new JTextArea(textString);
            text.setBackground(null);
            text.setEditable(false);
            text.setFont(FlexoCst.NORMAL_FONT);
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            text.setBorder(BorderFactory.createEmptyBorder(5, 30, 10, 30));
            JPanel infoPanel = new JPanel(new VerticalLayout());
            JPanel titlePanel = new JPanel(new FlowLayout());
            titlePanel.add(regenerateIcon);
            titlePanel.add(title);
            infoPanel.add(titlePanel);
            infoPanel.add(text);
            add(infoPanel, BorderLayout.SOUTH);

        }

        protected void update()
        {
        	title.setText(_entry.getTitle()!=null?_entry.getTitle():FlexoLocalization.localizedForKey("untitled",title));
            for (FlexoActionButton button : actionButtons) {
                button.update();
            }
        }

    }
}
