package org.example.studentjournal;

import javax.swing.*;
import java.util.Calendar;
import java.util.Date;

class DaySpinnerDateModel extends SpinnerDateModel {
    public DaySpinnerDateModel() {
        super();
    }

    public Object getNextValue() {
        Calendar cal = Calendar.getInstance();
        cal.setTime((Date) getValue());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public Object getPreviousValue() {
        Calendar cal = Calendar.getInstance();
        cal.setTime((Date) getValue());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }
}