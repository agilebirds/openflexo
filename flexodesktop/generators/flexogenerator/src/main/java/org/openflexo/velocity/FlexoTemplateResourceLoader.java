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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.generator.exception.TemplateNotFoundException;


public class FlexoTemplateResourceLoader extends ResourceLoader {

	private class _InputStream extends InputStream {
		
		int pushBack=-1;
		boolean newLine = true;
		InputStream is;
		
		public _InputStream(InputStream wrappedStream) {
			this.is = new BufferedInputStream(wrappedStream);
		}
		
		@Override
		public int read() throws IOException {
			if (newLine) {
				newLine = false;
				// We mark the input stream
			    is.mark(Integer.MAX_VALUE);
				int c = is.read();
				// We skip all the white characters (tab, newlines, spaces, ...)
				while(Character.isWhitespace(c)) {
					c = is.read();
				}
				// If we have a '#' sign, then we return it
				if (c=='#') {
					return c;
				} else {
					// Else we reset the input stream and let the normal flow take over
					is.reset();
				}
			}
			int c = pushBack;

			if (c != -1) {
			    pushBack = -1;
			} else {
			    c = is.read();
			}

			switch (c) {
			  case '\r':
				// OK, we got a '\r', so let's see ahead if we don't find a '\n'
			    pushBack = is.read();
			    if (pushBack == '\n') {
			    	// 1. We have found the expected '\n', so we return it (because in Flexo '\r' are not handled in memory)
				    pushBack = -1;
			    }// 2. We haven't found the expected '\n', and so we will still return a '\n' this time but the next time we are called we will return the char we just read.
			    newLine = true;
			    return '\n';
			  case '\n':
				newLine = true;
			    return '\n';
			}
			return c;
		}
		
		@Override
		public void close() throws IOException {
			super.close();
			if (is!=null)
				is.close();
		}
		
	}
	
	private TemplateLocator getTemplateLocator() throws ResourceNotFoundException {
		TemplateLocator locator = (TemplateLocator) rsvc.getApplicationAttribute("templateLocator");
		if (locator == null)
			throw new ResourceNotFoundException("Template locator not initialized for FlexoTemplateResourceLoader !");
		return locator;
	}
	
	private CGTemplate getTemplate(String name) throws ResourceNotFoundException {
		try {
			return getTemplateLocator().templateWithName(name);
		} catch (TemplateNotFoundException e) {
			throw new ResourceNotFoundException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getLastModified(Resource paramResource) {
		return getTemplate(paramResource.getName()).getLastUpdate().getTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(ExtendedProperties paramExtendedProperties) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSourceModified(Resource paramResource) {
		return getLastModified(paramResource) != paramResource.getLastModified();
	}

	@Override
	public InputStream getResourceStream(String templateName) throws ResourceNotFoundException {

		CGTemplate template = getTemplate(templateName);
		try {
			return new _InputStream(new ByteArrayInputStream(template.getContent().toString().getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
}
