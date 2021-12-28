package com.brenbrit.brenbot.entities;

import com.brenbrit.brenbot.entities.FakeUser;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class Kneejerk {

    public String name;
    public String message;
    public FakeUser sender;

    public Kneejerk(String name, String message, FakeUser sender) {
        System.out.println("Kneejerk created with a fake user.");
        System.out.println("\tname: " + name);
        System.out.println("\tmessage: " + message);
        System.out.println("Fake user: (" + sender.name + ", "
            + sender.pfp_loc + ")");
    }

    // Kneejerk to be sent with the default bot user
    public Kneejerk(String name, String message) {
        System.out.println("Kneejerk created without a fake user.");
        System.out.println("\tname: " + name);
        System.out.println("\tmessage: " + message);
    }

    public void MessageAction sendKneejerk(TextChannel chan) {
        System.out.println("sendKneejerk called");
    }

}
