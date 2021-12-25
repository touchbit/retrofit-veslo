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

package org.touchbit.retrofit.veslo.example.client.transport;

import org.slf4j.LoggerFactory;
import org.touchbit.retrofit.veslo.allure.AllureAction;
import org.touchbit.retrofit.veslo.client.inteceptor.CompositeInterceptor;
import org.touchbit.retrofit.veslo.client.inteceptor.LoggingAction;

public class CustomCompositeInterceptor extends CompositeInterceptor {

    public CustomCompositeInterceptor() {
        super(LoggerFactory.getLogger(CustomCompositeInterceptor.class));
        withRequestInterceptActionsChain(AuthAction.INSTANCE, LoggingAction.INSTANCE, AllureAction.INSTANCE);
        withResponseInterceptActionsChain(LoggingAction.INSTANCE, AllureAction.INSTANCE);
    }

}
