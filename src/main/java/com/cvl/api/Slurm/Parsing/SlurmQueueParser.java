package com.cvl.api.Slurm.Parsing;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SlurmQueueParser {

    private static Pattern jobIDPattern = Pattern.compile("(?m)^ +(?<id>[0-9]+) .*$");

    public static HashSet<Integer> parseQueue(String s) {
        Matcher m = jobIDPattern.matcher(s);
        HashSet<Integer> currentJobs = new HashSet<>();

        while (m.find()) {
            try {
                currentJobs.add(Integer.parseInt(m.group(1)));
            } catch (NumberFormatException ignored) { }
        }
        return currentJobs;
    }
}


