package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdKick {

    public CmdKick(SlashCommandInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).setEphemeral(true).queue();
            return;
        }
        Run run = BOT.getParticipants().get(event.getMember());
        /*if (!run.getHost().equals(event.getMember())) {
            event.reply(Responses.notTheHost()).setEphemeral(true).queue();
            return;
        }*/
        event.deferReply().setEphemeral(true).queue();
        new CmdInfo(event.getHook(), event.getMember(), run.getHost());
    }

    public CmdKick (ButtonInteractionEvent event, String user) {
        Member kicking = event.getGuild().getMemberById(user);
        if (kicking == null) {
            event.reply(Responses.errorMessage("Unable to identify kicked user - " + user)).setEphemeral(true).queue();
            return;
        }
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).setEphemeral(true).queue();
            return;
        }
        Run run = BOT.getParticipants().get(event.getMember());
        /*if (!run.getHost().equals(event.getMember())) {
            event.reply(Responses.notTheHost()).setEphemeral(true).queue();
            return;
        }*/
        if (run.getHost().equals(kicking)) {
            event.reply(Responses.cannotKickHost()).setEphemeral(true).queue();
            return;
        }
        if (event.getMember().equals(kicking)) {
            event.reply(Responses.cannotKickSelf()).setEphemeral(true).queue();
            return;
        }
        if (!run.getMembers().contains(kicking)) {
            event.reply(Responses.kickedNotInRun(Responses.memberName(kicking))).setEphemeral(true).queue();
            return;
        }
        run.removeMember(kicking);
        event.reply(Responses.kickedPlayer(Responses.memberName(kicking))).setEphemeral(true).queue();
        run.getChannel().sendMessage(Responses.kickedPlayerAnnounce(Responses.memberName(kicking), run.getHost().getEffectiveName(), run.isFullAsString(), run.getTypeAsString())).addActionRow(Responses.joinButton(run.getHost().getId(), run.isRsvp()), Responses.getInfoButton(run.getHost().getId()), Responses.listRunsButton(run.getHost().getId())).queue();
    }
}
