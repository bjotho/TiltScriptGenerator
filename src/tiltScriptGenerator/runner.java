package tiltScriptGenerator;

import java.util.List;

public class runner {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		ModelHandler modelHandler = new ModelHandler();
		//modelHandler.getInitialBodyTempReadings();
		
		GUI gui = new GUI("Tilt Script Generator");
		gui.setModelHandler(modelHandler);
	}
}
