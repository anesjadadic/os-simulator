package os;

class PCB {
	int ppid;
	char state;
	String name;
	int priority;
	int arrivalTime;
	int burstTime;
	int nextPCB;
	int processLocation;
	int processSize;
	RegisterSet registerSet;
	
	public PCB
		(
			String name, char state, int priority, int arrivalTime, int burstTime, int nextPCB, int ppid, int processSize,
			String XAR, String XDI, String XDO, String PC, String IR, String EMIT, String RR, String PSW,
			String R0, String R1, String R2, String R3, String R4, String R5, String R6, String R7
		) 
	{
		this.ppid = ppid;
		this.state = state;
		this.name = name;
		this.priority = priority;
		this.arrivalTime = arrivalTime;
		this.burstTime = burstTime;
		this.nextPCB = nextPCB;
		this.processSize = processSize;
		this.registerSet = new RegisterSet(XAR, XDI, XDO, PC, IR, EMIT, RR, PSW, R0, R1, R2, R3, R4, R5, R6, R7);
	}
}