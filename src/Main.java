import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import soot.Body;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.IdentityStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soot.jimple.internal.InvokeExprBox;
import soot.options.Options;
import soot.toolkits.graph.TrapUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;
import source.Channel;
import source.Context;
import source.EChannelsEhandlers;

public class Main {
	private static HashMap<Integer, Channel> canais = new HashMap<Integer, Channel>();
	private static EChannelsEhandlers dadosDesS;
	
	static {
		//Des-serializando...
		try {
			FileInputStream fIn = new FileInputStream("dados.out");
			ObjectInputStream in = new ObjectInputStream(fIn);


			//dados des-serializados
			dadosDesS = (EChannelsEhandlers)in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length != 1) {
			System.out.println("Usage: eflow <dir>");
			System.exit(1);
		}

		String dir = args[0];

		List<String> process_dirs = new LinkedList<String>();

		process_dirs.add(dir);

		Options.v().set_allow_phantom_refs(true);
		Options.v().set_process_dir(process_dirs);
		Options.v().set_soot_classpath(dir);
		Options.v().set_prepend_classpath(true);

		Scene.v().loadNecessaryClasses();
		retrieveActiveBodies();
//		trataCanais();
//		run();
		PackManager.v().writeOutput();
	}

	private static void retrieveActiveBodies() {
		for (Iterator<SootClass> klassIt = Scene.v().getApplicationClasses().iterator(); klassIt.hasNext();) {
			final SootClass klass = (SootClass) klassIt.next();
			List<SootMethod> methods = klass.getMethods();
			//itera nos metodos
			for (Iterator<SootMethod> methodsIt = methods.iterator(); methodsIt.hasNext(); ) {
				SootMethod sootMethod = (SootMethod) methodsIt.next();
				sootMethod.retrieveActiveBody();
			}
		}
	}

	private static void printMethod(Body body) {
		System.out.println(body.getMethod().getSignature());
		UnitGraph graph = new TrapUnitGraph(body);
		for (Iterator<Unit> graphIt = graph.iterator(); graphIt.hasNext();) { 
			Unit unit = graphIt.next();
			System.out.println(unit);
		}
		System.out.println();		
	}
	
	private static void printTraps(SootMethod method) {
		UnitGraph graph = new TrapUnitGraph(method.getActiveBody());
		
		for (Iterator<Unit> graphIt = graph.iterator(); graphIt.hasNext();) { //itera nos statements atrás de throws
			Unit unit = graphIt.next();
			System.out.println(unit);
		}
		
		System.out.println(method.getActiveBody().getTraps().size());
		for (Iterator<Trap> i = method.getActiveBody().getTraps().iterator(); i.hasNext();) {
			Trap trap = i.next();
			System.out.println(trap.getException());
		}
	}
	
	private static void trataCanais() throws IOException, ClassNotFoundException {
		SootClass klass = Scene.v().getSootClass("user.UserAccessJCML");

		for (Channel canal : dadosDesS.listaDeCanaisSerializavel) {
			for (Context context : canal.listaE) {
				for (String site : context.raisingSites) {
					SootMethod method = klass.getMethod(getSignatureFromSite(site));
					Body body = method.getActiveBody();
//					Chain<Unit> units = body.getUnits();
//					Unit init = units.getFirst();
//					Unit last = units.getLast();
//
//					SootClass echannelKlass = Scene.v().getSootClass("user.EChannelExceptions");
//					SootMethodRef ref = echannelKlass.getMethodByName("throwIt").makeRef();
					
//					InvokeStmt n = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(ref, IntConstant.v(channelID(canal))));
//					units.add(n);
					
//					Jimple.v().newTrap(Scene.v().getSootClass("user.EChannelExceptions"), init, last, n);
//					method.setActiveBody(body);
					printMethod(body);
					System.out.println();
					printTraps(method);
					System.out.println();
					System.out.println();
				}
			}
		}
	}
	
	private static int channelID(Channel channel) {
		return dadosDesS.listaDeCanaisSerializavel.indexOf(channel);
	}
	
	private static String getSignatureFromSite(String site) {
		Pattern exp = Pattern.compile("\\s+?\\w+\\s+(\\w+)\\s+(\\w+)\\((,?\\s?(\\w+)\\s+\\w+)?+\\)");
		Matcher matcher = exp.matcher(site);
		StringBuffer signature = new StringBuffer();
//		System.out.println(matcher.groupCount());
		if (matcher.matches()) {
			signature.append(matcher.group(1)); //tipo de retorno
			signature.append(" ");
			signature.append(matcher.group(2)); //nome do metodo
			signature.append("(");
//			for (int i = 4; i < matcher.groupCount();i++) {
				if (matcher.group(3) != null) {
					signature.append(matcher.group(4)); //tipo do primeiro parametro
				}
//			}
			signature.append(")");
		}
		
//		System.out.println(signature.toString());
		
		return signature.toString();
	}
}
