import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;

//https://github.com/cr5315/jSmite

public class Tester {

	long nib = 91004988247011328l;
	long sal = 276224945976115200l;
	long mon = 277916961999028224l;

	long botChat = 333382132988641281l;
	long botLogChat = 334154029091389449l;
	
	String prefix = "/";
	
	boolean log = true;
	IChannel logChannel;

	public Tester() {
		IDiscordClient bot = createClient("MzMzMzg5MTgxNjQ2NjY3Nzc2.DEL8bA.AFV9C1bCj6ocaPa8N0Nd8mbT4ys", true);

		while (!bot.isReady()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Bot is ready to please you master");
		
		logChannel = bot.getChannelByID(botLogChat);
		
		for (IGuild g : bot.getGuilds())
			System.out.println(g + "  :  " + g.getName());
		
		for (IChannel g : bot.getChannels())
			System.out.println(g + "  :  " + g.getName());

		EventDispatcher dis = bot.getDispatcher();

		dis.registerListener(new IListener<Event>() {

			@Override
			public void handle(Event event) {

				if (event instanceof MessageReceivedEvent) {

					MessageReceivedEvent ev = ((MessageReceivedEvent) event);
					IChannel channel = ev.getChannel();
					IUser author = ev.getMessage().getAuthor();
					String message = ev.getMessage().getContent();
					
					String[] command = message.split(" ");
					
					if (command[0].startsWith(prefix)){
						
						System.out.println(message);
						
						if (log)
							logChannel.sendMessage(author + " sent: " + message + "   in: " + channel);

						if (command[0].equalsIgnoreCase(prefix + "elo")){
							
							if (command.length == 1) {
								send(channel, ".elo username [gamemode]");
								return;
							}
	
							String luser = command[1];// user
							
							List<GameType> stats = getGameInfo(luser);
							
							if (stats.isEmpty()) {
								send(channel, "User is fake news");
								return;
							}
							
							if (command.length == 2) {
								send(channel, embedAll(stats , luser));
								return;
							} else {
								try {
									Modes mode = Modes.valueOf(formatMode(command[2].toLowerCase()));
									send(channel, embedOne(stats.get(mode.val) , luser));
								} catch (Exception e) {
									send(channel, "Invalid Mode");
								}
							}
						}
						else if (command[0].equalsIgnoreCase(prefix + "insult")) {
							
							if (command.length >= 2) {
								
								try {
									send(channel, bot.getUserByID(strip(command[1])).mention() + " is a " + getInsult());
								}
								catch(Exception e) {
									e.printStackTrace();
									
									//if they don't use @
									for (IUser user : ev.getGuild().getUsers()) {
										if (user.getName().equalsIgnoreCase(command[1]) || user.getDisplayName(ev.getGuild()).equalsIgnoreCase(command[1])) {
											send(channel, user.mention() + " is a " + getInsult());
											break;
										}
									}
								}
								
							}
							else
								send(channel, "/tts " + author.mention() + " is a " + getInsult());
						}
						else if (command[0].equalsIgnoreCase(prefix + "random"))
							send(channel, getImage());
						else if (command[0].equalsIgnoreCase(prefix + "setPrefix "))
							prefix = command[1];
						else if (command[0].equalsIgnoreCase(prefix + "help"))
							send(channel, "go fuck yourself");
					}
				}
			}

		});

	}
	
	protected long strip(String s) {
		
		char[] chars = s.toCharArray();
		String l = "";
		
		for (char c : chars)
			if (c > 47 && c < 58)
				l += c;
		
		return Long.parseLong(l);
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
	
	protected EmbedObject embedAll(List<GameType> stats, String user) {
		EmbedBuilder eb = new EmbedBuilder();

		eb.withDescription("[" + user + "'s ELO](https://smite.guru/profile/pc/" + user + "/casual)");
	    eb.withColor(new Color(245,160,69));
		
		for (GameType stat : stats) {
		    eb.appendField(stat.getMode() + ": " + stat.getElo(), "W/L: " + stat.getWinLoss() + "%", true);
		}
		return eb.build();
	}
	
	protected EmbedObject embedOne(GameType stat, String user) {
		EmbedBuilder eb = new EmbedBuilder();

		eb.withDescription("[" + user + "'s ELO](https://smite.guru/profile/pc/" + user + "/casual)");
	    eb.withColor(new Color(245,160,69));
		
	    eb.appendField(stat.getMode() + ": " + stat.getElo(), "W/L: " + stat.getWinLoss() + "%", true);
		return eb.build();
	}

	//Capitalizes first letter
	protected String formatMode(String mode) {
		char[] m = mode.toCharArray();
		if (m[0] > 96)
			m[0] = (char) (m[0] + (65 - 97));
		return new String(m);
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

	protected static String getInfoHTML(String user) {
		URL url;
		try {
			url = new URL("http://smite.guru/profile/pc/" + user + "/casual");

			URLConnection hc = url.openConnection();
			hc.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

			// Read all the text returned by the server
			BufferedReader in = new BufferedReader(new InputStreamReader(hc.getInputStream()));

			Scanner scan = new Scanner(in);
			String s = scan.nextLine();
			scan.close();
			in.close();
			return s;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	// and win loss
	protected static List<GameType> getGameInfo(String user) {

		List<GameType> elos = new ArrayList<GameType>();

		String html = getInfoHTML(user).trim();

		while (html.contains("\"lg-text\">")) {
			html = html.substring(html.indexOf("\"lg-text\">") + "\"lg-text\">".length());

			String val = html.substring(0, html.indexOf("</span>"));

			if (val.contains("%"))
				elos.get(elos.size() - 1).setWinLoss(val);
			else {
				elos.add(new GameType(val, Modes.valToMode(elos.size())));
			}
		}

		return elos;

	}

	protected static String getInsult() {

		try {
			// Create a URL for the desired page
			URL url = new URL("http://www.robietherobot.com/insult-generator.htm");

			// Read all the text returned by the server
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			Scanner scan = new Scanner(in);

			while (scan.hasNextLine()) {

				String nextLine = scan.nextLine().trim();

				// find line before insult
				if (nextLine.startsWith(
						"<td width=\"100%\" align=\"center\" valign=\"middle\"><h2>Call them a...</h2><h1>")) {
					String insult = scan.nextLine().trim();
					insult = insult.substring(0, insult.length() - 5);
					scan.close();
					in.close();
					return insult;
				}
			}

			scan.close();
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Ur a butt";
	}
	
	protected static EmbedObject getImage() {
		URL url;
		try {
			url = new URL("http://imgur.com/random");

			URLConnection hc = url.openConnection();
			//hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

			// Read all the text returned by the server
			BufferedReader in = new BufferedReader(new InputStreamReader(hc.getInputStream()));

			Scanner scan = new Scanner(in);
			
			while (scan.hasNextLine()) {
				
				String next = scan.nextLine();
				
				//System.out.println(next);
				
				String prefix = "href=\"http://i.imgur.com/";
				if (next.contains(prefix)) {
					
					//System.out.println(next);
					next = next.substring(next.indexOf(prefix) + prefix.length(), next.indexOf("\"/>"));
					System.out.println(next);
					
					/*char[] c = next.toCharArray();
					
					for (int i=c.length-1; i>0; i--)
						if (c[i] == 'b') {
							next = "http:" + next.substring(0,i) + next.substring(i+1);
						}*/
					
					scan.close();
					in.close();
					
					//System.out.println(next);
					EmbedBuilder e = new EmbedBuilder();
					e.withColor(new Color(245,160,69));
					e.withImage("http://i.imgur.com/" + next);
					//e.appendField("image: ", next, true);
					return e.build();
				}
				
				//System.out.println(next);
			}
			
			System.out.println("done");
			
			scan.close();
			in.close();
			//return s;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		new Tester();
	}

}

class GameType {

	String elo = "";
	String winLoss = "";
	Modes mode;

	public GameType(String elo, Modes mode) {
		setElo(elo);
		setMode(mode);
	}

	public Modes getMode() {
		return mode;
	}

	public void setMode(Modes mode) {
		this.mode = mode;
	}

	public String getElo() {
		return elo;
	}

	public void setElo(String elo) {
		this.elo = elo;
	}

	public String getWinLoss() {
		return winLoss;
	}

	public void setWinLoss(String winLoss) {
		this.winLoss = winLoss;
	}

	@Override
	public String toString() {
		return mode + "{Elo: " + getElo() + "  W/L: " + getWinLoss() + "}";
	}

}

enum Modes {

	Arena(0), Assault(1), Joust(2), Siege(3), Conquest(4), Clash(5), Average(6);

	int val;

	Modes(int val) {
		this.val = val;
	}

	static Modes valToMode(int val) {
		for (Modes m : Modes.values())
			if (m.val == val)
				return m;
		return null;
	}

	int getIndex() {
		return val;
	}

}
