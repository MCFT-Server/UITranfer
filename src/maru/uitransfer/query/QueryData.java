package maru.uitransfer.query;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class QueryData {
	public boolean status;
	public String hostname;
	public int port;
	public String protocol;
	public String version;
	public String software;
	public String game_type;
	public String game_name;
	public String motd;
	public String map;
	public long onlinePlayers;
	public long maxPlayers;
	public JsonArray list;
	public JsonArray plugins;
	public boolean cached;
	
	public QueryData(String json) {
		JsonParser parser = new JsonParser();
		JsonElement data = parser.parse(json);
		
		JsonObject obj = data.getAsJsonObject();
		
		this.status = obj.get("status").getAsBoolean();
		this.hostname = obj.get("hostname").getAsString();
		this.port = obj.get("port").getAsInt();
		this.protocol = obj.get("protocol").getAsString();

		if (status != false) {
			this.version = obj.get("version").getAsString();
			this.software = obj.get("software").getAsString();
			this.game_type = obj.get("game_type").getAsString();
			this.game_name = obj.get("game_name").getAsString();
			this.motd = obj.get("motd").getAsString();
			this.map = obj.get("map").getAsString();
			this.onlinePlayers = obj.get("players").getAsJsonObject().get("online").getAsLong();
			this.maxPlayers = obj.get("players").getAsJsonObject().get("max").getAsLong();
			if (obj.get("list").isJsonArray()) {
				this.list = obj.get("list").getAsJsonArray();
			}
			if (obj.get("plugins").isJsonArray()) {
				this.plugins = obj.get("plugins").getAsJsonArray();
			}
			this.cached = obj.get("cached").getAsBoolean();
		}
	}
}
