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

## Download

All downloads are in public folder. 

* MacOSX: https://www.dropbox.com/sh/y1ilqiz15b437bs/AAB2U0fLaQaQTpiqkpjdxFyDa?dl=0
* Windows: https://www.dropbox.com/sh/vweuktq3qcx9e02/AACTx0kr8pOQ-c5wSrrG0nela?dl=0

App automatically updates whenever there is a new version. 

## Issues

All issues/requests can be logged to:

* https://bitbucket.org/mmerkevicius/wt4/issues?status=new&status=open

## Behing the project

This app serves me as a sandbox for trying out various java/javafx stuff. As a side effect app was born that is being used for easier time tracking. So feel free copying / contributing / using code for your own pleasure. 

Tricks that were being tackled in this project: 

* JavaFX
* Java8
* RXJava
* Afterburner - a must have for JavaFX
* Dagger2 - (didnt survive much, as afterburner killed it)
* A bit of Guava - such an essential library, so little time to learn. 
* Gradle scripting - connects app bunding using https://bitbucket.org/shemnon/javafx-gradle and my own scripting for exporting project with auto update.
* UpdateFX - Automatic updates for the app. (https://github.com/vinumeris/updatefx)
* JFXTras - cool 'Agenda' view (http://jfxtras.org/)

## How to use it

In theory app should be self explanatory, in practice this is not always the case. Below you can find a simple overview for the features.

### Basic use

* To start/stop timer press "Play"
* Edit time/date whenever clock is running
* Issue field works as a search (If no issues are found, try refreshing!)
* Logs can update automatically or by manual trigger by pressing 'Update' button.
* Logged work is shown in the table below.
	* Right click lets you update/delete/clone a log
	* Bubbles indicate the state log is in
		* Yellow - locally stored log
		* Green - synced with remote
		* Red - error syncing with remote

![Work window](https://bitbucket.org/mmerkevicius/wt4/raw/master/img/Screenshot_2016-01-09_16.24.29.png)

### Setting up remote

Before you can synchronize with remote, first you have to set up your JIRA account credentials. 

* Input host/username/password for successful remote access
	* Hostname ex.: https://jira.ito.lt
	* Username ex.: marius.m@ito.lt
	* Password ex.: (if you can't figure this one out, you should not be using this anyway)
* To test remote access, press refresh
* Console ouput will display all the sync process info (and errors)

![Work window](https://bitbucket.org/mmerkevicius/wt4/raw/master/img/Screenshot_2016-01-09_16.25.23.png)

## Shortcomings

* Bundling without JRE
* Database migrations

## FAQ

* Q: Why does it weight so much ?
* A: It is bundled with Java8, so for most part its java that weights so much. It is done, so you don't have to download it separately. 
* Q: I have problems synchronizing with the remote server!
* A: Try checking settings window, as most of the stuff that is done in the background are printed out. That might give you an idea when might not be working.

	
 


