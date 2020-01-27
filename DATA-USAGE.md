# Data usage

This document will display how data is being used by the app.

- If you're using basic connection, whenever you enter account details username / password it's stored only on your computer with as encoded text (To be reused whenever you synchronize data with JIRA).
- If you're using OAUTH connection, only authorization tokens are being stored locally to be reused for connection.
- Worklogs / tickets are being stored on your computer as well in file storage as a sqlite database. 
- All the data can be found at ~/.wt4/ (~ means user home directory)
- There are app behaviour logs being stored locally on computer (For easier app support if anything goes awry)
- A minimal amount of data is being tracked and sent to analytics (App launch, open view), data is anonymized and no sensitive data is sent over.
