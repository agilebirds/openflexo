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
package org.openflexo.swing.merge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openflexo.diff.merge.DetailedMerge;
import org.openflexo.diff.merge.IMerge;
import org.openflexo.diff.merge.MergeChange;
import org.openflexo.diff.merge.MergeChange.MergeChangeAction;
import org.openflexo.diff.merge.MergeChange.MergeChangeSource;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.toolbox.TokenMarkerStyle;

public class MergePanelElements implements Observer {

	IMerge _merge;
	MergeTextArea _leftTextArea;
	MergeTextArea _rightTextArea;
	FilterChangeList _changeListPanel;
	private ListSelectionListener lsl;

	ComparePanel comparePanel;
	// JList changesList;
	JScrollPane _mergePanel;

	TokenMarkerStyle style;

	public MergePanelElements(IMerge merge) {
		this(merge, null, null);
	}

	public MergePanelElements(IMerge merge, TokenMarkerStyle style) {
		this(merge, style, null);
	}

	public MergePanelElements(IMerge merge, TokenMarkerStyle style, String noChangeLabel) {
		super();
		_merge = merge;

		_merge.addObserver(this);

		this.style = style;

		_changeListPanel = new FilterChangeList(noChangeLabel);
		// if (_merge.getChanges().size() > 0) {
		// changesList = new JList(_merge.getChanges());
		// changesList.setVisibleRowCount(5);
		// changesList.setCellRenderer(new DefaultListCellRenderer(){
		// public Component getListCellRendererComponent(
		// JList list,
		// Object value,
		// int index,
		// boolean isSelected,
		// boolean cellHasFocus)
		// {
		// JLabel returned = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		// returned.setIcon(iconForChange((MergeChange)value));
		// returned.setText(((MergeChange)value).toString());
		// return returned;
		// }
		// });
		// changesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// lsl = new ListSelectionListener() {
		// public void valueChanged(ListSelectionEvent e) {
		// selectChange((MergeChange)changesList.getSelectedValue());
		// }
		// };
		// changesList.addListSelectionListener(lsl);
		// changesList.addMouseListener(new MouseAdapter() {
		// public void mousePressed(MouseEvent mouseEvent)
		// {
		// if (mouseEvent.isPopupTrigger()
		// && changesList.getSelectedValue() != null
		// && changesList.getSelectedValue() instanceof MergeChange
		// && ((MergeChange)changesList.getSelectedValue()).getMergeChangeSource() == MergeChangeSource.Conflict)
		// comparePanel._buttonForChanges.get(changesList.getSelectedValue()).getPopupMenu().show(mouseEvent.getComponent(),
		// mouseEvent.getX()+10, mouseEvent.getY());
		// }
		// public void mouseReleased(MouseEvent mouseEvent)
		// {
		// if (mouseEvent.isPopupTrigger()
		// && changesList.getSelectedValue() != null
		// && changesList.getSelectedValue() instanceof MergeChange
		// && ((MergeChange)changesList.getSelectedValue()).getMergeChangeSource() == MergeChangeSource.Conflict)
		// comparePanel._buttonForChanges.get(changesList.getSelectedValue()).getPopupMenu().show(mouseEvent.getComponent(),
		// mouseEvent.getX()+10, mouseEvent.getY());
		// }
		//
		// });
		//
		// }
		// else {
		// Object[] obj = {noChangeLabel};
		// changesList = new JList(obj);
		// changesList.setEnabled(false);
		// }

		_leftTextArea = new MergeTextArea(merge.getLeftSource(), merge, MergeTextArea.Side.Left, style);
		_rightTextArea = new MergeTextArea(merge.getRightSource(), merge, MergeTextArea.Side.Right, style);

		_mergeTextArea = new MergeTextArea(merge.getMergedSource(), merge, MergeTextArea.Side.Merge, style);
		JPanel _insideMergePanel = new JPanel(new BorderLayout());
		_insideMergePanel.add(_mergeTextArea.getLinesTA(), BorderLayout.WEST);
		_insideMergePanel.add(_mergeTextArea.getPainter(), BorderLayout.CENTER);
		_mergePanel = new JScrollPane(_insideMergePanel);
		_mergeTextArea.validate();

		comparePanel = new ComparePanel();

		comparePanel._scrollPane.getVerticalScrollBar().addAdjustmentListener(mergePanelSBAdjustementListener = new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				// (new Exception()).printStackTrace();
				_mergePanel.getVerticalScrollBar().removeAdjustmentListener(comparePanelSBAdjustementListener);
				_mergePanel.getVerticalScrollBar().setValue(e.getValue());
				_mergePanel.getVerticalScrollBar().addAdjustmentListener(comparePanelSBAdjustementListener);
			}
		});
		_mergePanel.getVerticalScrollBar().addAdjustmentListener(comparePanelSBAdjustementListener = new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				// (new Exception()).printStackTrace();
				comparePanel._scrollPane.getVerticalScrollBar().removeAdjustmentListener(mergePanelSBAdjustementListener);
				comparePanel._scrollPane.getVerticalScrollBar().setValue(e.getValue());
				comparePanel._scrollPane.getVerticalScrollBar().addAdjustmentListener(mergePanelSBAdjustementListener);
			}
		});

		_mergePanel.getVerticalScrollBar().setUnitIncrement(10);
		_mergePanel.getVerticalScrollBar().setBlockIncrement(50);
		comparePanel._scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		comparePanel._scrollPane.getVerticalScrollBar().setBlockIncrement(50);

		_controlPanel = new JPanel(new FlowLayout());
		/*_controlPanel.add(_chooseButton = new JButton("Choose"));
		_chooseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getSelectedMergeChange() != null) {
					getSelectedMergeChange().setMergeChangeAction(MergeChangeAction.Choose);
					updateAfterMergeChangeActionChanging();
				}
			}
		});*/
		_controlPanel.add(_chooseButton = new JButton());

		/*_controlPanel.add(_ignoreButton = new JButton("Ignore"));
		_ignoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getSelectedMergeChange() != null) {
					getSelectedMergeChange().setMergeChangeAction(MergeChangeAction.Ignore);
					updateAfterMergeChangeActionChanging();
				}
			}
		});*/
		_controlPanel.add(_ignoreButton = new JButton());

		/*_controlPanel.add(_chooseLeftButton = new JButton("Choose left"));
		_chooseLeftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getSelectedMergeChange() != null) {
					getSelectedMergeChange().setMergeChangeAction(MergeChangeAction.ChooseLeft);
					updateAfterMergeChangeActionChanging();
				}
			}
		});*/
		_controlPanel.add(_chooseLeftButton = new JButton());

		/*_controlPanel.add(_chooseRightButton = new JButton("Choose right"));
		_chooseRightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getSelectedMergeChange() != null) {
					getSelectedMergeChange().setMergeChangeAction(MergeChangeAction.ChooseRight);
					updateAfterMergeChangeActionChanging();
				}
			}
		});*/
		_controlPanel.add(_chooseRightButton = new JButton());

		/*_controlPanel.add(_chooseNoneButton = new JButton("Choose none"));
		_chooseNoneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getSelectedMergeChange() != null) {
					getSelectedMergeChange().setMergeChangeAction(MergeChangeAction.ChooseNone);
					updateAfterMergeChangeActionChanging();
				}
			}
		});*/
		_controlPanel.add(_chooseNoneButton = new JButton());

		/*_controlPanel.add(_chooseBothLeftFirstButton = new JButton("Choose both left first"));
		_chooseBothLeftFirstButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getSelectedMergeChange() != null) {
					getSelectedMergeChange().setMergeChangeAction(MergeChangeAction.ChooseBothLeftFirst);
					updateAfterMergeChangeActionChanging();
				}
			}
		});*/
		_controlPanel.add(_chooseBothLeftFirstButton = new JButton());

		/*_controlPanel.add(_chooseBothRightFirstButton = new JButton("Choose both right first"));
		_chooseBothRightFirstButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getSelectedMergeChange() != null) {
					getSelectedMergeChange().setMergeChangeAction(MergeChangeAction.ChooseBothRightFirst);
					updateAfterMergeChangeActionChanging();
				}
			}
		});*/
		_controlPanel.add(_chooseBothRightFirstButton = new JButton());

		/*_controlPanel.add(_customEditingButton = new JButton("Choose both right first"));
		_customEditingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getSelectedMergeChange() != null) {
					getSelectedMergeChange().setMergeChangeAction(MergeChangeAction.ChooseBothRightFirst);
					updateAfterMergeChangeActionChanging();
				}
			}
		});*/
		_controlPanel.add(_customEditingButton = new JButton());

		updateControlPanel();

		comparePanel.validate();
		_controlPanel.validate();
		_mergePanel.validate();

	}

	void updateAfterMergeChangeActionChanging() {
		// Ensure merged source has been computed
		_merge.getMergedSource();
		_mergeTextArea.update();
		_mergePanel.revalidate();
		_mergePanel.repaint();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				selectChange(getSelectedMergeChange());
			}
		});
	}

	private JPanel _controlPanel;
	private JButton _chooseButton;
	private JButton _ignoreButton;
	private JButton _chooseLeftButton;
	private JButton _chooseRightButton;
	private JButton _chooseNoneButton;
	private JButton _chooseBothLeftFirstButton;
	private JButton _chooseBothRightFirstButton;
	private JButton _customEditingButton;

	private MergeTextArea _mergeTextArea;

	AdjustmentListener mergePanelSBAdjustementListener;
	AdjustmentListener comparePanelSBAdjustementListener;

	public class ComparePanel extends JPanel {

		JScrollPane _scrollPane;
		private JComponent _separator = null;
		AdjustmentListener leftSBAdjustementListener;
		AdjustmentListener rightSBAdjustementListener;
		private JPanel centerPane;
		private GridBagLayout gridbag;
		Hashtable<MergeChange, MergeChangeButton> _buttonForChanges;

		ComparePanel() {
			super(new BorderLayout());

			_buttonForChanges = new Hashtable<MergeChange, MergeChangeButton>();

			JComponent separator = getSeparator();

			JComponent left = _leftTextArea;
			JComponent right = _rightTextArea;

			gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			centerPane = new JPanel();
			centerPane.setLayout(gridbag);

			// c.gridwidth = 1;
			// c.gridheight = 1;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.fill = GridBagConstraints.NONE;
			// c.gridwidth = 1;
			gridbag.setConstraints(_leftTextArea.getLinesTA(), c);
			centerPane.add(_leftTextArea.getLinesTA());

			c.weightx = 1.0;
			c.weighty = 1.0;
			// c.gridwidth = 1;
			// c.gridheight = 1;
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.BOTH;
			// c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(left, c);
			centerPane.add(left);

			// c.gridwidth = 1;
			// c.gridheight = 1;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.VERTICAL;
			// c.gridwidth = 1;
			gridbag.setConstraints(separator, c);
			centerPane.add(separator);

			c.weightx = 1.0;
			c.weighty = 1.0;
			// c.gridwidth = 1;
			// c.gridheight = 1;
			c.anchor = GridBagConstraints.EAST;
			c.fill = GridBagConstraints.BOTH;
			// c.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints(right, c);
			centerPane.add(right);

			// c.gridwidth = 1;
			// c.gridheight = 1;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.NORTHEAST;
			c.fill = GridBagConstraints.NONE;
			// c.gridwidth = 1;
			gridbag.setConstraints(_rightTextArea.getLinesTA(), c);
			centerPane.add(_rightTextArea.getLinesTA());

			add(_scrollPane = new JScrollPane(centerPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
			JPanel scrollBars = new JPanel(new GridLayout(1, 2));
			scrollBars.add(_leftTextArea.getHorizontalScrollBar());
			scrollBars.add(_rightTextArea.getHorizontalScrollBar());
			scrollBars.validate();
			add(scrollBars, BorderLayout.SOUTH);
			validate();
			_leftTextArea.getHorizontalScrollBar().addAdjustmentListener(leftSBAdjustementListener = new AdjustmentListener() {
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e) {
					_rightTextArea.getHorizontalScrollBar().removeAdjustmentListener(rightSBAdjustementListener);
					_rightTextArea.getHorizontalScrollBar().setValue(e.getValue());
					_rightTextArea.getHorizontalScrollBar().addAdjustmentListener(rightSBAdjustementListener);
				}
			});
			_rightTextArea.getHorizontalScrollBar().addAdjustmentListener(rightSBAdjustementListener = new AdjustmentListener() {
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e) {
					_leftTextArea.getHorizontalScrollBar().removeAdjustmentListener(leftSBAdjustementListener);
					_leftTextArea.getHorizontalScrollBar().setValue(e.getValue());
					_leftTextArea.getHorizontalScrollBar().addAdjustmentListener(leftSBAdjustementListener);
				}
			});

			if (_merge.getChanges().size() > 0) {
				selectChange(_merge.getChanges().firstElement());
			}
		}

		void selectChange(MergeChange change) {
			_leftTextArea.setChange(change, true);
			_rightTextArea.setChange(change, true);
		}

		void update() {
			getSeparator().removeAll();
			buttonsInitialized = false;
			revalidate();
			repaint();
		}

		private JComponent getSeparator() {
			if (_separator == null) {
				_separator = new JPanel();
				_separator.setLayout(new BoxLayout(_separator, BoxLayout.Y_AXIS));
				_separator.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			}
			return _separator;
		}

		protected boolean buttonsInitialized = false;

		private class SelectChange implements ActionListener {
			private MergeChange change;

			SelectChange(MergeChange change) {
				this.change = change;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				MergePanelElements.this.selectChange(change);
			}
		}

		private void updateChangeButtonLocations() {
			if (!buttonsInitialized) {
				if (_leftTextArea.readyToDisplay()) {
					float remainderHeight = _leftTextArea.getPreferredSize().height;
					for (MergeChange c : _merge.getChanges()) {
						JButton button = new MergeChangeButton(c);
						float height = _leftTextArea.heightAboveChange(c, button.getPreferredSize().height);
						_separator.add(Box.createRigidArea(new Dimension(0, (int) height)));
						_separator.add(button);
						remainderHeight -= button.getPreferredSize().height;
						remainderHeight -= height;
					}
					_separator.add(Box.createRigidArea(new Dimension(0, (int) remainderHeight)));
					validate();
					repaint();
					buttonsInitialized = true;
				} else {
					// Not ready
				}
			}
		}

		protected class MergeChangeButton extends JButton {
			private JPopupMenu popupMenu = null;
			MergeChange change;
			LineBorder selectedBorder;

			protected MergeChangeButton(MergeChange c) {
				super(iconForChange(c));
				change = c;
				_buttonForChanges.put(change, this);
				addActionListener(new SelectChange(c));
				setBorder(BorderFactory.createEmptyBorder(1, 1, 1, _leftTextArea.getVerticalScrollBar().getPreferredSize().width));
				addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						setBorder(selectedBorder);
						// setBorder(BorderFactory.createLineBorder(Color.RED));
					}

					@Override
					public void mouseExited(MouseEvent e) {
						setBorder(BorderFactory.createEmptyBorder(1, 1, 1, _leftTextArea.getVerticalScrollBar().getPreferredSize().width));
					}

					@Override
					public void mousePressed(MouseEvent mouseEvent) {
						if (mouseEvent.isPopupTrigger()) {
							getPopupMenu().show(mouseEvent.getComponent(), mouseEvent.getX() + 10, mouseEvent.getY());
						}
					}

					@Override
					public void mouseReleased(MouseEvent mouseEvent) {
						if (mouseEvent.isPopupTrigger()) {
							getPopupMenu().show(mouseEvent.getComponent(), mouseEvent.getX() + 10, mouseEvent.getY());
						}
					}

				});
				selectedBorder = new LineBorder(Color.RED, 1) {
					@Override
					public Insets getBorderInsets(Component c) {
						Insets returned = super.getBorderInsets(c);
						returned.right = _leftTextArea.getVerticalScrollBar().getPreferredSize().width;
						return returned;
					}

					@Override
					public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
						Color oldColor = g.getColor();
						g.setColor(lineColor);
						g.drawRect(x, y, width - _leftTextArea.getVerticalScrollBar().getPreferredSize().width, height - 1);
						g.setColor(oldColor);
					}
				};
				if ((change.getMergeChangeAction() == MergeChangeAction.AutomaticMergeResolving) && change.isResolved()) {
					setToolTipText(change.getAutomaticMergeReason());
				}
			}

			JPopupMenu getPopupMenu() {
				if (popupMenu == null) {
					popupMenu = new JPopupMenu() {
						@Override
						public void show(Component invoker, int x, int y) {
							super.show(invoker, x, y);
							for (int i = 0; i < getComponentCount(); i++) {
								if (getComponent(i) instanceof ChangeActionCheckBoxMenuItem) {
									ChangeActionCheckBoxMenuItem item = (ChangeActionCheckBoxMenuItem) getComponent(i);
									item.updateState();
								}
							}
						}
					};
					if ((change.getMergeChangeSource() == MergeChangeSource.Left)
							|| (change.getMergeChangeSource() == MergeChangeSource.Right)) {
						popupMenu.add(new ChangeActionCheckBoxMenuItem(getChangeAction(change, MergeChangeAction.KeepChange)));
						popupMenu.add(new ChangeActionCheckBoxMenuItem(getChangeAction(change, MergeChangeAction.IgnoreChange)));
					} else if (change.getMergeChangeSource() == MergeChangeSource.Conflict) {
						popupMenu.add(new ChangeActionCheckBoxMenuItem(getChangeAction(change, MergeChangeAction.AutomaticMergeResolving)));
						popupMenu.add(new ChangeActionCheckBoxMenuItem(getChangeAction(change, MergeChangeAction.ChooseLeft)));
						popupMenu.add(new ChangeActionCheckBoxMenuItem(getChangeAction(change, MergeChangeAction.ChooseRight)));
						popupMenu.add(new ChangeActionCheckBoxMenuItem(getChangeAction(change, MergeChangeAction.ChooseNone)));
						popupMenu.add(new ChangeActionCheckBoxMenuItem(getChangeAction(change, MergeChangeAction.ChooseBothLeftFirst)));
						popupMenu.add(new ChangeActionCheckBoxMenuItem(getChangeAction(change, MergeChangeAction.ChooseBothRightFirst)));
						popupMenu.add(new ChangeActionCheckBoxMenuItem(getChangeAction(change, MergeChangeAction.CustomEditing)));
						popupMenu.add(new ChangeActionCheckBoxMenuItem(getChangeAction(change, MergeChangeAction.Undecided)));
					}

					if (!(change.getMerge() instanceof DetailedMerge)) {
						JMenuItem showDetailedAnalysis = new JMenuItem(localizedForKey("detailed_analysis"));
						showDetailedAnalysis.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								showDetailedMerge(change);
							}
						});
						popupMenu.addSeparator();
						popupMenu.add(showDetailedAnalysis);
					}
				}
				return popupMenu;
			}
		}

		void showDetailedMerge(MergeChange mergeChange) {
			new DetailedMergeAnalysisPanel(mergeChange.getDetailedMerge()) {
				@Override
				protected String localizedForKey(String key) {
					return MergePanelElements.this.localizedForKey(key);
				}

				@Override
				protected void validateChanges() {
					super.validateChanges();
					updateAfterMergeChangeActionChanging();
				}
			};
		}

		private class ChangeActionCheckBoxMenuItem extends JCheckBoxMenuItem {
			ChangeActionCheckBoxMenuItem(ChangeAction action) {
				super(action);
				updateState();
			}

			@Override
			public ChangeAction getAction() {
				return (ChangeAction) super.getAction();
			}

			@Override
			public boolean isSelected() {
				if (getAction() != null) {
					return getAction().mergeChange.getMergeChangeAction() == getAction().mergeChangeAction;
				}
				return super.isSelected();
			}

			protected void updateState() {
				setState(getState());
			}
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			updateChangeButtonLocations();
		}
	}

	public void selectChange(MergeChange change) {
		if ((MergeChange) getFilteredChangeList().getSelectedValue() != change) {
			getFilteredChangeList().removeListSelectionListener(lsl);
			getFilteredChangeList().setSelectedValue(change, true);
			getFilteredChangeList().addListSelectionListener(lsl);
		}
		comparePanel.selectChange(change);
		_mergeTextArea.setChange(change, true);
		updateControlPanel();
	}

	MergeChange getSelectedMergeChange() {
		return (MergeChange) getFilteredChangeList().getSelectedValue();
	}

	protected void updateControlPanel() {
		_controlPanel.remove(_chooseButton);
		_controlPanel.remove(_ignoreButton);
		_controlPanel.remove(_chooseLeftButton);
		_controlPanel.remove(_chooseRightButton);
		_controlPanel.remove(_chooseNoneButton);
		_controlPanel.remove(_chooseBothRightFirstButton);
		_controlPanel.remove(_chooseBothLeftFirstButton);
		_controlPanel.remove(_customEditingButton);
		if (getSelectedMergeChange() != null && getSelectedMergeChange().getMergeChangeSource() != MergeChangeSource.Conflict) {
			_chooseButton.setAction(getChangeAction(getSelectedMergeChange(), MergeChangeAction.KeepChange));
			_ignoreButton.setAction(getChangeAction(getSelectedMergeChange(), MergeChangeAction.IgnoreChange));
			_controlPanel.add(_ignoreButton, 0);
			_controlPanel.add(_chooseButton, 0);
		} else if (getSelectedMergeChange() != null && getSelectedMergeChange().getMergeChangeSource() == MergeChangeSource.Conflict) {
			_chooseLeftButton.setAction(getChangeAction(getSelectedMergeChange(), MergeChangeAction.ChooseLeft));
			_chooseRightButton.setAction(getChangeAction(getSelectedMergeChange(), MergeChangeAction.ChooseRight));
			_chooseNoneButton.setAction(getChangeAction(getSelectedMergeChange(), MergeChangeAction.ChooseNone));
			_chooseBothLeftFirstButton.setAction(getChangeAction(getSelectedMergeChange(), MergeChangeAction.ChooseBothLeftFirst));
			_chooseBothRightFirstButton.setAction(getChangeAction(getSelectedMergeChange(), MergeChangeAction.ChooseBothRightFirst));
			_customEditingButton.setAction(getChangeAction(getSelectedMergeChange(), MergeChangeAction.CustomEditing));
			_controlPanel.add(_customEditingButton, 0);
			_controlPanel.add(_chooseBothRightFirstButton, 0);
			_controlPanel.add(_chooseBothLeftFirstButton, 0);
			_controlPanel.add(_chooseNoneButton, 0);
			_controlPanel.add(_chooseRightButton, 0);
			_controlPanel.add(_chooseLeftButton, 0);
		}
		_controlPanel.validate();
		_controlPanel.repaint();

		/*_chooseButton.setEnabled(getSelectedMergeChange() != null && getSelectedMergeChange().getMergeChangeSource() != MergeChangeSource.Conflict);
		_ignoreButton.setEnabled(getSelectedMergeChange() != null && getSelectedMergeChange().getMergeChangeSource() != MergeChangeSource.Conflict);
		_chooseLeftButton.setEnabled(getSelectedMergeChange() != null && getSelectedMergeChange().getMergeChangeSource() == MergeChangeSource.Conflict);
		_chooseRightButton.setEnabled(getSelectedMergeChange() != null && getSelectedMergeChange().getMergeChangeSource() == MergeChangeSource.Conflict);
		_chooseNoneButton.setEnabled(getSelectedMergeChange() != null && getSelectedMergeChange().getMergeChangeSource() == MergeChangeSource.Conflict);
		_chooseBothButton.setEnabled(getSelectedMergeChange() != null && getSelectedMergeChange().getMergeChangeSource() == MergeChangeSource.Conflict);*/
	}

	public FilterChangeList getChangesList() {
		return _changeListPanel;
	}

	public ComparePanel getComparePanel() {
		return comparePanel;
	}

	public JScrollPane getMergePanel() {
		return _mergePanel;
	}

	static Icon iconForChangeCategory(MergeChange.ChangeCategory cat) {
		switch (cat) {
		case LEFT_ADDITION:
			return UtilsIconLibrary.LEFT_ADDITION_ICON;
		case LEFT_MODIFICATION:
			return UtilsIconLibrary.LEFT_MODIFICATION_ICON;
		case LEFT_REMOVAL:
			return UtilsIconLibrary.LEFT_REMOVAL_ICON;
		case SMART_CONFLICT_RESOLVED:
			return UtilsIconLibrary.SMART_CONFLICT_RESOLVED_ICON;
		case SMART_CONFLICT_UNRESOLVED:
			return UtilsIconLibrary.SMART_CONFLICT_UNRESOLVED_ICON;
		case CUSTOM_EDITING_RESOLVED:
			return UtilsIconLibrary.CUSTOM_EDITING_RESOLVED_ICON;
		case CUSTOM_EDITING_UNRESOLVED:
			return UtilsIconLibrary.CUSTOM_EDITING_UNRESOLVED_ICON;
		case ADD_CONFLICT_RESOLVED:
			return UtilsIconLibrary.ADD_CONFLICT_RESOLVED_ICON;
		case ADD_CONFLICT_UNRESOLVED:
			return UtilsIconLibrary.ADD_CONFLICT_UNRESOLVED_ICON;
		case CONFLICT_RESOLVED:
			return UtilsIconLibrary.CONFLICT_RESOLVED_ICON;
		case CONFLICT_UNRESOLVED:
			return UtilsIconLibrary.CONFLICT_UNRESOLVED_ICON;
		case DEL_CONFLICT_RESOLVED:
			return UtilsIconLibrary.DEL_CONFLICT_RESOLVED_ICON;
		case DEL_CONFLICT_UNRESOLVED:
			return UtilsIconLibrary.DEL_CONFLICT_UNRESOLVED_ICON;
		case RIGHT_ADDITION:
			return UtilsIconLibrary.RIGHT_ADDITION_ICON;
		case RIGHT_MODIFICATION:
			return UtilsIconLibrary.RIGHT_MODIFICATION_ICON;
		case RIGHT_REMOVAL:
			return UtilsIconLibrary.RIGHT_REMOVAL_ICON;
		}
		return null;
	}

	static Icon iconForChange(MergeChange value) {
		return iconForChangeCategory(value.category());
		// if (value.getMergeChangeSource() == MergeChange.MergeChangeSource.Left) {
		// if (value.getMergeChangeType() == MergeChange.MergeChangeType.Addition) {
		// return LEFT_ADDITION_ICON;
		// }
		// else if (value.getMergeChangeType() == MergeChange.MergeChangeType.Modification) {
		// return LEFT_MODIFICATION_ICON;
		// }
		// else if (value.getMergeChangeType() == MergeChange.MergeChangeType.Removal) {
		// return LEFT_REMOVAL_ICON;
		// }
		// }
		// else if (value.getMergeChangeSource() == MergeChange.MergeChangeSource.Conflict) {
		// if (value.getMergeChangeAction() == MergeChangeAction.AutomaticMergeResolving) {
		// if (value.isResolved()) return SMART_CONFLICT_RESOLVED_ICON;
		// else return SMART_CONFLICT_UNRESOLVED_ICON;
		// }
		// else if (value.getMergeChangeAction() == MergeChangeAction.CustomEditing) {
		// if (value.isResolved()) return CUSTOM_EDITING_RESOLVED_ICON;
		// else return CUSTOM_EDITING_UNRESOLVED_ICON;
		// }
		// if (value.getMergeChangeType() == MergeChange.MergeChangeType.Addition) {
		// if (value.isResolved()) return ADD_CONFLICT_RESOLVED_ICON;
		// else return ADD_CONFLICT_UNRESOLVED_ICON;
		// }
		// else if (value.getMergeChangeType() == MergeChange.MergeChangeType.Modification) {
		// if (value.isResolved()) return CONFLICT_RESOLVED_ICON;
		// else return CONFLICT_UNRESOLVED_ICON;
		// }
		// else if (value.getMergeChangeType() == MergeChange.MergeChangeType.Removal) {
		// if (value.isResolved()) return DEL_CONFLICT_RESOLVED_ICON;
		// else return DEL_CONFLICT_UNRESOLVED_ICON;
		// }
		// }
		// else if (value.getMergeChangeSource() == MergeChange.MergeChangeSource.Right) {
		// if (value.getMergeChangeType() == MergeChange.MergeChangeType.Addition) {
		// return RIGHT_ADDITION_ICON;
		// }
		// else if (value.getMergeChangeType() == MergeChange.MergeChangeType.Modification) {
		// return RIGHT_MODIFICATION_ICON;
		// }
		// else if (value.getMergeChangeType() == MergeChange.MergeChangeType.Removal) {
		// return RIGHT_REMOVAL_ICON;
		// }
		// }
	}

	public IMerge getMerge() {
		return _merge;
	}

	public JPanel getControlPanel() {
		return _controlPanel;
	}

	private Hashtable<MergeChange, Hashtable<MergeChangeAction, ChangeAction>> storedActions = new Hashtable<MergeChange, Hashtable<MergeChangeAction, ChangeAction>>();

	protected ChangeAction getChangeAction(MergeChange mergeChange, MergeChangeAction mergeChangeAction) {
		if (storedActions.get(mergeChange) == null) {
			storedActions.put(mergeChange, new Hashtable<MergeChangeAction, ChangeAction>());
		}
		if (storedActions.get(mergeChange).get(mergeChangeAction) == null) {
			storedActions.get(mergeChange).put(mergeChangeAction, new ChangeAction(mergeChange, mergeChangeAction));
		}
		return storedActions.get(mergeChange).get(mergeChangeAction);
	}

	public class ChangeAction extends AbstractAction {
		MergeChange mergeChange;
		MergeChangeAction mergeChangeAction;

		public ChangeAction(MergeChange mergeChange, MergeChangeAction mergeChangeAction) {
			super(localizedNameForMergeChangeAction(mergeChangeAction), iconForMergeChangeAction(mergeChangeAction));
			this.mergeChange = mergeChange;
			this.mergeChangeAction = mergeChangeAction;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (mergeChangeAction == MergeChangeAction.CustomEditing) {
				new HandConflictResolver(mergeChange, style, mergeChange.getMergeChangeAction(), MergePanelElements.this);
			} else {
				mergeChange.setMergeChangeAction(mergeChangeAction);
			}
			updateAfterMergeChangeActionChanging();
		}

		@Override
		public boolean isEnabled() {
			return isEditable();
		}

	}

	protected String localizedForKey(String key) {
		return key;
	}

	String localizedNameForMergeChangeAction(MergeChangeAction mergeChangeAction) {
		if (mergeChangeAction == MergeChangeAction.KeepChange) {
			return localizedForKey("keep_change");
		} else if (mergeChangeAction == MergeChangeAction.IgnoreChange) {
			return localizedForKey("ignore_change");
		} else if (mergeChangeAction == MergeChangeAction.ChooseLeft) {
			return localizedForKey("choose_left");
		} else if (mergeChangeAction == MergeChangeAction.ChooseRight) {
			return localizedForKey("choose_right");
		} else if (mergeChangeAction == MergeChangeAction.ChooseNone) {
			return localizedForKey("choose_none");
		} else if (mergeChangeAction == MergeChangeAction.ChooseBothLeftFirst) {
			return localizedForKey("choose_both_left_first");
		} else if (mergeChangeAction == MergeChangeAction.ChooseBothRightFirst) {
			return localizedForKey("choose_both_right_first");
		} else if (mergeChangeAction == MergeChangeAction.CustomEditing) {
			return localizedForKey("resolve_by_hand");
		} else if (mergeChangeAction == MergeChangeAction.AutomaticMergeResolving) {
			return localizedForKey("automatic_merge_resolving");
		} else if (mergeChangeAction == MergeChangeAction.Undecided) {
			return localizedForKey("unresolved_merge");
		}
		return null;
	}

	Icon iconForMergeChangeAction(MergeChangeAction mergeChangeAction) {
		if (mergeChangeAction == MergeChangeAction.KeepChange) {
			return UtilsIconLibrary.ACCEPT_ICON;
		} else if (mergeChangeAction == MergeChangeAction.IgnoreChange) {
			return UtilsIconLibrary.REFUSE_ICON;
		} else if (mergeChangeAction == MergeChangeAction.ChooseLeft) {
			return UtilsIconLibrary.CHOOSE_LEFT_ICON;
		} else if (mergeChangeAction == MergeChangeAction.ChooseRight) {
			return UtilsIconLibrary.CHOOSE_RIGHT_ICON;
		} else if (mergeChangeAction == MergeChangeAction.ChooseNone) {
			return UtilsIconLibrary.CHOOSE_NONE;
		} else if (mergeChangeAction == MergeChangeAction.ChooseBothLeftFirst) {
			return UtilsIconLibrary.CHOOSE_BOTH_LEFT_FIRST;
		} else if (mergeChangeAction == MergeChangeAction.ChooseBothRightFirst) {
			return UtilsIconLibrary.CHOOSE_BOTH_RIGHT_FIRST;
		} else if (mergeChangeAction == MergeChangeAction.CustomEditing) {
			return UtilsIconLibrary.CUSTOM_EDITING_ICON;
		} else if (mergeChangeAction == MergeChangeAction.AutomaticMergeResolving) {
			return UtilsIconLibrary.AUTOMATIC_MERGE_RESOLVING_ICON;
		} else if (mergeChangeAction == MergeChangeAction.Undecided) {
			return UtilsIconLibrary.CONFLICT_UNRESOLVED_ICON;
		}
		return null;
	}

	private boolean editable = true;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public MergeTextArea getLeftTextArea() {
		return _leftTextArea;
	}

	public MergeTextArea getMergeTextArea() {
		return _mergeTextArea;
	}

	public MergeTextArea getRightTextArea() {
		return _rightTextArea;
	}

	@Override
	public void update(Observable o, Object dataModification) {
		if (o == getMerge() && dataModification instanceof MergeChange) {
			// System.out.println("Hop on change");
			comparePanel.update();
			getFilteredChangeList().repaint();
		}
	}

	public JList getFilteredChangeList() {
		return _changeListPanel.filteredChangeList();
	}

	public class FilterChangeList extends JPanel {

		private Vector<MergeChange.ChangeCategory> selectedCategories;
		JList _filteredList;

		public FilterChangeList(String noChangeLabel) {
			super(new BorderLayout());
			selectedCategories = new Vector<MergeChange.ChangeCategory>();
			for (MergeChange.ChangeCategory c : MergeChange.ChangeCategory.values()) {
				selectedCategories.add(c);
			}
			initFilteredList(noChangeLabel);
			_filteredList.setMinimumSize(_filteredList.getPreferredSize());
			add(new FilterPanel(), BorderLayout.NORTH);
			JScrollPane pane = new JScrollPane(filteredChangeList());
			pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			add(pane, BorderLayout.CENTER);
		}

		private void initFilteredList(String noChangeLabel) {
			if (_merge.getChanges().size() > 0) {
				_filteredList = new JList(_merge.getChanges());
				_filteredList.setVisibleRowCount(5);
				_filteredList.setCellRenderer(new DefaultListCellRenderer() {
					@Override
					public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
							boolean cellHasFocus) {
						JLabel returned = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
						returned.setIcon(iconForChange((MergeChange) value));
						returned.setText(((MergeChange) value).toString());
						return returned;
					}
				});
				_filteredList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				lsl = new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						selectChange((MergeChange) _filteredList.getSelectedValue());
					}
				};
				_filteredList.addListSelectionListener(lsl);
				_filteredList.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent mouseEvent) {
						if (mouseEvent.isPopupTrigger() && _filteredList.getSelectedValue() != null
								&& _filteredList.getSelectedValue() instanceof MergeChange
								&& ((MergeChange) _filteredList.getSelectedValue()).getMergeChangeSource() == MergeChangeSource.Conflict) {
							comparePanel._buttonForChanges.get(_filteredList.getSelectedValue()).getPopupMenu()
									.show(mouseEvent.getComponent(), mouseEvent.getX() + 10, mouseEvent.getY());
						}
					}

					@Override
					public void mouseReleased(MouseEvent mouseEvent) {
						if (mouseEvent.isPopupTrigger() && _filteredList.getSelectedValue() != null
								&& _filteredList.getSelectedValue() instanceof MergeChange
								&& ((MergeChange) _filteredList.getSelectedValue()).getMergeChangeSource() == MergeChangeSource.Conflict) {
							comparePanel._buttonForChanges.get(_filteredList.getSelectedValue()).getPopupMenu()
									.show(mouseEvent.getComponent(), mouseEvent.getX() + 10, mouseEvent.getY());
						}
					}

				});

			} else {
				Object[] obj = { noChangeLabel };
				_filteredList = new JList(obj);
				_filteredList.setEnabled(false);
			}

		}

		public void addAllFilter(MergeChange.ChangeCategory[] categories) {
			for (MergeChange.ChangeCategory item : categories) {
				selectedCategories.add(item);
			}
			refreshList();
		}

		public void removeAllFilter(MergeChange.ChangeCategory[] categories) {
			for (MergeChange.ChangeCategory item : categories) {
				selectedCategories.remove(item);
			}
			refreshList();
		}

		// public void addFilter(MergeChange.ChangeCategory category){
		// selectedCategories.add(category);
		// refreshList();
		// }
		// public void removeFilter(MergeChange.ChangeCategory category){
		// selectedCategories.remove(category);
		// refreshList();
		// }
		public JList filteredChangeList() {
			return _filteredList;
		}

		public void refreshList() {
			final Vector<MergeChange> listData = _merge.filteredChangeList(selectedCategories);
			_filteredList.setModel(new AbstractListModel() {
				@Override
				public int getSize() {
					return listData.size();
				}

				@Override
				public Object getElementAt(int i) {
					return listData.elementAt(i);
				}
			});

		}

		class FilterPanel extends JPanel {

			public FilterPanel() {
				super(new FlowLayout(FlowLayout.LEFT));
				add(new ButtonFilter(MergeChange.CONFLICT_UNRESOLVED_CATEGORIES, true, UtilsIconLibrary.CONFLICT_ICON));
				add(new ButtonFilter(MergeChange.CONFLICT_RESOLVED_CATEGORIES, true, UtilsIconLibrary.CONFLICT_RESOLVED_ICON));
				add(new ButtonFilter(MergeChange.LEFT_CATEGORIES, true, UtilsIconLibrary.LEFT_ICON));
				add(new ButtonFilter(MergeChange.RIGHT_CATEGORIES, true, UtilsIconLibrary.RIGHT_ICON));
			}

			class ButtonFilter extends JButton {
				boolean _isSelected;
				final MergeChange.ChangeCategory[] _categories;

				public ButtonFilter(MergeChange.ChangeCategory[] categories, boolean isSelected, Icon icon) {
					super();
					_isSelected = isSelected;
					_categories = categories;
					setIcon(icon);
					setBorder(_isSelected ? BorderFactory.createEtchedBorder(EtchedBorder.LOWERED) : BorderFactory.createEmptyBorder());
					setPreferredSize(new Dimension(27, 18));
					addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							_isSelected = !_isSelected;
							if (_isSelected) {
								addAllFilter(_categories);
							} else {
								removeAllFilter(_categories);
							}
							setBorder(_isSelected ? BorderFactory.createEtchedBorder(EtchedBorder.LOWERED) : BorderFactory
									.createEmptyBorder());
							System.out.println(getPreferredSize());
						}

					});
				}

			}
		}

		public void setVisibleRowCount(int i) {
			_filteredList.setVisibleRowCount(i);
		}
	}
}
