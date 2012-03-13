package it.unifi.micc.homer.model;

import it.unifi.micc.homer.util.HomerConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class FakeServletRequest implements HttpServletRequest {

	private String postparTextType;
	private String postparTextExtraction;
	private String postparAnalysis;
	private String postparEntityType;
	private String postparPage;
	private String postparResultsPerPage;
	private String postparText;
	private String postparTextLang;
	private String postparInFilename;
	private String postparInFilePath;
	private String postparDocUrl;
	private String postparNumKeywords;
	private String postparNumTopics;

	public String getPostparPage() {
		return postparPage;
	}

	public void setPostparPage(String postparPage) {
		this.postparPage = postparPage;
	}

	public String getPostparResultsPerPage() {
		return postparResultsPerPage;
	}

	public void setPostparResultsPerPage(String postparResultsPerPage) {
		this.postparResultsPerPage = postparResultsPerPage;
	}

	public String getPostparText() {
		return postparText;
	}

	public void setPostparText(String postparText) {
		this.postparText = postparText;
	}

	public String getPostparTextLang() {
		return postparTextLang;
	}

	public void setPostparTextLang(String postparTextLang) {
		this.postparTextLang = postparTextLang;
	}

	public String getPostparInFilename() {
		return postparInFilename;
	}

	public void setPostparInFilename(String postparInFilename) {
		this.postparInFilename = postparInFilename;
	}

	public String getPostparInFilePath() {
		return postparInFilePath;
	}

	public void setPostparInFilePath(String postparInFilePath) {
		this.postparInFilePath = postparInFilePath;
	}

	public String getPostparDocUrl() {
		return postparDocUrl;
	}

	public void setPostparDocUrl(String postparDocUrl) {
		this.postparDocUrl = postparDocUrl;
	}

	public String getPostparNumKeywords() {
		return postparNumKeywords;
	}

	public void setPostparNumKeywords(String postparNumKeywords) {
		this.postparNumKeywords = postparNumKeywords;
	}

	public String getPostparNumTopics() {
		return postparNumTopics;
	}

	public void setPostparNumTopics(String postparNumTopics) {
		this.postparNumTopics = postparNumTopics;
	}

	public String getPostparEntityType() {
		return postparEntityType;
	}

	public void setPostparEntityType(String postparEntityType) {
		this.postparEntityType = postparEntityType;
	}

	public String getPostparAnalysis() {
		return postparAnalysis;
	}

	public void setPostparAnalysis(String postparAnalysis) {
		this.postparAnalysis = postparAnalysis;
	}

	public String getPostparTextType() {
		return postparTextType;
	}

	public void setPostparTextType(String postparTextType) {
		this.postparTextType = postparTextType;
	}

	@Override
	public Object getAttribute(String arg0) {
		return null;
	}

	@Override
	public Enumeration getAttributeNames() {
		return null;
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public int getContentLength() {
		return 0;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public String getLocalAddr() {
		return null;
	}

	@Override
	public String getLocalName() {
		return null;
	}

	@Override
	public int getLocalPort() {
		return 0;
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public Enumeration getLocales() {
		return null;
	}

	@Override
	public String getParameter(String arg0) {
		if (arg0 == HomerConstants.POSTPAR_TEXTTYPE) {
			return postparTextType;
		}
		if (arg0 == HomerConstants.POSTPAR_TEXTEXTRACT) {
			return postparTextExtraction;
		}
		if (arg0 == HomerConstants.POSTPAR_ANALYSIS) {
			return postparAnalysis;
		}
		if (arg0 == HomerConstants.POSTPAR_ENTITYTYPES) {
			return postparEntityType;
		}
		if (arg0 == HomerConstants.POSTPAR_PAGE) {
			return postparPage;
		}
		if (arg0 == HomerConstants.POSTPAR_RESULTSPERPAGE) {
			return postparResultsPerPage;
		}
		if (arg0 == HomerConstants.POSTPAR_TEXT) {
			return postparText;
		}
		if (arg0 == HomerConstants.POSTPAR_TEXTLANG) {
			return postparTextLang;
		}
		if (arg0 == HomerConstants.POSTPAR_INFILENAME) {
			return postparInFilename;
		}
		if (arg0 == HomerConstants.POSTPAR_INFILEPATH) {
			return postparInFilePath;
		}
		if (arg0 == HomerConstants.POSTPAR_DOCURL) {
			return postparDocUrl;
		}
		if (arg0 == HomerConstants.POSTPAR_NUMKEYWORDS) {
			return postparNumKeywords;
		}
		if (arg0 == HomerConstants.POSTPAR_NUMTOPICS) {
			return postparNumTopics;
		}
		return null;
	}

	@Override
	public Map getParameterMap() {
		return null;
	}

	@Override
	public Enumeration getParameterNames() {
		return null;
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return null;
	}

	@Override
	public String getProtocol() {
		return null;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return null;
	}

	@Override
	public String getRealPath(String arg0) {
		return null;
	}

	@Override
	public String getRemoteAddr() {
		return null;
	}

	@Override
	public String getRemoteHost() {
		return null;
	}

	@Override
	public int getRemotePort() {
		return 0;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return null;
	}

	@Override
	public String getScheme() {
		return null;
	}

	@Override
	public String getServerName() {
		return null;
	}

	@Override
	public int getServerPort() {
		return 0;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public void removeAttribute(String arg0) {
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {

	}

	@Override
	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
	}

	@Override
	public String getAuthType() {
		return null;
	}

	@Override
	public String getContextPath() {
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		return null;
	}

	@Override
	public long getDateHeader(String arg0) {
		return 0;
	}

	@Override
	public String getHeader(String arg0) {
		return null;
	}

	@Override
	public Enumeration getHeaderNames() {
		return null;
	}

	@Override
	public Enumeration getHeaders(String arg0) {
		return null;
	}

	@Override
	public int getIntHeader(String arg0) {
		return 0;
	}

	@Override
	public String getMethod() {
		return null;
	}

	@Override
	public String getPathInfo() {
		return null;
	}

	@Override
	public String getPathTranslated() {
		return null;
	}

	@Override
	public String getQueryString() {
		return null;
	}

	@Override
	public String getRemoteUser() {
		return null;
	}

	@Override
	public String getRequestURI() {
		return null;
	}

	@Override
	public StringBuffer getRequestURL() {
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		return null;
	}

	@Override
	public String getServletPath() {
		return null;
	}

	@Override
	public HttpSession getSession() {
		return null;
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return false;
	}

	public String getPostparTextExtraction() {
		return postparTextExtraction;
	}

	public void setPostparTextExtraction(String postparTextExtraction) {
		this.postparTextExtraction = postparTextExtraction;
	}

}
