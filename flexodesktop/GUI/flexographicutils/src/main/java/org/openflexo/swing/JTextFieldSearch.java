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
package org.openflexo.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/*
 * Created on Jun 10, 2005
 */

/**
 * @author bmangez
 * @version $Id: JTextFieldSearch.java,v 1.2 2011/09/12 11:47:23 gpolet Exp $ $Log: JTextFieldSearch.java,v $ Revision 1.2 2011/09/12
 *          11:47:23 gpolet Converted v2 to v3
 * 
 *          Revision 1.1 2011/08/03 10:51:18 sylvain Code moved from flexoutils to flexographicutils
 * 
 *          Revision 1.2 2011/06/21 14:46:34 gpolet MEDIUM: Added all missing @Override
 * 
 *          Revision 1.1 2011/05/24 01:12:47 gpolet LOW: First import of OpenFlexo
 * 
 *          Revision 1.1.2.2 2011/05/20 14:16:21 gpolet LOW: Added GPL v2 file header
 * 
 *          Revision 1.1.2.1 2011/05/19 09:39:54 gpolet refactored package names
 * 
 *          Revision 1.2 2006/02/02 15:51:30 bmangez merge from bdev
 * 
 *          Revision 1.1.2.2 2005/10/03 11:51:56 benoit *** empty log message *** Revision 1.1.2.1 2005/08/19 16:51:54 sguerin Commit on
 *          19/08/2005, Sylvain GUERIN, version 7.1.10.alpha See committing documentation
 * 
 *          Revision 1.1.2.1 2005/06/28 12:53:56 benoit ReusableComponents
 * 
 * 
 *          <B>Class Description</B><BR>
 *          Use this component to have a text field with completion assitant provided by a tooltip window.
 */
public class JTextFieldSearch extends JTextField {
	TextSearchWindow myWindow;

	/**
     * 
     */
	public JTextFieldSearch(JFrame parentFrame, Vector sortedList) {
		super();
		init(parentFrame, sortedList);
	}

	/**
	 * @param arg0
	 */
	public JTextFieldSearch(String arg0, JFrame parentFrame, Vector sortedList) {
		super(arg0);
		init(parentFrame, sortedList);
	}

	/**
	 * @param arg0
	 */
	public JTextFieldSearch(int arg0, JFrame parentFrame, Vector sortedList) {
		super(arg0);
		init(parentFrame, sortedList);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public JTextFieldSearch(String arg0, int arg1, JFrame parentFrame, Vector sortedList) {
		super(arg0, arg1);
		init(parentFrame, sortedList);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public JTextFieldSearch(Document arg0, String arg1, int arg2, JFrame parentFrame, Vector sortedList) {
		super(arg0, arg1, arg2);
		init(parentFrame, sortedList);
	}

	private void init(JFrame parentFrame, Vector sortedList) {
		myWindow = new TextSearchWindow(parentFrame, this, sortedList);
		addKeyListener(new java.awt.event.KeyAdapter() {

			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				if (evt.getKeyCode() != KeyEvent.VK_ENTER) {
					java.awt.Point myPoint = getLocationOnScreen();

					int x = myPoint.x;
					int y = myPoint.y;

					myWindow.setLocation(x, y + getHeight());
					myWindow.showWindow();
				}
			}
		});
	}

	class TextSearchWindow extends JWindow {

		protected JList list;

		JFrame itsParent;

		JTextField theTextField;

		public TextSearchWindow(JFrame theParentFrame, JTextField textField, Vector sortedList) {
			super(theParentFrame);
			itsParent = theParentFrame;
			theTextField = textField;

			MyListModel model = new MyListModel(textField);
			model.setData(sortedList);
			list = new JList(model);

			this.addFocusListener(new java.awt.event.FocusAdapter() {
				@Override
				public void focusGained(java.awt.event.FocusEvent evt) {
					TextSearchWindow.this.transferFocus();
					itsParent.requestFocus();
					theTextField.requestFocus();
				}

			});

			list.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						ListSelectionModel lsm = list.getSelectionModel();
						if (!lsm.isSelectionEmpty()) {
							int selectedRow = lsm.getMinSelectionIndex();
							theTextField.setText(list.getModel().getElementAt(selectedRow).toString());
							dispose();
						}
					} else if (e.getKeyCode() == KeyEvent.VK_TAB) {
						dispose();
					} else if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_SHIFT
							&& e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_LEFT
							&& e.getKeyCode() != KeyEvent.VK_RIGHT) {
						String curText = theTextField.getText();
						if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
							try {
								curText = curText.substring(0, curText.length() - 1);
							} catch (StringIndexOutOfBoundsException e1) {
								curText = "";
							}
						} else {
							curText = curText + new Character(e.getKeyChar());
						}
						theTextField.setText(curText);

					}

				}
			});
			this.getContentPane().add(list);
			this.pack();
		}

		// display this value in the textfield.
		public String getText() {
			return (String) list.getModel().getElementAt(list.getSelectedIndex());
		}

		@Override
		public void setVisible(boolean isVisible) {
			pack();
			super.setVisible(isVisible);
		}

		public void showWindow() {
			pack();
			setVisible(true);
		}

		public void hideWindow() {
			setVisible(false);
		}

		class MyListModel extends AbstractListModel implements DocumentListener {
			int min;

			int max;

			Vector data;

			JTextField tf;

			public MyListModel(JTextField textField) {
				super();
				tf = textField;
			}

			public void setData(Vector temp) {
				data = temp;
				min = 0;
				max = data.size() - 1;
				tf.getDocument().addDocumentListener(this);
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

			private void refreshInterval() {
				dispose();
				String curText = tf.getText();
				int previousMin = min;
				int previousMax = max;
				min = 0;
				while (min < data.size() && !((String) data.get(min)).startsWith(curText)) {
					min++;
				}
				max = min;
				while (max < data.size() && ((String) data.get(max)).startsWith(curText)) {
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
				// fireTableDataChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				refreshInterval();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				refreshInterval();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				refreshInterval();
			}
		}

	}// End of program
}
