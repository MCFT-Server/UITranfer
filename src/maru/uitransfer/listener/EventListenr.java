package maru.uitransfer.listener;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.ConfigSection;
import maru.uitransfer.Main;

public class EventListenr implements Listener {
	private Main plugin;
	
	private List<Integer> requestList = new ArrayList<>();
	
	public EventListenr(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.plugin.getServer().getScheduler().scheduleDelayedTask(new Task() {
			@Override
			public void onRun(int currentTick) {
				showTransferForm(event.getPlayer());
			}
		}, 20 * 3); 
	}
	
	@EventHandler
	public void onFormResponse(PlayerFormRespondedEvent event) {
		if (requestList.contains(event.getFormID())) {
			FormResponseSimple response = (FormResponseSimple) event.getResponse();
			Player player = event.getPlayer();
			if (response == null) {
				showTransferForm(player);
				return;
			}
			ElementButton button = response.getClickedButton();
			
			String address = this.plugin.getConfig().getSection("list").getString(button.getText());
			String[] iport = address.split(":");
			InetSocketAddress socket = new InetSocketAddress(iport[0], (iport.length < 2) ? 19132 : Integer.parseInt(iport[1]));
			player.transfer(socket);
			
			requestList.remove(event.getFormID());
		}
	}
	
	public void onPlayerTouch(PlayerInteractEvent event) {
		event.setCancelled();
		showTransferForm(event.getPlayer());
	}
	
	public void showTransferForm(Player player) {
		FormWindowSimple form = new FormWindowSimple("서버 목록", "접속할 서버를 선택하세요.");
		ConfigSection section = this.plugin.getConfig().getSection("list");
		for (Entry<String, Object> entry : section.entrySet()) {
			form.addButton(new ElementButton(entry.getKey()));
		}
		
		int id = player.showFormWindow(form);
		requestList.add(id);
	}
}
