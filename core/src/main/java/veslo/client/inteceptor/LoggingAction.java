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

package veslo.client.inteceptor;

import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import veslo.util.OkhttpUtils;

import javax.annotation.Nonnull;
import java.io.IOException;

public class LoggingAction implements InterceptAction {

    public static final LoggingAction INSTANCE = new LoggingAction();

    private final Logger logger;

    public LoggingAction() {
        this(LoggerFactory.getLogger(LoggingAction.class));
    }

    public LoggingAction(Logger logger) {
        this.logger = logger;
    }

    @Override
    @Nonnull
    public Request requestAction(@Nonnull Request request) throws IOException {
        String requestLogMsg = OkhttpUtils.requestToString(request);
        logger.info(requestLogMsg);
        return request;
    }

    /**
     * Exceptions for package "java.net." refer to errors in
     * client-server communication and do not refer to the API call made.
     * For example: {@link java.net.UnknownHostException}
     * It makes no sense to log the stack trace.
     * <p>
     * The rest of the errors will be logged with the stack trace.
     *
     * @param exception - Exception thrown when making a request to the server
     */
    @Override
    public void errorAction(@Nonnull Throwable exception) {
        if (exception.getClass().getTypeName().startsWith("java.net.")) {
            logger.error(exception.toString());
        } else {
            logger.error("Transport error", exception);
        }
    }

    @Override
    @Nonnull
    public Response responseAction(@Nonnull Response response) throws IOException {
        String responseLogMsg = OkhttpUtils.responseToString(response);
        logger.info(responseLogMsg);
        return response;
    }

}
