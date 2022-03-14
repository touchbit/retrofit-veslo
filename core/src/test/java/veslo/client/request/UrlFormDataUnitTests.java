/*
 * Copyright 2021-2022 Shaburov Oleg
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

package veslo.client.request;

import internal.test.utils.BaseUnitTest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncoded;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedAdditionalProperties;
import org.touchbit.www.form.urlencoded.marshaller.pojo.FormUrlEncodedField;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import veslo.client.adapter.UniversalCallAdapterFactory;
import veslo.client.converter.ExtensionConverterFactory;
import veslo.client.response.DualResponse;
import veslo.example.ExampleApiClientAssertionsTests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static internal.test.utils.TestUtils.listOf;
import static org.hamcrest.Matchers.is;

@DisplayName("UrlFormData.class unit tests")
public class UrlFormDataUnitTests extends BaseUnitTest {

    protected static final String ENCODED = "%D1%82%D0%B5%D1%81%D1%82";
    protected static final String DECODED = "тест";

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
    @DisplayName("Get values from ReflectQueryMap without annotations (null values)")
    public void test1647274773647() {
        DefaultFormUrlQueryData map = new DefaultFormUrlQueryData();
        final Response response = CLIENT.defaultFormUrlQueryDataCall(map).getResponse();
        assertThat(response.request().url().toString(), is("http://localhost/api/test"));
    }

    @Test
    @DisplayName("Get values from ReflectQueryMap without annotations (filled)")
    public void test1647274777201() {
        DefaultFormUrlQueryData map = new DefaultFormUrlQueryData()
                .limit(10)
                .offset(50)
                .nested(new NestedPOJO().car(DECODED));
        final Response response = CLIENT.defaultFormUrlQueryDataCall(map).getResponse();
        assertThat(response.request().url().toString(), is("http://localhost/api/test?limit=10&nested[car]=%D1%82%D0%B5%D1%81%D1%82&offset=50"));
    }

    @Test
    @DisplayName("toString()")
    public void test1647274780024() {
        final NestedPOJO nested = new NestedPOJO();
        nested.list = listOf("list_value_1", "list_value_2");
        nested.encodedCar = ENCODED;
        final DefaultFormUrlQueryData defaultFormUrlQueryData = new DefaultFormUrlQueryData();
        defaultFormUrlQueryData.bar = DECODED;
        defaultFormUrlQueryData.foo = "foo_value";
        defaultFormUrlQueryData.nested = nested;
        assertIs(defaultFormUrlQueryData.toString(), "nested[list]=list_value_1&" +
                                                     "nested[list]=list_value_2&" +
                                                     "nested[car]=%D1%82%D0%B5%D1%81%D1%82&" +
                                                     "bar=%D1%82%D0%B5%D1%81%D1%82&" +
                                                     "foo=foo_value");
    }

    private interface Client {

        @GET("/api/test")
        DualResponse<String, String> defaultFormUrlQueryDataCall(@QueryMap(encoded = true) DefaultFormUrlQueryData map);

    }

    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    @FormUrlEncoded
    public static class DefaultFormUrlQueryData extends FormUrlQueryData {

        @FormUrlEncodedField("foo")
        private String foo;

        @FormUrlEncodedField("bar")
        private String bar;

        @FormUrlEncodedField("limit")
        private Integer limit;

        @FormUrlEncodedField("offset")
        private Integer offset;

        @FormUrlEncodedField(value = "nested")
        private NestedPOJO nested;

        @FormUrlEncodedAdditionalProperties()
        public final Map<String, Object> additionalProperties = new HashMap<>();

    }

    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    @FormUrlEncoded
    public static class NestedPOJO extends FormUrlQueryData {

        @FormUrlEncodedField(value = "car", encoded = true)
        private String encodedCar;

        @FormUrlEncodedField("list")
        private List<String> list;

        @FormUrlEncodedField("car")
        private String car;

        @FormUrlEncodedAdditionalProperties()
        public final Map<String, Object> additionalProperties = new HashMap<>();

    }

}


