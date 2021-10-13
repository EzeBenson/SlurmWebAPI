package com.cvl.api.Slurm.Parsing;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SlurmSBatchParser {

    private static Pattern sbatchPattern = Pattern.compile(".* (?<id>[0-9]+)[\\S\\s]*$");

    public static Integer parseSBatch(String s) {
        Matcher m = sbatchPattern.matcher(s);

        if (m.matches()) {
            try {
                return Integer.parseInt(m.group("id"));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }
}


