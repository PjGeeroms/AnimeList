package validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String>
{
    @Override
    public void initialize(ValidUsername constraintAnnotation)
    {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context)
    {
        return username != null && username.matches("[a-zA-Z0-9]{4,255}+");
    }
}
