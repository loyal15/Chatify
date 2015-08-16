package photofoto.gpit.com.photofoto.common.date;

import android.util.Log;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for parsing and formatting dates and times.
 *
 * @author Tom Dignan
 */

public final class CIDateTimeUtils {
	
	private static final String TAG = "";
	private static final SimpleDateFormat DATE_FORMATTER =
			new SimpleDateFormat("yyyy-MM-dd");

	private static final SimpleDateFormat TIME_FORMATTER =
			new SimpleDateFormat("hh:mm a");
	private static final SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
	private static final SimpleDateFormat VERBOSE_LOCALE_DATE_FORMATTER =
			new SimpleDateFormat("MMM dd',' yyyy");
	private static final SimpleDateFormat VERBOSE_LOCALE_TIME_FORMATTER =
			new SimpleDateFormat("hh:mm a z");
	private static final SimpleDateFormat VERBOSE_LOCALE_DATE_TIME_FORMATTER =
			new SimpleDateFormat("MMM dd',' yyyy hh:mm a");
	private static final SimpleDateFormat ISO_8601_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
	private static final SimpleDateFormat ALMOST_ISO_8601_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
	public static final SimpleDateFormat CHILD_DATESTAMP_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
	public static final SimpleDateFormat VERBOSE_LOCALE_EVENT_DATE_FORMATTER = new SimpleDateFormat("'T' MMM dd, yyyy hh:mm a");
	public static final SimpleDateFormat VERBOSE_LOCALE_USER_BIRTH_FORMATTER = new SimpleDateFormat("MMM dd, yyyy");
	public static final SimpleDateFormat VERBOSE_LOCALE_EVENT_DATEVALUE_FORMATTER = new SimpleDateFormat("MMM dd, yyyy");
	
	@SuppressWarnings("deprecation")
	public static String formatToBirthDay(Date date) {
		
		DateFormatSymbols dateFormatSymbol = new DateFormatSymbols();
		String shortMonth = dateFormatSymbol.getShortMonths()[date.getMonth()];
		int year = date.getYear();
		int day = date.getDate();

		return String.format("%s %d, %d", shortMonth, day, year);
	}
	
	public static Date parseAlmostISO8601DateTimeWithTSeparator(String datetime) {
		Log.d(TAG, "datetime="+ datetime);
		try {
			return ALMOST_ISO_8601_FORMATTER.parse(datetime);
		} catch (ParseException e) {
			Log.e(TAG, "caught ParseException", e);
			return null;
		}
	}
	/**
	 * Parses an ISO8601 formatted datetime and returns a
	 * java.util.Date object for it, or NULL if parsing
	 * the date fails.
	 *
//	 * @param String datetime
	 * @return Date|null
	 */
	
	public static String formatToChildDateStamp(Date date) {
		return CHILD_DATESTAMP_FORMATTER.format(date);
	}
	
	public static Date parseFromChildDateStamp(String datetime) {
		Log.d(TAG, "datetime="+ datetime);
		try {
			return CHILD_DATESTAMP_FORMATTER.parse(datetime);
		} catch (ParseException e) {
			Log.e(TAG, "caught ParseException", e);
			return null;
		}
	}
	
	public static Date parseFromISO8601(String datetime) {
		Log.d(TAG, "datetime="+ datetime);
		try {
			return ISO_8601_FORMATTER.parse(datetime);
		} catch (ParseException e) {
			Log.e(TAG, "caught ParseException", e);
			return null;
		}
	}

	public static String toLocaleEventStartDate(Date date) {
		if ( date == null )
			return null;
		return VERBOSE_LOCALE_DATE_TIME_FORMATTER.format(date);
	}

	public static String toLocaleUserBirth(Date date) {
		if (date == null)
			return null;

		return VERBOSE_LOCALE_USER_BIRTH_FORMATTER.format(date);
	}

	
	public static String formatToIS08601(Date date) {
		return ISO_8601_FORMATTER.format(date);
	}

	public static Date parseOnlyDate(String date) {
		Log.d(TAG, "date="+ date);
		try {
			return DATE_FORMATTER.parse(date);
		} catch (ParseException e) {
			Log.e(TAG, "caught ParseException", e);
			return null;
		}
	}
	/**
	 * Tries to parse most date times that it is passed, using
	 * some heuristics.
	 *
//	 * @param string
	 * @return
	 */
	public static Date parseDateTime(String datetime) {
		if (datetime.contains("T")) {
			Log.d(TAG, "parseDateTime(): Trying ISO8601 with a T separator");
			return parseAlmostISO8601DateTimeWithTSeparator(datetime);
		} else if (datetime.length() == 10 && datetime.contains("-")) {
			Log.d(TAG, "parseDateTime(): Trying just yyyy-MM-dd date");
			return parseOnlyDate(datetime);
		} else {
			Log.d(TAG, "parseDateTime(): Trying regular ISO8601");
			return parseFromISO8601(datetime);
		}
	}
	public static Date parseTime(String time) {
		try {
			return TIME_FORMATTER.parse(time);
		} catch (ParseException e) {
			Log.d(TAG, "parseTime() caught ParseException", e);
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getShortTime(Date date) {
		
		if ( date == null )
			return "";
		
		return TIME_FORMATTER.format(date);
	}

    public static String getShortTime(int hourOfDay, int minuteOfHour) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minuteOfHour);

        Date date = cal.getTime();
        return getShortTime(date);
    }

	public static Date parseDate(String date) {
		try {
			return DATE_FORMATTER.parse(date);
		} catch (ParseException e) {
			Log.d(TAG, "parseDate() caught ParseException", e);
			e.printStackTrace();
			return null;
		}
	}
	public static Date parseDateAndTime(String strDate, String strTime) {
		Date date = parseDate(strDate);
		Date time = parseTime(strTime);
		long dateMillis = date.getTime();
		long timeMillis = time.getTime();
		return new Date(dateMillis + timeMillis);
	}

	public static Date parseEventStartDateAndTime(String strDate, String strTime) {
		Date date = parseEventStartDate(strDate);
		Date time = parseEventStartTime(strTime);
		long dateMillis = date.getTime();
		long timeMillis = time.getTime();
		return new Date(dateMillis + timeMillis);
	}

	public static Date parseEventStartTime(String time) {
		try {
			return TIME_FORMATTER.parse(time);
		} catch (ParseException e) {
			Log.d(TAG, "parseTime() caught ParseException", e);
			e.printStackTrace();
			return null;
		}
	}

	public static Date parseEventStartDate(String date) {
		try {
			return VERBOSE_LOCALE_EVENT_DATEVALUE_FORMATTER.parse(date);
		} catch (ParseException e) {
			Log.d(TAG, "parseTime() caught ParseException", e);
			e.printStackTrace();
			return null;
		}
	}

	public static String pad(int value) {
		if (value < 10) {
			return "0" + value;
		} else {
			return "" + value;
		}
	}
	
	public static String toDeviceDateTime(Date date) {
		return DATETIME_FORMATTER.format(date);
	}

	public static String toLocaleDateTime(Date date) {
		return VERBOSE_LOCALE_DATE_TIME_FORMATTER.format(date);
	}

	public static String toLocaleDate(Date date) {
		return VERBOSE_LOCALE_DATE_FORMATTER.format(date);
	}

    public static String toLocalDate(int year, int month, int day) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        Date date = cal.getTime();
        return toLocaleDate(date);
    }

	public static Date toLocaleDate(int year, int month, int day, int hour, int min) {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);

		Date date = cal.getTime();
		return date;
	}

	public static String toLocaleTime(Date date) {
		return VERBOSE_LOCALE_TIME_FORMATTER.format(date);
	}

    public static String toLocaleTime(int hourOfDay, int minuteOfHour) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minuteOfHour);

        Date date = cal.getTime();
        return toLocaleTime(date);
    }

	public static Date toLocalTime(Date date) {
		long millisUTC = date.getTime();
		TimeZone tz = TimeZone.getDefault();
		int tzOffset = tz.getOffset(millisUTC);
		if (tz.inDaylightTime(new Date(millisUTC))) {
			millisUTC -= tz.getDSTSavings();
		}
		return new Date(millisUTC + tzOffset);
	}
	public static long toLocalTime(long millisUTC) {
		TimeZone tz = TimeZone.getDefault();
		int tzOffset = tz.getOffset(millisUTC);
		if (tz.inDaylightTime(new Date(millisUTC))) {
			millisUTC -= tz.getDSTSavings();
		}
		return millisUTC + tzOffset;
	}
	
	public static String timerReading(int secInterval) {
		
		int hours = (int) Math.floor(secInterval/3600);
		
		String hourString = "";
		
		if ( hours < 10 )
			hourString = String.format("0%d", hours);
		else
			hourString = String.format("%d", hours);
		
		int minutes = (int) Math.floor((secInterval - hours * 3600) / 60);
		
		String minString = "";
		
		if ( minutes < 10 )
			minString = String.format("0%d", minutes);
		else
			minString = String.format("%d", minutes);
		
		int seconds = Math.round(secInterval - (hours * 3600) - (minutes * 60));
		
		String secString = "";
		
		if ( seconds < 10 )
			secString = String.format("0%d", seconds);
		else
			secString = String.format("%d", seconds);
		
		String timeString = String.format("%s:%s:%s", hourString, minString, secString);
		
		return timeString;
	}
	
	public static String getMonth(int month) {
	    return new DateFormatSymbols().getMonths()[month];
	}
	
	public static String getTimeInterval(Date date) {
		
		if ( date == null )
			return "";
			
		String dateStr = String.valueOf(date.getTime());
		
		return dateStr;
	}
	
	public static Date getDateFromInterval(String interval) {
		
		if ( interval == null || interval.isEmpty() )
			return null;
		
		Date date = new Date(Long.parseLong(interval));
		
		return date;
	}
	
	public static Date getDateFromInterval(long interval) {
		
		Date date = new Date(interval);
		
		return date;
	}
	
	public static Date getDateFromInterval(int interval) {
		
		Date date = new Date(interval);
		
		return date;
	}
	
	public static long getSecondsBetweenDates(Date firstDate, Date secondDate) {

		long diffInMs = firstDate.getTime() - secondDate.getTime();

		long timeInterval = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
		
		return timeInterval;
	}

	public static String getTimeDifferenceStr(Date date) {

		Date now = new Date();
		int secDiff = (int)getSecondsBetweenDates(now, date);

		if ( secDiff >= 3600 * 24 * 30 ) {

			int months = secDiff / 3600 / 24 / 30;
			String convert = String.format("%dmonths ago");
			return convert;

		} else if ( secDiff >= 3600 * 24 ) {

			int days = secDiff / 3600 / 24;
			String convert = String.format("%ddays ago", days);
			return convert;

		} else if ( secDiff >= 3600 ) {

			int hrs = secDiff / 3600;
			int min = (secDiff - hrs * 3600) / 60;
			String convert = String.format("%dhrs ago", hrs);
			return convert;

		} else if ( secDiff > 60 ) {

			int min = secDiff / 60;
			String convert = String.format("%dmins ago", min);
			return convert;

		} else {

			String convert = "1 mins ago";
			return convert;
		}
	}
	
	public static String convertSecondsToString(String prefix, int seconds) {
		
		if ( seconds >= 3600 ) {
			
			int hrs = seconds / 3600;
			int min = (seconds - hrs * 3600) / 60;
			String convert = String.format("%s %d h %d m", prefix, hrs, min);
			return convert;
			
		} else if ( seconds > 60 ) {
			
			int min = seconds / 60;
			String convert = String.format("%s %d m", prefix, min);
			return convert;
			
		} else if ( seconds <= 0 ) {
			
			String convert = String.format("%s %d s", prefix, seconds);
			
			return convert;
			
		} else {
			
			String convert = String.format("%s < 1 m", prefix);
			return convert;
		}
	}
}