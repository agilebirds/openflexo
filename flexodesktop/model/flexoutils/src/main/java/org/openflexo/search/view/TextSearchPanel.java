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
package org.openflexo.search.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.search.EndOfDocumentHasBeenReachedException;
import org.openflexo.search.ResultNotFoundException;
import org.openflexo.search.TextQuery;
import org.openflexo.search.TextQueryEngine;
import org.openflexo.search.TextQueryResult;
import org.openflexo.search.TextQueryResult.Direction;
import org.openflexo.search.TextQueryResult.Result;

public class TextSearchPanel extends JPanel implements CaretListener {

	public class FindReplaceFieldPanel extends JPanel implements ActionListener {

		protected JComboBox findField;
		protected JComboBox replaceField;

		public FindReplaceFieldPanel() {
			super(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.insets = new Insets(5, 5, 5, 5);
			JLabel find = new JLabel();
			find.setText(FlexoLocalization.localizedForKey("find"));
			add(find, gbc);
			findField = new JComboBox(new DefaultComboBoxModel(new Vector<String>()));
			findField.setEditable(true);
			if (query.getSearchedText() != null && query.getSearchedText().length() != 0) {
				((DefaultComboBoxModel) findField.getModel()).insertElementAt(query.getSearchedText(), 0);
				findField.setSelectedIndex(0);
			}
			if (findField.getEditor().getEditorComponent() instanceof JTextComponent) {
				((JTextComponent) findField.getEditor().getEditorComponent()).getDocument().addDocumentListener(new DocumentListener() {

					@Override
					public void changedUpdate(DocumentEvent e) {
						resultNeedsUpdate = true;
						updateButtons();
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						resultNeedsUpdate = true;
						updateButtons();
					}

					@Override
					public void removeUpdate(DocumentEvent e) {
						resultNeedsUpdate = true;
						updateButtons();
					}

				});
				if (findField.getEditor().getEditorComponent() instanceof JTextField) {
					((JTextField) findField.getEditor().getEditorComponent()).addActionListener(this);
				}
			}
			findField.addActionListener(this);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.gridx = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			add(findField, gbc);
			JLabel replace = new JLabel();
			replace.setText(FlexoLocalization.localizedForKey("replace_with"));
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.insets = new Insets(0, 5, 5, 5);
			add(replace, gbc);
			replaceField = new JComboBox(new DefaultComboBoxModel(new Vector<String>()));
			replaceField.setEditable(true);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.gridx = 1;
			gbc.insets = new Insets(0, 5, 5, 5);
			add(replaceField, gbc);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == findField) {
				String s = (String) findField.getEditor().getItem();
				int index = ((DefaultComboBoxModel) findField.getModel()).getIndexOf(s);
				if (index == -1) {
					((DefaultComboBoxModel) findField.getModel()).insertElementAt(s, 0);
					if (((DefaultComboBoxModel) findField.getModel()).getSize() > 15) {
						((DefaultComboBoxModel) findField.getModel()).removeElementAt(((DefaultComboBoxModel) findField.getModel())
								.getSize() - 1);
					}
					findField.setSelectedIndex(0);
					findField.revalidate();
				} else {
					if (index != 0) {
						((DefaultComboBoxModel) findField.getModel()).removeElementAt(index);
						((DefaultComboBoxModel) findField.getModel()).insertElementAt(s, 0);
						findField.setSelectedIndex(0);
						findField.revalidate();
					}
				}
				updateQuery();
			} else if (e.getSource() == replaceField) {
				String s = (String) replaceField.getEditor().getItem();
				int index = ((DefaultComboBoxModel) replaceField.getModel()).getIndexOf(s);
				if (index == -1) {
					((DefaultComboBoxModel) replaceField.getModel()).insertElementAt(s, 0);
					if (((DefaultComboBoxModel) replaceField.getModel()).getSize() > 15) {
						((DefaultComboBoxModel) replaceField.getModel()).removeElementAt(((DefaultComboBoxModel) replaceField.getModel())
								.getSize() - 1);
					}

					replaceField.setSelectedIndex(0);
					replaceField.revalidate();
				} else {
					if (index != 0) {
						((DefaultComboBoxModel) replaceField.getModel()).removeElementAt(index);
						((DefaultComboBoxModel) replaceField.getModel()).insertElementAt(s, 0);
						replaceField.setSelectedIndex(0);
						replaceField.revalidate();
					}
				}
			} else if (e.getSource() == findField.getEditor().getEditorComponent()) {
				updateResults();
				gotoNextResult();
			}
			return;
		}
	}

	public class DirectionPanel extends JPanel implements ActionListener {
		protected JRadioButton forward;
		protected JRadioButton backward;

		public DirectionPanel() {
			super(new GridBagLayout());
			setBorder(BorderFactory.createTitledBorder(FlexoLocalization.localizedForKey("direction")));
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.weighty = 0;
			forward = new JRadioButton(FlexoLocalization.localizedForKey("forward"));
			forward.setSelected(true);
			forward.addActionListener(this);
			backward = new JRadioButton(FlexoLocalization.localizedForKey("backward"));
			backward.addActionListener(this);
			add(forward, gbc);
			gbc.gridy = 1;
			add(backward, gbc);
			ButtonGroup group = new ButtonGroup();
			group.add(forward);
			group.add(backward);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			updateResultNavigator(false);
		}
	}

	public class ScopePanel extends JPanel implements ActionListener {
		protected JRadioButton all;
		protected JRadioButton selectedText;

		public ScopePanel() {
			super(new GridBagLayout());
			setBorder(BorderFactory.createTitledBorder(FlexoLocalization.localizedForKey("scope")));
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.weighty = 0;
			all = new JRadioButton(FlexoLocalization.localizedForKey("all"));
			all.setSelected(true);
			selectedText = new JRadioButton(FlexoLocalization.localizedForKey("selected_text"));
			all.addActionListener(this);
			selectedText.addActionListener(this);
			add(all, gbc);
			gbc.gridy = 1;
			add(selectedText, gbc);
			ButtonGroup group = new ButtonGroup();
			group.add(all);
			group.add(selectedText);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			updateResultNavigator(false);
		}
	}

	public class OptionPanel extends JPanel implements ActionListener {

		protected JCheckBox caseSensitive;
		protected JCheckBox wrapSearch;
		protected JCheckBox wholeWord;
		protected JCheckBox regularExpression;

		public OptionPanel() {
			super(new GridBagLayout());
			setBorder(BorderFactory.createTitledBorder(FlexoLocalization.localizedForKey("options")));
			caseSensitive = new JCheckBox(FlexoLocalization.localizedForKey("case_sensitive"));
			wrapSearch = new JCheckBox(FlexoLocalization.localizedForKey("wrap_search"));
			wrapSearch.setSelected(true);
			wholeWord = new JCheckBox(FlexoLocalization.localizedForKey("whole_word"));
			regularExpression = new JCheckBox(FlexoLocalization.localizedForKey("regular_expression"));
			caseSensitive.addActionListener(this);
			wrapSearch.addActionListener(this);
			wholeWord.addActionListener(this);
			regularExpression.addActionListener(this);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			add(caseSensitive, gbc);
			gbc.gridx = 1;
			gbc.weightx = 1;
			add(wrapSearch, gbc);
			gbc.weightx = 0;
			gbc.gridx = 0;
			gbc.gridy = 1;
			add(wholeWord, gbc);
			gbc.weightx = 1;
			gbc.gridx = 1;
			add(regularExpression, gbc);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			updateQuery();
		}
	}

	public class BottomPanel extends JPanel implements ActionListener {

		protected JLabel statusText;

		protected JButton find;
		protected JButton replace;
		protected JButton replaceFind;
		protected JButton replaceAll;

		protected JButton close;

		public BottomPanel() {
			super(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.gridwidth = 2;
			gbc.gridheight = 1;
			statusText = new JLabel();
			add(statusText, gbc);
			find = new JButton(FlexoLocalization.localizedForKey("find"));
			find.addActionListener(this);
			gbc.anchor = GridBagConstraints.WEST;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.gridx = 1;
			gbc.gridy = 0;
			add(find, gbc);
			replace = new JButton(FlexoLocalization.localizedForKey("replace"));
			replace.addActionListener(this);
			gbc.gridy = 1;
			add(replace, gbc);
			replaceFind = new JButton(FlexoLocalization.localizedForKey("replace/find"));
			replaceFind.addActionListener(this);
			gbc.gridx = 2;
			gbc.gridy = 0;
			add(replaceFind, gbc);
			replaceAll = new JButton(FlexoLocalization.localizedForKey("replace_all"));
			replaceAll.addActionListener(this);
			gbc.gridy = 1;
			add(replaceAll, gbc);
			close = new JButton(FlexoLocalization.localizedForKey("close"));
			close.addActionListener(this);
			gbc.insets = new Insets(5, 0, 0, 0);
			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.EAST;
			add(close, gbc);
			Component glue = Box.createGlue();
			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.gridheight = 2;
			gbc.fill = GridBagConstraints.BOTH;
			add(glue, gbc);
		}

		public void setStatusText(String text) {
			statusText.setText("<html>" + text + "</html>");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == find) {
				gotoNextResult();
			} else if (e.getSource() == close) {
				Window w = SwingUtilities.getWindowAncestor(this);
				if (w != null) {
					w.setVisible(false);
				}
			} else {
				if (resultNavigator != null) {
					if ((e.getSource() == replace || e.getSource() == replaceFind) && resultNavigator.getCurrentResult() != null) {
						if (textComponent.getSelectionStart() == resultNavigator.getCurrentResult().startOffset
								&& textComponent.getSelectionEnd() == resultNavigator.getCurrentResult().endOffset) {
							resultNavigator.replaceBy(getReplacementText());
							updateResults();
							updateButtons();
							if (e.getSource() == replaceFind) {
								gotoNextResult();
							}
						} else {
							gotoNextResult();
						}
					} else if (e.getSource() == replaceAll && resultNavigator != null) {
						if (resultNeedsUpdate) {
							updateResults();
						}
						resultNavigator.replaceAllBy(getReplacementText());
						updateResults();
					}
				}
			}
		}
	}

	private FindReplaceFieldPanel findReplacePanel;
	private DirectionPanel directionPanel;
	private ScopePanel scopePanel;
	private OptionPanel optionPanel;
	private BottomPanel bottomPanel;

	protected ITextComponent textComponent;
	protected TextQuery query;
	protected TextQueryResult queryResult;
	protected TextQueryResult.ResultNavigator resultNavigator;

	public TextSearchPanel() {
		super(new BorderLayout());
		this.query = new TextQuery();
		initUI();
		updateQuery();
	}

	public ITextComponent getTextComponent() {
		return textComponent;
	}

	public void setTextComponent(ITextComponent textComponent) {
		if (this.textComponent == textComponent) {
			return;
		}
		if (this.textComponent != null) {
			this.textComponent.removeCaretListener(this);
			queryResult = null;
		}
		this.textComponent = textComponent;
		if (this.textComponent != null) {
			this.textComponent.addCaretListener(this);
		}
		resultNeedsUpdate = true;
		updateFields();
		updateButtons();
		updateScopePanel();
	}

	public void notifyEditablePropertyChange() {
		updateFields();
		updateButtons();
	}

	private void updateFields() {
		if (textComponent != null) {
			findReplacePanel.findField.setEnabled(true);
			if (textComponent.getSelectedText() != null && textComponent.getSelectedText().length() != 0) {
				findReplacePanel.findField.getEditor().setItem(textComponent.getSelectedText());
			}
			if (textComponent.isEditable()) {
				findReplacePanel.replaceField.setEnabled(true);
			} else {
				findReplacePanel.replaceField.setEnabled(false);
			}

		} else {
			findReplacePanel.findField.setEnabled(false);
			findReplacePanel.replaceField.setEnabled(false);
		}
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	private void initUI() {
		JPanel northPanel = new JPanel(new BorderLayout());
		findReplacePanel = new FindReplaceFieldPanel();
		northPanel.add(findReplacePanel, BorderLayout.NORTH);
		JPanel centerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.weighty = 0;
		directionPanel = new DirectionPanel();
		centerPanel.add(directionPanel, gbc);
		scopePanel = new ScopePanel();
		gbc.gridx = 1;
		centerPanel.add(scopePanel, gbc);
		northPanel.add(centerPanel, BorderLayout.CENTER);
		optionPanel = new OptionPanel();
		northPanel.add(optionPanel, BorderLayout.SOUTH);
		add(northPanel, BorderLayout.NORTH);
		bottomPanel = new BottomPanel();
		add(bottomPanel, BorderLayout.SOUTH);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	protected boolean resultNeedsUpdate = true;

	protected void gotoNextResult() {
		if (resultNeedsUpdate) {
			updateResults();
		}
		try {
			textComponent.removeCaretListener(this);
			bottomPanel.setStatusText("");
			Result r = resultNavigator.getNextResult();
			textComponent.select(r.startOffset, r.endOffset);
		} catch (EndOfDocumentHasBeenReachedException e) {
			bottomPanel.setStatusText(FlexoLocalization.localizedForKey("end_of_document_has_been_reached"));
		} catch (ResultNotFoundException e) {
			bottomPanel.setStatusText(FlexoLocalization.localizedForKey("no_result_found"));
		} finally {
			textComponent.addCaretListener(this);
		}
	}

	protected void updateResults() {
		queryResult = TextQueryEngine.performSearchOnDocument(textComponent.getDocument(), query);
		resultNavigator = queryResult.new ResultNavigator();
		updateButtons();
		updateResultNavigator(true);
		resultNeedsUpdate = false;
	}

	protected void updateQuery() {
		query.setCaseSensitive(optionPanel.caseSensitive.isSelected());
		query.setRegularExpression(optionPanel.regularExpression.isSelected());
		query.setSearchedText((String) findReplacePanel.findField.getSelectedItem());
		query.setWholeWord(optionPanel.wholeWord.isSelected());
		resultNeedsUpdate = true;
	}

	protected void updateResultNavigator(boolean notifyCaretPosition) {
		if (resultNavigator == null) {
			return;
		}
		resultNavigator.setDirection(directionPanel.forward.isSelected() ? Direction.FORWARD : Direction.BACKWARD);
		resultNavigator.setLimitToSelectedText(scopePanel.selectedText.isSelected());
		if (textComponent.getSelectionStart() > textComponent.getSelectionEnd()) {
			resultNavigator.setSelectionStart(textComponent.getSelectionEnd());
			resultNavigator.setSelectionEnd(textComponent.getSelectionStart());
		} else {
			resultNavigator.setSelectionStart(textComponent.getSelectionStart());
			resultNavigator.setSelectionEnd(textComponent.getSelectionEnd());
		}
		resultNavigator.setWrapSearch(optionPanel.wrapSearch.isSelected());
		if (notifyCaretPosition) {
			resultNavigator.setCurrentCaretPosition(textComponent.getCaretPosition());
		}
	}

	protected void updateButtons() {
		if (textComponent == null) {
			bottomPanel.find.setEnabled(false);
			bottomPanel.replace.setEnabled(false);
			bottomPanel.replaceFind.setEnabled(false);
			bottomPanel.replaceAll.setEnabled(false);
		} else {
			bottomPanel.find.setEnabled(getSearchedText().length() != 0);
			if (textComponent.isEditable()) {
				bottomPanel.replace.setEnabled(true && resultNavigator != null);
				bottomPanel.replaceFind.setEnabled(true && resultNavigator != null);
				bottomPanel.replaceAll.setEnabled(true);
			} else {
				bottomPanel.replace.setEnabled(false);
				bottomPanel.replaceFind.setEnabled(false);
				bottomPanel.replaceAll.setEnabled(false);
			}
		}
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		updateResultNavigator(true);
		updateScopePanel();
		if (textComponent.getSelectionStart() > -1 && textComponent.getSelectionEnd() > -1) {
			if (resultNavigator == null || resultNavigator.getCurrentResult() == null
					|| resultNavigator.getCurrentResult().startOffset != textComponent.getSelectionStart()
					|| resultNavigator.getCurrentResult().endOffset != textComponent.getSelectionEnd()) {
				updateResultNavigator(true);
			}
		}
	}

	private void updateScopePanel() {
		scopePanel.selectedText.setEnabled(textComponent.getSelectedText() != null && textComponent.getSelectedText().length() != 0);
		if (!scopePanel.selectedText.isEnabled()) {
			scopePanel.all.setSelected(true);
		}
	}

	public JButton getDefaultButton() {
		return bottomPanel.find;
	}

	public void setSearchedText(String selectedText) {
		findReplacePanel.findField.getEditor().setItem(selectedText);
	}

	protected String getReplacementText() {
		if (findReplacePanel.replaceField.getEditor().getItem() == null) {
			return "";
		}
		return (String) findReplacePanel.replaceField.getEditor().getItem();
	}

	protected String getSearchedText() {
		if (findReplacePanel.findField.getEditor().getItem() == null) {
			return "";
		}
		return (String) findReplacePanel.findField.getEditor().getItem();
	}

	public void onFocus() {
		findReplacePanel.findField.getEditor().selectAll();
	}
}
