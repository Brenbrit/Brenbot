package com.brenbrit.brenbot.entities;

import com.brenbrit.brenbot.utils.FileReader;

import java.util.ArrayList;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeUser {

    public String name;
    public String displayName;
    public String pfp_loc;
    public static final String FAKEUSERS_LOC = "src/main/resources/fake_users.csv";

    public FakeUser(String name, String displayName, String pfp_loc) {
        this.name = name;
        this.displayName = displayName;
        this.pfp_loc = pfp_loc;
    }

    public static Hashtable<String, FakeUser> readFakeUsers() {

        Logger logger = LoggerFactory.getLogger(FakeUser.class);

        // this Hashtable holds the fake users we will eventually return
        // name is key, value is the FakeUser
        Hashtable<String, FakeUser> fakeUsers = new Hashtable<String, FakeUser>();
        logger.debug("Opening kneejerks csv file at " + FAKEUSERS_LOC);

        ArrayList<String> lines = FileReader.readCSV(FAKEUSERS_LOC);

        for (String line : lines) {
            // Some lines won't have anything in the B column. These don't
            // have explicit senders.
            String[] splitLine = line.split(",");
            FakeUser toAdd = new FakeUser(splitLine[0], splitLine[1], splitLine[2]);
            fakeUsers.put(toAdd.name, toAdd);
        }

        return fakeUsers;
    }
}
