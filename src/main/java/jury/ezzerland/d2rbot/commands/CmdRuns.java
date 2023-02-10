package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class CmdRuns {

    public CmdRuns(SlashCommandInteractionEvent event) {
        event.reply(Responses.amountOfActiveRuns()).setEphemeral(true).queue();
    }
}
