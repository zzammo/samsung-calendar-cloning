package com.zzammo.calendar.holiday.lunar;

import com.ibm.icu.util.ChineseCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class LunarCalendar {

    public static String Lunar2Solar(String yyyymmdd) {
        ChineseCalendar cc = new ChineseCalendar();
        Calendar cal = Calendar.getInstance();

        if (yyyymmdd == null)
            return "";

        String date = yyyymmdd.trim();
        if (date.length() != 8) {
            if (date.length() == 4)
                date = date + "0101";
            else if (date.length() == 6)
                date = date + "01";
            else if (date.length() > 8)
                date = date.substring(0, 8);
            else
                return "";
        }

        cc.set(ChineseCalendar.EXTENDED_YEAR, Integer.parseInt(date.substring(0, 4)) + 2637);
        cc.set(ChineseCalendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        cc.set(ChineseCalendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

        cal.setTimeInMillis(cc.getTimeInMillis());

        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);

        StringBuffer ret = new StringBuffer();
        ret.append(String.format("%04d", y));
        ret.append(String.format("%02d", m));
        ret.append(String.format("%02d", d));

        return ret.toString();
    }
    public static String Solar2Lunar(String yyyymmdd) {
        ChineseCalendar cc = new ChineseCalendar();
        Calendar cal = Calendar.getInstance();

        if (yyyymmdd == null)
            return "";

        String date = yyyymmdd.trim() ;
        if( date.length() != 8 ) {
            if( date.length() == 4 )
                date = date + "0101" ;
            else if( date.length() == 6 )
                date = date + "01" ;
            else if( date.length() > 8 )
                date = date.substring(0,8) ;
            else
                return "" ;
        }

        cal.set( Calendar.YEAR, Integer.parseInt(date.substring(0,4)) ) ;
        cal.set( Calendar.MONTH, Integer.parseInt(date.substring(4,6))-1 ) ;
        cal.set( Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)) ) ;

        cc.setTimeInMillis( cal.getTimeInMillis() ) ;

        // ChinessCalendar.YEAR 는 1~60 까지의 값만 가지고 ,
        // ChinessCalendar.EXTENDED_YEAR 는 Calendar.YEAR 값과 2637 만큼의 차이를 가진다.
        int y = cc.get(ChineseCalendar.EXTENDED_YEAR)-2637 ;
        int m = cc.get(ChineseCalendar.MONTH)+1 ;
        int d = cc.get(ChineseCalendar.DAY_OF_MONTH) ;

        StringBuffer ret = new StringBuffer() ;
        if( y < 1000 )          ret.append( "0" ) ;
        else if( y < 100 )      ret.append( "00" ) ;
        else if( y < 10 )       ret.append( "000" ) ;
        ret.append( y ) ;

        if( m < 10 ) ret.append( "0" ) ;
        ret.append( m ) ;

        if( d < 10 ) ret.append( "0" ) ;
        ret.append( d ) ;

        return ret.toString() ;
    }
}