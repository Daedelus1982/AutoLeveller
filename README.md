AutoLeveller
=========

AutoLeveller - The Java based software for 'levelling' GCode file for use on a CNC machine

Probes the surface to be etched then uses this information to adjust the Z height using bilenear interpolation

  - Can be used on multiple operating systems
  - Helps reduce air cuts when etching
  - Can equally be used for etching other projects such as a metallic project box

![w/o AutoLeveller](http://www.autoleveller.co.uk/assets/img/img-fig1.jpg "Circuit milled without AutoLeveller")
Circuit milled without AutoLeveller

![with AutoLeveller](http://www.autoleveller.co.uk/assets/img/img-fig2.jpg "Circuit milled with AutoLeveller")
Circuit milled with AutoLeveller

For more information, download the executable, donate, please visit the [AutoLeveller website](http://www.autoleveller.co.uk).

Installation
--------------

The AutoLeveller project requires the vecmath.jar library which is part of the [Java3D](http://www.oracle.com/technetwork/java/javase/tech/index-jsp-138252.html) library

```sh
git clone https://github.com/**username**/AutoLeveller.git
```

#####NOTE: 
Add vecmath.jar to your referenced libraries within your IDE. The path for my PC is... 

C:\Program Files (x86)\Java\Java3D\1.5.2\lib\ext\vecmath.jar


License
-

GPLv2
    