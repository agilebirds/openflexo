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
package org.openflexo.fps.automerge;

import java.awt.Point;
import java.util.logging.Logger;

import org.openflexo.diff.DiffSource;
import org.openflexo.diff.merge.AbstractAutomaticMergeResolvingRule;
import org.openflexo.diff.merge.MergeChange;
import org.openflexo.xmlcode.StringEncoder;


public abstract class XMLAutomaticMergeResolvingRule extends AbstractAutomaticMergeResolvingRule 
{
    private static final Logger logger = Logger.getLogger(XMLAutomaticMergeResolvingRule.class.getPackage().getName());

    private static final String QUOTE = "\"";	
	
	public static boolean isXMLAttributeValueConflict(MergeChange change, String attributeName) 
	{
		try {
			if ((change.getFirst0() == change.getLast0())
					&& (change.getFirst2() == change.getLast2())) {
				if ((relativeLeftTokenAt(change, -3).equals(attributeName))
						&& (relativeLeftTokenAt(change, -2).equals("="))
						&& (relativeLeftTokenAt(change, -1).equals(QUOTE))
						&& (relativeLeftTokenAt(change, 1).equals(QUOTE))
						&& (relativeRightTokenAt(change, -3).equals(attributeName))
						&& (relativeRightTokenAt(change, -2).equals("="))
						&& (relativeRightTokenAt(change, -1).equals(QUOTE))
						&& (relativeRightTokenAt(change, 1).equals(QUOTE))) {
					return true;
				}
			}			
		}
		catch (IndexOutOfBoundsException e) {
			// Return false
		}
		return false;
	}
	
	public static boolean isXMLValueConflict(MergeChange change, String elementName) 
	{
		try {
			if ((change.getFirst0() == change.getLast0())
					&& (change.getFirst2() == change.getLast2())) {
								
				if ((relativeLeftTokenAt(change, -3).equals("<"))
						&& (relativeLeftTokenAt(change, -2).equals(elementName))
						&& (relativeLeftTokenAt(change, -1).equals(">"))
						&& (relativeLeftTokenAt(change, 1).equals("<"))
						&& (relativeLeftTokenAt(change, 2).equals("/"))
						&& (relativeLeftTokenAt(change, 3).equals(elementName))
						&& (relativeLeftTokenAt(change, 4).equals(">"))
						&& (relativeRightTokenAt(change, -3).equals("<"))
						&& (relativeRightTokenAt(change, -2).equals(elementName))
						&& (relativeRightTokenAt(change, -1).equals(">"))
						&& (relativeRightTokenAt(change, 1).equals("<"))
						&& (relativeRightTokenAt(change, 2).equals("/"))
						&& (relativeRightTokenAt(change, 3).equals(elementName))
						&& (relativeRightTokenAt(change, 4).equals(">"))) {
					return true;
				}
			}			
		}
		catch (IndexOutOfBoundsException e) {
			// Return false
		}
		return false;
	}
	
	public static boolean isInsideAnXMLAttributeValueConflict(MergeChange change, String attributeName) 
	{
		if (extractContainerAttributeValueFromLeft(change,attributeName) == null) return false;
		if (extractContainerAttributeValueFromRight(change,attributeName) == null) return false;
		return true;
	}
	
	protected static String extractContainerAttributeValueFromLeft(MergeChange change, String attributeName)
	{
		int startTokenRelativeIndex = 0;
		boolean startIndexFound = false;
		while (!startIndexFound) {
			try {
				if (relativeLeftTokenAt(change, startTokenRelativeIndex).equals(QUOTE)) {
					startIndexFound = true;
				}
				else {
					startTokenRelativeIndex--;
				}
			}
			catch (IndexOutOfBoundsException e) {
				return null;
			}
		}
		
		int endTokenRelativeIndex = 0;
		boolean endIndexFound = false;
		while (!endIndexFound) {
			try {
				if (relativeLeftTokenAt(change, endTokenRelativeIndex).equals(QUOTE)) {
					endIndexFound = true;
				}
				else {
					endTokenRelativeIndex++;
				}
			}
			catch (IndexOutOfBoundsException e) {
				return null;
			}
		}

		try {
			if ((relativeLeftTokenAt(change, startTokenRelativeIndex-2).equals(attributeName))
					&& (relativeLeftTokenAt(change, startTokenRelativeIndex-1).equals("="))) {

				StringBuffer sb = new StringBuffer();
				DiffSource leftSource = change.getMerge().getLeftSource();
				for (int i=startTokenRelativeIndex+1; i<endTokenRelativeIndex; i++) {
					sb.append(leftSource.tokenAt(change.getFirst0()+i).getFullString());
				}
				//logger.info("extractContainerAttributeValueFromLeft="+sb.toString());
				return sb.toString();
			}
		}
		catch (IndexOutOfBoundsException e) {
			return null;
		}

		return null;
	}
	
	protected static String extractContainerAttributeValueFromRight(MergeChange change, String attributeName)
	{
		int startTokenRelativeIndex = 0;
		boolean startIndexFound = false;
		while (!startIndexFound) {
			try {
				if (relativeRightTokenAt(change, startTokenRelativeIndex).equals(QUOTE)) {
					startIndexFound = true;
				}
				else {
					startTokenRelativeIndex--;
				}
			}
			catch (IndexOutOfBoundsException e) {
				return null;
			}
		}
		
		int endTokenRelativeIndex = 0;
		boolean endIndexFound = false;
		while (!endIndexFound) {
			try {
				if (relativeRightTokenAt(change, endTokenRelativeIndex).equals(QUOTE)) {
					endIndexFound = true;
				}
				else {
					endTokenRelativeIndex++;
				}
			}
			catch (IndexOutOfBoundsException e) {
				return null;
			}
		}

		try {
			if ((relativeRightTokenAt(change, startTokenRelativeIndex-2).equals(attributeName))
					&& (relativeRightTokenAt(change, startTokenRelativeIndex-1).equals("="))) {

				StringBuffer sb = new StringBuffer();
				DiffSource rightSource = change.getMerge().getRightSource();
				for (int i=startTokenRelativeIndex+1; i<endTokenRelativeIndex; i++) {
					sb.append(rightSource.tokenAt(change.getFirst2()+i).getFullString());
				}
				//logger.info("extractContainerAttributeValueFromRight="+sb.toString());
				return sb.toString();
			}
		}
		catch (IndexOutOfBoundsException e) {
			return null;
		}

		return null;
	}
	
	public int getLeftIntAttributeValue(MergeChange change)
	{
		return StringEncoder.decodeAsInteger(relativeLeftTokenAt(change, 0));
	}

	public int getRightIntAttributeValue(MergeChange change)
	{
		return StringEncoder.decodeAsInteger(relativeRightTokenAt(change, 0));
	}

	public Point getRightPointAttributeValue(MergeChange change)
	{
		return StringEncoder.decodeObject(relativeRightTokenAt(change, 0),Point.class);
	}

	public Point getLeftPointAttributeValue(MergeChange change)
	{
		return StringEncoder.decodeObject(relativeLeftTokenAt(change, 0),Point.class);
	}

}
