package com.makefire.anonymous.config.swagger;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Compatibility discovery endpoints for clients of the former Springfox UI. */
@RestController
public class SwaggerResourcesController {
    @GetMapping("/swagger-resources")
    public List<Map<String, String>> resources() {
        return List.of(Map.of("name", "default", "url", "/v3/api-docs",
                "swaggerVersion", "3.0.3", "location", "/v3/api-docs"));
    }

    @GetMapping({"/swagger-resources/configuration/security", "/swagger-resources/configuration/ui"})
    public Map<String, Object> configuration() {
        return Map.of();
    }

    @GetMapping("/v2/api-docs")
    public org.springframework.web.servlet.view.RedirectView legacyDocs() {
        return new org.springframework.web.servlet.view.RedirectView("/v3/api-docs");
    }
}
