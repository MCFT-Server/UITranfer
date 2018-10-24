package maru.uitransfer.listener;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import maru.uitransfer.Main;
import maru.uitransfer.form.TransferForm;
import maru.uitransfer.query.Query;
import maru.uitransfer.query.QueryData;

public class EventListenr implements Listener {
	private Main plugin;

	private Map<Integer, TransferForm> requestList = new HashMap<>();

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
		if (requestList.containsKey(event.getFormID())) {
			FormResponseSimple response = (FormResponseSimple) event.getResponse();
			TransferForm form = requestList.get(event.getFormID());
			Player player = event.getPlayer();
			if (response == null) {
				if (this.plugin.getConfig().getBoolean("joinTransfer")) {
					player.kick(TextFormat.DARK_AQUA + "서버를 선택하지 않아서 강퇴처리되었습니다.", false);
				}
				return;
			}
			String address = form.getButtonData(response.getClickedButtonId());
			String[] iport = address.split(":");
			InetSocketAddress socket = new InetSocketAddress(iport[0],
					(iport.length < 2) ? 19132 : Integer.parseInt(iport[1]));
			player.transfer(socket);

			requestList.remove(event.getFormID());
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (this.plugin.getConfig().getBoolean("joinTransfer")) {
			Player player = event.getPlayer();
			event.setCancelled();
			player.sendTip("UI가 보이지 않는다면 /서버이동 명령어를 입력해주세요");
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
				event.setPlayerCount((int) (event.getPlayerCount() + ((data == null) ? 0 : data.onlinePlayers)));
				event.setMaxPlayerCount((int) (event.getMaxPlayerCount() + ((data == null) ? 0 : data.maxPlayers) + 1));
			});
		}
	}

	public void showTransferForm(Player player) {
		Server.getInstance().getScheduler().scheduleAsyncTask(plugin, new AsyncTask() {

			@Override
			public void onRun() {
				TransferForm form = new TransferForm("서버 목록", "접속할 서버를 선택하세요.", plugin);

				int id = player.showFormWindow(form.getForm());
				requestList.put(id, form);
			}
		});
	}

}