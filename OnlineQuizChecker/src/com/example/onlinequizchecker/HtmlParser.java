package com.example.onlinequizchecker;


import java.io.File;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * The Class HtmlParser.
 * This class is used for reading the quiz files
 * and changing them during the quiz.
 */
public class HtmlParser {
	
	/** The document. */
	public Document document;	

	/**
	 * Instantiates a new html parser.
	 *
	 * @param xmlFile the file to read
	 */
	public HtmlParser(InputStream xmlFile) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.parse(xmlFile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write html.
	 *
	 * @param path the path to save the file to
	 * @throws TransformerException the transformer exception
	 */
	public void writeHtml(String path) throws TransformerException{
		// write the content into HTML file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(path));

		transformer.transform(source, result);

	}
	
	
}
