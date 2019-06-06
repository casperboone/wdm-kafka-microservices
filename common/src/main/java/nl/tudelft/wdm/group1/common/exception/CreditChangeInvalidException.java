package nl.tudelft.wdm.group1.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class CreditChangeInvalidException extends Exception {
    public CreditChangeInvalidException(String message) {
        super(message);
    }
}
