package pl.mbak.traffic.engine.io;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonMapper() {}

    public static ObjectMapper mapper() {
        return MAPPER;
    }
}
