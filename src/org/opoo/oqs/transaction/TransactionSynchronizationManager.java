/*
 * $Id$
 *
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.opoo.oqs.transaction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Central helper that manages resources and transaction synchronizations per thread.
 * To be used by resource management code but not by typical application code.
 *
 * <p>Supports one resource per key without overwriting, i.e. a resource needs
 * to be removed before a new one can be set for the same key.
 * Supports a list of transaction synchronizations if synchronization is active.
 *
 * <p>Resource management code should check for thread-bound resources, e.g. JDBC
 * Connections or Hibernate Sessions, via <code>getResource</code>. Such code is
 * normally not supposed to bind resources to threads, as this is the responsiblity
 * of transaction managers. A further option is to lazily bind on first use if
 * transaction synchronization is active, for performing transactions that span
 * an arbitrary number of resources.
 *
 * @author Juergen Hoeller
 * @author Alex Lin(alex@opoo.org)
 * @version 1.0
 */
public abstract class TransactionSynchronizationManager {
    private static final Log logger = LogFactory.getLog(
            TransactionSynchronizationManager.class);

    private static final ThreadLocal resources = new ThreadLocal();

    public TransactionSynchronizationManager() {
        super();
    }

    //-------------------------------------------------------------------------
    // Management of transaction-associated resource handles
    //-------------------------------------------------------------------------

    /**
     * Return all resources that are bound to the current thread.
     * <p>Mainly for debugging purposes. Resource managers should always invoke
     * hasResource for a specific resource key that they are interested in.
     * @return Map with resource keys and resource objects,
     * or empty Map if currently none bound
     * @see #hasResource
     */
    public static Map getResourceMap() {
        Map map = (Map) resources.get();
        if (map == null) {
            map = new HashMap();
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * Check if there is a resource for the given key bound to the current thread.
     * @param key key to check
     * @return if there is a value bound to the current thread
     */
    public static boolean hasResource(Object key) {
        Map map = (Map) resources.get();
        return (map != null && map.containsKey(key));
    }

    /**
     * Retrieve a resource for the given key that is bound to the current thread.
     * @param key key to check
     * @return a value bound to the current thread, or null if none
     */
    public static Object getResource(Object key) {
        Map map = (Map) resources.get();
        if (map == null) {
            return null;
        }
        Object value = map.get(key);
        if (value != null && logger.isDebugEnabled()) {
            logger.debug("Retrieved value [" + value + "] for key [" + key +
                         "] bound to thread [" +
                         Thread.currentThread().getName() + "]");
        }
        return value;
    }

    /**
     * Bind the given resource for the given key to the current thread.
     * @param key key to bind the value to
     * @param value value to bind
     * @throws IllegalStateException if there is already a value bound to the thread
     */
    public static void bindResource(Object key, Object value) throws
            IllegalStateException {
        Map map = (Map) resources.get();
        // set ThreadLocal Map if none found
        if (map == null) {
            map = new HashMap();
            resources.set(map);
        }
        if (map.containsKey(key)) {
            throw new IllegalStateException("Already value [" + map.get(key) +
                                            "] for key [" + key +
                                            "] bound to thread [" +
                                            Thread.currentThread().getName() +
                                            "]");
        }
        map.put(key, value);
        if (logger.isDebugEnabled()) {
            logger.debug("Bound value [" + value + "] for key [" + key +
                         "] to thread [" +
                         Thread.currentThread().getName() + "]");
        }
    }

    /**
     * Unbind a resource for the given key from the current thread.
     * @param key key to check
     * @return the previously bound value
     * @throws IllegalStateException if there is no value bound to the thread
     */
    public static Object unbindResource(Object key) throws
            IllegalStateException {
        Map map = (Map) resources.get();
        if (map == null || !map.containsKey(key)) {
            throw new IllegalStateException(
                    "No value for key [" + key + "] bound to thread [" +
                    Thread.currentThread().getName() + "]");
        }
        Object value = map.remove(key);
        // remove entire ThreadLocal if empty
        if (map.isEmpty()) {
            resources.set(null);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Removed value [" + value + "] for key [" + key +
                         "] from thread [" +
                         Thread.currentThread().getName() + "]");
        }
        return value;
    }

}
