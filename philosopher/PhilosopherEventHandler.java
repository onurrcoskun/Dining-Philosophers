
public interface PhilosopherEventHandler
{

    void forkTaken(Philosopher philosopher, Fork fork, Direction direction);

    void forkReleased(Philosopher philosopher, Fork fork, Direction direction);

    void eating(Philosopher philosopher);

    void thinking(Philosopher philosopher);

}
