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
package org.openflexo.foundation.rm;

/**
 * Implemented by all data modifications which must be propagated through RM
 * scheme
 * 
 * @author sguerin
 * 
 */
public interface RMNotification
{

    /**
     * Return a flag indicating if the reception of this notification on an
     * unloaded resource must force this resource to be loaded and updated
     * according to the data contained in this notification. This method should
     * return true in the case of ForwardSynchronization (explicit
     * synchronization)
     * 
     * @return a boolean
     */
    public boolean forceUpdateWhenUnload();

    /**
     * Return a flag indicating if the computing of propagation tree for this
     * notification needs to be deep and pass across transitiv dependancies.
     * 
     * @return a boolean
     */
    public boolean isDeepNotification();

    /**
     * This method must be implemented according to choosen strategy relating to
     * the computing of propagation tree of this notification. It returns a flag
     * indicating if a 'isSynchronized' relationship between
     * 
     * <pre>
     * originResource
     * </pre>
     * 
     * and
     * 
     * <pre>
     * targetResource
     * </pre>
     * 
     * must lead to add
     * 
     * <pre>
     * targetResource
     * </pre>
     * 
     * to the propagation tree.
     * 
     * @param originResource:
     *            the origin resource of related 'isSynchronized' relationship
     * @param targetResource:
     *            the target resource of related 'isSynchronized' relationship
     * 
     * @return a boolean indicating if target resource must be added to
     *         notification's propagation tree
     */
    public boolean propagateToSynchronizedResource(FlexoResource originResource, FlexoResource targetResource);

    /**
     * This method must be implemented according to choosen strategy relating to
     * the computing of propagation tree of this notification. It returns a flag
     * indicating if a 'dependsOf' relationship between
     * 
     * <pre>
     * targetResource
     * </pre>
     * 
     * and
     * 
     * <pre>
     * originResource
     * </pre>
     * 
     * (eg a 'alters' relationship between
     * 
     * <pre>
     * originResource
     * </pre>
     * 
     * and
     * 
     * <pre>
     * targetResource
     * </pre>) must lead to add
     * 
     * <pre>
     * targetResource
     * </pre>
     * 
     * to the propagation tree.
     * 
     * @param originResource:
     *            the origin resource of related 'alters' relationship
     * @param targetResource:
     *            the target resource of related 'alters' relationship
     * 
     * @return a boolean indicating if target resource must be added to
     *         notification's propagation tree
     */
    public boolean propagateToAlteredResource(FlexoResource originResource, FlexoResource targetResource);

}
