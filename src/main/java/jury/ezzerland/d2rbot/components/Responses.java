package jury.ezzerland.d2rbot.components;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class Responses {

    //========= STRINGS
    public static String alreadyInQueue() { return "You are already in an active run."; }
    public static String alreadyInQueue(String player) { return player + " is already in an active run."; }
    public static String notInQueue() { return "You are not currently participating in a run."; }
    public static String notInQueue(String player) { return player + "is not in this run and must be added first."; }
    public static String fullQueue() { return "This room is already full!"; }
    public static String joinedQueue(String player, String host, String availability, String type) { return player + " has joined <@" + host + ">'s " + type + "! This run currently " + availability; }
    public static String leftQueue(String player, String host, String availability, String type) { return player + " has left " + host + "'s " + type + "! This run currently " + availability; }
    public static String leftQueue() { return "You have left the queue."; }
    public static String endQueue(String player) { return player + " has ended the run they were hosting."; }
    public static String addToQueue(String player) { return player + " has been added to your run."; }
    public static String kickedPlayer(String player) { return player + " has been removed from your run."; }
    public static String kickedNotInRun(String player) { return player + " is not in your run and cannot be kicked."; }
    public static String kickedAll() {
        return "All players have been removed from your queue.\n" +
                "`/leave` will end your run.\n" +
                "`/broadcast` will announce your run as available to join.\n" +
                "`/rename` will allow you to update the current game name and password for your run.";
    }
    public static String cannotKickHost() { return "The host cannot be kicked until a new host has been set."; }
    public static String cannotKickSelf() { return "You cannot kick yourself from a match. Please use /leave"; }
    public static String notTheHost() { return "Only the host of the run has access to this command."; }
    public static String setHost(String player) { return player + " is now the host of the run. You are still in the run."; }
    public static String changedHost(String player, String host) { return player + " has given host to <@" + host + ">."; }
    public static String failedToHost() { return "Operation Failed. Please try again!"; }
    public static String queueNoLongerActive() { return "The run you are attempting to join is no longer active"; }
    public static String noActiveRuns() { return "There are no active runs happening right now. Use `/host` to start a new run!"; }
    public static String noActiveRunsOfType(boolean ladder, String type) { return "There are no active " + getLadderString(ladder) + " " + type + "s right now. Use `/host` to start a new run!"; }
    public static String announcementMade(String channel) {
        return "This game has been announced in <#" + channel + ">.\n" +
                    "The Game Information will only be shared when people join your run.";
    }
    public static String renamedRun(String name, String password) { return "Your game information has been updated!\nNew Game Name: " + name + "\nNew Password: "+password; }
    public static String renamedRun(String name) { return "Your game information has been updated!\nNew Game Name: " + name; }
    public static String errorMessage(String msg) {
        return "**ERROR**: "+msg+"\n"+
                "Please report this error to the mod team with a screenshot if possible!";
    }
    public static void amountOfActiveRuns(InteractionHook event) {
        if (BOT.getParticipants().size() == 0) { event.sendMessage(noActiveRuns()).setEphemeral(true).queue(); return; }
        String ladder = "", nonladder = "", response = "";
        int laddercount = 0, nonladdercount = 0;
        Set<Button> ladderButtons = new HashSet<>(), nonLadderButtons = new HashSet<>();
        for (RunType type : RunType.values()) {
            if (BOT.getLadder().get(type).size() > 0) {
                int count = BOT.getLadder().get(type).size();
                laddercount += count;
                ladder += "\n" + type.getTypeAsString(type) + "s: " + count;
                ladderButtons.add(listButton(true, type.toString(), type.getTypeAsString(type)));
            }
            if (BOT.getNonLadder().get(type).size() > 0) {
                int count = BOT.getNonLadder().get(type).size();
                nonladdercount += count;
                nonladder += "\n" + type.getTypeAsString(type) + "s: " + count;
                nonLadderButtons.add(listButton(false, type.toString(), type.getTypeAsString(type)));
            }
        }
        if (laddercount > 0) {
            response += "**__Total Ladder Runs: " + laddercount + "__**\n```" + ladder + "\n```";
        }
        if (nonladdercount > 0) {
            response += "**__Total Non-Ladder Runs: " + nonladdercount + "__**\n```" + nonladder + "\n```";
        }
        response += "Click the buttons below or use `/list` to see what runs are available for you to join!";
        if (ladderButtons.size() > 0 && nonLadderButtons.size() > 0) {
            event.sendMessage(response).addActionRow(ladderButtons).addActionRow(nonLadderButtons).setEphemeral(true).queue();
            return;
        }
        if (ladderButtons.size() > 0) {
            event.sendMessage(response).addActionRow(ladderButtons).setEphemeral(true).queue();
            return;
        }
        event.sendMessage(response).addActionRow(nonLadderButtons).setEphemeral(true).queue();
    }




    //========= BUTTONS
    public static Button joinButton (String id) { return joinButton(id, false); }
    public static Button joinButton(String id, boolean rsvp) {
        String label = "Join Run";
        if (rsvp) { label = "RSVP"; }
        return Button.success("join-judge-queue."+id, label);
    }
    public static Button leaveButton(String id) { return Button.danger("leave-judge-queue."+id, "Leave Run"); }
    public static Button endRunButton(String id) { return Button.danger("leave-judge-queue."+id,"End Run"); }
    public static Button broadcastButton(String id) { return Button.secondary("broadcast-judge-queue."+id,"Broadcast Run"); }
    public static Button nextGameButton(String id) { return Button.primary("nextgame-judge-queue."+id,"Next Game"); }
    public static Button kickPlayerButton(String id, String name) { return Button.danger("kick-judge-queue."+id,"Kick "+name); }
    public static Button gameInfoButton(String id) { return Button.secondary("info-judge-queue."+id, "Game Info"); }
    public static Button getInfoButton(String id) { return Button.secondary("game-judge-queue."+id, "Game Info"); }
    public static Button renameGameButton(String id) { return Button.primary("rename-judge-queue."+id, "Rename Game"); }
    public static Button listButton(boolean ladder, String type, String name) {
        if (ladder) { return Button.success("ladder-judge-queue."+type, "L "+name); }
        return Button.primary("nonladder-judge-queue."+type, "NL "+name);
    }
    public static Button listRunsButton(String id) { return Button.primary("runs-judge-queue."+id, "View Runs"); }




    //========= EMBEDS
    public static MessageEmbed gameInfo(Run run, boolean isList) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(run.getHost().getEffectiveName() + " - " + run.getLadderAsString() + " " + run.getTypeAsString());
        if (run.isFull()) { embed.setColor(Color.RED); }
        else { embed.setColor(Color.GREEN); }
        if (!isList) {
            embed.addField("Game Name", run.getGameName(), false);
            embed.addField("Password", run.getPassword(), false);
        } else {
            embed.addField("", "Click Join to get the game name and password for this run!", false);
        }
        if (run.lastAction() == 1) { embed.addField("","Last Game Created " + run.lastAction() + " minute ago", false); }
        else { embed.addField("","Last Game Created " + run.lastAction() + " minutes ago", false); }
        embed.addField("**__Participants in this Run__**", "Count: "+run.getMemberCount()+"\n"+getParticipants(run),false);
        embed.setFooter("This run is hosted by " + run.getHost().getEffectiveName(), run.getHost().getAvatarUrl());
        return embed.build();
    }
    public static MessageEmbed announceNewRun(String user, String ladder, String type, boolean isNew, boolean isRsvp) {
        EmbedBuilder embed = new EmbedBuilder();
        if (isRsvp) {
            if (isNew) { embed.setTitle(user + " is hosting an upcoming " + ladder + " " + type + "!"); }
            else { embed.setTitle(user + " has an upcoming " + ladder + " " + type + "!"); }
        }
        else {
            if (isNew) { embed.setTitle(user + " is hosting a new " + ladder + " " + type + "!"); }
            else { embed.setTitle(user + " has a " + ladder + " " + type + " ongoing!"); }
        }
        if (isRsvp) {
            embed.setColor(Color.MAGENTA);
            embed.addField("**__RSVP NOW__**", "Simply click the RSVP Button!\nGame information will be sent to you upon joining.\nA reminder will go out 5 minutes prior to the start of the run!",false);
        }
        else {
            embed.setColor(Color.CYAN);
            embed.addField("**__How to join__**", "Simply click the Join Button!\nGame information will be sent to you upon joining.",false);
        }
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
                "**/ng** automatically increments game run-001 to run-002 etc.\n" +
                "**/kick** Kick player by UID or list UID's of players in your run.\n" +
                "**/kickall** will kick all players except for you from your run.",false);
        embed.addField("**__NOTE__**", "As the host, your focus should be on the runs.\n" +
                "Anyone in the run can do **/ng** or kick players that did not leave.\n" +
                "For the best experience, ask someone in your run to do these tasks!",false);
        return embed.build();
    }
    public static MessageEmbed publishRun(Run run, String user, String ladder, String type) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(user + " has a " + ladder + " " + type + " beginning now!");
        embed.addField("**__Participants__**", getParticipants(run, true),false);
        if (!run.isFull()) {
            embed.setColor(Color.GREEN);
            embed.addField("**__How to join__**", "Simply click the Join Button!\nGame information will be sent to you upon joining.",false);
        }
        else { embed.setColor(Color.RED); }
        embed.setFooter("This run is hosted by " + user);
        return embed.build();
    }




    //========= OPTIONS
    public static OptionData getRunTypeAsOption() {
        return new OptionData(OptionType.STRING, "type", "What type of run are you hosting?", true)
                .addChoice("Baal Runs", "BAAL")
                .addChoice("Chaos Runs", "CHAOS")
                .addChoice("TZ Runs", "TERRORZONE")
                .addChoice("MF Runs", "MAGICFIND")
                .addChoice("PvP Game", "PVP");
    }
    public static OptionData getLadderAsOption() {
        return new OptionData(OptionType.STRING, "ladder", "Is this a ladder or non-ladder game?", true)
                .addChoice("Ladder", "true")
                .addChoice("Non-Ladder", "false");
    }
    public static OptionData getRsvpAsOption() {
        return new OptionData(OptionType.STRING, "rsvp", "Will this run start at the beginning of the next hour?", true)
                .addChoice("Yes", "true")
                .addChoice("No", "false");
    }
    public static OptionData getAddOption() {
        return new OptionData(OptionType.USER, "tag", "Discord @tag of the person you are adding", true);
    }
    public static OptionData getHostOption() {
        return new OptionData(OptionType.USER, "tag", "Discord @tag of the person you are making host", true);
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

    private static String getParticipants(Run run) { return getParticipants(run, false); }
    private static String getParticipants(Run run, boolean useTags) {
        String participants = "";
        for (Member member : run.getMembers()) {
            if (!participants.equals("")) { participants += "\n"; }
            if (useTags) {
                participants += "<@" + member.getId() + ">";
            }
            else {
                participants += member.getEffectiveName();
            }
        }
        return participants;
    }

}
