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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

import org.openflexo.jedit.JEditTextArea;
import org.openflexo.jedit.TextAreaDefaults;
import org.openflexo.jedit.TextAreaPainter;
import org.openflexo.toolbox.FontCst;


public class JConsole extends JEditTextArea
{
    static final Logger logger = Logger.getLogger(JConsole.class.getPackage().getName());
    
    private boolean refreshOnlyInSwingEventDispatchingThread=true;

    public JConsole()
    {
        super();
        setTokenMarker(null);
        painter.setEOLMarkersPainted(false);
        painter.setInvalidLinesPainted(false);
        setFont(FontCst.CODE_FONT);
        setEditable(false);
    }

    public void clear()
    {
        setText("");
        colors.clear();
        refresh();
    }

    Vector<Color> colors = new Vector<Color>();

    public void log(String log, Color color)
    {
        int i = 0;
        int nextNL = 0;
        int nlCount = 0;
        while (nextNL > -1) {
            nextNL = log.indexOf("\n", i);
            if (nextNL > -1) {
                nlCount++;
                i = nextNL + 1;
            }
        }
        try {
            document.insertString(document.getLength(), log, null);
        } catch (BadLocationException e) {
            logger.warning("BadLocationException");
        }
        if (!log.endsWith("\n")) {
            try {
                document.insertString(document.getLength(), "\n", null);
            } catch (BadLocationException e) {
                logger.warning("BadLocationException");
            }
            nlCount++;
        }
        for (int j = 0; j < nlCount; j++)
            colors.add(color);
        if (colors.size() % paintOccurences == 0)
            refresh();
    }

    void refresh()
    {
        if (!SwingUtilities.isEventDispatchThread() && getRefreshOnlyInSwingEventDispatchingThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                /**
                 * Overrides run
                 * 
                 * @see java.lang.Runnable#run()
                 */
                @Override
				public void run()
                {
                    JConsole.this.refresh();
                }
            });
            return;
        }
        setCaretPosition(getDocument().getLength());
        paintImmediately(getVisibleRect());
    }

    private int paintOccurences = 10;

    @Override
	protected TextAreaPainter initTextAreaPainter(JEditTextArea textArea, TextAreaDefaults defaults)
    {
        return new JConsoleTextAreaPainter(this, defaults);
    }

    public class JConsoleTextAreaPainter extends TextAreaPainter
    {
        public JConsoleTextAreaPainter(JEditTextArea textArea, TextAreaDefaults defaults)
        {
            super(textArea, defaults);
        }

        @Override
		protected void paintPlainLine(Graphics gfx, int line, Font defaultFont, Color defaultColor, int x, int y)
        {
            if (line < colors.size())
                defaultColor = colors.elementAt(line);

            paintHighlight(gfx, line, y);
            textArea.getLineText(line, currentLine);

            gfx.setFont(defaultFont);
            gfx.setColor(defaultColor);

            y += fm.getHeight();
            x = Utilities.drawTabbedText(currentLine, x, y, gfx, this, 0);

            if (eolMarkers) {
                gfx.setColor(eolMarkerColor);
                gfx.drawString(".", x, y);
            }
        }

    }

    public boolean getRefreshOnlyInSwingEventDispatchingThread()
    {
        return refreshOnlyInSwingEventDispatchingThread;
    }

    public void setRefreshOnlyInSwingEventDispatchingThread(boolean refreshOnlyInSwingEventDispatchingThread)
    {
        this.refreshOnlyInSwingEventDispatchingThread = refreshOnlyInSwingEventDispatchingThread;
    }

}
