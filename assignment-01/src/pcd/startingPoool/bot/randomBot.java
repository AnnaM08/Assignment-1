package pcd.startingPoool.bot;

import pcd.sketch02.util.BoundedBuffer;
import pcd.startingPoool.controller.*;

import java.sql.Timestamp;
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
                    case 0 -> cmdBuffer.put(new DownCmd(BallType.BOT, new Timestamp(System.currentTimeMillis())));
                    case 1 -> cmdBuffer.put(new UpCmd(BallType.BOT, new Timestamp(System.currentTimeMillis())));
                    case 2 -> cmdBuffer.put(new LeftCmd(BallType.BOT, new Timestamp(System.currentTimeMillis())));
                    case 3 -> cmdBuffer.put(new RightCmd(BallType.BOT, new Timestamp(System.currentTimeMillis())));
                }
                System.out.println("bot added a cmd");
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
    }
}
