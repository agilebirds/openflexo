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
package org.openflexo.foundation.sg;

import java.util.logging.Logger;

import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.cg.utils.TemplateRepositoryType;
import org.openflexo.foundation.rm.CustomTemplatesResource;

public class CustomSGTemplateRepository extends CustomCGTemplateRepository {

	private static final Logger logger = Logger.getLogger(CustomSGTemplateRepository.class.getPackage().getName());

	public CustomSGTemplateRepository(SGTemplates templates, CustomTemplatesResource resource) {
		super(templates, resource, null);
	}

	@Override
	public CommonSGTemplateSet makeCommonTemplateSet() {
		return new CommonSGTemplateSet(getDirectory(), this, true) {
			@Override
			public String getName() {
				return CustomSGTemplateRepository.this.getName();
			}
		};
	}

	@Override
	public TemplateRepositoryType getRepositoryType() {
		return TemplateRepositoryType.Code;
	}
}
