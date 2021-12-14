package com.brenbrit.brenbot.utils;

import com.brenbrit.brenbot.utils.Downloader;

import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

public class EmbedFixer {

    String[] FIXABLE_EXTENSIONS = {
        "mp4",
        "mkv",
        "mov"
    };

    String DOWNLOAD_LOC = "/tmp/brenbot/";

    FFmpeg ffmpeg;
    FFprobe ffprobe;
    FFmpegExecutor ffmpegExecutor;

    private class VideoFile {
        public String fileLoc;
        public FFmpegProbeResult probeResult;

        public VideoFile(String loc, FFmpegProbeResult result) {
            fileLoc = loc;
            probeResult = result;
        }
    }

    public EmbedFixer() {
        if (!Files.isDirectory(Paths.get(DOWNLOAD_LOC))){
            new File(DOWNLOAD_LOC).mkdirs();
        }
        try {
            ffmpeg = new FFmpeg();
            ffprobe = new FFprobe();
            ffmpegExecutor = new FFmpegExecutor(ffmpeg, ffprobe);
        } catch (IOException e) {
            System.out.println("Failed to initialize FFmpeg.");
            e.printStackTrace();
        }
    }

    public void checkAndFixEmbeds(Message message) {
        for (Attachment attachment : message.getAttachments()) {
            VideoFile result = checkEmbed(attachment);
            if (result != null) {
                System.out.println("h.265 attachment found! Fixing.");
                String fixed = fixEmbed(result);
                System.out.printf("Fixed embed location: %s.%n", fixed);
                try {
                    System.out.println("Deleting " + result.fileLoc);
                    new File(result.fileLoc).delete();
                } catch (Exception e) {
                    System.out.printf("Failed to delete file %s.%n", result.fileLoc);
                }
            }
        }
    }

    private VideoFile checkEmbed(Attachment attachment) {
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
        if (!goodExtension) {
            split = url.split("/");
            System.out.printf("%s is not a repairable video file, so it was not downloaded.%n",
                    split[split.length - 1]);
            return null;
        }

        String fileLoc = DOWNLOAD_LOC + System.currentTimeMillis()
            + "." + split[split.length - 1];

        System.out.printf("Downloading %s -> %s...", url, fileLoc);

        // We have a good extension on our hands!
        if (!Downloader.downloadFromURL(url, fileLoc)) {
            System.out.println("Failed to download " + url);
            return null;
        } else System.out.println("done");

        try {
            FFmpegProbeResult res = ffprobe.probe(fileLoc);
            for (FFmpegStream stream : res.getStreams()) {
                if (stream.codec_name.equalsIgnoreCase("hevc")) {
                    return new VideoFile(fileLoc, res);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to probe " + fileLoc);
            new File(fileLoc).delete();
            e.printStackTrace();
        }


        return null;
    }

    private String fixEmbed(VideoFile input) {
        String[] splitLoc = input.fileLoc.split("/");
        String newLoc = "";
        for (int i = 0; i < splitLoc.length - 1; i++) {
            newLoc += splitLoc[i] + "/";
        }
        newLoc += "h264_" + splitLoc[splitLoc.length - 1];

        long oldSize = new File(input.fileLoc).length();
        long targetSize = oldSize;
        System.out.println(oldSize);

        FFmpegBuilder builder = new FFmpegBuilder()
            .setInput(input.fileLoc)
            .addOutput(newLoc)
                .disableSubtitle()
                .setTargetSize(oldSize)
                .setVideoCodec("libx264")
                .done();
        ffmpegExecutor.createTwoPassJob(builder).run();

        return newLoc;
    }
}
