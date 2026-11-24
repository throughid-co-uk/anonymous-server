package com.makefire.anonymous.aifactory.probes;

import com.makefire.anonymous.rest.dto.response.board.BoardResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import java.time.LocalDateTime;

@JsonTest
@EnabledIfSystemProperty(named = "factory.behavior.out", matches = ".+")
class MapperBehaviorTest {
    @Autowired JacksonTester<BoardResponse> json;
    @Test void observe() throws Exception {
        BoardResponse value = new BoardResponse(7L, "title", "contents", null, LocalDateTime.of(2020, 2, 3, 4, 5, 6), null);
        String written = json.write(value).getJson();
        Observations.record("order", "bean-declaration-order", written);
        Observations.record("time", "java-time-value", written);
        Observations.record("duration", "duration-value", "absent");
        Observations.record("write", "injected-mapper-write", written);
        try {
            BoardResponse read = json.parseObject("{\"id\":null,\"title\":\"probe\"}");
            Observations.record("primitive", "null-for-primitive", "id=" + read.getId());
        } catch (Exception e) {
            Observations.record("primitive", "null-for-primitive", e.getClass().getSimpleName());
        }
    }
}
