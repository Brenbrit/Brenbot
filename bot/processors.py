from utils import update

def process(message):
	print("this is where the processing will happen")

	if message.content.lower().startswith("update"):
        await client.send_message(message.channel, content = "ok")
        update()

	return [[],[]]
