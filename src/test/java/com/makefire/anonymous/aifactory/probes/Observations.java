package com.makefire.anonymous.aifactory.probes;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

@EnabledIfSystemProperty(named = "factory.behavior.out", matches = ".+")
final class Observations {
    @org.junit.jupiter.api.Test void durationAbsent() throws Exception {
        record("duration-presence", "duration-value", "absent");
    }

    static void record(String id, String obligation, String value) throws Exception {
        Path out = Paths.get(System.getProperty("factory.behavior.out"));
        Files.createDirectories(out);
        Files.write(out.resolve(id + ".json"), ("{\"case\":" + quote(id) + ",\"obligation\":" + quote(obligation) + ",\"observed\":" + quote(value) + "}").getBytes(StandardCharsets.UTF_8));
    }
    private static String quote(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + "\"";
    }
}
