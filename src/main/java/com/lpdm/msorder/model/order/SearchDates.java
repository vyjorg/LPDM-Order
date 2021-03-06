package com.lpdm.msorder.model.order;

import java.time.LocalDate;

/**
 * @author Kybox
 * @version 1.0
 * @since 01/12/2018
 */

public class SearchDates {

    private LocalDate date1;
    private LocalDate date2;

    public SearchDates() {
    }

    public LocalDate getDate1() {
        return date1;
    }

    public void setDate1(LocalDate date1) {
        this.date1 = date1;
    }

    public LocalDate getDate2() {
        return date2;
    }

    public void setDate2(LocalDate date2) {
        this.date2 = date2;
    }

    @Override
    public String toString() {
        return "SearchDates{" +
                "date1=" + date1 +
                ", date2=" + date2 +
                '}';
    }
}
