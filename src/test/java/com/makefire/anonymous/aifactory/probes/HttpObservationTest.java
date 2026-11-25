package com.makefire.anonymous.aifactory.probes;

import com.makefire.anonymous.aifactory.probes.factoryharness.FactoryProbeHarness;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@EnabledIfSystemProperty(named = "factory.behavior.out", matches = ".+")
class HttpObservationTest extends FactoryProbeHarness {
    private String stable(String body) {
        return body.replaceAll("\\\"(timestamp|createDate|modifiedDate)\\\":\\\"[^\\\"]*\\\"", "\"$1\":\"TIME\"");
    }
    @Test void observe() throws Exception {
        String accepted = perform(get(PREPARED_HTTP_JSON_MEDIA_TYPE_2).accept(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentType();
        String plain = perform(get(PREPARED_HTTP_JSON_MEDIA_TYPE_2)).andReturn().getResponse().getContentType();
        Observations.record("media", "http-json-media-type", "accept-json=" + accepted + "; no-accept=" + plain);
        Observations.record("response", "http-json-config-response", stable(perform(get(PREPARED_HTTP_JSON_CONFIG_RESPONSE_2)).andReturn().getResponse().getContentAsString()));
        String unknown = PREPARED_HTTP_JSON_CONFIG_REQUEST_1_BODY.replace("}", ",\"unknownProbe\":true}");
        org.springframework.mock.web.MockHttpServletResponse response = perform(post(PREPARED_HTTP_JSON_CONFIG_REQUEST_1).contentType(MediaType.APPLICATION_JSON).content(unknown)).andReturn().getResponse();
        String result = response.getStatus() + ":" + stable(response.getContentAsString());
        Observations.record("unknown", "unknown-property-request", result);
        Observations.record("request", "http-json-config-request", result);
        response = perform(post(PREPARED_HTTP_JSON_CONFIG_REQUEST_1).contentType(MediaType.APPLICATION_JSON).content(PREPARED_HTTP_JSON_CONFIG_REQUEST_1_BODY + " {} ")).andReturn().getResponse();
        Observations.record("trailing", "trailing-tokens", response.getStatus() + ":" + response.getContentAsString());
    }
}
