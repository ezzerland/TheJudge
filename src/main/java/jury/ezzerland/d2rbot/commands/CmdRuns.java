package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class CmdRuns {

    public CmdRuns(SlashCommandInteractionEvent event) {
        //migrated to responses to keep all language in that location and avoid doubling iterations
        Responses.amountOfActiveRuns(event);
        //event.reply(Responses.amountOfActiveRuns()).setEphemeral(true).queue();
    }
}
