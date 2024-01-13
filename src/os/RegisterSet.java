package os;

class RegisterSet {
	String XAR;
	String XDI;
	String XDO;
	String PC;
	String IR;
	String EMIT;
	String RR;
	String PSW;
	String R0;
	String R1;
	String R2;
	String R3;
	String R4;
	String R5;
	String R6;
	String R7;
	
	public RegisterSet 
		(
			String XAR, String XDI, String XDO, String PC, String IR, String EMIT, String RR, String PSW, 
			String R0, String R1, String R2, String R3, String R4, String R5, String R6, String R7
		) 
	{
		this.XAR = XAR;
		this.XDI = XDI;
		this.XDO = XDO;
		this.PC = PC;
		this.IR = IR;
		this.EMIT = EMIT;
		this.RR = RR;
		this.PSW = PSW;
		this.R0 = R0;
		this.R1 = R1;
		this.R2 = R2;
		this.R3 = R3;
		this.R4 = R4;
		this.R5 = R5;
		this.R6 = R6;
		this.R7 = R7;
	}
}