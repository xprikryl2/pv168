package dragonrental.gui;

import dragonrental.backend.Reservation;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.FormatStyle;
import static java.time.temporal.ChronoUnit.HOURS;
import java.util.AbstractSet;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.JXDatePicker;

/**
 *
 * @author Petr Soukop
 */
public class GUIUtils {
    
    private static Properties properties = new Properties();
    private static FormatStyle dateTimeFormat = fetchDateTimeFormat();
    private static ResourceBundle bundle = ResourceBundle.getBundle("dragonrental/gui/language");
    

    static FormatStyle getDateTimeFormat() {
        return dateTimeFormat;
    }
    
    /**
     * looks for dateTimeFormat in the properties file
     * @return FormatStyle
     */
    private static FormatStyle fetchDateTimeFormat() {
        try {
            properties.load(GUIUtils.class.getResourceAsStream("/Properties.properties"));
        } catch (IOException ex) {
            Logger.getLogger(GUIUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return FormatStyle.valueOf(properties.getProperty("dateTimeFormat"));
    }
    
    /**
     * blatantly stolen from StackOverflow, posted by user 'camickr'
     * 
     * will make the text field mark all of its text when focused (clicked on, tabbed into)
     * @param textField to give this feature to
     */
    public static void setMarkTextOnFocus(JTextField textField) {
        textField.addFocusListener(
                new FocusAdapter() {
                    public void focusGained(final FocusEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                JTextField tf = (JTextField)e.getSource();
                                tf.selectAll();
                            }
                        });
                    }
                });
    }
    
    /**
     * this one works for a Spinner, see documentation above
     * @param spinner
     */
    public static void setMarkSpinnerOnFocus(JSpinner spinner) {
        setMarkTextOnFocus(((JSpinner.DefaultEditor) spinner.getEditor()).getTextField());
    }
    
    /**
     * makes a LocalDateTime out of JXDatePicker and 2 JSpinner components
     * @param datePicker
     * @param hourSpinner
     * @param minuteSpinner
     * @return LocalDateTime represented by components, will return null if no date is selected
     */
    public static LocalDateTime getLocalDateTime(JXDatePicker datePicker, JSpinner hourSpinner, JSpinner minuteSpinner) {
        LocalDateTime dateTime = null;
        if(datePicker.getDate() != null) {
            dateTime = LocalDateTime.of(
                datePicker.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), 
                LocalTime.of(
                        ((SpinnerNumberModel) hourSpinner.getModel()).getNumber().intValue(), 
                        ((SpinnerNumberModel) minuteSpinner.getModel()).getNumber().intValue()
                            )
                        );
        }
        return dateTime;
    }
    
    public static BigDecimal getDuePayment(Reservation reservation) {
        if (reservation.getTo() == null) {
            return new BigDecimal(0).max(reservation.getPricePerHour().multiply(new BigDecimal(HOURS.between(reservation.getFrom(), LocalDateTime.now()))));
        }
        return reservation.getPricePerHour().multiply(new BigDecimal(HOURS.between(reservation.getFrom(), reservation.getTo())));
    }
    
    public static Set<ReservationStatus> getReservationStatus(Reservation reservation) {
        Set<ReservationStatus> statusSet = new HashSet<>();
        if (reservation.getFrom().isBefore(LocalDateTime.now()) && (reservation.getTo() == null || reservation.getTo().isAfter(LocalDateTime.now()))) {
            statusSet.add(ReservationStatus.ACTIVE);
        }
        if(reservation.getTo() != null) {
            if (getDuePayment(reservation).compareTo(reservation.getMoneyPaid()) <= 0) {
                statusSet.add(ReservationStatus.PAID);
            }
        }
        return statusSet;
    }
    
    public static String getReservationStatusString(Reservation reservation) {
        StringBuilder string = new StringBuilder();
        for (ReservationStatus status : getReservationStatus(reservation)) {
            string.append(bundle.getString(status.name()));
            string.append(" ");
        }
        return string.toString();
    }
}

