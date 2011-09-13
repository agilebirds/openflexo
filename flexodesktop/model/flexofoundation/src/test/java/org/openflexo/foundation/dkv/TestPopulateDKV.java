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

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dkv.Language;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.toolbox.FileUtils;


public class TestPopulateDKV extends DKVTestCase {

	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	private FlexoEditor _editor;
	private FlexoProject _project;

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test0CreateProject()
	{
		_editor = createProject("DKVTest");
		_project = _editor.getProject();
		Domain d1 = createDomain("d1", _editor);
		Language l1 = createLanguage("l1", _editor);
		l1.setIsoCode("FR");
		Language l2 = createLanguage("l2", _editor);
		l2.setIsoCode("EN");

		Hashtable<String, Language> values = new Hashtable<String, Language>();
		values.put("K1_en_français", l1);
		values.put("K1_en_anglais", l2);
		Key k1 = createKey(d1, "K1", values, _editor);

		values.clear();
		values.put("K2_en_français", l1);
		values.put("K2_en_anglais", l2);
		Key k2 = createKey(d1, "K2", values, _editor);
		assertNotSame(k1, k2);
		assertEquals(k1.getDomain(), k2.getDomain());
		assertEquals(k1.getDomain(), d1);
		assertEquals(d1.getValue(k1, l1).getValue(), "K1_en_français");
		assertEquals(d1.getValue(k1, l2).getValue(), "K1_en_anglais");
		assertEquals(d1.getValue(k2, l1).getValue(), "K2_en_français");
		assertEquals(d1.getValue(k2, l2).getValue(), "K2_en_anglais");

		saveProject(_project);
		_editor = reloadProject(_project.getProjectDirectory());
		if (_project!=null)
			_project.close();
		_project = _editor.getProject();
		d1 = null;
		l1 = null;
		l2 = null;
		k1 = null;
		k2 = null;

		d1 = _project.getDKVModel().getDomainNamed("d1");
		assertNotNull(d1);
		l1 = _project.getDKVModel().getLanguageNamed("l1");
		assertNotNull(l1);
		l2 = _project.getDKVModel().getLanguageNamed("l2");
		assertNotNull(l2);
		k1 = d1.getKeyNamed("K1");
		assertNotNull(k1);
		k2 = d1.getKeyNamed("K2");
		assertNotNull(k2);


		assertEquals(d1.getValue(k1, l1).getValue(), "K1_en_français");
		assertEquals(d1.getValue(k1, l2).getValue(), "K1_en_anglais");
		assertEquals(d1.getValue(k2, l1).getValue(), "K2_en_français");
		assertEquals(d1.getValue(k2, l2).getValue(), "K2_en_anglais");
		_project.close();
		FileUtils.deleteDir(_project.getProjectDirectory());
	}

	public TestPopulateDKV(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}
