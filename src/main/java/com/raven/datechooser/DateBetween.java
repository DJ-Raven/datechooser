package com.raven.datechooser;

import java.util.Date;

public class DateBetween {
    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public DateBetween(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public DateBetween() {
    }

    private Date fromDate;
    private Date toDate;

    public void fixDate() {
        if (fromDate.compareTo(toDate) == 1) {
            Date tempDate = fromDate;
            this.fromDate = toDate;
            this.toDate = tempDate;
        }
    }
}
