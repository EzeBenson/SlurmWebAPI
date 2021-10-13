package com.cvl.api.Utils;

import org.apache.commons.lang.RandomStringUtils;

public class FileNameUtils {
    public static String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }
    public static String generateRandomFileName(String ext) {
        String filename = "";
        long millis = System.currentTimeMillis();
        String rndchars = RandomStringUtils.randomAlphanumeric(16);
        return String.format("%s_%d.%s", rndchars, millis, ext);
    }
}

