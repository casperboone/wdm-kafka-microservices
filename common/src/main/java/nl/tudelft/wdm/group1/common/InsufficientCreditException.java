package nl.tudelft.wdm.group1.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class InsufficientCreditException extends Exception {
    public InsufficientCreditException(String message) {
        super(message);
    }
}
