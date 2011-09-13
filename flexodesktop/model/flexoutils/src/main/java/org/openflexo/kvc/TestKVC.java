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
package org.openflexo.kvc;

import java.util.Date;

import javax.swing.JPanel;

/**
 * @author sguerin
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TestKVC extends KVCObject
{

    private String testString = "init";

    private int testInt = 3;

    private Date testDate = new Date();

    private JPanel subObject = new JPanel();

    public Date date2 = new Date();

    public double unDouble = 9.8;

    public String getTestString()
    {
        System.out.println("getTestString");
        return testString;
    }

    public void setTestString(String aString)
    {
        System.out.println("setTestString with " + aString);
        testString = aString;
    }

    public int getTestInt()
    {
        System.out.println("getTestInt");
        return testInt;
    }

    public void setTestInt(int anInt)
    {
        System.out.println("setTestInt with " + anInt);
        testInt = anInt;
    }

    public Date getTestDate()
    {
        System.out.println("getTestDate");
        return testDate;
    }

    public void setTestDate(Date aDate)
    {
        System.out.println("setTestDate with " + aDate);
        testDate = aDate;
    }

    public JPanel getSubObject()
    {
        System.out.println("getSubObject");
        return subObject;
    }

    public void setSubObject(JPanel aSubObject)
    {
        System.out.println("setSubObject with " + aSubObject);
        subObject = aSubObject;
    }

    public static void main(String[] args)
    {
        TestKVC test = new TestKVC();
        test.setValueForKey("test1", "testString");
        System.out.println("Result=" + test.valueForKey("testString"));
        test.setValueForKey("7", "testInt");
        System.out.println("Result=" + test.valueForKey("testInt"));
        test.setValueForKey("18.789", "unDouble");
        System.out.println("Result=" + test.valueForKey("unDouble"));
        test.setIntegerValueForKey(9, "testInt");
        System.out.println("Result=" + test.integerValueForKey("testInt"));
        test.setObjectForKey(new Date(), "testDate");
        System.out.println("Result=" + test.objectForKey("testDate"));
        test.setObjectForKey(new JPanel(), "subObject");
        System.out.println("Result=" + test.objectForKey("subObject"));
        // Raised an exception here, because 3 is not parsable as a Date
        test.setValueForKey("3", "date2");
    }
}
