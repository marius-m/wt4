# WT4

App to speed up time tracking and logging work with ease.

The project is still in development, but main features are already completely/partially done. 

* Traking time, logging work
* Create/update/delete work locally
* Basic JIRA synchronization (Though still works only one way)

## Download

All downloads are in public folder. You should use the latest version that is available for best compatibility. 

* https://www.dropbox.com/sh/k7xvmljd2mkmn9b/AAAIJr-YzUm1t9izYlv1a7CZa?dl=0

## Issues

All issues/requests can be logged to:

* https://bitbucket.org/mmerkevicius/wt4/issues?status=new&status=open

## How to use it

In theory app should be self explanatory, in practice this is not always the case. Below you can find a simple overview for the features.

### Basic use

* To start/stop timer press "Play"
* You can edit time whenever clock is running
* Issue field works as a search (Must be synced with remote). 
	* If task cant be found, just put in issue number
	* Issue details can be viewed by pressing 'Forward' (next to search)
	* New issue can be created by pressing 'New'
	* Setting can be opened by hitting 'Settings'
* Logs are updated automatically (Can change behavior in the settings) or can 'force' update manually by pressing on the status bar below
* Logged work is shown in the table below.
	* Right click lets you update/delete/clone a log
	* Bubbles indicate the state log is in
		* Yellow - locally stored log
		* Green - synced with remote
		* Red - error syncing with remote

![Work window](https://bitbucket.org/mmerkevicius/wt4/raw/master/img/Screenshot_2016-01-09_16.24.29.png)

### Setting up remote

* Input host/username/password for successful remote access
	* Hostname ex.: https://jira.ito.lt
	* Username ex.: marius.m@ito.lt
	* Password ex.: (if you can't figure this one out, you should not be using this anyway)
* To test remote access, press refresh
* Console ouput will display all the sync process info (and errors)

![Work window](https://bitbucket.org/mmerkevicius/wt4/raw/master/img/Screenshot_2016-01-09_16.25.23.png)

## Shortcomings

* Still there is an original JIRA API restriction of reflecting the changes on remote when trying to edit/delete a task that has been already uploaded. Need to extend API with additional functionality.
* Bundling without JRE
* Auto update
* Database migrations
* Export project not to be coupled only with JIRA

## FAQ

* Q: Why does it weight so much ?
* A: It is bundled with Java8, so for most part its java that weights so much. It is done, so you don't have to download it separately. 
* Q: I'm getting 403, 401 status code when trying to log in!
* A: Either you're: 
	* Entering wrong user credentials
	* Must sign out/in into JIRA through Web (JIRA resets user to as CAPTCHA, to identify its not a spam bot, which this app is what it is)
	
 


