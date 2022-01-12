package com.brenbrit.brenbot;

import com.brenbrit.brenbot.listeners.*;
import com.brenbrit.brenbot.utils.HelpGenerator;
import com.brenbrit.brenbot.utils.VersionGetter;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    final static Logger logger = LoggerFactory.getLogger(Bot.class);

    public static void main(String[] args) {
        logger.info("Starting up Brenbot.");
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
            logger.error("LoginException caught. Exiting.");
            System.exit(1);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            logger.error("InterruptedException caught. Exiting.");
            System.exit(1);
        }

        logger.info("Getting version.");
        String version = VersionGetter.getVersion("pom.xml",
                LoggerFactory.getLogger("VersionGetter"));
        if (version != null) {
            String status = "v" + version;
            logger.info("Setting \"" + status + "\" as status.");
            jda.getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, status));
        }
        logger.info("Generating /help message.");
        String helpMessage = new HelpGenerator().generateHelpMessage();
        this.commandListener.addBasicTextCommand("help", helpMessage);
        logger.info("Upserting /help command...");
        jda.upsertCommand("help",
                "Prints a short description of Brenbot's public commands")
                .complete();
        logger.info("Done upserting.");

    }

    public static Properties readProperties(String fileName) {
        FileInputStream fis = null;
        Properties prop = null;

        try {
            fis = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(fis);
        } catch (FileNotFoundException fnfe) {
            logger.error("Propperties file not found at " + fileName + ".");
            logger.error(fnfe.getStackTrace().toString());
            System.exit(1);
        } catch (IOException ioe) {
            logger.error("IOException while readidng Properties file at " + fileName + ".");
            logger.error(ioe.getStackTrace().toString());
            ioe.printStackTrace();
            System.exit(1);
        } finally {
            try {
                fis.close();
            } catch (IOException ioe) {
                logger.error("IOException while closing properties file.");
                logger.error(ioe.getStackTrace().toString());
            }
        }
        return prop;
    }
}
