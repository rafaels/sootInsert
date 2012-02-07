package useraccess.application;

import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class UserTest {
	
	public static String formatBuffer(byte[] buffer,int length)  
	  {  
	         StringBuilder strBuff = new StringBuilder("");  
	         for (int i = 0; i < length; i++) {  
	             strBuff.append(String.format("%02X", buffer[i]));  
	         }  
	         return strBuff.toString();  
	  }  
	
	public static void main(String[] args) {
      //"Fabrica" de Terminais PC/SC
      TerminalFactory factory;
      //Lista de Leitores PC/SC
      List terminals;
      //Terminal PC/SC
      CardTerminal terminal;
      //Smart Card
      Card card;
      //Smart Card ATR
      ATR cardATR;
      //Canal de Comunicação com o Smart Card
      CardChannel cardChannel;
      //APDU de Comando
      CommandAPDU commandAPDU;
      //APDU de Resposta
      ResponseAPDU responseAPDU;
      //Buffer de Auxilio
      byte[] buffer;
    
      
    

      try {
          //Adquire Fabrica de Leitores
          factory = TerminalFactory.getDefault();

          //Adquire Lista de Leitores PC/SC no Sistema
          terminals = factory.terminals().list();
          System.out.println("Lista : " + terminals);

          //Adquire Primeiro Terminal da Lista
          terminal = (CardTerminal)terminals.get(0);
          System.out.println("Terminal Selecionado: "
                  + terminal.getName());

          //Estabelece Conexão com o Cartão na Leitora
          card = terminal.connect("*");
          System.out.println("card: " + card);

          //Adquire ATR do Cartão
          cardATR = card.getATR();
          /*buffer = cardATR.getBytes();
          System.out.println("ATR : "
                  + formatBuffer(buffer, buffer.length));*/

          //Adquire Canal de Comunicação
          cardChannel = card.getBasicChannel();

          //AID 
          buffer = new byte[]{(byte) 0xA2,0x04,0x05,0x06,0x07,0x08,0x09,0x10,0x11,0x12};

          //Monta APDU de Envio
          commandAPDU = new CommandAPDU(
                  0x00,       //CLA
                  0xA4,       //INS - SELECT
                  0x04,       //P1
                  0x00,       //P2
                  buffer);    //AID
         
          
          //Imprime Comando
          System.out.println("\n[SELECT COMMAND]");
          System.out.println("=> " + formatBuffer(  
          	                    commandAPDU.getBytes(),  
                                  commandAPDU.getBytes().length));
          

          //Trasnmite e Recebe
          responseAPDU = cardChannel.transmit(commandAPDU);

          //Verifica Resposta
          if (responseAPDU.getSW() != 0x9000) {
              throw new Exception("Falha ao Selecionar : "
                      + String.format("0x%04X",
                        responseAPDU.getSW()));
          }

         
          //Imprime Resposta
         System.out.println("<= " + formatBuffer(  
          		                  responseAPDU.getBytes(),  
          	                      responseAPDU.getBytes().length));   

          //Testando instrucoes
          buffer =
              new byte[]{0x00}; 

          //Monta APDU de Envio
          commandAPDU = new CommandAPDU(
                  0x80, //CLA
                  0x03, //INS addCredits
                  0x00, //P1
                  0x00, //P2
                  buffer);	//Teste
          
        

          //Imprime Comando
          System.out.println("\n[TESTE ADDCREDITS]");
          System.out.println("=> " + formatBuffer(  
          		                    commandAPDU.getBytes(),  
          		                    commandAPDU.getBytes().length));  

          //Trasnmite e Recebe
          responseAPDU = cardChannel.transmit(commandAPDU);

          //Verifica Resposta
          if (responseAPDU.getSW() != 0x9000) {
              throw new Exception("Falha ao Selecionar : "
                      + String.format("0x%04X",
                        responseAPDU.getSW()));
          }
          
          

          //Imprime Resposta
          System.out.println("<= " + formatBuffer(  
	                  responseAPDU.getBytes(),  
                  responseAPDU.getBytes().length));  
          
          //Testando getCredits
        
          //Monta APDU de Envio
          commandAPDU = new CommandAPDU(
                  0x80, //CLA
                  0x04, //INS getCredits
                  0x00, //P1
                  0x00 //P2
                  );	//Teste
          
        

          //Imprime Comando
          System.out.println("\n[TESTE GETCREDITS]");
          System.out.println("=> " + formatBuffer(  
          		                    commandAPDU.getBytes(),  
          		                    commandAPDU.getBytes().length));  

          //Trasnmite e Recebe
          responseAPDU = cardChannel.transmit(commandAPDU);

          //Verifica Resposta
          if (responseAPDU.getSW() != 0x9000) {
              throw new Exception("Falha ao Selecionar : "
                      + String.format("0x%04X",
                        responseAPDU.getSW()));
          }
          
          

          //Imprime Resposta
          System.out.println("<= " + formatBuffer(  
	                  responseAPDU.getData(),  
                  responseAPDU.getData().length));  

      } catch (Exception e) {
          e.printStackTrace(System.out);
      }
  }


}
