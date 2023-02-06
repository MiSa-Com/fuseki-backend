package org.tourism;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.tourism.controllers.GraphController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        GraphController graphController = new GraphController();
        /*try {
            graphController.createGraph(URLEncoder.encode("https://tourism-vietnam/graph", "UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        graphController.insertRelation();
        graphController.constructGraph();
    }
}