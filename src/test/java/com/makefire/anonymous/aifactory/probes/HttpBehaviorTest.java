package com.makefire.anonymous.aifactory.probes;

import com.makefire.anonymous.aifactory.probes.factoryharness.FactoryProbeHarness;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@EnabledIfSystemProperty(named = "factory.behavior.out", matches = ".+")
class HttpBehaviorTest extends FactoryProbeHarness {
    private String stable(String body) {
        return body.replaceAll("(\\\"(?:timestamp|createDate|modifiedDate)\\\"\\s*:\\s*\\\")[^\\\"]*(\\\")", "$1<time>$2");
    }
    @Test void observe() throws Exception {
        String withUnknown = "{\"id\":1,\"title\":\"probe\",\"contents\":\"probe\",\"author\":\"probe\",\"unexpected\":true}";
        perform(post(PREPARED_HTTP_JSON_CONFIG_REQUEST_1).contentType(MediaType.APPLICATION_JSON).content(withUnknown)).andDo(r -> {
            String observed = r.getResponse().getStatus() + ":" + stable(r.getResponse().getContentAsString());
            Observations.record("unknown", "unknown-property-request", observed);
            Observations.record("request-config", "http-json-config-request", observed);
        });
        perform(post(PREPARED_HTTP_JSON_CONFIG_REQUEST_1).contentType(MediaType.APPLICATION_JSON).content(PREPARED_HTTP_JSON_CONFIG_REQUEST_1_BODY + " {}"))
            .andDo(r -> Observations.record("trailing", "trailing-tokens", r.getResponse().getStatus() + ":" + stable(r.getResponse().getContentAsString())));
        perform(get(PREPARED_HTTP_JSON_CONFIG_RESPONSE_2)).andDo(r -> Observations.record("response-config", "http-json-config-response", stable(r.getResponse().getContentAsString())));
        String accepted = perform(get(PREPARED_HTTP_JSON_MEDIA_TYPE_1).accept(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentType();
        String noAccept = perform(get(PREPARED_HTTP_JSON_MEDIA_TYPE_1)).andReturn().getResponse().getContentType();
        Observations.record("media", "http-json-media-type", "accept-json=" + accepted + "; no-accept=" + noAccept);
    }
}
