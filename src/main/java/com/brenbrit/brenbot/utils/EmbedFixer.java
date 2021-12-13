package com.brenbrit.brenbot.utils;

import com.brenbrit.brenbot.utils.Downloader;

import discord4j.core.object.entity.Attachment;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EmbedFixer {

    String[] FIXABLE_EXTENSIONS = {
        "mp4",
        "mkv",
        "mov"
    };

    String DOWNLOAD_LOC = "/tmp/brenbot/";

    public EmbedFixer() {
        System.out.println("Init EmbedFixer");
        if (!Files.isDirectory(Paths.get(DOWNLOAD_LOC)))
            new File(DOWNLOAD_LOC).mkdirs();
    }

    public String checkAndFixEmbed(Attachment attachment) {
        if (checkEmbed(attachment) == null)
            return null;

        return "";
    }

    private String checkEmbed(Attachment attachment) {
        String url = attachment.getUrl();
        System.out.println(url);
        String[] split = url.split("\\.", 0);

        String fileExtension = split[split.length - 1];

        boolean goodExtension = false;
        for (String extension : FIXABLE_EXTENSIONS) {
            if (extension.equalsIgnoreCase(fileExtension)) {
                goodExtension = true;
                break;
            }
        }
        if (!goodExtension)
            return null;

        String fileLoc = DOWNLOAD_LOC + System.currentTimeMillis();

        // We have a good extension on our hands!
        if (!Downloader.downloadFromURL(url, fileLoc)) {
            System.out.println("Failed to download " + url);
            return null;
        }

        String[] ffmpegArgs = {"ffmpeg", fileLoc};
        String ffmpegOutput = getCommandOutput(ffmpegArgs);
        System.out.println(ffmpegOutput);

        return fileLoc;
    }

    private static String getCommandOutput(String[] args) {
        String output = "";
        String line;
        try {
            Process proc = new ProcessBuilder(args).start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader
                    (proc.getInputStream()));
            while ((line = stdInput.readLine()) != null) {
                output += line;
            }
            return output;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
