import discord
import os
import platform
import subprocess

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
        print(subprocess.Popen("sh /home/ubuntu/update.sh"))
        sys.exit(0)
        

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


#this actually starts the bot
token = getToken()
client.run(token)



    
