### Copyright 2023 - 2024 Omer Tarik Ilhan 334CA

# Load Balancer

## Description

This project is a load balancer that distributes the given tasks to a pool of worker nodes:
hosts. Each host runs on a different thread and has a queue of tasks. The load balancer
sends the tasks to the hosts in one of the following ways:

- **Round Robin** - the tasks are distributed to the hosts in order, one by one.
- **Shortest Queue** - the tasks are distributed to the host with the shortest queue.
- **Size Interval** - the tasks are distributed to the hosts with queues of a certain size.
- **Least Work Left** - the tasks are distributed to the host with the least amount of work left.

## Implementation

The project is implemented using Java. The main function starts the load balancer, the hosts
and the task generators.

The load balancer has an addTask method that adds a task to the queue of the host that is
chosen by the current distribution policy. Then, the host is notified that a new task has been
added to its queue. Each task has an execution time and a priority. The priority is used to
determine the order in which the tasks are executed. The tasks with the highest priority are
executed first. If a task with a higher priority is added to the queue of a host and the
current task is preemptable, the current task is preempted and the new task is executed. Otherwise,
the new task is added to the queue of the host. Each task runs until it is finished or it is
preempted. The hosts run until the shutdown method is called.

## Concurrency

Since the hosts and task generators run on different threads, communicating through the load
balancer, the load balancer must be thread-safe. Also, since the addTask method of the host
is called by the load balancer, the host must be thread-safe. The thread-safety is achieved
using Objects used as locks, synchronized keyword, wait and notify methods and
a PriorityBlockingQueue for each host.

## Challenges

The main challenge was to create the hosts, anti-climatically. The hosts need to be very
thread-safe, since any mistake could lead to a wrong distribution of tasks. The hosts also
need to be very efficient, since they are the ones that do the actual work. The single most
hindering factor was to figure out how to 'run' a task that does not do anything, and also
to make it preemptable.

## Conclusion

The project was a good opportunity and challenge to learn about concurrency and thread-safety in
Java.