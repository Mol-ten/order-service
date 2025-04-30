package kz.molten.techshop.orderservice.api.exception;

public class WebClientInternalServerException extends RuntimeException{
    public WebClientInternalServerException(String message) {
        super(message);
    }
}
