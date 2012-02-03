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
import soot.Local;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Transform;
import soot.Trap;
import soot.Unit;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
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
	
	static { //escrevendo em dadosDesS o objeto guardado em dados.out com as informações dos canais, contextos e tratadores
		try {
			FileInputStream fIn = new FileInputStream("dados.out");
			ObjectInputStream in = new ObjectInputStream(fIn);
			dadosDesS = (EChannelsEhandlers)in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public EChannelsEhandlers getDadosCanais() {
		return dadosDesS;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		PackManager.v().getPack("jtp").add(new Transform("jtp.appletContextsTransformer", AppletContextsTransformer.v()));
		soot.Main.main(args);
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
	
	
	private static void trataCanais() throws IOException, ClassNotFoundException {
		SootClass klass = Scene.v().getSootClass("user.UserAccessJCML");
		for (Channel canal : dadosDesS.listaDeCanaisSerializavel) {
			for (Context context : canal.listaE) {
				for (String site : context.raisingSites) {
					boolean addg = false , addc = false;
					SootMethod method = klass.getMethod(getSignatureFromSite(site));
					Body body = method.getActiveBody();
					Chain<Unit> units = body.getUnits();
					Unit init = units.getFirst();
					Unit methodReturn = units.getLast();
					Unit last = units.getPredOf(methodReturn);
					
					//goto
					UnitGraph graph = new TrapUnitGraph(body);
					for (Iterator<Unit> graphIt = graph.iterator(); graphIt.hasNext();) { 
						Unit unit = graphIt.next();
						if (unit instanceof GotoStmt){
							addg = true;
							break;
						}
					}
					if (addg == false) {
						GotoStmt g = Jimple.v().newGotoStmt(methodReturn);
						units.insertBefore(g, methodReturn);
					} else {
						last = units.getPredOf(last);
					}
					//fim goto
					
					// catch
				

					
					if(method.getActiveBody().getTraps().size() != 0)
						addc = true;
					
					if(addc == false){
				        soot.Local catchRefLocal = soot.jimple.Jimple.v().newLocal("$r2", soot.RefType.v("user.EChannelExceptions"));
				        body.getLocals().add(catchRefLocal);
				        soot.jimple.CaughtExceptionRef caughtRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
				        soot.jimple.Stmt caughtIdentity = soot.jimple.Jimple.v().newIdentityStmt(catchRefLocal, caughtRef);
				        units.insertAfter(caughtIdentity , units.getPredOf(units.getLast()));	
				        //fim catch 
				        
						Trap t = Jimple.v().newTrap(Scene.v().getSootClass("user.EChannelExceptions"), init, last, caughtIdentity);
						body.getTraps().add(t);
						
					}
					
					SootClass echannelKlass = Scene.v().getSootClass("user.EChannelExceptions");
					SootMethodRef ref = echannelKlass.getMethodByName("throwIt").makeRef();
					
					InvokeStmt n = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(ref, IntConstant.v(channelID(canal))));
				
					body.getUnits().insertAfter(n , units.getPredOf(units.getLast()));
					
					method.setActiveBody(body);
					printMethod(body);
					//System.out.println();
					printTraps(method);
					System.out.println();
					System.out.println();
				}
			}
		}
	}
	
	/*private static void trataCanais() throws IOException, ClassNotFoundException {
		SootClass klass = Scene.v().getSootClass("user.UserAccessJCML");
		
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
	}*/

	private static int channelID(Channel channel) {
		return dadosDesS.listaDeCanaisSerializavel.indexOf(channel);
	}

	private static String getSignatureFromSite(String site) {
		Pattern exp = Pattern.compile("\\s+?\\w+\\s+(\\w+)\\s+(\\w+)\\((,?\\s?(\\w+)\\s+\\w+)?+\\)");
		Matcher matcher = exp.matcher(site);
		StringBuffer signature = new StringBuffer();

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

		return signature.toString();
	}
}
