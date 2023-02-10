package jury.ezzerland.d2rbot.components;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.*;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class Responses {

    //========= STRINGS
    public static String alreadyInQueue() { return "You are already in an active run."; }
    public static String notInQueue() { return "You are not currently participating in a run."; }
    public static String fullQueue() { return "The queue you are attempting to join is already full!"; }
    public static String leftQueue() { return "You have left the run you were participating in."; }
    public static String endQueue() { return "The run you were hosting has ended."; }
    public static String kickedPlayer(String player) { return player + " has been removed from your run."; }
    public static String kickedAll() {
        return "All players have been removed from your queue.\n" +
                "`/leave` will end your run.\n" +
                "`/broadcast` will announce your run as available to join.\n" +
                "`/rename` will allow you to update the current game name and password for your run.";
    }
    public static String notTheHost() { return "Only the host of the run has access to this command."; }
    public static String queueNoLongerActive() { return "The run you are attempting to join is no longer active"; }
    public static String noActiveRuns() { return "There are no active runs happening right now. Use `/host` to start a new run!"; }
    public static String noActiveRunsOfType(boolean ladder, String type) { return "There are no active " + getLadderString(ladder) + " " + type + "s right now. Use `/host` to start a new run!"; }
    /*public static String announcementMade(String ladder, String type, String channel) {
        return "You are hosting a new " + ladder + " " + type + "!\n" +
                "This game has been announced in <#" + channel + ">.\n" +
                "The Game Information will only be shared when people join your run.";
    }*/
    public static String announcementMade(String channel) {
        return "This game has been announced in <#" + channel + ">.\n" +
                    "The Game Information will only be shared when people join your run.";
    }
    public static String renamedRun() { return "Your game information has been successfully update."; }
    public static String errorMessage(String msg) {
        return "**ERROR**: "+msg+"\n"+
                "Please report this error to the mod team with a screenshot if possible!";
    }
    public static String amountOfActiveRuns() {
        if (BOT.getParticipants().size() == 0) { return noActiveRuns(); }
        String ladder = "", nonladder = "", response = "";
        int laddercount = 0, nonladdercount = 0;
        for (RunType type : RunType.values()) {
            if (BOT.getLadder().get(type).size() > 0) {
                int count = BOT.getLadder().get(type).size();
                laddercount += count;
                ladder += "\n" + type.getTypeAsString(type) + "s: " + count;
            }
            if (BOT.getNonLadder().get(type).size() > 0) {
                int count = BOT.getNonLadder().get(type).size();
                nonladdercount += count;
                nonladder += "\n" + type.getTypeAsString(type) + "s: " + count;
            }
        }
        if (laddercount > 0) {
            response += "**__Total Ladder Runs: " + laddercount + "__**\n```" + ladder + "\n```";
        }
        if (nonladdercount > 0) {
            response += "**__Total Non-Ladder Runs: " + nonladdercount + "__**\n```" + nonladder + "\n```";
        }
        response += "Use `/list` to see what runs are available for you to join!";
        return response;
    }




    //========= BUTTONS
    public static Button joinButton(String id) { return Button.success("join-judge-queue."+id, "Join Run"); }
    public static Button leaveButton(String id) { return Button.danger("leave-judge-queue."+id, "Leave Run"); }
    public static Button endRun(String id) { return Button.danger("leave-judge-queue."+id,"End Run"); }
    public static Button broadcastButton(String id) { return Button.secondary("broadcast-judge-queue."+id,"Broadcast Run"); }
    public static Button nextGameButton(String id) { return Button.primary("nextgame-judge-queue."+id,"Next Game"); }




    //========= EMBEDS
    public static MessageEmbed joinedGame(Run run) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("You have joined a " + run.getLadderAsString() + " " + run.getTypeAsString());
        embed.setColor(Color.GREEN);
        embed.addField("Game Name",run.getGameName(), false);
        embed.addField("Password",run.getPassword(), false);
        embed.addBlankField(true);
        embed.addField("**__Participants in this Run__**", "Count: "+run.getMemberCount()+"\n"+getParticipants(run, false),false);
        embed.setFooter("This run is hosted by " + run.getHost().getEffectiveName(), run.getHost().getAvatarUrl());
        return embed.build();
    }
    public static MessageEmbed canJoinGame(Run run) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(run.getLadderAsString() + " " + run.getTypeAsString());
        if (run.isFull()) {
            embed.setColor(Color.RED);
        }
        else {
            embed.setColor(Color.GREEN);
        }
        embed.addField("**__Participants in this Run__**", "Count: "+run.getMemberCount()+"\n"+getParticipants(run, false),false);
        embed.setFooter("This run is hosted by " + run.getHost().getEffectiveName(), run.getHost().getAvatarUrl());
        return embed.build();
    }
    public static MessageEmbed announceNewRun(String user, String ladder, String type, boolean isNew) {
        EmbedBuilder embed = new EmbedBuilder();
        if (isNew) { embed.setTitle("A new " + ladder + " " + type + " has started!"); }
        else { embed.setTitle("A " + ladder + " " + type + " is ongoing!"); }
        embed.setColor(Color.CYAN);
        embed.addField("**__How to join__**", "Simply click the Join Button!\nGame information will be sent to you upon joining.",false);
        embed.setFooter("This run is hosted by " + user);
        return embed.build();
    }
    public static MessageEmbed announcementMade(String ladder, String type, String channel) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Your new " + ladder + " " + type + " has been created!");
        embed.setColor(Color.CYAN);
        embed.addField("", "This game has been announced in <#" + channel + ">.\nYour Game Information will only be shared when people join your run.",false);
        embed.addField("**__Commands__**", "**/leave** will end your run.\n" +
                "**/broadcast** will announce your run as available to join.\n" +
                "**/rename** opens UI to update both game name and password.\n" +
                "**/ng** automatically increments game run-001 to run-002 erc.\n" +
                "**/kick** Kick player by UID or list UID's of players in your run.\n" +
                "**/kickall** will kick all players except for you from your run.",false);
        return embed.build();
    }

    public static MessageEmbed kickMenu(Run run) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Your " + run.getLadderAsString() + " " + run.getTypeAsString());
        embed.setColor(Color.BLACK);
        embed.addField("**__Participants in this Run__**", getParticipants(run, true),false);
        return embed.build();
    }




    //========= OPTIONS
    public static OptionData getRunTypeAsOption() {
        return new OptionData(OptionType.STRING, "type", "What type of run are you hosting?", true)
                .addChoice("Baal Runs", "BAAL")
                .addChoice("Chaos Runs", "CHAOS")
                .addChoice("TZ Runs", "TERRORZONE")
                .addChoice("Cow Runs", "COW")
                .addChoice("MF Runs", "MAGICFIND");
    }
    public static OptionData getLadderAsOption() {
        return new OptionData(OptionType.STRING, "ladder", "Is this a ladder or non-ladder game?", true)
                .addChoice("Ladder", "true")
                .addChoice("Non-Ladder", "false");
    }
    public static OptionData getKickOption() {
        return new OptionData(OptionType.STRING, "uid", "Who are you kicking?", false);
    }




    //========= MODALS
    public static Modal getGameInfoModal(boolean isNew) {
        TextInput gameName = TextInput.create("gamename", "Game Name", TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(15)
                .setRequired(true)
                .setPlaceholder("Jury-Baal-01")
                .build();

        TextInput password = TextInput.create("password", "Game Password", TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(15)
                .setRequired(false)
                .setPlaceholder("Optional")
                .build();

        if (isNew) {
            return Modal.create("host-true", "Enter Game Information")
                    .addActionRows(ActionRow.of(gameName), ActionRow.of(password))
                    .build();
        }
        return Modal.create("host-false", "Enter Game Information")
                .addActionRows(ActionRow.of(gameName), ActionRow.of(password))
                .build();
    }




    //========= UTILITY
    private static String getLadderString(boolean ladder) {
        if (ladder) { return "Ladder"; }
        return "Non-Ladder";
    }

    private static String getParticipants(Run run, boolean includeId) {
        String participants = "";
        for (Member member : run.getMembers()) {
            if (!participants.equals("")) { participants += "\n"; }
            participants += member.getEffectiveName();
            if (includeId) { participants += "'s UID: " + member.getId(); }
        }
        return participants;
    }

}
