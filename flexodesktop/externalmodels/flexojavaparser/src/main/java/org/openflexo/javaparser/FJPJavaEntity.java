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
package org.openflexo.javaparser;

import java.util.logging.Logger;

import org.openflexo.foundation.dm.javaparser.ParsedJavaElement;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.JavaClassParent;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;

public abstract class FJPJavaEntity extends FJPJavaElement implements ParsedJavaElement {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FJPJavaEntity.class.getPackage().getName());

	private AbstractJavaEntity _qdJavaEntity;
	private JavadocItem _javadocItem;

	public FJPJavaEntity(AbstractJavaEntity qdJavaEntity, FJPJavaSource aJavaSource) {
		super(aJavaSource);
		_qdJavaEntity = qdJavaEntity;
	}

	@Override
	public JavadocItem getJavadoc() {
		if (_javadocItem == null
				&& (_qdJavaEntity.getComment() != null || _qdJavaEntity.getTags() != null && _qdJavaEntity.getTags().length > 0)) {
			_javadocItem = new JavadocItem(_qdJavaEntity.getComment(), _qdJavaEntity.getTags());
		}
		return _javadocItem;
	}

	public String getComment() {
		return _qdJavaEntity.getComment();
	}

	@Override
	public int getLineNumber() {
		return _qdJavaEntity.getLineNumber();
	}

	public abstract int getLinesCount();

	@Override
	public String[] getModifiers() {
		return _qdJavaEntity.getModifiers();
	}

	public JavaClassParent getParent() {
		return _qdJavaEntity.getParent();
	}

	public JavaSource getSource() {
		return _qdJavaEntity.getSource();
	}

	public boolean isAbstract() {
		return _qdJavaEntity.isAbstract();
	}

	public boolean isFinal() {
		return _qdJavaEntity.isFinal();
	}

	public boolean isNative() {
		return _qdJavaEntity.isNative();
	}

	public boolean isPrivate() {
		return _qdJavaEntity.isPrivate();
	}

	public boolean isProtected() {
		return _qdJavaEntity.isProtected();
	}

	public boolean isPublic() {
		return _qdJavaEntity.isPublic();
	}

	public boolean isStatic() {
		return _qdJavaEntity.isStatic();
	}

	public boolean isStrictfp() {
		return _qdJavaEntity.isStrictfp();
	}

	public boolean isSynchronized() {
		return _qdJavaEntity.isSynchronized();
	}

	public boolean isTransient() {
		return _qdJavaEntity.isTransient();
	}

	public boolean isVolatile() {
		return _qdJavaEntity.isVolatile();
	}

	protected static String getNonQualifiedName(Type aType) {
		String fullQualified = aType.toString();
		if (fullQualified.lastIndexOf(".") >= 0) {
			return fullQualified.substring(fullQualified.lastIndexOf(".") + 1);
		}
		return fullQualified;
	}
}
