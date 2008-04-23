/*
 * $Id: LongNumericGenerator.java 1.0 08-1-3 ����10:30 $
 *
 * Copyright 2005 the original author or authors.
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
package org.opoo.lang;

/**
 * Interface to guaranteed to return a long.
 *
 * @author Scott Battaglia
 * @version 1.0
 */
public interface LongNumericGenerator extends NumericGenerator {

    /**
     * Get the next long in the sequence.
     *
     * @return the next long in the sequence.
     */
    long getNextLong();
}
