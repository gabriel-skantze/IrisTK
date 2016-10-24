### A Dialog System Framework

IrisTK is a Java-based framework for developing multi-modal interactive systems. IrisTK provides:

* A framework for building event-based, modular interactive systems.
* A statechart-based XML formalism for defining the dialog flow.
* Generic interfaces for input and output components across different modalities (speech recognition, speech synthesis, vision, animation, etc)
* Input and output components for 
	* Windows ASR & TTS
	* Microsoft Kinect
	* Google ASR
	* Nuance Recognizer 9 and Cloud-based ASR 
	* Facial animation
* A 3D situation modelling framework which makes it possible to model situated interaction, including multiple users talking to the system and objects being discussed. 
* Support for distributed systems (over processes and/or machines)
* Logging of events and audio 

### A Social Robotics Platform

<img src="img/furhat.png" width="200" style="float:right"/>

IrisTK is especially designed to be a powerful framework for social robotics applications. IrisTK is used as a software platform for the [Furhat robot head](http://www.furhatrobotics.com) developed by [Furhat Robotics](http://www.furhatrobotics.com). You can start using the animated agent that comes with IrisTK, and then easily connect Furhat to your system by changing one line of code. Then you can also upload your application to Furhat as a "skill".

<img src="img/sitint_gui.jpg" width="400"/>

### Usage

IrisTK has been used in many experiments and demonstrations at the [Department of Speech Music and Hearing, KTH](http://www.speech.kth.se). 
Here is an example where it is used. The video is best viewed in 1080p and fullscreen.

<iframe class="video" width="560" height="315" src="https://www.youtube.com/embed/5fhjuGu3d0I?rel=0&vq=hd1080" frameborder="0" allowfullscreen></iframe>

IrisTK is also excellent for education. You can see some examples of what students have created with it [here](examples.html). 

### Licensing

IrisTK is released as open source under the terms of the [GNU Public License v3.0](http://www.gnu.org/licenses/gpl.html). 

If your project exceeds the limitations of GPL (for example if you want to distribute your code without revealing the source), other types of licenses can be provided by [Furhat Robotics](http://www.furhatrobotics.com). 

### Citing

If you use IrisTK in your research, please cite the following paper:

Skantze, G., & Al Moubayed, S. (2012). [IrisTK: a statechart-based toolkit for multi-party face-to-face interaction](http://www.speech.kth.se/prod/publications/files/3772.pdf). In _Proceedings of ICMI_. Santa Monica, CA.

### Mailing list

If you want to receive information about new releases of IrisTK, or if you have any questions or requests for new features, please use the [iristk-users](http://groups.google.com/group/iristk-users) mailing list. 

### Development and contact

IrisTK is mainly developed by [Gabriel Skantze](http://www.speech.kth.se/~gabriel).   