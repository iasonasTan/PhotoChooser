# PhotoChooser App
This app will help you clean up pictures you have on your computer.

## How it works?
First of all you have to give a path to the app.
You can do it by:
1. Pass a folder's path as string in command line
2. Choose with the app's file chooser (no argument in command line).
Then it will search for pictures in the given folder.
If there's pictures, the app will show them one-by-one.
And then you just have to press 'keep' or 'delete' to choose what will happen with each picture.

## Is it safe to use?
The app is safe to use because it does never delete files.
After you press 'Exit', pictures you marked as trash will be moved to a folder called '\_\_trash\_\_'
and pictures you didn't mark as trash will be moved to a folder called '\_\_keep\_\_'.

## How to run it?
The jar is released as `.jar` file.
Jar files are cross-platform, meaning that it doesn't matter if you use Windows, Linux, Mac or any other OS.
BUT **they require JRE installed**.
You can run the following command in terminal/powershell to run a `.jar` file.
```
java -jar /path/to/file.jar
```

If you don't want to install JVM, you can use the *jpackaged version*.
Jpackaged version is basically a JVM packaged into a file with the application.
You can download **exe** or **tar.gz** files to run the app easily. 


## Data collection? 
The app doesn't collect any user data.
You can check it yourself since the app is open-source.