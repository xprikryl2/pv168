package dragonrental.backend;
import dragonrental.common.DBUtils;
import dragonrental.common.DragonInUseException;
import dragonrental.common.IllegalEntityException;
import dragonrental.common.ServiceFailureException;
import dragonrental.common.Validation;
import dragonrental.common.ValidationException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.*;
/**
 * @author Petr Soukop
 */
public class ReservationManagerImpl implements ReservationManager {
    
    private DataSource dataSource;
    final static Logger LOG = LoggerFactory.getLogger(ReservationManagerImpl.class);
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private void checkDataSource() {
        if (dataSource == null) {
            LOG.error("DataSource is not set");
            throw new IllegalStateException("DataSource is not set");
        }
    }
    
    @Override
    public Reservation getReservation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID == null");
        }
        List<Reservation> reservations = findReservation(new ReservationFilter().withId(id));
        if (reservations.isEmpty()) {
            LOG.debug("Didn't find reservation with ID: " + id);
            return null;
        }
        if (reservations.size() > 1) {
            String msg = "Found more than 1 reservations with null" + id;
            LOG.error(msg);
            throw new IllegalEntityException(msg);
        }
        return reservations.get(0);
    }
        
    @Override
    public void createReservation(Reservation reservation) throws DragonInUseException {
        LOG.info("Adding reservation: " + reservation);
        checkDataSource();
        
        String errorMsg = Validation.validateReservationForCreating(reservation);
        if(errorMsg != null) {
            LOG.error(errorMsg);
            throw new ValidationException(errorMsg);
        }
        
        if ( ! findReservation(
                    new ReservationFilter()
                            .withDragon(reservation.getDragon().getId())
                            .withFromIsBefore(reservation.getTo())
                            .withToIsAfterOrNull(reservation.getFrom())
                                    ).isEmpty() ) {
            String msg = "Dragon " + reservation.getDragon().getName() + " already in use";
            LOG.info(msg);
            throw new DragonInUseException(msg);
        }
        
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            
            connection.setAutoCommit(false);
            
            statement = connection.prepareStatement(
                    "INSERT INTO Reservation "
                  + "(timeFrom, timeTo,borrower,dragon,moneyPaid,pricePerHour)"
                  + " VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS
                                                    );
            statement.setTimestamp(1, Timestamp.valueOf(reservation.getFrom()));
            if (reservation.getTo() != null) {
                statement.setTimestamp(2, Timestamp.valueOf(reservation.getTo()));
            } else {
            statement.setTimestamp(2, null);
            }
            statement.setLong(3, reservation.getBorrower().getId());
            statement.setLong(4, reservation.getDragon().getId());
            statement.setBigDecimal(5, reservation.getMoneyPaid());
            statement.setBigDecimal(6, reservation.getPricePerHour());
            int updateCount = statement.executeUpdate();
            DBUtils.checkUpdatesCount(updateCount, reservation, true);
            Long id = DBUtils.getId(statement.getGeneratedKeys());
            reservation.setId(id);
            connection.commit();
        } catch(SQLException ex) {
            String msg = "Service failure when trying to create reservation" + reservation;
            LOG.error(msg);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, statement);
        }
    }
    
    @Override
    public void removeReservation(Reservation reservation) {
        LOG.info("Removing reservation: " + reservation);
        
        checkDataSource();
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation == null");
        }
        if (reservation.getId() == null) {
            throw new ValidationException("Reservation has null id");
        }
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(
                    "DELETE FROM Reservation WHERE id = ?");
            statement.setLong(1, reservation.getId());
            
            int updateCount = statement.executeUpdate();
            DBUtils.checkUpdatesCount(updateCount, reservation, false);
            connection.commit();
        } catch(SQLException ex) {
            String msg = "Service failure when trying to remove reservation" + reservation;
            LOG.error(msg);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, statement);
        }
    }
    
    @Override
    public void updateReservation(Reservation reservation) throws DragonInUseException  {
        LOG.info("Updating reservation" + reservation);
        checkDataSource();
        if (reservation == null) {
            throw new IllegalArgumentException("Trying to update with null");
        }
        if (reservation.getId() == null) {
            throw new ValidationException("Reservation has no ID");
        }
        
        String errorMsg = Validation.validateReservationForUpdating(getReservation(reservation.getId()), reservation);
        if(errorMsg != null) {
            LOG.error(errorMsg);
            throw new ValidationException(errorMsg);
        }
        
        List<Reservation> conflictingReservations = findReservation(
                    new ReservationFilter()
                            .withDragon(reservation.getDragon().getId())
                            .withFromIsBefore(reservation.getTo())
                            .withToIsAfterOrNull(reservation.getFrom())
                                    );
        conflictingReservations.removeIf(r -> r.getId().equals(reservation.getId()));
        if ( ! conflictingReservations.isEmpty() ) {
            String msg = "Dragon " + reservation.getDragon().getName() + " in use";
            LOG.info(msg);
            throw new DragonInUseException(msg);
        }
        
        Connection connection = null;
        PreparedStatement statement= null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(
                    "UPDATE Reservation SET timeTo = ?, moneyPaid = ?, pricePerHour = ? WHERE id = ?");
            statement.setTimestamp(1, (reservation.getTo() == null) ? null : Timestamp.valueOf(reservation.getTo()));
            statement.setBigDecimal(2, reservation.getMoneyPaid());
            statement.setBigDecimal(3, reservation.getPricePerHour());
            statement.setLong(4, reservation.getId());
            int updateCount = statement.executeUpdate();
            DBUtils.checkUpdatesCount(updateCount, reservation, false);
            connection.commit();
        } catch(SQLException ex) {
            String msg = "Service failure when trying to update reservation" + reservation;
            LOG.error(msg);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(connection);
            DBUtils.closeQuietly(connection, statement);
        }
    }
    
    @Override
    public List<Reservation> listAllReservations() {
        return findReservation(new ReservationFilter());
    }
    
    @Override
    public List<Reservation> findReservation(ReservationFilter filter) {
        checkDataSource();
        if (filter == null) {
            throw new IllegalArgumentException("Filter is null");
        }
        LOG.debug("Finding reservations " + filter.toSQL() == null ? "" : "WHERE "+ filter.toSQL());
        try(Connection connection = dataSource.getConnection()) {
            try(
                    PreparedStatement findIdSt = connection.prepareStatement(
                    "SELECT * FROM Reservation" +
                    (filter.toSQL().equals("") ? "" : " WHERE ") + filter.toSQL())
                ) {
                try(ResultSet resultSet = findIdSt.executeQuery()) {
                    List<Reservation> reservations = parsedResultSet(resultSet);
                    return reservations;
                }
            }
        } catch(SQLException ex) {
            String msg = "Service failure when trying to find reservations \n"
                    + ex.getMessage();
            LOG.error(msg);
            throw new ServiceFailureException(msg, ex);
        }
    }
    
    //-------------------------------------------------------------------------
    
    private List<Reservation> parsedResultSet(ResultSet resultSet) throws SQLException {
        PersonManagerImpl personManager = new PersonManagerImpl();
        personManager.setDataSource(dataSource);
                                                //this null here is a bit dodgey
                         //but it suffices the contract and the clock isn't used
        DragonManagerImpl dragonManager = new DragonManagerImpl(null);
        dragonManager.setDataSource(dataSource);
        
        List<Reservation> reservationList = new ArrayList<>();
        while (resultSet.next()) {
            Reservation res = new Reservation();
            res.setId(resultSet.getLong("id"));
            res.setFrom(resultSet.getTimestamp("timeFrom").toLocalDateTime());
                Timestamp timeTo = resultSet.getTimestamp("timeTo");
            if (timeTo == null) {
                res.setTo(null);
            } else {
                res.setTo(timeTo.toLocalDateTime());
            }
            Person borrower = personManager.getPersonById(resultSet.getLong("borrower"));
            if (borrower == null) {
                String msg = "Error when parsing SQL result: Reservation has borrower == null";
                LOG.error(msg);
                throw new ValidationException(msg); 
            }
            res.setBorrower(borrower);
            Dragon dragon = dragonManager.getDragonById(resultSet.getLong("dragon"));
            if (dragon == null) {
                String msg = "Error when parsing SQL result: Reservation has dragon == null";
                LOG.error(msg);
                throw new ValidationException(msg);
            }
            res.setDragon(dragon);
            res.setMoneyPaid(resultSet.getBigDecimal("moneyPaid"));
            res.setPricePerHour(resultSet.getBigDecimal("pricePerHour"));
            reservationList.add(res);
        }
        return reservationList;
    }
    
    
}