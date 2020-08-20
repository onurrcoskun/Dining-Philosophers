
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class DiningPhilosophers extends Application implements PhilosopherEventHandler
{

    private final Map<Integer, Circle> philosopherCircles = new TreeMap<>();
    private final Map<Integer, Circle> forkCircles = new TreeMap<>();
    private final Map<Integer, Text> philosopherTexts = new TreeMap<>();
    private final Map<Integer, Text> forkTexts = new TreeMap<>();
    private final TextArea txtInfo = new TextArea();

    public static void main(String[] args)
    {
        launch(args);
    }

    private void appendInfoLine(String line)
    {
        Platform.runLater(() -> txtInfo.appendText(line + "\n"));
    }

    private void initialize()
    {
        final int count = 5;
        ArrayList<Fork> forks = new ArrayList<>(count);
        ArrayList<Philosopher> philosophers = new ArrayList<>(count);

        for (int i = 1; i <= count; i++)
        {
            forks.add(new Fork(i));
        }

        for (int i = 1; i <= count; i++)
        {
            Fork leftFork = forks.get(i - 1);
            Fork rightFork = forks.get(i % count);
            Philosopher philosopher;

            if (i == count)
            {
                philosopher = new Philosopher(i, rightFork, leftFork);
            }
            else
            {
                philosopher = new Philosopher(i, leftFork, rightFork);
            }

            philosopher.setPhilosopherEventHandler(this);
            philosophers.add(philosopher);
        }

        try
        {
            Thread.sleep(2000L);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            philosophers.forEach(Thread::start);
        }
    }

    private void initializeInNewThread()
    {
        new Thread(this::initialize).start();
    }

    private HBox createCircles(Map<Integer, Circle> circleMap, Map<Integer, Text> textMap, Paint backgroundColor)
    {
        HBox box = new HBox(10d);

        for (int i = 1; i <= 5; i++)
        {
            StackPane stackPane = new StackPane();

            Circle circle = new Circle(50d, backgroundColor);
            circleMap.put(i, circle);

            Text text = new Text("");
            text.setFont(new Font(24d));
            text.setBoundsType(TextBoundsType.VISUAL);
            textMap.put(i, text);

            stackPane.getChildren().add(circle);
            stackPane.getChildren().add(text);

            box.getChildren().add(stackPane);

            if (i == 1)
            {
                HBox.setMargin(stackPane, new Insets(0d, 0d, 0d, 9d));
            }
        }

        return box;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font(20d));

        Insets labelMargin = new Insets(9d);

        Label lbl1 = new Label("PHILOSOPHERS");
        lbl1.setFont(new Font(20d));
        VBox.setMargin(lbl1, labelMargin);

        Label lbl2 = new Label("FORKS");
        lbl2.setFont(new Font(20d));
        VBox.setMargin(lbl2, labelMargin);

        HBox philosophers = createCircles(philosopherCircles, philosopherTexts, Color.DARKGRAY);
        HBox forks = createCircles(forkCircles, forkTexts, Color.GREEN);

        VBox box = new VBox();
        box.getChildren().add(lbl1);
        box.getChildren().add(philosophers);
        box.getChildren().add(lbl2);
        box.getChildren().add(forks);

        BorderPane root = new BorderPane();
        root.setCenter(box);
        root.setBottom(txtInfo);

        for (Map.Entry<Integer, Text> entry : philosopherTexts.entrySet())
        {
            entry.getValue().setText("" + entry.getKey());
        }

        primaryStage.setResizable(false);
        primaryStage.setTitle("Dining Philosophers");
        primaryStage.setScene(new Scene(root, 500d, 600d));
        primaryStage.show();

        initializeInNewThread();
    }

    @Override
    public void forkTaken(Philosopher philosopher, Fork fork, Direction direction)
    {
        appendInfoLine(String.format(
                "Filozof %d %s çatalı (%d) eline aldı.",
                philosopher.getNumber(),
                direction == Direction.LEFT ? "solundaki" : "sağındaki",
                fork.getNumber()));

        final Circle circle = forkCircles.get(fork.getNumber());
        if (circle != null)
        {
            circle.setFill(Color.RED);
        }

        final Text text = forkTexts.get(fork.getNumber());
        if (text != null)
        {
            text.setText("" + philosopher.getNumber());
        }
    }

    @Override
    public void forkReleased(Philosopher philosopher, Fork fork, Direction direction)
    {
        appendInfoLine(String.format(
                "Filozof %d %s çatalı (%d) bıraktı.",
                philosopher.getNumber(),
                direction == Direction.LEFT ? "solundaki" : "sağındaki",
                fork.getNumber()));

        final Circle circle = forkCircles.get(fork.getNumber());
        if (circle != null)
        {
            circle.setFill(Color.GREEN);
        }

        final Text text = forkTexts.get(fork.getNumber());
        if (text != null)
        {
            text.setText("");
        }
    }

    @Override
    public void eating(Philosopher philosopher)
    {
        appendInfoLine(String.format("Filozof %d yemek yiyor..", philosopher.getNumber()));

        final Circle circle = philosopherCircles.get(philosopher.getNumber());
        if (circle != null)
        {
            circle.setFill(Color.White);
        }
    }

    @Override
    public void thinking(Philosopher philosopher)
    {
        appendInfoLine(String.format("Filozof %d yemeğini yedi. Düşünüyor..", philosopher.getNumber()));

        final Circle circle = philosopherCircles.get(philosopher.getNumber());
        if (circle != null)
        {
            circle.setFill(Color.Black);
        }
    }

}
