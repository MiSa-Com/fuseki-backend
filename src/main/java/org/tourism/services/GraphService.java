package org.tourism.services;

import org.tourism.models.Graph;

public class GraphService {
    public Graph createGraph(String graphUri) {
        return new Graph(graphUri);
    }
}
