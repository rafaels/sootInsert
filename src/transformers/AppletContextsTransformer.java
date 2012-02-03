package transformers;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Trap;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.toolkits.graph.TrapUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;
import sootInsert.Main;
import source.Channel;
import source.Context;
import util.Util;

public class AppletContextsTransformer extends BodyTransformer {

	private static AppletContextsTransformer instance = new AppletContextsTransformer();
	
	public static  AppletContextsTransformer v() {
		return instance;
	}
	
	@Override
	protected void internalTransform(Body body, String phaseName, Map options) {
		SootMethod method = body.getMethod();
		String signature = Util.getSignatureFromSignature(method.getSignature());
		List<Channel> channels = Main.getChannelsFromSite(signature);

		if (channels != null) {
			//todo método termina com return?
			Chain<Unit> units = body.getUnits();

			Unit returnStmt = units.getLast();
			Unit init       = units.getFirst(); //inicio da trap
			Unit last       = units.getPredOf(returnStmt); //fim da trap

			//construindo a trap
			GotoStmt gotoReturn = Jimple.v().newGotoStmt(returnStmt);
			units.insertAfter(gotoReturn, last);

			soot.Local catchRefLocal = soot.jimple.Jimple.v().newLocal("$r2", soot.RefType.v("user.EChannelExceptions"));
			body.getLocals().add(catchRefLocal);

			soot.jimple.CaughtExceptionRef caughtRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
			soot.jimple.Stmt caughtIdentity = soot.jimple.Jimple.v().newIdentityStmt(catchRefLocal, caughtRef);
			units.insertAfter(caughtIdentity, gotoReturn);
			//fim catch

			SootClass echannelExceptionKlass = Scene.v().getSootClass("user.EChannelExceptions");

			Trap t = Jimple.v().newTrap(echannelExceptionKlass, init, gotoReturn, caughtIdentity);
			body.getTraps().add(t);

			SootMethodRef ref = echannelExceptionKlass.getMethodByName("throwIt").makeRef();

			InvokeStmt invokeThrowItStmt = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(ref, IntConstant.v(Util.channelID(channels.get(0)))));

			units.insertAfter(invokeThrowItStmt, caughtIdentity);
		}
	}

	private static void trataCanais() throws IOException, ClassNotFoundException {
		SootClass klass = Scene.v().getSootClass("user.UserAccessJCML");
		for (Channel canal : Main.getDadosCanais().listaDeCanaisSerializavel) {
			for (Context context : canal.listaE) {
				for (String site : context.raisingSites) {
					boolean addg = false , addc = false;
					SootMethod method = klass.getMethod(Util.getSignatureFromSite(site));
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

					InvokeStmt n = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(ref, IntConstant.v(Util.channelID(canal))));

					body.getUnits().insertAfter(n , units.getPredOf(units.getLast()));

					method.setActiveBody(body);
					Util.printMethod(body);
					//System.out.println();
					Util.printTraps(method);
					System.out.println();
					System.out.println();
				}
			}
		}
	}
}
