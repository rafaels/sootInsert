package useraccess.application;

import smartio.UserAccessCommunication;

public class UserAccessClient {
	// AID do applet instalado no cartão
	static byte[] appletAID = new byte[] { (byte) 0xA2, 0x04, 0x05, 0x06, 0x07,
			0x08, 0x09, 0x10, 0x11, 0x12 };

	public static void main(String[] args) throws Exception {
		try {
			out(":: User Access Client application <init> ::\n");

			out("---->> Creating Communication class...\n");
			// Objeto que ao ser instanciado estabelece comunicacao com o proxy
			// que se comunica com o cartão
			UserAccessCommunication app = new UserAccessCommunication(appletAID);

			out("---->> Calling the card methods...\n");

			/*
			 * out("\t\n ## Setting user type ... " ); app.setType((byte)0);
			 * 
			 * out("\t\n ## Getting user type ... " ); out (""+ app.getType());
			 */

			// o proxy chama as operacoes que foram implementadas no applet

			out("\t\n ## Adding  units of credit ... ");

			app.addCredits((short) 3000);

			out("\t\n ## Current amount of credits ... ");
			short cr = app.getCredits();
			out("" + cr);

		} finally {
			out("");
			out("\n-> Execution finished <-");
		}
	}

	private static void out(String str) {
		System.out.println(str);
		System.out.flush();
	}
}
