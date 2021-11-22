#    ______                _           _
#    | ___ \              | |         | |
#    | |_/ /_ __ ___ _ __ | |__   ___ | |_
#    | ___ \ '__/ _ \ '_ \| '_ \ / _ \| __|
#    | |_/ / | |  __/ | | | |_) | (_) | |_
#    \____/|_|  \___|_| |_|_.__/ \___/ \__|

# This is the script to run the best Discord bot: Brenbot!
# written by Brendan Guillory (Brenbrit#2222)
# Use absolute paths!


import discord
from discord import Webhook, RequestsWebhookAdapter
from discord.ext import tasks, commands
import sys
import requests
import re
import os
import shutil
import time
from converter import Converter
from datetime import datetime, timedelta
from owoify import owoify
import random
import uuid


# ADMINS
# list of ids for admin users
ADMINS = [
        316021839220572160, # brenbrit
        270292738589720597, # kevyn
        352234788255563776, # vackham
        297945389863272450, # pip
        386688181879177226, # brit
        ]

# don't read messages by these people
IGNORED_USERS = [
        ]

# Read messages from these select bots
ALLOWED_BOTS = [
        247852652019318795, # Dad Bot
        ]

# list of video codecs that discord embeds
EMBEDDABLE_VIDEO_CODECS = [
        "h264",
        "aac"
        ]

# Imaginary users directory.
# This is the base dir for all profile pictures and the users.txt file.
IMAGINARY_USERS_DIR = "/home/brendan/brenbot/data/imaginary_users/"

# data file that stores the last time a user was banned from Brit's server
LAST_BAN_LOC = "data/last_ban"

# we need to use the /tmp directory for video re-encoding
def refresh_tmp_dir():
    # check if the directory already exists
    if os.path.isdir("/tmp/brenbot"):
        try:
            shutil.rmtree("/tmp/brenbot")
        except Exception as e:
            print("Failed to clear /tmp/brenbot: {}".format(e))
            sys.exit(1)
    # make the folder
    os.makedirs("/tmp/brenbot")

# Load imaginary users, like Kotori. This is done at Brenbot's
# startup.
def load_imaginary_users(imaginary_users_file):
    users = {}
    lines = []
    try:
        with open(imaginary_users_file) as f:
            lines = f.readlines()
    except Exception as e:
        print("Could not read imaginary users file. Stack trace follows.")
        print(e)

    for line in lines:
        if line[-1] == "\n":
            line = line[:-1]
        if line[0] not in ["#", " ", "\t"]:
            split_line = line.split(";")

            variable_name = split_line[0]
            user_name = split_line[1]
            pfp = IMAGINARY_USERS_DIR + split_line[2]

            users[split_line[0]] = {"name":user_name, "pfp":pfp}
    return users

# Get a list of all untracked imaginary users. Each item in the list
# is a string referring to an imaginary user in imaginary_users.
def get_untracked_imaginary_pfps(imaginary_users_dir):
    file_list = []

    # Append each file name (except users.txt) to file_list
    for root, dirs, files in os.walk(imaginary_users_dir):
        for name in files:
            if name != "users.txt":
                file_list.append(IMAGINARY_USERS_DIR + name)

    # get a list of tracked users
    users = load_imaginary_users(imaginary_users_dir + "users.txt")
    tracked_files = []
    for user in users.keys():
        tracked_files.append(users[user]["pfp"])

    # Make a list of untracked pics
    untracked_pics = []
    for picture in file_list:
        if picture not in tracked_files:
            untracked_pics.append(picture)

    print(untracked_pics)

    return untracked_pics



# Remove all files in the imaginary_users_dir other than users.txt and whatever
# images it specifies.
def del_untracked_imaginary_users(imaginary_users_dir):

    # Get a list of untracked pictures
    untracked_pics = get_untracked_imaginary_pfps(imaginary_users_dir)

    # Print to log
    if len(untracked_pics) > 0:
        print("Removing {} files:".format(len(untracked_pics)))
    else:
        print("No untracked files!")
        return

    # Remove picture, then print to log.
    for picture in untracked_pics:
        os.remove(picture)
        print(picture.split("/")[-1])


# creates a webhook with a channel id and some other info
async def send_with_webhook(client, channel, message, username, profile_picture=None):
    # Make the webhook
    webhook = await channel.create_webhook(name=username, avatar=profile_picture)
    # Send the message, then delete the webhook
    await webhook.send(message)
    await webhook.delete()

# user impersonator
async def send_as(client, user_id, channel, message):
    # Get user to impersonate (this fails on webhooks)
    try:
        user = await channel.guild.fetch_member(user_id)
    except discord.HTTPException:
        user = await client.fetch_user(user_id)
    # Profile picture (bytes-like object)
    response = requests.get(user.avatar_url)
    await send_with_webhook(client, channel, message, user.display_name, response.content)

# Send as imaginary user
async def send_as_imaginary(client, imaginary_user, channel, message):
    # Read the profile picture from disk. This variable "pfp" is a bytes-like
    # object which can be passed to send_with_webhook.
    with open(imaginary_user["pfp"], "rb") as image:
        pfp = image.read()
    await send_with_webhook(client, channel, message, imaginary_user["name"], pfp)

# scan attachments for bad embeds
async def scan_attachments(message):
    # this is the list of file extensions to scan
    scannable_extensions = ["mp4", "mov", "mkv"]
    for attachment in message.attachments:
        file_extension = attachment.url.split('.')[-1].lower()
        if file_extension in scannable_extensions:
            # scan it!
            # first we have to download it
            try:
                r = requests.get(attachment.url)
                file_name = "/tmp/brenbot/" + str(time.time()) + '.' + file_extension
                # print("Downloading to " + file_name + "... ", end='')
                with open(file_name, 'wb') as f:
                    f.write(r.content)
                # print("success!")
            except:
                print("failed to download.")
                os.remove(file_name)
                return
            # download finished!
            # let's move on to the scanning.
            info = conv.probe(file_name)
            if message.channel.id == 405947520884146200:
                await message.channel.send(info.video.codec)
            if info.video.codec not in EMBEDDABLE_VIDEO_CODECS:
                # re-encode!
                try:
                    out_file = re_encode_video(file_name, info)
                except Exception as e:
                    print("Failed to convert video.")
                    print(e)
                if out_file == '':
                    # converter failed
                    continue
                await message.channel.send(file=discord.File(out_file))
                os.remove(out_file)
            os.remove(file_name)

# convert video to h264
def re_encode_video(original, info):
    output_file_name = "/tmp/brenbot/" + str(time.time()) + ".mp4"

    # does the video have audio?
    if info.audio == None:
        # looks like the video doesn't have any audio!
        # use no-audio settings
        options = {
                'format': 'mp4',
                'video': {
                    'codec': 'h264',
                    'width': info.video.video_width,
                    'height': info.video.video_height,
                    'fps': info.video.video_fps
                    }
                }
    else:
        # we have audio
        options = {
                'format': 'mp4',
                'audio': {
                    'codec': info.audio.codec,
                    'samplerate': info.audio.audio_samplerate,
                    'channels': info.audio.audio_channels
                    },
                'video': {
                    'codec': 'h264',
                    'width': info.video.video_width,
                    'height': info.video.video_height,
                    'fps': info.video.video_fps
                    }
                }
    # prepare the converter
    upload_converter = conv.convert(original, output_file_name, options)
    print("Converting...")
    try:
        for timecode in upload_converter:
            pass
    except Exception as e:
        print("Failed to convert.")
        print(e)
        return ''
    print("Finished!")

    return output_file_name

# replace @s in a string with local names
async def replace_at_with_nick(client, guild, msg):

    # First, take out any @everyones or @heres.
    msg = re.sub("@everyone", "everyone", msg)
    msg = re.sub("@here", "here", msg)

    # for finding what is an @ and what isn't
    at_regex = r'(<@!{0,1}\d+>)'
    # for taking out the <@! or <@ at the beginning of an @
    beginning_at_regex = r'<@!{0,1}'

    message_parts = re.split(at_regex, msg)

    # only iterate over mention parts
    for i in range(1, len(message_parts), 2):
        user_id = int(re.split(beginning_at_regex, message_parts[i])[1][:-1])
        mentioned_user = await guild.fetch_member(user_id)
        message_parts[i] = mentioned_user.display_name

    to_return = ''
    for part in message_parts:
        to_return += part

    return to_return

# owoifier: new and improved!
async def advanced_owoify(client, guild, original_message, mode):
    no_ats = await replace_at_with_nick(client, guild, original_message)
    return owoify(no_ats, mode)

# task to automatically ban a random member off Brit's server once
# a week.
class BanCog(commands.Cog):

    #SECONDS_IN_WEEK = 60*60*24*7
    SECONDS_IN_WEEK=15
    REPEAT_TIME = SECONDS_IN_WEEK
    SERVER_ID = 609659675964735498

    def __init__(self, bot):
        self.bot = bot
        self.ban.start()

    def get_last_ban(self):
        last_ban = 0
        with open(LAST_BAN_LOC, 'r') as file:
            last_ban = float(file.read())
            file.close()
        return last_ban

    def cog_unload(self):
        self.ban.cancel();

    @tasks.loop(seconds=REPEAT_TIME)
    async def ban(self):

        ban_message = "\"Suck my robo cock\" - Brenbot"

        last_ban = self.get_last_ban()
        print("Ban method called. Last ban was " + datetime.fromtimestamp(last_ban).strftime("%Y-%m-%d at %H:%M:%S UTC."))
        current_time = time.time()

        if (current_time - last_ban <= BanCog.REPEAT_TIME):
            print("This was less than {} seconds ago. Waiting {} seconds before another ban.".format(
                BanCog.REPEAT_TIME, (BanCog.REPEAT_TIME - (current_time - last_ban))))
            time.sleep(BanCog.REPEAT_TIME - (current_time - last_ban))

        print("Banning a user from Brit's server.")
        print("Fetching server...")
        server = await self.bot.fetch_guild(BanCog.SERVER_ID)

        print("Finding users...")
        member_list = []
        async for member in server.fetch_members():
            member_list.append(member)
        user = random.choice(member_list)

        #await server.ban(user, delete_message_days=0, reason=ban_message)
        print("Banning user {} ({}).".format(user, user.name))


        with open(LAST_BAN_LOC, 'r+') as file:
            file.seek(0)
            file.write(str(time.time()))
            file.truncate()
        print("Wrote to " + LAST_BAN_LOC)


class MyClient(discord.Client):
    async def on_ready(self):
        print("Logged in as {0}!".format(self.user))
        print("Starting ban cog...")
        BanCog(self)

    async def on_guild_join(guild):
        print("Joined new guild: {}".format(guild.name))

    async def on_message(self, message):
        # make sure we didn't send the message!
        if message.author == client.user:
            return

        # Don't read messages by other bots
        if message.author.bot and message.author.id not in ALLOWED_BOTS:
            return

        # don't read messages by banned users
        if message.author.id in IGNORED_USERS:
            return

        # scan attachments for bad embeds
        await scan_attachments(message)

        # Restart the bot.
        if message.content.lower() in [".reboot", ".restart"] and message.author.id in ADMINS:
            await message.add_reaction("🆗")
            print("Received restart signal. Exiting with error.")
            sys.exit(1)

        # Add a user to the blocked list (temporary, resets on restart)
        if message.content.lower().startswith(".ignore") and message.author.id in ADMINS:
            for member in message.mentions:
                IGNORED_USERS.append(member.id)
            IGNORED_USERS.append(int(message.content.split(' ')[-1]))
            await message.add_reaction("✅")

        # Add an imaginary user to the list (temporary)
        if message.content.startswith(".aiu") and message.author.id in ADMINS:

            # First, make sure the command is using the right syntax.
            try:
                arguments = message.content.split(".aiu ")[1]
            except Exception as e:
                await message.channel.send("Syntax: \".aiu variable_name;display_name;image_url\"")
                await message.channel.send("Error.\n{}".format(e))
                return
            if len(arguments) <= 2:
                await message.channel.send("Syntax: \".aiu variable_name;display_name;image_url\"")
                return

            # Get the data from the arguments
            variable_name, display_name, pfp_url = arguments.split(";")
            # Try to pull the profile picture
            try:
                r = requests.get(pfp_url)
                file_name = IMAGINARY_USERS_DIR + str(uuid.uuid1())
                with open(file_name, 'wb') as f:
                    f.write(r.content)

            except Exception as e:
                await message.channel.send("Couldn't get pfp\n{}".format(e))
                return

            # Add the new fake user
            imaginary_users[variable_name] = {"name":display_name, "pfp":file_name}
            print("Added an imaginary user \"{}\" by request of {}. Profile pic: {}.".format(variable_name,
                message.author.name, file_name.split("/")[-1]))
            await message.add_reaction("✅")
            return




        # User impersonator
        if message.content.startswith(".impersonate") and message.author.id in ADMINS:

            # This command has loots of risky code. We'll wrap everything
            # in one big try loop, and send an error message if we mess up.
            try:
                # Grab some data about the user and message.
                user_to_impersonate = message.content.split(' ')[1].lower()
                message_channel = message.content.split(' ')[2]
                if message_channel == ".":
                    channel = message.channel
                else:
                    try:
                        channel = await self.fetch_channel(int(message_channel))
                    except Exception as e:
                        if str(e).startswith("invalid literal"):
                            await message.channel.send("Invalid channel ID")
                        else:
                            await message.channel.send(e)
                        return
                message_content = message.content.split(' ', 3)[3]

                # Should we send this as an imaginary user?
                if user_to_impersonate in imaginary_users.keys():
                    await send_as_imaginary(self, imaginary_users[user_to_impersonate], channel, message_content)
                else:
                    print("{} not found in imaginary_users. Defaulting to a real user...".format(user_to_impersonate))
                    user_id = int(user_to_impersonate)
                    await send_as(self, user_id, channel, message_content)
                await message.delete()
                return
            except Exception as e:
                await message.channel.send(e)

        #print("@{} in {}: {}".format(message.author, message.channel, message.content))

        # .send command
        if message.content.startswith('.send') and message.author.id in ADMINS:
            message_channel = message.content.split(' ', 2)[1]
            if message_channel == ".":
                channel = message.channel
            else:
                channel = self.get_channel(int(message_channel))
            message_content = message.content.split(' ', 2)[2]
            await channel.send(message_content)
            if message_channel == ".":
                await message.delete()
            return

        # clear untracked imaginary user profile pictures
        if message.content in [".duiu", ".cuiu"] and message.author.id in ADMINS:
            del_untracked_imaginary_users(IMAGINARY_USERS_DIR)
            await message.add_reaction("✅")

        # .owo command
        if message.content in [".owo", ".uwu", ".uvu"] and message.reference is not None:
            command = message.content[1:]
            reference = message.reference
            if reference == None:
                return
            msg_chan = await self.fetch_channel(reference.channel_id)
            msg = await msg_chan.fetch_message(reference.message_id)
            try:
                await self.fetch_user(msg.author.id)
            except discord.errors.NotFound:
                await msg_chan.send("I can't grab user data from a user who doesn't exist, silly!")
                return
            owoified = await advanced_owoify(self, message.guild, msg.content, command)
            await send_as(self, msg.author.id, msg_chan, owoified)
            try:
                await message.delete()
            except Exception as e:
                print("Tried to delete message for .owo and failed.")
                print(e)

        # ban anyone in Brit's server who says the word 'league'
        if 'league' in message.content.lower() and message.guild.id == 609659675964735498:
            print("Banning user who said 'league' in brit's server...")
            await message.guild.ban(message.author, delete_message_days=0, reason="no league players allowed")

        # ban anyone in pip's server who says the word 'smash'
        if 'smash' in message.content.lower() and message.guild.id == 780895418171129887:
            print("Banning user who said 'smash' in brit's server...")
            await message.guild.ban(message.author, delete_message_days=0, reason="no smash players allowed")



        # kneejerk reactions (by request of Trace)

        # send pic of kobayashi, tohru
        if message.content.lower() == ".kobayashi":
            await message.channel.send("https://media.discordapp.net/attachments/472313197836107780/620030849454309396/gKWpRPH.png")
            await message.delete()
        elif message.content.lower() == ".tohru":
            await message.channel.send("https://images-ext-2.discordapp.net/external/zQ8K51DzEbX22hsrvcWrtH_9H2d88b2yqrdr0xX6mAo/https/media.discordapp.net/attachments/472313197836107780/545475894144270347/8UhfwOc.png")
            await message.delete()
        elif message.content.lower() == ".chun chun":
            await message.channel.send("https://cdn.discordapp.com/attachments/771210072785027102/838270959416115210/illust_64915790_20181125_122856.png")
            await message.delete()
        elif message.content.lower() == ".nanachi":
            await message.channel.send("https://images-ext-1.discordapp.net/external/Ff96ZfulGGLY6evTfwRPfnLpzcrQVcAi-1nVeUrl0Yc/https/imgur.com/NmGh7rb.jpg")
            await message.delete()
        elif message.content.lower() == ".kotobomb":
            await send_as_imaginary(self, imaginary_users["kotori"], message.channel, "https://cdn.discordapp.com/attachments/771210072785027102/838917995124228096/kotobomb.png")
            await message.delete()
        elif message.content.lower() in [".kotoyikes", ".kotosheeesh"]:
            await send_as_imaginary(self, imaginary_users["kotori"], message.channel, "https://cdn.discordapp.com/attachments/771210072785027102/849347597486391296/Screenshot_20210601-130140.jpg")
            await message.delete()

        # 1% chance to say "*ur" for each your or you're in each message
        for word in message.content.split(' '):
            if word.lower() in ["your", "you're"]:
                if random.random() < 0.01:
                    await message.channel.send("*ur")
        # 1/1000 chance to say one of these to any non-command message
        if random.random() < 0.001:
            await message.channel.send(random.choice([
                "how",
                "no cap?",
                "no kizzy?",
                "?",
                "yeah",
                "hey <@{}> suck my ass".format(message.author.id),
                "deez nuts"
                ]))
            return



# first argument should be the location of the bot's token.
# this should be a text file with only the token
token = ''
if len(sys.argv) <= 1:
    try:
        with open('/usr/share/brenbot/token', 'r') as file:
            token = file.readline()
    except Exception as e:
        print(str(e))
        exit(1)
else:
    with open(sys.argv[1], 'r') as file:
        token = file.readline()

# for video re-encoding
refresh_tmp_dir()
conv = Converter()

imaginary_users = load_imaginary_users(IMAGINARY_USERS_DIR + "users.txt")

client = MyClient()
client.run(token)
