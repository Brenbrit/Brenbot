package com.brenbrit.brenbot;

import com.brenbrit.brenbot.listeners.*;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import javax.security.auth.login.LoginException;

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

        try {
            final JDA jda = JDABuilder.createDefault(properties.getProperty("discord.token"))
                .addEventListeners(new MessageListener(), new ReadyListener())
                .build();
        } catch (LoginException le) {
            System.out.println("LoginException caught. Exiting.");
            System.exit(1);
        }

        System.out.println("done");



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
