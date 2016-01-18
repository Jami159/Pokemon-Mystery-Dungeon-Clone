from pygame import *

pic = image.load("transition.png")

count = 0

for y in range(12):

    for x in range(3):

        if count < 34:
            img = pic.subsurface(256*x + 2*x, 192*y + 2*y, 256, 192)
            img = transform.scale(img, (480, 384))
            image.save(img, "transition"+str(count)+".png")
            count += 1


for i in range(51):

    image.save(img, "transition"+str(count)+".png")
    count += 1
