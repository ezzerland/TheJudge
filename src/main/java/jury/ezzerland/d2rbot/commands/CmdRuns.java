package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;


public class CmdRuns {

    public CmdRuns(SlashCommandInteractionEvent event) {
        //migrated to responses to keep all language in that location and avoid doubling iterations
        event.deferReply().setEphemeral(true).queue();
        Responses.amountOfActiveRuns(event.getHook());
        //event.reply(Responses.amountOfActiveRuns()).setEphemeral(true).queue();
    }
    public CmdRuns(ButtonInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();
        Responses.amountOfActiveRuns(event.getHook());
    }
}
