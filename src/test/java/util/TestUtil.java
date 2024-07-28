package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtil {
    private static final String JSON_BASE_PATH = "src/test/resources/json/";
    private static final String JSON_EXTENSION = ".json";

    public static String readJsonAsString(String filename) throws IOException {
        var path = Paths.get(JSON_BASE_PATH, filename + JSON_EXTENSION);
        return Files.readString(path);
    }
}
