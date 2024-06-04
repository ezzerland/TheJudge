package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdAdd {
    public CmdAdd (SlashCommandInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).setEphemeral(true).queue();
            return;
        }
        Member member = event.getOption("tag").getAsMember();
        if (member == null) {
            event.reply(Responses.errorMessage("Member not found "+event.getOption("tag").getAsString())).setEphemeral(true).queue();
            return;
        }
        if (BOT.getParticipants().containsKey(member)) {
            event.reply(Responses.alreadyInQueue(Responses.memberName(member))).setEphemeral(true).queue();
            return;
        }
        Run run = BOT.getParticipants().get(event.getMember());
        if (run.addMember(member)) {
            event.reply(Responses.addToQueue(Responses.memberName(member))).addActionRow(Responses.gameInfoButton(event.getMember().getId())).setEphemeral(true).queue();
            BOT.getDatabase().addRun(member, run);
            BOT.getParticipants().put(member, run);
            if (run.isFull()) { run.getChannel().sendMessage(Responses.joinedQueue(Responses.memberName(member), run.getHost().getId(), run.isFullAsString(), run.getTypeAsString(), run.isRsvp())).addActionRow(Responses.getInfoButton(run.getHost().getId()), Responses.listRunsButton(run.getHost().getId())).queue(); }
            else { run.getChannel().sendMessage(Responses.joinedQueue(Responses.memberName(member), run.getHost().getId(), run.isFullAsString(), run.getTypeAsString(), run.isRsvp())).addActionRow(Responses.joinButton(run.getHost().getId(), run.isRsvp()), Responses.getInfoButton(run.getHost().getId()), Responses.listRunsButton(run.getHost().getId())).queue(); }
            return;
        }
        event.reply(Responses.fullQueue()).addActionRow(Responses.gameInfoButton(event.getMember().getId())).setEphemeral(true).queue();
    }
}
