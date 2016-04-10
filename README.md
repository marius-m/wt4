# WT4

App to speed up time tracking and logging work with ease.

The project is still in development, but main features are already done. 

* Simple work tracking on JIRA issues
* Work offline
* Reflect changes on remote
* Create/read/update/delete work
* Issue search (caches open issues from remote)
* Automatic app updates
* Day/week overview
* Archived for all major platforms

## Download

All downloads are in public folder. 

* MacOSX: [https://www.dropbox.com/sh/y1ilqiz15b437bs/AAB2U0fLaQaQTpiqkpjdxFyDa?dl=0](https://www.dropbox.com/sh/y1ilqiz15b437bs/AAB2U0fLaQaQTpiqkpjdxFyDa?dl=0)
* Windows: [https://www.dropbox.com/sh/vweuktq3qcx9e02/AACTx0kr8pOQ-c5wSrrG0nela?dl=0](https://www.dropbox.com/sh/vweuktq3qcx9e02/AACTx0kr8pOQ-c5wSrrG0nela?dl=0)
* Linux (Ubuntu): [https://www.dropbox.com/sh/zojzamei7e1mk8x/AAATAxAskFyXJKfQH0MglkoJa?dl=0](https://www.dropbox.com/sh/zojzamei7e1mk8x/AAATAxAskFyXJKfQH0MglkoJa?dl=0)

App automatically updates whenever there is a new version. 

## Issues

Track issues / feature requests [here](https://bitbucket.org/mmerkevicius/wt4/issues?status=new&status=open)

## Behind the project (Attributions)

This app serves me as a sandbox for trying out various java stuff that I find interesting. As a side effect app was born that is being used for easier time tracking. So feel free copying / contributing / using code for your own pleasure. 

Tricks that were being tackled in this project: 

* [JavaFX](http://docs.oracle.com/javase/8/javase-clienttechnologies.htm)
* [Java8](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html)
* [Google material design](https://design.google.com/icons/) - Icons for easier use and clear purpose! [LICENSE: CC-BY](https://creativecommons.org/licenses/by/4.0/)
* [RXJava](https://github.com/ReactiveX/RxJava) (Very cool programming [paradigm](http://reactivex.io/)) - [LICENSE: Apache 2.0](https://github.com/ReactiveX/RxJava/blob/1.x/LICENSE)
* [Afterburner](https://github.com/AdamBien/afterburner.fx) - MVP pattern for javafx and small DI framework. - [LICENSE: Apache 2.0](http://afterburner.adam-bien.com/)
* [UpdateFX](https://github.com/vinumeris/updatefx) - Automatic updates for the app. - [LICENSE: Apache 2.0](https://github.com/vinumeris/updatefx/blob/master/LICENSE)
* [JFXTras](https://github.com/JFXtras/jfxtras) - cool 'Agenda' view - [LICENSE: new BSD](https://en.wikipedia.org/wiki/BSD_licenses#3-clause_license_.28.22Revised_BSD_License.22.2C_.22New_BSD_License.22.2C_or_.22Modified_BSD_License.22.29)
* [Guava](https://github.com/google/guava) - such an essential library, so little time to learn. - [LICENSE: Apache 2.0](https://github.com/google/guava/blob/master/COPYING)
* [Dagger2](https://github.com/google/dagger) - (didnt survive much, as afterburner killed it) - [LICENSE: Apache 2.0](https://github.com/google/dagger/blob/master/LICENSE.txt)
* Gradle scripting - app [bunding](https://bitbucket.org/shemnon/javafx-gradle) and my own scripting for project export and update

## How to use it

In theory app should be self explanatory, in practice this is not always the case. Below you can find a simple overview for the features.

### Basic

* Press "clock" icon to start the timer
* Press "enter" to log work 
* Edit changes by pressing right mouse button for the entered log
* Synchronize changes on the remote JIRA server by pressing "cloud" button at the bottom
* Toggle your "view" with the button at the bottom
* Search for issues at the "search" bar at the top ([need sync first!](#.settings))
* Bubbles will indicate the sync state
	* Yellow - ready for sync
	* Green - sync success
	* Red - sync fail

![Work window](https://bitbucket.org/mmerkevicius/wt4/raw/master/img/screen_1.png)

### Settings

For the app to work correctly, set up your credentials and personal preferences how it should work.

* Input host/username/password for remote access
	* Hostname ex.: https://jira.ito.lt
	* Username ex.: marius.m@ito.lt
	* Password ex.: (if you can't figure this one out, you should not be using this anyway)
* Press "cloud" to sync
* Enter custom "JQL" for the issue search or use the default suggested one (can be reset with the "restore" button)

![Work window](https://bitbucket.org/mmerkevicius/wt4/raw/master/img/screen_2.png)

### Update

Logs can be updated by pressing the right mouse button on the target log. 

* Every option that will be changed when saving changes
* Only logs that were not sync'ed can be edited (still a drawback of one way sync)
* Header will indicate the log status and error (if there is any)

![Work window](https://bitbucket.org/mmerkevicius/wt4/raw/master/img/screen_3.png)

### Week view / Day view

By pressing little "calendar" at the bottom, you can change view's from day to a whole week view. 

![Work window](https://bitbucket.org/mmerkevicius/wt4/raw/master/img/screen_4.png)

## FAQ

* Q: Why does it weight so much ?
* A: It is bundled with Java8, so for most part its java that weights so much. It is done, so you don't have to download it separately. 
* Q: I have problems synchronizing with the remote server!
* A: Try checking settings window, as most of the stuff that is done in the background are printed out. That might give you an idea when might not be working.

	
 


