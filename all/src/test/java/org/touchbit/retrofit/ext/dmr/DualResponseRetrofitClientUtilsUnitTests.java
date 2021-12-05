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

package org.touchbit.retrofit.ext.dmr;

import internal.test.utils.BaseUnitTest;
import internal.test.utils.asserter.ThrowableAsserter;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.touchbit.retrofit.ext.dmr.allure.AllureInterceptAction;
import org.touchbit.retrofit.ext.dmr.client.adapter.DualResponseCallAdapterFactory;
import org.touchbit.retrofit.ext.dmr.client.converter.ExtensionConverterFactory;
import org.touchbit.retrofit.ext.dmr.client.inteceptor.CompositeInterceptor;
import org.touchbit.retrofit.ext.dmr.client.inteceptor.LoggingInterceptAction;
import org.touchbit.retrofit.ext.dmr.client.inteceptor.RequestInterceptAction;
import org.touchbit.retrofit.ext.dmr.client.inteceptor.ResponseInterceptAction;
import org.touchbit.retrofit.ext.dmr.gson.GsonDualConverterFactory;
import org.touchbit.retrofit.ext.dmr.jackson.JacksonDualConverterFactory;
import retrofit2.Retrofit;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.touchbit.retrofit.ext.dmr.DualResponseRetrofitClientUtils.*;

@SuppressWarnings("ConstantConditions")
@DisplayName("DualResponseClientUtils class tests")
public class DualResponseRetrofitClientUtilsUnitTests extends BaseUnitTest {

    private static final String URL = "http://localhost";
    private static final CompositeInterceptor INTERCEPTOR = new CompositeInterceptor();
    private static final DualResponseCallAdapterFactory CA_FACTORY = new DualResponseCallAdapterFactory();
    private static final Class<? extends DualResponseCallAdapterFactory> CA_FACTORY_CLASS = CA_FACTORY.getClass();
    private static final ExtensionConverterFactory EXT_FACTORY = new ExtensionConverterFactory();
    private static final JacksonDualConverterFactory J_FACTORY = new JacksonDualConverterFactory();
    private static final Class<? extends JacksonDualConverterFactory> J_FACTORY_CLASS = J_FACTORY.getClass();
    private static final GsonDualConverterFactory G_FACTORY = new GsonDualConverterFactory();
    private static final Class<? extends GsonDualConverterFactory> G_FACTORY_CLASS = G_FACTORY.getClass();

    @Test
    @DisplayName("DualResponseClientUtils is utility class")
    public void test1639065953042() {
        ThrowableAsserter.assertUtilityClassException(DualResponseRetrofitClientUtils.class);
    }

    @Nested
    @DisplayName("#buildRetrofit(String, Interceptor, CallAdapter.Factory, Converter.Factory) method")
    public class FirstBuildRetrofitMethodTests {

        @Test
        @DisplayName("Required parameters: baseUrl, callAdapterFactory, converterFactory")
        public void test1639065953052() {
            assertThrow(() -> buildRetrofit(null, INTERCEPTOR, CA_FACTORY, EXT_FACTORY)).assertNPE("baseUrl");
            assertThrow(() -> buildRetrofit(URL, INTERCEPTOR, null, EXT_FACTORY)).assertNPE("callAdapterFactory");
            assertThrow(() -> buildRetrofit(URL, INTERCEPTOR, CA_FACTORY, null)).assertNPE("converterFactory");
        }

        @Test
        @DisplayName("Return retrofit client if parameter interceptor = null")
        public void test1639065953060() {
            final Retrofit retrofit = buildRetrofit(URL, null, CA_FACTORY, EXT_FACTORY);
            final OkHttpClient client = (OkHttpClient) retrofit.callFactory();
            final List<Interceptor> interceptors = client.interceptors();
            assertThat("", interceptors.size(), is(0));
            assertThat("", retrofit.callAdapterFactories(), hasItem(CA_FACTORY));
            assertThat("", retrofit.converterFactories(), hasItem(EXT_FACTORY));
        }

        @Test
        @DisplayName("Return retrofit client if parameter interceptor != null")
        public void test1639065953071() {
            final Retrofit retrofit = buildRetrofit(URL, INTERCEPTOR, CA_FACTORY, EXT_FACTORY);
            final OkHttpClient client = (OkHttpClient) retrofit.callFactory();
            final List<Interceptor> interceptors = client.interceptors();
            assertThat("", interceptors.size(), is(1));
            assertThat("", interceptors.get(0), is(INTERCEPTOR));
            assertThat("", retrofit.callAdapterFactories(), hasItem(CA_FACTORY));
            assertThat("", retrofit.converterFactories(), hasItem(EXT_FACTORY));
        }

    }

    @Nested
    @DisplayName("#buildRetrofit(String, Converter.Factory) method")
    public class SecondBuildRetrofitMethodTests {

        @Test
        @DisplayName("Required parameters: baseUrl, converterFactory")
        public void test1639065953089() {
            assertThrow(() -> buildRetrofit(null, EXT_FACTORY)).assertNPE("baseUrl");
            assertThrow(() -> buildRetrofit(URL, null)).assertNPE("converterFactory");
        }

        @Test
        @DisplayName("Return retrofit client with CompositeInterceptor and DualCallAdapterFactory")
        public void test1639065953096() {
            final Retrofit retrofit = buildRetrofit(URL, EXT_FACTORY);
            final OkHttpClient client = (OkHttpClient) retrofit.callFactory();
            final List<Interceptor> interceptors = client.interceptors();
            assertThat("", interceptors.size(), is(1));
            assertThat("", interceptors, hasItem(instanceOf(CompositeInterceptor.class)));
            final CompositeInterceptor interceptor = (CompositeInterceptor) interceptors.get(0);
            final List<RequestInterceptAction> requestInterceptActions = interceptor.getRequestInterceptActions();
            final List<ResponseInterceptAction> responseInterceptActions = interceptor.getResponseInterceptAction();
            assertThat("", requestInterceptActions,
                    contains(instanceOf(LoggingInterceptAction.class), instanceOf(AllureInterceptAction.class)));
            assertThat("", responseInterceptActions,
                    contains(instanceOf(LoggingInterceptAction.class), instanceOf(AllureInterceptAction.class)));
            assertThat("", retrofit.callAdapterFactories(), hasItem(instanceOf(CA_FACTORY_CLASS)));
            assertThat("", retrofit.converterFactories(), hasItem(EXT_FACTORY));
        }

    }

    @Nested
    @DisplayName("#buildGsonRetrofit(String)")
    public class BuildGsonRetrofitTests {

        @Test
        @DisplayName("Required parameters: baseUrl")
        public void test1639065953121() {
            assertThrow(() -> buildGsonRetrofit(null)).assertNPE("baseUrl");
        }

        @Test
        @DisplayName("Return retrofit client with CompositeInterceptor, DualCallAdapterFactory and Gson converter")
        public void test1639065953127() {
            final Retrofit retrofit = buildGsonRetrofit(URL);
            final OkHttpClient client = (OkHttpClient) retrofit.callFactory();
            final List<Interceptor> interceptors = client.interceptors();
            assertThat("", interceptors.size(), is(1));
            assertThat("", interceptors, hasItem(instanceOf(CompositeInterceptor.class)));
            final CompositeInterceptor interceptor = (CompositeInterceptor) interceptors.get(0);
            final List<RequestInterceptAction> requestInterceptActions = interceptor.getRequestInterceptActions();
            final List<ResponseInterceptAction> responseInterceptActions = interceptor.getResponseInterceptAction();
            assertThat("", requestInterceptActions,
                    contains(instanceOf(LoggingInterceptAction.class), instanceOf(AllureInterceptAction.class)));
            assertThat("", responseInterceptActions,
                    contains(instanceOf(LoggingInterceptAction.class), instanceOf(AllureInterceptAction.class)));
            assertThat("", retrofit.callAdapterFactories(), hasItem(instanceOf(CA_FACTORY_CLASS)));
            assertThat("", retrofit.converterFactories(), hasItem(instanceOf(G_FACTORY_CLASS)));
        }


    }

    @Nested
    @DisplayName("#buildJacksonRetrofit(String)")
    public class BuildJacksonRetrofitTests {

        @Test
        @DisplayName("Required parameters: baseUrl")
        public void test1639065953153() {
            assertThrow(() -> buildJacksonRetrofit(null)).assertNPE("baseUrl");
        }

        @Test
        @DisplayName("Return retrofit client with CompositeInterceptor, DualCallAdapterFactory and Jackson converter")
        public void test1639065953159() {
            final Retrofit retrofit = buildJacksonRetrofit(URL);
            final OkHttpClient client = (OkHttpClient) retrofit.callFactory();
            final List<Interceptor> interceptors = client.interceptors();
            assertThat("", interceptors.size(), is(1));
            assertThat("", interceptors, hasItem(instanceOf(CompositeInterceptor.class)));
            final CompositeInterceptor interceptor = (CompositeInterceptor) interceptors.get(0);
            final List<RequestInterceptAction> requestInterceptActions = interceptor.getRequestInterceptActions();
            final List<ResponseInterceptAction> responseInterceptActions = interceptor.getResponseInterceptAction();
            assertThat("", requestInterceptActions,
                    contains(instanceOf(LoggingInterceptAction.class), instanceOf(AllureInterceptAction.class)));
            assertThat("", responseInterceptActions,
                    contains(instanceOf(LoggingInterceptAction.class), instanceOf(AllureInterceptAction.class)));
            assertThat("", retrofit.callAdapterFactories(), hasItem(instanceOf(CA_FACTORY_CLASS)));
            assertThat("", retrofit.converterFactories(), hasItem(instanceOf(J_FACTORY_CLASS)));
        }

    }

    @Nested
    @DisplayName("#create() methods tests")
    public class CreateMethodsTests {

        @Test
        @DisplayName("#create(Class, String, Interceptor, CallAdapter.Factory, Converter.Factory)")
        public void test1639065953184() {
            create(RetrofitCallClient.class, URL, INTERCEPTOR, CA_FACTORY, EXT_FACTORY);
        }

        @Test
        @DisplayName("#create(Class, String, Converter.Factory)")
        public void test1639065953190() {
            create(RetrofitCallClient.class, URL, EXT_FACTORY);
        }

        @Test
        @DisplayName("#createGsonClient(Class, String)")
        public void test1639065953196() {
            createGsonClient(RetrofitCallClient.class, URL);
        }

        @Test
        @DisplayName("#createJacksonClient(Class, String)")
        public void test1639065953202() {
            createJacksonClient(RetrofitCallClient.class, URL);
        }

    }

}