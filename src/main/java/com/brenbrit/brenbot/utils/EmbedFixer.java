package com.brenbrit.brenbot.utils;

import com.brenbrit.brenbot.utils.Downloader;

import discord4j.core.object.entity.Attachment;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegStream;

public class EmbedFixer {

    String[] FIXABLE_EXTENSIONS = {
        "mp4",
        "mkv",
        "mov"
    };

    String DOWNLOAD_LOC = "/tmp/brenbot/";
//    String FFMPEG_LOC;

    FFmpeg ffmpeg;
    FFprobe ffprobe;

    public EmbedFixer() {
        System.out.println("Init EmbedFixer");
        if (!Files.isDirectory(Paths.get(DOWNLOAD_LOC))){
            new File(DOWNLOAD_LOC).mkdirs();
        }
        try {
            ffmpeg = new FFmpeg();
            ffprobe = new FFprobe();
        } catch (IOException e) {
            System.out.println("Failed to initialize FFmpeg.");
            e.printStackTrace();
        }
    }

    public String checkAndFixEmbed(Attachment attachment) {
        if (checkEmbed(attachment) == null)
            return null;

        return "";
    }

    private String checkEmbed(Attachment attachment) {
        String url = attachment.getUrl();
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

        System.out.printf("Downloading %s -> %s%n", url, fileLoc);

        // We have a good extension on our hands!
        if (!Downloader.downloadFromURL(url, fileLoc)) {
            System.out.println("Failed to download " + url);
            return null;
        }

        try {
            FFmpegStream stream = ffprobe.probe(fileLoc).getStreams().get(0);
            System.out.printf("%nCodec: '%s' ; Width: %dpx ; Height: %dpx",
                stream.codec_long_name, stream.width, stream.height);
        } catch (IOException e) {
            System.out.println("Failed to probe " + fileLoc);
            e.printStackTrace();
        }


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
