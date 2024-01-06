package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CmdStats {
    public CmdStats (SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();
        Responses.publishStats(event);
    }
}
