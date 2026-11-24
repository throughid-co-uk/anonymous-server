package com.makefire.anonymous.aifactory.probes;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;

@org.junit.jupiter.api.condition.EnabledIfSystemProperty(named = "factory.behavior.out", matches = ".+")
final class Observations {
    @org.junit.jupiter.api.Test void helperAvailable() { org.junit.jupiter.api.Assertions.assertTrue(true); }

    static void record(String id, String obligation, String observed) throws Exception {
        Path out = Paths.get(System.getProperty("factory.behavior.out"));
        Files.createDirectories(out);
        Files.write(out.resolve(id + ".json"), ("{\"case\":" + quote(id) + ",\"obligation\":" + quote(obligation) + ",\"observed\":" + quote(observed) + "}").getBytes(StandardCharsets.UTF_8));
    }
    private static String quote(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + "\"";
    }
}
