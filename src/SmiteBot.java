import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class SmiteBot {

	String prefix = "/";

	boolean log = true;
	IChannel logChannel;

	public SmiteBot() {

		DiscordManager dickSword = new DiscordManager("MzMzMzg5MTgxNjQ2NjY3Nzc2.DEL8bA.AFV9C1bCj6ocaPa8N0Nd8mbT4ys",
				true);

		dickSword.getDispatcher().registerListener(new IListener<Event>() {

			@Override
			public void handle(Event event) {

				if (event instanceof MessageReceivedEvent) {

					MessageReceivedEvent ev = ((MessageReceivedEvent) event);
					IChannel channel = ev.getChannel();
					IUser author = ev.getMessage().getAuthor();
					String message = ev.getMessage().getContent();

					String[] command = message.split(" ");

					if (command[0].startsWith(prefix)) {

						System.out.println(message);

						if (log)
							logChannel.sendMessage(author + " sent: " + message + "   in: " + channel);

						if (command[0].equalsIgnoreCase(prefix + "elo")) {

							if (command.length == 1) {
								dickSword.send(channel, ".elo username [gamemode]");
								return;
							}

							String luser = command[1];// user

							//get stats

							/*if (stats.isEmpty()) {
								dickSword.send(channel, "User is fake news");
								return;
							}*/

							if (command.length == 2) {
								//Send Embed all
								return;
							} 
							else {
								try {
									//send Embed one
								} catch (Exception e) {
									dickSword.send(channel, "Invalid Mode");
								}
							}
						}
					}
				}
			}

		});
	}

	// Capitalizes first letter
	protected String formatMode(String mode) {
		char[] m = mode.toCharArray();
		if (m[0] > 96)
			m[0] = (char) (m[0] + (65 - 97));
		return new String(m);
	}

	public static void main(String[] args) {
		new Tester();
	}

}
