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

package veslo.client.model;

import veslo.BriefAssertionError;
import veslo.asserter.SoftlyAsserter;
import veslo.util.Utils;

import javax.annotation.Nullable;
import java.util.Arrays;

import static veslo.constant.ParameterNameConstants.*;

/**
 * Universal DTO model with built-in checks and helper methods.
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 08.11.2021
 */
@SuppressWarnings("UnusedReturnValue")
public class RawBody {

    private static final String RESPONSE_BODY_MSG = "Response body\n";

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
            throw new BriefAssertionError(RESPONSE_BODY_MSG +
                    "Expected: is byte array\n" +
                    "     but: was " + Arrays.toString(bodyData) + "\n");
        }
        return this;
    }

    public RawBody assertBodyIsNull() {
        if (!isNullBody()) {
            throw new BriefAssertionError(RESPONSE_BODY_MSG +
                    "Expected: is null\n" +
                    "     but: was array length '" + bodyData.length + "'\n");
        }
        return this;
    }

    public RawBody assertBodyIsNotEmpty() {
        if (isNullBody() || isEmptyBody()) {
            throw new BriefAssertionError(RESPONSE_BODY_MSG +
                    "Expected: is not empty byte array\n" +
                    "     but: was " + Arrays.toString(bodyData) + "\n");
        }
        return this;
    }

    public RawBody assertBodyIsEmpty() {
        assertBodyIsNotNull();
        if (!isEmptyBody()) {
            throw new BriefAssertionError(RESPONSE_BODY_MSG +
                    "Expected: is empty byte array\n" +
                    "     but: was array length '" + bodyData.length + "'\n");
        }
        return this;
    }

    public RawBody assertStringBodyContains(String... expectedStrings) {
        Utils.parameterRequireNonNull(expectedStrings, EXPECTED_STRINGS_PARAMETER);
        assertBodyIsNotNull();
        try (final SoftlyAsserter softlyAsserter = SoftlyAsserter.get()) {
            for (String expectedString : expectedStrings) {
                Utils.parameterRequireNonNull(expectedString, EXPECTED_STRING_PARAMETER);
                //noinspection ConstantConditions -> assertBodyIsNotNull()
                if (!string().contains(expectedString)) {
                    softlyAsserter.softly(() -> {
                        throw new BriefAssertionError(RESPONSE_BODY_MSG +
                                "Expected: contains '" + expectedString + "'\n" +
                                "     but: does not contain\n");
                    });
                }
            }
        }
        return this;
    }

    public RawBody assertStringBodyContainsIgnoreCase(String... expectedStrings) {
        Utils.parameterRequireNonNull(expectedStrings, EXPECTED_STRINGS_PARAMETER);
        assertBodyIsNotNull();
        try (final SoftlyAsserter softlyAsserter = SoftlyAsserter.get()) {
            for (String expectedString : expectedStrings) {
                Utils.parameterRequireNonNull(expectedString, EXPECTED_STRING_PARAMETER);
                //noinspection ConstantConditions -> assertBodyIsNotNull()
                if (!string().toLowerCase().contains(expectedString.toLowerCase())) {
                    softlyAsserter.softly(() -> {
                        throw new BriefAssertionError(RESPONSE_BODY_MSG +
                                "Expected: contains '" + expectedString + "' (ignore case)\n" +
                                "     but: does not contain\n");
                    });
                }
            }
        }
        return this;
    }

    public RawBody assertStringBodyIs(String expected) {
        Utils.parameterRequireNonNull(expected, EXPECTED_PARAMETER);
        assertBodyIsNotNull();
        if (!expected.equals(string())) {
            throw new BriefAssertionError(RESPONSE_BODY_MSG +
                    "Expected: '" + expected + "'\n" +
                    "     but: was '" + string() + "'\n");
        }
        return this;
    }

    public RawBody assertStringBodyIsIgnoreCase(String expected) {
        Utils.parameterRequireNonNull(expected, EXPECTED_PARAMETER);
        assertBodyIsNotNull();
        if (!expected.equalsIgnoreCase(string())) {
            throw new BriefAssertionError(RESPONSE_BODY_MSG +
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
