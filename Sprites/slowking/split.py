from pygame import *
init()
BASE = input("Enter pic name")
pic = image.load(BASE+".png")
pic.set_colorkey(pic.get_at((0,0)))
pic = pic.convert(32,SRCALPHA)

pokemon = input("Pokemon: ")
action = input("Action: ")
direction = input("Direction ")

double = input()

wid,hi = pic.get_size()
back = Surface((wid+2,hi+2),SRCALPHA)
back.blit(pic,(1,1))

screen = display.set_mode((wid+2,hi+2))
screen.blit(pic,(1,1))
display.flip()

def lineHasPixel(pic,y):
    for x in range(wid):
        if back.get_at((x,y))[3]>0:
            return True
    return False

def findPixelLine(pic,y):
    while y<hi and not lineHasPixel(pic,y):
        y+=1
    return y

def findOpenLine(pic,y):
    while y<hi and lineHasPixel(pic,y):
        y+=1
    return y

def colHasPixel(pic,top,bott,x):
    for y in range(top,bott+1):
        if pic.get_at((x,y))[3]>0:
            return True
    return False

def findPixelCol(pic,top,bott,x):
    while x<wid and not colHasPixel(pic,top,bott,x):
        x+=1
    return x

def findOpenCol(pic,top,bott,x):
    while x<wid and colHasPixel(pic,top,bott,x):
        x+=1
    return x

cnt= 1 
num = 0
bott = 0
if double == '1':
    stuff = int(input("Enter number of pics"))
while bott<hi:
    top = findPixelLine(pic,bott)
    if top == hi:break
    bott = findOpenLine(pic,top)
    right = 0
    while right<wid:
        left = findPixelCol(pic,top,bott,right)
        
        if left == wid:break
        right = findOpenCol(pic,top,bott,left)
        image.save(back.subsurface((left,top,right-left+1,bott-top+1)),pokemon+"_"+direction+"_"+action+"_"+str(num)+".png")
        #use str(cnt) if want chronological numbers
        
        if double == '1':
            image.save(back.subsurface((left,top,right-left+1,bott-top+1)),BASE+str(2*stuff-1-num)+".png")
            
        draw.rect(screen,(255,0,0), (left,top,right-left+1,bott-top+1),1)
        num += 1
        display.flip()

quit()
