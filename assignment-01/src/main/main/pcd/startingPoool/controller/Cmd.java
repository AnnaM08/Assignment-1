package pcd.startingPoool.controller;

import pcd.startingPoool.model.Board;

import java.sql.Timestamp;

public interface Cmd {


	void execute(Board b);
    Timestamp getTimestamp();
}
