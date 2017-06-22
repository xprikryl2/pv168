package dragonrental.backend;

import dragonrental.common.DBUtils;
import dragonrental.common.DragonInUseException;
import dragonrental.common.IllegalEntityException;
import dragonrental.common.ServiceFailureException;
import dragonrental.common.ValidationException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.*;

import static org.assertj.core.api.Assertions.*;
import org.assertj.core.api.Fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Petr Soukop
 */
public class ReservationManagerImplTest {
    
    private ReservationManagerImpl rManager;
    private DataSource ds;
    private PersonManagerImpl pManager;
    private DragonManagerImpl dManager;
    private Person bob = sampleBobPerson().build();
    private Person helmut = sampleHelmutPerson().build();
    private Dragon grigori = sampleGrigoriDragon().build();
    private Dragon icey = sampleIceyDragon().build();
        
    
    //constant for fixed current time
    //it's exactly 12:00 on the 1st of January, year 1450
    private final static LocalDateTime NOW = 
            LocalDateTime.of(1450, Month.JANUARY, 1, 12, 0, 0, 0);
    
    //-------------------------------------------------------------------------
    
    private PersonBuilder sampleBobPerson() {
        return new PersonBuilder()
                .withId(null)
                .withName("Bob")
                .withSurname("D'Builder")
                .withEmail("sleep@work.com");
    }
    
    private PersonBuilder sampleHelmutPerson() {
        return new PersonBuilder()
                .withId(null)
                .withName("Helmut")
                .withSurname("Schwartz")
                .withEmail("schwartz.helmut@email.de");
    }
    
    private DragonBuilder sampleGrigoriDragon() {
        return new DragonBuilder()
                .withId(null)
                .withName("Grigori")
                .withDateOfBirth(LocalDate.of(1150, Month.JUNE, 12))
                .withElement(DragonElement.FIRE)
                .withMaximumSpeed(200);
    }
    
    private DragonBuilder sampleIceyDragon() {
        return new DragonBuilder()
                .withId(null)
                .withName("Icey")
                .withDateOfBirth(LocalDate.of(1175, Month.NOVEMBER, 7))
                .withElement(DragonElement.WATER)
                .withMaximumSpeed(150);
    }
    
    //a valid reservation to be created
    private ReservationBuilder sampleReservation1() {
        return new ReservationBuilder()
                .withId(null)
                .withBorrower(helmut)
                .withDragon(grigori)
                .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 1, 12, 0))
                .withTo(LocalDateTime.of(1450, Month.FEBRUARY, 16, 12, 0))
                .withMoneyPaid(new BigDecimal(0))
                .withPricePerHour(new BigDecimal(50));
    }
    
    //another valid reservation to be created
    private ReservationBuilder sampleReservation2() {
        return new ReservationBuilder()
                .withId(null)
                .withBorrower(bob)
                .withDragon(icey)
                .withFrom(LocalDateTime.of(1450, Month.MARCH, 25, 12, 0))
                .withTo(LocalDateTime.of(1450, Month.APRIL, 1, 12, 0))
                .withMoneyPaid(new BigDecimal(300))
                .withPricePerHour(new BigDecimal(60));
    }
    
    //a valid reservation with the minimum parameters
    private ReservationBuilder sampleReservation3() {
        return new ReservationBuilder()
                .withId(null)
                .withBorrower(helmut)
                .withDragon(icey)
                .withFrom(LocalDateTime.of(1450, Month.JUNE, 5, 12, 0))
                .withTo(null)
                .withMoneyPaid(new BigDecimal(0))
                .withPricePerHour(new BigDecimal(40));
    }
    
    //-------------------------------------------------------------------------
    
    //to make a Clock object with a fixed time given by NOW constant
    //Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
    
    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:reservationManager-test");
        ds.setCreateDatabase("create");
        return ds;
    }
    
    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds,ReservationManager.class.getResource("createTables.sql"));
        rManager = new ReservationManagerImpl();
        rManager.setDataSource(ds);
        pManager = new PersonManagerImpl();
        pManager.setDataSource(ds);
        dManager = new DragonManagerImpl(Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
        dManager.setDataSource(ds);
        pManager.addPerson(bob);
        pManager.addPerson(helmut);
        dManager.createDragon(grigori);
        dManager.createDragon(icey);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds,ReservationManager.class.getResource("dropTables.sql"));
    }
    
    //-------------------------------------------------------------------------
    
    @Test
    public void testGetReservation() {
        try {
            Reservation reservation = sampleReservation1().build();
            
            rManager.createReservation(reservation);
            assertThat( rManager.getReservation(reservation.getId()) )
                    .isEqualToComparingFieldByField(reservation);
            
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
    }
    
    @Test
    public void testGetNonExistingReservation() {
        assertThat(rManager.getReservation(1234L))
                    .isNull();
    }
    
    //-------------------------------------------------------------------------
    
    @Test
    public void testCreateValidReservation() {
        Reservation reservation = sampleReservation1()
                    .withId(null)
                    .build();
        
        try {
            rManager.createReservation(reservation);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        Long reservationId = reservation.getId();
            
        assertThat( reservationId )
                    .isNotNull();
        assertThat( rManager.getReservation( reservation.getId() ) )
                    .isNotSameAs(reservation)
                    .isEqualToComparingFieldByField(reservation);
    }
    
    @Test
    public void testCreateValidReservationWithNullTimeTo()  {
        Reservation reservation = sampleReservation1()
                    .withId(null)
                    .withTo(null)
                    .build();
        
        try {
            rManager.createReservation(reservation);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        Long reservationId = reservation.getId();
            
        assertThat( reservationId )
                    .isNotNull();
        assertThat( rManager.getReservation( reservation.getId() ) )
                    .isNotSameAs(reservation)
                    .isEqualToComparingFieldByField(reservation);
    }
    
    @Test
    public void testCreateNullReservation() {
        assertThatThrownBy( () -> rManager.createReservation(null) )
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void testCreateReservationWithExistingId() {
        Reservation reservation1 = sampleReservation1()
                .withId(1234L)
                .build();
        
        assertThatThrownBy( () -> rManager.createReservation(reservation1) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testCreateReservationWithNoTimeFrom() {
        Reservation reservation = sampleReservation1()
                .withFrom(null)
                .build();
        
        assertThatThrownBy( () -> rManager.createReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testCreateReservationWithNoDragon() {
        Reservation reservation = sampleReservation1()
                .withDragon(null)
                .build();
        
        assertThatThrownBy( () -> rManager.createReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testCreateReservationWithNoBorrower() {
        Reservation reservation = sampleReservation1()
                .withBorrower(null)
                .build();
        
        assertThatThrownBy( () -> rManager.createReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testCreateReservationWithNoMoneyPaid() {
        Reservation reservation = sampleReservation1()
                .withMoneyPaid(null)
                .build();
        
        assertThatThrownBy( () -> rManager.createReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testCreateReservationWithNegativeMoneyPaid() {
        Reservation reservation = sampleReservation1()
                .withMoneyPaid(new BigDecimal(-100))
                .build();
        
        assertThatThrownBy( () -> rManager.createReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testCreateReservationWithNoPricePerHour() {
        Reservation reservation = sampleReservation1()
                .withPricePerHour(null)
                .build();
        
        assertThatThrownBy( () -> rManager.createReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testCreateReservationWithNegativePricePerHour() {
        Reservation reservation = sampleReservation1()
                .withPricePerHour(new BigDecimal(-50))
                .build();
        
        assertThatThrownBy( () -> rManager.createReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testCreateReservationWithToBeforeFrom() {
        Reservation reservation = sampleReservation1()
                .withFrom(LocalDateTime.of(1450, Month.APRIL, 25, 12, 0))
                .withTo(LocalDateTime.of(1450, Month.APRIL, 10, 12, 0))
                .build();
        
        assertThatThrownBy( () -> rManager.createReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testCreateReservationWithLongTimeValue() {
        Reservation reservation = sampleReservation1()
                .withFrom(LocalDateTime.of(1450, Month.APRIL, 10, 12, 0, 0, 123456789))
                .withTo(LocalDateTime.of(1450, Month.APRIL, 15, 12, 0))
                .build();
        try{
            rManager.createReservation(reservation);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat(rManager.getReservation(reservation.getId()))
                    .isEqualToComparingFieldByField(reservation);
    }
    
    @Test
    public void testCreateReservationWithDragonOccupied() {
        Reservation reservation1 = sampleReservation1()
                .withDragon(grigori)
                .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 10, 12, 0))
                .withTo(LocalDateTime.of(1450, Month.FEBRUARY, 16, 12, 0))
                .build();
        
        Reservation reservation2 = sampleReservation1()
                .withDragon(grigori)
                .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 1, 12, 0))
                .withTo(LocalDateTime.of(1450, Month.FEBRUARY, 11, 12, 0))
                .build();
        
        Reservation reservation3 = sampleReservation1()
                .withDragon(grigori)
                .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 11, 12, 0))
                .withTo(LocalDateTime.of(1450, Month.FEBRUARY, 13, 12, 0))
                .build();
        
        Reservation reservation4 = sampleReservation1()
                .withDragon(grigori)
                .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 5, 12, 0))
                .withTo(LocalDateTime.of(1450, Month.FEBRUARY, 18, 12, 0))
                .build();
        
        Reservation reservation5 = sampleReservation1()
                .withDragon(grigori)
                .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 14, 12, 0))
                .withTo(LocalDateTime.of(1450, Month.FEBRUARY, 18, 12, 0))
                .build();
        
        Reservation reservation6 = sampleReservation1()
                .withDragon(grigori)
                .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 8, 12, 0))
                .withTo(null)
                .build();
        
        Reservation reservation7 = sampleReservation1()
                .withDragon(grigori)
                .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 12, 12, 0))
                .withTo(null)
                .build();
        
        try {
            rManager.createReservation(reservation1);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        
        assertThatThrownBy( () -> rManager.createReservation(reservation2) )
                .isInstanceOf(DragonInUseException.class);
        
        assertThatThrownBy( () -> rManager.createReservation(reservation3) )
                .isInstanceOf(DragonInUseException.class);
        
        assertThatThrownBy( () -> rManager.createReservation(reservation4) )
                .isInstanceOf(DragonInUseException.class);
        
        assertThatThrownBy( () -> rManager.createReservation(reservation5) )
                .isInstanceOf(DragonInUseException.class);
        
        assertThatThrownBy( () -> rManager.createReservation(reservation6) )
                .isInstanceOf(DragonInUseException.class);
        
        assertThatThrownBy( () -> rManager.createReservation(reservation7) )
                .isInstanceOf(DragonInUseException.class);
    }
    
    @Test
    public void testCreateReservationWithDragonOccupiedWithToNull() {
        Reservation reservation1 = sampleReservation1()
                .withDragon(grigori)
                .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 10, 12, 0))
                .withTo(null)
                .build();
        
        Reservation reservation2 = sampleReservation1()
                .withDragon(grigori)
                .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 1, 12, 0))
                .withTo(LocalDateTime.of(1450, Month.FEBRUARY, 11, 12, 0))
                .build();
        
        Reservation reservation3 = sampleReservation1()
                .withDragon(grigori)
                .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 11, 12, 0))
                .withTo(LocalDateTime.of(1450, Month.FEBRUARY, 13, 12, 0))
                .build();
        
        Reservation reservation4 = sampleReservation1()
                .withDragon(grigori)
                .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 5, 12, 0))
                .withTo(null)
                .build();
        
        Reservation reservation5 = sampleReservation1()
                .withDragon(grigori)
                .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 14, 12, 0))
                .withTo(null)
                .build();
                
        try {
            rManager.createReservation(reservation1);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        
        assertThatThrownBy( () -> rManager.createReservation(reservation2) )
                .isInstanceOf(DragonInUseException.class);
        
        assertThatThrownBy( () -> rManager.createReservation(reservation3) )
                .isInstanceOf(DragonInUseException.class);
        
        assertThatThrownBy( () -> rManager.createReservation(reservation4) )
                .isInstanceOf(DragonInUseException.class);
        
        assertThatThrownBy( () -> rManager.createReservation(reservation5) )
                .isInstanceOf(DragonInUseException.class);
        
    }
    
    @Test
    public void testCreateReservationWithDragonNotOccupiedAndNullTo() {
        Reservation reservation1 = sampleReservation1()
                    .withDragon(grigori)
                    .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 10, 12, 0))
                    .withTo(null)
                    .build();
            
            Reservation reservation2 = sampleReservation1()
                    .withDragon(grigori)
                    .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 1, 12, 0))
                    .withTo(LocalDateTime.of(1450, Month.FEBRUARY, 8, 12, 0))
                    .build();
            
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat(rManager.listAllReservations())
                    .usingFieldByFieldElementComparator()
                    .containsOnly(reservation1, reservation2);
    }
    
    @Test
    public void testCreateReservationWithDragonNotOccupied() {
        Reservation reservation1 = sampleReservation1()
                    .withDragon(grigori)
                    .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 10, 12, 0))
                    .withTo(LocalDateTime.of(1450, Month.FEBRUARY, 16, 12, 0))
                    .build();
            
            Reservation reservation2 = sampleReservation1()
                    .withDragon(grigori)
                    .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 1, 12, 0))
                    .withTo(LocalDateTime.of(1450, Month.FEBRUARY, 8, 12, 0))
                    .build();
            
            Reservation reservation3 = sampleReservation1()
                    .withDragon(grigori)
                    .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 19, 12, 0))
                    .withTo(LocalDateTime.of(1450, Month.FEBRUARY, 22, 12, 0))
                    .build();
            
            Reservation reservation4 = sampleReservation1()
                    .withDragon(grigori)
                    .withFrom(LocalDateTime.of(1450, Month.FEBRUARY, 25, 12, 0))
                    .withTo(null)
                    .build();
            
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
            rManager.createReservation(reservation3);
            rManager.createReservation(reservation4);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat(rManager.listAllReservations())
                    .usingFieldByFieldElementComparator()
                    .containsOnly(reservation1, reservation2, reservation3, reservation4);
    }
    
    //-------------------------------------------------------------------------
    
    @Test
    public void testUpdateReservation() {
        Reservation reservation = sampleReservation3()
                    .withTo(null)
                    .withMoneyPaid(new BigDecimal(0))
                    .withPricePerHour(new BigDecimal(10))
                    .build();
            Reservation anotherReservation = sampleReservation1().build();
        
        try {
            rManager.createReservation(reservation);
            rManager.createReservation(anotherReservation);
            
            reservation.setTo(LocalDateTime.of(1450, Month.JUNE, 25, 12, 0));
            reservation.setMoneyPaid(new BigDecimal(200));
            reservation.setPricePerHour(new BigDecimal(20));
            
            rManager.updateReservation(reservation);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat( rManager.getReservation(reservation.getId()) )
                    .isEqualToComparingFieldByField(reservation);
        assertThat( rManager.getReservation(anotherReservation.getId()) )
                    .isEqualToComparingFieldByField(anotherReservation);
        
    }
    
    @Test
    public void testUpdateReservationWithDifferentTimeTo() {
        Reservation reservation = sampleReservation1()
                    .withFrom( LocalDateTime.of(1450, Month.FEBRUARY, 10, 12, 0) )
                    .withTo( LocalDateTime.of(1450, Month.FEBRUARY, 16, 12, 0) )
                    .build();
        
        try {
            rManager.createReservation(reservation);
            
            reservation.setTo( LocalDateTime.of(1450, Month.FEBRUARY, 15, 12, 0) );
            
            rManager.updateReservation(reservation);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat( rManager.getReservation(reservation.getId()) )
                    .isEqualToComparingFieldByField(reservation);
    }
    
    @Test
    public void testUpdateReservationWithNullId() {
        Reservation reservation = sampleReservation1()
                .withId(null)
                .build();
        
        assertThatThrownBy( () -> rManager.updateReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testUpdateNullReservation() {
        assertThatThrownBy( () -> rManager.updateReservation(null) )
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void testUpdateReservationWithTimeFrom() {
        Reservation reservation = sampleReservation1().build();
        
        try {
            rManager.createReservation(reservation);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        
        reservation.setFrom(LocalDateTime.of(1451, Month.FEBRUARY, 10, 12, 0));
        
        assertThatThrownBy( () -> rManager.updateReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testUpdateReservationWithPerson() {
        Reservation reservation = sampleReservation1()
                .withBorrower(bob)
                .build();
        
        try {
            rManager.createReservation(reservation);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        
        reservation.setBorrower(helmut);
        
        assertThatThrownBy( () -> rManager.updateReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testUpdateReservationWithDragon() {
        Reservation reservation = sampleReservation1()
                .withDragon(grigori)
                .build();
        
        try {
            rManager.createReservation(reservation);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        
        reservation.setDragon(icey);
        
        assertThatThrownBy( () -> rManager.updateReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testUpdateReservationWithToBeforeFrom() {
        Reservation reservation = sampleReservation1()
                .withFrom( LocalDateTime.of(1450, Month.FEBRUARY, 10, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.FEBRUARY, 16, 12, 0) )
                .build();
        
        try {
            rManager.createReservation(reservation);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        reservation.setTo( LocalDateTime.of(1450, Month.FEBRUARY, 5, 12, 0) );
        assertThatThrownBy( () -> rManager.updateReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testUpdateReservationWithDragonOccupied() {
        Reservation reservation1 = sampleReservation1()
                .withDragon(grigori)
                .withFrom( LocalDateTime.of(1450, Month.FEBRUARY, 10, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.FEBRUARY, 16, 12, 0) )
                .build();
        
        Reservation reservation2 = sampleReservation1()
                .withDragon(grigori)
                .withFrom( LocalDateTime.of(1450, Month.FEBRUARY, 20, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.FEBRUARY, 26, 12, 0) )
                .build();
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        reservation1.setTo(LocalDateTime.of(1450, Month.FEBRUARY, 23, 12, 0));
        assertThatThrownBy( () -> rManager.updateReservation(reservation1) )
                .isInstanceOf(DragonInUseException.class);
    }
    
    @Test
    public void testUpdateReservationWithDragonOccupiedAndToNull() {
        Reservation reservation1 = sampleReservation1()
                .withDragon(grigori)
                .withFrom( LocalDateTime.of(1450, Month.FEBRUARY, 10, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.FEBRUARY, 16, 12, 0) )
                .build();
        
        Reservation reservation2 = sampleReservation1()
                .withDragon(grigori)
                .withFrom( LocalDateTime.of(1450, Month.FEBRUARY, 20, 12, 0) )
                .withTo(null)
                .build();
        
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        reservation1.setTo(LocalDateTime.of(1450, Month.FEBRUARY, 23, 12, 0));
        
        assertThatThrownBy( () -> rManager.updateReservation(reservation1) )
                .isInstanceOf(DragonInUseException.class);
    }
    
    @Test
    public void testUpdateReservationWithDragonNotOccupied() {
        Reservation reservation1 = sampleReservation1()
                .withDragon(grigori)
                .withFrom( LocalDateTime.of(1450, Month.FEBRUARY, 10, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.FEBRUARY, 16, 12, 0) )
                .build();
        
        Reservation reservation2 = sampleReservation1()
                .withDragon(grigori)
                .withFrom( LocalDateTime.of(1450, Month.FEBRUARY, 20, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.FEBRUARY, 23, 12, 0) )
                .build();
        
        Reservation reservation3 = sampleReservation1()
                .withDragon(grigori)
                .withFrom( LocalDateTime.of(1450, Month.FEBRUARY, 25, 12, 0) )
                .withTo(null)
                .build();
        
        Reservation reservation4 = sampleReservation1()
                .withDragon(grigori)
                .withFrom( LocalDateTime.of(1450, Month.FEBRUARY, 5, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.FEBRUARY, 8, 12, 0) )
                .build();
        
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
            rManager.createReservation(reservation3);
            rManager.createReservation(reservation4);
        
            reservation1.setTo(LocalDateTime.of(1450, Month.FEBRUARY, 18, 12, 0));
            rManager.updateReservation(reservation1);
            
            assertThat( rManager.getReservation(reservation1.getId()) )
                    .isEqualToComparingFieldByField(reservation1);
            
            reservation1.setTo(LocalDateTime.of(1450, Month.FEBRUARY, 14, 12, 0));
            rManager.updateReservation(reservation1);
            
            assertThat( rManager.getReservation(reservation1.getId()) )
                    .isEqualToComparingFieldByField(reservation1);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
    }
    
    @Test
    public void testUpdateReservationWithDragonNotOccupiedAndToNull() {
        Reservation reservation1 = sampleReservation1()
                .withDragon(grigori)
                .withFrom( LocalDateTime.of(1450, Month.FEBRUARY, 10, 12, 0) )
                .withTo(null)
                .build();
        
        Reservation reservation2 = sampleReservation1()
                .withDragon(grigori)
                .withFrom( LocalDateTime.of(1450, Month.FEBRUARY, 5, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.FEBRUARY, 8, 12, 0) )
                .build();
        
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
        
            reservation1.setTo(LocalDateTime.of(1450, Month.FEBRUARY, 18, 12, 0));
            rManager.updateReservation(reservation1);
            
            assertThat( rManager.getReservation(reservation1.getId()) )
                    .isEqualToComparingFieldByField(reservation1);
            
            reservation1.setTo(null);
            rManager.updateReservation(reservation1);
            
            assertThat( rManager.getReservation(reservation1.getId()) )
                    .isEqualToComparingFieldByField(reservation1);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
    }
    
    //-------------------------------------------------------------------------
    
    @Test
    public void testRemoveNullReservation() {
        assertThatThrownBy( () -> rManager.removeReservation(null) )
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void testRemoveReservationWithNullId() {
        Reservation reservation = sampleReservation1()
                .withId(null)
                .build();
        
        assertThatThrownBy( () -> rManager.removeReservation(reservation) )
                .isInstanceOf(ValidationException.class);
    }
    
    @Test
    public void testRemoveReservationLast() {
        Reservation reservation = sampleReservation1().build();
        
        try {        
            rManager.createReservation(reservation);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat( rManager.listAllReservations() )
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation);
        
        rManager.removeReservation(reservation);
        
        assertThat( rManager.listAllReservations() )
                .isEmpty();
    }
    
    public void testRemoveReservationSecondLast() {
        Reservation reservation1 = sampleReservation1().build();
        Reservation reservation2 = sampleReservation2().build();
        
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat( rManager.listAllReservations() )
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation1, reservation2);
        
        rManager.removeReservation(reservation1);
        
        assertThat( rManager.listAllReservations() )
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation2);
    }
    
    @Test
    public void testRemoveNonPresentReservation() {
        Reservation reservation1 = sampleReservation1().build();
        Reservation reservation2 = sampleReservation2()
                .withId(1234L)
                .build();
        
        try {
            rManager.createReservation(reservation1);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertThat( rManager.listAllReservations() )
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation1);
        assertThatThrownBy( () -> rManager.removeReservation(reservation2) )
                .isInstanceOf(IllegalEntityException.class);
        assertThat( rManager.listAllReservations() )
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation1);
    }

    //-------------------------------------------------------------------------
    
    @Test
    public void testListNoReservations() {
        assertThat( rManager.listAllReservations() )
                    .isEmpty();
        
    }
    
    @Test
    public void testListAllReservations() {
        Reservation reservation1 = sampleReservation1().build();
        Reservation reservation2 = sampleReservation2().build();
        
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat( rManager.listAllReservations() )
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation1, reservation2);
    }
    
    //-------------------------------------------------------------------------
 
    @Test
    public void testFindNullReservation() {
        assertThatThrownBy( () -> rManager.findReservation(null) )
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void testFindReservationForId() {
        Reservation reservation1 = sampleReservation1().build();
        Reservation reservation2 = sampleReservation2().build();
        
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat( rManager.findReservation( 
                new ReservationFilter().withId(reservation1.getId())
                                            ) )
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation1);
    }
    
    @Test
    public void testFindReservationForDragon() {
        Reservation reservation1 = sampleReservation1()
                .withDragon(grigori)
                .build();
        Reservation reservation2 = sampleReservation2()
                .withDragon(grigori)
                .build();
        Reservation reservation3 = sampleReservation3()
                .withDragon(icey)
                .build();
        
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
            rManager.createReservation(reservation3);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat( rManager.findReservation( 
                new ReservationFilter().withDragon(grigori.getId())
                                            ) )
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation1, reservation2);
    }
    
    @Test
    public void testFindReservationForPerson() {
        Reservation reservation1 = sampleReservation1()
                .withBorrower(bob)
                .build();
        Reservation reservation2 = sampleReservation2()
                .withBorrower(helmut)
                .build();
        Reservation reservation3 = sampleReservation3()
                .withBorrower(helmut)
                .build();
        
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
            rManager.createReservation(reservation3);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat( rManager.findReservation( 
                new ReservationFilter().withBorrower(helmut.getId())
                                            ) )
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation2, reservation3);
    }
    
    
    @Test
    public void testFindReservationAfterTimeFrom() {
        Reservation reservation1 = sampleReservation1()
                .withFrom( LocalDateTime.of(1450, Month.APRIL, 16, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.APRIL, 19, 12, 0) )
                .build();
        Reservation reservation2 = sampleReservation2()
                .withFrom( LocalDateTime.of(1450, Month.APRIL, 20, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.APRIL, 30, 12, 0) )
                .build();
        Reservation reservation3 = sampleReservation3()
                .withFrom( LocalDateTime.of(1450, Month.JUNE, 16, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.JULY, 6, 12, 0) )
                .build();
        
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
            rManager.createReservation(reservation3);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat( rManager.findReservation(
                new ReservationFilter().withFromIsAfter(LocalDateTime.of(1450, Month.APRIL, 20, 11, 0))
                                            ) )
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation2, reservation3);
    }
    
    @Test
    public void testFindReservationInATimeInterval() {
        Reservation reservation1 = sampleReservation1()
                .withFrom( LocalDateTime.of(1450, Month.APRIL, 16, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.APRIL, 19, 12, 0) )
                .build();
        Reservation reservation2 = sampleReservation2()
                .withFrom( LocalDateTime.of(1450, Month.APRIL, 20, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.APRIL, 30, 12, 0) )
                .build();
        Reservation reservation3 = sampleReservation3()
                .withFrom( LocalDateTime.of(1450, Month.JUNE, 16, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.JULY, 6, 12, 0) )
                .build();
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
            rManager.createReservation(reservation3);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat( rManager.findReservation(
                new ReservationFilter().withFromIsAfter(LocalDateTime.of(1450, Month.APRIL, 19, 11, 0))
                .withToIsBefore(LocalDateTime.of(1450, Month.JUNE, 17, 12, 0))
                                            ) )
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation2);
        
    }
    
    @Test
    public void testFindReservationWithPricePerHourLargerThan() {
        Reservation reservation1 = sampleReservation1()
                    .withPricePerHour(new BigDecimal(20))
                    .build();
            Reservation reservation2 = sampleReservation2()
                    .withPricePerHour(new BigDecimal(30))
                    .build();
            Reservation reservation3 = sampleReservation3()
                    .withPricePerHour(new BigDecimal(40))
                    .build();
            
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
            rManager.createReservation(reservation3);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat( rManager.findReservation( new ReservationFilter().withPricePerHourIsMoreThan(new BigDecimal(25)) ) )
                    .usingFieldByFieldElementComparator()
                    .containsOnly(reservation2, reservation3);
    }
    
    /*
    The OnlyActive function from ReservationFilter works with current time, but
    I dunno how to override database's CURRENT_TIMESTAMP
    if reservation2 has timeTo, which is in future, test will pass
    
    @Test
    public void testFindActiveReservations() {
        Reservation reservation1 = sampleReservation1()
                .withFrom( LocalDateTime.of(1450, Month.APRIL, 16, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.APRIL, 19, 12, 0) )
                .build();
        Reservation reservation2 = sampleReservation2()
                .withFrom( LocalDateTime.of(1450, Month.APRIL, 20, 12, 0) )
                .withTo( LocalDateTime.of(1450, Month.APRIL, 30, 12, 0) )
                .build();
        Reservation reservation3 = sampleReservation3()
                .withFrom( LocalDateTime.of(1450, Month.APRIL, 18, 12, 0) )
                .withTo(null)
                .build();
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
            rManager.createReservation(reservation3);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        
        assertThat( rManager.findReservation(
                new ReservationFilter().withOnlyActive(true)
                                            ) )
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation2, reservation3);
    }
    */
    
    @Test
    public void testFindUnpaidReservations() {
        Reservation reservation1 = sampleReservation1()
                    .withFrom( LocalDateTime.of(1450, Month.APRIL, 16, 12, 0) )
                    .withTo( LocalDateTime.of(1450, Month.APRIL, 17, 12, 0) )
                    .withPricePerHour(new BigDecimal(10))
                    .withMoneyPaid(new BigDecimal(250))
                    .build();
            Reservation reservation2 = sampleReservation2()
                    .withFrom( LocalDateTime.of(1450, Month.APRIL, 18, 12, 0) )
                    .withTo( LocalDateTime.of(1450, Month.APRIL, 19, 12, 0) )
                    .withPricePerHour(new BigDecimal(10))
                    .withMoneyPaid(new BigDecimal(130))
                    .build();
            Reservation reservation3 = sampleReservation3()
                    .withFrom( LocalDateTime.of(1450, Month.APRIL, 13, 12, 01) )
                    .withTo( LocalDateTime.of(1450, Month.APRIL, 14, 12, 00) )
                    .withPricePerHour(new BigDecimal(10))
                    .withMoneyPaid(new BigDecimal(240))
                    .withPricePerHour(new BigDecimal(0))
                    .build();
            
        try {
            rManager.createReservation(reservation1);
            rManager.createReservation(reservation2);
            rManager.createReservation(reservation3);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        assertThat( rManager.findReservation( new ReservationFilter().withOnlyUnpaid(true) ) )
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation2);
    }
    
    //-------------------------------------------------------------------------
    
    @FunctionalInterface
    private static interface Operation<T> {
        void callOn(T subjectOfOperation) throws DragonInUseException;
    }
    
    private void testExpectedServiceFailureException(Operation<ReservationManager> operation) throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        rManager.setDataSource(failingDataSource);
        assertThatThrownBy(() -> operation.callOn(rManager))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }
    
    @Test
    public void testGetReservationWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException(manager -> {manager.getReservation(1234L);});
    }
    
    
    @Test
    public void testCreateReservationWithSqlExceptionThrown() throws SQLException {
        Reservation reservation = sampleReservation1().build();
        testExpectedServiceFailureException(manager -> {manager.createReservation(reservation);});
    }
    
    
    
    @Test
    public void testUpdateReservationWithSqlExceptionThrown() throws SQLException {
        Reservation reservation = sampleReservation1().build();
        try {
            rManager.createReservation(reservation);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        testExpectedServiceFailureException(manager -> {manager.updateReservation(reservation);});
    }
    
    
    @Test
    public void testRemoveReservationWithSqlExceptionThrown() throws SQLException {
        Reservation reservation = sampleReservation1().build();
        try {
            rManager.createReservation(reservation);
        } catch (DragonInUseException ex) {
            Logger.getLogger(ReservationManagerImplTest.class.getName()).log(Level.SEVERE, null, ex);
            Fail.fail(ex.getMessage());
        }
        testExpectedServiceFailureException(manager -> {manager.removeReservation(reservation);});
    }
    
    @Test
    public void testListAllReservationsWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException(manager -> {manager.listAllReservations();});
    }
    
    @Test
    public void testFindReservationWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException(manager -> {manager.findReservation(new ReservationFilter());});
    }
}