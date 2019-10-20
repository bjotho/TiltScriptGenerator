package tiltScriptGenerator;

import java.io.InputStream;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;

public class runner {
	private static String inputFileName = "uia_tilt_input.ttl";
	private static String outputFileName = "uia_tilt_output.ttl";

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		OntModel model = readFile(OntModelSpec.OWL_DL_MEM);
		String defaultNameSpace = "http://www.uia.no/jpn/tilt#";
		
		Property hasSCTID = model.getProperty(defaultNameSpace + "hasSCTID");
		System.out.println("Created property: " + hasSCTID);
		
		String format = "%-30s";
		
		ResIterator iter = model.listSubjectsWithProperty(hasSCTID);
		System.out.println("Iterating...");
		
		while (iter.hasNext()) {
			Resource r = iter.nextResource();
			if (r.hasProperty(hasSCTID, "297976006")) {
				String valueTriple = String.format(format, r.getLocalName().toString());
				String timeTriple = String.format(format, r.getLocalName().toString());
				Property hasValue = model.getProperty(defaultNameSpace + "hasValue");
				valueTriple += String.format(format, hasValue.getLocalName().toString());
				RDFNode rValueNode = r.getProperty(hasValue).getObject();
				Resource rValue = model.getResource(rValueNode.toString());
				Property hasValueValue = model.getProperty(defaultNameSpace + "hasValueValue");
				Property hasTime = model.getProperty(defaultNameSpace + "hasTime");
				timeTriple += String.format(format, hasTime.getLocalName().toString());
				RDFNode value = rValue.getProperty(hasValueValue).getObject();
				RDFNode time = rValue.getProperty(hasTime).getObject();
				valueTriple += String.format(format, value.toString());
				timeTriple += String.format(format, time.toString());
				System.out.println(valueTriple);
				System.out.println(timeTriple + "\n");
			}
		}
		System.out.println("Done");
		
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
