This project, sb7-scala, is a port of the example code provided by the authors
of the book, "OpenGL Super Bible (7th Edition)". It even comes with TypeSafe's
Activator, making building and running the examples a piece of cake.

From the command line, launch `bin/activator shell`. Once the console is up
and ready to go, type in `run` and press enter. From there you'll be given a
list of options, like this:

    Multiple main classes detected, select one to run:
    
     [1] com.example.chapter02.AnimClear
     [2] com.example.chapter02.SimpleClear
     [3] com.example.chapter02.SinglePoint
     [4] com.example.chapter02.SingleTri
     [5] com.example.chapter03.MovingTri
    
    Enter number: 

Enter the number of the example you'd like to see and it should be up and
running in pretty quickly.

These examples require you to have Java 8 SDK installed and a computer
capable of supporting OpenGL 4.1 (later examples may require that your computer
support OpenGL 4.5).

Native libraries should automatically unzip themselves into the lib directory;
and for Mac users, the required `-XstartOnFirstThread` Java option should be
applied automatically.

Have fun!
