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

package org.touchbit.retrofit.ext.dmr.client.request;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.BaseCoreUnitTest;
import org.touchbit.retrofit.ext.dmr.client.adapter.UniversalCallAdapterFactory;
import org.touchbit.retrofit.ext.dmr.client.converter.ExtensionConverterFactory;
import org.touchbit.retrofit.ext.dmr.client.response.DualResponse;
import org.touchbit.retrofit.ext.dmr.exaple.ExampleApiClientAssertionsTests;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import static org.hamcrest.Matchers.is;
import static org.touchbit.retrofit.ext.dmr.client.request.QueryParameterCaseRule.SNAKE_CASE;
import static org.touchbit.retrofit.ext.dmr.client.request.QueryParameterNullValueRule.RULE_EMPTY_STRING;
import static org.touchbit.retrofit.ext.dmr.client.request.QueryParameterNullValueRule.RULE_NULL_MARKER;

@DisplayName("QueryMapVesloUnitTests.class functional tests")
public class QueryMapVesloFuncTests extends BaseCoreUnitTest {

    public static final Client CLIENT = new Retrofit.Builder()
            .client(new OkHttpClient.Builder()
                    .addInterceptor(new ExampleApiClientAssertionsTests.LoggedMockInterceptor())
                    .build())
            .baseUrl("http://localhost")
            .addCallAdapterFactory(UniversalCallAdapterFactory.INSTANCE)
            .addConverterFactory(new ExtensionConverterFactory())
            .build()
            .create(Client.class);

    @Test
    @DisplayName("Get values from QueryMap without annotations (null values)")
    public void test1640009960749() {
        DefaultQueryMap map = new DefaultQueryMap();
        final Response response = CLIENT.defaultQueryMapCall(map).getResponse();
        assertThat(response.request().url().toString(), is("http://localhost/api/test"));
    }

    @Test
    @DisplayName("Get values from QueryMap without annotations (filled)")
    public void test1640010167646() {
        DefaultQueryMap map = new DefaultQueryMap();
        map.firstName = "foo";
        map.lastName = "bar";
        final Response response = CLIENT.defaultQueryMapCall(map).getResponse();
        assertThat(response.request().url().toString(), is("http://localhost/api/test?firstName=foo&lastName=bar"));
    }

    @Test
    @DisplayName("Get values from QueryMap with annotations (filled)")
    public void test1640010267136() {
        NullMarkerSnakeCaseQueryMap map = new NullMarkerSnakeCaseQueryMap();
        map.ruleEmptyStringField = "foo";
        map.camelCaseParameterName = "bar";
        final Response response = CLIENT.nullMarkerSnakeCaseQueryMap(map).getResponse();
        assertThat(response.request().url().toString(), is("http://localhost/api/test?lastName=bar&rule_empty_string_field=foo"));
    }

    @Test
    @DisplayName("Get values from QueryMap with annotations (null values)")
    public void test1640010272640() {
        NullMarkerSnakeCaseQueryMap map = new NullMarkerSnakeCaseQueryMap();
        final Response response = CLIENT.nullMarkerSnakeCaseQueryMap(map).getResponse();
        assertThat(response.request().url().toString(), is("http://localhost/api/test?lastName=%00&rule_empty_string_field="));
    }

    private interface Client {

        @GET("/api/test")
        DualResponse<String, String> defaultQueryMapCall(@QueryMap DefaultQueryMap map);

        @GET("/api/test")
        DualResponse<String, String> nullMarkerSnakeCaseQueryMap(@QueryMap NullMarkerSnakeCaseQueryMap map);

    }

    private static final class DefaultQueryMap extends QueryMapVeslo {

        private Object firstName;
        private Object lastName;

    }

    @QueryMapParameterRules(nullRule = RULE_NULL_MARKER, caseRule = SNAKE_CASE)
    private static final class NullMarkerSnakeCaseQueryMap extends QueryMapVeslo {

        @QueryMapParameter(nullRule = RULE_EMPTY_STRING)
        private Object ruleEmptyStringField;
        @QueryMapParameter(name = "lastName")
        private Object camelCaseParameterName;

    }

}
