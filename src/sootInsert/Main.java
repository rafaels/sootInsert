package sootInsert;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import soot.PackManager;
import soot.Transform;
import source.Channel;
import source.Context;
import source.EChannelsEhandlers;
import transformers.AppletContextsTransformer;
import transformers.HostHandlersTransformer;
import util.Util;

public class Main {
	private static HashMap<String, List<Channel>> methodToChannels = new HashMap<String, List<Channel>>();
	private static EChannelsEhandlers dadosDesS;

	public static EChannelsEhandlers getDadosCanais() {
		return dadosDesS;
	}

	public static List<Channel> getChannelsFromSite(String signature) {
		return methodToChannels.get(signature);
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String hostOrClient = args[0];
		String appDir     = args[1];

		String[] subArgs = Arrays.copyOfRange(args, 2, args.length);

		String[] newArgs = makeSootArgs(subArgs, appDir, hostOrClient);

		processEChannels();

		if (hostOrClient.equals("client")) {
			Transform clientTransform = new Transform("jtp.clientTransform", AppletContextsTransformer.v());
			PackManager.v().getPack("jtp").add(clientTransform);
			soot.Main.main(newArgs);
		} else if (hostOrClient.equals("host")) {
			Transform hostTransform = new Transform("jtp.hostTransform", HostHandlersTransformer.v());
			PackManager.v().getPack("jtp").add(hostTransform);
			soot.Main.main(newArgs);
		}
	}

	private static void processEChannels() throws IOException, ClassNotFoundException {
		FileInputStream fIn = new FileInputStream("dados.out");
		ObjectInputStream in = new ObjectInputStream(fIn);
		dadosDesS = (EChannelsEhandlers) in.readObject();

		for (Channel canal : Main.getDadosCanais().listaDeCanaisSerializavel) {
			for (Context context : canal.listaE) {
				for (String site : context.raisingSites) {
					String signature = Util.getSignatureFromSite(site);
					if (!methodToChannels.containsKey(signature)) {
						methodToChannels.put(signature, new ArrayList<Channel>());
					}

					methodToChannels.get(signature).add(canal);
				}
			}
		}
	}

	private static String[] makeSootArgs(String[] args, String proccessDir, String outputDir) {
		String[] newArgs = new String[args.length + 4];
		newArgs[0] = "-process-dir";
		newArgs[1] = proccessDir;
		for (int i = 0; i < args.length; i++) {
			newArgs[i + 2] = args[i];
		}
		newArgs[2+args.length] = "-output-dir";
		newArgs[3+args.length] = outputDir;
		return newArgs;
	}
}
