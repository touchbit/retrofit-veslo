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

package org.touchbit.retrofit.ext.dmr.client.header;

import okhttp3.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static internal.test.utils.asserter.ThrowableAsserter.assertThrow;
import static internal.test.utils.asserter.ThrowableAsserter.assertUtilityClassException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings({"ConstantConditions", "EqualsBetweenInconvertibleTypes"})
@DisplayName("ContentType tests")
public class ContentTypeUnitTests {

    @Test
    @DisplayName("Successfully instantiating class without charset parameter")
    public void test1639065952345() {
        final ContentType contentType = new ContentType("a", "b");
        assertThat("getType()", contentType.getType(), is("a"));
        assertThat("getSubtype()", contentType.getSubtype(), is("b"));
        assertThat("getCharset()", contentType.getCharset(), nullValue());
        assertThat("isNull()", contentType.isNull(), is(false));
        assertThat("getMediaType()", contentType.getMediaType(), is(MediaType.parse("a/b")));
        assertThat("toString()", contentType.toString(), is("a/b"));
        assertThat("hashCode()", contentType.hashCode(), notNullValue());
    }

    @Test
    @DisplayName("Successfully instantiating class with charset parameter")
    public void test1639065952358() {
        final ContentType contentType = new ContentType("a", "b", "c");
        assertThat("getType()", contentType.getType(), is("a"));
        assertThat("getSubtype()", contentType.getSubtype(), is("b"));
        assertThat("getCharset()", contentType.getCharset(), is("c"));
        assertThat("isNull()", contentType.isNull(), is(false));
        assertThat("isNull()", contentType.getMediaType(), is(MediaType.parse("a/b; charset=c")));
        assertThat("toString()", contentType.toString(), is("a/b; charset=c"));
        assertThat("hashCode()", contentType.hashCode(), notNullValue());
    }

    @Test
    @DisplayName("Successfully instantiating class where all parameters is null")
    public void test1639065952371() {
        final ContentType contentType = new ContentType(null, null, null);
        assertThat("getType()", contentType.getType(), nullValue());
        assertThat("getSubtype()", contentType.getSubtype(), nullValue());
        assertThat("getCharset()", contentType.getCharset(), nullValue());
        assertThat("isNull()", contentType.isNull(), is(true));
        assertThat("isNull()", contentType.getMediaType(), nullValue());
        assertThat("toString()", contentType.toString(), is("null"));
        assertThat("hashCode()", contentType.hashCode(), notNullValue());
    }

    @Test
    @DisplayName("Successfully instantiating class where type and subtype parameters is null")
    public void test1639065952384() {
        final ContentType contentType = new ContentType(null, null, "c");
        assertThat("getType()", contentType.getType(), nullValue());
        assertThat("getSubtype()", contentType.getSubtype(), nullValue());
        assertThat("getCharset()", contentType.getCharset(), nullValue());
        assertThat("isNull()", contentType.isNull(), is(true));
        assertThat("isNull()", contentType.getMediaType(), nullValue());
        assertThat("toString()", contentType.toString(), is("null"));
        assertThat("hashCode()", contentType.hashCode(), notNullValue());
    }

    @Test
    @DisplayName("Successfully instantiating class where MediaType is null")
    public void test1639065952397() {
        final ContentType contentType = new ContentType(null);
        assertThat("getType()", contentType.getType(), nullValue());
        assertThat("getSubtype()", contentType.getSubtype(), nullValue());
        assertThat("getCharset()", contentType.getCharset(), nullValue());
        assertThat("isNull()", contentType.isNull(), is(true));
        assertThat("isNull()", contentType.getMediaType(), nullValue());
        assertThat("toString()", contentType.toString(), is("null"));
        assertThat("hashCode()", contentType.hashCode(), notNullValue());
    }

    @Test
    @DisplayName("Successfully instantiating class where MediaType is present (with charset)")
    public void test1639065952410() {
        MediaType mediaType = MediaType.get("a/b; charset=utf-8");
        final ContentType contentType = new ContentType(mediaType);
        assertThat("getType()", contentType.getType(), is("a"));
        assertThat("getSubtype()", contentType.getSubtype(), is("b"));
        assertThat("getCharset()", contentType.getCharset(), is("utf-8"));
        assertThat("isNull()", contentType.isNull(), is(false));
        assertThat("getMediaType()", contentType.getMediaType(), is(MediaType.get("a/b; charset=utf-8")));
        assertThat("toString()", contentType.toString(), is("a/b; charset=utf-8"));
        assertThat("hashCode()", contentType.hashCode(), notNullValue());
    }

    @Test
    @DisplayName("Successfully instantiating class where MediaType is present (without charset)")
    public void test1639065952424() {
        MediaType mediaType = MediaType.get("a/b");
        final ContentType contentType = new ContentType(mediaType);
        assertThat("getType()", contentType.getType(), is("a"));
        assertThat("getSubtype()", contentType.getSubtype(), is("b"));
        assertThat("getCharset()", contentType.getCharset(), nullValue());
        assertThat("isNull()", contentType.isNull(), is(false));
        assertThat("getMediaType()", contentType.getMediaType(), is(MediaType.get("a/b")));
        assertThat("toString()", contentType.toString(), is("a/b"));
        assertThat("hashCode()", contentType.hashCode(), notNullValue());
    }

    @Test
    @DisplayName("Error creating class instance if type=<present> and subtype=null")
    public void test1639065952438() {
        assertThrow(() -> new ContentType("test1637475796153", null))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("Type and subtype can only be null at the same time");
    }

    @Test
    @DisplayName("Error creating class instance if type=null and subtype=<present>")
    public void test1639065952446() {
        assertThrow(() -> new ContentType(null, "test1637475903584"))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("Type and subtype can only be null at the same time");
    }

    @Test
    @DisplayName("Error creating class instance if type is blank")
    public void test1639065952454() {
        assertThrow(() -> new ContentType("    ", "b"))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("Parameter 'type' cannot be blank.");
    }

    @Test
    @DisplayName("Error creating class instance if subtype is blank")
    public void test1639065952462() {
        assertThrow(() -> new ContentType("a", "   "))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("Parameter 'subtype' cannot be blank.");
    }

    @Test
    @DisplayName("Error creating class instance if charset is blank")
    public void test1639065952470() {
        assertThrow(() -> new ContentType("a", "b", "    "))
                .assertClass(IllegalArgumentException.class)
                .assertMessageIs("Parameter 'charset' cannot be blank.");
    }

    @Test
    @DisplayName("equals(null) with nullable ContentType (true)")
    public void test1639065952478() {
        ContentType contentType = new ContentType(null, null);
        assertThat("", contentType.equals(null), is(true));
    }

    @Test
    @DisplayName("equals() with same parameters ContentType (true)")
    public void test1639065952485() {
        ContentType a1 = new ContentType("a", "b", "c");
        ContentType a2 = new ContentType("a", "b", "c");
        assertThat("", a1.equals(a2), is(true));
    }

    @Test
    @DisplayName("equals() with same parameters MediaType (false)")
    public void test1639065952493() {
        final MediaType mediaType = MediaType.get("a/b; charset=utf-8");
        ContentType contentType = new ContentType("a", "b", "utf-8");
        assertThat("", contentType.equals(mediaType), is(false));
    }

    @Test
    @DisplayName("equals() with different type value (false)")
    public void test1639065952501() {
        ContentType a1 = new ContentType("a1", "b", "c");
        ContentType a2 = new ContentType("a", "b", "c");
        assertThat("", a1.equals(a2), is(false));
    }

    @Test
    @DisplayName("equals() with different subtype value (false)")
    public void test1639065952509() {
        ContentType a1 = new ContentType("a", "b1", "c");
        ContentType a2 = new ContentType("a", "b", "c");
        assertThat("", a1.equals(a2), is(false));
    }

    @Test
    @DisplayName("equals() with different charset value (false)")
    public void test1639065952517() {
        ContentType a1 = new ContentType("a", "b", "c1");
        ContentType a2 = new ContentType("a", "b", "c");
        assertThat("", a1.equals(a2), is(false));
    }

    @Test
    @DisplayName("ContentTypeConstants is util class")
    public void test1639065952525() {
        assertUtilityClassException(ContentTypeConstants.class);
    }

}