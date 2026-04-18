package pcd.startingPoool.controller;

import pcd.startingPoool.util.BoundedBuffer;
import pcd.startingPoool.util.OrderedBoundedBufferImpl;
import pcd.startingPoool.model.Board;
import pcd.startingPoool.bot.randomBot;

public class ActiveController extends Thread {

	private final BoundedBuffer<Cmd> cmdBuffer;
	private final Board board;
    private final randomBot randomBot;

    private final static int BUFFER_MAX_SIZE = 100;
	
	public ActiveController(Board board) {
        //l'accesso al bounded buffer è thread safe perchè per inserire e togliere un elemento dal buffer si deve predere il lock (uso costrutto synchronized)
		this.cmdBuffer = new OrderedBoundedBufferImpl(BUFFER_MAX_SIZE);
		this.board = board;

        this.randomBot = new randomBot(this.cmdBuffer);

	}

    //i comandi dati al controller dalla view e che devono modificare il model (la palla del giocatore)
    // sono UpCmd, DownCmd, LeftCmd, RightCmd
	public void run() {
		log("started.");
        this.randomBot.start();
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
