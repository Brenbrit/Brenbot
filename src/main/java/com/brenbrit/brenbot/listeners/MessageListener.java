package com.brenbrit.brenbot.listeners;

import com.brenbrit.brenbot.entities.FakeUser;
import com.brenbrit.brenbot.entities.Kneejerk;
import com.brenbrit.brenbot.utils.EmbedFixer;

import java.util.ArrayList;
import java.util.Hashtable;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageListener extends ListenerAdapter {

    private EmbedFixer embedFixer;
    private Logger logger = LoggerFactory.getLogger(MessageListener.class);
    private ArrayList<Kneejerk> kneejerks;
    private Hashtable<String, FakeUser> fakeUsers;
    public String prefix = ".";

    private final String PROFAWNSOR_ID = "341282409565650964";

    public MessageListener() {
        embedFixer = new EmbedFixer();
        logger.debug("Reading kneejerks from " + Kneejerk.KNEEJERKS_LOC);
        kneejerks = Kneejerk.readKneejerks();
        logger.debug("Finished reading kneejerks.");
        logger.debug("Reading fake users from " + FakeUser.FAKEUSERS_LOC);
        fakeUsers = FakeUser.readFakeUsers();
        logger.debug("Finished reading fake users.");
        logger.debug("MessageListener initialized.");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        printMessageReceived(event);

        Message msg = event.getMessage();

        // Is the message a kneejerk reaction command?
        for (Kneejerk kneejerk : kneejerks) {
            logger.info("Message: " + msg.getContentDisplay());
            logger.info("Kneejerk " + prefix + kneejerk.name);
            if (msg.getContentDisplay().equalsIgnoreCase(prefix + kneejerk.name)) {
                kneejerk.sendKneejerk(msg.getTextChannel(), msg);
                break;
            }
        }

        // Is the message from profawnsor?
        if (msg.getAuthor().getId().equals(PROFAWNSOR_ID)) {
            logger.info("Profawnsor found. Sending funny gif");
            msg.getTextChannel().sendMessage("https://media.discordapp.net/attachments/676920706243493898/1039347070915579974/togif.gif").queue();
        }

        // Check and fix embeds
        embedFixer.checkAndFixEmbeds(msg);
    }

    private void printMessageReceived(MessageReceivedEvent event) {

        if (event.isFromType(ChannelType.PRIVATE)) {
            logger.debug(String.format("[PM] %s: $s\n", event.getAuthor().getName(),
                event.getMessage().getContentDisplay()));
        } else {
            logger.debug(String.format("[%s][%s] %s: %s\n", event.getGuild().getName(),
                event.getTextChannel().getName(),
                event.getMember().getEffectiveName(),
                event.getMessage().getContentDisplay()));
        }
    }

}
