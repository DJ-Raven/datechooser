package com.raven.datechooser;

class ErrorCheck {
    public static boolean checkDate(RDate date) throws DateChooserException {
        if (date.getMonth() > 12) {
            throw new DateChooserException("Invalid month " + date.getMonth() + ">12");
        } else if (date.getMonth() < 1) {
            throw new DateChooserException("Invalid month " + date.getMonth() + "<1");
        } else if (date.getYear() < 1) {
            throw new DateChooserException("Invalid year " + date.getYear() + "<1");
        }
        return true;
    }
}
