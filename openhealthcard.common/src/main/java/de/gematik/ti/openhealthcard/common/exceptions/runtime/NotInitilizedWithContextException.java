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

/**
 * The Android Application Context is not found
 *
 */
public class NotInitilizedWithContextException extends RuntimeException {

    private static final long serialVersionUID = 5563980665499307459L;
    private static final String MESSAGE = "The class need the android application context but it was not set.";

    /**
     * Constructs a new NotInitilizedWithContextException with default detail message.
     */
    public NotInitilizedWithContextException() {
        super(MESSAGE);
    }

    /**
     * Constructs a new exception with the default detail message and cause.
     * 
     * @param cause
     */
    public NotInitilizedWithContextException(final Throwable cause) {
        super(MESSAGE, cause);
    }

    /**
     * Constructs a new exception with the detail detail message, cause, suppression enabled or disabled, and writable stack trace enabled or disabled.
     *
     * @param cause
     *            the cause. (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression
     *            whether or not suppression is enabled or disabled
     * @param writableStackTrace
     *            whether or not the stack trace should be writable
     */
    public NotInitilizedWithContextException(final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(MESSAGE, cause, enableSuppression, writableStackTrace);
    }
}
