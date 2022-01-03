package com.vijay.shb.model;

import java.math.BigDecimal;

public record Account( String personNumber, String personName,  long accountNumber, AccountType accountType, BigDecimal balance) {

}
