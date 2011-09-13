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
package org.openflexo.foundation.xml;

import java.awt.Color;

/**
 * This class converts a <CODE>String</CODE> to the HTML-format of a String.
 * <P>
 * To convert the <CODE>String</CODE>, each character is examined:
 * <UL>
 * <LI>ASCII-characters from 000 till 031 are represented as &amp;#xxx;<BR>
 * (with xxx = the value of the character)
 * <LI>ASCII-characters from 032 t/m 127 are represented by the character
 * itself, except for:
 * <UL>
 * <LI>'\n' becomes &lt;BR&gt;\n
 * <LI>&quot; becomes &amp;quot;
 * <LI>&amp; becomes &amp;amp;
 * <LI>&lt; becomes &amp;lt;
 * <LI>&gt; becomes &amp;gt;
 * </UL>
 * <LI>ASCII-characters from 128 till 255 are represented as &amp;#xxx;<BR>
 * (with xxx = the value of the character)
 * </UL>
 * <P>
 * Example:
 * <P>
 * <BLOCKQUOTE>
 * 
 * <PRE>
 * 
 * String htmlPresentation = HtmlEncoder.encode("Marie-Th&#233;r&#232;se
 * S&#248;rensen");
 * 
 * </PRE>
 * 
 * </BLOCKQUOTE>
 * <P>
 * for more info: see O'Reilly; "HTML: The Definitive Guide" (page 164)
 * 
 * @author mario.maccarini@rug.ac.be
 */

public class HtmlEncoder
{

    // membervariables

    /** List with the HTML translation of all the characters. */
    private static final String[] htmlCode = new String[256];

    static {
        for (int i = 0; i < 10; i++) {
            htmlCode[i] = "&#00" + i + ";";
        }

        for (int i = 10; i < 32; i++) {
            htmlCode[i] = "&#0" + i + ";";
        }

        for (int i = 32; i < 128; i++) {
            htmlCode[i] = String.valueOf((char) i);
        }

        // Special characters
        htmlCode['\"'] = "&quot;"; // double quote
        htmlCode['&'] = "&amp;"; // ampersand
        htmlCode['<'] = "&lt;"; // lower than
        htmlCode['>'] = "&gt;"; // greater than

        for (int i = 128; i < 256; i++) {
            htmlCode[i] = "&#" + i + ";";
        }
    }

    // constructors

    /**
     * This class will never be constructed.
     * <P>
     * HtmlEncoder only contains static methods.
     */

    private HtmlEncoder()
    {
        super();
    }

    // methods

    /**
     * Converts a <CODE>String</CODE> to the HTML-format of this <CODE>String</CODE>.
     * 
     * @param string
     *            The <CODE>String</CODE> to convert
     * @return a <CODE>String</CODE>
     */

    public static String encode(String string)
    {
        int n = string.length();
        char character;
        StringBuffer buffer = new StringBuffer();
        // loop over all the characters of the String.
        for (int i = 0; i < n; i++) {
            character = string.charAt(i);
            // the Htmlcode of these characters are added to a StringBuffer one
            // by one
            if (character < 256) {
                buffer.append(htmlCode[character]);
            } else {
                // Improvement posted by Joachim Eyrich
                buffer.append("&#").append((int) character).append(";");
            }
        }
        return buffer.toString().trim();
    }

    /**
     * Converts a <CODE>Color</CODE> into a HTML representation of this <CODE>Color</CODE>.
     * 
     * @param color
     *            the <CODE>Color</CODE> that has to be converted.
     * @return the HTML representation of this <COLOR>Color</COLOR>
     */

    public static String encode(Color color)
    {
        StringBuffer buffer = new StringBuffer("#");
        if (color.getRed() < 16) {
            buffer.append('0');
        }
        buffer.append(Integer.toString(color.getRed(), 16));
        if (color.getGreen() < 16) {
            buffer.append('0');
        }
        buffer.append(Integer.toString(color.getGreen(), 16));
        if (color.getBlue() < 16) {
            buffer.append('0');
        }
        buffer.append(Integer.toString(color.getBlue(), 16));
        return buffer.toString();
    }

    /**
     * Translates the alignment value.
     * 
     * @param alignment
     *            the alignment value
     * @return the translated value
     */
    /*
     * public static String getAlignment(int alignment) { switch(alignment) {
     * case Element.ALIGN_LEFT: return HtmlTags.ALIGN_LEFT; case
     * Element.ALIGN_CENTER: return HtmlTags.ALIGN_CENTER; case
     * Element.ALIGN_RIGHT: return HtmlTags.ALIGN_RIGHT; case
     * Element.ALIGN_JUSTIFIED: case Element.ALIGN_JUSTIFIED_ALL: return
     * HtmlTags.ALIGN_JUSTIFIED; case Element.ALIGN_TOP: return
     * HtmlTags.ALIGN_TOP; case Element.ALIGN_MIDDLE: return
     * HtmlTags.ALIGN_MIDDLE; case Element.ALIGN_BOTTOM: return
     * HtmlTags.ALIGN_BOTTOM; case Element.ALIGN_BASELINE: return
     * HtmlTags.ALIGN_BASELINE; default: return ""; } }
     */
}
