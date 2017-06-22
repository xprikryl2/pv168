package dragonrental.backend;

import dragonrental.common.DragonInUseException;
import dragonrental.common.ServiceFailureException;
import java.util.List;

/**
 * This class allows reservation manipulation in connection with database.
 *
 * @author Zuzana Wolfová
 */
public interface ReservationManager {

    /**
     * finds a reservation with given ID
     * @param id
     * @return Reservation object, or null if no reservation was found
     */    
    public Reservation getReservation(Long id);
    
    /**
     * This method adds new reservation to the database and generates id.
     * borrower, dragon, from, pricePerHour and moneyPaid must be set
     * @param reservation to be created
     * @throws DragonInUseException when the dragon in already in use in the time interval
     * @throws ServiceFailureException when database fails
     */
    public void createReservation(Reservation reservation)  throws DragonInUseException;

    /**
     * This method removes reservation from the database.
     * @param reservation to be removed
     * @throws ServiceFailureException when database fails
     */
    public void removeReservation(Reservation reservation);

    /**
     * This method updates the reservation.
     * borrower, dragon and from cannot be changed
     * @param reservation to be updated
     * @throws DragonInUseException when the dragon in already in use in the time interval
     */
    public void updateReservation(Reservation reservation)  throws DragonInUseException;

    /**
     * This method lists all reservations stored in the database.
     * @return list of all reservations in the database
     */
    public List<Reservation> listAllReservations();

    /**
     * This method searches for a reservation in database using various criteria.
     * @param filter
     * @return list of all reservations matching searching criteria
     */
    public List<Reservation> findReservation(ReservationFilter filter);
}
