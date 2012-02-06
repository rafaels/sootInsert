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
		Pattern exp = Pattern.compile("(?:\\s+)?\\w+\\s+(\\w+)\\s+([\\w\\.]+)\\.(\\w+)(.+)");
		Matcher matcher = exp.matcher(site);
		StringBuffer signature = new StringBuffer();

		if (matcher.matches()) {
			String retorno    = matcher.group(1);
			String classe     = matcher.group(2);
			String metodo     = matcher.group(3);
			String parametros = matcher.group(4);
			parametros = parametros.replace(" ", "");

			signature.append("<" + classe + ": ");
			signature.append(retorno + " ");
			signature.append(metodo);
			signature.append(parametros + ">");
		}

		return signature.toString();
	}

	public static String getSignatureFromSignature(String signature) {
		return signature;
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
