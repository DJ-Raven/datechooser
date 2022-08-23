package com.raven.datechooser.listener;

import com.raven.datechooser.DateBetween;

import java.util.Date;

public interface DateChooserListener {
    public void dateChanged(Date date, DateChooserAction action);

    public void dateBetweenChanged(DateBetween date, DateChooserAction action);
}
