package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.HashSet;
import java.util.Set;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdInfo {
    public CmdInfo (SlashCommandInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).addActionRow(Responses.listRunsButton(event.getMember().getId())).setEphemeral(true).queue();
            return;
        }
        Run run = BOT.getParticipants().get(event.getMember());
        event.replyEmbeds(Responses.gameInfo(run, false)).setEphemeral(true).queue();
    }

    public CmdInfo (ButtonInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).setEphemeral(true).queue();
            return;
        }
        Run run = BOT.getParticipants().get(event.getMember());
        if (!run.getHost().equals(event.getMember())) {
            event.replyEmbeds(Responses.gameInfo(run, false)).setEphemeral(true).queue();
            return;
        }
        Set<Button> buttonsOne = new HashSet<>(), buttonsTwo = new HashSet<>();
        int i = 0;
        for (Member member : run.getMembers()) {
            if (run.getHost().equals(member)) { continue; }
            if (i<=3) {
                buttonsOne.add(Responses.kickPlayerButton(member.getId(), member.getEffectiveName()));
            } else {
                buttonsTwo.add(Responses.kickPlayerButton(member.getId(), member.getEffectiveName()));
            }
            i++;
        }
        if (i >= 4) {
            event.replyEmbeds(Responses.gameInfo(run, false)).addActionRow(buttonsOne).addActionRow(buttonsTwo).setEphemeral(true).queue();
            return;
        }
        if (i >= 1) {
            event.replyEmbeds(Responses.gameInfo(run, false)).addActionRow(buttonsOne).setEphemeral(true).queue();
            return;
        }
        event.replyEmbeds(Responses.gameInfo(run, false)).setEphemeral(true).queue();
    }

    public CmdInfo (ButtonInteractionEvent event, String user) {
        Member host = event.getGuild().getMemberById(user);
        if (host == null) {
            event.reply(Responses.errorMessage("Unable to identify game host - " + user)).setEphemeral(true).queue();
            return;
        }
        if (!BOT.getParticipants().containsKey(host)) {
            event.reply(Responses.queueNoLongerActive()).setEphemeral(true).queue();
            return;
        }
        Run run = BOT.getParticipants().get(host);
        if (run.isFull()) {
            event.replyEmbeds(Responses.gameInfo(run, true)).setEphemeral(true).queue();
        } else {
            event.replyEmbeds(Responses.gameInfo(run, true)).addActionRow(Responses.joinButton(run.getHost().getId())).setEphemeral(true).queue();
        }
    }
}
