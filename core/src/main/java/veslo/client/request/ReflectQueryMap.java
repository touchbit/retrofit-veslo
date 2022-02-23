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

import org.apache.commons.lang3.reflect.FieldUtils;
import retrofit2.internal.EverythingIsNonNull;
import veslo.QueryMapException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
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
        for (Field declaredField : this.getClass().getDeclaredFields()) {
            final QueryMapParameterRules classRules = this.getClass().getAnnotation(QueryMapParameterRules.class);
            final QueryMapParameter queryMapParameter = declaredField.getAnnotation(QueryMapParameter.class);
            final String declaredFieldName = declaredField.getName();
            // ignore jacocoData fields and types implements ReflectQueryMap (protection from StackOverflowError)
            if (ReflectQueryMap.class.isAssignableFrom(declaredField.getType()) ||
                    declaredFieldName.contains("jacocoData")) {
                continue;
            }
            final Object declaredFieldValue = readField(declaredField.getName());
            final String parameterName = getParameterName(queryMapParameter, classRules, declaredFieldName);
            final Object parameterValue = getParameterValue(queryMapParameter, classRules, declaredFieldValue);
            if (parameterValue != null) {
                this.put(parameterName, parameterValue);
            }
        }
        return super.entrySet();
    }

    protected Object readField(String fieldName) {
        try {
            return FieldUtils.readField(this, fieldName, true);
        } catch (Exception e) {
            throw new QueryMapException("Unable to get value from field: " + fieldName, e);
        }
    }

    @EverythingIsNonNull
    protected String getParameterName(final @Nullable QueryMapParameter queryMapParameter,
                                      final @Nullable QueryMapParameterRules classRules,
                                      final @Nonnull String declaredFieldName) {
        if (queryMapParameter != null && !queryMapParameter.name().trim().isEmpty()) {
            return queryMapParameter.name();
        } else {
            if (classRules != null) {
                return classRules.caseRule().format(declaredFieldName);
            } else {
                return declaredFieldName;
            }
        }
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
