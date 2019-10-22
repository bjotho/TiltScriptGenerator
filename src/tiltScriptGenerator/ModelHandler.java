package tiltScriptGenerator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;

public class ModelHandler {
	private static String inputFileName = "uia_tilt_input.ttl";
	private static String defaultNameSpace = "http://www.uia.no/jpn/tilt#";
	private OntModel model;
	
	public ModelHandler() {
		this.model = readFile(OntModelSpec.OWL_DL_MEM);
	}
	
	public List<String[]> getInitialBodyTempReadings() {
		Property hasSCTID = this.model.getProperty(ModelHandler.defaultNameSpace + "hasSCTID");
		//System.out.println("Created property: " + hasSCTID);
		
		//String format = "%-30s";
		
		ResIterator iter = model.listSubjectsWithProperty(hasSCTID);
		//System.out.println("Iterating...\n");
		
		List<String[]> output = new ArrayList<String[]>();
		
		while (iter.hasNext()) {
			Resource r = iter.nextResource();
			if (r.hasProperty(hasSCTID, "297976006")) {
				String[] event = new String[3];
				//String valueTriple = String.format(format, r.getLocalName().toString());
				//String timeTriple = String.format(format, r.getLocalName().toString());
				Property hasValue = model.getProperty(defaultNameSpace + "hasValue");
				//valueTriple += String.format(format, hasValue.getLocalName().toString());
				RDFNode rValueNode = r.getProperty(hasValue).getObject();
				Resource rValue = rValueNode.asResource();
				Property hasValueValue = model.getProperty(defaultNameSpace + "hasValueValue");
				Property hasTime = model.getProperty(defaultNameSpace + "hasTime");
				//timeTriple += String.format(format, hasTime.getLocalName().toString());
				RDFNode value = rValue.getProperty(hasValueValue).getObject();
				RDFNode time = rValue.getProperty(hasTime).getObject();
				//valueTriple += String.format(format, value.toString());
				//timeTriple += String.format(format, time.toString());
				Property hasTypeName = model.getProperty(defaultNameSpace + "hasTypeName");
				try {
					event[0] = r.getProperty(hasTypeName).getObject().toString();
				} catch (NullPointerException e) {
					try {
						event[0] = rValue.getProperty(hasTypeName).getObject().toString();
					} catch (NullPointerException e1) {
						event[0] = r.getLocalName().toString();
					}
				}
				event[1] = value.toString();
				event[2] = time.toString();
				//System.out.println(valueTriple);
				//System.out.println(timeTriple + "\n");
				output.add(event);
			}
		}
		//System.out.println("Done\n");
		
		return output;
	}
	
	public static OntModel readFile(OntModelSpec ontModelSpec) {
		OntModel ontModel = ModelFactory.createOntologyModel(ontModelSpec);
		InputStream inFile = FileManager.get().open(inputFileName);
		if(inFile == null) {
			System.out.println("File: " + inputFileName + " not found");
			System.exit(1);
		}
		ontModel.read(inFile, null, "Turtle");
		return ontModel;
	}
}
