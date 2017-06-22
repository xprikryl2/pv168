package dragonrental.common;

/**
 * This exception is thrown when attempting to create a reservation with a dragon,
 * which is in use for that time
 *
 * @author Petr Soukop
 */
public class DragonInUseException extends Exception {
    
    public DragonInUseException() {
        
    }
    
    public DragonInUseException(String message) {
        super(message);
    }
    
    public DragonInUseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DragonInUseException(Throwable cause) {
        super(cause);
    }
}
