package pcd.startingPoool.view;

import pcd.sketch02.controller.IncCmd;
import pcd.sketch02.controller.ResetCmd;
import pcd.startingPoool.controller.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ViewFrame extends JFrame implements KeyListener {
    
    private VisualiserPanel panel;
    private ViewModel model;
    private RenderSynch sync;
    private ActiveController controller; //istanza del controller per notificare gli input utente
    
    public ViewFrame(ViewModel model, int w, int h, ActiveController controller){
    	this.model = model;
        // si crea un ascoltatore per gli eventi da tastiera
        this.addKeyListener(this);
        this.controller = controller;

    	this.sync = new RenderSynch();
    	setTitle("Poool");
        setSize(w,h + 25);
        setResizable(false);
        panel = new VisualiserPanel(w,h);
        getContentPane().add(panel);
        addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent ev){
				System.exit(-1);
			}
			public void windowClosed(WindowEvent ev){
				System.exit(-1);
			}
		});
    }
     
    public void render(){
		long nf = sync.nextFrameToRender();
        panel.repaint();
		try {
			sync.waitForFrameRendered(nf);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getExtendedKeyCode() == KeyEvent.VK_UP){
            controller.notifyNewCmd(new UpCmd());
        } else if (e.getExtendedKeyCode() == KeyEvent.VK_DOWN){
            System.out.println("Cliccata freccia DOWN");
            controller.notifyNewCmd(new DownCmd());
        } else if (e.getExtendedKeyCode() == KeyEvent.VK_LEFT){
            controller.notifyNewCmd(new LeftCmd());
        } else if (e.getExtendedKeyCode() == KeyEvent.VK_RIGHT) {
            controller.notifyNewCmd(new RightCmd());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public class VisualiserPanel extends JPanel {
        private int ox;
        private int oy;
        private int delta;
        
        public VisualiserPanel(int w, int h){
            setSize(w,h + 25);
            ox = w/2;
            oy = h/2;
            delta = Math.min(ox, oy);
        }

        public void paint(Graphics g){
    		Graphics2D g2 = (Graphics2D) g;
            Font originalFont = g2.getFont();
    		
    		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    		          RenderingHints.VALUE_ANTIALIAS_ON);
    		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
    		          RenderingHints.VALUE_RENDER_QUALITY);
    		g2.clearRect(0,0,this.getWidth(),this.getHeight());
            
    		g2.setColor(Color.LIGHT_GRAY);
		    g2.setStroke(new BasicStroke(1));
    		g2.drawLine(ox,0,ox,oy*2);
    		g2.drawLine(0,oy,ox*2,oy);
    		g2.setColor(Color.BLACK);
    		
    		    g2.setStroke(new BasicStroke(1));
	    		for (var b: model.getBalls()) {
	    			var p = b.pos();
	            	int x0 = (int)(ox + p.x()*delta);
	                int y0 = (int)(oy - p.y()*delta);
	                int radiusX = (int)(b.radius()*delta);
	                int radiusY = (int)(b.radius()*delta);
	                g2.drawOval(x0 - radiusX,y0 - radiusY,radiusX*2,radiusY*2);
	    		}
	
    		    g2.setStroke(new BasicStroke(2));
	    		var pb = model.getPlayerBall();
	    		if (pb != null) {
					var p1 = pb.pos();
		        	int x0 = (int)(ox + p1.x()*delta);
		            int y0 = (int)(oy - p1.y()*delta);
	                int radiusX = (int)(pb.radius()*delta);
	                int radiusY = (int)(pb.radius()*delta);
	                g2.drawOval(x0 - radiusX,y0 - radiusY,radiusX*2,radiusY*2);
                    // Aggiunta Label "H" (Human)
                    g2.setFont(new Font("Arial", Font.BOLD, 12));
                    g2.drawString("H", x0 - 4, y0 + 5);
                }


                // Rendering di Bot Ball
                var bb = model.getBotBall();
                if (bb != null) {
                    g2.setStroke(new BasicStroke(2)); // Spessore maggiore come per il player

                    var pBot = bb.pos();
                    int x0 = (int)(ox + pBot.x() * delta);
                    int y0 = (int)(oy - pBot.y() * delta);
                    int radiusX = (int)(bb.radius() * delta);
                    int radiusY = (int)(bb.radius() * delta);

                    g2.drawOval(x0 - radiusX, y0 - radiusY, radiusX * 2, radiusY * 2);
                    // Aggiunta Label "B" (Bot)
                    g2.setFont(new Font("Arial", Font.BOLD, 12));
                    g2.drawString("B", x0 - 4, y0 + 5);
                }

                //Rendering delle buche
                var fh = model.getFirstHole();
                if(fh != null){
                    var pHole = fh.pos();
                    int x0 = (int)(ox + pHole.x() * delta);
                    int y0 = (int)(oy - pHole.y() * delta);
                    int radiusX = (int)(fh.radius() * delta);
                    int radiusY = (int)(fh.radius() * delta);

                    g2.fillOval(x0 - radiusX, y0 - radiusY, radiusX * 2, radiusY * 2);
                }

                var sh = model.getSecondHole();
                if(sh != null){
                    var pHole = sh.pos();
                    int x0 = (int)(ox + pHole.x() * delta);
                    int y0 = (int)(oy - pHole.y() * delta);
                    int radiusX = (int)(sh.radius() * delta);
                    int radiusY = (int)(sh.radius() * delta);

                    g2.fillOval(x0 - radiusX, y0 - radiusY, radiusX * 2, radiusY * 2);
                }


                //Aggiunta dei punteggi dell'utente e del bot
                g2.setColor(Color.BLUE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 90));
                g2.drawString(model.getPlayerScore(), ox / 5, oy + oy / 2);
                //g2.setFont(new Font("Arial", Font.BOLD, 30));
                g2.drawString(model.getBotScore(), ox * 2 - ox / 3, oy + oy / 2);
                g2.setFont(originalFont);
    		    g2.setColor(Color.BLACK);

    		    g2.setStroke(new BasicStroke(1));
	    		g2.drawString("Num small balls: " + model.getBalls().size(), 20, 40);
	    		g2.drawString("Frame per sec: " + model.getFramePerSec(), 20, 60);

	    		sync.notifyFrameRendered();
    		
        }


        
    }
}
