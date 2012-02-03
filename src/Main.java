import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import soot.PackManager;
import soot.Transform;
import source.Channel;
import source.EChannelsEhandlers;

public class Main {
	private static HashMap<Integer, Channel> canais = new HashMap<Integer, Channel>();
	private static EChannelsEhandlers dadosDesS;
	
	static { //escrevendo em dadosDesS o objeto guardado em dados.out com as informações dos canais, contextos e tratadores
		try {
			FileInputStream fIn = new FileInputStream("dados.out");
			ObjectInputStream in = new ObjectInputStream(fIn);
			dadosDesS = (EChannelsEhandlers)in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static EChannelsEhandlers getDadosCanais() {
		return dadosDesS;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		PackManager.v().getPack("jtp").add(new Transform("jtp.appletContextsTransformer", AppletContextsTransformer.v()));
		soot.Main.main(args);
	}
}
