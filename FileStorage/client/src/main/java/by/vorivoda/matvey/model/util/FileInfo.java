package by.vorivoda.matvey.model.util;

import java.math.BigInteger;

public class FileInfo {

    private final String realPath;
    private final String resourcePath;
    private final BigInteger size;

    public FileInfo(String realPath, String resourcePath, BigInteger size) {
        this.realPath = realPath;
        this.resourcePath = resourcePath;
        this.size = size;
    }

    public String getRealPath() {
        return realPath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public BigInteger getSize() {
        return size;
    }
}
