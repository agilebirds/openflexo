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
package org.openflexo.jedit;
/*
 * TextAreaDefaults.java - Encapsulates default values for various settings
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openflexo.toolbox.Localized;


/**
 * Encapsulates default settings for a text area. This can be passed
 * to the constructor once the necessary fields have been filled out.
 * The advantage of doing this over calling lots of set() methods after
 * creating the text area is that this method is faster.
 */
public class TextAreaDefaults
{
	private static TextAreaDefaults DEFAULTS;

	public InputHandler inputHandler;
	public SyntaxDocument document;
	public boolean editable;

	public boolean caretVisible;
	public boolean caretBlinks;
	public boolean blockCaret;
	public int electricScroll;

	public int cols;
	public int rows;
	public SyntaxStyle[] styles;
	public Color caretColor;
	public Color selectionColor;
	public Color lineHighlightColor;
	public boolean lineHighlight;
	public Color bracketHighlightColor;
	public boolean bracketHighlight;
	public Color eolMarkerColor;
	public boolean eolMarkers;
	public boolean paintInvalid;

	public JPopupMenu popup;

	/**
	 * Returns a new TextAreaDefaults object with the default values filled
	 * in.
	 */
	public static TextAreaDefaults getDefaults()
	{
		if(DEFAULTS == null)
		{
			DEFAULTS = new TextAreaDefaults();

			DEFAULTS.inputHandler = new DefaultInputHandler();
			DEFAULTS.inputHandler.addDefaultKeyBindings();
			DEFAULTS.document = new SyntaxDocument();
			DEFAULTS.editable = true;

			DEFAULTS.caretVisible = true;
			DEFAULTS.caretBlinks = true;
			DEFAULTS.electricScroll = 0;

			DEFAULTS.cols = 80;
			DEFAULTS.rows = 25;
			DEFAULTS.styles = SyntaxUtilities.getDefaultSyntaxStyles();
			DEFAULTS.caretColor = Color.red;
			DEFAULTS.selectionColor = new Color(0xccccff);
			DEFAULTS.lineHighlightColor = new Color(0xe0e0e0);
			DEFAULTS.lineHighlight = true;
			DEFAULTS.bracketHighlightColor = Color.black;
			DEFAULTS.bracketHighlight = true;
			DEFAULTS.eolMarkerColor = new Color(0x009999);
			DEFAULTS.eolMarkers = true;
			DEFAULTS.paintInvalid = true;
            DEFAULTS.popup = buildMenu();
		}

		return DEFAULTS;
	}
     // GPO Addition
    public static JPopupMenu buildMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(makeMenu("cut",InputHandler.CUT));
        popupMenu.add(makeMenu("copy",InputHandler.COPY));
        popupMenu.add(makeMenu("paste",InputHandler.PASTE));
        popupMenu.add(makeMenu("delete",InputHandler.DELETE));
        popupMenu.addSeparator();
        popupMenu.add(makeMenu("select_all", InputHandler.SELECT_ALL));
        popupMenu.addSeparator();
        popupMenu.add(makeMenu("disable_syntax_coloring", InputHandler.TOGGLE_SYNTAX_COLORING));
        return popupMenu;
    }
	
	/**
     * @param string
     * @param cut
     * @return
     */
    private static JMenuItem makeMenu(String string, ActionListener actionListener)
    {
        JMenuItem menu = new JMenuItem(Localized.localizedForKey(string));
        menu.addActionListener(actionListener);
        return menu;
    }
    // End of GPO Addition
    
    public static TextAreaDefaults getNewDefaults(){
		DEFAULTS = null;
		return getDefaults();
	}
}
