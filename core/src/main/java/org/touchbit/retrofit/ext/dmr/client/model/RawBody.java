/*
 * Copyright 2021 Shaburov Oleg
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

package org.touchbit.retrofit.ext.dmr.client.model;

import org.touchbit.retrofit.ext.dmr.asserter.SoftlyAsserter;
import org.touchbit.retrofit.ext.dmr.util.Utils;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Universal DTO model with built-in checks and helper methods.
 * <p>
 * Created by Oleg Shaburov on 08.11.2021
 * shaburov.o.a@gmail.com
 */
@SuppressWarnings("UnusedReturnValue")
public class RawBody {

    private final byte[] bodyData;

    public RawBody() {
        this((byte[]) null);
    }

    /**
     * @param data - byte body
     */
    public RawBody(@Nullable byte[] data) {
        this.bodyData = data;
    }

    public RawBody(String string) {
        this(string == null ? null : string.getBytes());
    }

    public static RawBody nullable() {
        return new RawBody((byte[]) null);
    }

    public static RawBody empty() {
        return new RawBody(new byte[]{});
    }

    public RawBody assertBodyIsNotNull() {
        if (isNullBody()) {
            throw new AssertionError("Response body\n" +
                    "Expected: is byte array\n" +
                    "     but: was " + Arrays.toString(bodyData) + "\n");
        }
        return this;
    }

    public RawBody assertBodyIsNull() {
        if (!isNullBody()) {
            throw new AssertionError("Response body\n" +
                    "Expected: is null\n" +
                    "     but: was array length '" + bodyData.length + "'\n");
        }
        return this;
    }

    public RawBody assertBodyIsNotEmpty() {
        if (isNullBody() || isEmptyBody()) {
            throw new AssertionError("Response body\n" +
                    "Expected: is not empty byte array\n" +
                    "     but: was " + Arrays.toString(bodyData) + "\n");
        }
        return this;
    }

    public RawBody assertBodyIsEmpty() {
        assertBodyIsNotNull();
        if (!isEmptyBody()) {
            throw new AssertionError("Response body\n" +
                    "Expected: is empty byte array\n" +
                    "     but: was array length '" + bodyData.length + "'\n");
        }
        return this;
    }

    public RawBody assertStringBodyContains(String... expectedStrings) {
        Utils.parameterRequireNonNull(expectedStrings, "expectedStrings");
        assertBodyIsNotNull();
        try (final SoftlyAsserter softlyAsserter = SoftlyAsserter.get()) {
            for (String expected : expectedStrings) {
                Utils.parameterRequireNonNull(expectedStrings, "expectedStrings");
                //noinspection ConstantConditions -> assertBodyIsNotNull()
                if (!string().contains(expected)) {
                    softlyAsserter.softly(() -> {
                        throw new AssertionError("Response body\n" +
                                "Expected: contains '" + expected + "'\n" +
                                "     but: does not contain\n");
                    });
                }
            }
        }
        return this;
    }

    public RawBody assertStringBodyContainsIgnoreCase(String... expectedStrings) {
        Utils.parameterRequireNonNull(expectedStrings, "expectedStrings");
        assertBodyIsNotNull();
        try (final SoftlyAsserter softlyAsserter = SoftlyAsserter.get()) {
            for (String expected : expectedStrings) {
                Utils.parameterRequireNonNull(expectedStrings, "expectedStrings");
                //noinspection ConstantConditions -> assertBodyIsNotNull()
                if (!string().toLowerCase().contains(expected.toLowerCase())) {
                    softlyAsserter.softly(() -> {
                        throw new AssertionError("Response body\n" +
                                "Expected: contains '" + expected + "' (ignore case)\n" +
                                "     but: does not contain\n");
                    });
                }
            }
        }
        return this;
    }

    public RawBody assertStringBodyIs(String expected) {
        Utils.parameterRequireNonNull(expected, "expected");
        assertBodyIsNotNull();
        if (!expected.equals(string())) {
            throw new AssertionError("" +
                    "Response body\n" +
                    "Expected: '" + expected + "'\n" +
                    "     but: was '" + string() + "'\n");
        }
        return this;
    }

    public RawBody assertStringBodyIsIgnoreCase(String expected) {
        Utils.parameterRequireNonNull(expected, "expected");
        assertBodyIsNotNull();
        if (!expected.equalsIgnoreCase(string())) {
            throw new AssertionError("" +
                    "Response body\n" +
                    "Expected: '" + expected + "' (ignore case)\n" +
                    "     but: was '" + string() + "'\n");
        }
        return this;
    }

    public boolean isNullBody() {
        return bodyData == null;
    }

    public boolean isEmptyBody() {
        return isNullBody() || bodyData.length == 0;
    }

    public byte[] bytes() {
        return bodyData;
    }

    @Nullable
    public String string() {
        if (isNullBody()) {
            return null;
        }
        return new String(bodyData);
    }

    @Override
    public String toString() {
        return "RawBody{bodyData=" + Arrays.toString(bodyData) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null && bodyData == null) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RawBody rawBody = (RawBody) o;
        return Arrays.equals(bodyData, rawBody.bodyData);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bodyData);
    }

}
