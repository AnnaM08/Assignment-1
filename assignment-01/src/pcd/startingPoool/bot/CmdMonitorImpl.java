package pcd.startingPoool.bot;

import pcd.startingPoool.controller.Cmd;

import java.util.ArrayList;
import java.util.List;

public class CmdMonitorImpl implements CmdMonitor {

    private final List<Cmd> CommandsQueue = new ArrayList<>();
    public CmdMonitorImpl() {

    }

    public synchronized void addCmd(Cmd cmd) {
        CommandsQueue.add(cmd);
    }

    public synchronized List<Cmd> getAllCmd() {
        var returnList = new ArrayList<Cmd>(CommandsQueue);
        CommandsQueue.clear();
        return returnList;
    }

    public synchronized Boolean isQueueEmpty() {
        return CommandsQueue.isEmpty();
    }
}
