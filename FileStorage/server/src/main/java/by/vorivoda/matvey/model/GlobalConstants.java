package by.vorivoda.matvey.model;

import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class GlobalConstants {
    // Security
    public static final String USERNAME_SYMBOLS_REGEX = "[A-Za-z0-9_]*";
    public static final Integer USERNAME_MIN_LENGTH = 4;
    public static final Integer USERNAME_MAX_LENGTH = 32;
    public static final Integer PASSWORD_MIN_LENGTH = 8;
    public static final Integer PASSWORD_MAX_LENGTH = 64;
    public static final Integer TOKEN_LIFETIME_IN_MINUTES = 60;

    // Mapping
    public static final String LOGIN_PATH = "/login";
    public static final String REGISTRATION_PATH = "/registration";
    public static final String STORAGE_PATH = "/storage";

    // STORAGE
    public static final String STORAGES_LOCATION = "/storages";

    // WEB
    public static final Map<String, String> CONTENT_TYPES = new HashMap<>(){{
        put("txt", MediaType.TEXT_PLAIN_VALUE);
        put("css", "text/css");
        put("html", MediaType.TEXT_HTML_VALUE);
        put("js", "text/javascript");
        put("ts", "text/javascript");
        put("php", "text/php");
        put("xml", MediaType.TEXT_XML_VALUE);

        put("ogg", "video/ogg");
        put("mp4", "video/mp4");

        put("png", MediaType.IMAGE_PNG_VALUE);
        put("jpeg", MediaType.IMAGE_JPEG_VALUE);
        put("gif", MediaType.IMAGE_GIF_VALUE);

        put("mp3", "audio/mpeg");

        put("zip", "application/zip");
        put("pdf", MediaType.APPLICATION_PDF_VALUE);
    }};

    public static final String DEFAULT_CONTENT_TYPE = MediaType.TEXT_PLAIN_VALUE;
}
