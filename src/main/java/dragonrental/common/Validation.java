package dragonrental.common;

import dragonrental.backend.*;import java.time.LocalDate;
import org.apache.commons.validator.routines.EmailValidator;

/**
 *
 
 */
public class Validation {
    
    public static String validateReservationForCreating(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation == null");
        }
        if (reservation.getId() != null) {
            return "Reservation has ID already set";
        }
        if (reservation.getBorrower() == null) {
            return "Reservation has no borrower set";
        }
        if (reservation.getDragon() == null) {
            return "Reservation has no dragon set";
        }
        if (reservation.getFrom() == null) {
            return "Reservation has no time from set";
        }
        if (reservation.getTo() != null && 
                reservation.getTo().isBefore(reservation.getFrom())) {
            return "Time to is before Time from";
        }
        if (reservation.getMoneyPaid() == null) {
            return "Reservation has moneyPaid == null";
        }
        if (reservation.getPricePerHour() == null) {
            return "Reservation has pricePerHour == null";
        }
        if (reservation.getPricePerHour().signum() == -1) {
            return "Reservation has negative price per hour";
        }
        if (reservation.getMoneyPaid().signum() == -1) {
            return "Reservation has negative money paid";
        }
        return null;
    }
    
    public static String validateReservationForUpdating(Reservation existingRes, Reservation newRes) {
        if (!existingRes.getFrom().equals(newRes.getFrom())) {
            return "Time from cannot be changed";
        }
        if (newRes.getTo() != null && 
                newRes.getTo().isBefore(existingRes.getFrom())) {
            return "Time to is before Time from";
        }
        if (!existingRes.getBorrower().equals(newRes.getBorrower())) {
            return "Borrwer cannot be changed";
        }
        if (!existingRes.getDragon().equals(newRes.getDragon())) {
            return "Dragon cannot be changed";
        }
        if (newRes.getPricePerHour().signum() == -1) {
            return "Reservation has negative price per hour";
        }
        if (newRes.getMoneyPaid().signum() == -1) {
            return "Reservation has negative money paid";
        }
        return null;
    }
    
    public static String personValidator(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person is null");
        }
        if (person.getName() == null || person.getName().isEmpty()) {
            return "Name is null or empty";
        }
        if (person.getSurname() == null || person.getSurname().isEmpty()) {
            return "Surname is null or empty";
        }
        if (person.getEmail() == null || person.getEmail().isEmpty()) {
            return "Email is null or empty";
        }
        if(!EmailValidator.getInstance().isValid(person.getEmail())) {
            return "Email address is invalid.";
        }
        return null;
    }
    
    public static String dragonValidator(Dragon dragon) {
        if (dragon == null) {
            throw new IllegalArgumentException("Dragon is null.");
        }
        if (dragon.getName() == null || dragon.getName().isEmpty()) {
            return "Dragon name is null or empty.";
        }
        if (dragon.getElement() == null) {
            return "Dragon element is empty.";
        }
        if (dragon.getMaximumSpeed() == null) {
            return "Dragon speed is empty.";
        }
        if (dragon.getMaximumSpeed().compareTo(0) < 0) {
            return "Dragon speed is negative.";
        }
        if (dragon.getDateOfBirth() == null) {
            return "Dragon date of birth is null.";
        }
        if (dragon.getDateOfBirth().isAfter(LocalDate.now())) {
            return "Dragon date of birth is after today.";
        }
        return null;
    }

}
