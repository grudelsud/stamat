/**
 * 
 */
package it.unifi.micc.homer.controller;

import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.util.HomerConstants;
import it.unifi.micc.homer.util.HomerException;
import it.unifi.micc.homer.util.HomerProperties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.l3s.boilerpipe.extractors.ArticleExtractor;


/**
 * @author bertini
 *
 */
/**
 * @author bertini
 *
 */
public abstract class AbstractAnalyser implements IAnalyser {
	private HomerProperties hp = null;
	
	public static JSONObject toJSONSearchResult(String servletUrl, Vector<SemanticKeyword> result, int page,
			int resultsPerPage) throws HomerException {
		try {
			JSONObject jsonDoc = new JSONObject();

			int size;
			if (result != null)
				size = result.size();
			else
				size = 0;

			JSONArray elementsArray = new JSONArray();
			if (size > 0) {
				int start = page * resultsPerPage;
				if (start > size) {
					start = size - resultsPerPage >= 0 ? size - resultsPerPage : 0;
				}
				int end = start + resultsPerPage <= size ? start + resultsPerPage : size;

				List<SemanticKeyword> resultPage = result.subList(start, end);

				for (SemanticKeyword semkey : resultPage) {
					// n = xmldoc.importNode(
					// semkey.toRSSItem().getDocumentElement() , true );
					elementsArray.put(semkey.toJSONItem());
				}
				jsonDoc.put("results", elementsArray);

			} else {
				jsonDoc.put("result", elementsArray);
			}
			// System.out.println(jsondoc);
			return jsonDoc;
		} catch (Throwable t) {
			throw new HomerException(t);
		}
	}


	public static Document toRSSSearchResult( String servletUrl, Vector<SemanticKeyword> result, int page, int resultsPerPage ) throws HomerException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();
			Document xmldoc = impl.createDocument(null, "rss", null);
			
			Element e = null;
			Node n = null;
			
			// Root element.
			Element root = xmldoc.getDocumentElement();
			root.setAttribute( "version", "2.0" );

			Element channel = xmldoc.createElement( "channel" );

			e = xmldoc.createElement( "title" );
			n = xmldoc.createTextNode( "Homer RSS feed" );
			e.appendChild( n );
			channel.appendChild( e );

			e = xmldoc.createElement( "link" );
			n = xmldoc.createTextNode( servletUrl );
			e.appendChild( n );
			channel.appendChild( e );

			e = xmldoc.createElement( "description" );
			n = xmldoc.createTextNode( "Results for Homer analysis" );
			e.appendChild( n );
			channel.appendChild( e );

			e = xmldoc.createElement( "numresults" );
			int size;
			if ( result != null )
				size = result.size();
			else
				size = 0;
			n = xmldoc.createTextNode( String.valueOf( size ) );
			e.appendChild( n );
			channel.appendChild( e );

			if( size > 0 ) {
				int start = page * resultsPerPage;
				if( start > size ) {
					start = size - resultsPerPage >= 0 ? size - resultsPerPage : 0;
				}
				int end   = start + resultsPerPage <= size ? start + resultsPerPage : size;

				List<SemanticKeyword> resultPage = result.subList( start, end );

				for( SemanticKeyword semkey : resultPage ) {
					n = xmldoc.importNode( semkey.toRSSItem().getDocumentElement() , true );
					channel.appendChild( n );
				}
			} else {
				e = xmldoc.createElement( "message" );
				n = xmldoc.createTextNode( "Nothing found" );
				e.appendChild( n );
				channel.appendChild( e );
			}

			root.appendChild( channel );

			return xmldoc;

		} catch( Throwable t ) {
			throw new HomerException( t );
		}
	}
	
	/**
	 * @param response
	 * @param analyser
	 * @param result
	 * @throws IOException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	public static void createResponse(HttpServletResponse response, IAnalyser analyser, Document result) 
	throws IOException, TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {

		response.setContentType( "text/xml" );
		PrintWriter out = response.getWriter();

		DOMSource          domSource    = new DOMSource( result );
		StreamResult       streamResult = new StreamResult(out);
		TransformerFactory tf           = TransformerFactory.newInstance();
		Transformer        serializer   = tf.newTransformer();

		serializer.setOutputProperty( OutputKeys.INDENT, "yes" );
		serializer.transform(domSource, streamResult);
	}
	
	public static void writeXMLFileResponse(String fileName, IAnalyser analyser, Document result)
			throws IOException, TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {

		FileWriter out = new java.io.FileWriter(fileName);

		DOMSource          domSource    = new DOMSource( result );
		StreamResult       streamResult = new StreamResult(out);
		TransformerFactory tf           = TransformerFactory.newInstance();
		Transformer        serializer   = tf.newTransformer();

		serializer.setOutputProperty( OutputKeys.INDENT, "yes" );
		serializer.transform(domSource, streamResult);
	}

	public static void createResponseJson(HttpServletResponse response, IAnalyser analyser, JSONObject result) 
			throws IOException, TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {

				response.setContentType( "application/json" );
				PrintWriter out = response.getWriter();
				out.print(result);
				out.flush();
				
	}
	
	/**
	 * Check servlet parameters to get the required analysis. If not found then return HomerConstants.ANALYSER_TOPIC
	 * 
	 * @param request
	 * @return
	 */
	protected String getAnalysisParameter(HttpServletRequest request) {
		try {
			String analysis = (String)request.getParameter( HomerConstants.POSTPAR_ANALYSIS );
			if( analysis == null )
				analysis = HomerConstants.DEFAULT_ANALYSIS;
			
			return analysis;
		} catch (Exception e) {
			return HomerConstants.DEFAULT_ANALYSIS;
		}
	}
	
	/**
	 * Attempts to retrieve text from request, following a preferential order:
	 * 1. text passed as URL
	 * 2. text passed as post parameter
	 * 3. text passed as file (path+name) 
	 * 
	 * @param request
	 * @return text if found null otherwise
	 */
	protected String getAnyText(HttpServletRequest request) {
		String text = null;
		text = getTextURLParameter(request);
		if( text == null || (text != null && text.equals("")) )
			text = getTextParameter(request);
		if( text == null || (text != null && text.equals("")) )
			text = getTextFileParameter(request);
		
		if( text == null )
			text = "";
		
		return text;
	}
	
	protected String getTextParameter(HttpServletRequest request) {
		try {
			String text = (String)request.getParameter( HomerConstants.POSTPAR_TEXT );
			
			return text;
		}catch (Exception e) {
			return null;
		}
	}
	
	protected String getTextURLParameter(HttpServletRequest request) {
		try {
			String url = (String)request.getParameter( HomerConstants.POSTPAR_DOCURL );
			
			String text = null;
			if( ((String)request.getParameter(HomerConstants.POSTPAR_TEXTEXTRACT)).equals(HomerConstants.HTMLEXTRACT_ARTICLE) ) {
				text = ArticleExtractor.INSTANCE.getText(Jsoup.connect(url).get().html());
			} else {
				org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
				if( doc != null )
					text = Jsoup.parse(doc.text()).text();
			}
			
			return text;
		} catch (Exception e) {
			return null;
		}
	}
	
	protected String getTextFileParameter(HttpServletRequest request) {
		String filename = (String) request.getParameter(HomerConstants.POSTPAR_INFILENAME);
		String filepath = (String) request.getParameter(HomerConstants.POSTPAR_INFILEPATH);
		String fullname;
		if (filepath != null)
			fullname = filepath + filename;
		else
			fullname = filepath;

		String text = null;
		if (filepath != null) {
			StringBuffer strb = new StringBuffer();
			try {
				BufferedReader in = new BufferedReader(new FileReader(fullname));
				String str;
				while ((str = in.readLine()) != null) {
					strb.append(str);
				}
				in.close();
			} catch (IOException e) {
			}
			text = strb.toString();
		}
		return text;
	}
	
	
	protected String getTextTypeParameter(HttpServletRequest request) {
		try {
			String texttype = (String)request.getParameter( HomerConstants.POSTPAR_TEXTTYPE );
			if( texttype == null )
				texttype = HomerConstants.DEFAULT_TEXTTYPE;
			
			return texttype;
		} catch (Exception e) {
			return HomerConstants.DEFAULT_TEXTTYPE;
		}
	}

	/**
	 * @return the hp
	 */
	public HomerProperties getHomerProperties() {
		return hp;
	}

	/**
	 * @param hp the hp to set
	 */
	public void setHomerProperties(HomerProperties hp) {
		this.hp = hp;
	}


}
