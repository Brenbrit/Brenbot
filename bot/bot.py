import discord
import subprocess
import time
import processors
import utils

#used in the @ parts and at the client.run line
client = discord.Client()

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
    utils.printKomi()

@client.event
async def on_message(message):
    text_responses = []
    file_responses = []

    #the processors output an array
    #each array has:
        #0: text responses, in the form of tuples
            #[channel, content]
        #1: file responses, in the form of tuples
            #[channel, content]
            #these will be deleted later
        #anything else may be appended to this list, but each
        #returned array from processors must have >= 2 sub-arrays.
    #temporary

    print("Received message:\"", end="")
    print(message.content, end="")
    print("\"")

    if message.author == client.user:
        return

    out = processors.process(message)
    text_responses = out[0]
    file_responses = out[1]

    for text_resp in text_responses:
        await client.send_message(text_resp[0], content = text_resp[1])
    for file_resp in file_responses:
        await client.send_message(file_resp[0], file_resp[1])


    #if message.content.lower().startswith("update"):
    #    await client.send_message(message.channel, content = "ok")
    #    update()

    #test for all the kneejerk-reaction comments. require an exact match.
    #timt.time() returns a seconds amount, spam filter is 15sec
    #for test in kneejerkList:
    #    if (message.content.lower() == test[0]):
    #        if time.time() - test[2] >= 15:
    #            test[2] = time.time()
    #            await client.send_message(message.channel, content = test[1])
    #        else:
    #            print("anti-spam caught something")

    #test for the same as above, but works whenever the test
    #text is at the beginning
    #for test in kneejerkBeginningList:
    #    if (message.content.lower().startswith(test[0])):
    #        if time.time() - test[2] >= 15:
    #            test[2] = time.time()
    #            await client.send_message(message.channel, content = test[1])
    #        else:
    #            print("anti-spam caught something")

#this actually starts the bot
token = utils.getToken()
client.run(token)
