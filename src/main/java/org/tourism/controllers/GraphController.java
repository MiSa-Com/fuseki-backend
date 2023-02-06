package org.tourism.controllers;

import org.apache.jena.atlas.lib.StrUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.update.UpdateExecution;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.tourism.services.GraphService;

import java.io.IOException;
import java.net.*;

public class GraphController {
    private GraphService graphService = new GraphService();
    private String baseUri = "http://localhost:3332/newDs";

    public void createGraph(String graphUri) throws IOException {
        URL url = new URL(this.baseUri + "?graph=" + graphUri);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("PUT");
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type", "text/turtle");

        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
        http.disconnect();

        graphService.createGraph(graphUri);
    }

    public void constructGraph() {
        RDFConnectionRemoteBuilder builder = RDFConnectionRemote.newBuilder()
                .destination("http://localhost:3332/newDs")
                // Query only.
                .queryEndpoint("sparql")
                .updateEndpoint("update")
                .gspEndpoint("data");

        Query query = QueryFactory.create("PREFIX prop: <https://tourism-vietnam.com/property#>\n" +
                "PREFIX res: <https://tourism-vietnam.com/resource#>\n" +
                "Select * WHERE {?s ?p ?o. }");

                //"CONSTRUCT {?human prop:is res:billionaire . }\n" +
                //"WHERE { ?human prop:own (?a ?b) . }"  );

        // Whether the connection can be reused depends on the details of the implementation.
        // See example 5.
        try ( RDFConnection conn = builder.build() ) {
            ResultSet rs = conn.query(query).execSelect();
            ResultSetFormatter.out(rs);
        }
    }

    public void insertRelation() {
        RDFConnectionRemoteBuilder builder = RDFConnectionRemote.newBuilder()
                .destination("http://localhost:3332/newDs")
                // Query only.
                .queryEndpoint("sparql")
                .updateEndpoint("update")
                .gspEndpoint("data");

        RDFConnection conn = builder.build();
        Dataset ds = conn.fetchDataset();
        ds.begin(ReadWrite.WRITE);
        try {
            // ... perform a SPARQL Update
            String sparqlUpdateString = StrUtils.strjoinNL(
                "PREFIX dcterms: <http://purl.org/dc/terms/>\n" +
                        "INSERT DATA {\n" +
                        "    GRAPH <http://example/shelf_A> {\n" +
                        "        <http://example/author> dcterms:name \"author\" .\n" +
                        "        <http://example/book> dcterms:title \"book\" ;\n" +
                        "                              dcterms:author <http://example/author> .  \n" +
                        "    } \n" +
                        "}"
            );

            UpdateRequest request = UpdateFactory.create(sparqlUpdateString) ;
            UpdateExecution.dataset(ds).update(request).execute();

            // Finally, commit the transaction.
            ds.commit() ;
            // Or call .abort()
        } finally {
            ds.end() ;
        }
    }

}
