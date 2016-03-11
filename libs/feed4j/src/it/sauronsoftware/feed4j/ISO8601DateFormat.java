package it.sauronsoftware.feed4j;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * An ISO 8601 compliant DateFormat.
 * 
 * @author Carlo Pelliccia
 */
class ISO8601DateFormat extends DateFormat {

	private static final long serialVersionUID = 1L;

	private DateFormat[] formats = new DateFormat[] {
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US),
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
			new SimpleDateFormat("yyyy-MM-dd", Locale.US) };

	public Date parse(String source, ParsePosition pos) {
		Date date = formats[0].parse(source, pos);
		if (date != null) {
			return date;
		}
		date = formats[1].parse(source, pos);
		if (date != null) {
			return date;
		}
		date = formats[2].parse(source, pos);
		if (date != null) {
			return date;
		}
		date = formats[3].parse(source, pos);
		if (date != null) {
			return date;
		}
		return null;
	}

	public StringBuffer format(Date date, StringBuffer toAppendTo,
			FieldPosition fieldPosition) {
		return formats[0].format(date, toAppendTo, fieldPosition);
	}

}
