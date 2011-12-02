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
package org.openflexo.foundation.dkv;

import java.util.Enumeration;
import java.util.Hashtable;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.dkv.action.AddDomainAction;
import org.openflexo.foundation.dkv.action.AddKeyAction;
import org.openflexo.foundation.dkv.action.AddLanguageAction;

public abstract class DKVTestCase extends FlexoTestCase {

	public DKVTestCase(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	protected Domain createDomain(String newDomainName, FlexoEditor editor) {
		AddDomainAction addDomain = AddDomainAction.actionType.makeNewAction(editor.getProject().getDKVModel(), null, editor);
		addDomain.setDkvModel(editor.getProject().getDKVModel());
		addDomain.setNewDomainDescription("description test");
		addDomain.setNewDomainName(newDomainName);
		addDomain.doAction();
		assertTrue(addDomain.hasActionExecutionSucceeded());
		assertNotNull(addDomain.getNewDomain());
		return addDomain.getNewDomain();
	}

	protected Language createLanguage(String newLanguageName, FlexoEditor editor) {
		AddLanguageAction addLanguage = AddLanguageAction.actionType.makeNewAction(editor.getProject().getDKVModel(), null, editor);
		addLanguage.setDkvModel(editor.getProject().getDKVModel());
		addLanguage.setLanguageName(newLanguageName);
		addLanguage.doAction();
		assertTrue(addLanguage.hasActionExecutionSucceeded());
		assertNotNull(addLanguage.getNewLanguage());
		return addLanguage.getNewLanguage();
	}

	protected Key createKey(Domain domain, String val, Hashtable<String, Language> values, FlexoEditor editor) {
		AddKeyAction addKey = AddKeyAction.actionType.makeNewAction(domain, null, editor);
		addKey.setDomain(domain);
		addKey.setKeyName(val);
		for (Enumeration<String> iter = values.keys(); iter.hasMoreElements();) {
			String element = iter.nextElement();
			addKey.setValueForLanguage(element, values.get(element));
		}
		addKey.doAction();
		assertTrue(addKey.hasActionExecutionSucceeded());
		assertNotNull(addKey.getNewKey());
		return addKey.getNewKey();
	}

}
