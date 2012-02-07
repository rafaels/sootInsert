package smartio;

import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class SmartCardIOConnector implements ICardConnection {
	private Card card;

	public void cardInit() {
		// show the list of available terminals
		// "Fabrica" de Terminais PC/SC
		// Adquire Fabrica de Leitores
		final TerminalFactory factory = TerminalFactory.getDefault();
		// Lista de Leitores PC/SC
		List<CardTerminal> terminals;
		try {
			// Adquire Lista de Leitores PC/SC no Sistema
			terminals = factory.terminals().list();
			System.out.println("Terminals available: " + terminals);
			// Terminal PC/SC
			CardTerminal terminal = terminals.get(0);
			System.out.println("Terminals : " + terminal);

			// establish a connection with the card
			card = terminal.connect("T=1");// pode enviar e receber mais de um
											// byte de dados pelo canal

			System.out.println("card: " + card);
			// Canal de Comunicação com o Smart Card
			// Adquire Canal de Comunicação
			CardChannel channel = card.getBasicChannel();

			byte[] buffer = new byte[] { (byte) 0xA2, 0x04, 0x05, 0x06, 0x07,
					0x08, 0x09, 0x10, 0x11, 0x12 };

			// Monta APDU de Envio
			CommandAPDU commandAPDU = new CommandAPDU(0x00, // CLA
					0xA4, // INS - SELECT
					0x04, // P1
					0x00, // P2
					buffer); // AID
			ResponseAPDU responseAPDU = channel.transmit(commandAPDU);

		} catch (CardException e) {
			e.printStackTrace();
		}
	}

	public void cardInserted() {
		// TODO Auto-generated method stub

	}

	public void cardRemoved() {
		card = null;
	}

	public Object getCardReference() {
		return card;
	}

}
