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

			soot.Local eReasonLocal = soot.jimple.Jimple.v().newLocal("$r3", soot.RefType.v("java.lang.short"));
			soot.Local jcmlCodeLocal = soot.jimple.Jimple.v().newLocal("$r4", soot.RefType.v("java.lang.short"));
			body.getLocals().add(eReasonLocal);
			body.getLocals().add(jcmlCodeLocal);
			
			Stmt eReasonLocalAssignment = Jimple.v().newAssignStmt(eReasonLocal, eReason);
			units.insertAfter(eReasonLocalAssignment, caughtIdentity);
			
			InvokeStmt finalInvokeThrowItStmt = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(throwItRef, eReasonLocal));
			units.insertAfter(finalInvokeThrowItStmt, eReasonLocalAssignment);
						
			for (Channel channel : channels) {
				InvokeStmt channelInvokeThrowItStmt = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(throwItRef, IntConstant.v(Util.channelID(channel))));
				
				SootFieldRef jcmlErrorCodeRef = Scene.v().makeFieldRef(echannelExceptionKlass, Util.eChannelTipoToStatic(channel.tipo), soot.RefType.v("java.lang.short"), true);
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
}
