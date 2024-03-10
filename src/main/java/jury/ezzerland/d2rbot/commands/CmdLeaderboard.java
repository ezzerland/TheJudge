package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CmdLeaderboard {
    public CmdLeaderboard (SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        Responses.publishMonthlyLeaderboard(event);
        //event.getHook().sendMessageEmbeds(Responses.publishLeaderboard()).setEphemeral(true).queue();
    }
}
