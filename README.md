GWT Model Weight
========
ver-6.0 *licensed Apache License Version 2.0*

![](https://akjava.github.io/website/images/modelweigh6.png)

This App is written with not javascript but GWT/Java.Based on three.js.only works on Chrome Browser

This app contains Characters created by Manuel Bastioni Lab Blender Plugin(http://www.manuelbastioni.com/)

Basic
--------
If you are familiar with Blender Posing/Skinning,don't need this app.
Blender can do Most of things this app can.

This apps main purpose is skinning  Manuel Bastioni Lab Character's cloth/hair/item.

Actually Manuel Bastioni Lab blender plugin has a such function.but I can't understand how to use.

Right now modifying skinning character itself is not supported yet.[Issue #11](https://github.com/akjava/GWTModelWeight/issues/11)

And you can't positioning and rotating and scalling.do these things on Blender

Warning
--------
There are bug [Issue #12](https://github.com/akjava/GWTModelWeight/issues/12)

**Need fix before load**

This app can't load three.js-Json model created by Manuel Bastioni Lab with Blend Shape Animation Options.

So at first you have to fix geometry from `Fix morphtargets Tab`

Main Functions
--------------
For the geometry(cloth/hair/item) you loaded.

**Do auto weight** -  pick closed vertex on character and copy indices/weights data.

**Weight all targeted bon**e - all the skinning for the target single bone.good at hair/hand item.

**replace indices/weights jsondata** - if two geometry are same size,you can copy from `Copy Indices/Weights Tab`.This is the case once you finish skinning with this app,but you modify little positioning , rotating or scalling with blender.

be careful,right now when you added/removed vertex,copy don't work correctly.[Issue #14](https://github.com/akjava/GWTModelWeight/issues/14)

fixing mbl3d character - the character created by Manuel Bastioni Lab Blender Plugin with Blend Shape Key(morphtargets) .once fixed ,you can load normal way.

**select vertex in animation** - you can pause a animation and pick wrongly transformed vertex.(technically picking totally wrong vertex is hard thing)

Target User
-----------
Users have a basic Blender knowledge, but not good at posing/skinning

Target Situation
----------------
Basically it's hard to import three.js data to Blender.
if the data made in three.js,it's hard to modify these on Blender.

No geometry on Blender (it's possible load via Objexporter)

No Bone on Blender (logically it's possible via BVH or something format,but it's not included three.js examples)

No Animation on Blender

Basic use case
--------------
create geometry and Uv in Blender
export as jsonmodel

auto-skinning this app.

load animation and test.

pause animation and modify skinning.

continue until you satisfied.

export skinned geometry.


Tips
----
###how to control
- Left Mouse Button - select vertex
- Middle Mouse Button - rotate  camera
- Right Mouse Button - move camera
- Mouse Wheel - zooming

###Make compatible animation
the animation data should not contain transform data without root.if you contain ,the animation only work correctly the bone you made with.

###Avoid error
be careful management json file don't select wrong place.No error check so far. [Issue #13](https://github.com/akjava/GWTModelWeight/issues/13)

