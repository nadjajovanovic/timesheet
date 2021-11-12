package projekat.util;

import lombok.experimental.UtilityClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class DateFormatter {
    final static String format = "yyyy-MM-dd";
    final static SimpleDateFormat formatter = new SimpleDateFormat(format);

    public String dateToString(Date date){
        final var formattedDate = formatter.format(date);
        return formattedDate;
    }

    public Date stringToDate(String dateAsString) throws ParseException {
        final var parsedDate = formatter.parse(dateAsString);
        return parsedDate;
    }
}
