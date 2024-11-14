package com.pilzbros.mazehunt.io;

public enum Setting {
    CheckForUpdates("CheckForUpdates", true),
    NotifyOnNewUpdates("NotifyOnNewUpdates", true),
    ReportMetrics("MetricReporting", true),
    NotifyOnAustinPilz("NotifyOnPluginCreatorJoin", true),
    SelectionTool("Setup.SelectionTool", 284),
    CountdownTime("Gameplay.WaitingTime", 30),
    InGameCommands("Gameplay.InGameCommands", false),
    InGameTeleport("Gameplay.Teleporting", false),
    SignStats("Gameplay.RealtimeSignStats", true),
    WinSword("Gameplaye.WinnerSword", 276),
    OtherSword("Gameplay.PlayerSword", 272),
    MinPlayers("Gameplay.MinimumPlayers", 2),
    WaitingMessage("Messages.InGame.WaitingMessage", "MazeHunt will begin after the countdown finishes. To leave, type /mazehunt quit"),
    GameBeginMessage("Messages.InGame.GameBeginMessage", "MazeHunt game has begun!"),
    SwordReachedMessage("Messages.InGame.WinPointReached", "You've been awarded the sword! To win, hunt down and kill the others! GO!"),
    SwordOtherMessage("Messages.InGame.NotifyWinPoint", "has reached the sword! Run, or team up with others to hunt them down!"),
    SwordAlreadyAwarded("Mesages.InGame.SwordAlreadyReachd", "The sword has already been reached by another player!"),
    NoRoomMessage("Messages.InGame.NoMoreRoom", "There is no more room in this arena!"),
    QuitMessage("Messages.InGame.QuitMessage", "You quit MazeHunt, thanks for playing!"),
    KickMessage("Messages.InGame.KickMessage", "You have been kicked from MazeHunt!"),
    DeathMessage("Messages.InGame.DeathMessage", "You died, thanks for playing MazeHunt!"),
    SwordDeathMessage("Messages.InGame.SwordDeathMessage", "You failed at being MazeHunter! The other players win. Better luck next time!"),
    WinMessage("Messages.InGame.WinMessage", "Congratulations! You hunted down and killed everyone else, you win MazeHunt!"),
    WinMessageReg("Messages.InGame.PlayersWinMessage", "You and the others hunted down and killed the MazeHunter! You win, congratulations!"),
    SwordPlayerQuit("Messages.InGame.SwordPlayerQuit", "The player with the sword quit MazeHunt. Loser"),
    ReloadMessage("Messages.InGame.ReloadMessage", "A server reload has forced you out of MazeHunt"),
    GameAlreadyInSession("Messages.InGame.GameAlreadyOngoing", "A round of MazeHunt is already in progress, you can play when it's done!"),
    TeleportDisable("Messages.InGame.TeleportDisable", "You cannot teleport while playing MazeHunt. To quit, type /mazehunt quit"),
    CommandsDisable("Messages.InGame.CommandDisable", "You cannot use non MazeHunt commands while playing. To quit, type /mazehunt quit"),
    CommandDisabledMessage("Messages.Etc.CommandsDisabled", "Commands are disabled during MazeHunt gameplay");

    private String name;
    private Object def;

    private Setting(String Name, Object Def) {
        this.name = Name;
        this.def = Def;
    }

    public String getString() {
        return this.name;
    }

    public Object getDefault() {
        return this.def;
    }
}
