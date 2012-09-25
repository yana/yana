package org.yana

import java.text.SimpleDateFormat

class DateFormatUtil {
    static RFC3339_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"

    public static String formatRfc3339(Date date) {
        def d = new SimpleDateFormat(RFC3339_DATE_FORMAT).format(date)
        return d.toString()
    }
}

