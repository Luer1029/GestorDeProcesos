package com.gestorprocesos.schedulers;

import com.gestorprocesos.models.SchedulerType;

public final class SchedulerFactory {
    private SchedulerFactory() {
    }

    public static Scheduler crear(SchedulerType tipo, int quantum) {
        return switch (tipo) {
            case FCFS -> new FcfsScheduler();
            case SJF -> new SjfScheduler();
            case ROUND_ROBIN -> new RoundRobinScheduler(quantum);
            case PRIORIDADES -> new PriorityScheduler();
        };
    }
}
