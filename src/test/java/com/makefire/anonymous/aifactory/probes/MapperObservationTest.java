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
class MapperObservationTest {
    @Autowired JacksonTester<BoardResponse> json;
    @Test void observe() throws Exception {
        String written = json.write(BoardResponse.builder().id(7L).title("title").contents("contents").author("author").createDate(LocalDateTime.of(2020, 1, 2, 3, 4, 5)).build()).getJson();
        Observations.record("order", "bean-declaration-order", written);
        Observations.record("time", "java-time-value", written);
        Observations.record("write", "injected-mapper-write", written);
        Observations.record("duration", "duration-value", "absent");
        try {
            BoardResponse value = json.parseObject("{\"id\":null,\"title\":\"title\"}");
            Observations.record("primitive", "null-for-primitive", "id=" + value.getId());
        } catch (Exception e) {
            Observations.record("primitive", "null-for-primitive", e.getClass().getSimpleName());
        }
    }
}
