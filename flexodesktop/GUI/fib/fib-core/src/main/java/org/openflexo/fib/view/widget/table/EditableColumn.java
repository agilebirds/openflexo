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
package org.openflexo.fib.view.widget.table;

import org.openflexo.antar.binding.BindingEvaluationContext;

/**
 * Represents an editable column in a table
 * 
 * @author sylvain
 * 
 * @param <T>
 *            type of row object beeing handled by this column
 * @param <V>
 *            type of value beeing managed by column's cells
 */
public interface EditableColumn<T, V> {

	public boolean isCellEditableFor(T object);

	public void setValueFor(T object, V value, BindingEvaluationContext evaluationContext);
}
