package com.brenbrit.brenbot.listeners;

import com.brenbrit.brenbot.utils.EmbedFixer;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageListener extends ListenerAdapter {

    private EmbedFixer embedFixer;
    private Logger logger = LoggerFactory.getLogger(MessageListener.class);

    public MessageListener() {
        embedFixer = new EmbedFixer();
        logger.info("MessageListener initialized.");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        printMessageReceived(event);

        Message msg = event.getMessage();
        embedFixer.checkAndFixEmbeds(msg);


        /* if (msg.getContentRaw().equals(".hello")) {
            System.out.println("Hello found");
            MessageChannel channel = event.getChannel();
            long time = System.currentTimeMillis();
            channel.sendMessage("world!")
                .queue(response -> {
                    response.editMessageFormat("world!: %d ms", System.currentTimeMillis() - time).queue();
                });
        } */
    }

    private void printMessageReceived(MessageReceivedEvent event) {

        if (event.isFromType(ChannelType.PRIVATE)) {
            logger.info(String.format("[PM] %s: $s\n", event.getAuthor().getName(),
                event.getMessage().getContentDisplay()));
        } else {
            logger.info(String.format("[%s][%s] %s: %s\n", event.getGuild().getName(),
                event.getTextChannel().getName(),
                event.getMember().getEffectiveName(),
                event.getMessage().getContentDisplay()));
        }
    }

}
