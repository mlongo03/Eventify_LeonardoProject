package com.eventify.app.validator;

import com.eventify.app.exception.ObjectValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ObjectsValidator<T> {

  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final Validator validator = factory.getValidator();

  public void validate(T objectToValidate) {
    Set<ConstraintViolation<T>> violations =  validator.validate(objectToValidate);

    if (!violations.isEmpty()) {
      Set<String> errorMsg = violations
          .stream()
          .map(ConstraintViolation::getMessage)
          .collect(Collectors.toSet());
        for (String error : errorMsg) {
          System.out.println(error);
        }
      throw new ObjectValidationException(errorMsg, objectToValidate.getClass().getSimpleName());
    }
  }
}
