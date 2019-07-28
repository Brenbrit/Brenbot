from utils import update

def process(message):
	out = [[],[]]
	print("this is where the processing will happen")

	if message.content.lower().startswith("update"):
        out[0].append(message.channel, "ok")
        update()

	return [[],[]]
