import discord
import os
import platform
import subprocess
import sys
import time

#used in the @ parts and at the client.run line
client = discord.Client()
currentPlatform = platform.system().lower()

def printKomi():
    if currentPlatform.startswith("linux"):
        loc = "/home/ubuntu/Brenbot/komi.txt"
    elif currentPlatform.startswith("win"):
        loc = "C:\\Users\\Brenbrit\\Documents\\Brenbot\\komi.txt"
    with open(loc, encoding="utf8") as f:
        for line in f.readlines():
            print(line, end="")
    print("\n\n")

def getToken():
    if currentPlatform.startswith("linux"):
        loc = "/home/ubuntu/codes.txt"
    elif currentPlatform.startswith("win"):
        loc = "C:\\Users\\Brenbrit\\Documents\\Brenbot connection info\\codes.txt"
    file = open(loc, "r")
    return file.readline().split(":")[1]

def update():
    if currentPlatform.startswith("linux"):
        print("Recognized a linux computer. Starting the update.sh.")
        #subprocess.Popen(["sh", "/home/ubuntu/update.sh"])
        subprocess.Popen(['./update.sh'], shell=True)
        sys.exit(0)

#these are instant responses
kneejerkList = [
    ["dragon maid sucks", "screw you",0],
    ["ayy", "lmao",0],
    ["ligma", "what's ligma",0],
    #["k","You fucking do that every damn time I try to talk to you about anything even if it's not important you just say K and to be honest it makes me feel rejected and unheard like nothing would be better that that bullshit who the fuck just says k after you tell them something important I just don't understand how you think that's ok and I swear to god you're probably just gonna say k to this but when you do you'll know that you're slowly killing me inside",0],
    ["ur gay", "nou",0],
    ["wait","...",0]
    ]

kneejerkBeginningList = [
    #["https://media.discordapp.net/attachments","reee",0],
    #["https://cdn.discordapp.com/attachments","reee",0],
    ["im a bad guy","duh",0],
    ["i'm a bad guy","duh",0],
    ["xd","ecks dee",0],
    ["sauce","https://i.imgur.com/R390EId_d.jpg",0]
    ]
    

@client.event
async def on_ready():
    print('We have logged in as {0.user}'.format(client))
    printKomi()

@client.event
async def on_message(message):
    #temporary
    print("Received message:\"", end="")
    print(message.content, end="")
    print("\"")
    if message.author == client.user:
        return

    if message.content.lower().startswith("update"):
        await client.send_message(message.channel, content = "ok")
        update()

    #test for all the kneejerk-reaction comments. require an exact match.
    #timt.time() returns a seconds amount, spam filter is 15sec
    for test in kneejerkList:
        if (message.content.lower() == test[0]):
            if time.time() - test[2] >= 15:
                test[2] = time.time()
                await client.send_message(message.channel, content = test[1])
            else:
                print("anti-spam caught something")

    #test for the same as above, but works whenever the test
    #text is at the beginning
    for test in kneejerkBeginningList:
        if (message.content.lower().startswith(test[0])):
            if time.time() - test[2] >= 15:
                test[2] = time.time()
                await client.send_message(message.channel, content = test[1])
            else:
                print("anti-spam caught something")

#this actually starts the bot
token = getToken()
client.run(token)



    
