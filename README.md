In a paint industry we have a total of 100 orders. The colors, regardless of their id, must remain in the machine for about 6 sec per kilo of color produced. The dark field in each order indicates if that order corresponds to a dark color type (if dark = True, then the color is dark). The factory has five machines of the same characteristics. Only one order can be produced at any time and on each machine. Obviously, in each machine will be produced sequentially (one after the other) the various orders. Between the production of two orders (let 𝑖 and 𝑗) there must be a time interval in which the machine is cleaned for the next color. The necessary cleaning times (in minutes) for the transition from color to color are created by the transitionTime method, which  creates a (asymmetric) Table 100 x 100.

In addition to the above cleaning times, any transition from dark to light color requires an additional time equal to 15 minutes to replace some parts of the machine.

The goal is for all orders to be produced as soon as possible, so that all five machines are free as soon as possible to start production of the next batch of orders.

A. Write the code needed to represent the problem as well as a solution (classes, birth input, etc.). (2)
B. Write a function that will receive a solution and return the value of the objective function for that solution. (1)
C. Write an advantageous algorithm for scheduling order production on the five machines. (2)
D. Write an algorithm that applies the basic scheme of local research which will receive the initial solution that emerged from C and by applying moves will repeatedly improve the solution. Choose the type of traffic and describe how it works. In how many repetitions is your algorithm trapped in a local minimum? What is the value of the objective function of the local minimum? (3)
E. Write a VND algorithm that will take the initial solution that resulted from C and applying local search moves will repeatedly improve the solution. The VND algorithm must apply three types of motion. What is the value of the objective function of the local minimum for all three types of motion you applied? (2)
