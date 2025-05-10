package kz.molten.techshop.orderservice.api.exception;

public class UserInsufficientRoleException extends RuntimeException {
    public UserInsufficientRoleException(String message) {
        super(message);
    }
}
