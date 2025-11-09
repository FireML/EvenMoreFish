package com.oheers.fish.competition;

import com.oheers.fish.EvenMoreFish;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

public class CompetitionTimer {

    private final Timer timer = new Timer();
    private final Competition competition;

    public CompetitionTimer(@NotNull Competition competition) {
        this.competition = competition;
    }

    public void start() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                EvenMoreFish.getScheduler().runTask(() -> {
                    competition.getStatusBar().timerUpdate(competition.getTimeLeft(), competition.getMaxDuration());
                    if (competition.decreaseTime()) {
                        cancel();
                        timer.cancel();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(
            task,
            0,
            Duration.ofSeconds(1).toMillis()
        );
    }

    public void stop() {
        timer.cancel();
    }

}
