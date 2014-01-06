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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.SelectionController;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.CustomPopup;

public abstract class AbstractSelectorPanel<T extends FlexoObject> extends CustomPopup.ResizablePanel {
	static final Logger logger = Logger.getLogger(AbstractSelectorPanel.class.getPackage().getName());

	private final AbstractSelectorPanelOwner<T> _owner;

	ProjectBrowser _browser;

	protected BrowserChooserView _browserView;
	private JList list;
	private JScrollPane scrollList;
	private JButton _applyButton;
	private JButton _cancelButton;
	private JButton _resetButton;
	protected CompletionListModel _completionListModel;
	private Vector<T> sortedObjectList;
	private Vector<String> sortedNameList;
	private JPanel _controlPanel;

	protected ListSelectionListener listSelectionListener;
	protected MouseAdapter listMouseListener;

	public static interface AbstractSelectorPanelOwner<T> {
		public FlexoProject getProject();

		public FlexoObject getRootObject();

		public FlexoEditor getEditor();

		public boolean isSelectable(FlexoObject object);

		public T getEditedObject();

		public void setEditedObject(T object);

		public void apply();

		public void cancel();

		public String renderedString(T editedObject);

		public JTextField getTextField(); // used to manage completion

		public boolean popupIsShown();

		public void openPopup();

		public void closePopup();

		public boolean isProgrammaticalySet();

		public void setProgrammaticalySet(boolean aFlag);

		public KeyAdapter getCompletionListKeyAdapter();

		public Integer getDefaultWidth();

		public Integer getDefaultHeight();
	}

	public AbstractSelectorPanel(AbstractSelectorPanelOwner<T> owner) {
		super();
		_owner = owner;
	}

	@Override
	public Dimension getDefaultSize() {
		Dimension returned = _browserView.getDefaultSize();
		returned.width = returned.width + 10;
		returned.height = returned.height + 40;
		if (_owner.getDefaultWidth() != null) {
			returned.width = _owner.getDefaultWidth();
		}
		if (_owner.getDefaultHeight() != null) {
			returned.height = _owner.getDefaultHeight();
		}
		return returned;
	}

	public FlexoObject getSelectedObject() {
		if (_browserView != null) {
			return _browserView.getSelectedObject();
		}
		return null;
	}

	protected abstract ProjectBrowser createBrowser(FlexoProject project);

	T _lastBrowsed = null;

	public void setRootObject(FlexoObject aRootObject) {
		if (_browser != null) {
			_browser.setRootObject(aRootObject);
		}
	}

	protected void init() {
		setLayout(new BorderLayout());

		_browser = createBrowser(_owner.getProject());
		_browser.setHandlesControlPanel(false);
		_browser.setRootObject(_owner.getRootObject());
		_browser.setSelectionController(new SelectionController() {
			@Override
			public boolean isSelectable(FlexoObject o) {
				return _owner.isSelectable(o);
			}
		});
		// TODO: grab a hand on the FlexoController here.
		_browserView = new BrowserChooserView(_browser, null, _owner) {
			@Override
			public void objectWasSelected(FlexoObject object) {
				_owner.setEditedObject((T) object);
			}

			@Override
			public void objectWasDefinitelySelected(FlexoObject object) {
				_owner.setEditedObject((T) object);
				_owner.apply();
			}
		};

		sortedObjectList = new Vector<T>();
		Iterator<FlexoObject> en = _browser.getAllObjects();
		while (en.hasNext()) {
			T o = (T) en.next();
			if (_owner.isSelectable(o)) {
				sortedObjectList.add(o);
			}
		}
		Collections.sort(sortedObjectList, new Comparator<T>() {
			private Collator collator = Collator.getInstance();

			@Override
			public int compare(T o1, T o2) {
				String s1 = _owner.renderedString(o1);
				String s2 = _owner.renderedString(o2);
				if (s1 != null && s2 != null) {
					return collator.compare(s1, s2);
				} else {
					return 0;
				}
			}
		});

		sortedNameList = new Vector<String>();
		for (T object : sortedObjectList) {
			sortedNameList.add(_owner.renderedString(object));
		}

		_completionListModel = new CompletionListModel(_owner.getTextField());
		_completionListModel.setData(sortedNameList);
		list = new JList(_completionListModel);

		list.addKeyListener(_owner.getCompletionListKeyAdapter());

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				T o = getCurrentSelectedInCompletionList();
				if (o != null) {
					if (_lastBrowsed != null && _browser != null) {
						_browser.collapse(_lastBrowsed);
					}
					if (_browser != null) {
						_browser.fireResetSelection();
						_browser.fireObjectSelected(o);
					}
					_lastBrowsed = o;
					// setEditedObject(o); /* ADDED on Apr 26th 2007, by SGU */
				}
			}
		};
		list.addListSelectionListener(listSelectionListener);
		listMouseListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					T o = getCurrentSelectedInCompletionList();
					if (o != null) {
						_owner.setEditedObject(o);
						_owner.apply();
					}
				}
			}
		};
		list.addMouseListener(listMouseListener);

		completionListIsShown = false;

		_controlPanel = new JPanel();
		_controlPanel.setOpaque(false);
		_controlPanel.setLayout(new FlowLayout());
		_controlPanel.add(_applyButton = new JButton(FlexoLocalization.localizedForKey("ok")));
		_controlPanel.add(_cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel")));
		_controlPanel.add(_resetButton = new JButton(FlexoLocalization.localizedForKey("reset")));
		_applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_lastBrowsed != null) {
					AbstractSelectorPanel.this._owner.setEditedObject(_lastBrowsed);
				}
				_owner.apply();
			}
		});
		_cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_owner.cancel();
			}
		});
		_resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractSelectorPanel.this._owner.setEditedObject(null);
				_owner.apply();
			}
		});
		add(_browserView, BorderLayout.CENTER);
		add(_controlPanel, BorderLayout.SOUTH);
		update();
	}

	// return true if an object was correctly selected, false otherwise
	protected boolean processEnterPressed() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Pressed on ENTER");
		}
		T o = getCurrentSelectedInCompletionList();
		if (o != null) {
			_owner.setEditedObject(o);
			_owner.apply();
			return true;
		}
		return false;
	}

	protected void processTabPressed() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Pressed on TAB, _completionListModel.size()=" + _completionListModel.getSize());
		}
		if (_completionListModel.getSize() == 1) {
			T o = getObjectInCompletionListAtIndex(0);
			if (o != null) {
				_owner.setEditedObject(o);
				_owner.apply();
			}
		} else if (_completionListModel.getSize() > 1) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Pressed on TAB, _completionListModel.size()=" + _completionListModel.getSize());
			}
			String commonPrefix = _completionListModel.getCommonPrefix();
			if (commonPrefix.equalsIgnoreCase(_owner.getTextField().getText())) {
				// Special case where text might be completed
				for (int i = 0; i < _completionListModel.getSize(); i++) {
					if (_completionListModel.getElementAt(i) != null) {
						if (_completionListModel.getElementAt(i).equals(_owner.getTextField().getText())) {
							T o = getObjectInCompletionListAtIndex(i);
							if (o != null) {
								_owner.setEditedObject(o);
								_owner.apply();
							}
						}
					}
				}
			} else { // Just complete
				_owner.setProgrammaticalySet(true);
				_owner.getTextField().setText(_completionListModel.getCommonPrefix());
				_owner.setProgrammaticalySet(false);
			}
		}
	}

	protected void processUpPressed() {
		int selectedIndex = list.getSelectedIndex();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("processUpPressed() selectedIndex=" + selectedIndex + " size=" + list.getModel().getSize());
		}
		if (selectedIndex > 0) {
			list.setSelectedIndex(selectedIndex - 1);
			list.ensureIndexIsVisible(selectedIndex - 1);
		} else if (selectedIndex == 0) {
			list.setSelectedIndex(list.getModel().getSize() - 1);
			list.ensureIndexIsVisible(list.getModel().getSize() - 1);
		} else if (selectedIndex == -1) {
			if (list.getModel().getSize() > 0) {
				list.setSelectedIndex(list.getModel().getSize() - 1);
				list.ensureIndexIsVisible(list.getModel().getSize() - 1);
			}
		}
		_owner.getTextField().requestFocusInWindow();
	}

	protected void processDownPressed() {
		int selectedIndex = list.getSelectedIndex();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("processDownPressed() selectedIndex=" + selectedIndex + " size=" + list.getModel().getSize());
		}
		if (selectedIndex < list.getModel().getSize() - 1) {
			list.setSelectedIndex(selectedIndex + 1);
			list.ensureIndexIsVisible(selectedIndex + 1);
		} else if (selectedIndex == list.getModel().getSize() - 1) {
			list.setSelectedIndex(0);
			list.ensureIndexIsVisible(0);
		} else if (selectedIndex == -1) {
			if (list.getModel().getSize() > 0) {
				list.setSelectedIndex(0);
				list.ensureIndexIsVisible(0);
			}
		}
		_owner.getTextField().requestFocusInWindow();
	}

	protected JPanel getControlPanel() {
		return _controlPanel;
	}

	protected T getCurrentSelectedInCompletionList() {
		if (list == null) {
			return null;
		}
		ListSelectionModel lsm = list.getSelectionModel();
		int selectedRow = lsm.getMinSelectionIndex();
		int selectedIndex = _completionListModel.getIndexOf(selectedRow);
		if (!lsm.isSelectionEmpty()) {
			if (selectedIndex >= 0 && selectedIndex < sortedObjectList.size()) {
				T returned = sortedObjectList.elementAt(selectedIndex);
				return returned;
			}
		}
		return null;
	}

	protected T getObjectInCompletionListAtIndex(int index) {
		int selectedIndex = _completionListModel.getIndexOf(index);
		if (selectedIndex >= 0 && selectedIndex < sortedObjectList.size()) {
			T returned = sortedObjectList.elementAt(selectedIndex);
			return returned;
		}
		return null;
	}

	private boolean completionListIsShown = false;

	private JComponent _completionList;

	void showCompletionList() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("showCompletionList(), _popupIsShown=" + _owner.popupIsShown());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("showCompletionList() " + _completionListModel.getSize());
		}
		if (!_owner.popupIsShown()) {
			_owner.openPopup();
		}
		if (!completionListIsShown) {
			_lastBrowsed = _owner.getEditedObject();
			if (_completionListModel.getSize() > 5) {
				scrollList = new JScrollPane(list);
				_completionList = scrollList;
				add(_completionList, BorderLayout.NORTH);
				list.setVisibleRowCount(5);
				scrollList.validate();
				scrollList.repaint();
			} else {
				_completionList = list;
				add(_completionList, BorderLayout.NORTH);
			}
			completionListIsShown = true;
			validate();
			repaint();
		} else {
			if (_completionList == list && _completionListModel.getSize() > 5 || _completionList == scrollList
					&& _completionListModel.getSize() <= 5) {
				T rememberMe = _lastBrowsed;
				hideCompletionList();
				showCompletionList();
				_lastBrowsed = rememberMe;
			}
		}
	}

	void hideCompletionList() {
		_lastBrowsed = null;
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("hideCompletionList()");
		}
		if (completionListIsShown) {
			remove(_completionList);
			completionListIsShown = false;
			validate();
			repaint();
		}
	}

	protected void update() {
		if (_browser != null) {
			_browser.fireResetSelection();
			if (_owner.getEditedObject() != null) {
				_browser.fireObjectSelected(_owner.getEditedObject());
			}
		}
	}

	protected void delete() {
		_completionListModel.delete();
		list.removeKeyListener(_owner.getCompletionListKeyAdapter());
		list.removeListSelectionListener(listSelectionListener);
		list.removeMouseListener(listMouseListener);
		_browser.delete();
		_browser = null;
		removeAll();
	}

	protected class CompletionListModel extends AbstractListModel implements DocumentListener {
		protected int min;
		protected int max;
		private Vector data;
		private JTextField tf;

		public CompletionListModel(JTextField textField) {
			super();
			tf = textField;
			min = 0;
			max = -1;
		}

		public void setData(Vector temp) {
			data = temp;
			min = 0;
			max = data.size() - 1;
			tf.getDocument().addDocumentListener(this);
		}

		public void delete() {
			if (tf != null) {
				tf.getDocument().removeDocumentListener(this);
			}
			data.clear();
			min = 0;
			max = -1;
		}

		@Override
		public int getSize() {
			return max - min + 1;
		}

		@Override
		public Object getElementAt(int row) {
			try {
				return data.get(min + row);
			} catch (Exception e) {
				return null;
			}
		}

		public int getIndexOf(int row) {
			return min + row;
		}

		public String getCommonPrefix() {
			StringBuffer returned = new StringBuffer();
			boolean stillMatches = true;
			int index = 0;
			String string1 = (String) data.get(min);
			String string2 = (String) data.get(max);
			while (stillMatches && index < string1.length() && index < string2.length()) {
				if (!string1.regionMatches(true, index, string2, index, 1)) {
					stillMatches = false;
				} else {
					if (index < tf.getText().length()) {
						returned.append(tf.getText().charAt(index));
					} else {
						returned.append(string1.charAt(index));
					}
					index++;
				}
			}
			return returned.toString();
		}

		private void refreshInterval() {
			String curText = tf.getText();
			int previousMin = min;
			int previousMax = max;
			min = 0;
			while (min < data.size() && !((String) data.get(min)).regionMatches(true, 0, curText, 0, curText.length())) {
				min++;
			}
			max = min;
			while (max < data.size() && ((String) data.get(max)).regionMatches(true, 0, curText, 0, curText.length())) {
				max++;
			}
			max--;
			if (previousMin < min) {
				fireIntervalRemoved(this, previousMin, min);
			} else if (previousMin > min) {
				fireIntervalAdded(this, min, previousMin);
			}
			if (previousMax < max) {
				fireIntervalAdded(this, previousMax, max);
			} else if (previousMax > max) {
				fireIntervalRemoved(this, max, previousMax);
			}
		}

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			textWasChanged();
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			textWasChanged();
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			textWasChanged();
		}

		protected void textWasChanged() {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("textWasChanged()");
			}
			refreshInterval();
			if (!AbstractSelectorPanel.this._owner.isProgrammaticalySet()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("showCompletionList()");
				}
				showCompletionList();
			}
		}
	}

	public ProjectBrowser getBrowser() {
		return _browser;
	}

	public void textWasChanged() {
		_completionListModel.textWasChanged();
	}

}
