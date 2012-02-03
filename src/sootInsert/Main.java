package sootInsert;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;

import soot.PackManager;
import soot.Transform;
import source.Channel;
import source.EChannelsEhandlers;
import transformers.AppletContextsTransformer;

public class Main {
	private static HashMap<String, List<Channel>> methodToChannels = new HashMap<String, List<Channel>>();
	private static EChannelsEhandlers dadosDesS;
	
	public static EChannelsEhandlers getDadosCanais() {
		return dadosDesS;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		processEChannels();
		PackManager.v().getPack("jtp").add(new Transform("jtp.appletContextsTransformer", AppletContextsTransformer.v()));
		soot.Main.main(args);
	}
	
	private static void processEChannels() throws IOException, ClassNotFoundException {
		FileInputStream fIn = new FileInputStream("dados.out");
		ObjectInputStream in = new ObjectInputStream(fIn);
		dadosDesS = (EChannelsEhandlers) in.readObject();
	}
}
