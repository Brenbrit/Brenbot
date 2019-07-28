from utils import update

def process(message):
    out = [[],[]]
    print(message.type)

    if message.content.lower().startswith("update"):
        out[0].append([message.channel, "ok"])
        update()

    return [[],[]]
