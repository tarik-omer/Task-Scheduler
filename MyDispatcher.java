/* Implement this class. */

import java.util.List;

public class MyDispatcher extends Dispatcher {

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    private int lastAssignedHostIndex = -1;

    @Override
    public void addTask(Task task) {
        // based on the scheduling algorithm, assign the task to a host: RoundRobin, ShortestQueue, LeastWorkLeft, Size Interval Task Assignment
        switch (algorithm) {
            case ROUND_ROBIN:
                // RoundRobin: assign the task to the next host in a circular manner
                roundRobin(task);
                break;
            case SHORTEST_QUEUE:
                // ShortestQueue: assign the task to the host with the shortest queue
                shortestQueue(task);
                break;
            case LEAST_WORK_LEFT:
                // LeastWorkLeft: assign the task to the host with the least work left
                leastWorkLeft(task);
                break;
            case SIZE_INTERVAL_TASK_ASSIGNMENT:
                // Size Interval Task Assignment: assign the task to the host with the corresponding index
                sizeIntervalTaskAssignment(task);
                break;
        }
    }

    private void roundRobin(Task task) {
        // Find the next available host in a circular manner
        int nextHostIndex = (lastAssignedHostIndex + 1) % hosts.size();

        // Assign the task to the next host
        hosts.get(nextHostIndex).addTask(task);

        // Update lastAssignedHostIndex accordingly
        lastAssignedHostIndex = nextHostIndex;
    }

    private void shortestQueue(Task task) {
        // find the host with the shortest queue
        int shortestQueueIndex = 0;

        for (int i = 1; i < hosts.size(); i++) {
            // count the running task as well
            if (hosts.get(i).getQueueSize() < hosts.get(shortestQueueIndex).getQueueSize()) {
                shortestQueueIndex = i;
            }
        }

        // assign the task to the host with the shortest queue
        hosts.get(shortestQueueIndex).addTask(task);
    }

    private void leastWorkLeft(Task task) {
        // find the host with the least work left
        int leastWorkLeftIndex = 0;
        long leastWorkLeft = hosts.get(leastWorkLeftIndex).getWorkLeft();

        for (int i = 1; i < hosts.size(); i++) {
            // get the work left of the current host in seconds
            long currentWorkLeft = hosts.get(i).getWorkLeft();

            if (currentWorkLeft <= leastWorkLeft) {
                leastWorkLeftIndex = i;
                leastWorkLeft = currentWorkLeft;
            }
        }

        // assign the task to the host with the least work left
        hosts.get(leastWorkLeftIndex).addTask(task);
    }

    private void sizeIntervalTaskAssignment(Task task) {
        // find the corresponding host based on the length interval of the task
        int index = 0;
        TaskType size = task.getType();
        index = switch (size) {
            case SHORT -> 0;
            case MEDIUM -> 1;
            case LONG -> 2;
        };

        // assign the task to the host with the corresponding index
        hosts.get(index).addTask(task);
    }
}
