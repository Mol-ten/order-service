package kz.molten.techshop.orderservice.exception;

public class WebClientInternalServerException extends RuntimeException{
    public WebClientInternalServerException(String message) {
        super(message);
    }
}
