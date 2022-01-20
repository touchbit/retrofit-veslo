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

package veslo.client;

import internal.test.utils.client.MockInterceptor;
import okhttp3.Request;
import okhttp3.Response;
import veslo.client.inteceptor.LoggingAction;

import java.io.IOException;

public class LoggedMockInterceptor extends MockInterceptor {

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
