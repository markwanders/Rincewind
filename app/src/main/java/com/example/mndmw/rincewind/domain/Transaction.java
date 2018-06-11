package com.example.mndmw.rincewind.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by mndmw on 10-2-2018.
 */

public class Transaction {
    private String counterParty;

    private BigDecimal amount;

    private String currency;

    private String description;

    public String getCounterParty() {
        return counterParty;
    }

    public void setCounterParty(String counterParty) {
        this.counterParty = counterParty;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
