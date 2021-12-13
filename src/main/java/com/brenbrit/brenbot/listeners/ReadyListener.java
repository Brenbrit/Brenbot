package com.brenbrit.brenbot.listeners;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import reactor.core.publisher.Mono;

/**
 * Created by steve on 14/07/2016.
 */
public class ReadyListener {


    public Mono<Void> onReady(ReadyEvent event) {

        System.out.printf("Logged in as %s#%s%n",
                event.getSelf().getUsername(),
                event.getSelf().getDiscriminator());

        return Mono.empty();
    }
}
