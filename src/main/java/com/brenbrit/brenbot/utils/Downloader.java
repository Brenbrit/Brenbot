package com.brenbrit.brenbot.utils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

class Downloader {

    private static final int DOWNLOAD_CHUNK_SIZE = 2048;

    public static boolean downloadFromURL(String url, String filename) {
        try (BufferedInputStream inputStream =
                new BufferedInputStream(new URL(url).openStream());
                FileOutputStream fileOS = new FileOutputStream(filename)) {
            byte data[] = new byte[DOWNLOAD_CHUNK_SIZE];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, DOWNLOAD_CHUNK_SIZE)) != -1) {
                fileOS.write(data, 0, byteContent);
                }
            return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
    }
}
