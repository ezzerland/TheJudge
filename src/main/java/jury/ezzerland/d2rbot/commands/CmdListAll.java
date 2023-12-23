package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import jury.ezzerland.d2rbot.components.RunType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashSet;
import java.util.Set;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdListAll {

    public CmdListAll(SlashCommandInteractionEvent event) {
        if (BOT.getParticipants().size() == 0) {
            event.reply(Responses.noActiveRuns()).setEphemeral(true).queue();
            return;
        }
        if (BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.alreadyInQueue()).addActionRow(Responses.leaveButton(event.getMember().getId())).setEphemeral(true).queue();
            return;
        }
        event.deferReply().setEphemeral(true).queue();
        for (RunType type : RunType.values()) {
            Set<Run> runs = new HashSet<>();
            runs.addAll(BOT.getLadder().get(type));
            runs.addAll(BOT.getNonLadder().get(type));
            runs.addAll(BOT.getHCLadder().get(type));
            runs.addAll(BOT.getHCNonLadder().get(type));
            if (runs.size() == 0) { continue; }
            for (Run run : runs) {
                if (run.isFull()) {
                    event.getHook().sendMessageEmbeds(Responses.gameInfo(run, true)).setEphemeral(true).queue();
                } else {
                    event.getHook().sendMessageEmbeds(Responses.gameInfo(run, true)).addActionRow(Responses.joinButton(run.getHost().getId(), run.isRsvp())).setEphemeral(true).queue();
                }
            }
        }
    }
}
