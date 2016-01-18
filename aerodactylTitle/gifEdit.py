from pygame import *

for i in range(39):

    s = image.load("tmp-"+str(i)+".gif")
    image.save(transform.scale(s, (200, 152)), "tmp"+str(i)+".gif")

