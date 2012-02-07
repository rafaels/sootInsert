package smartio;

import javacard.framework.CardRuntimeException;
import javacard.framework.ISO7816;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import jc.common.OPNAMES;
import useraccess.host.Conversion;

public class UserAccessProxySide {
	final static byte APPLET_CLA = (byte) 0x80; // classe apdu
	private static final int OK = 0x9000; // mensagem enviada a aplicacao
	// cliente quando uma operacao e
	// realizada com sucesso
	private static final int INDEX_OUT_OF_RANGE = 0x6A83;

	private CommandAPDU cmdAPDU;// representa um comando apdu
	private byte[] appletAID;
	private Card card = null; // representa o smart card

	public UserAccessProxySide() {
		// instancia o objeto responsavel por estabelecer a conexao com o cartao
		SmartCardIOConnector conn = new SmartCardIOConnector();
		conn.cardInit();
		this.card = (Card) conn.getCardReference();
	}

	/*
	 * protected void initialize(CardServiceScheduler scheduler, SmartCard card,
	 * boolean blocking) throws CardServiceException {
	 * super.initialize(scheduler, card, blocking); try { allocateCardChannel();
	 * } finally { releaseCardChannel(); } }
	 */

	protected void setCardAID(byte[] aid) {
		appletAID = aid;
	}

	protected void executeOperation(byte INS) throws CardException {
		CardChannel cc = null;
		try {
			card.beginExclusive();
			cc = card.getBasicChannel();

			cmdAPDU = new CommandAPDU(APPLET_CLA, INS, 0x00, 0x00);

			// Send command APDU and check the response.
			ResponseAPDU response = cc.transmit(cmdAPDU);

			switch (response.getSW() & 0xFFFF) {
			case OK:
				return;
			case INDEX_OUT_OF_RANGE:
				throw new CardException("Index out of range");
			default:
				throw new CardException(
						"sw = 0x"
								+ Integer
										.toHexString((short) (response.getSW() & 0xFFFF)));
			}
		} finally {
			cc.close();
			card.endExclusive();
		}
	}

	protected byte[] getField(byte INS) throws CardException {
		CardChannel cc = null;
		try {
			card.beginExclusive();
			cc = card.getBasicChannel();

			// Monta comando apdu a ser enviado ao applet no cartao
			cmdAPDU = new CommandAPDU(APPLET_CLA, INS, 0x00, 0x00);

			// Send command APDU and check the response.
			ResponseAPDU response = cc.transmit(cmdAPDU);
			/*
			 * Onde inserir código dos tratadores? aki?
			 * Proposta:	A = B()
			 * 				C = B()
			 * 				realiza as ações de inserção
			 * 				A = C
			 */

			switch (response.getSW() & 0xFFFF) {
			case OK:
				return response.getData();
			case INDEX_OUT_OF_RANGE:
				throw new CardException("Index out of range");
			default:
				throw new CardException("Falha ao Selecionar : "
	                      + String.format("0x%04X",
	                        response.getSW()));
			}
		} finally {
			// cc.close();
			card.endExclusive();
		}
	}

	protected void setField(byte p1, byte p2, byte data[], byte INS)
			throws CardException {

		CardChannel cc = null;
		try {
			card.beginExclusive();
			cc = card.getBasicChannel();

			// Set up the command APDU and send it to the card.
			// CLA, INS, P1, P2, Data, OFFSET_CDATA (5), Le
			cmdAPDU = new CommandAPDU(APPLET_CLA, INS, p1, p2, data,
					ISO7816.OFFSET_CDATA);

			ResponseAPDU response = cc.transmit(cmdAPDU);

			switch (response.getSW() & 0xFFFF) {
			case OK:
				return;
			case INDEX_OUT_OF_RANGE:
				throw new CardException("Index out of range");
			default:
				throw new CardException(
						"Codigo da exceção: " + "SW: 0x"
								+ Integer
										.toHexString((short) (response.getSW() & 0xFFFF)));
			}
		} finally {
			// cc.close();
			card.endExclusive();
		}

	}

	protected byte[] set_getField(byte p1, byte p2, byte data[], byte INS)
			throws CardException {

		CardChannel cc = null;
		try {
			card.beginExclusive();
			cc = card.getBasicChannel();

			// Set up the command APDU and send it to the card.
			// CLA, INS, P1, P2, Data, OFFSET_CDATA (5), length, Le
			cmdAPDU = new CommandAPDU(APPLET_CLA, INS, p1, p2, data,
					ISO7816.OFFSET_CDATA);

			ResponseAPDU response = cc.transmit(cmdAPDU);

			switch (response.getSW() & 0xFFFF) {
			case OK:
				return response.getData();
			case INDEX_OUT_OF_RANGE:
				throw new CardException("Index out of range");
			default:
				return response.getData();
							}
		} finally {
			// cc.close();
			card.endExclusive();
		}

	}

	public void addCredits(short cr) throws CardException {

		byte dataArray[];
		int len = 0;
		dataArray = new byte[len];
		int pos = 0;
		byte p1, p2;
		p1 = p2 = (byte) 0;

		byte[] ba_cr = Conversion.shortToByteArray(cr);
		p1 = ba_cr[0];
		p2 = ba_cr[1];

		// funcao que monta o comando apdu
		// p1, p2 sao os parametros, dataArray um campo de dados e
		// OPNAMES.op_addCredits eh a INS
		setField(p1, p2, ba_cr, OPNAMES.op_addCredits);

	}

	/*
	 * public void removeCredits(short cr)throws CardException {
	 * 
	 * byte dataArray[]; int len = 0; dataArray = new byte[len]; int pos = 0;
	 * byte p1, p2; p1 = p2 = (byte)0;
	 * 
	 * byte[] ba_cr = Conversion.shortToByteArray(cr); p1 = ba_cr[0]; p2 =
	 * ba_cr[1];
	 * 
	 * 
	 * setField(p1, p2, dataArray, OPNAMES.op_removeCredits);
	 * 
	 * }
	 */

	public byte[] getCredits() throws CardException {

		return getField(OPNAMES.op_getCredits);

	}

	/*
	 * public void setType(byte tp) throws CardException {
	 * 
	 * byte dataArray[]; int len = 0; dataArray = new byte[len]; int pos = 0;
	 * byte p1, p2; p1 = p2 = (byte)0;
	 * 
	 * setField(p1, p2, dataArray, OPNAMES.op_setType);
	 * 
	 * }
	 * 
	 * public byte[] getType() throws CardException {
	 * 
	 * return getField(OPNAMES.op_getType);
	 * 
	 * }
	 */

	/*
	 * public void setBalance(short balanceInit) throws CardException,
	 * CardTerminalException {
	 * 
	 * byte dataArray[]; int len = 0; dataArray = new byte[len]; int pos = 0;
	 * byte p1, p2; p1 = p2 = (byte) 0;
	 * 
	 * byte[] ba_balanceInit = Conversion.shortToByteArray(balanceInit); p1 =
	 * ba_balanceInit[0]; p2 = ba_balanceInit[1];
	 * 
	 * setField(p1, p2, dataArray, OPNAMES.op_setBalance);
	 * 
	 * }
	 * 
	 * public void debit(short debitAmount) throws CardException,
	 * CardTerminalException {
	 * 
	 * byte dataArray[]; int len = 0; dataArray = new byte[len]; int pos = 0;
	 * byte p1, p2; p1 = p2 = (byte) 0;
	 * 
	 * byte[] ba_debitAmount = Conversion.shortToByteArray(debitAmount); p1 =
	 * ba_debitAmount[0]; p2 = ba_debitAmount[1];
	 * 
	 * setField(p1, p2, dataArray, OPNAMES.op_debit);
	 * 
	 * }
	 * 
	 * public void credit(short creditAmount) throws CardException { byte
	 * dataArray[]; int len = 0; dataArray = new byte[len]; int pos = 0; byte
	 * p1, p2; p1 = p2 = (byte) 0;
	 * 
	 * byte[] ba_creditAmount = Conversion.shortToByteArray(creditAmount); p1 =
	 * ba_creditAmount[0]; p2 = ba_creditAmount[1];
	 * 
	 * setField(p1, p2, dataArray, OPNAMES.op_credit);
	 * 
	 * }
	 * 
	 * public byte[] getBalance() throws CardException { return
	 * getField(OPNAMES.op_getBalance); }
	 */
}
