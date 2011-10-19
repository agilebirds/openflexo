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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.FlexoCst;
import org.openflexo.icon.IconLibrary;
import org.openflexo.module.UserType;
import org.openflexo.toolbox.ToolBox;

@SuppressWarnings("serial")
public class SplashWindow extends JWindow {

	private JLabel splash;

	public SplashWindow(Frame f, UserType userType, int waitTime) {
		super(f);

		Dimension imageDim = new Dimension(IconLibrary.SPLASH_IMAGE.getIconWidth(), IconLibrary.SPLASH_IMAGE.getIconHeight());

		//cree un label avec notre image
		splash = new JLabel(IconLibrary.SPLASH_IMAGE);

		//ajoute le label au panel
		getContentPane().setLayout(null);

		JLabel flexoLabel = new JLabel(IconLibrary.OPENFLEXO_TEXT_ICON, SwingConstants.RIGHT);
		flexoLabel.setForeground(FlexoCst.WELCOME_FLEXO_COLOR);
		flexoLabel.setBackground(Color.RED);
		flexoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		getContentPane().add(flexoLabel);
		flexoLabel.setBounds(319,142,231,59);

		JLabel businessLabel = new JLabel(userType.getBusinessName2(), SwingConstants.RIGHT);
		businessLabel.setForeground(FlexoCst.WELCOME_FLEXO_COLOR);
		businessLabel.setFont(new Font("SansSerif", Font.ITALIC, 18));
		getContentPane().add(businessLabel);
		businessLabel.setBounds(260,195,280,15);

		JLabel versionLabel = new JLabel("Version " + FlexoCst.BUSINESS_APPLICATION_VERSION+ " (build " + FlexoCst.BUILD_ID+")", SwingConstants.RIGHT);
		versionLabel.setForeground(Color.DARK_GRAY);
		versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		getContentPane().add(versionLabel);
		versionLabel.setBounds(260,215,280,15);

		JLabel urlLabel = new JLabel("<html><u>www.openflexo.com</u></html>", SwingConstants.RIGHT);
		urlLabel.addMouseListener(new MouseAdapter() {

			/**
			 * Overrides mouseEntered
			 * @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseEntered(MouseEvent e)
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			/**
			 * Overrides mouseEntered
			 * @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseExited(MouseEvent e)
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			/**
			 * Overrides mouseClicked
			 *
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseClicked(MouseEvent e)
			{
				ToolBox.openURL("http://www.openflexo.com");
			}
		});
		urlLabel.setForeground(new Color(180,150,200));
		urlLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
		getContentPane().add(urlLabel);
		urlLabel.setBounds(290,263,280,12);

		JLabel copyrightLabel = new JLabel("(c) Copyright Agile Birds sprl, 2011, all rights reserved", SwingConstants.RIGHT);
		copyrightLabel.setForeground(Color.DARK_GRAY);
		copyrightLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
		getContentPane().add(copyrightLabel);
		copyrightLabel.setBounds(290,277,280,12);

		getContentPane().add(splash);
		splash.setBounds(0,0,imageDim.width,imageDim.height);

		setPreferredSize(imageDim);

		pack();

		//centre le splash screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = splash.getPreferredSize();
		setLocation(screenSize.width / 2 - labelSize.width / 2,
				screenSize.height / 2 - labelSize.height / 2);

		//rend le splash screen invisible lorsque l'on clique dessus
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setVisible(false);
				dispose();
			}
		});

		//afin d'acceder à la valeur WaitTime
		final int pause = waitTime;

		//thread pour fermer le splash screen
		final Runnable closerRunner = new Runnable() {
			@Override
			public void run() {
				setVisible(false);
				dispose();
			}
		};


		Runnable waitRunner = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(pause);
					//lance le thread qui ferme le splash screen
					SwingUtilities.invokeAndWait(closerRunner);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		//affiche le splash screen
		setVisible(true);

		//lance le thread qui ferme le splash screen apres un certain temps
		Thread splashThread = new Thread(waitRunner, "SplashThread");
		//setAlwaysOnTop(true);
		splashThread.start();
	}
	/*
    private Image fadingSplash = null;

    @Override
    public void paint(Graphics g) {
    	super.paint(g);
    	if (fadingSplash!=null) {
    		g.drawImage(fadingSplash, 0, 0, null);
    	}
    }

    @Override
    public void setVisible(boolean arg0) {
    	/*if (!arg0) {
    		getContentPane().removeAll();
    		if (getContentPane() instanceof JComponent) {
    			((JComponent)getContentPane()).setOpaque(false);
    		}
    		int steps =5;
    		for(int i=0;i<steps;i++) {
    			fadingSplash = getSplashImage((steps-i)/steps);
    			if (getContentPane() instanceof JComponent) {
        			((JComponent)getContentPane()).paintImmediately(getContentPane().getBounds());
        		} else {
        			repaint();
        		}
    			try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	} else
    		super.setVisible(arg0);
    }

    public Image getSplashImage(float alpha)
    {
    	BufferedImage image = new BufferedImage(splash.getWidth(), splash.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE);//buffered image reference passing the label's ht and width
        Graphics2D graphics = image.createGraphics();//creating the graphics for buffered image
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));   //Sets the Composite for the Graphics2D context
        splash.setOpaque(false);
        splash.print(graphics); //painting the graphics to label
        graphics.dispose();
        return image;
    }*/
}