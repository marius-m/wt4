# Changelog

## 1.5.0 - 1.5.2
- Introduce OAuth authorization support

## 1.4.0 - 1.4.6
- Rework synchronization mechansim to JIRA (introduce two way synchronization)

## 1.3.0
- Full replaced UI to TornadoFX

## 1.2.2
- Minor fixes in Ticket screen

## 1.2.0 - 1.2.1
- Introduce and replace UI framework to TornadoFX

## 1.1.4
- Minor fixes on 'Quick edit' functionality

## 1.1.3
- Add 'Quick edit' of worklogs in calendar

## 1.1.1
- Update jfoenix
- Fix memory leaks

## 1.1.0

Features:
  - Ticket search screen for easier ticket number binding to a log
  - Persistence when editing currently active log
  - Ticket name overview when ticket is bound to a log
  - Rewrite ticket synchronization with the remote server

Tech:
  - Started removal of a custom ORM implementation
  - ..replacing with a lovely implementation of jOOQ!
  - Simplifying synchronization mechanism altogether

## 1.0.9
- Bugfix when JIRA ticket code has numbers (for ex.: T2EE-123)

## 1.0.5 - 1.0.7
- Cool calendar view
- Quick day / week change view
- Profiles (multiple JIRA account support)

## 1.0.4
- Authentication settings 
 - Simple checkup test
 - Debug window with full logs
- Quick +/- buttons on time change

## 1.0.3
- Added graphs
 - Fixed bugs with graphs (tho' still not as useful or working 100%)
- Added simplified simple table for viewing logs
 - Smaller info on simple view
- Added status for various logs on week/day view of the calendar
- Add insert custom log option
- Add copy issue to clipboard
 - Snackbar indicates of copied issue

## 1.0.0
- Redesigned window to a more simple version in material design!
- Most of the functionality is already moved, though still missing some parts

## 0.9.9.8-EAP
- Update how enter button scales when resizing the window
- Proper size for search result when looking for an issue
- Smarter query for searching issues
- Open issues from search bar to external browser
- Provide parent info if issue is a subtask

## 0.9.9.7
- Configuration sets (can be found in setting, will update documentation how to use)
 - Add configurations to change instance to more than one JIRA
- Graphs (Graph representation on worked issues. Can be opened by pressing on 'Total worked time button')
- Move enter button to top bar for cleaner interface

## 0.9.9.6
- Change remote auto updates url

## 0.9.9.5
- Improved issue search (should work properly now)
- Added issue search in update

## 0.9.9.4
* Refactored all synchronization with remote
* Rewritten most of the core functionality 
  - GA tracking
  - Auto syncrhonization
  - Issue search module
  - Renewed networking
  - Changed to easier bundling
  - Rewritten all bundle scripts
  - Changed app upgrade functionality
	
