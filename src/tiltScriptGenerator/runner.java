package tiltScriptGenerator;

import java.io.InputStream;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;

public class runner {
	private static String inputFileName = "uia_tilt_input.ttl";
	private static String outputFileName = "uia_tilt_output.ttl";

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		OntModel model = readFile(OntModelSpec.OWL_DL_MEM_RULE_INF);
		String defaultNameSpace = model.getNsPrefixMap().get("");
		Property hasSCTID = model.getProperty(defaultNameSpace + "hasSCTID");
		System.out.println("Created property: " + hasSCTID);
		ResIterator iter = model.listSubjectsWithProperty(hasSCTID);
		System.out.println("Iterating...");
		while (iter.hasNext()) {
			Resource r = iter.nextResource();
			System.out.println(r);
		}
		
		//GUI gui = new GUI("Tilt Script Generator");
	}
	
	public static OntModel readFile(OntModelSpec ontModelSpec) {
		OntModel ontModel = ModelFactory.createOntologyModel(ontModelSpec);
		InputStream inFile = FileManager.get().open(inputFileName);
		if(inFile == null) {
			System.out.println("File: " + inputFileName + " not found");
			System.exit(1);
		}
		//read the Turtle file
		ontModel.read(inFile, null, "Turtle");
		return ontModel;
	}
}
