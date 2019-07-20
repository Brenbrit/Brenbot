import discord

#used in the @ parts and at the client.run line
client = discord.Client()

@client.event
async def on_ready():
    print('We have logged in as {0.user}'.format(client))

@client.event
async def on_message(message):
    if message.author == client.user:
        return

    if message.content.lower().startswith("update"):
        await message.channel.send('ok')


#this actually starts the bot
client.run("NDU3Mjg1MDE0NjMyMjY3Nzg5.XTKLSg.WcbluBpyBaJBPUF8PKy4G5gX5hs")
