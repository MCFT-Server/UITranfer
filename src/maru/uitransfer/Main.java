package maru.uitransfer;

import cn.nukkit.plugin.PluginBase;
import maru.uitransfer.listener.EventListenr;

public class Main extends PluginBase {
	private EventListenr listener;
	
	@Override
	public void onEnable() {
		this.saveResource("config.yml");
		
		this.listener = new EventListenr(this);
		this.getServer().getPluginManager().registerEvents(listener, this);
	}
}
