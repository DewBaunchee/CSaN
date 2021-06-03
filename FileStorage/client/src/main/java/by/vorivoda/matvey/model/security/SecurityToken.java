package by.vorivoda.matvey.model.security;

import java.util.Date;

public class SecurityToken {

    private final String value;
    private final Date expiredDate;

    public SecurityToken(String value, Date expiredDate) {
        this.value = value;
        this.expiredDate = expiredDate;
    }

    public String getValue() {
        return value;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }
}
