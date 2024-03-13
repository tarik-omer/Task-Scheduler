/* Implement this class. */
import java.util.concurrent.PriorityBlockingQueue;

public class MyHost extends Host {
    // blocking queue for tasks - tasks are sorted by priority (highest priority first)
    private final PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>(10, (o1, o2) -> {
        if (o1.getPriority() == o2.getPriority()) {
            // if the priorities are equal, sort by id
            return o1.getId() < o2.getId() ? -1 : 1;
        }
        return o1.getPriority() > o2.getPriority() ? -1 : 1;
    });

    private boolean preempted = false;
    private Task currentTask = null;

    private boolean stop = false;
    private final int timeLimit = 20;

    private final Object lock = new Object();
    private final Object currentTaskLock = new Object();
    private final Object preemptedLock = new Object();


    // getter for current task
    public Task getCurrentTask() {
        return currentTask;
    }

    @Override
    public void run() {
        // run tasks in queue until time is up
        while (!stop) {
            // if the queue is empty, wait for a task to be added
            while (queue.isEmpty() && !stop) {
                synchronized (lock) {
                    try {
                        lock.wait(1000);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }

            // get the task with the highest priority
            synchronized (currentTaskLock) {
                currentTask = queue.poll();
            }
            if (currentTask == null) {
                continue;
            }

            // run the task - while loop until the task is finished or interrupted
            long startTime = (long)(Timer.getTimeDouble() * 1000);
            long timeToRun = currentTask.getLeft();
            long timeRan = (long)(Timer.getTimeDouble() * 1000 - startTime);
            while (timeRan < timeToRun && !stop) {
                timeRan = (long)(Timer.getTimeDouble() * 1000 - startTime);

                // update work time left
                currentTask.setLeft(timeToRun - timeRan);

                // if the task is preempted, stop running the task
                synchronized (preemptedLock) {
                    if (preempted) {
                        preempted = false;
                        break;
                    }
                }
            }

            // if the task is not finished, add it back to the queue
            if (timeRan < timeToRun) {
                currentTask.setLeft(timeToRun - timeRan);
                queue.add(currentTask);
            } else {
                // if the task is finished
                currentTask.finish();
                synchronized (currentTaskLock) {
                    currentTask = null;
                }
            }
        }
    }

    @Override
    public void addTask(Task task) {
        // add task to queue
        try {
            queue.add(task);
            synchronized (lock) {
                // debug print task id
                lock.notifyAll();
            }
        } catch (Exception e) {
            // ignore
        }

        // if the current task is preemptible, and there is a task running, interrupt the running task
        synchronized (currentTaskLock) {
            if (currentTask != null && currentTask.isPreemptible() && task.getPriority() > currentTask.getPriority()) {
                synchronized (preemptedLock) {
                    preempted = true;
                }
            }
        }
    }

    @Override
    public int getQueueSize() {
        // return queue size
        synchronized (currentTaskLock) {
            return queue.size() + (currentTask == null ? 0 : 1);
        }
    }

    @Override
    public long getWorkLeft() {
        // return work left
        long workLeft = 0;
        for (Task task : queue) {
            workLeft += task.getLeft();
        }
        synchronized (currentTaskLock) {
            if (currentTask != null) {
                workLeft += currentTask.getLeft();
            }
        }
        return workLeft;
    }

    @Override
    public void shutdown() {
        // stop the host
        stop = true;
    }
}