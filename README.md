# WT4

Dedicated worklog management for JIRA

* Works offline
* Synchronization with JIRA
* Dynamic log editing
* Works on Windows, Linux and Mac
* Supports Basic authorizatiuon (username / pass) and OAuth2

## Downloads

Builds for `1.5.2`

* [MacOS](http://738649.s.dedikuoti.lt/public/wt4/WT4-1.5.2.dmg)
* [Windows](http://738649.s.dedikuoti.lt/public/wt4/WT4-1.5.2.exe)
* [Deb package](http://738649.s.dedikuoti.lt/public/wt4/wt4-1.5.2.deb)
  
## Downloads iTo
Some of our guys my company [iTo](https://www.ito.lt/) are using this tool as well, so we use specially baked app with OAuth connection. 

Builds for `1.5.2`

- [MacOS](http://738649.s.dedikuoti.lt/public/wt4/ito/WT4-1.5.2.dmg)
- [Windows](http://738649.s.dedikuoti.lt/public/wt4/ito/WT4-1.5.2.exe)
- [Linux](http://738649.s.dedikuoti.lt/public/wt4/ito/wt4-1.5.2.deb)
 
## Issues

Track issues / feature requests [here](https://github.com/marius-m/wt4/issues)

## Attributions

This app serves me as a sandbox for trying out various java stuff that I find interesting. As a side effect app was born that is being used for easier time tracking. 
Feel free copying / contributing / using code for your own pleasure. 

* [CalendarFX](https://github.com/dlemmermann/CalendarFX) - Most amazing calendar that lets display logs and modify them. Very very cool.  
* [JFoenix](http://www.jfoenix.com/) - Material designs for JavaFX! Amazing!
* [Kotlin](https://kotlinlang.org/) - Most of the app is converted to kotlin, once got used to it, never want to look back. 
* [RXJava](https://github.com/ReactiveX/RxJava) Very cool programming [paradigm](http://reactivex.io/)
* [Dagger2](https://github.com/google/dagger) - Essential for making object graph. Loved it every bit.
* [JavaFX](http://docs.oracle.com/javase/8/javase-clienttechnologies.htm) - Great components and quite easy to assemble with after burner.
* [Google material design](https://design.google.com/icons/) - Icons for easier use and clear purpose!
* [jOOQ](https://www.jooq.org/)
* [TornadoFX]() - Lightweight UI framework for JavaFX
* [Java8](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html)

## Changelog

### 1.1.1
- Update jfoenix
- Fix memory leaks

### 1.1.0

Features:
  - Ticket search screen for easier ticket number binding to a log
  - Persistence when editing currently active log
  - Ticket name overview when ticket is bound to a log
  - Rewrite ticket synchronization with the remote server

Tech:
  - Started removal of a custom ORM implementation
  - ..replacing with a lovely implementation of jOOQ!
  - Simplifying synchronization mechanism altogether

### 1.0.9
- Bugfix when JIRA ticket code has numbers (for ex.: T2EE-123)

### 1.0.5 - 1.0.7
- Cool calendar view
- Quick day / week change view
- Profiles (multiple JIRA account support)

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
	
