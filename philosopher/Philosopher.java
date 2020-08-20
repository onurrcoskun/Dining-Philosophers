
import java.util.Random;

public class Philosopher extends Thread
{

    private final Random random = new Random();

    private final int number;
    private final Fork leftFork;
    private final Fork rightFork;

    private PhilosopherEventHandler philosopherEventHandler = null;

    public Philosopher(int number, Fork leftFork, Fork rightFork)
    {
        this.number = number;
        this.leftFork = leftFork;
        this.rightFork = rightFork;

        setName("philosopher-" + number);
        setDaemon(true);
    }

    public int getNumber()
    {
        return number;
    }

    public Fork getLeftFork()
    {
        return leftFork;
    }

    public Fork getRightFork()
    {
        return rightFork;
    }

    public PhilosopherEventHandler getPhilosopherEventHandler()
    {
        return philosopherEventHandler;
    }

    public void setPhilosopherEventHandler(PhilosopherEventHandler philosopherEventHandler)
    {
        this.philosopherEventHandler = philosopherEventHandler;
    }

    private void sleepMe(long ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private void eat()
    {
        if (philosopherEventHandler != null)
        {
            philosopherEventHandler.eating(this);
        }

        sleepMe(2000L + random.nextInt(4000));

        if (philosopherEventHandler != null)
        {
            philosopherEventHandler.thinking(this);
        }
    }

    private void forkTaken(Fork fork, Direction direction)
    {
        if (philosopherEventHandler != null)
        {
            philosopherEventHandler.forkTaken(this, fork, direction);
        }
    }

    private void forkReleased(Fork fork, Direction direction)
    {
        if (philosopherEventHandler != null)
        {
            philosopherEventHandler.forkReleased(this, fork, direction);
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            synchronized (leftFork)
            {
                // Soldaki çatalım elimde
                forkTaken(leftFork, Direction.LEFT);

                synchronized (rightFork)
                {
                    // Sağdaki çatalım elimde
                    forkTaken(rightFork, Direction.RIGHT);

                    // Yemek yiyebilirim
                    eat();

                    // Sağdaki çatalı bırakıyorum..
                    forkReleased(rightFork, Direction.RIGHT);
                }

                // Soldaki çatalı bırakıyorum..
                forkReleased(leftFork, Direction.LEFT);
            }

            sleepMe(2000L);
        }
    }

}

