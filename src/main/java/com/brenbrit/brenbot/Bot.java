package com.brenbrit.brenbot;

import com.brenbrit.brenbot.listeners.*;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

public class Bot {

    public static final String PROPERTIES_LOC = "config.properties";
    private Properties properties;

    public static void main(String[] args) {
        new Bot().start();
    }

    public Bot() {
        this.properties = readProperties(Bot.PROPERTIES_LOC);
    }

    public void start() {

        System.out.println("Starting up Brenbot.");

        // A DiscordClient only represents the operations we can do while not
        // logged in. To do other bot things, we've got to log in.
        final DiscordClient discord = DiscordClient.create(properties.getProperty("discord.token"));

        discord.gateway().setEnabledIntents(IntentSet.of(
                    Intent.GUILDS,
                    Intent.GUILD_MEMBERS,
                    Intent.GUILD_MESSAGES,
                    Intent.GUILD_MESSAGE_REACTIONS,
                    Intent.DIRECT_MESSAGES))
            .withGateway(client -> Mono.when(
                        readyListener(client),
                        messageListener(client)))
            .block();

    }

    private Mono<Void> messageListener(GatewayDiscordClient client) {
        final MessageListener listener = new MessageListener();
        return client.getEventDispatcher().on(MessageCreateEvent.class)
            .flatMap(listener::onReady)
            .then();
    }

    private Mono<Void> readyListener(GatewayDiscordClient client) {
        final ReadyListener listener = new ReadyListener();
        return client.getEventDispatcher().on(ReadyEvent.class)
            .flatMap(listener::onReady)
            .then();
    }

    public static Properties readProperties(String fileName) {
        FileInputStream fis = null;
        Properties prop = null;

        try {
            fis = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(fis);
        } catch (FileNotFoundException fnfe) {
            System.out.println("Propperties file not found at " + fileName + ".");
            fnfe.printStackTrace();
            System.exit(1);
        } catch (IOException ioe) {
            System.out.println("IOException while readidng Properties file at " + fileName + ".");
            ioe.printStackTrace();
            System.exit(1);
        } finally {
            try {
                fis.close();
            } catch (IOException ioe) {
                System.out.println("IOException while closing properties file.");
                ioe.printStackTrace();
            }
        }
        return prop;
    }
}
