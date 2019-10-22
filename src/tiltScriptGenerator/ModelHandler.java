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
		ResIterator iter = model.listSubjectsWithProperty(hasSCTID);
		List<String[]> output = new ArrayList<String[]>();
		
		while (iter.hasNext()) {
			Resource r = iter.nextResource();
			if (r.hasProperty(hasSCTID, "297976006")) {
				String[] event = new String[3];
				Property hasValue = model.getProperty(defaultNameSpace + "hasValue");
				RDFNode rValueNode = r.getProperty(hasValue).getObject();
				Resource rValue = rValueNode.asResource();
				Property hasValueValue = model.getProperty(defaultNameSpace + "hasValueValue");
				Property hasTime = model.getProperty(defaultNameSpace + "hasTime");
				RDFNode value = rValue.getProperty(hasValueValue).getObject();
				RDFNode time = rValue.getProperty(hasTime).getObject();
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
				output.add(event);
			}
		}
		
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
