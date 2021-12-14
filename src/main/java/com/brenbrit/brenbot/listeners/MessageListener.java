package com.brenbrit.brenbot.listeners;

import com.brenbrit.brenbot.utils.EmbedFixer;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

/**
 * Created by steve on 14/07/2016.
 */

public class MessageListener {

    private EmbedFixer embedFixer;

    public MessageListener() {
        System.out.println("Init MessageListener");
        embedFixer = new EmbedFixer();
    }

    public Mono<MessageChannel> onReady(MessageCreateEvent event) {
        /*
         return event.getMessage().getChannel()
                .doOnSuccess(channel -> {
                    // If the authorId is a bot, message get ignored
                    if (! event.getMessage().getAuthor().map(User::isBot).orElse(true)) {
                        System.out.println("Received a message!");
                    }
                }); */
        Message message = event.getMessage();

        if (message.getAuthor().isPresent()) {
            User author = message.getAuthor().get();
            System.out.printf("%s#%s: \"%s\"%n", author.getUsername(),
                    author.getDiscriminator(), message.getContent());
        }

            // Does the message have any attachments?
            embedFixer.checkAndFixEmbeds(message);

        return Mono.empty();
    }
}
