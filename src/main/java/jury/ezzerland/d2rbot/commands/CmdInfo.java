package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
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
        event.deferReply().setEphemeral(true).queue();
        new CmdInfo(event.getHook(), event.getMember(), event.getMember(), false);
    }

    public CmdInfo (ButtonInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).addActionRow(Responses.listRunsButton(event.getMember().getId())).setEphemeral(true).queue();
            return;
        }
        event.deferReply().setEphemeral(true).queue();
        new CmdInfo(event.getHook(), event.getMember(), event.getMember(), false);
    }

    public CmdInfo (ButtonInteractionEvent event, String user) {
        Member host = event.getGuild().getMemberById(user);
        if (host == null) {
            event.reply(Responses.errorMessage("Unable to identify game host - " + user)).setEphemeral(true).queue();
            return;
        }
        if (!BOT.getParticipants().containsKey(host)) {
            event.reply(Responses.queueNoLongerActive()).addActionRow(Responses.listRunsButton(host.getId())).setEphemeral(true).queue();
            return;
        }
        event.deferReply().setEphemeral(true).queue();
        new CmdInfo(event.getHook(), event.getMember(), host, false);
    }

    public CmdInfo (InteractionHook event, Member player, Member host, boolean kick) {
        Run run = BOT.getParticipants().get(host);
        if (run.getMembers().contains(player)) {
            if (!kick) {
                event.sendMessageEmbeds(Responses.gameInfo(run, false)).addActionRow(Responses.nextGameButton(host.getId()), Responses.getInfoButton(host.getId()), Responses.listRunsButton(host.getId())).setEphemeral(true).queue();
                return;
            }
            Set<Button> buttonsOne = new HashSet<>(), buttonsTwo = new HashSet<>();
            int i = 0;
            for (Member member : run.getMembers()) {
                if (run.getHost().equals(member) || player.equals(member)) { // can't kick the host or yourself
                    continue;
                }
                i++;
                if (i <= 4) {
                    buttonsOne.add(Responses.kickPlayerButton(member.getId(), Responses.memberName(member)));
                } else {
                    buttonsTwo.add(Responses.kickPlayerButton(member.getId(), Responses.memberName(member)));
                }
            }
            if (i >= 5) {
                event.sendMessageEmbeds(Responses.gameInfo(run, false)).addActionRow(buttonsOne).addActionRow(buttonsTwo).setEphemeral(true).queue();
                return;
            }
            if (i >= 1) {
                event.sendMessageEmbeds(Responses.gameInfo(run, false)).addActionRow(buttonsOne).setEphemeral(true).queue();
                return;
            }
            event.sendMessageEmbeds(Responses.gameInfo(run, false)).setEphemeral(true).queue();
            return;
        } else {
            if (run.isFull()) {
                event.sendMessageEmbeds(Responses.gameInfo(run, true)).addActionRow(Responses.getInfoButton(host.getId()), Responses.listRunsButton(host.getId())).setEphemeral(true).queue();
            } else {
                event.sendMessageEmbeds(Responses.gameInfo(run, true)).addActionRow(Responses.joinButton(run.getHost().getId(), run.isRsvp()), Responses.getInfoButton(host.getId()), Responses.listRunsButton(host.getId())).setEphemeral(true).queue();
            }
        }
    }
}
