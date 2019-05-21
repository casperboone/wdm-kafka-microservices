package nl.tudelft.wdm.group1.stock;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidStockChangeException extends Exception{
    public InvalidStockChangeException(final String message) { super(message); }
}
