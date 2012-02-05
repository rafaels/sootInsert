package user;

import javacard.framework.CardRuntimeException;


public class EChannelExceptions extends CardRuntimeException {
	public static final short SW_INVARIANT_ERROR = (short) 0x6377 ;
	public static final short SW_ENSURES_ERROR = (short) 0x6388 ;
	public static final short SW_REQUIRES_ERROR = (short) 0x6399 ;
	private short reason;
	private static EChannelExceptions systemInstance;
	
	public EChannelExceptions(short sw) {
		super(sw);
	}
	
	public void setReason(short reason) {
		this.reason = reason;
	}
	
	public short getReason(){
		return this.reason;
	}
	
	public static void throwIt(short sw) {
		if (systemInstance == null) {
			systemInstance = new EChannelExceptions(sw);
		}
		systemInstance.setReason(sw);
		throw systemInstance;
	}
	
	public static EChannelExceptions getInstance() {
		if (systemInstance == null) {
			systemInstance = new EChannelExceptions((short) -1);
		}
		return systemInstance;
	}
}
