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

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.diff.ComputeDiff;
import org.openflexo.diff.ComputeDiff.DiffReport;
import org.openflexo.diff.DelimitingMethod;
import org.openflexo.diff.DiffSource;
import org.openflexo.diff.merge.DetailedMerge;
import org.openflexo.diff.merge.MergeChange;
import org.openflexo.diff.merge.MergeChange.MergeChangeAction;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.jedit.JEditTextArea;
import org.openflexo.jedit.JEditTextAreaWithHighlights;
import org.openflexo.jedit.TokenMarker;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.swing.diff.DiffPanel;
import org.openflexo.swing.merge.MergePanelElements.ComparePanel;
import org.openflexo.toolbox.TokenMarkerStyle;


public class DetailedMergeAnalysisPanel extends JDialog implements Observer {

	MergePanelElements mergePanelElements;
	DetailedMerge _detailedMerge;
	private MergePanelElements.FilterChangeList changesList;
	private ComparePanel comparePanel;

	ReadOnlyTextArea mergedText;
	ReadOnlyTextArea leftText;
	ReadOnlyTextArea rightText;
	ReadOnlyTextArea originalText;

	private TokenMarkerStyle _style;

	private static final int MAX_ROWS = 7;

	private DiffReport _diffLeft = null;
	private DiffReport _diffRight = null;
	private DiffReport _diffLeftRight = null;

	void updateHeader()
	{
		String titleText = localizedForKey("detailed_analysis_for_conflict")
				+ " ["+_detailedMerge.getDocumentType().getName()+"] "
				+ " : ["+(_detailedMerge.isResolved()?localizedForKey("resolved"):localizedForKey("unresolved"))+"]";

		setTitle(titleText);

		resolvedIcon.setIcon(_detailedMerge.isResolved()?UtilsIconLibrary.ACCEPT_ICON:UtilsIconLibrary.REFUSE_ICON);
		title.setText(titleText);

		acceptButton.setEnabled(_detailedMerge.isResolved());
	}

	private JPanel header;
	private JLabel resolvedIcon;
	private JLabel title;
	private JButton acceptButton;
	private JButton cancelButton;

	public DetailedMergeAnalysisPanel(DetailedMerge detailedMerge)
	{
		super((Frame)null,true);

		_detailedMerge = detailedMerge;
		_detailedMerge.addObserver(this);

		String original = detailedMerge.getChange().getOriginalText();
		String left = detailedMerge.getChange().getTokenizedLeftText();
		String right = detailedMerge.getChange().getTokenizedRightText();

		_diffLeft = ComputeDiff.diff(left,original,detailedMerge.getDelimitingMethod());;
		_diffRight = ComputeDiff.diff(original,right,detailedMerge.getDelimitingMethod());
		_diffLeftRight = ComputeDiff.diff(left,right,detailedMerge.getDelimitingMethod());

		_style = detailedMerge.getDocumentType().getStyle();

		JPanel content = new JPanel(new BorderLayout());

		mergePanelElements = new MergePanelElements(detailedMerge,_style) {
			@Override
			public void selectChange (MergeChange change) {
				super.selectChange(change);
				DetailedMergeAnalysisPanel.this.selectChange(change);
			}
			@Override
			public void update(Observable o, Object dataModification)
			{
				super.update(o,dataModification);
				if (o == getMerge() && dataModification instanceof MergeChange) {
					updateHeader();
				}
			}
			@Override
			protected String localizedForKey(String key)
			{
				return DetailedMergeAnalysisPanel.this.localizedForKey(key);
			}


		};
		changesList = mergePanelElements.getChangesList();
		changesList.setVisibleRowCount(5);
		comparePanel = mergePanelElements.getComparePanel();
		JScrollPane mergeTextArea = mergePanelElements.getMergePanel();

		title = new JLabel(localizedForKey("detailed_analysis_for_conflict"),SwingConstants.LEFT);
		title.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

		resolvedIcon = new JLabel(_detailedMerge.isResolved()?UtilsIconLibrary.ACCEPT_ICON:UtilsIconLibrary.REFUSE_ICON);

		JPanel titlePanel = new JPanel(new FlowLayout());
		titlePanel.add(resolvedIcon);
		titlePanel.add(title);

		acceptButton = new JButton(localizedForKey("accept_merged_result"),UtilsIconLibrary.ACCEPT_ICON);
		cancelButton = new JButton(localizedForKey("close"),UtilsIconLibrary.REFUSE_ICON);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		acceptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				validateChanges();
			}
		});
		JPanel buttonsPanel = new JPanel(new FlowLayout());
		buttonsPanel.add(acceptButton);
		buttonsPanel.add(cancelButton);

		header = new JPanel(new BorderLayout());
		header.add(titlePanel,BorderLayout.WEST);
		header.add(buttonsPanel,BorderLayout.EAST);

		content.add(header,BorderLayout.NORTH);

		updateHeader();

		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel mergePanel = new JPanel(new BorderLayout());

		mergePanel.add(changesList,BorderLayout.EAST);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,mergeTextArea,comparePanel);
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);
		Dimension dim = mergeTextArea.getPreferredSize();
		dim.height = 280;
		mergeTextArea.setPreferredSize(dim);
		Dimension dim2 = comparePanel.getPreferredSize();
		dim2.height = 300;
		comparePanel.setPreferredSize(dim2);
		splitPane.setDividerLocation(0.5);

		mergePanel.add(splitPane,BorderLayout.CENTER);

		tabbedPane.add(mergePanel,localizedForKey("detailed_merge"));
		tabbedPane.add(new DiffPanel(_diffLeft,_style,BorderLayout.EAST),localizedForKey("left_original_diff"));
		tabbedPane.add(new DiffPanel(_diffRight,_style,BorderLayout.EAST),localizedForKey("original_right_diff"));
		tabbedPane.add(new DiffPanel(_diffLeftRight,_style,BorderLayout.EAST),localizedForKey("left_right_diff"));

		content.add(tabbedPane,BorderLayout.CENTER);

		/*controlPanel = mergePanelElements.getControlPanel();
		add(controlPanel,BorderLayout.SOUTH);

		JButton doneButton = new JButton();
		doneButton.setText("Done");
		doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				done();
			}
		});

		controlPanel.add(doneButton);*/

		int maxRows = 0;
		int maxCols = 0;

		DiffSource mergedSource = new DiffSource(_detailedMerge.getMergedSource().getText(),DelimitingMethod.LINES);
		DiffSource leftSource = new DiffSource(_detailedMerge.getLeftSource().getText(),DelimitingMethod.LINES);
		DiffSource rightSource = new DiffSource(_detailedMerge.getRightSource().getText(),DelimitingMethod.LINES);
		DiffSource originalSource = new DiffSource(_detailedMerge.getOriginalSource().getText(),DelimitingMethod.LINES);

		if (mergedSource.tokensCount() > maxRows) {
			maxRows = mergedSource.tokensCount();
		}
		if (leftSource.tokensCount() > maxRows) {
			maxRows = leftSource.tokensCount();
		}
		if (rightSource.tokensCount() > maxRows) {
			maxRows = rightSource.tokensCount();
		}
		if (originalSource.tokensCount() > maxRows) {
			maxRows = originalSource.tokensCount();
		}
		if (mergedSource.getMaxCols() > maxCols) {
			maxCols = mergedSource.getMaxCols();
		}
		if (leftSource.getMaxCols() > maxCols) {
			maxCols = leftSource.getMaxCols();
		}
		if (rightSource.getMaxCols() > maxCols) {
			maxCols = rightSource.getMaxCols();
		}
		if (originalSource.getMaxCols() > maxCols) {
			maxCols = originalSource.getMaxCols();
		}

		mergedText = new ReadOnlyTextArea(_detailedMerge.getMergedSource().getText(),_style);
		mergedText.setColumns(maxCols);
		mergedText.setRows(maxRows);
		mergedText.validate();

		leftText = new ReadOnlyTextArea(_detailedMerge.getLeftSource().getText(),_style);
		leftText.setColumns(maxCols);
		leftText.setRows(maxRows);
		leftText.validate();

		rightText = new ReadOnlyTextArea(_detailedMerge.getRightSource().getText(),_style);
		rightText.setColumns(maxCols);
		rightText.setRows(maxRows);
		rightText.validate();

		originalText = new ReadOnlyTextArea(_detailedMerge.getOriginalSource().getText(),_style);
		originalText.setColumns(maxCols);
		originalText.setRows(maxRows);
		originalText.validate();

		JPanel previewPanel = new JPanel(new VerticalLayout());

		JPanel mergedTextPanel = new JPanel(new BorderLayout());
		JLabel mergedTextLabel = new JLabel(localizedForKey("merged_result")+" ",SwingConstants.RIGHT);
		mergedTextLabel.setVerticalAlignment(SwingConstants.TOP);
		mergedTextLabel.setPreferredSize(new Dimension(120,15));
		mergedTextPanel.add(mergedTextLabel,BorderLayout.WEST);
		mergedTextPanel.add(mergedText,BorderLayout.CENTER);

		JPanel leftTextPanel = new JPanel(new BorderLayout());
		JLabel leftTextLabel = new JLabel(localizedForKey("left_source")+" ",SwingConstants.RIGHT);
		leftTextLabel.setVerticalAlignment(SwingConstants.TOP);
		leftTextLabel.setPreferredSize(new Dimension(120,15));
		leftTextPanel.add(leftTextLabel,BorderLayout.WEST);
		leftTextPanel.add(leftText,BorderLayout.CENTER);

		JPanel rightTextPanel = new JPanel(new BorderLayout());
		JLabel rightTextLabel = new JLabel(localizedForKey("right_source")+" ",SwingConstants.RIGHT);
		rightTextLabel.setVerticalAlignment(SwingConstants.TOP);
		rightTextLabel.setPreferredSize(new Dimension(120,15));
		rightTextPanel.add(rightTextLabel,BorderLayout.WEST);
		rightTextPanel.add(rightText,BorderLayout.CENTER);

		JPanel originalTextPanel = new JPanel(new BorderLayout());
		JLabel originalTextLabel = new JLabel(localizedForKey("original_source")+" ",SwingConstants.RIGHT);
		originalTextLabel.setVerticalAlignment(SwingConstants.TOP);
		originalTextLabel.setPreferredSize(new Dimension(120,15));
		originalTextPanel.add(originalTextLabel,BorderLayout.WEST);
		originalTextPanel.add(originalText,BorderLayout.CENTER);

		previewPanel.add(mergedTextPanel);
		previewPanel.add(leftTextPanel);
		previewPanel.add(rightTextPanel);
		previewPanel.add(originalTextPanel);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(previewPanel,BorderLayout.CENTER);
		final JScrollBar horizontalScrollBar = new JScrollBar(Adjustable.HORIZONTAL);
		int widthToScroll = mergedText.getPreferredSize().width;
		horizontalScrollBar.setValues(-mergedText.getHorizontalOffset(),widthToScroll,1,widthToScroll * 3);
		horizontalScrollBar.setUnitIncrement(mergedText.getPainter().getFontMetrics()
				.charWidth('W'));
		horizontalScrollBar.setBlockIncrement(widthToScroll / 2);

		horizontalScrollBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e)
			{
				mergedText.getHorizontalScrollBar().setValue(e.getValue()
						*(mergedText.getHorizontalScrollBar().getMaximum()
								-mergedText.getHorizontalScrollBar().getMinimum())
								/(horizontalScrollBar.getMaximum()-horizontalScrollBar.getMinimum()));
				leftText.getHorizontalScrollBar().setValue(e.getValue()
						*(leftText.getHorizontalScrollBar().getMaximum()
								-leftText.getHorizontalScrollBar().getMinimum())
								/(horizontalScrollBar.getMaximum()-horizontalScrollBar.getMinimum()));
				rightText.getHorizontalScrollBar().setValue(e.getValue()
						*(rightText.getHorizontalScrollBar().getMaximum()
								-rightText.getHorizontalScrollBar().getMinimum())
								/(horizontalScrollBar.getMaximum()-horizontalScrollBar.getMinimum()));
				originalText.getHorizontalScrollBar().setValue(e.getValue()
						*(originalText.getHorizontalScrollBar().getMaximum()
								-originalText.getHorizontalScrollBar().getMinimum())
								/(horizontalScrollBar.getMaximum()-horizontalScrollBar.getMinimum()));
			}
		});
		panel.add(horizontalScrollBar,BorderLayout.SOUTH);

		if (maxRows > MAX_ROWS) {
			mergedText.setPreferredSize(new Dimension(100,mergedText.getPainter().getFontMetrics().getHeight()*MAX_ROWS));
			leftText.setPreferredSize(new Dimension(100,leftText.getPainter().getFontMetrics().getHeight()*MAX_ROWS));
			rightText.setPreferredSize(new Dimension(100,rightText.getPainter().getFontMetrics().getHeight()*MAX_ROWS));
			originalText.setPreferredSize(new Dimension(100,originalText.getPainter().getFontMetrics().getHeight()*MAX_ROWS));

			final JScrollBar verticalScrollBar = new JScrollBar(Adjustable.VERTICAL);
			int heightToScroll = mergedText.getPreferredSize().width;
			verticalScrollBar.setValues(-mergedText.getHorizontalOffset(),heightToScroll,1,heightToScroll * 3);
			verticalScrollBar.setUnitIncrement(mergedText.getPainter().getFontMetrics()
					.charWidth('W'));
			verticalScrollBar.setBlockIncrement(heightToScroll / 2);

			verticalScrollBar.addAdjustmentListener(new AdjustmentListener() {
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e)
				{
					mergedText.getVerticalScrollBar().setValue(e.getValue()
							*(mergedText.getVerticalScrollBar().getMaximum()
									-mergedText.getVerticalScrollBar().getMinimum())
									/(verticalScrollBar.getMaximum()-verticalScrollBar.getMinimum()));
					leftText.getVerticalScrollBar().setValue(e.getValue()
							*(leftText.getVerticalScrollBar().getMaximum()
									-leftText.getVerticalScrollBar().getMinimum())
									/(verticalScrollBar.getMaximum()-verticalScrollBar.getMinimum()));
					rightText.getVerticalScrollBar().setValue(e.getValue()
							*(rightText.getVerticalScrollBar().getMaximum()
									-rightText.getVerticalScrollBar().getMinimum())
									/(verticalScrollBar.getMaximum()-verticalScrollBar.getMinimum()));
					originalText.getVerticalScrollBar().setValue(e.getValue()
							*(originalText.getVerticalScrollBar().getMaximum()
									-originalText.getVerticalScrollBar().getMinimum())
									/(verticalScrollBar.getMaximum()-verticalScrollBar.getMinimum()));
				}
			});
			panel.add(verticalScrollBar,BorderLayout.EAST);

			MouseWheelListener mouseWheelListener = new MouseWheelListener() {

				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					if (verticalScrollBar.isVisible() &&
							e.getScrollAmount() != 0) {
						int direction = 0;
						direction = e.getWheelRotation() < 0 ? -1 : 1;
						if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
							JEditTextArea.scrollByUnits(verticalScrollBar, direction, e.getScrollAmount());
						}
						else if (e.getScrollType() ==
								MouseWheelEvent.WHEEL_BLOCK_SCROLL) {
							JEditTextArea.scrollByBlock(verticalScrollBar, direction);
						}
					}
				}
			};
			panel.addMouseWheelListener(mouseWheelListener);
		}


		content.add(panel,BorderLayout.SOUTH);

		content.validate();

		getContentPane().add(content);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (_detailedMerge.getChanges().size() > 0) {
					mergePanelElements.selectChange(_detailedMerge.getChanges().firstElement());
				}
			}
		});

		setPreferredSize(new Dimension(1000,800));
		validate();
		pack();
		setVisible(true);

	}

	protected void validateChanges()
	{
		//System.out.println("Validate: "+_detailedMerge.getMergedSource().getText());
		_detailedMerge.getChange().setCustomHandEdition(_detailedMerge.getMergedSource().getText());
		_detailedMerge.getChange().setMergeChangeAction(MergeChangeAction.CustomEditing);
		dispose();
	}

	@Override
	public void dispose()
	{
		_detailedMerge.deleteObserver(this);
		super.dispose();
	}

	public void selectChange (MergeChange change)
	{
		mergedText.getPainter().setSelectionColor(getColor(change));
		leftText.getPainter().setSelectionColor(getColor(change));
		rightText.getPainter().setSelectionColor(getColor(change));
		originalText.getPainter().setSelectionColor(getColor(change));

		if (change.getLast1() >= change.getFirst1()) {
			originalText.selectArea(
					change.getMerge().getOriginalSource().tokenAt(change.getFirst1()).getTokenStartIndex(), change
					.getMerge().getOriginalSource().tokenAt(change.getLast1()).getTokenEndIndex());
		}
		else {
			originalText.selectArea(0,0);
		}

		if (change.getLast0() >= change.getFirst0()) {
			if (change.getMerge().getLeftSource().tokenAt(change.getFirst0()) != null
					&& change.getMerge().getLeftSource().tokenAt(change.getLast0()) != null) {
				leftText.selectArea(
						change.getMerge().getLeftSource().tokenAt(change.getFirst0()).getTokenStartIndex(), change.getMerge()
						.getLeftSource().tokenAt(change.getLast0()).getTokenEndIndex());
			}
		}
		else {
			leftText.selectArea(0,0);
		}

		if (change.getLast2() >= change.getFirst2()) {
			if (change.getMerge().getRightSource().tokenAt(change.getFirst2()) != null
					&& change.getMerge().getRightSource().tokenAt(change.getLast2()) != null) {
				rightText.selectArea(
						change.getMerge().getRightSource().tokenAt(change.getFirst2()).getTokenStartIndex(), change.getMerge()
						.getRightSource().tokenAt(change.getLast2()).getTokenEndIndex());
			}
		}
		else {
			rightText.selectArea(0,0);
		}

		if (change.getLastMergeIndex() >= change.getFirstMergeIndex()
				&& change.getMerge().getMergedSource().tokenAt(change.getFirstMergeIndex()) != null
				&& change.getMerge().getMergedSource().tokenAt(change.getLastMergeIndex()) != null) {
			mergedText.selectArea(
change.getMerge().getMergedSource().tokenAt(change.getFirstMergeIndex()).getTokenStartIndex(), change
					.getMerge().getMergedSource().tokenAt(change.getLastMergeIndex()).getTokenEndIndex());
		}
		else {
			mergedText.selectArea(0,0);
		}

	}

	protected String localizedForKey(String key)
	{
		return key;
	}

	protected class ReadOnlyTextArea extends JEditTextAreaWithHighlights
	{
		public ReadOnlyTextArea (String text, TokenMarkerStyle style)
		{
			super();
			disableDefaultMouseWheelListener();
			setTokenMarker(TokenMarker.makeTokenMarker(style));
			setText(text);
			setEditable(false);
			setBackground(Color.RED);
			remove(vertical);
			remove(horizontal);
			getPainter().setLineHighlightEnabled(false);
			getPainter().setCaretColor(Color.WHITE);
		}

		public JScrollBar getHorizontalScrollBar()
		{
			return horizontal;
		}

		public JScrollBar getVerticalScrollBar()
		{
			return vertical;
		}

		// Disable select
		@Override
		public void select(int start, int end)
		{
		}

		public void selectArea(int start, int end)
		{
			super.select(start, end);
		}
	}

	private Color getColor(MergeChange change)
	{
		if (change.getMergeChangeSource() == MergeChange.MergeChangeSource.Left) {
			return MergeHighlight.MODIFICATION_SELECTED_COLOR;
		}
		else if (change.getMergeChangeSource() == MergeChange.MergeChangeSource.Conflict) {
			return MergeHighlight.REMOVAL_SELECTED_COLOR;
		}
		else if (change.getMergeChangeSource() == MergeChange.MergeChangeSource.Right) {
			return MergeHighlight.ADDITION_SELECTED_COLOR;
		} else {
			return Color.YELLOW;
		}
	}

	@Override
	public void update(Observable observable, Object dataModification)
	{
		if (observable == _detailedMerge && dataModification instanceof MergeChange) {
			mergedText.setText(_detailedMerge.getMergedSource().getText());
		}
	}

}
