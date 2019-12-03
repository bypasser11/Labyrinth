
import gup.LabStep;
import gup.Labyrinth;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Moses
 */
public class Tafel extends javax.swing.JPanel implements MouseListener,Runnable{

    /**
     * Creates new form Tafel
     */
    
    //Instanzvariablen
    private int sizerow;
    private int sizecolumn;
    protected double cellw ,cellh, unit; //cell width, cell height
    protected int startrow, startcolumn; //Start position
    protected int exitrow, exitcolumn; //Target position
    protected double anchorx,anchory;
    protected int mousex,mousey; //Mouse coordinate
    
    protected ArrayList<LabStep> thepath, wegfinal;
    
    protected Labyrinth lab;

    protected Point2D.Double p[];
    protected boolean isclo1, isclo2, isclo3, isclo4;
    
    // For Thread & Runnable
    protected long t;
    protected boolean isFinding, isFinished;
    protected int c = 0;
    
    protected PropertyChangeSupport pcs;
    
    
   
    public Tafel() {
        super();
        initComponents();
        
        //Size of row/column
        sizerow= 5;
        sizecolumn = 5;
        
        //Startrow/Startcolumn
        startrow = 0;
        startcolumn = 0;    
        
        //Exit Row/column
        exitrow = 4;
        exitcolumn = 4;
        
        //Time
        t = 500;
        
        //Initialize Labyrinth
        lab = new Labyrinth();
        lab.init(sizerow, sizecolumn);
        p = new Point2D.Double[6];
        
        //Set start value for start and end points
        lab.setStart(startrow, startcolumn);
        lab.setExit(exitrow, exitcolumn);
        
        //Get the path
        thepath = lab.getPath();
        
        //
        isclo1 = isclo2 = isclo3 = isclo4 = false;
        
        pcs = new PropertyChangeSupport(this);
        
        //Test
        System.out.println("Row of Lab: " + lab.getRows() + "Column of Lab: "+ lab.getColumns());
        
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.black);
       
        
        //Cell Size
        cellw = getWidth()/lab.getColumns();
        cellh = getHeight()/lab.getRows();
        unit = Math.min(cellw, cellh);
        
        //Set Anchor
        anchorx = (getWidth() - unit * lab.getColumns())/2;
        anchory = (getHeight()- unit * lab.getRows())/2;
        
        //Draw Cells
        drawCell(g2);
        
        //Draw Start & Exit points
        drawStartStop(g2);
        
        //Finding the Path
        drawFinder(g2);
        
        if(isFinished == true && isFinding == false){
            drawFinalWay(g2);
        }
    }
    
    protected void drawCell(Graphics2D g2){
        
        g2.setStroke(new BasicStroke(2.5F));
        Line2D.Double linie;
        for (int i = 1; i <= lab.getRows(); i++) {
            for (int j = 1; j <= lab.getColumns(); j++) {
                
                //Direction check
                checkDirClosed(i-1, j-1);
               
                p[0] = new Point2D.Double(anchorx +(j-1)*unit ,anchory +(i-1)*unit );
                p[1] = new Point2D.Double(p[0].x + unit       , p[0].y);
                p[2] = new Point2D.Double(p[0].x              , p[0].y + unit);
                p[3] = new Point2D.Double(p[1].x              , p[2].y);//
                
                if(isclo2 == true){
                    //Left
                    linie = new Line2D.Double(p[0], p[2]);
                    g2.draw(linie);
                }
                if(isclo1 == true){
                    //Right
                    linie = new Line2D.Double(p[1], p[3]);
                    g2.draw(linie);
                }
                if(isclo3 == true ){
                    //Top line
                    linie = new Line2D.Double(p[0], p[1]);
                    g2.draw(linie);
                }               
                if(isclo4 == true ){
                   //Bottom Line
                    linie = new Line2D.Double(p[2], p[3]);
                    g2.draw(linie); 
                }

            }
        }
    }
   
    //Draw the finding dots
    protected void drawFinder(Graphics2D g2) {
        for (int i = 1 ; i <= c; i++) {
            Ellipse2D.Double pfinder = new Ellipse2D.Double(
                    anchorx + (thepath.get(i).c + 0.3) * unit,
                    anchory + (thepath.get(i).r + 0.3) * unit, 0.6 * unit, 0.6 * unit);
                //If the path is false, change colour!
                if(thepath.get(i).forward == true){
                    g2.setColor(Color.blue);
                } else if(thepath.get(i).forward == false) {
                    g2.setColor(Color.DARK_GRAY);
                }
                g2.fill(pfinder);
                
                if (i != 0 && i != thepath.size()-1)            // don't draw over start & endpoint
                    g2.fill(pfinder);
        }
    }

    protected void drawFinalWay(Graphics2D g) {
        
        //Get the points from the Class
        wegfinal = new ArrayList<>();
        
        //Draw Ellipse
        for (int i = 0; i < thepath.size() - 1; i++) {
            Ellipse2D.Double pfinder = new Ellipse2D.Double(
                    anchorx + (thepath.get(i).c + 0.3) * unit,
                    anchory + (thepath.get(i).r + 0.3) * unit, 0.6 * unit, 0.6 * unit);
            
            if (thepath.get(i).forward == true) {
                wegfinal.add(thepath.get(i));
                g.setColor(Color.yellow);
            } else {
                g.setColor(Color.DARK_GRAY);
                wegfinal.remove(wegfinal.size() - 1); //Wrong path --> remove
            }
            
            if (i != 0 && i != thepath.size()-1) {
                g.fill(pfinder);
            }
        }
        
        //Draw final line to connect start and end
        Line2D.Double linie;
        g.setStroke(new BasicStroke(2F));
        g.setColor(Color.green);

        wegfinal.add(thepath.get(thepath.size() - 1));
        for (int j = 1; j < wegfinal.size(); j++) {
            p[4] = new Point2D.Double(
                    anchorx + (wegfinal.get(j - 1).c + 0.6) * unit,
                    anchory + (wegfinal.get(j - 1).r + 0.6) * unit
            );
            p[5] = new Point2D.Double(
                    anchorx + (wegfinal.get(j).c + 0.6) * unit,
                    anchory + (wegfinal.get(j).r + 0.6) * unit
            );
            linie = new Line2D.Double(p[4], p[5]);
            g.draw(linie);
        }
        

    }
   
    protected void drawStartStop(Graphics2D g2){
        //Draw Start Point
        Ellipse2D.Double stp = new Ellipse2D.Double(
                anchorx+(lab.getStartColumn()+0.3)*unit, 
                anchory+(lab.getStartRow()+0.3)*unit, 0.6*unit, 0.6*unit);
        g2.setColor(Color.red);
        g2.fill(stp);
        
        //Draw Exit Point
        Ellipse2D.Double exp = new Ellipse2D.Double(
                anchorx+(lab.getExitColumn()+0.3)*unit, 
                anchory+(lab.getExitRow()+0.3)*unit, 0.6*unit, 0.6*unit);
        g2.setColor(Color.green);
        g2.fill(exp);
    }
    
    protected void checkDirClosed(int r, int c){
        
        try {
            isclo1 = lab.isClosedTo(r, c, lab.EAST);
            //System.out.println("EAST " + isclo1);
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,"Failure at CheckDir1 (Right)", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        try {
            
            isclo2 = lab.isClosedTo(r, c, lab.WEST);
            //System.out.println("WEST " + isclo2);
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,"Failure at CheckDir1 (Left)", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        try {
            isclo3 = lab.isClosedTo(r, c, lab.NORTH);
            //System.out.println("NORTH " + isclo3);
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,"Failure at CheckDir1 (top)", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        try {
            isclo4 = lab.isClosedTo(r, c, lab.SOUTH);
            //System.out.println("SOUTH " + isclo4);
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,"Failure at CheckDir1 (Bottom)", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    

    
    public void coordConv(int mouse) {
        int r = 0, c = 0;
   
        if ((mousex > anchorx && mousex < anchorx + unit*lab.getColumns()) &&
            (mousey > anchory && mousey < anchory + unit*lab.getRows())) {
            c = (int)((mousex-anchorx) / unit);
            r = (int)((mousey-anchory) / unit);
        } else {
            return;
        }
        
        if (mouse == 1) {
            if (r == lab.getExitRow() && c == lab.getExitColumn()) // same start & exit
                return;
            lab.setStart(r, c);
        }
        else if (mouse == 2) {
            if (r == lab.getStartRow() && c == lab.getStartColumn()) // same start & exit
                return;
            lab.setExit(r, c);
        }
        else
            return;
        
        System.out.println("Valid mouse: "+r+" "+c+" Type "+mouse+" changed.");
        
        c = 0;
        repaint();
    }
    
    //Für Thread
    protected Thread threadfind;
   
    
    @Override
    public void run(){
        
        try{
            thepath = new ArrayList<>();
            thepath = lab.getPath();
        } catch (StackOverflowError e){
            System.err.println("Stackoverflow");
            stop();
            pcs.firePropertyChange("Stackoverflow", null, null);
        }
        while (isFinding) {
            
            if (c == thepath.size()-1) { // way found!
                isFinished = true;
                stop();
                
                if(lab.isProblemSolved() == true)
                    pcs.firePropertyChange("Found", null, null);
                else if(lab.isProblemSolved() == false)
                    pcs.firePropertyChange("NoWay", null, null);
                c = 0;
                break;
            }
                      
            try {
                Thread.sleep(t);
            } catch (InterruptedException ie) {
                // tue nichts
            }
                        
            repaint();
            c++;
        }
        threadfind = null;
    }
    
    public void start(){
        if (threadfind == null) {
            threadfind = new Thread(this);
            isFinding = true;
            threadfind.start();
        }
    }
    
    public void stop(){
        isFinding = false;
        pcs.firePropertyChange("Stopped", null, null);
    }
    
    

    
            /**
     * @return the sizerow
     */
    public int getRow() {
        return sizerow;
    }

    /**
     * @param row the sizerow to set
     */
    public void setRow(int row) {
        this.sizerow = row;
    }

    /**
     * @return the sizecolumn
     */
    public int getColumn() {
        return sizecolumn;
    }

    /**
     * @param column the sizecolumn to set
     */
    public void setColumn(int column) {
        this.sizecolumn = column;
    }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        int mouse = 0; // 1 = left, 2 = right
        mousex = evt.getX();
        mousey = evt.getY();
        if (SwingUtilities.isLeftMouseButton(evt) == true && isFinding == false) 
            mouse = 1;
        else if (SwingUtilities.isRightMouseButton(evt) == true && isFinding == false)           
            mouse = 2;
        else
            return;
        
        c = 0;
        isFinished = false;
        repaint();
        
        coordConv(mouse);
    }//GEN-LAST:event_formMouseClicked

    /* unused
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    */

    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    /* Property Change Listener */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl); 
    }
    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl); 
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
