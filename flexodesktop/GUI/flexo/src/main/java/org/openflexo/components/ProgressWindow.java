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
package org.openflexo.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.icon.IconLibrary;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoFrame;


/**
 * Default progress window
 *
 * @author sguerin
 */
public class ProgressWindow extends JDialog implements FlexoProgress
{

	private static class ProgressBarLabel extends JLabel {

		private static final Color LABEL_COLOR = FlexoCst.OPEN_BLUE_COLOR;

		public ProgressBarLabel() {
			super();
			setForeground(LABEL_COLOR);
		}

		public ProgressBarLabel(Icon image, int horizontalAlignment) {
			super(image, horizontalAlignment);
			setForeground(LABEL_COLOR);
		}

		public ProgressBarLabel(Icon image) {
			super(image);
			setForeground(LABEL_COLOR);
		}

		public ProgressBarLabel(String text, Icon icon, int horizontalAlignment) {
			super(text, icon, horizontalAlignment);
			setForeground(LABEL_COLOR);
		}

		public ProgressBarLabel(String text, int horizontalAlignment) {
			super(text, horizontalAlignment);
			setForeground(LABEL_COLOR);
		}

		public ProgressBarLabel(String text) {
			super(text);
			setForeground(LABEL_COLOR);
		}

	}
	private static final Logger logger = Logger.getLogger(ProgressWindow.class.getPackage().getName());

	private static ProgressWindow _instance;

	//protected WhiteLabel flexoLogo;

	protected ProgressBarLabel label;

	protected ProgressBarLabel mainProgressBarLabel;

	protected ProgressBarLabel secondaryProgressBarLabel;

	protected JProgressBar mainProgressBar;

	protected JProgressBar secondaryProgressBar;

	protected int mainProgress = 0;

	protected int secondaryProgress = 0;

	// protected boolean backgroundIsPainted = false;

	protected boolean isSecondaryProgressIndeterminate = true;
	ImageIcon icon;
	protected Frame initOwner;

	protected JPanel mainPane;

	public static ProgressWindow makeProgressWindow(String title, int steps)
	{
		if (_instance != null) {
			logger.warning("Invoke creation of new progress window while an other one is displayed. Using old one.");
		}
		else {
			_instance = new ProgressWindow(getActiveModuleFrame(),title, steps);
		}
		return _instance;
	}

	private ProgressWindow(Frame frameOwner, String title, int steps)
	{
		super(frameOwner = FlexoFrame.getOwner(frameOwner));
		setUndecorated(true);
		initOwner = frameOwner;
		//logger.info("Build progress max="+steps);
		setFocusable(false);
		mainProgress = 0;
		secondaryProgress = 0;
		mainProgressBar = new JProgressBar(0, steps);
		mainProgressBar.setIndeterminate(false);
		mainProgressBar.setStringPainted(false);
		mainProgressBar.setValue(mainProgress);
		secondaryProgressBar = new JProgressBar();
		secondaryProgressBar.setIndeterminate(isSecondaryProgressIndeterminate);
		secondaryProgressBar.setStringPainted(false);
		//flexoLogo = new WhiteLabel(IconLibrary.LOGIN_IMAGE);
		label = new ProgressBarLabel(title, SwingConstants.CENTER);
		label.setFont(FlexoCst.BIG_FONT);
		mainProgressBarLabel = new ProgressBarLabel("", SwingConstants.LEFT);
		mainProgressBarLabel.setFont(FlexoCst.NORMAL_FONT);
		
		secondaryProgressBarLabel = new ProgressBarLabel("", SwingConstants.LEFT);
		secondaryProgressBarLabel.setFont(FlexoCst.NORMAL_FONT);
		secondaryProgressBarLabel.setForeground(Color.DARK_GRAY);

		icon = IconLibrary.PROGRESS_BACKGROUND;
		mainPane = new JPanel()
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				//  Dispaly image at at full size
				g.drawImage(icon.getImage(), 0, 0, null);

				//  Scale image to size of component
				//				Dimension d = getSize();
				//				g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);

				//  Fix the image position in the scroll pane
				//				Point p = scrollPane.getViewport().getViewPosition();
				//				g.drawImage(icon.getImage(), p.x, p.y, null);

				super.paintComponent(g);
			}
		};
		mainPane.setLayout(null);
		mainPane.setOpaque(false);
		//panel.add(flexoLogo);
		mainPane.add(label);
		mainPane.add(mainProgressBarLabel);
		mainPane.add(mainProgressBar);
		mainPane.add(secondaryProgressBarLabel);
		mainPane.add(secondaryProgressBar);
		//flexoLogo.setBounds(135, 15, 230, 80);
		label.setBounds(50, /*115*/180, 510, 20);
		mainProgressBarLabel.setBounds(25, /*150*/205, 560, 15);
		mainProgressBar.setBounds(25, /*165*/220, 560, 15);
		secondaryProgressBarLabel.setBounds(25, /*200*/245, 560, 15);
		secondaryProgressBar.setBounds(25, /*215*/260, 560, 15);
		mainPane.setPreferredSize(new Dimension(610, 292));
		getContentPane().add(mainPane);
		setSize(610, 292);
		Dimension dim = null;
		if (getActiveModuleFrame()==null || !getActiveModuleFrame().isVisible()) {
			dim = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
		} else {
			centerOnFrame(getActiveModuleFrame());
		}
		pack();
		setVisible(true);
		toFront();
		paintImmediately();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Displaying progress window");
		}
	}

	protected static FlexoFrame getActiveModuleFrame()
	{
		return FlexoFrame.getActiveFrame();
	}

	private void paintImmediately()
	{
		if (ToolBox.getPLATFORM() != ToolBox.WINDOWS) {
			paintImmediately((JComponent) getContentPane());
			//paintImmediately(flexoLogo);
			paintImmediately(label);
			paintImmediately(mainProgressBarLabel);
			paintImmediately(mainProgressBar);
			paintImmediately(secondaryProgressBarLabel);
			paintImmediately(secondaryProgressBar);
		} else {
			paintImmediately(mainPane);
		}
	}

	public static void showProgressWindow(String title, int steps)
	{
		showProgressWindow(getActiveModuleFrame(), title, steps);
	}

	public static void showProgressWindow(Frame owner, String title, int steps)
	{
		if (_instance != null) {
			logger.warning("Try to open another ProgressWindow !!!!");
		} else {
			_instance = new ProgressWindow(owner, title, steps);
		}
	}

	public static void hideProgressWindow()
	{
		if (_instance != null) {
			_instance.hideWindow();
			_instance = null;
		}
	}

	@Override
	public void hideWindow()
	{
		setVisible(false);
		dispose();
		if (initOwner!=null) {
			initOwner.repaint();
		}
		_instance = null;
	}

	public static ProgressWindow instance()
	{
		return _instance;
	}

	public static boolean hasInstance()
	{
		return _instance != null;
	}

	@Override
	public void setProgress(String stepName)
	{
		//logger.info("Progress "+mainProgress+"/"+mainProgressBar.getMaximum());
		if (!isVisible()) {
			setVisible(true);
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Progress: " + stepName);
		}
		mainProgress++;
		mainProgressBar.setValue(mainProgress);
		mainProgressBarLabel.setText(stepName);
		isSecondaryProgressIndeterminate = true;
		secondaryProgressBar.setIndeterminate(true);
		secondaryProgressBarLabel.setText("");
		//paintImmediately(flexoLogo);
		paintImmediately(label);
		paintImmediately(mainProgressBarLabel);
		paintImmediately(mainProgressBar);
		paintImmediately(secondaryProgressBarLabel);
		paintImmediately(secondaryProgressBar);
		//toFront();
	}

	@Override
	public void resetSecondaryProgress(int steps)
	{
		isSecondaryProgressIndeterminate = false;
		secondaryProgressBar.setIndeterminate(false);
		secondaryProgressBar.setMinimum(0);
		secondaryProgressBar.setMaximum(steps);
		secondaryProgress = 1;
		secondaryProgressBar.setValue(secondaryProgress);
		paintImmediately(secondaryProgressBarLabel);
		paintImmediately(secondaryProgressBar);
		//toFront();
	}

	@Override
	public void setSecondaryProgress(String stepName)
	{
		secondaryProgress++;
		secondaryProgressBar.setValue(secondaryProgress);
		secondaryProgressBarLabel.setText(stepName);
		if (secondaryProgress == secondaryProgressBar.getMaximum()) {
			secondaryProgressBar.setIndeterminate(true);
			secondaryProgressBarLabel.setText("");
		}
		paintImmediately(secondaryProgressBarLabel);
		paintImmediately(secondaryProgressBar);
	}

	public static void setProgressInstance(String stepName)
	{
		if (instance() != null) {
			if (!instance().isVisible()) {
				instance().setVisible(true);
			}
			instance().setProgress(stepName);
		}
	}

	public static void resetSecondaryProgressInstance(int steps)
	{
		if (instance() != null) {
			instance().resetSecondaryProgress(steps);
		}
	}

	public static void setSecondaryProgressInstance(String stepName)
	{
		if (instance() != null) {
			instance().setSecondaryProgress(stepName);
		}
	}

	protected void paintImmediately(final JComponent component)
	{
		if (!SwingUtilities.isEventDispatchThread()) {
			/*Thread t = new Thread() {

				public void run() {
					try {*/
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					paintImmediately(component);
				}
			});
			/*} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				};
			};
			t.start();*/
			/*try {
				t.join(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			return;
		}
		try {
			if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
				/*
				 * if ((Frame.getFrames().length < 2 || !backgroundIsPainted) && component != getContentPane()) {
				 * mainPane.paintImmediately(mainPane.getBounds()); backgroundIsPainted = true; } else { }
				 */
				mainPane.paintImmediately(mainPane.getBounds());
			} else {
				Rectangle r = component.getBounds();
				r.x = 0;
				r.y = 0;
				component.paintImmediately(r);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param flexoFrame
	 */
	public void centerOnFrame(FlexoFrame frame)
	{
		if (frame==null) {
			return;
		}
		Dimension dim = new Dimension(frame.getLocationOnScreen().x+frame.getWidth()/2,
				frame.getLocationOnScreen().y+frame.getHeight()/2);
		setLocation(dim.width - getSize().width / 2, dim.height - getSize().height / 2);
	}
}
