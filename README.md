# WT4

Work tracker for JIRA

App is designed for sole purpose - help you track/log worked issues

* Work offline
* Synchronize whenever comfortable
* Easy issue search
* Day/week overview
* Works on evey major platform

## Download

App is bundled with and without java inside. 
If you dont know what i'm talking about, download the bigger package to avoid various issues. 
If you're sure you have java, you could try downloading a smaller app package. 

* MacOSX: pending
* Windows: pending
* Linux (Ubuntu): pending

App will automatically update to newest version

## Issues

Track issues / feature requests [here](https://github.com/marius-m/wt4/issues)

## Attributions

This app serves me as a sandbox for trying out various java stuff that I find interesting. 
As a side effect app was born that is being used for easier time tracking. 
Feel free copying / contributing / using code for your own pleasure. 

Tricks that were being tackled in this project: 

* [Kotlin](https://kotlinlang.org/) - Most of the app is converted to kotlin, once got used to it, never want to look back. 
* [RXJava](https://github.com/ReactiveX/RxJava) Very cool programming [paradigm](http://reactivex.io/)
* [Dagger2](https://github.com/google/dagger) - Essential for making object graph. Loved it every bit.
* [Getdown](https://github.com/threerings/getdown) - Automatic updater for java. The one that really bahaved as intended, love it so much. 
* [JavaFX-Gradle-Plugin](https://github.com/FibreFoX/javafx-gradle-plugin) - A bit more advanced version of java bundling for platforms.
* [JavaFX](http://docs.oracle.com/javase/8/javase-clienttechnologies.htm) - Great components and quite easy to assemble with after burner.
* [Afterburner](https://github.com/AdamBien/afterburner.fx) - When got into it, cant imagine JavaFx without it.
* [Google material design](https://design.google.com/icons/) - Icons for easier use and clear purpose!
* [JFXTras](https://github.com/JFXtras/jfxtras) - cool 'Week' overview. 
* [Java8](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html)

## How to use it

Overview of the main app features

### Main

* Press "Clokc" to start working. Whenever finished press enter to log time. 
* Search on active issues at the top ([will need to setup first!](#.settings))
* Bubbles will indicate the sync with remote state
	* Yellow - log stored locally
	* Green - log synchronized with remote
	* Red - log had problems synchronizing (double click for details)

![Work window](img/screen_1.png)

### Settings

* Enter credentials and personal preferences
* You can use custom "JQL" for the issue search

![Work window](img/screen_2.png)

### Update

Logs can be updated by pressing the right mouse button on the target log

![Work window](img/screen_3.png)

### Week view / Day view

![Work window](img/screen_4.png)

## FAQ

* Q: Why does it weight so much ?
* A: It is bundled with Java8, so for most part its java that weights so much. It is done, so you don't have to download it separately. 
* Q: I have problems synchronizing with the remote server!
* A: Try checking settings window, as most of the stuff that is done in the background are printed out. That might give you an idea when might not be working.

## Changelog

Only recently started tracking versions.

### 0.9.9.4

* Refactored all synchronization with remote
* Rewritten most of the core functionality 
  - GA tracking
  - Auto syncrhonization
  - Issue search module
  - Renewed networking
  - Changed to easier bundling
  - Rewritten all bundle scripts
  - Changed app upgrade functionality
	
 


