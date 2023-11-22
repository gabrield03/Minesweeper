public class Driver implements Runnable{

    GUI gui = new GUI();

    public static void main(String args[])
    {
        new Thread(new Driver()).start();
    }

    @Override
    public void run()
    {
        while(true)
        {
            gui.repaint();
        }
    }
}