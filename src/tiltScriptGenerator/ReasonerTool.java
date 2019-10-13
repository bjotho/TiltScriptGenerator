package tiltScriptGenerator;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class ReasonerTool {
	public static OntModel getInferredStatementsAsAModel(OntModel ontModel) {
		if(ontModel == null) return null;
		String defaultNameSpace = ontModel.getNsPrefixURI("");
		Model baseModel = ontModel.getBaseModel();
		OntModel ontModelResult = ModelFactory.createOntologyModel();
		ontModelResult.setNsPrefix("", defaultNameSpace);
		StmtIterator iter = ontModel.listStatements();
		while(iter.hasNext()) {
			Statement stmt = iter.nextStatement(); //get next statement
			if(!baseModel.contains(stmt)) ontModelResult.add(stmt);
		}
		return ontModelResult;
	}
}
