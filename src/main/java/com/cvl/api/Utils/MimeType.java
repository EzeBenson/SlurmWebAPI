package com.cvl.api.Utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MimeType {

    private static final Map<String,String> MIMETYPES_FILE_TYPE_MAP = createMap();
    private static final String MIMETYPE_DEFAULT = "application/octet-stream";

    private static Map<String,String> createMap() {
        Resource resource = new ClassPathResource("data/mimetypes.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            HashMap<String,String> map = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                int g = 0;
                String[] arr = line.split(" ");
                String content = arr[0];
                for (int i = 1; i < arr.length; i++) map.put(arr[i], content);
            }
            return map;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getContentType(String fileName) {
        return getContentType(fileName, null);
    }

    public static String getContentType(String fileName, String charset) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }

        if (extension == "" || !MIMETYPES_FILE_TYPE_MAP.containsKey(extension)) {
            return MIMETYPE_DEFAULT;
        }

        String mimeType = MIMETYPES_FILE_TYPE_MAP.get(extension);
        if (charset != null && (mimeType.startsWith("text/") || mimeType.contains("javascript"))) {
            mimeType += ";charset=" + charset.toLowerCase();
        }
        return mimeType;
    }
}