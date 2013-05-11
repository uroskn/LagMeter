package main.java.com.webkonsept.minecraft.lagmeter.eventhandlers;

import main.java.com.webkonsept.minecraft.lagmeter.LagMeter;
import main.java.com.webkonsept.minecraft.lagmeter.events.HighLagEvent;
import main.java.com.webkonsept.minecraft.lagmeter.listeners.LagListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DefaultHighLag implements LagListener{
	private final LagMeter plugin;

	public DefaultHighLag(LagMeter plugin){
		this.plugin = plugin;
	}

	@Override
	public void onHighLagEvent(HighLagEvent evt){
		final Player[] players = Bukkit.getServer().getOnlinePlayers();
		for(final Player p: players)
			if(this.plugin.permit(p, "lagmeter.notify.lag")||p.isOp())
				p.sendMessage(ChatColor.GOLD+"[LagMeter] "+ChatColor.RED+"The server's TPS has dropped below "+this.plugin.getTpsNotificationThreshold()+"! If you configured a server command to execute at this time, it will run now.");
		this.plugin.severe("The server's TPS has dropped below "+this.plugin.getTpsNotificationThreshold()+"! Executing command (if configured).");
		if(this.plugin.getLagCommand().contains(";"))
			for(final String cmd: this.plugin.getLagCommand().split(";"))
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceFirst("/", ""));
		else
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), this.plugin.getLagCommand().replaceFirst("/", ""));
	}
}