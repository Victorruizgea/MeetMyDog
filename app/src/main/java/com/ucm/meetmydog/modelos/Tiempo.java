package com.ucm.meetmydog.modelos;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Tiempo {
    public String getTiempo(){
        String tiempo, formatoHora;
        formatoHora = "HH:mm";
        String ZonaHoraria = "GMT+1";
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat(formatoHora);
        sdf.setTimeZone(TimeZone.getTimeZone(ZonaHoraria));
        return sdf.format(date);
    }
}
