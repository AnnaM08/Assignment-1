package pcd.startingPoool.controller;

import pcd.startingPoool.model.Board;

import java.sql.Timestamp;

public abstract class AbstractCmd implements Cmd{

        private final Timestamp timestamp;
        public AbstractCmd(Timestamp t){
            this.timestamp = t;
        }

        public Timestamp getTimestamp(){
            return timestamp;
        }

        public abstract void execute(Board b);
}
