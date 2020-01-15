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

public class NotInitilizedWithContextExceptionTest {

    public static final String MSG = "The class need the android application context but it was not set.";

    @Test
    public void testNotInitilizedWithContextException() {
        Exception exception = new NotInitilizedWithContextException();
        Assert.assertNotNull(exception);
        Assert.assertEquals(MSG, exception.getMessage());
    }

    @Test
    public void testNotInitilizedWithContextExceptionWithCause() {
        Exception cause = new RuntimeException("Mhfhzy");
        Exception exception = new NotInitilizedWithContextException(cause);
        Assert.assertNotNull(exception);
        Assert.assertEquals(cause, exception.getCause());
        Assert.assertEquals(MSG, exception.getMessage());
    }

    @Test
    public void testNotInitilizedWithContextExceptionWithCauseSuppressionStackTrace() {
        Exception cause = new RuntimeException("Myss");
        Exception cause2 = new RuntimeException("My22sdfsdf");
        Exception exception = new NotInitilizedWithContextException(cause, true, true);
        exception.addSuppressed(cause2);
        Assert.assertNotNull(exception);
        Assert.assertEquals(cause, exception.getCause());
        Assert.assertNotNull(exception.getStackTrace());
        Assert.assertTrue(exception.getStackTrace().length > 0);
        Assert.assertNotNull(exception.getSuppressed());
        Assert.assertTrue(exception.getSuppressed().length > 0);
        Assert.assertEquals(MSG, exception.getMessage());
    }

    @Test
    public void testNotInitilizedWithContextExceptionWithCauseWithoutSuppressionStackTrace() {
        Exception cause = new RuntimeException("M2sfdy");
        Exception cause2 = new RuntimeException("My2iu");
        Exception exception = new NotInitilizedWithContextException(cause, false, false);
        exception.addSuppressed(cause2);
        Assert.assertNotNull(exception);
        Assert.assertEquals(cause, exception.getCause());
        Assert.assertNotNull(exception.getStackTrace());
        Assert.assertTrue(exception.getStackTrace().length == 0);
        Assert.assertNotNull(exception.getSuppressed());
        Assert.assertTrue(exception.getSuppressed().length == 0);
        Assert.assertEquals(MSG, exception.getMessage());
    }
}
