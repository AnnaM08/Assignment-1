package jpf;

import pcd.StartingPool.model.multithread.ColliderAgentTest;

public class MainForJpf {

    public static void main(String[] args) {


        ColliderAgentTest testRunner = new ColliderAgentTest();

        // 2. Esecuzione di un test specifico
        // NOTA: Per JPF, usiamo numeri MOLTO piccoli (es. 4 palle, 2 thread)
        // per evitare l'esplosione degli stati.
        System.out.println("Avvio test per JPF...");

        // Esempio: 10 palle con 2 agenti
        testRunner.test1_WithBallsAnd1Agent();


    }
}

