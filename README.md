# Android Autofill Framework Crash Demonstration
Android project demonstrating Autofill Framework issue which allows to crash any other app accepting autofill.

# Steps to reproduce
* Clone the code here.
* Run my app.
* Go to any other app that has login form. For instance, Twitter App login can demonstrate this issue.
* When autofill item will show up, click on it.
* App might crash here. If not, go back, and do the same thing again. At the 2nd or the 3rd trial the app will crash.

# Note
App itself might also crash, since I have added no conditional checking circumstances, such as that there are no autofill fields at all, and so on. So, no guarantees. But if you will use as suggested above, you should experience the crash where it should never happen.
