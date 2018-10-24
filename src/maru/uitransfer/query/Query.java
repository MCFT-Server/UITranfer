package maru.uitransfer.query;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Query {
	private String ip;
	private int port;
	
	public Query(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public QueryData send() {
		try {
			String json = this.sendGet("https://use.gameapis.net/mcpe/query/extensive/"+ this.ip + ":" + this.port);
			return new QueryData(json);
		} catch (Exception e) {
			return null;
		}
	}
	
    private String sendGet(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //Request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        //int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
