package smartio;

import javax.smartcardio.CardException;

import javacard.framework.CardRuntimeException;
import jc.common.OPNAMES;

public class UserAccessCommunication {
	UserAccessProxySide proxy = null;
	byte[] appletAID = null;

	public UserAccessCommunication(byte[] aid) {
		appletAID = aid;
		// instancia o proxy
		proxy = new UserAccessProxySide();
		// seta o AID do applet a ser usado no cartão no proxy
		proxy.setCardAID(aid);
	}

	/*
	 * public void addArea(byte ar){ try{ // card.beginMutex();
	 * proxy.addArea(ar); } catch (Exception e) { e.printStackTrace(); } finally
	 * { // card.endMutex(); } }
	 * 
	 * public void removeArea(byte ar){ try{ // card.beginMutex();
	 * proxy.removeArea(ar); } catch (Exception e) { e.printStackTrace(); }
	 * finally { card.endMutex(); } }
	 */
	// operacoes que estao implementadas no applet e que a aplicacao cliente
	// solicita
	public void addCredits(short cr) {
		try {
			proxy.addCredits(cr);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public short getCredits() {
		try {
			byte[] result = proxy.getCredits();
			byte[] baShort = new byte[2];

			int posDATA, arrayLen;

			posDATA = 0;

			// baShort[0] = result[posDATA];
			// baShort[1] = result[posDATA++];
			return Conversion.ByteToShort(result);

		} catch (Exception e) {
			e.printStackTrace();
			return (short) -1;
		} finally {
		}
	}

	/*
	 * public void removeCredits(short cr){ try{ proxy.removeCredits(cr); }
	 * catch (Exception e) { e.printStackTrace(); } finally { } }
	 * 
	 * public void setType(byte tp){ try{ proxy.setType(tp); } catch (Exception
	 * e) { e.printStackTrace(); } finally { } }
	 * 
	 * public byte getType(){ try{ byte[] result = proxy.getType(); byte[]
	 * baShort = new byte[2];
	 * 
	 * int posDATA, arrayLen;
	 * 
	 * posDATA = 0;
	 * 
	 * return result[posDATA];
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return (byte) -1; } finally
	 * { } }
	 */

	/*
	 * public void setID(int idu){ try{ // card.beginMutex(); proxy.setID(idu);
	 * } catch (Exception e) { e.printStackTrace(); } finally { //
	 * card.endMutex(); } }
	 * 
	 * public void setType(byte tp){ try{ // card.beginMutex();
	 * proxy.setType(tp); } catch (Exception e) { e.printStackTrace(); } finally
	 * { // card.endMutex(); } }
	 * 
	 * public byte hasAccess(byte ac){
	 * 
	 * try{ // card.beginMutex(); byte result = 0; //proxy.hasAccess(ac);
	 * 
	 * int posDATA, arrayLen;
	 * 
	 * posDATA = 0;
	 * 
	 * return result;
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return 0; } finally { //
	 * card.endMutex(); } }
	 * 
	 * 
	 * 
	 * public byte[] getID(){ try{ // card.beginMutex(); byte[] result =
	 * proxy.getID(); byte[] baShort = new byte[2];
	 * 
	 * int posDATA, arrayLen;
	 * 
	 * posDATA = 0;
	 * 
	 * return result;
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return null; } finally { //
	 * card.endMutex(); } }
	 */

}
