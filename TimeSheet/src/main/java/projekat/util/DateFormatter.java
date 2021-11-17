package projekat.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import projekat.enums.ErrorCode;
import projekat.exception.ParseInputException;

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
