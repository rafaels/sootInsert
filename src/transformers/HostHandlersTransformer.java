package transformers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.Scene;
import soot.ShortType;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.util.Chain;
import sootInsert.Main;
import source.Channel;
import source.Handler;
import util.Util;

public class HostHandlersTransformer extends BodyTransformer {
	private static HostHandlersTransformer instance = new HostHandlersTransformer();

	public static  HostHandlersTransformer v() {
		return instance;
	}

	@Override
	protected void internalTransform(Body body, String phaseName, Map options) {
		SootClass handlerKlass = Scene.v().getSootClass("UserAccessEchannel");
		SootMethod transmitMethod = Scene.v().getMethod("<javax.smartcardio.CardChannel: javax.smartcardio.ResponseAPDU transmit(javax.smartcardio.CommandAPDU)>");
		SootMethodRef getSW = Scene.v().getMethod("<javax.smartcardio.ResponseAPDU: int getSW()>").makeRef();

		Chain<Unit> units = body.getUnits();

		for (Iterator<Unit> iterator = units.snapshotIterator(); iterator.hasNext();) {
			Unit unit = iterator.next();
			if (unit instanceof AssignStmt) {
				AssignStmt stmt = (AssignStmt) unit;
				Unit next = units.getSuccOf(stmt);
				if (stmt.containsInvokeExpr()) {
					SootMethod invokeMethod = stmt.getInvokeExpr().getMethod();
					if (transmitMethod == invokeMethod) {
						Value v = stmt.getLeftOp(); //é um ResponseAPDU
						if (v instanceof Local) {
							System.out.println("Hardcore Handling " + body.getMethod().getSignature() + "...");
							Local l = (Local) v;

							//short e = l.getSW()
							InvokeExpr invoke = Jimple.v().newVirtualInvokeExpr(l, getSW);
							soot.Local eReasonLocal = soot.jimple.Jimple.v().newLocal("$r" + body.getLocalCount(), ShortType.v());
							body.getLocals().add(eReasonLocal);
							Stmt eReasonLocalAssignment = Jimple.v().newAssignStmt(eReasonLocal, invoke);
							units.insertAfter(eReasonLocalAssignment, stmt);

							//switch e
							ArrayList<Integer> lookupValues = new ArrayList<Integer>();
							ArrayList<Unit> targets = new ArrayList<Unit>();
							//canais
							for (Channel canal : Main.getChannels()) {
								int id = Util.channelID(canal);
								lookupValues.add(id);

								Unit last = units.getLast();
								for (Handler handler : Main.getHandlersFromChannel(canal)) {
									SootMethodRef ref = handlerKlass.getMethodByName(handler.metodo).makeRef();
									Stmt invokeHandler = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(ref));
									units.addLast(invokeHandler);
								}

								Stmt gotoStmt = Jimple.v().newGotoStmt(next);
								units.addLast(gotoStmt);
								targets.add(units.getSuccOf(last));
							}

							Stmt lookupSwitch = Jimple.v().newLookupSwitchStmt(eReasonLocal, lookupValues, targets, next);
							units.insertAfter(lookupSwitch, eReasonLocalAssignment);
						}
					}
				}
			}
		}
	}
}
