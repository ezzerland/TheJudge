package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.*;
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
        run.setMode(RunMode.valueOf(event.getOption("mode").getAsString()));
        run.setRsvp(Boolean.valueOf(event.getOption("rsvp").getAsString()));
        run.setFlag(RunFlag.valueOf(event.getOption("flag").getAsString()));
        BOT.getParticipants().put(event.getMember(), run);
        if (run.isLadder()) {
            if (run.isHardcore()) { BOT.getHCLadder().get(type).add(run); }
            else { BOT.getLadder().get(type).add(run); }
        } else {
            if (run.isHardcore()) { BOT.getHCNonLadder().get(type).add(run); }
            else { BOT.getNonLadder().get(type).add(run); }
        }
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.failedToHost()).setEphemeral(true).queue();
            return;
        }
        event.replyModal(Responses.getGameInfoModal(true)).queue();
    }

    public CmdHost (ModalInteractionEvent event, boolean isNew) {
        Run run = BOT.getParticipants().get(event.getMember());
        if (run == null) {
            event.reply(Responses.failedToHost()).setEphemeral(true).queue();
            return;
        }
        run.setGameName(event.getValue("gamename").getAsString());
        run.setPassword(event.getValue("password").getAsString());
        run.setMaxMembers(getMaxPlayers(event.getValue("maxplayers").getAsString()));
        run.setDescription(event.getValue("description").getAsString());
        if (isNew) {
            run.broadcastRun(true);
            event.replyEmbeds((Responses.announcementMade(run.getModeAsString(), run.getTypeAsString(), run.getChannel().getId(), run.isRsvp()))).addActionRow(Responses.nextGameButton(event.getMember().getId()), Responses.gameInfoButton(event.getMember().getId()), Responses.broadcastButton(event.getMember().getId()), Responses.renameGameButton(event.getMember().getId()), Responses.endRunButton(event.getMember().getId())).setEphemeral(true).queue();
            return;
        }
        event.reply(Responses.renamedRun(run.getGameName(), run.getPassword())).setEphemeral(true).queue();
    }

    private int getMaxPlayers(String players) {
        int max = 8;
        if (players != null && !players.isBlank()) {
            try {
                max = Integer.parseInt(players);
            } catch (NumberFormatException e) {}
        }
        if (max > 8) { max = 8; }
        if (max < 2) { max = 2; }
        return max;
    }
}
