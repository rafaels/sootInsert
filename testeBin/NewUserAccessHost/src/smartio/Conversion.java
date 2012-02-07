/*****************************
 * class Conversion.java
 * generated on 04/09/2008 16:46:25
 *****************************/

package smartio; 

public class Conversion{

	public static byte[] shortToByteArray(short s){

		byte[] ba = new byte[2];
		ba[0] = (byte)(s & 0xFF);
		ba[1] = (byte)((s >> 8) & 0xFF);

		return ba;
	}

	public static short byteArrayToShort(byte[] ba){

		short s = (short)(((ba[0] & 0xFF)) + (ba[1] & 0xFF));
		return s;

	}
	
	public static short ByteToShort(byte[] ba){
		short res = 0;
		
        for (int i = 0; i < ba.length; i++) {
        	
            short read = (short) ((short) ba[i] & 0xff);
            res += read;
        }
        return res;
	}

}