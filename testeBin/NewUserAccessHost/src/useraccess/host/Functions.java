/*****************************
 * class Functions.java
 * generated on 12/08/2011 21:28:18
 *****************************/

package useraccess.host; 

public class Functions{

public static byte[] concatByte(byte bArray[], byte b, int pos){
	bArray[pos++] = b;

	return bArray;

}

public static byte[] concatByte(byte bArray[], short s, int pos){
	byte[] ba = Conversion.shortToByteArray(s);

	bArray[pos++] = ba[0];
	bArray[pos++] = ba[1];

	return bArray;

}

public static byte[] concatByte(byte bArray[], byte bArrayNew[], int pos){
	int len = bArrayNew.length;
int index = 0;

for(int i = 0; i < len; i++)
bArray[pos++] = bArrayNew[index++];

	return bArray;

}

	
}