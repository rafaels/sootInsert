/*****************************
 * class GenericReturn.java
 * generated on 12/08/2011 21:28:18
 *****************************/

package useraccess.host; 

public class GenericReturn{

	public byte[] byteArray;
	public short[] shortArray;
	public boolean[] booleanArray;
	public byte[][] byteSetArray;
	int byteArrayLen;
	int shortArrayLen;
	int booleanArrayLen;
	public GenericReturn(int byteLen, int shortLen, int booleanLen, int byteSetLenX, int byteSetLenY){
		byteArray = new byte[byteLen];
		byteArrayLen = byteLen;
		shortArray = new short[shortLen];
		shortArrayLen = shortLen;
		booleanArray = new boolean[booleanLen];
		booleanArrayLen = booleanLen;
		byteSetArray = new byte[byteSetLenX][byteSetLenY];
	}

	public void setByteSetArray(byte[] bArray, int x){
		byteSetArray[x] = bArray;
	}

	public int getByteArrayLen(){
		return byteArrayLen;
	}

	public int getShortArrayLen(){
		return shortArrayLen;
	}

	public int getBooleanArrayLen(){
		return booleanArrayLen;
	}

	public void printValues(){
		int i;
		for(i = 0; i < byteArrayLen; i++){
			System.out.println(byteArray[i]);
		}
		for(i = 0; i < shortArrayLen; i++){
			System.out.println(shortArray[i]);
		}
		for(i = 0; i < booleanArrayLen; i++){
			System.out.println(booleanArray[i]);
		}
	}

}