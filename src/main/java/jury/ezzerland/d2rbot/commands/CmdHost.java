package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import jury.ezzerland.d2rbot.components.RunType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdHost {
    public CmdHost(SlashCommandInteractionEvent event) {

        if (BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.alreadyInQueue()).addActionRow(Responses.leaveButton(event.getMember().getId())).setEphemeral(true).queue();
            return;
        }
        Run run = new Run(event.getMember());
        RunType type = RunType.valueOf(event.getOption("type").getAsString());
        run.setType(type);
        run.setLadder(Boolean.valueOf(event.getOption("ladder").getAsString()));
        BOT.getParticipants().put(event.getMember(), run);
        if (run.isLadder()) {
            BOT.getLadder().get(type).add(run);
        } else {
            BOT.getNonLadder().get(type).add(run);
        }
        event.replyModal(Responses.getGameInfoModal(true)).queue();
    }

    public CmdHost (ModalInteractionEvent event, boolean isNew) {
        Run run = BOT.getParticipants().get(event.getMember());
        run.setGameName(event.getValue("gamename").getAsString());
        run.setPassword(event.getValue("password").getAsString());
        if (isNew) {
            run.broadcastRun(true);
            event.replyEmbeds((Responses.announcementMade(run.getLadderAsString(), run.getTypeAsString(), run.getChannel().getId()))).addActionRow(Responses.nextGameButton(event.getMember().getId()), Responses.gameInfoButton(event.getMember().getId()), Responses.broadcastButton(event.getMember().getId()), Responses.renameGameButton(event.getMember().getId()), Responses.endRunButton(event.getMember().getId())).setEphemeral(true).queue();
            return;
        }
        event.reply(Responses.renamedRun(run.getGameName(), run.getPassword())).setEphemeral(true).queue();
    }
}
