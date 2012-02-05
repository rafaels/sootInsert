package user;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.CardException;
import javacard.framework.CardRuntimeException;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;
import jc.common.OPNAMES;

public class UserAccessJCMLApplet extends Applet {

	public static final byte CLA_USERACCESS = (byte) 0x80;

	/** Senha inv�lida. */
	public static final short SW_SENHA_INVALIDA = (short) 0x6301;
	/** Usu�rio n�o autenticado. */
	public static final short SW_AUTENTICACAO_INVALIDA = (short) 0x6302;

	/** Número máximo de tentativas de PIN. */
	// public static final byte MAXIMO_TENTATIVAS_PIN = 3;
	/** Número de caracteres máximo do PIN. */
	// public static final byte TAMANHO_MAXIMO_PIN = 10;

	// private OwnerPIN pin;
	private UserAccessJCML user;

	private UserAccessJCMLApplet(byte[] bArray, short bOffset, byte bLength) {
		super();
		user = new UserAccessJCML();
		// pin = new OwnerPIN(MAXIMO_TENTATIVAS_PIN, TAMANHO_MAXIMO_PIN);
		// pin.update(bArray, bOffset, bLength);
		register();
	}

	/**
	 * Instala o applet no cart�o.
	 * 
	 * @param bArray
	 *            Dados informados para a instala��o.
	 * @param bOffset
	 *            O �ndice onde esses dados iniciam.
	 * @param bLength
	 *            A quantidade de bytes de informa��o, a partir de
	 *            <code>bOffset</code>.
	 */
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		new UserAccessJCMLApplet(bArray, bOffset, bLength);
	}

	/**
	 * Permite a sele��o do applet, para poder ser usado.
	 * 
	 * @return Se o applet pode ser utilizado.
	 */
	/* ensures \result == true || \result== false; */
	public boolean select() {
		/*
		 * if (pin.getTriesRemaining() == 0) { return false; }
		 */
		return true;
	}

	/**
	 * Efetua as opera��es de limpeza no applet, necess�rias quando o applet n�o
	 * for mais usado, temporariamente.
	 */
	public void deselect() {
		// pin.reset();
	}

	/**
	 * Processa e interpreta cada APDU enviada ao applet.
	 * 
	 * @param apdu
	 *            O APDU enviado.
	 */

	/*
	 * @ requires apdu != null && apdu.getBuffer().lenght >= 0;
	 * 
	 * @
	 */

	public void process(APDU apdu) throws ISOException {
		byte[] buffer = apdu.getBuffer();
		byte cla = buffer[ISO7816.OFFSET_CLA];
		byte ins = buffer[ISO7816.OFFSET_INS];

		// Tratamento do comando SELECT APDU FILE
		if (cla == ISO7816.CLA_ISO7816 && ins == ISO7816.INS_SELECT) {
			return;
		}

		// Tratamento da classe do comando
		if (cla != CLA_USERACCESS) {
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
		}

		switch (ins) {
		case OPNAMES.op_addCredits:
			this.addCredits(apdu);
			break;
		case OPNAMES.op_getCredits:
			this.getCredits(apdu);
			break;

		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

	private void addCredits(APDU apdu) {
		/*
		 * if (!pin.isValidated()) {
		 * ISOException.throwIt(SW_AUTENTICACAO_INVALIDA); }
		 */

		byte[] buffer = apdu.getBuffer();
		short valor = Util.getShort(buffer, ISO7816.OFFSET_CDATA);

		user.addCredits(valor);

	}

	private void getCredits(APDU apdu) {
		byte[] buffer = apdu.getBuffer();

		apdu.setOutgoing();

		apdu.setOutgoingLength((byte) 2);

		short indice_inicio = user.getCredits();

		buffer[0] = (byte) (indice_inicio >> 8);
		buffer[1] = (byte) (indice_inicio & 0xFF);

		apdu.sendBytes((short) 0, (short) 2);

	}
}