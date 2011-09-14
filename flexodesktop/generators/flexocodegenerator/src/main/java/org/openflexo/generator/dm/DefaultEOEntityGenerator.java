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
package org.openflexo.generator.dm;

import java.util.logging.Logger;

import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.EOEntityCodeGenerator;
import org.openflexo.generator.EOGenerator;
import org.openflexo.generator.GeneratorFormatter;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.JavaFormattingException;
import org.openflexo.logging.FlexoLogger;


public class DefaultEOEntityGenerator implements EOEntityCodeGenerator {

	private static final Logger logger = FlexoLogger.getLogger(DefaultEOEntityGenerator.class.getPackage().getName());
	private EOGenerator eoGenerator;

	public DefaultEOEntityGenerator(DMModel model) {
		try {
			eoGenerator = new EOGenerator(model.getProject());
		} catch (GenerationException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * This method is dynamically invoked from the DMModel class (installEOGenerators())
	 */
	public static void init(DMModel model) {
		DefaultEOEntityGenerator entityCodeGenerator = new DefaultEOEntityGenerator(model);
		model.setEOEntityCodeGenerator(entityCodeGenerator);
	}

	@Override
	public String generateCodeForEntity(final DMEOEntity entity)
	{
		String javaCode = null;
		try {
			GenericRecordGenerator generator = new GenericRecordGenerator(eoGenerator,entity);
			javaCode = generator.merge(generator.getTemplateName(),generator.defaultContext());
			try {
				javaCode = GeneratorFormatter.formatJavaCode(javaCode, "", entity.getName(), generator,entity.getProject());
			} catch (JavaFormattingException javaFormattingException) {
				logger.fine("javaFormattingException: "+javaFormattingException);
			}
		} catch (GenerationException e) {
			e.printStackTrace();
		}


		//logger.info("javaCode = "+javaCode);

		return javaCode;
	}

}
