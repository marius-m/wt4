# WT4

Work tracker for JIRA

App is designed for sole purpose - help you track/log worked issues

* Work offline
* Synchronize whenever comfortable
* Easy issue search
* Day/week overview
* Works on evey major platform

## Download

App is built for various platform. There are a **small** and **fat** versions. 

If you have java8 you can try out **small version** (15-20mb). If you're not sure what i'm talking about, install **fat version** and don't concern yourself :)

* MacOSX: 
  - [Fat installer (Recommended)](https://www.dropbox.com/s/0982iaqga9lithe/WT4-fat-4.0.dmg?dl=0)
  - [Small installer](https://www.dropbox.com/s/tgcomkvrl28fi0w/WT4-4.0.dmg?dl=0)
* Windows:
  - [Fat installer (Recommended)](https://www.dropbox.com/s/slcnc24cnt9sg3w/WT4-4.0_fat.exe?dl=0)
  - [Fat zip](https://www.dropbox.com/s/555x7yhy0ae18mr/WT4_fat.zip?dl=0)
  - [Small zip](https://www.dropbox.com/s/e2rlz1lt9hopbi8/WT4.zip?dl=0)
  - [Small installer](https://www.dropbox.com/s/ytph3cephh59be3/WT4-4.0.exe?dl=0)
* Linux:
  - [Fat package installer (Recommended)](https://www.dropbox.com/s/9t84ukocehx8pmf/wt4-4.0_fat.deb?dl=0)
  - [Small package installer](https://www.dropbox.com/s/xdh9cmv8s9e210w/wt4-4.0.deb?dl=0)
  
* Linux troubleshooting:
  - Using small build, but no java: Paste in terminal `sudo apt-get install openjdk-8-jre openjfx`
  - Cannot install dpkg. Paste in terminal: `sudo dpkg -i wt4-4.0_fat.deb`. Will install the app to `/opt/WT4`. After it should work properly.
  - To uninstall: Paste in terminal: `sudo dpkg -r wt4`.
 
App will automatically update to newest version

## News

Fun fun fun! As playing around a bit, ive converted the designs for the app to material ones. So as we are in transition to new and neat design, also entering the new 1.0.0 version!. Have fun! 

![New1](img/new_1.png)
![New2](img/new_2.png)
![New3](img/new_3.png)
![New4](img/new_4.png)

## Issues

Track issues / feature requests [here](https://github.com/marius-m/wt4/issues)

## Attributions

This app serves me as a sandbox for trying out various java stuff that I find interesting. 
As a side effect app was born that is being used for easier time tracking. 
Feel free copying / contributing / using code for your own pleasure. 

Tricks that were being tackled in this project: 

* [JFoenix](http://www.jfoenix.com/) - Material designs for JavaFX! Amazing!
* [Kotlin](https://kotlinlang.org/) - Most of the app is converted to kotlin, once got used to it, never want to look back. 
* [RXJava](https://github.com/ReactiveX/RxJava) Very cool programming [paradigm](http://reactivex.io/)
* [Dagger2](https://github.com/google/dagger) - Essential for making object graph. Loved it every bit.
* [Getdown](https://github.com/threerings/getdown) - Automatic updater for java. The one that really bahaved as intended, love it so much. 
* [JavaFX-Gradle-Plugin](https://github.com/FibreFoX/javafx-gradle-plugin) - A bit more advanced version of java bundling for platforms.
* [JavaFX](http://docs.oracle.com/javase/8/javase-clienttechnologies.htm) - Great components and quite easy to assemble with after burner.
* [Afterburner](https://github.com/AdamBien/afterburner.fx) - When got into it, cant imagine JavaFx without it.
* [Google material design](https://design.google.com/icons/) - Icons for easier use and clear purpose!
* [JFXTras](https://github.com/JFXtras/jfxtras) - neat components for easier use
  - Agenda view
* [FXExperiende](http://fxexperience.com/) - Advanced components for the jfx
  - Auto completion text view
* [Java8](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html)

## How to use it (in old window styles)

Overview of the main app features

### Main

* Press "Clock" to start working. Whenever finished press enter to log time. 
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

### 1.0.4
- Authentication settings 
 - Simple checkup test
 - Debug window with full logs
- Quick +/- buttons on time change

### 1.0.3
- Added graphs
 - Fixed bugs with graphs (tho' still not as useful or working 100%)
- Added simplified simple table for viewing logs
 - Smaller info on simple view
- Added status for various logs on week/day view of the calendar
- Add insert custom log option
- Add copy issue to clipboard
 - Snackbar indicates of copied issue

### 1.0.0
- Redesigned window to a more simple version in material design!
- Most of the functionality is already moved, though still missing some parts

### 0.9.9.8-EAP
- Update how enter button scales when resizing the window
- Proper size for search result when looking for an issue
- Smarter query for searching issues
- Open issues from search bar to external browser
- Provide parent info if issue is a subtask

### 0.9.9.7
- Configuration sets (can be found in setting, will update documentation how to use)
 - Add configurations to change instance to more than one JIRA
- Graphs (Graph representation on worked issues. Can be opened by pressing on 'Total worked time button')
- Move enter button to top bar for cleaner interface

### 0.9.9.6
- Change remote auto updates url

### 0.9.9.5
- Improved issue search (should work properly now)
- Added issue search in update

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
	
