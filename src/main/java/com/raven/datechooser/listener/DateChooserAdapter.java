package com.raven.datechooser.listener;

import com.raven.datechooser.DateBetween;

import java.util.Date;

public abstract class DateChooserAdapter implements DateChooserListener {

    @Override
    public void dateChanged(Date date, DateChooserAction action) {
    }

    @Override
    public void dateBetweenChanged(DateBetween date, DateChooserAction action) {
    }
}
