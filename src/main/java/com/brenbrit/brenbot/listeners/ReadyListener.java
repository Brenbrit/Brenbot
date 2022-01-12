package com.brenbrit.brenbot.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadyListener extends ListenerAdapter {

    private Logger logger = LoggerFactory.getLogger(ReadyListener.class);

    public ReadyListener() {
        logger.info("ReadyListener initialized.");
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("Bot ready!");
        System.out.println("Bot ready!");
    }

}
