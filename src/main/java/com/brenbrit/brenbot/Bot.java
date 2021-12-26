package com.brenbrit.brenbot;

import com.brenbrit.brenbot.listeners.*;
import com.brenbrit.brenbot.utils.HelpGenerator;
import com.brenbrit.brenbot.utils.VersionGetter;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import javax.security.auth.login.LoginException;

public class Bot {

    public JDA jda;
    public static final String PROPERTIES_LOC = "config.properties";
    private Properties properties;
    private CommandListener commandListener;

    public static void main(String[] args) {
        System.out.println("Starting up Brenbot.");
        new Bot().start();
    }

    public Bot() {
        this.properties = readProperties(Bot.PROPERTIES_LOC);
        this.commandListener = new CommandListener();
    }

    public void start() {

        try {
            jda = JDABuilder.createDefault(properties.getProperty("discord.token"))
                .addEventListeners(new MessageListener(), new ReadyListener(),
                commandListener)
                .build().awaitReady();
        } catch (LoginException le) {
            le.printStackTrace();
            System.out.println("LoginException caught. Exiting.");
            System.exit(1);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            System.out.println("InterruptedException caught. Exiting.");
            System.exit(1);
        }

        System.out.println("Getting version.");
        String version = VersionGetter.getVersion("pom.xml");
        if (version != null) {
            String status = "v" + version;
            System.out.println("Setting \"" + status + "\" as status.");
            jda.getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, status));
        }
        System.out.println("Generating /help message.");
        String helpMessage = new HelpGenerator().generateHelpMessage();
        this.commandListener.addBasicTextCommand("help", helpMessage);
        System.out.print("Upserting /help command...");
        jda.upsertCommand("help",
                "Prints a short description of Brenbot's public commands")
                .complete();
        System.out.println(" done!");

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
