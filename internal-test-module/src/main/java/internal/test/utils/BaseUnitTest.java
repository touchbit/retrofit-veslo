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

package internal.test.utils;

import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static internal.test.utils.TestUtils.array;

public abstract class BaseUnitTest {

    protected static final Annotation[] AA = array();
    protected static final Retrofit RTF = RetrofitUtils.retrofit();
    protected static final Type OBJ_T = Object.class;
    protected static final Type STRING_T = String.class;
    protected static final Type CHARACTER_T = Character.class;
    protected static final Type BOOLEAN_T = Boolean.class;
    protected static final Type BYTE_T = Byte.class;
    protected static final Type INTEGER_T = Integer.class;
    protected static final Type DOUBLE_T = Double.class;
    protected static final Type FLOAT_T = Float.class;
    protected static final Type LONG_T = Long.class;
    protected static final Type SHORT_T = Short.class;
    protected static final Type PRIMITIVE_CHARACTER_T = Character.TYPE;
    protected static final Type PRIMITIVE_BOOLEAN_T = Boolean.TYPE;
    protected static final Type PRIMITIVE_BYTE_T = Byte.TYPE;
    protected static final Type PRIMITIVE_INTEGER_T = Integer.TYPE;
    protected static final Type PRIMITIVE_DOUBLE_T = Double.TYPE;
    protected static final Type PRIMITIVE_FLOAT_T = Float.TYPE;
    protected static final Type PRIMITIVE_LONG_T = Long.TYPE;
    protected static final Type PRIMITIVE_SHORT_T = Short.TYPE;
    protected static final Type STRING_SET_T = RealGenericTypes.getGenericReturnTypeForMethod("stringSetType");
    protected static final Type STRING_LIST_T = RealGenericTypes.getGenericReturnTypeForMethod("stringListType");
    protected static final Type STRING_MAP_T = RealGenericTypes.getGenericReturnTypeForMethod("stringMapType");
    protected static final Type STRING_ARRAY_T = String[].class;

    @SuppressWarnings("unused")
    private interface RealGenericTypes {

        Set<String> stringSetType();

        List<String> stringListType();

        Map<String, String> stringMapType();

        String[] stringArrayType();

        static Type getGenericReturnTypeForMethod(String methodName) {
            try {
                return RealGenericTypes.class.getDeclaredMethod(methodName).getGenericReturnType();
            } catch (Exception e) {
                throw new CorruptedTestException(e);
            }
        }

        static Type getReturnTypeForMethod(String methodName) {
            try {
                return RealGenericTypes.class.getDeclaredMethod(methodName).getReturnType();
            } catch (Exception e) {
                throw new CorruptedTestException(e);
            }
        }

    }

}
