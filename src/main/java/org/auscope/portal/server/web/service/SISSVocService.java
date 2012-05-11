package org.auscope.portal.server.web.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.apache.commons.httpclient.HttpMethodBase;
import org.auscope.portal.server.domain.vocab.Concept;
import org.auscope.portal.server.domain.vocab.ConceptFactory;
import org.auscope.portal.server.domain.vocab.VocabNamespaceContext;
import org.auscope.portal.server.util.DOMUtil;
import org.auscope.portal.server.web.SISSVocMethodMaker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Service class for interacting with a SISSVoc endpoint
 * @author Josh Vote
 */
public class SISSVocService {
    private HttpServiceCaller httpServiceCaller;
    private ConceptFactory conceptFactory;
    private SISSVocMethodMaker sissVocMethodMaker;

    public SISSVocService(HttpServiceCaller httpServiceCaller, ConceptFactory conceptFactory, SISSVocMethodMaker sissVocMethodMaker) {
        this.httpServiceCaller = httpServiceCaller;
        this.conceptFactory = conceptFactory;
        this.sissVocMethodMaker = sissVocMethodMaker;
    }

    /**
     * Gets the Concept objects associated with the specified label
     * @param serviceUrl The SISSVoc endpoint to query
     * @param repository The SISSVoc repository to query
     * @param label The label query for
     * @return
     * @throws PortalServiceException
     */
    public Concept[] getConceptByLabel(String serviceUrl, String repository, String label) throws PortalServiceException {
        HttpMethodBase method = null;
        try {
            //Do the request
            method = sissVocMethodMaker.getConceptByLabelMethod(serviceUrl, repository, label);
            InputStream responseStream = httpServiceCaller.getMethodResponseAsStream(method, httpServiceCaller.getHttpClient());

            //Parse the response
            Document doc = DOMUtil.buildDomFromStream(responseStream);
            XPathExpression rdfExpression = DOMUtil.compileXPathExpr("rdf:RDF", new VocabNamespaceContext());
            Node response = (Node) rdfExpression.evaluate(doc, XPathConstants.NODE);
            return conceptFactory.parseFromRDF(response);
        } catch (Exception ex) {
            throw new PortalServiceException(method, ex);
        }
    }

    /**
     * Gets the Concept objects associated with a getCommodity request
     * @param serviceUrl The SISSVoc endpoint to query
     * @param repository The SISSVoc repository to query
     * @param commodityParent The commodity paretn
     * @return
     * @throws PortalServiceException
     */
    public Concept[] getCommodityConcepts(String serviceUrl, String repository, String commodityParent) throws PortalServiceException {
        HttpMethodBase method = null;
        try {
            //Do the request
            method = sissVocMethodMaker.getCommodityMethod(serviceUrl, repository, commodityParent);
            InputStream responseStream = httpServiceCaller.getMethodResponseAsStream(method, httpServiceCaller.getHttpClient());

            //Parse the response
            Document doc = DOMUtil.buildDomFromStream(responseStream);
            XPathExpression expression = DOMUtil.compileXPathExpr("/sparql:sparql/sparql:results/sparql:result", new VocabNamespaceContext());
            NodeList exprResult = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);


            List<Concept> concepts = new ArrayList<Concept>();
            for (int i = 0; i < exprResult.getLength(); i++) {
                Element node = (Element)exprResult.item(i);
                String uri = node.getElementsByTagName("uri").item(0).getTextContent();
                String literal = node.getElementsByTagName("literal").item(0).getTextContent();

                Concept concept = new Concept(uri);
                concept.setLabel(literal);

                concepts.add(concept);
            }

            return concepts.toArray(new Concept[concepts.size()]);
        } catch (Exception ex) {
            throw new PortalServiceException(method, ex);
        }
    }
}
