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

import java.util.Arrays;

/**
 * Universal DTO model with built-in checks and helper methods.
 * <p>
 * Created by Oleg Shaburov on 08.11.2021
 * shaburov.o.a@gmail.com
 */
public class AnyBody implements IAnyBody {

    private final byte[] bodyData;

    /**
     * @param data - byte body
     */
    public AnyBody(byte[] data) {
        this.bodyData = data;
    }

    public AnyBody assertBodyIsNotNull() {
        if (isNullBody()) {
            throw new AssertionError("Response body\n" +
                    "Expected: is byte array\n" +
                    "     but: was " + Arrays.toString(bodyData) + "\n");
        }
        return this;
    }

    public AnyBody assertBodyIsNull() {
        if (!isNullBody()) {
            throw new AssertionError("Response body\n" +
                    "Expected: is null\n" +
                    "     but: was array length '" + bodyData.length + "'\n");
        }
        return this;
    }

    public AnyBody assertBodyIsNotEmpty() {
        if (isNullBody() || isEmptyBody()) {
            throw new AssertionError("Response body\n" +
                    "Expected: is not empty byte array\n" +
                    "     but: was " + Arrays.toString(bodyData) + "\n");
        }
        return this;
    }

    public AnyBody assertBodyIsEmpty() {
        assertBodyIsNotNull();
        if (!isEmptyBody()) {
            throw new AssertionError("Response body\n" +
                    "Expected: is empty byte array\n" +
                    "     but: was array length '" + bodyData.length + "'\n");
        }
        return this;
    }

    public boolean isNullBody() {
        return bodyData == null;
    }

    public boolean isEmptyBody() {
        return isNullBody() || bodyData.length == 0;
    }

    public byte[] getBytes() {
        return bodyData;
    }

    @Override
    public String toString() {
        return new String(bodyData);
    }

}
