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

import java.io.File;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.model.ModelContextLibrary;
import org.openflexo.model.converter.DataBindingConverter;
import org.openflexo.model.converter.RelativePathFileConverter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

/**
 * {@link ModelFactory} used to handle FIB models<br>
 * One instance is declared for the {@link FIBLibrary}
 * 
 * @author sylvain
 * 
 */
public class FIBModelFactory extends ModelFactory {

	public FIBModelFactory() throws ModelDefinitionException {
		super(ModelContextLibrary.getModelContext(FIBComponent.class));
		addConverter(new DataBindingConverter());
	}

	public FIBModelFactory(File relativePath) throws ModelDefinitionException {
		this();
		addConverter(new RelativePathFileConverter(relativePath));
	}

}
