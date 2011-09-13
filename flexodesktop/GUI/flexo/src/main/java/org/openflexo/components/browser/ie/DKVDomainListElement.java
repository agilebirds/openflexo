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
package org.openflexo.components.browser.ie;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.DKVModel.DomainList;


/**
 * @author gpolet
 *
 */
public class DKVDomainListElement extends BrowserElement
{

    /**
     * @param object
     * @param elementType
     * @param browser
     */
    public DKVDomainListElement(DKVModel.DomainList object,
            ProjectBrowser browser, BrowserElement parent)
    {
        super(object, BrowserElementType.DKV_DOMAIN_LIST, browser, parent);
    }

    /**
     * Overrides buildChildrenVector
     *
     * @see org.openflexo.components.browser.BrowserElement#buildChildrenVector()
     */
    @Override
	protected void buildChildrenVector()
    {
        DKVModel.DomainList dl = (DomainList) getObject();
        Vector<Domain> domains = (Vector<Domain>)dl.getDomains().clone();
        Collections.sort(domains, new Comparator<Domain>(){
            /**
             * Overrides compare
             * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
             */
            @Override
			public int compare(Domain o1, Domain o2)
            {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
        Enumeration<Domain> en = domains.elements();
        while (en.hasMoreElements()) {
            addToChilds(en.nextElement());
        }
    }

}
