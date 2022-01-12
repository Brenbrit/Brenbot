package com.brenbrit.brenbot.entities;

import com.brenbrit.brenbot.utils.FileReader;

import java.util.ArrayList;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kneejerk {

    public static final String KNEEJERKS_LOC = "src/main/resources/kneejerks.csv";

    public String name;
    public String message;
    public String sender;
    private Logger logger = LoggerFactory.getLogger(Kneejerk.class);

    public Kneejerk(String name, String message, String sender) {
        logger.debug("Kneejerk created with a fake user.");
        this.name = name;
        this.message = message;
        this.sender = sender;
    }

    // Kneejerk to be sent with the default bot user
    public Kneejerk(String name, String message) {
        logger.debug("Kneejerk created without a fake user.");
        this.name = name;
        this.message = message;
        this.sender = null;
    }

    public MessageAction sendKneejerk(TextChannel chan) {
<<<<<<< HEAD
        System.out.println("sendKneejerk called");
        return chan.sendMessage(message);
=======
        logger.debug("sendKneejerk for " + name + " called.");
        return chan.sendMessage(message);
    }

    public void sendKneejerk(TextChannel chan, Message msg) {
        try {
            sendKneejerk(chan).queue();
            logger.debug("Deleting message with content " + msg.getContentDisplay());
            msg.delete().queue();
        } catch (Exception e) {
            logger.error("Failed to execute kneejerk " + name);
            logger.error(e.getStackTrace().toString());
        }
    }

    public static ArrayList<Kneejerk> readKneejerks() {

        Logger logger = LoggerFactory.getLogger(Kneejerk.class);

        // this ArrayList holds the kneejerks we will eventually return
        ArrayList<Kneejerk> kneejerks = new ArrayList<Kneejerk>();
        logger.debug("Opening kneejerks csv file at " + KNEEJERKS_LOC);

        ArrayList<String> lines = FileReader.readCSV(KNEEJERKS_LOC);

        for (String line : lines) {
            // Some lines won't have anything in the B column. These don't
            // have explicit senders.
            String[] splitLine = line.split(",");
            if (splitLine[1].equals("")) {
                kneejerks.add(new Kneejerk(splitLine[0], splitLine[2]));
            } else {
                kneejerks.add(new Kneejerk(splitLine[0], splitLine[2], splitLine[1]));
            }
        }

        return kneejerks;
>>>>>>> e9b5ea083afe052e1b95a585dcd0bcda01bc943e
    }

}
