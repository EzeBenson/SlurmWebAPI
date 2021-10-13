package com.cvl.api.Slurm.Parsing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SlurmControlParser {

    private static final Pattern showPattern = Pattern.compile(".*?JobId=(?<id>[0-9]+) [\\S\\s]*?JobState=(?<state>[A-Z]+) [\\S\\s]*?ExitCode=(?<returnval>[\\-0-9]+):[\\S\\s]*");

    public static SlurmJobStatus parseSControlShow(String s) {

        // Check for unknown slurm ID.
        if (s.contains("Invalid job")) {
            return null;
        }

        Matcher m = showPattern.matcher(s);

        if (m.matches()) {
            try {
                Integer id = Integer.parseInt(m.group("id"));
                String state = m.group("state");
                Integer returnValue = Integer.parseInt(m.group("returnval"));
                return new SlurmJobStatus(id, state, returnValue);
            } catch (NumberFormatException ex) {
                return null;
            }
        } else
        {
            return null;
        }
    }

}
