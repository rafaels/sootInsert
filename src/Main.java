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
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.Jimple;
import soot.jimple.ParameterRef;
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
		trataCanais();
		run();
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

	private static void run() throws IOException, ClassNotFoundException {
		SootClass testeKlass = Scene.v().getSootClass("UserAccessJCML");
		SootClass tratadorKlass = Scene.v().getSootClass("UserAccessEchannel");

		SootMethod testeMethod = testeKlass.getMethodByName("addCredits");
		SootMethod tratadorMethod = tratadorKlass.getMethodByName("MH1");
		testeMethod.retrieveActiveBody();
		tratadorMethod.retrieveActiveBody();

		Body testeBody = testeMethod.getActiveBody();
		printMethod(testeBody);

		Chain testeUnits = testeBody.getUnits();
		Body tratadorBody = tratadorMethod.getActiveBody();
		Chain tratadorUnits = tratadorBody.getUnits();

		//O codigo abaixo serah necessario apenas se desejar-mos inserir no inicio do metodo apos a primeira instrucao
		Object init = testeUnits.getFirst();


		for (Iterator<Object> unitsIterator = testeUnits.iterator(); unitsIterator.hasNext(); ) {
			Unit unit = (Unit) unitsIterator.next();


			if(testeUnits.getFirst() == unit) {
				continue;
			}

			if(unit instanceof IdentityStmt) {
				IdentityStmt identityStmt = (IdentityStmt) unit;

				if(identityStmt.getRightOp() instanceof ParameterRef) {
					continue;
				}
			}

			init = unit;
			break;
		}

		tratadorUnits.removeFirst();
		tratadorUnits.removeLast();

		System.out.println("ULTIMO "+ testeUnits.getLast());
		testeUnits.insertAfter(tratadorUnits, init);
		//testeUnits.insertBefore(tratadorUnits, testeUnits.getLast()); //Sempre insere antes do return do mehtodo, melhor assim?
		Unit first = (Unit) testeUnits.getFirst();
		Unit last  = (Unit) testeUnits.getLast();
		Unit handler = (Unit) tratadorUnits.getFirst();
		Jimple.v().newTrap(Scene.v().getSootClass("EChannelExceptions"), first, last, handler);

		testeBody.getLocals().addAll(tratadorBody.getLocals());

		printMethod(testeBody);

		testeMethod.setActiveBody(testeBody);
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
	
	private static void trataCanais() throws IOException, ClassNotFoundException {
		SootClass klass = Scene.v().getSootClass("UserAccessJCML");

		for (Channel canal : dadosDesS.listaDeCanaisSerializavel) {
			System.out.println(canal.nome);
			for (Context context : canal.listaE) {
				System.out.println(context.nomeContext);
				for (String site : context.raisingSites) {
					System.out.println(site);
					System.out.println(getSignatureFromSite(site));
					SootMethod method = klass.getMethod("void addCredits(short)");
					System.out.println(method.getSignature());
					System.out.println();
				}
			}
		}
		System.exit(0);
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
