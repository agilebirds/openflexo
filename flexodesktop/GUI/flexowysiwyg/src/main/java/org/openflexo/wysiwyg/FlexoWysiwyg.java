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
package org.openflexo.wysiwyg;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.Document;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;

import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.HTMLUtils;
import org.openflexo.toolbox.ToolBox;

import sferyx.administration.editors.EditorHTMLDocument;
import sferyx.administration.editors.HTMLEditor;

public abstract class FlexoWysiwyg extends HTMLEditor {

	protected static final Logger logger = Logger.getLogger(FlexoWysiwyg.class.getPackage().getName());

	private boolean isViewSourceAvailable = false;
	private boolean isFocusListenerActivated = true;
	private FocusListener focusListener;
	private boolean focusListenerAddedToSourceComponent = false;
	private Set<String> removedPopupMenuItems = new HashSet<String>();

	private boolean isInPaste = false;

	/**
	 * Creates the default wysiwyg lightweight component (JRootPane), with the JMenuBar and without any CSS file. This class must implement
	 * <code>textChanged(String htmlText)</code> to be concrete.
	 */
	public FlexoWysiwyg(boolean isViewSourceAvailable) {
		this(null, isViewSourceAvailable);
	}

	/**
	 * Creates the default wysiwyg lightweight component (JRootPane) initialized with <code>htmlContent</code> with the JMenuBar and without
	 * any CSS file. This class must implement <code>textChanged(String htmlText)</code> to be concrete.
	 * 
	 * @param htmlContent
	 *            the HTML content to initialize the wysiwyg with.
	 */
	public FlexoWysiwyg(String htmlContent, boolean isViewSourceAvailable) {
		this(htmlContent, null, isViewSourceAvailable);
	}

	/*
	 * Main constructor for all the wysiwyg. Should only be called from this package
	 */
	/**
	 * Creates the default wysiwyg lightweight component (JRootPane). This class must implement <code>textChanged(String htmlText)</code> to
	 * be concrete.
	 * 
	 * @param htmlContent
	 *            the HTML content to initialize the wysiwyg with.
	 * @param cssFile
	 *            the CSS file to apply on the document.
	 */
	protected FlexoWysiwyg(String htmlContent, File cssFile, boolean isViewSourceAvailable) {

		super();
		this.isViewSourceAvailable = isViewSourceAvailable;
		applyDefaultTextOptions();
		applyDefaultDisplayOptions();
		setContent(htmlContent);
		// let handleCss AFTER setContent otherwise a deadlock occurs !!
		handleCss(cssFile);
		getSelectedEditorComponent().addFocusListener(focusListener = new FlexoWysiwygFocusListener());
		setPreferredPasteOperation(PASTE_FORMATTED__TEXT);

		if (!getIsViewSourceAvailable()) {
			setSourceEditorVisible(false);
		}

		addToConstructor();
	}

	protected Component getSourceEditorComponent() {
		if (getMainTabbedPane().getTabCount() > 1) {
			Component c = getMainTabbedPane().getComponentAt(1);
			if (c instanceof JScrollPane) {
				c = ((JScrollPane) c).getViewport().getView();
			}
			return c;
		}
		return null;
	}

	@Override
	public void setSourceEditorVisible(boolean visible) {
		if (!visible && getSourceEditorComponent() != null) {
			getSourceEditorComponent().removeFocusListener(focusListener);
			focusListenerAddedToSourceComponent = false;
		}
		super.setSourceEditorVisible(visible);
		if (visible && !focusListenerAddedToSourceComponent && getSourceEditorComponent() != null) {
			getSourceEditorComponent().addFocusListener(focusListener);
			focusListenerAddedToSourceComponent = true;
		}
	}

	@Override
	public boolean hasFocus() {
		return getSelectedEditorComponent().hasFocus();
	}

	/*
	 * CALLS FROM CONSTRUCTOR
	 */

	/**
	 * Override this method if you want to add statements at the end of the constructor
	 */
	public void addToConstructor() {
	}

	@Override
	public void pasteFormattedTextFromClipboard() {
		/**
		 * Actually the super.pasteFormattedTextFromClipboard performs a lot of cleaning on the pasted content, and then call the
		 * insertContent method. So the isInPaste boolean is set to true to perform the html cleaning in insertContent method only if this
		 * occurs because of a paste. This is ugly but I cannot find a better way to do it: - doing the clean after the whole paste process
		 * will clean the entire content and breaks the 'undo' action - doing it before means getting the content from the clipboard, doing
		 * the same cleaning than the one performed by super.pasteFormattedTextFromClipboard and setting back this content into the
		 * clipboard.
		 */
		isInPaste = true;
		super.pasteFormattedTextFromClipboard();
		isInPaste = false;
	}

	@Override
	public void insertContent(String content) {
		if (isInPaste) {
			try {
				content = FlexoWysiwygHtmlCleaner.cleanHtml(content, getStyleClasses());
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Cannot clean pasted content !", e);
			}
		}

		super.insertContent(content);
	}

	private void applyDefaultTextOptions() {
		setDefaultCharset("utf-8");
		// setDefaultInitialFont("Dialog");
		setDefaultInitialFontSize("8");
		setShowBodyContentOnlyInSource(Boolean.TRUE.toString());
	}

	/**
	 * Some useless menus are removed by default (File, Tools, Window and Help), as well as the 'new file', 'open file' and 'save file'
	 * icons on the toolbar.
	 */
	private void applyDefaultDisplayOptions() {

		getFormattingToolBar().setFloatable(false);
		getEditingToolBar().setFloatable(false);
		// to remove the default Border is bad looking on MacOsX L&F
		if (ToolBox.isMacOSLaf()) {
			for (int i = 0; i < getFormattingToolBar().getComponentCount(); i++) {
				if (getFormattingToolBar().getComponent(i) instanceof JComboBox) {
					JComboBox c = (JComboBox) getFormattingToolBar().getComponent(i);
					c.setBorder(null);
				}
			}
		}
		setRemovedMenus("menuFile, menuTools, menuWindow, menuForm, menuHelp");
		setRemovedMenuItems("pagePropertiesMainMenuItem");
		setRemovedPopupMenuItems("pagePropertiesMenuItem, createTablePopupMenu");
		setRemovedToolbarItems("newFileButton, openFileButton, saveFileButton, tableBtn"); // items must be separated by a comma
	}

	@Override
	public void setRemovedToolbarItems(String toolbarItemNames) {
		super.setRemovedToolbarItems(toolbarItemNames);
		cleanUpToolBars();
	}

	@Override
	public void setRemovedPopupMenuItems(String removedItems) {
		// Fix bug in implementation, the method setRemovedPopupMenuItems from parent overrides the previously entered items
		// So we keep those items in set removedPopupMenuItems to avoid losing previously removed items

		for (String removedItem : removedItems.split(",")) {
			removedPopupMenuItems.add(removedItem.trim());
		}

		StringBuilder sb = new StringBuilder();
		for (String removedItem : removedPopupMenuItems) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(removedItem);
		}

		super.setRemovedPopupMenuItems(sb.toString());
	}

	@Override
	protected void adjustPopupForElement() {
		super.adjustPopupForElement();
		cleanUpPopupMenu();
	}

	private void cleanUpPopupMenu() {
		removeDuplicateAndUnusedSeparator(getVisualEditorPopupMenu(), JPopupMenu.Separator.class);
	}

	private void cleanUpToolBars() {
		removeDuplicateAndUnusedSeparator(getEditingToolBar(), JToolBar.Separator.class);
		removeDuplicateAndUnusedSeparator(getFormattingToolBar(), JToolBar.Separator.class);
	}

	private void removeDuplicateAndUnusedSeparator(JComponent container, Class separatorClass) {
		boolean isFirstShowComponent = true;
		Component previousComponent = null;
		Component[] c = container.getComponents();
		// Set all separators to visible
		for (int i = 0; i < c.length; i++) {
			Component component = c[i];
			if (separatorClass.isInstance(component)) {
				component.setVisible(true);
			}
		}
		for (int i = 0; i < c.length; i++) {
			Component component = c[i];
			if (!component.isVisible()) {
				continue;
			}
			if (separatorClass.isInstance(component)) {
				// If it is the first shown component or the previous shown component was separator, we hide this one
				if (isFirstShowComponent || separatorClass.isInstance(previousComponent)) {
					component.setVisible(false);
					continue;
				}
			}
			previousComponent = component;
			isFirstShowComponent = false;
		}
		// Hides trailing separator
		if (separatorClass.isInstance(previousComponent)) {
			previousComponent.setVisible(false);
		}
	}

	/**
	 * applies a css to the html document. The css also defines styles to add to the styles JComboBox on the formatting toolbar
	 * 
	 * @param cssFile
	 *            the CSS file to apply. All the CSS properties will be applied on the page rendering but only classes defined as
	 *            <code>.aClass{property:value;}</code>will be added to the CSS styles JComboBox. <br>
	 *            Example:<br>
	 *            <code>.green{color:green;}</code> will be applied in the rendering and added as 'green' in the styles JComboBox<br>
	 *            <code>p.green{color:green;}</code> will only be applied in the rendering
	 * @Deprecated foire à mort...on va s'en passer pour le moment;
	 */
	private void handleCss(File cssFile) {

		if (cssFile != null) {
			try {
				loadExternalStyleSheet(cssFile.toURL().toString());
			} catch (Exception e) {
				logger.log(Level.WARNING, "Could not load the external style sheet '" + cssFile.getPath()
						+ "'. Use level fine for stacktrace");
				if (logger.isLoggable(Level.FINE)) {
					logger.log(Level.FINE, "Stacktrace : ", e);
				}
			}
		} else {
			setRemovedToolbarItems("styleClasses");
		}
	}

	/**
	 * add a gray line border around the toolbars to give a better look, in particular in inspectors.
	 */
	public void addBorderAroundToolbar() {
		try {
			// the toolbar panel is actually a panel which contains two panels, each containing a JToolbar
			JPanel toolbarPanel = (JPanel) getFormattingToolBar().getParent().getParent();
			Border grayBorder = new LineBorder(Color.GRAY) {
				@Override
				public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
					super.paintBorder(c, g, x, y, width, height + 1); // height+1 = astuce pour ne pas dessiner la ligne du bas du border...
				}
			};
			toolbarPanel.setBorder(grayBorder);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error while drawing the gray border around the toolbars", e);
		}
	}

	/**
	 * Little hack so that the file chooser does not take hours to load up. Ideally, we should provide the FlexoFileChooser.
	 * 
	 * @see sferyx.administration.editors.HTMLEditor#getFileDialog()
	 */
	@Override
	public JFileChooser getFileDialog() {
		if (isLocalFileBrowsingDisabled()) {
			setLocalFileBrowsingDisabled(false);
		}
		if (ToolBox.fileChooserRequiresFix()) {
			ToolBox.fixFileChooser();
		}
		JFileChooser dialog = super.getFileDialog();
		if (ToolBox.fileChooserRequiresFix()) {
			ToolBox.undoFixFileChooser();
		}
		return dialog;
	}

	private File documentBaseFolder;
	private String insertedObjectsFolderName;

	/**
	 * Please use this method if you need this wysiwyg to handle with images imported into a specific directory. Each time the user will
	 * upload an image, it will be copied into <code>insertedObjectsFolderPath</code> and the <code>src</code> attribute of the image will
	 * be made relative.<br>
	 * <p>
	 * Example:
	 * </p>
	 * <p>
	 * <code>addSupportForInsertedObjects(aFolder, "images")</code> will set <code>aFolder</code> as <code>base</code> of the
	 * <code>Document</code>.<br>
	 * The images will be copied into <code>.../aFolder/images/</code><br>
	 * The <code>src<code> attribute of an image file such as "example.jpg" will be <code>src="images/example.jpg"</code><br>
	 * </p>
	 * 
	 * @param insertedObjectsFolder
	 *            the name of the folder in which all the images will be copied. This parameter cannot be null or empty. The HTML file will
	 *            be saved in the insertedObjectsFolder parent folder (actually the <code>base</code> of the <code>Document</code>).
	 * 
	 * @see Document
	 */
	public void addSupportForInsertedObjects(File insertedObjectsFolder) {
		try {
			if (insertedObjectsFolder == null) {
				throw new IllegalArgumentException("insertedObjectsFolder cannot be null");
			}
			if (!insertedObjectsFolder.exists()) {
				insertedObjectsFolder.mkdir();
			}
			documentBaseFolder = insertedObjectsFolder.getParentFile();
			if (documentBaseFolder == null) {
				throw new IllegalArgumentException("insertedObjectsFolder must have a parent, cannot be a root folder");
			}

			// pretty stupid but we need to get the file dialog to set the document base folder. Otherwise you would get a
			// NullpointerException at saving.
			getFileDialog().setSelectedFile(documentBaseFolder);
			((HTMLDocument) getInternalJEditorPane().getDocument()).setBase(documentBaseFolder.toURL());
			insertedObjectsFolderName = insertedObjectsFolder.getName();
			setLinkedObjectsFolderName(insertedObjectsFolderName);
			setGenerateUniqueImageFilenames(true);
			setSaveEntireDocumentTree(true);
			getInternalJEditorPane().repaint();

			// Reload wysiwyg to display images if any (otherwise images are not displayed in the Toc editor)
			EditorHTMLDocument document = (EditorHTMLDocument) getInternalJEditorPane().getDocument();
			if (document.getIterator(Tag.IMG).isValid()) {
				createNewDocument(getBodyContent(), ((EditorHTMLDocument) getInternalJEditorPane().getDocument()).getBase());
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not add support for inserted objects : " + e.getMessage() + ". Use level fine for stacktrace");
			if (logger.isLoggable(Level.FINE)) {
				logger.log(Level.FINE, "Stacktrace : ", e);
			}
		}
	}

	/**
	 * inserts the image specified at this URL into the document and copies the image into the <code>insertedObjectsFolderPath</code>
	 * specified in the method <code>addSupportForInsertedObjects</code>. If the support for inserted objects has not been added, the image
	 * will not be copied.
	 */
	@Override
	public void insertImage(String imageURL) {
		String fileName = imageURL;
		int index = fileName.lastIndexOf('/');
		if (index > -1) {
			fileName = fileName.substring(index + 1);
		}
		File test = new File(imageURL);
		if (test.exists()) {
			try {
				imageURL = test.toURI().toURL().toString();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
		}
		fileName = FileUtils.lowerCaseExtension(FileUtils.removeNonASCIIAndPonctuationAndBadFileNameChars(fileName));
		File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
		tempFile.deleteOnExit();
		try {
			FileUtils.createNewFile(tempFile);
			FileUtils.saveToFile(tempFile, new URL(imageURL).openStream());
			super.insertImage(tempFile.toURI().toURL().toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			super.insertImage(imageURL);
		}
		saveRelatedObjects();
	}

	@Override
	public String getRelativePath(String imageURL) {
		if (imageURL == null || imageURL.startsWith("file:/")) {
			return imageURL;
		}
		return super.getRelativePath(imageURL);
	}

	private void saveRelatedObjects() {
		final File documentBaseFolder = getDocumentBaseFolder();
		if (documentBaseFolder != null && getLinkedObjectsFolderName() != null) { // means there is a support for the inserted Objects
			getFileDialog().setSelectedFile(documentBaseFolder);
			boolean crappyFixRequired = getFileDialog().getSelectedFile() == null
					|| !getFileDialog().getSelectedFile().equals(documentBaseFolder);
			JFileChooser fileChooser = null;
			if (crappyFixRequired) {
				fileChooser = getFileDialog();
				setFileDialog(new JFileChooser() {
					@Override
					public File getSelectedFile() {
						return documentBaseFolder;
					}
				});
			}
			try {
				// we create a fake html file in order to let the function 'saveEntireDocumentTree(htmlFile)' save the whole page and copy
				// the related objects into 'getLinkedObjectsFolderName()'
				File htmlFile = new File(documentBaseFolder, "destroyMeMaster.html");
				saveEntireDocumentTree(htmlFile);
				// we delete the fake html page once everything has been saved
				if (!htmlFile.delete()) {
					htmlFile.deleteOnExit();
				}
			} finally {
				if (crappyFixRequired) {
					setFileDialog(fileChooser);
				}
			}
		}
	}

	private File getDocumentBaseFolder() {
		try {
			URL baseURL = ((HTMLDocument) getInternalJEditorPane().getDocument()).getBase();
			return new File(baseURL.getPath());
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not get document base folder : " + e.getMessage() + ". Use level fine for stacktrace");
			if (logger.isLoggable(Level.FINE)) {
				logger.log(Level.FINE, "Stacktrace : ", e);
			}
			setDocumentBaseFolder();
			return documentBaseFolder;
		}
	}

	@Override
	public String getBodyContent() {
		String s = super.getBodyContent();
		if (s != null) {
			s = s.trim();
			if (s.length() == 0 || HTMLUtils.isEmtpyParagraph(s)) {
				return "";
			}
		}
		return s;
	}

	/**
	 * @deprecated please use getBodyContent to prevent from importing <code>&lt;html&gt;</code>, <code>&lt;head&gt;</code> and
	 *             <code>&lt;body&gt;</code> tags;
	 */
	@Override
	@Deprecated
	public String getContent() {
		return super.getContent();
	}

	@Override
	public void setContent(String htmlContent) {
		if (htmlContent == null) {
			htmlContent = "";
		}
		super.setContent(htmlContent);
		setDocumentBaseFolder();
	}

	private void setDocumentBaseFolder() {
		if (documentBaseFolder != null) {
			try {
				((HTMLDocument) getInternalJEditorPane().getDocument()).setBase(documentBaseFolder.toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		if (insertedObjectsFolderName != null) {
			setLinkedObjectsFolderName(insertedObjectsFolderName);
		}
	}

	/**
	 * Implements this method to handle the changes in the HTML code of the wysiwyg. These changes are only notified if
	 * <code>isDocumentListenerActivated<code> is true.
	 * Observers should then call getBodyContent to retrieve the HTML of the wysiwyg
	 */
	public abstract void notifyTextChanged();

	/**
	 * Returns if the focus listener is currently activated
	 */
	public boolean isActivated() {
		return isFocusListenerActivated;
	}

	/**
	 * Activates or deactivates the focus listener on the wysiwyg and the focusable property If set to false, the wysiwyg will not call
	 * <code>textChanged</code> when an focus occurs on the HTML document.
	 */
	public void setActivated(boolean activated) {
		this.isFocusListenerActivated = activated;
		getSelectedEditorComponent().setFocusable(activated); // remove focus because JEditorPane requests the focus on
																// JEditorPane.setText() -> Avoid to lose focus when both doc editors are
																// opened.
	}

	/**
	 * Default DocumentListener that redirects all the document events to <code>textChanged</code> if
	 * <code>isDocumentListenerActivated</code> is true.
	 */
	public class FlexoWysiwygFocusListener implements FocusListener {

		@Override
		public void focusGained(FocusEvent e) {
			Component focussedComp = e.getOppositeComponent();
			if (focussedComp == null || !SwingUtilities.isDescendingFrom(focussedComp, FlexoWysiwyg.this)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("focus gained on the wysiwyg, init key strokes");
				}
				initKeyStrokes();
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Focus is still on the wysiwyg, do not init key strokes");
				}
			}

		}

		@Override
		public void focusLost(FocusEvent e) {
			if (isActivated()) {
				Component focussedComp = e.getOppositeComponent();
				if (focussedComp == null || !SwingUtilities.isDescendingFrom(focussedComp, FlexoWysiwyg.this)) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Focus lost on the wysiwyg, applying changes");
					}
					notifyTextChanged();
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Focus is still on the wysiwyg, do not apply changes");
					}
				}
			}
		}
	}

	public boolean getIsViewSourceAvailable() {
		return isViewSourceAvailable;
	}

	public void setViewSourceAvailable(boolean isViewSourceAvailable) {
		this.isViewSourceAvailable = isViewSourceAvailable;
	}

	/*
	 * @Override public void addNotify() { if (logger.isLoggable(Level.FINE)) logger.fine("add notify on " + getClass().getName() + " " +
	 * hashCode()); super.addNotify(); if (logger.isLoggable(Level.FINE) &&
	 * getSelectedEditorComponent().getKeymap().getBoundActions().length == 0) { logger.fine("!!!!!!!! Key map is empty on " +
	 * getClass().getName() + " " + hashCode()); } }
	 * 
	 * @Override public void initKeyStrokes() { if (logger.isLoggable(Level.FINE)) logger.fine("Init key strokes on " + getClass().getName()
	 * + " " + hashCode()); super.initKeyStrokes(); }
	 * 
	 * @Override public void removeKeyStrokes() { if (logger.isLoggable(Level.FINE)) logger.fine("Remove key strokes on " +
	 * getClass().getName() + " " + hashCode()); super.removeKeyStrokes(); }
	 * 
	 * @Override public void removeNotify() { if (logger.isLoggable(Level.FINE)) logger.fine("remove notify on " + getClass().getName() +
	 * " " + hashCode()); super.removeNotify(); }
	 */
}
