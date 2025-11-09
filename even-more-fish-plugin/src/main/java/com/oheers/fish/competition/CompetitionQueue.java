package com.oheers.fish.competition;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.api.AbstractFileBasedManager;
import com.oheers.fish.competition.configs.CompetitionConversions;
import com.oheers.fish.competition.configs.CompetitionFile;
import com.oheers.fish.fishing.rods.RodManager;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

public class CompetitionQueue extends AbstractFileBasedManager<CompetitionFile> {

    private final Map<Integer, CompetitionFile> competitions = new TreeMap<>();

    public CompetitionQueue() {
        super(RodManager.getInstance());
    }

    @Override
    protected void performPreLoadConversions() {
        new CompetitionConversions().performCheck();
    }

    @Override
    protected void loadItems() {
        loadItemsFromFiles(
                "competitions",
                CompetitionFile::new,
                CompetitionFile::getId,
                CompetitionFile::isDisabled
        );

        // Populate the competitions schedule
        competitions.clear();
        getItemMap().values().forEach(file -> {
            if (loadSpecificDayTimes(file)) {
                return;
            }
            if (loadRepeatedTiming(file)) {
                return;
            }
            EvenMoreFish.getInstance().debug(
                    Level.WARNING,
                    file.getFile().getName() + "'s timings are not configured properly. " +
                            "This competition will never automatically start."
            );
        });
    }

    @Override
    protected void logLoadedItems() {
        EvenMoreFish.getInstance().getLogger().info(
                "Loaded " + getItemMap().size() + " competition file(s) and " + competitions.size() + " scheduled competitions."
        );
    }

    public Map<Integer, CompetitionFile> getCompetitions() {
        return competitions;
    }

    private boolean loadSpecificDayTimes(@NotNull CompetitionFile file) {
        Map<DayOfWeek, List<String>> scheduledDays = file.getScheduledDays();
        if (scheduledDays.isEmpty()) {
            return false;
        }
        scheduledDays.forEach((day, times) ->
                times.forEach(time ->
                        competitions.put(generateTimeCode(day, time), file)
                )
        );
        return true;
    }

    private boolean loadRepeatedTiming(@NotNull CompetitionFile file) {
        List<String> repeatedTimes = file.getTimes();

        if (repeatedTimes.isEmpty()) {
            return false;
        }

        List<DayOfWeek> daysToUse = new ArrayList<>(Arrays.asList(DayOfWeek.values()));
        daysToUse.removeAll(file.getBlacklistedDays());

        for (String time : repeatedTimes) {
            for (DayOfWeek day : daysToUse) {
                competitions.put(generateTimeCode(day, time), file);
            }
        }
        return true;
    }

    public int generateTimeCode(DayOfWeek day, String tfh) {
        int beginning = Arrays.asList(DayOfWeek.values()).indexOf(day) * 24 * 60;
        if (tfh != null) {
            String[] time = tfh.split(":");
            if (time.length != 2) {
                return -1;
            }

            try {
                beginning += Integer.parseInt(time[0]) * 60;
                beginning += Integer.parseInt(time[1]);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return beginning;
    }

    public int getSize() {
        return competitions.size();
    }

    public int getNextCompetition() {
        int currentTimeCode = AutoRunner.getCurrentTimeCode();

        if (competitions.containsKey(currentTimeCode)) {
            return currentTimeCode;
        }

        Competition competition = new Competition(-1, CompetitionType.LARGEST_FISH);
        competitions.put(currentTimeCode, competition);

        int nextTimeCode = findNextCompetitionTimeCode(currentTimeCode);

        competitions.remove(currentTimeCode);
        return nextTimeCode;
    }

    private int findNextCompetitionTimeCode(int currentTimeCode) {
        List<Integer> timeCodes = new ArrayList<>(competitions.keySet());
        int position = timeCodes.indexOf(currentTimeCode);

        if (position == competitions.size() - 1) {
            return timeCodes.get(0);
        }

        return timeCodes.get(position + 1);
    }
}

