package maru.uitransfer.listener;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import maru.uitransfer.Main;
import maru.uitransfer.query.Query;
import maru.uitransfer.query.QueryData;

public class EventListenr implements Listener {
	private Main plugin;
	
	private List<Integer> requestList = new ArrayList<>();
	
	public EventListenr(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (this.plugin.getConfig().getBoolean("joinTransfer")) {
			this.plugin.getServer().getScheduler().scheduleDelayedTask(new Task() {
				@Override
				public void onRun(int currentTick) {
					showTransferForm(event.getPlayer());
				}
			}, 20 * 3); 
		}
	}
	
	@EventHandler
	public void onFormResponse(PlayerFormRespondedEvent event) {
		if (requestList.contains(event.getFormID())) {
			FormResponseSimple response = (FormResponseSimple) event.getResponse();
			Player player = event.getPlayer();
			if (response == null) {
				if (this.plugin.getConfig().getBoolean("joinTransfer")) {
					player.kick(TextFormat.DARK_AQUA + "������ �������� �ʾƼ� ����ó���Ǿ����ϴ�.", false);
				}
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
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (this.plugin.getConfig().getBoolean("joinTransfer")) {
			Player player = event.getPlayer();
			event.setCancelled();
			player.sendTip("UI�� ������ �ʴ´ٸ� /�����̵� ��ɾ �Է����ּ���");
		}
	}
	
	@EventHandler
	public void onQueryReceived(QueryRegenerateEvent event) {
		if (this.plugin.getConfig().getBoolean("joinTransfer")) {
			ConfigSection section = plugin.getConfig().getSection("list");
			event.setMaxPlayerCount(0);
			event.setPlayerCount(0);
			section.entrySet().parallelStream().forEach(entry -> {
				String[] iport = entry.getValue().toString().split(":");
				Query query = new Query(iport[0], (iport.length < 2) ? 19132 : Integer.parseInt(iport[1]));
				QueryData data = query.send();
				event.setPlayerCount((int) (event.getPlayerCount() + data.onlinePlayers));
				event.setMaxPlayerCount((int) (event.getMaxPlayerCount() + data.maxPlayers));
			});
		}
	}
	
	public void showTransferForm(Player player) {
		Server.getInstance().getScheduler().scheduleAsyncTask(plugin, new AsyncTask() {
			
			@Override
			public void onRun() {
				FormWindowSimple form = new FormWindowSimple("���� ���", "������ ������ �����ϼ���.");
				ConfigSection section = plugin.getConfig().getSection("list");
				Map<Object, String> online = new HashMap<>();
				
				section.entrySet().parallelStream().forEach(entry -> {
					String[] iport = entry.getValue().toString().split(":");
					Query query = new Query(iport[0], (iport.length < 2) ? 19132 : Integer.parseInt(iport[1]));
					QueryData data = query.send();
					String msg = data.status ? "������ ��: (" + data.onlinePlayers + "/" + data.maxPlayers + ")" : "���� OFF";
					online.put(entry.getKey(), msg);
				});
				
				for (Entry<String, Object> entry : section.entrySet()) {
					form.addButton(new ElementButton(entry.getKey() + "\n" + online.get(entry.getKey())));
				}
				
				int id = player.showFormWindow(form);
				requestList.add(id);
			}
		});
	}
	
}
