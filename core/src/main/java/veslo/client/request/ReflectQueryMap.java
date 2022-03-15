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

package veslo.client.request;

import retrofit2.internal.EverythingIsNonNull;
import veslo.util.ReflectUtils;
import veslo.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Set;

/**
 * * public class ExampleQueryMap extends ReflectQueryMap {
 * *
 * *     private Object camelCaseParameter;
 * *
 * *     @QueryMapParameter("snake_case_parameter")
 * *     private Object snakeCaseParameter;
 * *
 * * }
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 20.12.2021
 */
public abstract class ReflectQueryMap extends HashMap<String, Object> {

    @Override
    @SuppressWarnings("ConstantConditions")
    public Set<Entry<String, Object>> entrySet() {
        this.putAll(readReflectQueryMapParameters(this));
        return super.entrySet();
    }

    protected HashMap<String, Object> readReflectQueryMapParameters(final Object source) {
        Utils.parameterRequireNonNull(source, "source");
        final HashMap<String, Object> result = new HashMap<>();
        for (Field declaredField : ReflectUtils.getAllSerializableFields(source, HashMap.class, AbstractMap.class)) {
            final QueryMapParameterRules classRules = source.getClass().getAnnotation(QueryMapParameterRules.class);
            final QueryMapParameter queryMapParameter = declaredField.getAnnotation(QueryMapParameter.class);
            final Object declaredFieldValue = ReflectUtils.readFieldValue(source, declaredField);
            final String parameterName = getParameterName(queryMapParameter, classRules, declaredField.getName());
            final Object parameterValue = getParameterValue(queryMapParameter, classRules, declaredFieldValue);
            if (parameterValue != null) {
                result.put(parameterName, parameterValue);
            }
        }
        return result;
    }

    @EverythingIsNonNull
    protected String getParameterName(final @Nullable QueryMapParameter queryMapParameter,
                                      final @Nullable QueryMapParameterRules classRules,
                                      final @Nonnull String declaredFieldName) {
        return !Utils.isNullOrBlank(QueryMapParameter::name, queryMapParameter) ?
                queryMapParameter.name() : classRules != null ?
                classRules.caseRule().format(declaredFieldName) : declaredFieldName;
    }

    @Nullable
    protected Object getParameterValue(final @Nullable QueryMapParameter queryMapParameter,
                                       final @Nullable QueryMapParameterRules classRules,
                                       final @Nullable Object value) {
        QueryParameterNullValueRule rule = null;
        if (classRules != null) {
            rule = classRules.nullRule();
        }
        if (queryMapParameter != null && !queryMapParameter.nullRule().equals(QueryParameterNullValueRule.NONE)) {
            rule = queryMapParameter.nullRule();
        }
        final QueryParameterNullValueRule parameterValueRule = QueryParameterNullValueRule.valueOf(value);
        if (value != null) {
            if (parameterValueRule != null) {
                rule = parameterValueRule;
            } else {
                return value;
            }
        }
        if (rule == QueryParameterNullValueRule.RULE_NULL_MARKER) {
            return new String(new byte[]{0});
        } else if (rule == QueryParameterNullValueRule.RULE_EMPTY_STRING) {
            return "";
        } else if (rule == QueryParameterNullValueRule.RULE_NULL_STRING) {
            return "null";
        } else {
            return null;
        }
    }

}
