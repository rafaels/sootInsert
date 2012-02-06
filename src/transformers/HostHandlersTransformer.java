package transformers;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;

public class HostHandlersTransformer extends BodyTransformer {
	private static HostHandlersTransformer instance = new HostHandlersTransformer();

	public static  HostHandlersTransformer v() {
		return instance;
	}

	private boolean isActive = false;

	public void setActive(boolean active) {
		isActive = active;
	}

	@Override
	protected void internalTransform(Body body, String phaseName, Map options) {
		if (!isActive) {
			return;
		}
	}
}
