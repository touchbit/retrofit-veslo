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

package veslo.util;

import org.touchbit.www.form.urlencoded.marshaller.util.FormUrlUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

public abstract class ExceptionBuilder<E extends RuntimeException> {

    /***/
    protected static final String L_DELIMITER = "\n     - ";
    /***/
    private final StringJoiner additionalInfo = new StringJoiner("\n");
    /***/
    private String errorMessage = "\n";
    /***/
    private Exception cause = null;

    /**
     * @return heirs of RuntimeException
     */
    public abstract E build();

    /**
     * @param errorMessage {@link RuntimeException#getMessage()}
     * @return this
     */
    public ExceptionBuilder<E> errorMessage(final String errorMessage) {
        this.errorMessage = errorMessage + "\n";
        return this;
    }


    /**
     * Add additional info to Exception message
     *
     * @param object nullable object
     * @return this
     */
    public ExceptionBuilder<E> object(final Object object) {
        return object(value(Object::getClass, object));
    }

    /**
     * Add additional info to Exception message
     *
     * @param type nullable {@link Type}
     * @return this
     */
    public ExceptionBuilder<E> object(final Type type) {
        getAdditionalInfo().add("    Object: " + value(Type::getTypeName, type));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param field nullable {@link Field}
     * @return this
     */
    public ExceptionBuilder<E> field(final Field field) {
        getAdditionalInfo().add("    Field: " + value(this::getFieldInfo, field));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param constructedType nullable {@link Type}
     * @return this
     */
    public ExceptionBuilder<E> constructedType(final Type constructedType) {
        getAdditionalInfo().add("    Constructed type: " + value(Type::getTypeName, constructedType));
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param args the array of arguments
     * @return this
     */
    public ExceptionBuilder<E> constructorArguments(final Object... args) {
        final String prefix = args == null || args.length == 0 ? " <absent>" : L_DELIMITER;
        final StringJoiner argsInfo = new StringJoiner(L_DELIMITER, prefix, "");
        if (args != null) {
            Arrays.stream(args).forEach(arg -> argsInfo.add(value(Object::toString, arg)));
        }
        getAdditionalInfo().add("    Constructor arguments:" + argsInfo);
        return this;
    }

    /**
     * Add additional info to Exception message
     *
     * @param exception nullable {@link Exception}
     * @return this
     */
    public ExceptionBuilder<E> errorCause(final Exception exception) {
        final List<Throwable> causes = getNestedCauses(exception);
        final String message;
        if (causes.isEmpty()) {
            message = " <absent>";
        } else {
            final StringJoiner stringJoiner = new StringJoiner(L_DELIMITER, L_DELIMITER, "");
            for (Throwable throwable : causes) {
                final String cMsg = throwable.getMessage();
                final String cCls = throwable.getClass().getSimpleName();
                stringJoiner.add(cCls + ": " + cMsg);
            }
            message = stringJoiner.toString();
        }
        this.additionalInfo.add("    Error cause:" + message);
        this.cause = exception;
        return this;
    }

    /**
     * @return built exception message
     */
    public String getMessage() {
        return "\n  " + getErrorMessage() + getAdditionalInfo().add("");
    }

    /**
     * @return nullable {@link Exception} cause
     */
    public Exception getCause() {
        return cause;
    }

    /**
     * @return error message without additional info
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return additional info without error message
     */
    public StringJoiner getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * @param function any function
     * @param rawValue nullable function incoming parameter value
     * @param <T>      function incoming parameter type
     * @param <R>      function return type
     * @return function result or null if rawValue is null
     */
    protected <T, R> R value(final Function<T, R> function, final T rawValue) {
        return rawValue == null ? null : function.apply(rawValue);
    }

    /**
     * @param field {@link Field}
     * @return field info in format {@code private String foo;}
     */
    protected String getFieldInfo(final Field field) {
        return new StringJoiner(" ")
                .add(Modifier.toString(field.getModifiers()))
                .add(FormUrlUtils.getGenericSimpleName(field))
                .add(field.getName() + ";")
                .toString();
    }

    /**
     * Collect nested exception causes
     *
     * @param throwable nullable error
     * @return List of nested exception causes
     */
    protected List<Throwable> getNestedCauses(final Throwable throwable) {
        final List<Throwable> result = new ArrayList<>();
        if (throwable != null) {
            result.add(throwable);
            if (throwable.getCause() != null) {
                result.addAll(getNestedCauses(throwable.getCause()));
            }
        }
        return result;
    }
    
}
