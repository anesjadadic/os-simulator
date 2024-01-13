package os;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

import java.util.concurrent.ThreadLocalRandom;

public class OperatingSystem
{
	public static void main(String[] args) throws IOException 
	{	
		int currentTime = 0;
		int nextPTslot = 0;
		int nextPCB = 1;
		int ppid = 0;
		Path processList = Path.of("src/os/processes4.txt");
		PCB[] processTable = new PCB[50];
		
		int CPUtime = 0;
		int CPU = -1;
		Queue<Integer> readyQueue = new LinkedList<Integer>();
		Queue<Integer> terminatedQueue = new LinkedList<Integer>();
		
		int MAX = 200000;
		int processLocationHolder;
		int checkForSpace = 0;
		Queue<Integer> memoryWaitingQueue = new LinkedList<Integer>();
		FreePartitionList partitionList = new FreePartitionList();
		partitionList.insertFreePartition(0, MAX);
		
		String processes = Files.readString(processList);		// This converts the contents of the file into one string.
		String[] parsedProcesses = processes.split("\\s+");		// This splices every value in the 'processes' string, anything separated by a space or newline is spliced.
		int numberOfProcesses = (parsedProcesses.length / 21);	// Each process contains 21 values. So, if we divide parsedProcesses by 21, we get the amount of processes in the file.
		
		int unterminatedProcesses = numberOfProcesses;
		while (unterminatedProcesses > 0)
		{
			for (int n = 0; n < parsedProcesses.length; n = n + 21)	// Since each process has 21 values, we traverse the 'parsedProcesses' array in chunks of 21.
			{
				if (Integer.parseInt(parsedProcesses[n + 2]) == currentTime) // parsedProcesses[n + 2] = arrivalTime.
				{
					PCB processControlBlock = new PCB	// Create the PCB
					(
						parsedProcesses[n], 'n', Integer.parseInt(parsedProcesses[n + 1]), Integer.parseInt(parsedProcesses[n + 2]), Integer.parseInt(parsedProcesses[n + 3]), 
						nextPCB, ppid, Integer.parseInt(parsedProcesses[n + 4]), parsedProcesses[n + 5], parsedProcesses[n + 6], parsedProcesses[n + 7], parsedProcesses[n + 8], 
						parsedProcesses[n + 9], parsedProcesses[n + 10], parsedProcesses[n + 11], parsedProcesses[n + 12], parsedProcesses[n + 13], parsedProcesses[n + 14], 
						parsedProcesses[n + 15], parsedProcesses[n + 16], parsedProcesses[n + 17], parsedProcesses[n + 18], parsedProcesses[n + 19], parsedProcesses[20]
					);
					
					if (processTable[nextPTslot] == null) 	// If there's an available slot in the process table...
					{		
						processTable[nextPTslot] = processControlBlock;	// Insert the PCB into the table.
						processLocationHolder = partitionList.Allocate(processControlBlock.processSize); // Allocate() returns the starting location of the next available
																										 // ...free partition. If none are found, it returns -1.
						if (processLocationHolder != -1) // If a suitable free partition is found...
						{
							System.out.println("\n+++ Allocating PID# " + processControlBlock.ppid + " (" + processControlBlock.processSize + " KB)");
							partitionList.printFreePartitionList();
							processControlBlock.processLocation = processLocationHolder;	// Pass the returned value as the starting location of the process.
							
							System.out.println("\nAdding PID# " + processControlBlock.ppid + " to the ready queue."); 
							processControlBlock.state = 'r';								// Set the process to ready.
							readyQueue.add(processControlBlock.ppid);						// Insert the PCBs PPID into the ready queue.
							System.out.println("Ready Queue: " + readyQueue.toString());
						}
						else // No suitable free partition was found...
						{
							System.out.println("\nAdding PID# " + processControlBlock.ppid + " to the memory waiting queue."); 
							processControlBlock.state = 'w';					// Set the process to waiting.
							memoryWaitingQueue.add(processControlBlock.ppid);	// Send the process to the memory waiting queue.
							System.out.println("Memory Waiting Queue: " + memoryWaitingQueue.toString());
						}
						nextPCB = nextPTslot;
						nextPTslot++;
						ppid++;
						if (nextPTslot > 49) { nextPTslot = 0; }	// Reset nextPTslot to prevent overflow errors.
					}
					else // No open slot in the process table was found...
					{
						int arrivalTime = Integer.parseInt(parsedProcesses[n + 2]); 	// Transform arrivalTime from a string to an integer so it may incremented.
						arrivalTime++;													// Increment that integer.
						parsedProcesses[n + 2] = String.valueOf(arrivalTime);			// Transform it back to a string and put it back in the array.
						
						// System.out.println("Process creation delayed!"); <-- Commented this out as it prevents the full list of events from being printed to console.
					}
				}
				
				if (terminatedQueue.peek() != null)  // If the terminated queue is not empty...
				{
					int dIndex = findIndexOfPPID(processTable, terminatedQueue.peek());  // dIndex is the index of the PCB in the process table whose PPID matches the one in
					processTable[dIndex] = null;										 // ...the termination queue. That matching PCB is removed from the table.
					System.out.println("\nPID# " + terminatedQueue.peek() + " has been terminated.");
					unterminatedProcesses--;											 // Completely terminate the process, it now no longer exists in this program.
					terminatedQueue.clear();											 // Clear the queue.
				}
				
				if (CPU == -1) // If the CPU is available...
				{
					if (readyQueue.peek() != null) // And if the ready queue is not empty...
					{
						CPUtime = 0;
						CPU = readyQueue.poll();							// Make the CPU variable equal to the front value in the ready queue, and remove that value from the queue.
						int rIndex = findIndexOfPPID(processTable, CPU);	// rIndex is the index of the PCB in the process table whose PPID matches the value of the CPU.
						processTable[rIndex].state = 'e';
						System.out.println("\nPID# " + processTable[rIndex].ppid + " is being executed...");
					}
				}
				else // The CPU is not currently available...
				{
					CPUtime++;
					int tIndex = findIndexOfPPID(processTable, CPU); // Same thing as rIndex, but for a different purpose.
					generateNewHexSet(processTable, tIndex);		 // Generate new values for all of the registers in the PCB currently in the CPU.
					if (CPUtime >= processTable[tIndex].burstTime)   // If the process in the CPU is finished...
					{
						System.out.println("\nAdding PID# " + processTable[tIndex].ppid + " to the terminated queue.");
						processTable[tIndex].state = 't';					// Set the process as terminated.
						terminatedQueue.add(processTable[tIndex].ppid);		// Add said process to the terminated queue.
						System.out.println("Terminated Queue: " + terminatedQueue.toString());
						partitionList.Deallocate(processTable[tIndex].processLocation, processTable[tIndex].processSize); // Deallocate the terminated process from the free partition list. 
						System.out.println("\n--- Deallocating PID# " + processTable[tIndex].ppid + " (" + processTable[tIndex].processSize +  " KB)"); 
						partitionList.printFreePartitionList();
						
						if (memoryWaitingQueue.peek() != null) // If the memory waiting queue is not empty...
						{
							int currentPCBid = memoryWaitingQueue.peek();							   // Grab the ID of the next PCB in the queue
							int mIndex = findIndexOfPPID(processTable, currentPCBid);				   // Find the PCB in the process table that matches that ID
							checkForSpace = partitionList.Allocate(processTable[mIndex].processSize);  // Check if there is a free partition suitable for said PCB
							if (checkForSpace != -1) // If there is a free partition suitable...
							{
								System.out.println("\n+++ Allocating PID# " + processTable[mIndex].ppid + " (" + processTable[mIndex].processSize + " KB)");
								partitionList.printFreePartitionList();
								memoryWaitingQueue.poll();			// Take that PCB ID out of the memory waiting queue.
								processTable[mIndex].state = 'r';	// Set that PCB to ready.
								readyQueue.add(currentPCBid);		// Add that PCB to the ready queue.
							}
							checkForSpace = 0; // Reset the placeholder variable.
						}
						CPU = -1; // Mark the CPU as available.											
					}
				}
			}
			currentTime++;
		}
	}
	
	public static int findIndexOfPPID (PCB[] processTable, int ppid) {	
		// Search the process table until the PCB with a matching PPID is found. Return the index of that PCB.
		int index = 0;
		for (int i = 0; i < 50; i++) {
			if (processTable[i] != null && processTable[i].ppid == ppid) {
				index = i;
			}
		}
		return index;
	}
	
	public static String generateNewHexValue() {
		String hexa = Integer.toString(ThreadLocalRandom.current().nextInt(0xFFFFFF), 16);
	    return hexa.toUpperCase();
	}
	
	public static void generateNewHexSet(PCB[] processTable, int index) {
		processTable[index].registerSet.XAR = generateNewHexValue();
		processTable[index].registerSet.XDI = generateNewHexValue();
		processTable[index].registerSet.XDO = generateNewHexValue();
		processTable[index].registerSet.PC = generateNewHexValue();
		processTable[index].registerSet.IR = generateNewHexValue();
		processTable[index].registerSet.EMIT = generateNewHexValue();
		processTable[index].registerSet.RR = generateNewHexValue();
		processTable[index].registerSet.PSW = generateNewHexValue();
		processTable[index].registerSet.R0 = generateNewHexValue();
		processTable[index].registerSet.R1 = generateNewHexValue();
		processTable[index].registerSet.R2 = generateNewHexValue();
		processTable[index].registerSet.R3 = generateNewHexValue();
		processTable[index].registerSet.R4 = generateNewHexValue();
		processTable[index].registerSet.R5 = generateNewHexValue();
		processTable[index].registerSet.R6 = generateNewHexValue();
		processTable[index].registerSet.R7 = generateNewHexValue();
	}
}