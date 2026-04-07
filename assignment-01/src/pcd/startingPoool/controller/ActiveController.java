package pcd.startingPoool.controller;

import pcd.sketch02.util.BoundedBuffer;
import pcd.sketch02.util.BoundedBufferImpl;
import pcd.startingPoool.Board;

public class ActiveController extends Thread {

	private BoundedBuffer<Cmd> cmdBuffer;
	private Board board;
	
	public ActiveController(Board board) {
        //l'accesso al bounded buffer è thread safe perchè per inserire e togliere un elemento dal buffer si deve predere il lock (uso costrutto synchronized)
		this.cmdBuffer = new BoundedBufferImpl<Cmd>(100);
		this.board = board;
	}

    //i comandi dati al controller dalla view e che devono modificare il model (la palla del giocatore)
    // sono UpCmd, DownCmd, LeftCmd, RightCmd
	public void run() {
		log("started.");
		while (true) {
			try {
				//log("Waiting for cmds ");
				var cmd = cmdBuffer.get();
				//log("new cmd fetched: " + cmd);
				cmd.execute(board);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

    //metodo con cui la view notifica al controller un evento
	public void notifyNewCmd(Cmd cmd) {
		try {
			cmdBuffer.put(cmd);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void log(String msg) {
		System.out.println("[ " + System.currentTimeMillis() + "][ Controller ] " + msg);
	}
}
