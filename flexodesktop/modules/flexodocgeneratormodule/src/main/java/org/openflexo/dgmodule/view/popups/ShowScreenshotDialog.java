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
package org.openflexo.dgmodule.view.popups;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.openflexo.foundation.rm.ScreenshotResource;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoDialog;

/**
 * Popup showing a screenshot
 * 
 * @author sguerin
 * 
 */
public class ShowScreenshotDialog extends FlexoDialog
{

    private ScreenshotResource _screenshotResource;
     protected ScreenshotPanel screenshotPanel;
    
    public ShowScreenshotDialog(String labelString, ScreenshotResource screenshotResource, Frame owner)
    {
        super(owner);

        _screenshotResource = screenshotResource;
        
        String title = FlexoLocalization.localizedForKey("screenshot")+":"+screenshotResource.getFile().getName();
        setTitle(title);
        getContentPane().setLayout(new BorderLayout());

        JLabel label = new JLabel(labelString,SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        screenshotPanel = new ScreenshotPanel();
        screenshotPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JButton closeButton = new JButton(FlexoLocalization.localizedForKey("close"));
 
        closeButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                 dispose();
            }
        });
         controlPanel.add(closeButton);
         closeButton.setSelected(true);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        contentPanel.add(label, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(screenshotPanel), BorderLayout.CENTER);
        contentPanel.add(controlPanel, BorderLayout.SOUTH);

        getContentPane().add(contentPanel, BorderLayout.CENTER);
        getRootPane().setDefaultButton(closeButton);
        setModal(true);
        validate();
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2 - 100);
        setVisible(true);
    }

    private class ScreenshotPanel extends JPanel
    {
        private BufferedImage _image;
        
        private ScreenshotPanel()
        {
            super();
            _image = _screenshotResource.getScreenshotData().getImage();
            setPreferredSize(new Dimension(_image.getWidth(),_image.getHeight()));
        }
        
        @Override
		public void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            g.drawImage(_image,0,0,null);
        }
    }
    
}
