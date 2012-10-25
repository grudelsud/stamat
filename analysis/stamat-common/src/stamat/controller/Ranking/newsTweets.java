package stamat.controller.Ranking;



import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;


public class NewsTweets {

	Map<String,String> mpTweets;
	Map<String,String> mpNews;


	public  NewsTweets() throws IOException, ParseException{
		// import tweets
		mpTweets = createTweets();

		//create tweets index
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
		IndexWriter w = new IndexWriter(index, config);

		Set<Entry<String, String>> sTweets=mpTweets.entrySet();
		Iterator<Entry<String, String>> itTweets=sTweets.iterator();

		while(itTweets.hasNext())
		{
			// key=value separator this by Map.Entry to get key and value
			Map.Entry m =(Map.Entry)itTweets.next();
			// getValue is used to get value of key in Map
			String tweet=(String)m.getValue();
			// add tweets to the index
			addDoc(w, tweet);
		}
		w.close();

		// import news
		mpNews = createNews();
		Set<Entry<String, String>> sNews=mpNews.entrySet();
		Iterator<Entry<String, String>> itNews=sNews.iterator();

		HashMap<String,Float> mScore = new HashMap<String,Float>();
		ValueComparator bvc =  new ValueComparator(mScore);
		TreeMap<String,Float> sortedScore = new TreeMap<String,Float>(bvc);


		while(itNews.hasNext())
		{
			Map.Entry m =(Map.Entry)itNews.next();
			String idNews=(String)m.getKey();
			String titleNews=(String)m.getValue();
			float scoreNews=computeTweetsRelevance(titleNews,index,analyzer);
			mScore.put(idNews,scoreNews);
		}

		sortedScore.putAll(mScore);

		System.out.println("results: "+sortedScore);

	}


	class ValueComparator implements Comparator<String> {

		Map<String, Float> base;
		public ValueComparator(Map<String, Float> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with equals.    
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}

	private void addDoc(IndexWriter w, String value) throws IOException {
		Document doc = new Document();
		doc.add(new Field("title", value, Field.Store.YES, Field.Index.ANALYZED));
		w.addDocument(doc);
	}
	private  float computeTweetsRelevance(String queryStr,Directory index, StandardAnalyzer analyzer) throws IOException, ParseException {
		// the "title" arg specifies the default field to use
		// when no field is explicitly specified in the query.
		Query q = new QueryParser(Version.LUCENE_35, "title", analyzer).parse(queryStr);

		int hitsPerPage = 10;
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		// 4. display results
		System.out.println("Found " + hits.length + " hits.");
		float totScore=0;
		for(int i=0;i<hits.length;++i) {
			int docId = hits[i].doc;
			float docScore = hits[i].score;
			totScore = totScore + docScore;
			Document d = searcher.doc(docId);
			//System.out.println((i + 1) + ". " + d.get("title") + " " + docScore);
		}

		//System.out.println("Score totale: " + totScore);
		// searcher can only be closed when there
		// is no need to access the documents any more. 
		searcher.close();

		return totScore;

	}


	public Map<String,String> createTweets(){
		Map<String,String> mp=new HashMap<String, String>();

		// adding or set elements in Map by put method key and value pair
		mp.put("2", "Ordini un sushi online... ed è già qui. Come? Con la nuova rete veloce #4G di Vodafone #Vodafone4G http://youtu.be/bUgF2pSYI3c  http://4g.vodafone.it ");
		mp.put("1", "Temporeggiano le #borse europee in attesa dei dati #USA: sussidi di disoccupazione; ordini dei beni durevoli e vendite di immobili in corso.");
		mp.put("3", "Cresce l’#eCommerce B2C in Italia con un tasso del 19% http://www.netpropaganda.net/cresce-ecommerce-b2c-1025.html … #shopping #online #web");
		mp.put("4", "Speriamo che la vostra su http://www.adviseonly.com  non sia tra queste: Le #password più usate su #internet. http://huff.to/QHHG9Z  #internet #web");
		mp.put("5", "Da oggi LTE è una realtà! La rivoluzione che stavamo aspettando è arrivata con TIM #TimLte #Tim4G");
		return mp;
	}

	public Map<String,String> createNews(){
		Map<String,String> mp=new HashMap<String, String>();

		// adding or set elements in Map by put method key and value pair
		mp.put("2", "Two cani");
		mp.put("1", "Come si mangia il sushi");
		mp.put("3", "Three topi cani Three");
		mp.put("4", "Four elefanti");
		mp.put("5", "Cinque canguri");
		return mp;
	}

}
