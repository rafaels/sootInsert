import java.util.Map;

import soot.Body;
import soot.BodyTransformer;


public class AppletContextsTransformer extends BodyTransformer {

	private static AppletContextsTransformer instance = new AppletContextsTransformer();
	
	public static  AppletContextsTransformer v() {
		return instance;
	}
	
	@Override
	protected void internalTransform(Body body, String phaseName, Map options) {
		
	}

}
