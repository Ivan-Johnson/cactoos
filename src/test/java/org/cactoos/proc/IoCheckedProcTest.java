/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2020 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cactoos.proc;

import java.io.IOException;
import org.cactoos.Proc;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;
import org.llorllale.cactoos.matchers.Throws;

/**
 * Test case for {@link IoCheckedProc}.
 *
 * @since 0.4
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class IoCheckedProcTest {
    @Test
    void rethrowsIoException() {
        final IOException exception = new IOException("intended");
        new Assertion<>(
            "Must rethrow original IOException",
            () -> {
                new IoCheckedProc<>(
                    (Proc<Integer>) i -> {
                        throw exception;
                    }
                ).exec(1);
                return null;
            },
            new Throws<>(exception.getMessage(), exception.getClass())
        ).affirm();
    }

    @Test
    void rethrowsCheckedToIoException() {
        new Assertion<>(
            "Must wrap and throw IOException",
            () -> {
                new IoCheckedProc<>(
                    (Proc<Integer>) i -> {
                        throw new InterruptedException("intended to fail");
                    }
                ).exec(1);
                return null;
            },
            new Throws<>(IOException.class)
        ).affirm();

        final boolean status = Thread.interrupted();
        new Assertion<>(
            "Interrupt status must be preserved",
            status,
            new IsTrue()
        ).affirm();
    }

    @Test
    void runtimeExceptionGoesOut() {
        new Assertion<>(
            "Must throw runtime exceptions as is",
            () -> {
                new IoCheckedProc<>(
                    i -> {
                        throw new IllegalStateException("intended to fail here");
                    }
                ).exec(1);
                return null;
            },
            new Throws<>(IllegalStateException.class)
        ).affirm();
    }

}
