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

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static veslo.constant.ParameterNameConstants.ACTIONS_PARAMETER;
import static veslo.constant.ParameterNameConstants.ACTION_PARAMETER;

/**
 * Interceptor allows you to store multiple
 * request, response and exception handlers.
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 24.11.2021
 */
public class CompositeInterceptor implements Interceptor {

    private final List<RequestInterceptAction> requestInterceptActions = new ArrayList<>();
    private final List<ResponseInterceptAction> responseInterceptAction = new ArrayList<>();
    private final Logger logger;

    public CompositeInterceptor() {
        this(LoggerFactory.getLogger(CompositeInterceptor.class));
    }

    public CompositeInterceptor(Logger logger) {
        this.logger = logger;
    }

    public List<RequestInterceptAction> getRequestInterceptActions() {
        return requestInterceptActions;
    }

    public List<ResponseInterceptAction> getResponseInterceptAction() {
        return responseInterceptAction;
    }

    /**
     * The sequence of execution of {@link RequestInterceptAction} depends
     * on the sequence of passing them to the requestActionsChain
     *
     * @param actions - sequence of execution of {@link RequestInterceptAction}
     * @return this
     */
    public CompositeInterceptor withRequestInterceptActionsChain(RequestInterceptAction... actions) {
        Utils.parameterRequireNonNull(actions, ACTIONS_PARAMETER);
        this.requestInterceptActions.clear();
        for (RequestInterceptAction action : actions) {
            Utils.parameterRequireNonNull(action, ACTION_PARAMETER);
            this.requestInterceptActions.add(action);
        }
        if (actions.length == 0) {
            logger.debug("Received an empty RequestInterceptAction list");
        } else {
            StringJoiner stringJoiner = new StringJoiner("\n * ",
                    "Received a list of RequestInterceptAction with the following execution sequence:\n * ", "");
            for (RequestInterceptAction action : actions) {
                stringJoiner.add(action.getClass().getName());
            }
            logger.debug("{}", stringJoiner);
        }
        return this;
    }

    /**
     * The sequence of execution of {@link ResponseInterceptAction} depends
     * on the sequence of passing them to the responseActionsChain
     *
     * @param actions - sequence of execution of {@link ResponseInterceptAction}
     * @return this
     */
    public CompositeInterceptor withResponseInterceptActionsChain(ResponseInterceptAction... actions) {
        Utils.parameterRequireNonNull(actions, ACTIONS_PARAMETER);
        this.responseInterceptAction.clear();
        for (ResponseInterceptAction action : actions) {
            Utils.parameterRequireNonNull(action, ACTION_PARAMETER);
            this.responseInterceptAction.add(action);
        }
        if (actions.length == 0) {
            logger.debug("Received an empty ResponseInterceptAction list");
        } else {
            StringJoiner stringJoiner = new StringJoiner("\n * ",
                    "Received a list of ResponseInterceptAction with the following execution sequence:\n * ", "");
            for (ResponseInterceptAction action : actions) {
                stringJoiner.add(action.getClass().getName());
            }
            logger.debug("{}", stringJoiner);
        }
        return this;
    }

    /**
     * Interception call in the following action sequence
     * - RequestInterceptAction.chainAction(Chain)
     * - RequestInterceptAction.requestAction(Request)
     * - ResponseInterceptAction.errorAction(Exception) if error occurred
     * - ResponseInterceptAction.responseAction(Response)
     *
     * @param realChain - {@link Chain}
     * @return - {@link Response}
     * @throws IOException -
     */
    @Override
    @Nonnull
    public Response intercept(@Nonnull Chain realChain) throws IOException {
        Chain chain = realChain;
        for (RequestInterceptAction action : getRequestInterceptActions()) {
            logger.trace("chainAction() call: {}", action);
            chain = action.chainAction(chain);
        }
        Response response;
        try {
            Request request = chain.request();
            for (RequestInterceptAction action : getRequestInterceptActions()) {
                logger.trace("requestAction() call: {}", action);
                request = action.requestAction(request);
            }
            response = chain.proceed(request);
        } catch (IOException | RuntimeException e) {
            for (ResponseInterceptAction action : getResponseInterceptAction()) {
                logger.trace("errorAction() call: {}", action);
                action.errorAction(e);
            }
            throw e;
        }
        for (ResponseInterceptAction action : getResponseInterceptAction()) {
            logger.trace("responseAction() call: {}", action);
            response = action.responseAction(response);
        }
        return response;
    }

}
