package projekat.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import projekat.exception.ParseInputException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class DateFormatter {
    private static final String format = "yyyy-MM-dd";
    private static final SimpleDateFormat formatter = new SimpleDateFormat(format);

    public String dateToString(Date date){
        final var formattedDate = formatter.format(date);
        return formattedDate;
    }

    public Date stringToDate(String dateAsString){
        Date parsedDate = null;
        try {
            parsedDate = formatter.parse(dateAsString);
        } catch (ParseException e) {
            throw new ParseInputException("Invalid date", HttpStatus.BAD_REQUEST);
        }
        return parsedDate;
    }
}
