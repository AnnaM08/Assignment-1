package pcd.startingPoool;

import pcd.startingPoool.model.BoardWithExecutor;
import pcd.startingPoool.model.BoardWithThreads;
import pcd.startingPoool.model.StandardConf;
//import pcd.startingPoool.model.multithread.CollisionsMonitorImpl;
import pcd.startingPoool.model.multithread.CollisionsMonitorImpl2;
import pcd.startingPoool.view.View;
import pcd.startingPoool.view.ViewModel;
import pcd.startingPoool.controller.ActiveController;

public class Main {

    public static void main(String[] argv) {

        // Setup del model, ovvero della board del gioco (palline)
        var conf = new StandardConf();
        var board = new BoardWithThreads();
        var bufferOfTasks = new CollisionsMonitorImpl2();
        board.init(conf, bufferOfTasks);

        // Setup del controller, che è un componente attivo che modifica il model a seguito
        //dell'input dell'utente
        var controller = new ActiveController(board);

        // Setup della view e del view model
        var viewModel = new ViewModel();
        var view = new View(viewModel, 900, 600, controller);
        //model.addObserver(view);

        controller.start();


        //GAME LOOP
        long lastTime = System.currentTimeMillis();
        int nFrames = 0;
        long t0 = System.currentTimeMillis();

        while (true) {
            long current = System.currentTimeMillis();
            long elapsed = current - lastTime;
            lastTime = current;

            //update della fisica del modello

            board.updateState(elapsed);



            /* render */

            nFrames++;
            int framePerSec = 0;
            long dt = (System.currentTimeMillis() - t0);
            if (dt > 0) {
                framePerSec = (int)(nFrames*1000/dt);
            }

            viewModel.update(board, framePerSec);
            view.render();
        }

    }

}
