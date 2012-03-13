package it.unifi.micc.homer.controller;

import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.util.HomerConstants;
import it.unifi.micc.homer.util.HomerException;
import it.unifi.micc.homer.util.HomerProperties;
import it.unifi.micc.homer.util.HomerPropertiesLDAModels;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.w3c.dom.Document;

/**
 * Servlet implementation class Homer
 */
public class Homer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger( Homer.class );
	private static HomerProperties homerProperties = null;
	private static HomerPropertiesLDAModels homerPropertiesLDAModels = null;
	
	protected AnalyserFactory analyserFactory = new AnalyserFactory();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Homer() {
        super();
    }


	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession( true );
		boolean sortResults = false;
		boolean sortByConfidence = false;
		int page;
		int shotsPerPage;
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		/*
		 * fetch parameters from REQUEST
		 */
		try {
			page = Integer.parseInt( (String)request.getParameter( HomerConstants.POSTPAR_PAGE ) );
		} catch( Throwable t ) {
			page = 0;
		}
		try {
			shotsPerPage = Integer.parseInt( (String)request.getParameter( HomerConstants.POSTPAR_RESULTSPERPAGE ) );
		} catch( Throwable t ) {
			shotsPerPage = 20;
		}
		
		try {
			String analysisType;
			try { 
				analysisType = (String)request.getParameter( HomerConstants.POSTPAR_ANALYSIS ); 
			} catch( Throwable t ) {
				analysisType = HomerConstants.ANALYSER_TOPIC;
			}
			AbstractAnalyser homerAnalyser = analyserFactory.create(analysisType);
			homerAnalyser.setHomerProperties(homerProperties);
			Vector<SemanticKeyword> anResult = homerAnalyser.process(request, response);
			/*
			 * save results in session
			 */
			session.setAttribute( HomerConstants.ANALYSIS_RESULTS, anResult );
			
			String outputFormat;
			try {
				outputFormat = (String) request.getParameter(HomerConstants.POSTPAR_OUTPUTFORMAT);
			} catch (Throwable t) {
				outputFormat = "RSS";
			}

			if ((outputFormat != null) && (outputFormat.equals(HomerConstants.JSON_OUTPUTFORMAT))) {
				JSONObject result = AbstractAnalyser.toJSONSearchResult(request.getRequestURL().toString(), anResult,
						page, shotsPerPage);
				AbstractAnalyser.createResponseJson(response, homerAnalyser, result);
			} else {
				Document result = AbstractAnalyser.toRSSSearchResult(request.getRequestURL().toString(), anResult,
						page, shotsPerPage);
				AbstractAnalyser.createResponse(response, homerAnalyser, result);
			}
			
		} catch( Throwable t ) {
			throw new ServletException( t );
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.processRequest(req, resp);
	}
	

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		logger.info("homer servlet init");
		super.init();

		try {
			// 3 lines standard servlet initialization
			homerProperties = HomerProperties.getInstance( config.getServletContext().getRealPath( "/" ) );

			// read default DB access parameters
			String host = (String)homerProperties.getProperty( HomerConstants.PROP_DBHOST );
			String db   = (String)homerProperties.getProperty( HomerConstants.PROP_DBNAME );
			String user = (String)homerProperties.getProperty( HomerConstants.PROP_DBUSER );
			String pass = (String)homerProperties.getProperty( HomerConstants.PROP_DBPASS );
			
			//homerPropertiesLDAModels = HomerPropertiesLDAModels.
			
		} catch( HomerException ve ) {
			logger.debug("init homer servlet exception", ve );
			throw new ServletException( ve.getMessage() );
		}

	}



}
