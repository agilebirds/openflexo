/**
 * Metaphase Editor - WYSIWYG HTML Editor Component
 * Copyright (C) 2010  Rudolf Visagie
 * Full text of license can be found in com/metaphaseeditor/LICENSE.txt
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author can be contacted at metaphase.editor@gmail.com.
 */

package com.metaphaseeditor;

import java.io.File;

import org.openflexo.toolbox.FileResource;

/**
 *
 * @author Rudolf Visagie
 */
public enum SpellCheckDictionaryVersion {
    STRICT_UK("Spellcheck/dictionary/eng_uk_strict.zip"),
    LIBERAL_UK("Spellcheck/dictionary/eng_uk_liberal.zip"),
    STRICT_US("Spellcheck/dictionary/eng_us_strict.zip"),
    LIBERAL_US("Spellcheck/dictionary/eng_us_liberal.zip"),
    CUSTOM(null);

    private String filename;

    SpellCheckDictionaryVersion(String filename) {
        this.filename = filename;
    }

    public File getFile()
    {
        return new FileResource(filename);
    }

    @Override
    public String toString() 
    {
        return filename;
    }
}
