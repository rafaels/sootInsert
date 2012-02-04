package util;

import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import soot.Body;
import soot.SootMethod;
import soot.Trap;
import soot.Unit;
import soot.toolkits.graph.TrapUnitGraph;
import soot.toolkits.graph.UnitGraph;
import sootInsert.Main;

import source.Channel;

import util.Util;

public class Util {
	public static void printMethod(Body body) {
		System.out.println(body.getMethod().getSignature());
		UnitGraph graph = new TrapUnitGraph(body);
		for (Iterator<Unit> graphIt = graph.iterator(); graphIt.hasNext();) {
			Unit unit = graphIt.next();
			System.out.println(unit);
		}
		System.out.println();
	}

	public static void printTraps(SootMethod method) {
		UnitGraph graph = new TrapUnitGraph(method.getActiveBody());

		/*for (Iterator<Unit> graphIt = graph.iterator(); graphIt.hasNext();) { //itera nos statements atr�s de throws
			Unit unit = graphIt.next();
			System.out.println(unit);
		}*/

		System.out.println(method.getActiveBody().getTraps().size());
		for (Iterator<Trap> i = method.getActiveBody().getTraps().iterator(); i.hasNext();) {
			Trap trap = i.next();
			System.out.println(trap);
		}
	}


	public static int channelID(Channel channel) {
		return Main.getDadosCanais().listaDeCanaisSerializavel.indexOf(channel);
	}

	public static String getSignatureFromSite(String site) {
		Pattern exp = Pattern.compile("\\s+?\\w+\\s+(\\w+)\\s+(\\w+)\\((,?\\s?(\\w+)\\s+\\w+)?+\\)");
		Matcher matcher = exp.matcher(site);
		StringBuffer signature = new StringBuffer();

		if (matcher.matches()) {
			signature.append(matcher.group(1)); //tipo de retorno
			signature.append(" ");
			signature.append(matcher.group(2)); //nome do metodo
			signature.append("(");
			//                     for (int i = 4; i < matcher.groupCount();i++) {
			if (matcher.group(3) != null) {
				signature.append(matcher.group(4)); //tipo do primeiro parametro
			}
			//                     }
			signature.append(")");
		}

		return signature.toString();
	}

	public static String getSignatureFromSignature(String signature) {
		Pattern exp = Pattern.compile("<.+:\\s(.+)>");
		Matcher matcher = exp.matcher(signature);
		StringBuffer signatureBuffer = new StringBuffer();

		if (matcher.matches()) {
			signatureBuffer.append(matcher.group(1)); //tipo de retorno
		}

		return signatureBuffer.toString();
	}

	public static String eChannelTipoToStatic(String tipo) {
		if (tipo.equals("RequiresException"))
			return "SW_REQUIRES_ERROR";
		if (tipo.equals("EnsuresException"))
			return "SW_ENSURES_ERROR";
		if (tipo.equals("InvariantException"))
			return "SW_INVARIANT_ERROR";
		
		throw new IllegalArgumentException();
	}
}
