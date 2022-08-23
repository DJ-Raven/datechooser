package com.raven.datechooser.render;

import com.raven.datechooser.DateBetween;
import com.raven.datechooser.DateChooser;

import java.util.Date;

public class DefaultDateChooserRender implements DateChooserRender {

    @Override
    public String renderLabelCurrentDate(DateChooser dateChooser, Date date) {
        return "Today : " + dateChooser.getDateFormat().format(date);
    }

    @Override
    public String renderTextFieldDate(DateChooser dateChooser, Date date) {
        return dateChooser.getDateFormat().format(date);
    }

    @Override
    public String renderTextFieldDateBetween(DateChooser dateChooser, DateBetween dateBetween) {
        if (dateBetween.getToDate() != null) {
            if (dateBetween.getFromDate().compareTo(dateBetween.getToDate()) == 0) {
                return dateChooser.getDateFormat().format(dateBetween.getFromDate());
            } else {
                return dateChooser.getDateFormat().format(dateBetween.getFromDate()) + dateChooser.getBetweenCharacter() + dateChooser.getDateFormat().format(dateBetween.getToDate());
            }
        } else {
            return dateChooser.getDateFormat().format(dateBetween.getFromDate());
        }
    }
}
