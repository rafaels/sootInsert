package transformers;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;

public class HostHandlersTransformer extends BodyTransformer {
	private static HostHandlersTransformer instance = new HostHandlersTransformer();

	public static  HostHandlersTransformer v() {
		return instance;
	}

	@Override
	protected void internalTransform(Body body, String phaseName, Map options) {
	}
}
