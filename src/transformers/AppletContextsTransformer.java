package transformers;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
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
			GotoStmt gotoReturn = Jimple.v().newGotoStmt(returnStmt); //goto return
			units.insertAfter(gotoReturn, last);

			soot.Local catchRefLocal = soot.jimple.Jimple.v().newLocal("$r2", soot.RefType.v("user.EChannelExceptions")); // local que guarda a exceção capturada => e
			body.getLocals().add(catchRefLocal);

			soot.jimple.CaughtExceptionRef caughtRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
			soot.jimple.Stmt caughtIdentity = soot.jimple.Jimple.v().newIdentityStmt(catchRefLocal, caughtRef);
			units.insertAfter(caughtIdentity, gotoReturn);
			//fim catch

			SootClass echannelExceptionKlass = Scene.v().getSootClass("user.EChannelExceptions");

			Trap t = Jimple.v().newTrap(echannelExceptionKlass, init, gotoReturn, caughtIdentity);
			body.getTraps().add(t);

			SootMethodRef throwItRef = echannelExceptionKlass.getMethodByName("throwIt").makeRef();    //getReason()
			SootMethodRef getReasonRef = echannelExceptionKlass.getSuperclass().getMethodByName("getReason").makeRef();//throwIt()
			
			VirtualInvokeExpr eReason = Jimple.v().newVirtualInvokeExpr(catchRefLocal, getReasonRef); //e.getReason()
			InvokeStmt finalInvokeThrowItStmt = Jimple.v().newInvokeStmt(eReason); //vai pro final
			
			Unit next = caughtIdentity;
			
			for (Channel channel : channels) {
				InvokeStmt invokeThrowItStmt = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(throwItRef, IntConstant.v(Util.channelID(channel))));

				SootFieldRef invariantErrorCodeRef = Scene.v().makeFieldRef(echannelExceptionKlass, "SW_INVARIANT_ERROR", soot.RefType.v("java.lang.short"), true);
				StaticFieldRef invariantErrorCode = Jimple.v().newStaticFieldRef(invariantErrorCodeRef);
				
				
				Value condition = Jimple.v().newCmpExpr(eReason, invariantErrorCode);
				
				IfStmt ifStmt = Jimple.v().newIfStmt(condition, invokeThrowItStmt);
				
				units.insertAfter(ifStmt, next);
				next = invokeThrowItStmt;
			}
			
			units.insertAfter(finalInvokeThrowItStmt, next);
		}
	}
}
