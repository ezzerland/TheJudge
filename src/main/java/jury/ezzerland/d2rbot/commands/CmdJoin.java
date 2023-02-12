package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdJoin {

    public CmdJoin (ButtonInteractionEvent event, String user) {
        Member host = event.getGuild().getMemberById(user);
        if (host == null) {
            event.reply(Responses.errorMessage("Unable to identify game host - " + user)).setEphemeral(true).queue();
            return;
        }
        if (BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.alreadyInQueue()).addActionRow(Responses.leaveButton(event.getMember().getId())).setEphemeral(true).queue();
            return;
        }
        if (!BOT.getParticipants().containsKey(host)) {
            event.reply(Responses.queueNoLongerActive()).setEphemeral(true).queue();
            return;
        }
        Run run = BOT.getParticipants().get(host);
        if (!run.addMember(event.getMember())) {
            event.reply(Responses.fullQueue()).setEphemeral(true).queue();
            return;
        }
        BOT.getParticipants().put(event.getMember(), run);
        if (run.isFull()) { run.getChannel().sendMessage(Responses.joinedQueue(event.getMember().getEffectiveName(), host.getId(), run.isFullAsString())).addActionRow(Responses.listRunsButton(host.getId())).queue(); }
        else { run.getChannel().sendMessage(Responses.joinedQueue(event.getMember().getEffectiveName(), host.getId(), run.isFullAsString())).addActionRow(Responses.joinButton(host.getId()), Responses.listRunsButton(host.getId())).queue(); }
        event.replyEmbeds(Responses.gameInfo(run, false)).addActionRow(Responses.nextGameButton(event.getMember().getId()), Responses.broadcastButton(event.getMember().getId()), Responses.gameInfoButton(event.getMember().getId()), Responses.leaveButton(event.getMember().getId())).setEphemeral(true).queue();
    }
}
