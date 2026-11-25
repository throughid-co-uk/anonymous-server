package com.makefire.anonymous.config.web.advice;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GlobalExceptionHandlerTest {
    @Test
    void malformedJsonUsesCurrentParserExceptionName() {
        StreamReadException parse = assertThrows(StreamReadException.class,
                () -> JsonMapper.builder().build().readTree("{\"value\":"));
        var exception = new HttpMessageNotReadableException("Malformed JSON", parse,
                new MockHttpInputMessage(new byte[0]));

        var result = new GlobalExceptionHandler().handleInternalServerError(exception);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError().getStatus()).isEqualTo(500);
        assertThat(result.getError().getMessage())
                .contains(parse.getOriginalMessage(), parse.getClass().getName());
    }
}
