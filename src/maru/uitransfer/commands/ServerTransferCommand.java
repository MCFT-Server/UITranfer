package maru.uitransfer.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.utils.TextFormat;
import maru.uitransfer.Main;

public class ServerTransferCommand extends PluginCommand<Main> {

	public ServerTransferCommand(String name, Main owner) {
		super(name, owner);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TextFormat.RED + "�� ��ɾ�� ���� �������� �Է� �����մϴ�.");
			return true;
		}
		this.getPlugin().getEventListener().showTransferForm((Player)sender);
		return true;
	}
}
