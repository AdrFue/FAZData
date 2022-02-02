import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

//    using once
//    public static void main(String[] args) {
//        WebHandler.runPgm();
//        MySqlHandler.insert();
//    }

    long delay = 60 * 1000; // delay in milliseconds, here 60 seconds
    Main.LoopTask task = new Main.LoopTask();
    Timer timer = new Timer("TaskName");

    public void start() {
        timer.cancel();
        timer = new Timer("TaskName");
        Date executionDate = new Date(); // no params = now
        timer.scheduleAtFixedRate(task, executionDate, delay);
    }

    private class LoopTask extends TimerTask {
        public void run() {
            WebHandler.runPgm();
            MySqlHandler.insert();
        }
    }

    public static void main(String[] args) {
        Main executingTask = new Main();
        executingTask.start();
    }

}
