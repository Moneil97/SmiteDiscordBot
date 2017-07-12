import java.awt.Color;
import java.util.List;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;

@SuppressWarnings("unused")
public class DiscordManager {
	
	private long nib = 91004988247011328l;
	private long sal = 276224945976115200l;
	private long mon = 277916961999028224l;

	private long botChat = 333382132988641281l;
	private long botLogChat = 334154029091389449l;
	
	private boolean log = true;
	private IChannel logChannel;
	
	private EventDispatcher dispatch;

	public DiscordManager(String token, boolean login) {
		
		IDiscordClient bot = createClient(token, login);

		while (!bot.isReady()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		logChannel = bot.getChannelByID(botLogChat);
		
		System.out.println("Bot is ready to please you master");
		
		
		/*
		for (IGuild g : bot.getGuilds())
			System.out.println(g + "  :  " + g.getName());
		
		for (IChannel g : bot.getChannels())
			System.out.println(g + "  :  " + g.getName());
		*/
		
		dispatch = bot.getDispatcher();
		
	}
	
	protected EventDispatcher getDispatcher() {
		return dispatch;
	}

	protected static IDiscordClient createClient(String token, boolean login) { // Returns a new instance of the Discord
		// client
		ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
		clientBuilder.withToken(token); // Adds the login info to the builder
		try {
			if (login) {
				return clientBuilder.login(); // Creates the client instance and logs the client in
			} else {
				return clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you
				// would have to call client.login() yourself
			}
		} catch (DiscordException e) { // This is thrown if there was a problem building the client
			e.printStackTrace();
			return null;
		}
	}
	
	protected void send(IChannel channel, String message) {
		channel.sendMessage(message);
		if (log)
			logChannel.sendMessage(message);
	}
	
	protected void send(IChannel channel, EmbedObject embed) {
		channel.sendMessage(embed);
		if (log)
			logChannel.sendMessage(embed);
	}
	
	protected void sendEmbedAll(IChannel channel, List<GameType> stats, String user) {
		EmbedBuilder eb = new EmbedBuilder();

		eb.withDescription("[" + user + "'s ELO](https://smite.guru/profile/pc/" + user + "/casual)");
	    eb.withColor(new Color(245,160,69));
		
		for (GameType stat : stats) {
		    eb.appendField(stat.getMode() + ": " + stat.getElo(), "W/L: " + stat.getWinLoss() + "%", true);
		}

		send(channel, eb.build());
	}
	
	protected void sendEmbedOne(IChannel channel, GameType stat, String user) {
		EmbedBuilder eb = new EmbedBuilder();

		eb.withDescription("[" + user + "'s ELO](https://smite.guru/profile/pc/" + user + "/casual)");
	    eb.withColor(new Color(245,160,69));
		
	    eb.appendField(stat.getMode() + ": " + stat.getElo(), "W/L: " + stat.getWinLoss() + "%", true);
		
		send(channel, eb.build());
	}
	
	protected long strip(String s) {
		
		char[] chars = s.toCharArray();
		String l = "";
		
		for (char c : chars)
			if (c > 47 && c < 58)
				l += c;
		
		return Long.parseLong(l);
	}

}
