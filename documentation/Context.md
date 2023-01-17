Back in the days of Vyxal 2.4.x, there was a code clean-up planned. However, it resulted in the horrible mess that was the 2.5.x releases. One of the reasons the original code clean-up failed is that the one-file interpreter relied too heavily on global variable; in order to access values such as flag settings, input levels and context values (the ones used with n), variables had to be scoped as global within functions. Ignoring the fact that global variables are generally a bad programming practice, this was an issue because you can't access global variables from one file inside another file.

The best solution to this was to have a special Context class that contains all the variables that were previously global. And it's the same solution that's used in Vyxal 3.x releases.

At the start of the interpreter, an instance of Context called ctx (think ctx = Context()) will be created. This will need to be passed between element functions, meaning they all need a ctx parameter.
