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
package org.openflexo.view.palette;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.icon.IconLibrary;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.ModuleLoader;
import org.openflexo.view.controller.FlexoController;


/**
 * Abstract definition of a palette that will be used in a particular module
 * Contains dragging components and supports drag operations
 * 
 * @author sguerin
 */
public abstract class FlexoPalette extends JPanel implements ChangeListener
{
    private FlexoController _controller;

    protected static final Logger logger = Logger.getLogger(FlexoPalette.class.getPackage().getName());

    protected PaletteTabbedPane currentTabbedPane;

    protected JPanel controlPanel = null;

    protected FlexoPalette(FlexoController controller)
    {
        super();
        _controller = controller;
        _controller.setPalette(this);
        setLayout(new BorderLayout());
        // setBackground(FlexoCst.GUI_BACK_COLOR);

        if (logger.isLoggable(Level.FINE))
            logger.fine("Constructor for FlexoPalette");

        currentTabbedPane = makeTabbedPane();
        if (currentTabbedPane.getComponentCount()>0)
        	currentTabbedPane.setSelectedIndex(0);
        currentTabbedPane.setName("Mon tableau d'onglets");
        add(currentTabbedPane, BorderLayout.CENTER);

       /* Dimension preferredSize = new Dimension(0,0);
        for (PalettePanel c : currentTabbedPane.getPalettes()) {
        	logger.info("Component "+c.getClass().getSimpleName()+" preferred size="+c.getPreferredSize());
        	if (c.getPreferredSize().width > preferredSize.width) preferredSize.width=c.getPreferredSize().width;
           	if (c.getPreferredSize().height > preferredSize.height) preferredSize.height=c.getPreferredSize().height;
        }*/
        
       if (ModuleLoader.isMaintainerRelease()&&handlesPaletteEdition()) {
            controlPanel = makeControlPanel();
            add(controlPanel, BorderLayout.SOUTH);
            //preferredSize.height += controlPanel.getPreferredSize().height;
        }

        updateControlButtons();

        //logger.info("Sets preferred size to be "+preferredSize);
        //setPreferredSize(preferredSize);
        
    }

    public void selectTab(int tabIndex){
    	if(currentTabbedPane!=null){
    		currentTabbedPane.setSelectedIndex(tabIndex);
    	}
    }
    public FlexoController getController()
    {
        return _controller;
    }

    public FlexoModule getModule()
    {
        return getController().getModule();
    }

    private JButton addPaletteButton;

    private JButton editPaletteButton;

    private JButton savePaletteButton;

    private JButton closeEditionButton;

    protected JPanel makeControlPanel()
    {
        JPanel answer = new JPanel(new FlowLayout());
        addPaletteButton = new JButton(IconLibrary.ADD_PALETTE_ICON);
        addPaletteButton.setDisabledIcon(IconLibrary.ADD_PALETTE_DISABLED_ICON);
        addPaletteButton.setBorder(BorderFactory.createEmptyBorder());
        addPaletteButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                addPalette();
            }
        });
        editPaletteButton = new JButton(IconLibrary.EDIT_PALETTE_ICON);
        editPaletteButton.setDisabledIcon(IconLibrary.EDIT_PALETTE_DISABLED_ICON);
        editPaletteButton.setBorder(BorderFactory.createEmptyBorder());
        editPaletteButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                editPalette();
            }
        });
        savePaletteButton = new JButton(IconLibrary.SAVE_PALETTE_ICON);
        savePaletteButton.setDisabledIcon(IconLibrary.SAVE_PALETTE_DISABLED_ICON);
        savePaletteButton.setBorder(BorderFactory.createEmptyBorder());
        savePaletteButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                savePalette();
            }
        });
        closeEditionButton = new JButton(IconLibrary.CLOSE_EDITION_ICON);
        closeEditionButton.setDisabledIcon(IconLibrary.CLOSE_EDITION_DISABLED_ICON);
        closeEditionButton.setBorder(BorderFactory.createEmptyBorder());
        closeEditionButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                closePaletteEdition();
            }
        });
        answer.add(addPaletteButton);
        answer.add(editPaletteButton);
        answer.add(savePaletteButton);
        answer.add(closeEditionButton);
        
        /*Dimension preferredSize = new Dimension(100,40);
        answer.setPreferredSize(preferredSize);*/
        
        return answer;
    }

    protected void addPalette()
    {
        updateControlButtons();
    }

    protected void editPalette()
    {
        PalettePanel current = getCurrentPalettePanel();
        if (current != null) {
            current.editPalette();
        }
        updateControlButtons();
    }

    protected void savePalette()
    {
        PalettePanel current = getCurrentPalettePanel();
        if (current != null) {
            current.savePalette();
        }
        updateControlButtons();
    }

    protected void closePaletteEdition()
    {
        PalettePanel current = getCurrentPalettePanel();
        if (current != null) {
            current.closePaletteEdition();
        }
        updateControlButtons();
    }

    protected void updateControlButtons()
    {
        if (controlPanel != null) {
            PalettePanel current = getCurrentPalettePanel();
            if (current != null) {
                addPaletteButton.setEnabled(false);
                editPaletteButton.setEnabled(!current.isEdited());
                savePaletteButton.setEnabled(current.isEdited());
                closeEditionButton.setEnabled(current.isEdited());
            }
        }
    }
    
    public abstract boolean handlesPaletteEdition();

    public PalettePanel getCurrentPalettePanel()
    {
        return (PalettePanel) (((JScrollPane) currentTabbedPane.getSelectedComponent()).getViewport().getView());
    }

    protected abstract PaletteTabbedPane makeTabbedPane();

    @Override
	public void stateChanged(ChangeEvent e)
    {
        logger.fine("Changed for " + getCurrentPalettePanel().getName());
        updateControlButtons();
    }

    protected class PaletteTabbedPane extends JTabbedPane
    {
    	private Vector<PalettePanel> palettes;
    	
        public PaletteTabbedPane()
        {
            super();
            setFocusable(false);
            palettes = new Vector<PalettePanel>();
        }

        // All palettes are put in JScrollPane
        @Override
		public Component add(Component component)
        {
            if (component instanceof PalettePanel) {
            	palettes.add((PalettePanel)component);
                JScrollPane scrollPane = new JScrollPane(component);
                scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                super.add(scrollPane, component.getName());
                return scrollPane;
            } else {
                logger.warning("Inserted tab in panel must be PalettePanel instances !");
                super.add(component,"???");
                return null;
            }
        }

        @Override
		public void remove(Component component)
        {
            if (component instanceof PalettePanel) {
                Component cs[] = getComponents();
                for (int i = 0; i < cs.length; i++) {
                    JScrollPane p = (JScrollPane) cs[i];
                    if (p.getViewport() == component.getParent()) {
                        super.remove(p);
                        break;
                    }
                }
            }
        }

		public Vector<PalettePanel> getPalettes() 
		{
			return palettes;
		}
    }

}
