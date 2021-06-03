package by.vorivoda.matvey.model.util;

public class FilesMethods {

    public static String getFileExtension(String path) {
        int slashIndex = path.lastIndexOf("/");
        int dotIndex = path.lastIndexOf(".");

        if (slashIndex == path.length()) return "/";
        return dotIndex > -1 && dotIndex > slashIndex ? path.substring(dotIndex) : "";
    }

    public static String getFileName(String path) {
        int slashIndex = path.lastIndexOf("/");
        slashIndex = slashIndex == path.length() ? path.lastIndexOf("/", slashIndex - 1) : slashIndex;

        int dotIndex = path.lastIndexOf(".");
        dotIndex = dotIndex == -1 ? path.length() : dotIndex;

        return path.substring(slashIndex + 1, dotIndex);
    }

    public static String getFullName(String path) {
        int slashIndex = path.lastIndexOf("/");
        slashIndex = slashIndex == path.length() ? path.lastIndexOf("/", slashIndex - 1) : slashIndex;

        return path.substring(slashIndex + 1);
    }
}
