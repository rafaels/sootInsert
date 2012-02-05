package user;

import javacard.framework.*;
import javacard.framework.*;

public class UserAccessJCML {
	public static final byte MAX_USER_ID_LENGTH = 15;
	public static final byte STUDENT = 0;
	public static final byte PROFESSOR = 1;
	public static final byte MAX_AREAS = 20;
	public static final short MAX_CREDITS = 3000;
	public static final byte STUDENT_MAX_AREAS = 10;
	public static final short STUDENT_MAX_CREDITS = 1000;
	private byte[] userId;
	private byte userType;
	private byte[] authorizedAreas;
	private byte nextArea;
	private short printerCredits;

	/* @ @ */
	/* @ @ */
	/* @ @ */
	/* @ @ */
	/* @ @ */
	/* @ @ */

	public UserAccessJCML() {
		userId = new byte[MAX_USER_ID_LENGTH];
		userType = STUDENT;
		authorizedAreas = new byte[STUDENT_MAX_AREAS];
		printerCredits = 0;
		nextArea = 0;
		checkInv$UserAccessJCML$();
	}

	/*@ econtext (rSite1, withincode ( public  void addCredits( short value))) @*/
	/*@ echannel (ECC1, RequiresException , rSite1) @*/
	public void addCredits(short value) {
		checkInv$UserAccessJCML$();
		checkPre$addCredits$(value);
		internal$addCredits$(value);

		checkPost$addCredits$(value);
		checkInv$UserAccessJCML$();
	}

	private void internal$addCredits$(short value) {
		printerCredits += value;

	}

	private void checkPre$addCredits$(short value) {
		try {
			if (!(value >= 0 && (short) (value + getCredits()) <= MAX_CREDITS && (!(userType == STUDENT) || ((short) (value + getCredits()) <= STUDENT_MAX_CREDITS)))) {
				EChannelExceptions
						.throwIt(EChannelExceptions.SW_REQUIRES_ERROR);
			}

		} catch (EChannelExceptions except) {
			EChannelExceptions.throwIt(EChannelExceptions.SW_REQUIRES_ERROR);
		}
	}

	private void checkPost$addCredits$(short value) {
		try {
			if (!(printerCredits >= value)) {
				EChannelExceptions.throwIt(EChannelExceptions.SW_ENSURES_ERROR);
			}

		} catch (Throwable except) {
			EChannelExceptions.throwIt(EChannelExceptions.SW_ENSURES_ERROR);
		}
	}

	public short getCredits() {

		checkInv$UserAccessJCML$();
		short returnValue = internal$getCredits$();

		checkInv$UserAccessJCML$();
		if (returnValue > (short) 4) {
			return returnValue;
		} else {
			return (short) 0;
		}

	}

	private short internal$getCredits$() {

		return printerCredits;

	}

	private void checkInv$UserAccessJCML$() throws EChannelExceptions {
		try {
			if (!(userId.length <= MAX_USER_ID_LENGTH)) {
				EChannelExceptions
						.throwIt(EChannelExceptions.SW_INVARIANT_ERROR);
			}
			if (!(userType == STUDENT || userType == PROFESSOR)) {
				EChannelExceptions
						.throwIt(EChannelExceptions.SW_INVARIANT_ERROR);
			}
			if (!(authorizedAreas.length <= MAX_AREAS)) {
				EChannelExceptions
						.throwIt(EChannelExceptions.SW_INVARIANT_ERROR);
			}
			if (!(printerCredits >= 0 && printerCredits <= MAX_CREDITS)) {
				EChannelExceptions
						.throwIt(EChannelExceptions.SW_INVARIANT_ERROR);
			}
			if (!(!(userType == STUDENT) || (authorizedAreas.length <= STUDENT_MAX_AREAS))) {
				EChannelExceptions
						.throwIt(EChannelExceptions.SW_INVARIANT_ERROR);
			}
			if (!(!(userType == STUDENT) || (printerCredits <= STUDENT_MAX_CREDITS))) {
				EChannelExceptions
						.throwIt(EChannelExceptions.SW_INVARIANT_ERROR);
			}

		} catch (Exception except) {
			EChannelExceptions.throwIt(EChannelExceptions.SW_INVARIANT_ERROR);
		}
	}

}