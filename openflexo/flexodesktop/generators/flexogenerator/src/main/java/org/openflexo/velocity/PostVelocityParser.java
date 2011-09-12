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
package org.openflexo.velocity;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class PostVelocityParser
{
    private static final Logger logger = FlexoLogger.getLogger(PostVelocityParser.class.getPackage().getName());

    private static final String TAG_END = "end";

    private static final String[] TAGS = new String[] { "single", "onereturn", TAG_END };

    private static final String TAG_START = "@";

    public static String parseAndRenderCustomTag(String s)
    {
        try {
            Stack<String> tags = new Stack<String>();
            Stack<String> output = new Stack<String>();
            int index = 0, next;
            String nextTag = findNextTag(s, index);
            boolean lastMatchWasEndTag = false;
            if (nextTag == null)
                return s;
            while (nextTag != null) {
                next = s.indexOf(TAG_START + nextTag, index);
                if (lastMatchWasEndTag)
                    output.push(output.pop() + s.substring(index, next));
                else
                    output.push(s.substring(index, next));
                lastMatchWasEndTag = false;
                if (nextTag.equals(TAG_END)) {
                    int tmp = s.indexOf('\n', next);
                    if (s.charAt(next+nextTag.length()+1)=='@' && tmp>-1 && tmp<s.length()-1)
                        index = tmp+1;
                    else
                        index = next + nextTag.length() + 1;
                    lastMatchWasEndTag = true;
                    String tagToRender = tags.pop();
                    String middleText = output.pop();
                    output.push(output.pop() + render(tagToRender, middleText));
                } else {
                    tags.push(nextTag);
                    index = next + nextTag.length() + 1;
                    if (s.indexOf('\n',index)>-1)
                        if (s.substring(index, s.indexOf('\n',index)).trim().length()==0)
                            index = s.indexOf('\n',index)+1;
                }
                
                nextTag = findNextTag(s, index);
            }
            return output.firstElement() + s.substring(index);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * @param tagToRender
     * @param middleText
     * @return
     */
    private static String render(String tagToRender, String middleText)
    {
        if (tagToRender.equals(TAGS[0])) {
            return single(middleText);
        } else if (tagToRender.equals(TAGS[1]))
            return onereturn(middleText);
        if (logger.isLoggable(Level.WARNING))
            logger.warning("Unknown tag: " + tagToRender);
        return null;
    }

    /**
     * @param middleText
     * @return
     */
    private static String onereturn(String middleText)
    {
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile("\\s*?\n\\s*").matcher(middleText);
        while(m.find()) {
            m.appendReplacement(sb, "\n");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param middleText
     * @return
     */
    private static String single(String middleText)
    {
        return middleText.replaceAll("\\s+", " ");
    }

    /**
     * @param index
     * @return
     */
    private static String findNextTag(String input, int index)
    {
        int next = input.indexOf(TAG_START, index);
        while (next > -1) {
            for (String tag : TAGS) {
                if (input.regionMatches(next + 1, tag, 0, tag.length()))
                    return tag;
            }
            next = input.indexOf(TAG_START, next+1);
        }
        return null;
    }

    public static void main(String[] args)
    {
        String text = "    private static BIMeetingData createPage(WOContext context, Boolean isProviderSelection, Boolean isTOC, BIMeeting meeting,\r\n" + 
                "        String selectedTab, long ciID) {\r\n" + 
                "        // TODO: check arguments of this method\r\n" +
                "@onereturn\r\n" + 
                "        BIMeetingData nextPage = (BIMeetingData) WOApplication.application().pageWithName(\"BIMeetingData\", context);\r\n" + 
                "        nextPage.setSelected_BIMeetingDataTabs_Tab(selectedTab);\r\n" + 
                "        nextPage.isProviderSelection = isProviderSelection;\r\n" + 
                "        nextPage.isTOC = isTOC;\r\n" + 
                "\r\n" + 
                "        if (meeting == null) {\r\n" + 
                "            meeting = BIMeeting.newBIMeeting();\r\n" + 
                "        }\r\n" + 
                "\r\n" + 
                "        nextPage.meeting = meeting;\r\n" + 
                "        nextPage._componentInstanceID = ciID;\r\n" + 
                "\r\n" + 
                "        return nextPage;\r\n" + 
                "    }\r\n" + 
                "@end\r\n";
        System.out.println(parseAndRenderCustomTag(text));
    }
}
