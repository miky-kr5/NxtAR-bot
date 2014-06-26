NxtAR: A generic software architecture for Augmented Reality based mobile robot control.
========================================================================================

Robot control module
--------------------

### Abstract ###

NxtAR is a generic software architecture for the development of Augmented Reality games
and applications centered around mobile robot control. This is a reference implementation
with support for [LEGO Mindstorms NXT][1] mobile robots.

### Module description ###

The robot control module is a control application that receives control commands sent from the 
[NxtAR-cam][2] module and executes concrete control instructions as supported by the [LejOS][3] firmware.

### Module installation and usage. ###

Upload the NxtAR-bot_XXXXXX.nxj file to the LEGO Mindstorms NXT robot and execute it. Then follow the
on-screen instructions. More detailed usage instructions can be found in the [NxtAR Android backend module][4]
documentation. The robot must have the LejOS firmware installed prior to loading the nxj file.

 [1]: http://www.lego.com/en-us/mindstorms/?domainredir=mindstorms.lego.com
 [2]: https://github.com/sagge-miky/NxtAR-cam
 [3]: http://www.lejos.org/nxj.php
 [4]: https://github.com/sagge-miky/NxtAR-android
