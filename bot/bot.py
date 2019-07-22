import discord
import os
import platform
import subprocess
import sys

#used in the @ parts and at the client.run line
client = discord.Client()
currentPlatform = platform.system().lower()

def getToken():
    if currentPlatform.startswith("linux"):
        loc = "/home/ubuntu/codes.txt"
    elif currentPlatform.startswith("win"):
        loc = "C:\\Users\\Brenbrit\\Documents\\Brenbot\\important\\codes.txt"
    file = open(loc, "r")
    return file.readline().split(":")[1]

def update():
    if currentPlatform.startswith("linux"):
        print("gotem")
        #subprocess.Popen(["sh", "/home/ubuntu/update.sh"])
        subprocess.Popen("chmod +x update.sh")
        subprocess.Popen("./update.sh")
        sys.exit(0)

#these are instant responses
kneejerkList = [
    ["dragon maid sucks", "screw you"],
    ["ayy", "lmao"],
    ["ligma", "what's ligma"],
    ["k","You fucking do that every damn time I try to talk to you about anything even if it's not important you just say K and to be honest it makes me feel rejected and unheard like nothing would be better that that bullshit who the fuck just says k after you tell them something important I just don't understand how you think that's ok and I swear to god you're probably just gonna say k to this but when you do you'll know that you're slowly killing me inside"],
    ["ur gay", "nou"]
    ]

kneejerkBeginningList = [
    ["https://media.discordapp.net/attachments","reee"]
    ]
    

@client.event
async def on_ready():
    print('We have logged in as {0.user}'.format(client))

@client.event
async def on_message(message):
    if message.author == client.user:
        return

    if message.content.lower().startswith("update"):
        await client.send_message(message.channel, content = "ok")
        update()
    for test in kneejerkList:
        if (message.content.lower() == test[0]):
            await client.send_message(message.channel, content = test[1])
    for test in kneejerkBeginningList:
        if (message.content.lower().startswith(test[0])):
            await client.send_message(message.channel, content = test[1])

#this actually starts the bot
token = getToken()
client.run(token)



    
