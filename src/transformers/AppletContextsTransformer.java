package transformers;
import java.util.List;
import java.util.Map;

import polyglot.ast.Instanceof;

import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.ShortType;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.util.Chain;
import sootInsert.Main;
import source.Channel;
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

		if (channels != null) { //é um método que está associado ao menos com um canal
			transformContext(body, signature, channels);
		}

		if (method.getSignature().matches("<.*:\\svoid process\\(javacard.framework.APDU\\)>")) {
			transformProccess(body);
		}
	}

	private void transformProccess(Body body) {
		Chain<Unit> units = body.getUnits();
		Unit init         = units.getFirst(); //inicio da trap
		Unit last         = units.getLast(); //fim da trap
		ReturnVoidStmt returnStmt = Jimple.v().newReturnVoidStmt();
	}

    //usado para aplicar as transformações nos raising sites
	private void transformContext(Body body, String signature, List<Channel> channels) {
		//todo método termina com return?
		Chain<Unit> units = body.getUnits();

		Unit init       = units.getFirst(); //inicio da trap
		Unit last       = units.getLast(); //fim da trap
		Unit returnStmt = units.getLast();

		if (!(returnStmt instanceof ReturnStmt)) {
			returnStmt = Jimple.v().newReturnVoidStmt();
			units.addLast(returnStmt);

			GotoStmt gotoReturn = Jimple.v().newGotoStmt(returnStmt); //goto return
			units.insertAfter(gotoReturn, last);
			last = gotoReturn;
		}

		//construindo a trap
		soot.Local catchRefLocal = soot.jimple.Jimple.v().newLocal("$r2", soot.RefType.v("user.EChannelExceptions")); // local que guarda a exceção capturada => e
		body.getLocals().add(catchRefLocal);

		soot.jimple.CaughtExceptionRef caughtRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
		soot.jimple.Stmt caughtIdentity = soot.jimple.Jimple.v().newIdentityStmt(catchRefLocal, caughtRef);
		units.insertAfter(caughtIdentity, last);
		//fim catch

		SootClass echannelExceptionKlass = Scene.v().getSootClass("user.EChannelExceptions");

		Trap t = Jimple.v().newTrap(echannelExceptionKlass, init, last, caughtIdentity);
		body.getTraps().add(t);

		SootMethodRef throwItRef = echannelExceptionKlass.getMethodByName("throwIt").makeRef();    //getReason()
		SootMethodRef getReasonRef = echannelExceptionKlass.getSuperclass().getMethodByName("getReason").makeRef();//throwIt()

		VirtualInvokeExpr eReason = Jimple.v().newVirtualInvokeExpr(catchRefLocal, getReasonRef); //e.getReason()

		soot.Local eReasonLocal = soot.jimple.Jimple.v().newLocal("$r3", ShortType.v());
		soot.Local jcmlCodeLocal = soot.jimple.Jimple.v().newLocal("$r4", ShortType.v());
		body.getLocals().add(eReasonLocal);
		body.getLocals().add(jcmlCodeLocal);

		Stmt eReasonLocalAssignment = Jimple.v().newAssignStmt(eReasonLocal, eReason);
		units.insertAfter(eReasonLocalAssignment, caughtIdentity);

		InvokeStmt finalInvokeThrowItStmt = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(throwItRef, eReasonLocal));
		units.insertAfter(finalInvokeThrowItStmt, eReasonLocalAssignment);

		for (Channel channel : channels) {
			InvokeStmt channelInvokeThrowItStmt = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(throwItRef, IntConstant.v(Util.channelID(channel))));

			SootFieldRef jcmlErrorCodeRef = Scene.v().makeFieldRef(echannelExceptionKlass, Util.eChannelTipoToStatic(channel.tipo), ShortType.v(), true);
			StaticFieldRef invariantErrorCode = Jimple.v().newStaticFieldRef(jcmlErrorCodeRef);
			Stmt jcmlCodeAssignment = Jimple.v().newAssignStmt(jcmlCodeLocal, invariantErrorCode);

			Value condition = Jimple.v().newEqExpr(eReasonLocal, jcmlCodeLocal);//colocar os dois em outro canto

			IfStmt ifStmt = Jimple.v().newIfStmt(condition, channelInvokeThrowItStmt);

			units.insertAfter(channelInvokeThrowItStmt, finalInvokeThrowItStmt);
			units.insertBefore(jcmlCodeAssignment, finalInvokeThrowItStmt);
			units.insertAfter(ifStmt, jcmlCodeAssignment);
		}
	}
}
