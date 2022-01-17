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

package veslo;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.client.adapter.JavaTypeCallAdapterFactory;
import veslo.client.adapter.UniversalCallAdapterFactory;
import veslo.client.inteceptor.CompositeInterceptor;
import veslo.client.inteceptor.LoggingAction;
import veslo.client.response.DualResponse;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility class for creating a Retrofit client
 * that works with the return type of {@link DualResponse}
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 07.12.2021
 */
@SuppressWarnings("UnusedReturnValue")
public class DualResponseRetrofitClientUtils {

    /**
     * @param <CLIENT>    - API client type
     * @param clientClass - API client interface class
     * @param baseUrl     - api URL is format schema://domain:port
     * @return API client implementation with {@link JacksonConverterFactory} and default interceptors
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT createJacksonClient(final Class<CLIENT> clientClass, final String baseUrl) {
        return buildRetrofit(baseUrl, new JacksonConverterFactory()).create(clientClass);
    }

    /**
     * @param <CLIENT>    - API client type
     * @param clientClass - API client interface class
     * @param baseUrl     - api URL is format schema://domain:port
     * @return API client implementation with {@link GsonConverterFactory} and default interceptors
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT createGsonClient(final Class<CLIENT> clientClass, final String baseUrl) {
        return buildRetrofit(baseUrl, new GsonConverterFactory()).create(clientClass);
    }

    /**
     * @param <CLIENT>         - API client type
     * @param clientClass      - API client interface class
     * @param baseUrl          - api URL is format schema://domain:port
     * @param converterFactory - {@link JacksonConverterFactory}, {@link GsonConverterFactory}, etc.
     * @return API client implementation with default interceptors
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT create(final Class<CLIENT> clientClass,
                                         final String baseUrl,
                                         final Converter.Factory converterFactory) {
        return buildRetrofit(baseUrl, converterFactory).create(clientClass);
    }

    /**
     * @param <CLIENT>           - API client type
     * @param clientClass        - API client interface class
     * @param baseUrl            - api URL is format schema://domain:port
     * @param interceptor        - {@link Interceptor} implementation. For example {@link CompositeInterceptor}
     * @param callAdapterFactory - For example {@link UniversalCallAdapterFactory} or {@link JavaTypeCallAdapterFactory}
     * @param converterFactory   - {@link JacksonConverterFactory}, {@link GsonConverterFactory}, etc.
     * @return API client implementation
     */
    @EverythingIsNonNull
    public static <CLIENT> CLIENT create(final Class<CLIENT> clientClass,
                                         final String baseUrl,
                                         final Interceptor interceptor,
                                         final CallAdapter.Factory callAdapterFactory,
                                         final Converter.Factory converterFactory) {
        return buildRetrofit(baseUrl, interceptor, callAdapterFactory, converterFactory).create(clientClass);
    }

    /**
     * @param baseUrl - api URL is format schema://domain:port
     * @return {@link Retrofit} with {@link JacksonConverterFactory} and default interceptors
     */
    @EverythingIsNonNull
    public static Retrofit buildJacksonRetrofit(final String baseUrl) {
        Utils.parameterRequireNonNull(baseUrl, "baseUrl");
        return buildRetrofit(baseUrl, new JacksonConverterFactory());
    }

    /**
     * @param baseUrl - api URL is format schema://domain:port
     * @return {@link Retrofit} with {@link GsonConverterFactory} and default interceptors
     */
    @EverythingIsNonNull
    public static Retrofit buildGsonRetrofit(final String baseUrl) {
        Utils.parameterRequireNonNull(baseUrl, "baseUrl");
        return buildRetrofit(baseUrl, new GsonConverterFactory());
    }

    /**
     * @param baseUrl          - api URL is format schema://domain:port
     * @param converterFactory - {@link JacksonConverterFactory}, {@link GsonConverterFactory}, etc.
     * @return {@link Retrofit} with default interceptors
     */
    @EverythingIsNonNull
    public static Retrofit buildRetrofit(final String baseUrl,
                                         final Converter.Factory converterFactory) {
        Utils.parameterRequireNonNull(baseUrl, "baseUrl");
        Utils.parameterRequireNonNull(converterFactory, "converterFactory");
        final LoggingAction loggingAction = new LoggingAction();
        final AllureAction allureAction = new AllureAction();
        final CompositeInterceptor compositeInterceptor = new CompositeInterceptor()
                .withRequestInterceptActionsChain(loggingAction, allureAction)
                .withResponseInterceptActionsChain(loggingAction, allureAction);
        return buildRetrofit(baseUrl, compositeInterceptor, new UniversalCallAdapterFactory(), converterFactory);
    }

    /**
     * @param baseUrl            - api URL is format schema://domain:port
     * @param interceptor        - {@link Interceptor} implementation. For example {@link CompositeInterceptor}
     * @param callAdapterFactory - For example {@link UniversalCallAdapterFactory} or {@link JavaTypeCallAdapterFactory}
     * @param converterFactory   - {@link JacksonConverterFactory}, {@link GsonConverterFactory}, etc.
     * @return {@link Retrofit}
     */
    @Nonnull
    public static Retrofit buildRetrofit(@Nonnull final String baseUrl,
                                         @Nullable final Interceptor interceptor,
                                         @Nonnull final CallAdapter.Factory callAdapterFactory,
                                         @Nonnull final Converter.Factory converterFactory) {
        Utils.parameterRequireNonNull(baseUrl, "baseUrl");
        Utils.parameterRequireNonNull(callAdapterFactory, "callAdapterFactory");
        Utils.parameterRequireNonNull(converterFactory, "converterFactory");
        if (interceptor == null) {
            return new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addCallAdapterFactory(callAdapterFactory)
                    .addConverterFactory(converterFactory)
                    .build();
        }
        return new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        .addNetworkInterceptor(interceptor)
                        .build())
                .baseUrl(baseUrl)
                .addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(converterFactory)
                .build();
    }

    /**
     * Utility class
     */
    private DualResponseRetrofitClientUtils() {
        throw new UtilityClassException();
    }

}
