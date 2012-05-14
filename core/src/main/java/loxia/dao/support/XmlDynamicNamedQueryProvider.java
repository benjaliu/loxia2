package loxia.dao.support;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlDynamicNamedQueryProvider extends MappedDynamicNamedQueryProvider {

	private String[] configFileList;
	
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;

	public void setConfigFileList(String[] configFileList) {
		this.configFileList = configFileList;
	}
	
	@PostConstruct
	public void init(){
		dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Initialize query provider error.");
		}
		//clear map first
		if(queryMap == null) queryMap = new HashMap<String, String>();
		else queryMap.clear();
		//set query map
		try {
			if(configFileList != null && configFileList.length > 0)
				for(int i=0; i< configFileList.length; i++)
					initConfigFile(configFileList[i]);
		} catch (SAXException e) {
			throw new RuntimeException("parse query xml error.");
		} catch (IOException e) {
			throw new RuntimeException("parse query xml error.");
		}
	}
	
	private void initConfigFile(String config) throws SAXException, IOException{
		Document doc = db.parse(getResourceAsStream(config, XmlDynamicNamedQueryProvider.class));
		NodeList nodeList = doc.getElementsByTagName("bean");
		for (int s = 0; s < nodeList.getLength(); s++) {
			Node node = nodeList.item(s);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) node;
				String name = e.getAttribute("id");
				if(name == null) name = e.getAttribute("name");
				if(name == null) throw new RuntimeException("query xml file format is wrong: cannot file bean id/name");
				System.out.println(name);
				NodeList l = e.getElementsByTagName("value");
				if(l.getLength() != 1)
					throw new RuntimeException("query xml file format is wrong: no query string found for name:" + name);
				String queryString = l.item(0).getTextContent();
				queryMap.put(name, queryString);
			}
		}
	}
	
	private InputStream getResourceAsStream(String resourceName, Class<?> callingClass) {
        URL url = getResource(resourceName, callingClass);
        try {
            return (url != null) ? url.openStream() : null;
        } catch (IOException e) {
            return null;
        }
    }
		
	private URL getResource(String resourceName, Class<?> callingClass) {
        URL url = null;
        url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null) {
            url = callingClass.getClassLoader().getResource(resourceName);
        }
        return url;
    }

	@Override
	public void setQueryMap(Map<String, String> queryMap) {
		throw new RuntimeException("Please do not use this method to set query map, use setting configFileList instead.");
	}
}
