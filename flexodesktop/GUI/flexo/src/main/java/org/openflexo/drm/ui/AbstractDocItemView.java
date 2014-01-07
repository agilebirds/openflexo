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
package org.openflexo.drm.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;

import org.openflexo.drm.ActionType;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemAction;
import org.openflexo.drm.DocItemVersion;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.action.AddToEmbeddingChildItem;
import org.openflexo.drm.action.AddToInheritanceChildItem;
import org.openflexo.drm.action.AddToRelatedToItem;
import org.openflexo.drm.action.RemoveEmbeddingChildItem;
import org.openflexo.drm.action.RemoveInheritanceChildItem;
import org.openflexo.drm.action.RemoveRelatedToItem;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wysiwyg.FlexoWysiwyg;
import org.openflexo.wysiwyg.FlexoWysiwygLight;

/**
 * Please comment this class
 * 
 * @author yourname
 * 
 */
public abstract class AbstractDocItemView extends JPanel {

	protected static final Logger logger = Logger.getLogger(AbstractDocItemView.class.getPackage().getName());

	protected FlexoController _controller;
	protected DocItem _docItem;

	protected JPanel topPanel;
	protected JTextField titleTF;
	protected JComboBox languageCB;
	protected JTextArea descriptionTA;

	protected EditorPanel editorPanel;

	protected JPanel rightPanel;
	protected GeneralInfoPanel generalInfoPanel;
	protected HistoryPanel historyPanel;

	protected JPanel bottomPanel;
	protected DocItemListView inheritanceChildsListView;
	protected DocItemListView embeddingChildsListView;
	protected DocItemListView relatedToListView;

	private DocumentListener titleListener;

	FlexoEditor _editor;

	public DocItem getDocItem() {
		return _docItem;
	}

	public void setDocItem(DocItem docItem) {
		if (_docItem != docItem) {
			logger.info("Sets docItem to be " + docItem);
			_docItem = docItem;
			updateViewFromModel();
			historyPanel.setDefaultAction();
		}
	}

	public AbstractDocItemView(DocItem docItem, FlexoController controller, FlexoEditor editor) {
		super(new BorderLayout());
		_controller = controller;
		_editor = editor;
		_docItem = docItem;

		topPanel = new JPanel(new BorderLayout());

		languageCB = new JComboBox(docItem.getDocResourceCenter().getLanguages());
		languageCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				titleTF.setText(_docItem.getTitle(getCurrentLanguage()));
				if (getDocResourceManager().isEdited(_docItem)) {
					getDocResourceManager().getEditedVersion(_docItem).setLanguage(getCurrentLanguage());
				}
				updateViewFromModel();
			}
		});

		titleTF = new JTextField();
		titleListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				valueChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				valueChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				valueChanged();
			}

			protected void valueChanged() {
				_docItem.setTitle(titleTF.getText(), getCurrentLanguage());
			}
		};
		titleTF.getDocument().addDocumentListener(titleListener);

		JLabel titleLabel = new JLabel(FlexoLocalization.localizedForKey("title") + " : ");
		topPanel.add(titleLabel, BorderLayout.WEST);
		topPanel.add(titleTF, BorderLayout.CENTER);
		topPanel.add(languageCB, BorderLayout.EAST);

		add(topPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new BorderLayout());

		descriptionTA = new JTextArea();
		descriptionTA.setRows(3);
		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setText(FlexoLocalization.localizedForKey("maintainer_description", descriptionLabel));
		descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		descriptionLabel.setForeground(Color.DARK_GRAY);
		descriptionLabel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		JPanel descriptionPanel = new JPanel(new BorderLayout());
		descriptionPanel.add(descriptionLabel, BorderLayout.NORTH);
		descriptionPanel.add(descriptionTA, BorderLayout.CENTER);
		descriptionTA.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				valueChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				valueChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				valueChanged();
			}

			protected void valueChanged() {
				_docItem.setDescription(descriptionTA.getText());
			}
		});

		editorPanel = new EditorPanel();

		centerPanel.add(descriptionPanel, BorderLayout.NORTH);
		centerPanel.add(editorPanel, BorderLayout.CENTER);

		add(centerPanel, BorderLayout.CENTER);

		rightPanel = new JPanel(new BorderLayout());

		generalInfoPanel = new GeneralInfoPanel();
		historyPanel = makeHistoryPanel();

		rightPanel.add(generalInfoPanel, BorderLayout.NORTH);
		rightPanel.add(historyPanel, BorderLayout.CENTER);

		add(rightPanel, BorderLayout.EAST);

		bottomPanel = new JPanel(new GridLayout(1, 3));
		inheritanceChildsListView = new DocItemListView("inheritance_child_items", new DocItemListView.DocItemListModel() {
			@Override
			public Vector<DocItem> getItems() {
				return _docItem.getInheritanceChildItems();
			}

			@Override
			public void itemAdded() {
				AddToInheritanceChildItem.actionType.makeNewAction(_docItem, null, _editor).doAction();
			}

			@Override
			public void itemRemoved(DocItem anItem) {
				RemoveInheritanceChildItem.actionType.makeNewAction(anItem, null, _editor).doAction();
				updateViewFromModel();
			}

			@Override
			public void itemDoubleClicked(DocItem anItem) {
				if (_controller != null) {
					_controller.selectAndFocusObject(anItem);
				}
			}
		});
		embeddingChildsListView = new DocItemListView("embedding_child_items", new DocItemListView.DocItemListModel() {
			@Override
			public Vector<DocItem> getItems() {
				return _docItem.getEmbeddingChildItems();
			}

			@Override
			public void itemAdded() {
				AddToEmbeddingChildItem.actionType.makeNewAction(_docItem, null, _editor).doAction();
			}

			@Override
			public void itemRemoved(DocItem anItem) {
				RemoveEmbeddingChildItem.actionType.makeNewAction(anItem, null, _editor).doAction();
				updateViewFromModel();
			}

			@Override
			public void itemDoubleClicked(DocItem anItem) {
				if (_controller != null) {
					_controller.selectAndFocusObject(anItem);
				}
			}
		});
		relatedToListView = new DocItemListView("related_to_items", new DocItemListView.DocItemListModel() {
			@Override
			public Vector<DocItem> getItems() {
				return _docItem.getRelatedToItems();
			}

			@Override
			public void itemAdded() {
				AddToRelatedToItem.actionType.makeNewAction(_docItem, null, _editor).doAction();
			}

			@Override
			public void itemRemoved(DocItem anItem) {
				Vector<FlexoObject> globalSelection = new Vector<FlexoObject>();
				globalSelection.add(_docItem);
				RemoveRelatedToItem.actionType.makeNewAction(anItem, globalSelection, _editor).doAction();
				updateViewFromModel();
			}

			@Override
			public void itemDoubleClicked(DocItem anItem) {
				if (_controller != null) {
					_controller.selectAndFocusObject(anItem);
				}
			}
		});

		bottomPanel.add(inheritanceChildsListView);
		bottomPanel.add(embeddingChildsListView);
		bottomPanel.add(relatedToListView);

		add(bottomPanel, BorderLayout.SOUTH);

		updateViewFromModel();
	}

	protected HistoryPanel makeHistoryPanel() {
		return new HistoryPanel();
	}

	public Language getCurrentLanguage() {
		return (Language) languageCB.getSelectedItem();
	}

	public FlexoProject getProject() {
		if (_controller != null) {
			return _controller.getProject();
		}
		return null;
	}

	public void setCurrentAction(DocItemAction currentAction) {
		historyPanel.setCurrentAction(currentAction);
	}

	protected class GeneralInfoPanel extends InfoPanel {
		private JTextField docItemIdentifierTF;
		private JTextField currentStatusTF;
		protected DocItemSelector parentItemRelatedToInheritanceDIS;
		protected DocItemSelector parentItemRelatedToEmbeddingDIS;
		protected JCheckBox isEmbeddedCB;
		protected JCheckBox isHiddenCB;

		protected GeneralInfoPanel() {
			super();

			addField("identifier", docItemIdentifierTF = new JTextField(15), true, false);
			docItemIdentifierTF.setEnabled(false);

			addField("status", currentStatusTF = new JTextField(15), true, false);
			currentStatusTF.setEnabled(false);

			addField("extends", parentItemRelatedToInheritanceDIS = new DocItemSelector(_docItem.getInheritanceParentItem()) {
				@Override
				public void apply() {
					super.apply();
					logger.info("Sets parent related to inheritance to be " + getEditedObject());
					_docItem.setInheritanceParentItem(getEditedObject());
					updateViewFromModel();
				}
			}, true, false);
			parentItemRelatedToInheritanceDIS.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						if (_docItem.getInheritanceParentItem() != null) {
							if (_controller != null) {
								_controller.selectAndFocusObject(_docItem.getInheritanceParentItem());
							}
						}
					}
				}
			});

			addField("found_in", parentItemRelatedToEmbeddingDIS = new DocItemSelector(_docItem.getEmbeddingParentItem()) {
				@Override
				public void apply() {
					super.apply();
					logger.info("Sets parent related to embedding to be " + getEditedObject());
					_docItem.setEmbeddingParentItem(getEditedObject());
				}
			}, true, false);
			parentItemRelatedToEmbeddingDIS.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						if (_docItem.getInheritanceParentItem() != null) {
							if (_controller != null) {
								_controller.selectAndFocusObject(_docItem.getEmbeddingParentItem());
							}
						}
					}
				}
			});

			addField("is_embedded", isEmbeddedCB = new JCheckBox("", _docItem.getIsEmbedded()), true, false);
			isEmbeddedCB.setEnabled(_docItem.getEmbeddingParentItem() != null);
			isEmbeddedCB.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					_docItem.setIsEmbedded(isEmbeddedCB.isSelected());
				}
			});

			addField("is_hidden", isHiddenCB = new JCheckBox("", _docItem.getIsHidden()), true, false);
			isHiddenCB.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					_docItem.setIsHidden(isHiddenCB.isSelected());
				}
			});

		}

		public void updateViewFromModel() {
			docItemIdentifierTF.setText(_docItem.getIdentifier());
			currentStatusTF.setText(_docItem.getLocalizedStatusForLanguage(getCurrentLanguage()));
			parentItemRelatedToInheritanceDIS.setEditedObject(_docItem.getInheritanceParentItem());
			parentItemRelatedToEmbeddingDIS.setEditedObject(_docItem.getEmbeddingParentItem());
			isEmbeddedCB.setEnabled(_docItem.getEmbeddingParentItem() != null);
			isEmbeddedCB.setSelected(_docItem.getIsEmbedded());
			isHiddenCB.setSelected(_docItem.getIsHidden());
		}

	}

	protected class EditorPanel extends JPanel {
		private final JPanel shortHTMLDescriptionPanel;
		private final JPanel fullHTMLDescriptionPanel;
		private final JEditorPane shortHTMLDescriptionLabel;
		private final JEditorPane fullHTMLDescriptionLabel;
		FlexoWysiwyg shortHTMLDescriptionEditor;
		FlexoWysiwyg fullHTMLDescriptionEditor;
		private boolean _isEditing;

		protected EditorPanel() {
			super(new BorderLayout());
			// RelativeImageView.addToImagePaths(_docItem.getFolder().getDirectory());
			shortHTMLDescriptionPanel = new JPanel(new BorderLayout());
			JLabel shortDescription = new JLabel();
			shortDescription.setText(FlexoLocalization.localizedForKey("short_formatted_description", shortDescription));
			shortDescription.setHorizontalAlignment(SwingConstants.CENTER);
			shortDescription.setForeground(Color.DARK_GRAY);
			shortHTMLDescriptionLabel = new JEditorPane();
			shortHTMLDescriptionLabel.setContentType("text/html");
			shortHTMLDescriptionLabel.setOpaque(true);
			shortHTMLDescriptionLabel.setEditable(false);
			shortHTMLDescriptionLabel.setBackground(Color.WHITE);
			shortHTMLDescriptionLabel.setPreferredSize(new Dimension(500, 100));
			shortHTMLDescriptionPanel.add(shortDescription, BorderLayout.NORTH);
			shortHTMLDescriptionPanel.add(shortHTMLDescriptionLabel, BorderLayout.CENTER);

			fullHTMLDescriptionPanel = new JPanel(new BorderLayout());
			JLabel fullDescription = new JLabel();
			fullDescription.setText(FlexoLocalization.localizedForKey("full_formatted_description", fullDescription));
			fullDescription.setHorizontalAlignment(SwingConstants.CENTER);
			fullDescription.setForeground(Color.DARK_GRAY);
			fullHTMLDescriptionLabel = new JEditorPane();
			fullHTMLDescriptionLabel.setContentType("text/html");
			fullHTMLDescriptionLabel.setEditable(false);
			fullHTMLDescriptionLabel.setPreferredSize(new Dimension(500, 200));
			fullHTMLDescriptionLabel.setBackground(Color.WHITE);
			fullHTMLDescriptionLabel.setOpaque(true);
			/*fullHTMLDescriptionLabel.setVerticalAlignment(JLabel.TOP);
			fullHTMLDescriptionLabel.setHorizontalAlignment(JLabel.LEFT);*/
			fullHTMLDescriptionPanel.add(fullDescription, BorderLayout.NORTH);
			fullHTMLDescriptionPanel.add(fullHTMLDescriptionLabel, BorderLayout.CENTER);

			/*
			shortHTMLDescriptionEditor = new HTMLEditor(FlexoLocalization.localizedForKey("write_documentation_here")) {
			   public void textChanged(String newText) {
			       getDocResourceManager().getEditedVersion(_docItem).setShortHTMLDescription(newText);
			   }
			};
			shortHTMLDescriptionEditor.setPreferredSize(new Dimension(500,100));
			shortHTMLDescriptionEditor.setPreferredImagePath(new File(_docItem.getFolder().getDirectory(),"Figures"));
			fullHTMLDescriptionEditor = new HTMLEditor(FlexoLocalization.localizedForKey("write_documentation_here")) {
			    public void textChanged(String newText) {
			        getDocResourceManager().getEditedVersion(_docItem).setFullHTMLDescription(newText);
			    }
			};
			fullHTMLDescriptionEditor.setPreferredImagePath(new File(_docItem.getFolder().getDirectory(),"Figures"));
			*/
			// change AJA
			abstract class FlexoWysiwygHelpDocEditor extends FlexoWysiwygLight {

				public FlexoWysiwygHelpDocEditor(String htmlContent, File cssFile, boolean isViewSourceAvailable) {
					super(htmlContent, cssFile, isViewSourceAvailable);
					setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
					setStatusBarVisible(false);
					addSupportForInsertedObjects(new File(_docItem.getFolder().getDirectory(), "Figures"));
				}
			}
			File cssFile = getDocResourceManager().getDocumentationCssResourceFile();
			shortHTMLDescriptionEditor = new FlexoWysiwygHelpDocEditor(FlexoLocalization.localizedForKey("write_documentation_here"),
					cssFile, true) {
				@Override
				public void notifyTextChanged() {
					getDocResourceManager().getEditedVersion(_docItem).setShortHTMLDescription(shortHTMLDescriptionEditor.getBodyContent());
				}
			};
			shortHTMLDescriptionEditor.setPreferredSize(new Dimension(850, 250));

			fullHTMLDescriptionEditor = new FlexoWysiwygHelpDocEditor(FlexoLocalization.localizedForKey("write_documentation_here"),
					cssFile, true) {
				@Override
				public void notifyTextChanged() {
					getDocResourceManager().getEditedVersion(_docItem).setFullHTMLDescription(shortHTMLDescriptionEditor.getBodyContent());
				}
			};
			fullHTMLDescriptionEditor.setPreferredSize(new Dimension(850, 500));

			// END change AJA

			add(shortHTMLDescriptionPanel, BorderLayout.NORTH);
			add(fullHTMLDescriptionPanel, BorderLayout.CENTER);
			_isEditing = false;
			willShow();
			updateViewFromModel();
		}

		public void willShow() {
			updateViewerEditorPaths();
		}

		private void updateViewerEditorPaths() {
			Document doc = shortHTMLDescriptionLabel.getDocument();
			if (doc instanceof HTMLDocument) {
				URL u;
				try {
					u = _docItem.getFolder().getDirectory().toURI().toURL();
					((HTMLDocument) doc).setBase(u);
					// AJA shortHTMLDescriptionEditor.getEkitCore().getHtmlDoc().setBase(u);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			doc = fullHTMLDescriptionLabel.getDocument();
			if (doc instanceof HTMLDocument) {
				URL u;
				try {
					u = _docItem.getFolder().getDirectory().toURI().toURL();
					((HTMLDocument) doc).setBase(u);
					// AJA fullHTMLDescriptionEditor.getEkitCore().getHtmlDoc().setBase(u);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			// RelativeImageView.addToImagePaths(_docItem.getFolder().getDirectory());
			// AJA fullHTMLDescriptionEditor.setPreferredImagePath(new File(_docItem.getFolder().getDirectory(),"Figures"));
			// AJA shortHTMLDescriptionEditor.setPreferredImagePath(new File(_docItem.getFolder().getDirectory(),"Figures"));
		}

		public void updateViewFromModel() {
			if (getDocResourceManager().isEdited(_docItem)) {
				if (!isEditing()) {
					edit();
				}
				if (getDocResourceManager().getEditedVersion(_docItem) != null) {
					DocItemVersion version = getDocResourceManager().getEditedVersion(_docItem);
					/*                 if ((historyPanel.getCurrentAction() != null)
					                         && (historyPanel.getCurrentAction().getVersion() != null)) {
					                     DocItemVersion version = historyPanel.getCurrentAction().getVersion();*/
					shortHTMLDescriptionEditor.setContent(version.getShortHTMLDescription()); // change AJA
					fullHTMLDescriptionEditor.setContent(version.getFullHTMLDescription()); // change AJA
				} else {
					logger.warning("You are about to edit a null version, which is a strange situation. Good luck !");
				}
			} else {
				if (isEditing()) {
					closeEdition();
				}
				if (historyPanel != null && historyPanel.getCurrentAction() != null && historyPanel.getCurrentAction().getVersion() != null) {
					DocItemVersion version = historyPanel.getCurrentAction().getVersion();
					if (version.getShortHTMLDescription() == null) {
						shortHTMLDescriptionLabel.setForeground(Color.GRAY);
						/*shortHTMLDescriptionLabel.setVerticalAlignment(JLabel.CENTER);
						shortHTMLDescriptionLabel.setHorizontalAlignment(JLabel.CENTER);*/
						shortHTMLDescriptionLabel.setText(FlexoLocalization.localizedForKey("not_defined"));
					} else {
						shortHTMLDescriptionLabel.setForeground(Color.BLACK);
						/*shortHTMLDescriptionLabel.setVerticalAlignment(JLabel.TOP);
						shortHTMLDescriptionLabel.setHorizontalAlignment(JLabel.LEFT);*/
						shortHTMLDescriptionLabel.setText("<html>" + version.getShortHTMLDescription() + "</html>");
					}
					if (version.getFullHTMLDescription() == null) {
						fullHTMLDescriptionLabel.setForeground(Color.GRAY);
						/*fullHTMLDescriptionLabel.setVerticalAlignment(JLabel.CENTER);
						fullHTMLDescriptionLabel.setHorizontalAlignment(JLabel.CENTER); */
						fullHTMLDescriptionLabel.setText(FlexoLocalization.localizedForKey("not_defined"));
					} else {
						fullHTMLDescriptionLabel.setForeground(Color.BLACK);
						/*fullHTMLDescriptionLabel.setVerticalAlignment(JLabel.TOP);
						fullHTMLDescriptionLabel.setHorizontalAlignment(JLabel.LEFT);*/
						fullHTMLDescriptionLabel.setText("<html>" + version.getFullHTMLDescription() + "</html>");
					}
				} else {
					fullHTMLDescriptionLabel.setForeground(Color.GRAY);
					/*fullHTMLDescriptionLabel.setVerticalAlignment(JLabel.CENTER);
					fullHTMLDescriptionLabel.setHorizontalAlignment(JLabel.CENTER);*/
					fullHTMLDescriptionLabel.setText(FlexoLocalization.localizedForKey("no_selection"));
					shortHTMLDescriptionLabel.setForeground(Color.GRAY);
					/*shortHTMLDescriptionLabel.setVerticalAlignment(JLabel.CENTER);
					shortHTMLDescriptionLabel.setHorizontalAlignment(JLabel.CENTER);*/
					shortHTMLDescriptionLabel.setText(FlexoLocalization.localizedForKey("no_selection"));
				}
			}
			updateViewerEditorPaths();
		}

		public void edit() {
			_isEditing = true;
			remove(shortHTMLDescriptionPanel);
			remove(fullHTMLDescriptionPanel);
			add(shortHTMLDescriptionEditor, BorderLayout.NORTH);
			add(fullHTMLDescriptionEditor, BorderLayout.CENTER);
			revalidate();
			repaint();
		}

		public void closeEdition() {
			_isEditing = false;
			remove(shortHTMLDescriptionEditor);
			remove(fullHTMLDescriptionEditor);
			add(shortHTMLDescriptionPanel, BorderLayout.NORTH);
			add(fullHTMLDescriptionPanel, BorderLayout.CENTER);
			revalidate();
			repaint();
		}

		public boolean isEditing() {
			return _isEditing;
		}
	}

	protected class HistoryPanel extends JPanel implements ListSelectionListener {
		protected JList actionList;
		protected JButton editButton;
		protected JButton submitReviewButton;
		protected JButton approveButton;
		protected JButton refuseButton;

		protected JPanel actionPanel;

		protected JTextField authorTF;
		protected JTextField actionTF;
		protected JTextField dateTF;
		protected JTextField versionTF;
		protected JTextField languageTF;
		protected JTextArea noteTA;

		class HistoryPanelListModel extends AbstractListModel {
			@Override
			public int getSize() {
				return _docItem.getActions().size();
			}

			@Override
			public Object getElementAt(int index) {
				if (index < _docItem.getActions().size()) {
					return _docItem.getActions().elementAt(index);
				}
				return null;
			}
		}

		public DocItemAction getCurrentAction() {
			return (DocItemAction) actionList.getSelectedValue();
		}

		public void setCurrentAction(DocItemAction currentAction) {
			if (currentAction == null) {
				actionList.clearSelection();
			} else {
				actionList.setSelectedValue(currentAction, true);
			}
		}

		protected HistoryPanel() {
			super(new BorderLayout());
			JLabel historyLabel = new JLabel();
			historyLabel.setText(FlexoLocalization.localizedForKey("history", historyLabel));
			historyLabel.setHorizontalAlignment(SwingConstants.CENTER);
			historyLabel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			actionList = new JList(new HistoryPanelListModel()/*_docItem.getActions()*/);
			actionList.setCellRenderer(new HistoryPanelCellRenderer());
			actionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			actionList.addListSelectionListener(HistoryPanel.this);
			actionPanel = new JPanel(new FlowLayout());

			editButton = new JButton();
			editButton.setText(FlexoLocalization.localizedForKey("edit"));
			editButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!getDocResourceManager().isEdited(_docItem) && getCurrentAction() != null) {
						getDocResourceManager().editVersion(getCurrentAction().getVersion());
					}
					AbstractDocItemView.this.updateViewFromModel();
				}
			});
			actionPanel.add(editButton);

			submitReviewButton = new JButton();
			submitReviewButton.setText(FlexoLocalization.localizedForKey("submit"));
			submitReviewButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (_docItem.getLastActionForLanguage(getCurrentLanguage()) == null) {
						if (!getDocResourceManager().isEdited(_docItem)) {
							getDocResourceManager().beginVersionSubmission(_docItem, getCurrentLanguage());
						} else {
							historyPanel.setCurrentAction(getDocResourceManager().endVersionSubmission(_docItem));
						}
					} else if (_docItem.getLastActionForLanguage(getCurrentLanguage()) == getCurrentAction()) {
						if (!getDocResourceManager().isEdited(_docItem)) {
							getDocResourceManager().beginVersionReview(getCurrentAction().getVersion());
						} else {
							if (getDocResourceManager().isSubmitting(_docItem)) {
								historyPanel.setCurrentAction(getDocResourceManager().endVersionReview(_docItem));
							} else {
								getDocResourceManager().stopEditVersion(getCurrentAction().getVersion());
							}
						}
					} else {
						if (getDocResourceManager().isEdited(_docItem)) {
							/*	if (editorPanel.shortHTMLDescriptionEditor.getEkitCore().isSourceWindowActive())
									editorPanel.shortHTMLDescriptionEditor.getEkitCore().toggleSourceWindow();
								if (editorPanel.fullHTMLDescriptionEditor.getEkitCore().isSourceWindowActive())
									editorPanel.fullHTMLDescriptionEditor.getEkitCore().toggleSourceWindow();  change AJA */
							getDocResourceManager().stopEditVersion(getCurrentAction().getVersion());
						}
					}
					AbstractDocItemView.this.updateViewFromModel();
				}
			});
			actionPanel.add(submitReviewButton);

			approveButton = new JButton();
			approveButton.setText(FlexoLocalization.localizedForKey("approve"));
			approveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (getCurrentAction() != null && getCurrentAction().isProposal() && getCurrentAction().isPending()) {
						historyPanel.setCurrentAction(getDocResourceManager().approveVersion(getCurrentAction().getVersion()));
						AbstractDocItemView.this.updateViewFromModel();
					}
				}
			});
			actionPanel.add(approveButton);

			refuseButton = new JButton();
			refuseButton.setText(FlexoLocalization.localizedForKey("refuse"));
			refuseButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (getCurrentAction() != null && getCurrentAction().isProposal() && getCurrentAction().isPending()) {
						historyPanel.setCurrentAction(getDocResourceManager().refuseVersion(getCurrentAction().getVersion()));
						AbstractDocItemView.this.updateViewFromModel();
					}
				}
			});
			actionPanel.add(refuseButton);

			InfoPanel historyInfoPanel = new InfoPanel() {
				@Override
				protected void valueChanged(JTextComponent component, String value) {
					if (component == noteTA) {
						if (getCurrentAction() != null) {
							getCurrentAction().setNote(value);
						}
					}
				}
			};
			historyInfoPanel.addField("author", authorTF = new JTextField(15), true, false);
			authorTF.setEnabled(false);
			historyInfoPanel.addField("action", actionTF = new JTextField(15), true, false);
			actionTF.setEnabled(false);
			historyInfoPanel.addField("date", dateTF = new JTextField(15), true, false);
			dateTF.setEnabled(false);
			historyInfoPanel.addField("version", versionTF = new JTextField(15), true, false);
			versionTF.setEnabled(false);
			historyInfoPanel.addField("language", languageTF = new JTextField(15), true, false);
			languageTF.setEnabled(false);
			noteTA = new JTextArea(5, 15);
			historyInfoPanel.addField("note", new JScrollPane(noteTA), true, true);
			noteTA.setEnabled(false);
			noteTA.getDocument().addDocumentListener(historyInfoPanel.new InfoPanelDocumentListener(noteTA));

			JPanel historyCenterPanel = new JPanel(new BorderLayout());
			historyCenterPanel.add(new JScrollPane(actionList), BorderLayout.CENTER);
			historyCenterPanel.add(historyInfoPanel, BorderLayout.SOUTH);

			add(historyLabel, BorderLayout.NORTH);
			add(historyCenterPanel, BorderLayout.CENTER);
			add(actionPanel, BorderLayout.SOUTH);

			setDefaultAction();

		}

		public void updateViewFromModel() {
			actionList.updateUI();
			refreshButtons();
			refreshActionInfos();
		}

		class HistoryPanelCellRenderer extends DefaultListCellRenderer {
			private final Color GREEN = new Color(20, 120, 20);

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel returned = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				DocItemAction docItemAction = (DocItemAction) value;
				returned.setText(docItemAction.getLocalizedName());
				if (docItemAction.getVersion().getLanguage() == getCurrentLanguage()) {
					returned.setForeground(Color.BLACK);
					if (docItemAction.getActionType() == ActionType.APPROVED) {
						returned.setForeground(GREEN);
					}
					if (docItemAction.getActionType() == ActionType.REFUSED) {
						returned.setForeground(Color.RED);
					}
				} else {
					returned.setForeground(Color.LIGHT_GRAY);
				}
				return returned;
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			/*HistoryPanel.this.updateViewFromModel();*/
			editorPanel.updateViewFromModel();
			refreshButtons();
			refreshActionInfos();
		}

		protected void refreshButtons() {
			actionList.setEnabled(!getDocResourceManager().isEdited(_docItem));
			if (getDocResourceManager().isEdited(_docItem)) {
				submitReviewButton.setText(FlexoLocalization.localizedForKey("done"));
				submitReviewButton.setEnabled(true);
				approveButton.setEnabled(false);
				refuseButton.setEnabled(false);
				editButton.setEnabled(false);
			} else {
				if (_docItem.getLastActionForLanguage(getCurrentLanguage()) == null) {
					submitReviewButton.setText(FlexoLocalization.localizedForKey("submit"));
					submitReviewButton.setEnabled(true);
				} else {
					submitReviewButton.setText(FlexoLocalization.localizedForKey("review"));
					submitReviewButton.setEnabled(_docItem.getLastActionForLanguage(getCurrentLanguage()) == getCurrentAction());
				}
				if (getCurrentAction() != null && getCurrentAction().isProposal()
						&& getCurrentAction().getVersion().getLanguage() == getCurrentLanguage()) {
					approveButton.setEnabled(true);
					refuseButton.setEnabled(true);
					editButton.setEnabled(true);
				} else {
					approveButton.setEnabled(false);
					refuseButton.setEnabled(false);
					editButton.setEnabled(false);
				}

			}
			approveButton.setText(FlexoLocalization.localizedForKey("approve"));
			refuseButton.setText(FlexoLocalization.localizedForKey("refuse"));

		}

		private void refreshActionInfos() {
			if (getCurrentAction() == null) {
				authorTF.setText("");
				actionTF.setText("");
				dateTF.setText("");
				versionTF.setText("");
				languageTF.setText("");
				noteTA.setText("");
				noteTA.setEnabled(false);
			} else {
				authorTF.setText(getCurrentAction().getAuthorId());
				actionTF.setText(getCurrentAction().getActionType().getLocalizedName());
				dateTF.setText(getCurrentAction().getLocalizedFullActionDate());
				versionTF.setText(getCurrentAction().getVersion().getVersion().toString());
				languageTF.setText(getCurrentAction().getVersion().getLanguage().getIdentifier());
				noteTA.setText(getCurrentAction().getNote());
				noteTA.setEnabled(true);
			}
		}

		protected void setDefaultAction() {
			DocItemAction lastApprovedAction = _docItem.getLastApprovedActionForLanguage(getCurrentLanguage());
			if (lastApprovedAction != null) {
				setCurrentAction(lastApprovedAction);
			} else {
				setCurrentAction(_docItem.getLastActionForLanguage(getCurrentLanguage()));
			}

		}
	}

	public class InfoPanel extends JPanel {
		private final GridBagLayout _gridbag;

		public InfoPanel() {
			super();
			_gridbag = new GridBagLayout();
			setLayout(_gridbag);

		}

		protected void addField(String text, JComponent component, boolean expandX, boolean expandY) {
			JLabel label = new JLabel();
			label.setText(FlexoLocalization.localizedForKey(text, label));
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(3, 3, 3, 3);
			c.weightx = 1.0;
			c.gridwidth = GridBagConstraints.RELATIVE;
			if (expandY) {
				c.anchor = GridBagConstraints.NORTHEAST;
			} else {
				c.anchor = GridBagConstraints.EAST;
			}
			_gridbag.setConstraints(label, c);
			add(label);
			if (expandX) {
				c.fill = GridBagConstraints.BOTH;
				c.anchor = GridBagConstraints.CENTER;
				if (expandY) {
					c.weighty = 1.0;
					// c.gridheight = GridBagConstraints.RELATIVE;
				}
			} else {
				c.fill = GridBagConstraints.NONE;
				c.anchor = GridBagConstraints.WEST;

			}
			c.weightx = 2.0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			_gridbag.setConstraints(component, c);
			add(component);

		}

		protected class InfoPanelDocumentListener implements DocumentListener {
			private final JTextComponent _textComponent;

			protected InfoPanelDocumentListener(JTextComponent textComponent) {
				super();
				_textComponent = textComponent;
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				valueChanged(_textComponent, _textComponent.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				valueChanged(_textComponent, _textComponent.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				valueChanged(_textComponent, _textComponent.getText());
			}

		}

		protected void valueChanged(JTextComponent component, String value) {
			logger.info("valueChanged() for " + component + " with " + value);
		}

	}

	public FlexoController getController() {
		return _controller;
	}

	public static String getTitleForDocItem(DocItem docItem) {
		return docItem.getIdentifier();
	}

	public void updateViewFromModel() {
		generalInfoPanel.updateViewFromModel();
		editorPanel.updateViewFromModel();
		historyPanel.updateViewFromModel();
		inheritanceChildsListView.updateViewFromModel();
		embeddingChildsListView.updateViewFromModel();
		relatedToListView.updateViewFromModel();
		updateTitle();
		descriptionTA.setText(_docItem.getDescription());
	}

	private void updateTitle() {
		String title = _docItem.getTitle(getCurrentLanguage());
		titleTF.getDocument().removeDocumentListener(titleListener);
		if (title != null) {
			titleTF.setText(title);
		} else {
			titleTF.setText("");
		}
		titleTF.getDocument().addDocumentListener(titleListener);

	}

	public DocResourceManager getDocResourceManager() {
		return getController().getApplicationContext().getDocResourceManager();
	}

}
