import platform
from sys import exit

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
        exit(0)
