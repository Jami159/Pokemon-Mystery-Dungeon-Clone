from pygame import *

name = input("Enter name: ");

for i in range(int(input("Enter length: "))):

    for d in ("up", "down", "left", "right"):

        s = image.load(name+"_"+d+"_attack_"+str(i)+".png")

        image.save(s, name+"_"+d+"_atk_"+str(i)+".png")
