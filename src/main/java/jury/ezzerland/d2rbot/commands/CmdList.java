package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import jury.ezzerland.d2rbot.components.RunType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.HashSet;
import java.util.Set;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdList {

    public CmdList (ButtonInteractionEvent event, boolean ladder, String t) {
        if (BOT.getParticipants().size() == 0) {
            event.reply(Responses.noActiveRuns()).setEphemeral(true).queue();
            return;
        }
        RunType type = RunType.valueOf(t);
        Set<Run> runs = new HashSet<>();
        if (ladder) {
            runs.addAll(BOT.getLadder().get(type));
        } else {
            runs.addAll(BOT.getNonLadder().get(type));
        }
        if (runs.size() == 0) {
            event.reply(Responses.noActiveRunsOfType(ladder, type.getTypeAsString(type))).setEphemeral(true).queue();
            return;
        }
        event.deferReply().setEphemeral(true).queue();
        for (Run run : runs) {
            if (run.isFull()) {
                event.getHook().sendMessageEmbeds(Responses.gameInfo(run, true)).setEphemeral(true).queue();
            } else {
                event.getHook().sendMessageEmbeds(Responses.gameInfo(run, true)).addActionRow(Responses.joinButton(run.getHost().getId())).setEphemeral(true).queue();
            }
        }
    }
}
