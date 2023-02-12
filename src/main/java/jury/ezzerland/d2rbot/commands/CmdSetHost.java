package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdSetHost {
    public CmdSetHost(SlashCommandInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).setEphemeral(true).queue();
            return;
        }
        Run run = BOT.getParticipants().get(event.getMember());
        if (!run.getHost().equals(event.getMember())) {
            event.reply(Responses.notTheHost()).setEphemeral(true).queue();
            return;
        }
        Member member = event.getOption("tag").getAsMember();
        if (member == null) {
            event.reply(Responses.errorMessage("Member not found "+event.getOption("tag").getAsString())).setEphemeral(true).queue();
            return;
        }
        if (!run.getMembers().contains(member)) {
            event.reply(Responses.notInQueue(member.getEffectiveName())).setEphemeral(true).queue();
            return;
        }
        run.setHost(member);
        event.reply(Responses.setHost(member.getEffectiveName())).addActionRow(Responses.leaveButton(event.getMember().getId())).setEphemeral(true).queue();
    }
}
