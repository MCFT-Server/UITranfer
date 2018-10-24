package maru.uitransfer;

import cn.nukkit.plugin.PluginBase;
import maru.uitransfer.commands.ServerTransferCommand;
import maru.uitransfer.listener.EventListenr;

public class Main extends PluginBase {
	private EventListenr listener;
	
	@Override
	public void onEnable() {
		this.saveResource("config.yml");
		
		this.listener = new EventListenr(this);
		this.getServer().getPluginManager().registerEvents(listener, this);
		
		this.registerCommands();
	}
	
	public EventListenr getEventListener() {
		return this.listener;
	}
	
	public void registerCommands() {
		this.getServer().getCommandMap().register("ServerTransfer", new ServerTransferCommand("서버이동", this));
	}
}
