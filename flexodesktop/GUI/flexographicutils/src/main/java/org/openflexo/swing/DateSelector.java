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

/**
 * Widget allowing to edit a date with a calendar popup
 * 
 * @author sguerin
 * 
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DateSelector extends TextFieldCustomPopup<Date>
{
	public static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 11);

	static final String[] days = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

	static final String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", };

	private Calendar _cal;

	private SimpleDateFormat formatter;

	Date _revertValue;


	/* -------------- GUI Components --------------------------- */
	CalendarPanel calendarPanel;

	public DateSelector()
	{
		super(null);
	}

	public DateSelector(Date d)
	{
		this();
		setEditedObject(d);
		_revertValue = (Date)d.clone();
	}

	public Calendar getCalendar()
	{
		if (_cal == null) {
			_cal = Calendar.getInstance(Locale.ENGLISH);
		}
		return _cal;
	}

	@Override
	protected ResizablePanel createCustomPanel(Date editedObject)
	{
		calendarPanel = new CalendarPanel();
		return calendarPanel;
	}

	@Override
	public Date getEditedObject()
	{
		return getDate();
	}

	@Override
	public void setEditedObject(Date object)
	{
		super.setEditedObject(object);
		if (object != null) {
			getCalendar().setTime(object);
		}
	}

	/**
	 * Returns the Date associated with the picker
	 * 
	 * @return The current selected date.
	 */
	public Date getDate()
	{
		return getCalendar().getTime();
	}

	/**
	 * Sets the date for the picker. All GUI elements associated with the picker
	 * are updated and the grid of date buttons is rebuilt
	 * 
	 * @param d
	 *            the new date for this date picker.
	 */
	public void setDate(Date d)
	{
		if (d == null) {
			d = new Date();
		}
		setEditedObject(d);
	}

	@Override
	public void updateCustomPanel(Date editedObject)
	{
		if (calendarPanel != null) {
			calendarPanel.update();
		}
	}

	@Override
	public String renderedString(Date editedObject)
	{
		if (formatter == null) {
			formatter = new SimpleDateFormat();
		}
		formatter.applyPattern("MMMM d, yyyy");
		return (formatter.format(getDate()));
	}

	protected class CalendarPanel extends ResizablePanel
	{
		ButtonsControlPanel controlPanel;

		private JButton prevMonth;

		private JButton nextMonth;

		JComboBox monthComboBox;

		JSpinner yearSpinner;

		private JPanel dayButtons;

		void update()
		{
			yearSpinner.setValue(new Integer(getCalendar().get(Calendar.YEAR)));
			monthComboBox.setSelectedIndex(getCalendar().get(Calendar.MONTH));
			rebuildDayButtons();
		}

		protected CalendarPanel()
		{
			super();

			// Create the month chooser as a comboBox
			monthComboBox = new JComboBox(months);
			monthComboBox.setLightWeightPopupEnabled(false);
			monthComboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					getCalendar().set(Calendar.MONTH, monthComboBox.getSelectedIndex());
					fireEditedObjectChanged();
				}
			});

			// create the spinner that selects the current year
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			// Range is current year +/- 100 , step size 1
			SpinnerNumberModel yearModel = new SpinnerNumberModel(currentYear, currentYear - 100, currentYear + 100, 1);
			yearSpinner = new JSpinner(yearModel);
			yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
			yearSpinner.setMinimumSize(yearSpinner.getPreferredSize());
			// GUITools.getTextField(yearSpinner).addActionListener(this);
			// //just gets texfield of the spinners jformatted textfield
			yearSpinner.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e)
				{
					if (e.getSource() == yearSpinner) {
						getCalendar().set(Calendar.YEAR, ((Number) yearSpinner.getValue()).intValue());
					}
					fireEditedObjectChanged();
				}
			});
			// Create next/prev buttons
			prevMonth = new JButton("<");
			nextMonth = new JButton(">");
			// setup actionlisteners
			prevMonth.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					getCalendar().add(Calendar.MONTH, -1);
					fireEditedObjectChanged();
				}
			});
			nextMonth.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					getCalendar().add(Calendar.MONTH, 1);
					fireEditedObjectChanged();
				}
			});

			// Build the calendar Panel
			setLayout(new BorderLayout());

			// Build the month / year selectors
			JPanel monthPanel = new JPanel(new BorderLayout());
			JPanel choosersPanel = new JPanel();
			choosersPanel.add(monthComboBox);
			choosersPanel.add(yearSpinner);
			monthPanel.add(prevMonth, BorderLayout.WEST);
			monthPanel.add(choosersPanel, BorderLayout.CENTER);
			monthPanel.add(nextMonth, BorderLayout.EAST);
			add(monthPanel, BorderLayout.NORTH);

			calendarPanel = this;

			// Day button grid
			dayButtons = new JPanel(new GridBagLayout());
			rebuildDayButtons();
			add(dayButtons, BorderLayout.CENTER);

			controlPanel = new ButtonsControlPanel() {
				@Override
				public String localizedForKeyAndButton(String key, JButton component) {
					return DateSelector.this.localizedForKeyAndButton(key, component);
				}
			};
			controlPanel.addButton("apply",new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					apply();
				}
			});
			controlPanel.addButton("cancel",new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					cancel();
				}
			});
			controlPanel.addButton("reset",new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					setEditedObject(null);
					apply();
				}
			});

			controlPanel.applyFocusTraversablePolicyTo(this,true);

			add(controlPanel, BorderLayout.SOUTH);
		}


		@Override
		public Dimension getDefaultSize()
		{
			return getSize();
		}

		@Override
		public void setPreferredSize(Dimension preferredSize) {

		}

		/**
		 * Anytime the date buttons need to be changes, this should be called
		 * When the month or year changes, or when any dae button is clicked.
		 * Clicked day buttons are highlighted, and so the grid is redrawn
		 */
		private void rebuildDayButtons()
		{
			// Completely cleanout the panel and rebuild it from scratch.
			dayButtons.removeAll();

			// Build the Date Grid
			GridBagConstraints c = new GridBagConstraints();
			c.weightx = c.weighty = 1;
			c.gridx = c.gridy = 0;
			c.fill = GridBagConstraints.BOTH;

			int dayOfWeek = 0;
			// This is needed because Caldendar does not provide an ordinal day
			// of week field.
			switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) // find
			// out
			// what
			// day
			// of
			// the
			// week
			// today
			// is
			{
			case Calendar.SUNDAY:
				dayOfWeek = 0;
				break;
			case Calendar.MONDAY:
				dayOfWeek = 1;
				break;
			case Calendar.TUESDAY:
				dayOfWeek = 2;
				break;
			case Calendar.WEDNESDAY:
				dayOfWeek = 3;
				break;
			case Calendar.THURSDAY:
				dayOfWeek = 4;
				break;
			case Calendar.FRIDAY:
				dayOfWeek = 5;
				break;
			case Calendar.SATURDAY:
				dayOfWeek = 6;
				break;
			}

			for (int i = 0; i < 7; i++) {
				c.gridx = i;
				JLabel l = new JLabel(days[i], SwingConstants.CENTER);
				l.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
				// Make the Day of the week bold if it is today
				if (i == dayOfWeek && getCalendar().get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
						&& getCalendar().get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
					l.setFont(l.getFont().deriveFont(Font.BOLD));
				}
				dayButtons.add(l, c);
			}
			Calendar constructionCal = (Calendar) getCalendar().clone();

			for (int i = 1; i <= getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
				constructionCal.set(Calendar.DAY_OF_MONTH, i);
				c.gridx = constructionCal.get(Calendar.DAY_OF_WEEK) - 1; // start
				// at
				// gridx
				// = 0
				c.gridy = constructionCal.get(Calendar.WEEK_OF_MONTH); // start
				// at
				// gridy
				// = 1,
				// because
				// of
				// the
				// day
				// labels
				DateButton b = new DateButton(constructionCal);
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e)
					{
						getCalendar().setTime(((DateButton) e.getSource()).getDate());
						fireEditedObjectChanged();
					}
				});
				dayButtons.add(b, c);
			}

			if (c.gridy < 6) {
				c.gridy = 6;
				dayButtons.add(Box.createRigidArea(new Dimension(25, 20)), c);
			}

			calendarPanel.validate();
			calendarPanel.repaint();
		}

		/**
		 * An extension of JButton that shows a date as a digit indicating the
		 * day of the month
		 */
		private class DateButton extends JButton
		{

			private boolean selectedDay = false;

			private boolean busy = false; // any events occuring today?

			private Date date = new Date();

			public DateButton(Calendar cal)
			{
				super("");
				date = cal.getTime(); // create a date specifically for this
				// button

				setFont(NORMAL_FONT);

				// If this button represents today, set appropriate status.
				// comaparing dates doesnt work since those are only equal if
				// they match upto the millisecond.
				if (cal.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
						&& cal.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
					setFont(getFont().deriveFont(Font.BOLD));
				}

				setText(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
				setForeground(Color.BLUE);
				setPreferredSize(new Dimension(30, 20));
				setMinimumSize(getPreferredSize());
				setMaximumSize(getPreferredSize());
			}

			public void setDateStyle()
			{
				if (selectedDay) {
					setBackground(Color.GREEN);
				} else if (busy) {
					setBackground(Color.GRAY);
				} else {
					setBackground(Color.YELLOW);
				}
			}

			public Date getDate()
			{
				return date;
			}

			public boolean isBusy()
			{
				return busy;
			}

			public void setBusy(boolean b)
			{
				this.busy = b;
			}

			public boolean isSelectedDay()
			{
				return selectedDay;
			}

			public void setSelectedDay(boolean selected)
			{
				this.selectedDay = selected;
			}

		}

	}

	@Override
	public void apply()
	{
		setRevertValue(getEditedObject());
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
	public void setRevertValue(Date oldValue)
	{
		_revertValue = oldValue;
	}

	public Date getRevertValue()
	{
		return _revertValue;
	}


	@Override
	protected void openPopup() {
		super.openPopup();
		if (calendarPanel != null) {
			calendarPanel.controlPanel.applyFocusTraversablePolicyTo(calendarPanel,true);
		}
	}

	// Override if required
	public String localizedForKeyAndButton(String key, JButton component)
	{
		return key;
	}

}
