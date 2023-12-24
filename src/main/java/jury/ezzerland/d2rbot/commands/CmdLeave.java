package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdLeave {

    public CmdLeave (ButtonInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).addActionRow(Responses.listRunsButton(event.getMember().getId())).setEphemeral(true).queue();
            return;
        }
        event.reply(Responses.leftQueue()).setEphemeral(true).queue();
        if (BOT.getParticipants().get(event.getMember()).isRsvp()) {
            BOT.getParticipantTimeOut().put(event.getMember(), timeoutStamp());
            event.reply(Responses.rsvpTimeOut(event.getMember())).setEphemeral(true).queue();
        }
        leaveCommand(event.getMember());
    }

    public CmdLeave (SlashCommandInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).addActionRow(Responses.listRunsButton(event.getMember().getId())).setEphemeral(true).queue();
            return;
        }
        event.reply(Responses.leftQueue()).setEphemeral(true).queue();
        if (BOT.getParticipants().get(event.getMember()).isRsvp()) {
            BOT.getParticipantTimeOut().put(event.getMember(), timeoutStamp());
            event.reply(Responses.rsvpTimeOut(event.getMember())).setEphemeral(true).queue();
        }
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
        run.getChannel().sendMessage(Responses.leftQueue(Responses.memberName(member), run.getHost().getEffectiveName(), run.isFullAsString(), run.getTypeAsString(), run.isRsvp())).addActionRow(Responses.joinButton(run.getHost().getId(), run.isRsvp()), Responses.getInfoButton(run.getHost().getId()), Responses.listRunsButton(run.getHost().getId())).queue();
    }

    private long timeoutStamp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextHour = now.plusHours(1).truncatedTo(ChronoUnit.HOURS);
        long punishment = 15, temp = Duration.between(now, nextHour.minusMinutes(5)).toMinutes();
        if (temp > 0 && temp < 15) {
            punishment = temp;
        }
        return System.nanoTime()+ TimeUnit.MINUTES.toNanos(punishment);
    }
}
