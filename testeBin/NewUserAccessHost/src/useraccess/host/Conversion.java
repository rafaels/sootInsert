/*****************************
 * class Conversion.java
 * generated on 12/08/2011 21:28:18
 *****************************/

package useraccess.host; 

public class Conversion{

	public static byte[] shortToByteArray(short s){

		byte[] ba = new byte[2];
		ba[0] = (byte)((s >> 8) & 0xFF);
		ba[1] = (byte)(s & 0xFF);

		return ba;
	}

	public static short byteArrayToShort(byte[] ba){

		short s = (short)(((ba[0] & 0xFF) << 8) + (ba[1] & 0xFF));
		return s;

	}

}