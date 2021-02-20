package ykim164cs242.tournamentor.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * DateHandler class contains functions that checks conflicting times
 * and calculates time differences.
 */

public class DateHandler {

    /**
     * This function converts a string date into a Date object
     * and see if the starting date comes before ending date.
     */
    public static boolean isValidTerm(String startDate, String endDate) {

        try {
            Date start = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH)
                    .parse(startDate);
            Date end = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH)
                    .parse(endDate);

            System.out.println(start);
            System.out.println(end);

            if (start.compareTo(end) > 0) {
                // Start comes after end -> invalid
                return false;

            } else if (start.compareTo(end) < 0) {
                // Start comes before end -> valid
                return true;
            } else if (start.compareTo(end) == 0) {
                // same date -> one day tournament -> valid
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Checks if the given date has passed today or not
    public static boolean isNotOver(String date) {

        String today = new SimpleDateFormat("MM-dd-yyyy").format(new Date());

        return isValidTerm(today, date);

    }

    // Given two HH:mm format, get the time difference.
    public static long minuteDifference(String time1, String time2) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("HH : mm");

        Date date1 = format.parse(time1);
        Date date2 = format.parse(time2);
        long difference = date2.getTime() - date1.getTime();

        return TimeUnit.MILLISECONDS.toMinutes(difference);
    }

}
