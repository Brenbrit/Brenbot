import discord

client = discord.Client()

@client.event
async def on_ready():
    print('We have logged in as {0.user}'.format(client))

@client.event
async def on_message(message):
    if message.author == client.user:
        return

    if message.content.startswith('$hello'):
        await message.channel.send('Hello!')

client.run("NDU3Mjg1MDE0NjMyMjY3Nzg5.XTKLSg.WcbluBpyBaJBPUF8PKy4G5gX5hs")
