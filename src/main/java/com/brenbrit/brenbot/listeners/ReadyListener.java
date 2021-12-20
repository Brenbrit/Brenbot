package com.brenbrit.brenbot.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter {

    public ReadyListener() {
        System.out.println("ReadyListener initialized.");
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Ready!");
    }

}
