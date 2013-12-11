package net.sf.nachocalendar.components;

import java.text.DateFormatSymbols;
import java.util.Locale;

import javax.swing.text.DateFormatter;

//this is just a demo routine.
// in reality, doing it this way wuuld prevent , you from starting
// "nachocalendars"
//that had different locales, under the same instance.
//this gould be gotten round by generating a hashmap eachtime a different
// locale
//was called.
// but also there in no need to keep creating instances of "dateformatsymbols"
//one would do, for each used locale.
//originally I was passing in a simple date format, which was a lot cleaner.
//but unfortunatly there was no way to recover the locale from it.
//this was needed to get the correct "dateformatsymbols"
//it was either a re-write of most of the classes, or this minor botch.

public class FormatSymbols extends DateFormatSymbols {
    private static DateFormatSymbols ref;

    //private DateFormatSymbols symbols;

    //private static DateFormatter requiredFormatter = new DateFormatter(); // setup
                                                                          // a
                                                                          // default
                                                                          // formatter

    private static Locale requiredLocale = Locale.getDefault(); // setup a
                                                                // default
                                                                // locale

    public FormatSymbols(DateFormatter passedrequiredFormatter,
            Locale passedrequiredLocale) {
        //requiredFormatter = passedrequiredFormatter;
        requiredLocale = passedrequiredLocale;
        ref = null;
        getSingletonObject();
    }

    public static DateFormatSymbols getSingletonObject() {
        if (ref == null) {
            //make it thread safe
            synchronized (FormatSymbols.class) {
                if (ref == null) {
                    // it's ok, we can call this constructor
                    ref = new DateFormatSymbols(requiredLocale);
                }
            }
        }

        return ref;
    }

}
