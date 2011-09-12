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
package org.openflexo.fib.model;

import javax.swing.ImageIcon;

import org.openflexo.fib.utils.FIBIconLibrary;



public class FIBLocalizedEntry extends FIBModelObject {

	private FIBLocalizedDictionary _dictionary;
	
	private String key;
	private String language;
	private String value;
	
	public FIBLocalizedEntry()
	{
	}
	
	public FIBLocalizedEntry(FIBLocalizedDictionary dictionary, String key,
			String language, String value)
	{
		super();
		_dictionary = dictionary;
		this.key = key;
		this.language = language;
		this.value = value;
	}

	public void setLocalizedDictionary(FIBLocalizedDictionary dict) 
	{
		_dictionary = dict;
	}

	public FIBLocalizedDictionary getLocalizedDictionary() 
	{
		return _dictionary;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getInspectorName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ImageIcon getIcon()
	{
		return FIBIconLibrary.LOCALIZATION_ICON;
	}

	@Override
	public FIBComponent getRootComponent()
	{
		return getLocalizedDictionary().getRootComponent();
	}

}
