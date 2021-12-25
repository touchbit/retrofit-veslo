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

package org.touchbit.retrofit.veslo.jsr;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.touchbit.retrofit.veslo.exception.BriefAssertionError;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An interface for checking JavaBean specification/contract compliance.
 * <p>
 *
 * @author Oleg Shaburov (shaburov.o.a@gmail.com)
 * Created: 15.11.2021
 */
public interface BeanValidation<DTO> {

    /**
     * Method provides an easy way of ensuring that the properties of your JavaBean(s)
     * have the right values in them (compliance to the specification/contract).
     *
     * @return {@link DTO} model
     */
    @SuppressWarnings("unchecked")
    default DTO assertConsistency() {
        Logger.getLogger("org.hibernate.validator.internal.util").setLevel(Level.OFF);
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        final Validator validator = validatorFactory.getValidator();
        final Set<ConstraintViolation<Object>> constraintViolations = validator.validate(this);
        final List<Throwable> errors = new ArrayList<>();
        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
            String modelInfo = "Model property: " + this.getClass().getSimpleName();
            String message = modelInfo + "." + constraintViolation.getPropertyPath() + "\n" +
                    "Expected: " + constraintViolation.getMessage() + "\n" +
                    "  Actual: " + constraintViolation.getInvalidValue() + "\n";
            errors.add(new BriefAssertionError(message));
        }
        if (!errors.isEmpty()) {
            final StringJoiner resultMessage = new StringJoiner("\n");
            errors.forEach(e -> resultMessage.add(e.getMessage()));
            throw new BriefAssertionError(resultMessage.toString());
        }
        return (DTO) this;
    }

}
