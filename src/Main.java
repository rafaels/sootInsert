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
	
	public static EChannelsEhandlers getDadosCanais() {
		return dadosDesS;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		PackManager.v().getPack("jtp").add(new Transform("jtp.appletContextsTransformer", AppletContextsTransformer.v()));
		soot.Main.main(args);
	}
}
