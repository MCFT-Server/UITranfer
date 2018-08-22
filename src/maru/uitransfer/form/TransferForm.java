package maru.uitransfer.form;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.ConfigSection;
import maru.uitransfer.query.Query;
import maru.uitransfer.query.QueryData;

public class TransferForm {
	private FormWindowSimple form;
	private Plugin plugin;
	private Map<Integer, String> extraButtonData = new HashMap<>();
	private int button_id = 0;

	public TransferForm(String title, String content, Plugin plugin) {
		this.form = new FormWindowSimple(title, content);
		this.plugin = plugin;
		drawUI();
	}
	private void drawUI() {
		ConfigSection section = plugin.getConfig().getSection("list");
		Map<Object, String> online = new HashMap<>();
		
		section.entrySet().parallelStream().forEach(entry -> {
			String[] iport = entry.getValue().toString().split(":");
			Query query = new Query(iport[0], (iport.length < 2) ? 19132 : Integer.parseInt(iport[1]));
			QueryData data = query.send();
			String msg = data.status ? "접속자 수: (" + data.onlinePlayers + "/" + data.maxPlayers + ")" : "서버 OFF";
			online.put(entry.getKey(), msg);
		});
		
		for (Entry<String, Object> entry : section.entrySet()) {
			form.addButton(new ElementButton(entry.getKey() + "\n" + online.get(entry.getKey())));
			extraButtonData.put(button_id++, entry.getValue().toString());
		}
	}

	public String getButtonData(int id) {
		return this.extraButtonData.get(id);
	}
	
	public FormWindowSimple getForm() {
		return this.form;
	}
}
