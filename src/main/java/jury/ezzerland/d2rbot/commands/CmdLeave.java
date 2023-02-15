package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdLeave {

    public CmdLeave (ButtonInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).addActionRow(Responses.listRunsButton(event.getMember().getId())).setEphemeral(true).queue();
            return;
        }
        event.deferReply().queue();
        leaveCommand(event.getHook(), event.getMember());
    }

    public CmdLeave (SlashCommandInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).addActionRow(Responses.listRunsButton(event.getMember().getId())).setEphemeral(true).queue();
            return;
        }
        event.deferReply().queue();
        leaveCommand(event.getHook(), event.getMember());
    }

    private void leaveCommand(InteractionHook event, Member member) {
        Run run = BOT.getParticipants().get(member);
        if (run.getHost().equals(member)) {
            run.endRun();
            event.sendMessage(Responses.endQueue(member.getEffectiveName())).queue();
            return;
        }
        run.removeMember(member);
        event.sendMessage(Responses.leftQueue(member.getEffectiveName(), run.getHost().getEffectiveName(), run.isFullAsString())).addActionRow(Responses.joinButton(run.getHost().getId()), Responses.getInfoButton(run.getHost().getId()), Responses.listRunsButton(run.getHost().getId())).queue();
    }


}
