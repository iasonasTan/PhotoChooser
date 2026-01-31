# PhotoChooser App
This app will help you clean up those 5000 pictures you have on your ~/Pictures folder (or any other folder).

## How it works?
First of all you have to give a path to the app.
You can do it by:
1. Pass a folder's path as string in command line
2. Choose with the app's file chooser.

Then it will search for pictures.
If there's pictures, the app will show them one-by-one
And you just press 'keep' or 'delete' to choose what will happen with this picture.

## Is it safe to use?
The app is kind of safe because it doesn't delete files.
After you press 'Exit', pictures you marked as trash will be moved to a folder called '\_\_trash\_\_' 
and the pictures you didn't mark as trash will be moved to a folder called '\_\_keep\_\_'

## How can I run it?
You can just download the JAR of the latest release but it's also possible to download the source code,
compile it and run it.
The app uses a library called 'Lib.jar' that's developed by me.
You can see the library's source code at [GitHub Repository](https://github.com/iasonasTan/JavaLib/)

## Data collection? 
The app doesn't collect any user data.