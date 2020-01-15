/*
 * Copyright (c) 2020 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.ti.openhealthcard.common.exceptions.runtime;

import org.junit.Assert;
import org.junit.Test;

public class WrongThreadExceptionTest {

    public static final String MSG = "One Message to the users";

    @Test
    public void testWrongThreadException() {
        Exception exception = new WrongThreadException(MSG);
        Assert.assertNotNull(exception);
        Assert.assertEquals(MSG, exception.getMessage());
    }

    @Test
    public void testWrongThreadExceptionWithCause() {
        Exception cause = new RuntimeException("My");
        Exception exception = new WrongThreadException(MSG, cause);
        Assert.assertNotNull(exception);
        Assert.assertEquals(cause, exception.getCause());
        Assert.assertEquals(MSG, exception.getMessage());
    }
}
