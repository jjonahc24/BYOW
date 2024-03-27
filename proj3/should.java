UI Behavior 
After the world has been generated, the user must be in control of some sort of avatar that is displayed in the world. The user must be able to move up, left, down, and right using the W, A, S, and D keys, respectively. These keys may also do additional things, e.g. pushing objects. You may include additional keys in your engine. The avatar should not move when attempting to move into a wall and the program should not error.

The system must behave pseudo-randomly. That is, given a certain seed, the same set of key presses must yield the exact same results!

In addition to movement keys, if the user enters :Q (note the colon), the program should quit and save. The description of the saving (and loading) function is described in the next section. This command must immediately quit and save, and should require no further key-presses to complete, e.g. do not ask them if they are sure before quitting. We will call this single action of quitting and saving at the same time “quit/saving”. This command is not case-sensitive, so :q should work as well. Additionally, : followed by any other letter should not do anything.

This project uses StdDraw to handle user input. This results in a couple of important limitations:

StdDraw does not support key combinations. When we say :Q, we mean : followed by Q.
It can only register key presses that result in a char. This means any unicode character will be fine but keys such as the arrow keys and escape will not work.
On some computers, it may not support holding down of keys without some significant modifications; i.e. you can’t hold down the e key and keep moving east. If you can figure out how to support holding down of keys in a way that is compatible with getWorldFromInput, you’re welcome to do so.
Because of the requirement that your system must handle String input (via getWorldFromInput), your engine cannot make use of real time, i.e. your system cannot have any mechanic which depends on a certain amount of time passing in real life, since that would not be captured in an input string and would not lead to deterministic behavior when using that string vs. providing input with the keyboard. Keeping track of the number of turns that have elapsed is a perfectly reasonable mechanic, and might be an interesting thing to include in your world, e.g. maybe the world grows steadily darker with each step. You’re welcome to include other key presses like allowing the user to press space bar in order to wait one turn.

Saving and Loading 
Sometimes, you’ll be exploring your world, and you suddenly notice that it’s time to go to watch a CS 61B lecture. For times like these, being able to save your progress and load it later, is very handy. Your system must have the ability to save the state of the world while exploring, as well as to subsequently load the world into the exact state it was in when last saved.

Within a running Java program, we use variables to store and load values. Keep in mind that when your program ends, all the variables will go out of scope. Thus, you will need to persist the state of your program on some files that your program should create.

When the user restarts core.Main and presses L, the world should be in exactly the same state as it was before the project was terminated. This state includes the state of the random number generator! More on this in the next section. In the case that a user attempts to load but there is no previous save, your system should simply quit and the UI interface should close with no errors produced.

In the base requirements, the command :Q should save and completely terminate the program. This means an input string that contains :Q should not have any more characters after it and loading a world would require the program to be run again with an input string starting with L.