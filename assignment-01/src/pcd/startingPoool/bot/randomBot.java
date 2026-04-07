package pcd.startingPoool.bot;

import com.sun.tools.jconsole.JConsoleContext;
import pcd.sketch02.util.BoundedBuffer;
import pcd.startingPoool.Ball;
import pcd.startingPoool.controller.*;

import java.io.Console;
import java.util.Random;

public class randomBot extends Thread {

    private final BoundedBuffer<Cmd> cmdBuffer;
    private final Random random;

    public randomBot(BoundedBuffer<Cmd> cmdBuffer) {
        this.cmdBuffer = cmdBuffer;
        this.random = new Random();
    }

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(1000);
                switch (random.nextInt(4)) {
                    case 0 -> cmdBuffer.put(new DownCmd(BallType.BOT));
                    case 1 -> cmdBuffer.put(new UpCmd(BallType.BOT));
                    case 2 -> cmdBuffer.put(new LeftCmd(BallType.BOT));
                    case 3 -> cmdBuffer.put(new RightCmd(BallType.BOT));
                }
                System.out.println("bot added a cmd");
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
    }
}
