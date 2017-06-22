/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dragonrental.backend;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 *
 * @author zuz
 */
public class Reservation {
    
    private Long id;
    private LocalDateTime from;
    private LocalDateTime to;
    private Person borrower;
    private Dragon dragon;
    private BigDecimal moneyPaid;
    private BigDecimal pricePerHour;

    public Reservation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public Person getBorrower() {
        return borrower;
    }

    public void setBorrower(Person borrower) {
        this.borrower = borrower;
    }

    public Dragon getDragon() {
        return dragon;
    }

    public void setDragon(Dragon dragon) {
        this.dragon = dragon;
    }

    public BigDecimal getMoneyPaid() {
        return moneyPaid;
    }

    public void setMoneyPaid(BigDecimal moneyPaid) {
        this.moneyPaid = moneyPaid;
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(BigDecimal pricePerHour) {
        this.pricePerHour = pricePerHour;
    }
    
    private static final double BASE_COST = 10;
    private static final double OVERALL_MULT = 1.0;
    private static final double PRICE_PER_SPEED = 0.01;
    
    public BigDecimal getPricePerHourForDragon() {
        double amount;
        amount = (BASE_COST + getDragon().getMaximumSpeed() * PRICE_PER_SPEED) * getDragon().getElement().getElementCostMult() * OVERALL_MULT;
        return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reservation that = (Reservation) o;

        return id != null ? id.equals(that.id) : false;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", borrower=" + borrower +
                ", dragon=" + dragon +
                '}';
    }
}
