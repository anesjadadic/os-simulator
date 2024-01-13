## A small scale operating system simulator.

### Includes a list of 200 PCBs, a PCB table, 3 separate queues, memory partition management, and a very simple makeshift CPU. All working in unison!

The program first reads a text file containing the information of 200 PCBs (process control blocks) and converts the information of each PCB into a respective object.
It then places each PCB object into a PCB table (which has 50 slots) ONLY if there is enough space in the table AND if there is an avaiable memory partition for the PCB.
If there is not enough space in the table or not enough memory available, the PCB is put into a 'waiting' state via increasing its arrival time. Otherwise, it is put into a 'ready' state.

The oldest 'ready' PCB in the table will be sent through the CPU, where it will now be put in a 'executing' state, and have its register values changed.
After this, the CPU is now done with the PCB, and it puts the PCB in a 'terminated' state, allowing the terminated queue to wipe the PCB from the PCB table, as well as free any memory the PCB was holding.

This goes on until all 200 PCBs have been converted, executed, and terminated.
