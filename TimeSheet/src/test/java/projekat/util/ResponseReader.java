package projekat.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.test.web.servlet.MvcResult;

@UtilityClass
public class ResponseReader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public <T> T readResponse(MvcResult result, Class<T> clazz){
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final String json = result.getResponse().getContentAsString();

        return objectMapper.readValue(json, clazz);
    }
}
