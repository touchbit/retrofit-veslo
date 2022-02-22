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

package veslo.example;

import internal.test.utils.CorruptedTestException;
import internal.test.utils.client.MockInterceptor;
import okhttp3.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;
import veslo.BaseCoreUnitTest;
import veslo.ConverterUnsupportedTypeException;
import veslo.client.adapter.UniversalCallAdapterFactory;
import veslo.client.converter.ExtensionConverterFactory;
import veslo.client.converter.api.ExtensionConverter;
import veslo.client.converter.typed.RawBodyConverter;
import veslo.client.inteceptor.LoggingAction;
import veslo.example.dto.DTO;
import veslo.example.dto.ErrDTO;
import veslo.example.dto.SucDTO;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@DisplayName("ExampleApiClientAssertions.class tests")
public class ExampleApiClientAssertionsTests extends BaseCoreUnitTest {

    static {
        ExtensionConverterFactory factory = new ExtensionConverterFactory();
        factory.registerPackageConverter(new ExampleExtensionConverter(), SucDTO.class);
        ExampleApiClientAssertions.apiClient = new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new LoggedMockInterceptor())
                        .build())
                .baseUrl("http://localhost")
                .addCallAdapterFactory(new UniversalCallAdapterFactory())
                .addConverterFactory(factory)
                .build()
                .create(ExampleClient.class);
    }

    @Test
    @DisplayName("AssertResponseMethodTests")
    public void test1639586648784() {
        final ExampleApiClientAssertions.AssertResponseMethodExamples examples = new ExampleApiClientAssertions.AssertResponseMethodExamples();
        examples.example1639328754881();
        examples.example1639329013867();
        examples.example1639437048937();
    }

    @Test
    @DisplayName("AssertHeadersMethodExamples")
    public void test1639586656310() {
        final ExampleApiClientAssertions.AssertHeadersMethodExamples examples = new ExampleApiClientAssertions.AssertHeadersMethodExamples();
        examples.example1639329975511();
        examples.example1639330184783();
    }

    @Test
    @DisplayName("AssertSucResponseMethodExamples")
    public void test1639586716278() {
        final ExampleApiClientAssertions.AssertSucResponseMethodExamples examples = new ExampleApiClientAssertions.AssertSucResponseMethodExamples();
        examples.example1639323053942();
        examples.example1639323439880();
        examples.example1639324202096();
        examples.example1639324398972();
        examples.example1639323829528();
        examples.example1639325042327();
        examples.example1639328323975();
        examples.example1639328330398();
        examples.example1639327810398();
    }

    @Test
    @DisplayName("AssertSucBodyMethodExamples")
    public void test1639586784559() {
        final ExampleApiClientAssertions.AssertSucBodyMethodExamples examples = new ExampleApiClientAssertions.AssertSucBodyMethodExamples();
        examples.example1639325312188();
        examples.example1639325440427();
        examples.example1639325443017();
        examples.example1639325444984();
        examples.example1639325561761();
        examples.example1639325563806();
        examples.example1639325807124();
        examples.example1639326363134();
        examples.example1639326893023();
    }

    @Test
    @DisplayName("AssertErrResponseMethodExamples")
    public void test1639586918228() {
        final ExampleApiClientAssertions.AssertErrResponseMethodExamples examples = new ExampleApiClientAssertions.AssertErrResponseMethodExamples();
        examples.example1639435639889();
        examples.example1639435652717();
        examples.example1639435661467();
        examples.example1639435669326();
        examples.example1639435677413();
        examples.example1639435684781();
        examples.example1639435692258();
        examples.example1639435699849();
        examples.example1639435708260();
    }

    @Test
    @DisplayName("AssertErrBodyMethodExamples")
    public void test1639586983351() {
        final ExampleApiClientAssertions.AssertErrBodyMethodExamples examples = new ExampleApiClientAssertions.AssertErrBodyMethodExamples();
        examples.example1639437244747();
        examples.example1639437254775();
        examples.example1639437276238();
        examples.example1639437293672();
        examples.example1639437371546();
        examples.example1639437376687();
        examples.example1639437383840();
        examples.example1639437391538();
        examples.example1639437397547();
    }

    private static class ExampleExtensionConverter implements ExtensionConverter<Object> {

        @Override
        @EverythingIsNonNull
        public RequestBodyConverter requestBodyConverter(Type type,
                                                         Annotation[] parameterAnnotations,
                                                         Annotation[] methodAnnotations,
                                                         Retrofit retrofit) {
            return body -> {
                if (body instanceof DTO) {
                    DTO dto = (DTO) body;
                    RequestBody.create(null, dto.getMsg());
                }
                throw new ConverterUnsupportedTypeException(RawBodyConverter.class, body.getClass(), String.class);
            };
        }

        @Override
        @EverythingIsNonNull
        public ResponseBodyConverter<Object> responseBodyConverter(Type type,
                                                                   Annotation[] methodAnnotations,
                                                                   Retrofit retrofit) {
            return new ResponseBodyConverter<Object>() {

                @Override
                @Nullable
                public Object convert(@Nullable ResponseBody body) throws IOException {
                    if (body == null) {
                        return null;
                    }
                    if (type.equals(SucDTO.class)) {
                        return new SucDTO(body.string());
                    }
                    if (type.equals(ErrDTO.class)) {
                        return new ErrDTO(body.string());
                    }
                    throw new CorruptedTestException();
                }

            };
        }

    }

    public static class LoggedMockInterceptor extends MockInterceptor {

        private final LoggingAction loggingAction = new LoggingAction();

        @Override
        public void logRequest(Request request) throws IOException {
            loggingAction.requestAction(request);
        }

        @Override
        public void logResponse(Response response) throws IOException {
            loggingAction.responseAction(response);
        }

    }

}
