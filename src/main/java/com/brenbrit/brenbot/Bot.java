package com.brenbrit.brenbot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class Bot {

    public static void main(String[] args) {
        System.out.println("Starting up Brenbot.");

        final String token = args[0];

        // A DiscordClient only represents the operations we can do while not
        // logged in. To do other bot things, we've got to log in.
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            if ("hello".equals(message.getContent().toLowerCase()))
                message.getChannel().block().
                    createMessage("world! Programmed to work and not to feel!").block();
        });
    }
}
