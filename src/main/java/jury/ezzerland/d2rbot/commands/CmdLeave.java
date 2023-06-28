package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdLeave {

    public CmdLeave (ButtonInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).addActionRow(Responses.listRunsButton(event.getMember().getId())).setEphemeral(true).queue();
            return;
        }
        event.reply(Responses.leftQueue()).setEphemeral(true).queue();
        leaveCommand(event.getMember());
    }

    public CmdLeave (SlashCommandInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).addActionRow(Responses.listRunsButton(event.getMember().getId())).setEphemeral(true).queue();
            return;
        }
        event.reply(Responses.leftQueue()).setEphemeral(true).queue();
        leaveCommand(event.getMember());
    }

    private void leaveCommand(Member member) {
        Run run = BOT.getParticipants().get(member);
        if (run.getHost().equals(member)) {
            run.endRun();
            run.getChannel().sendMessage(Responses.endQueue(Responses.memberName(member))).queue();
            return;
        }
        run.removeMember(member);
        run.getChannel().sendMessage(Responses.leftQueue(Responses.memberName(member), run.getHost().getEffectiveName(), run.isFullAsString(), run.getTypeAsString())).addActionRow(Responses.joinButton(run.getHost().getId()), Responses.getInfoButton(run.getHost().getId()), Responses.listRunsButton(run.getHost().getId())).queue();
    }


}
