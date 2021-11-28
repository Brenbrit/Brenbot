package com.brenbrit.brenbot;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.GatewayDiscordClient;
//import discord4j.core.object.entity.Message;
//import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
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
        final DiscordClient client = DiscordClient.create(properties.getProperty("discord.token"));
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) ->
            gateway.on(ReadyEvent.class, event ->
                Mono.fromRunnable(() -> {
                    final User self = event.getSelf();
                    System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
                })));

        System.out.println("Blocking");
        login.block();
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
