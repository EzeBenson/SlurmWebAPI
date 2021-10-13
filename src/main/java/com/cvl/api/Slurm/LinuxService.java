package com.cvl.api.Slurm;

import com.cvl.api.JSON.Model.RuntimeParameters;
import com.cvl.api.JSON.Model.SlurmParameters;
import com.cvl.api.Slurm.Parsing.SlurmControlParser;
import com.cvl.api.Slurm.Parsing.SlurmJobStatus;
import com.cvl.api.Slurm.Parsing.SlurmQueueParser;
import com.cvl.api.Slurm.Parsing.SlurmSBatchParser;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;

@Service
@Profile("production")
public class LinuxService implements PSExecutor {
    private static String executeCommand(String command)
    {
        System.out.println("Operation: "+ command);
        String result = "";
        try {
            Process ps = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim()).append("\n");
                System.out.println(line);
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public HashSet<Integer> getCurrentJobs()
    {
        return SlurmQueueParser.parseQueue(executeCommand("squeue"));
    }

    @Override
    public SlurmJobStatus getJobStatus(int jobid) {
        String commandResult = executeCommand("scontrol show job " + jobid);
        return SlurmControlParser.parseSControlShow(commandResult);
    }

    @Override
    public Integer executeJob(RuntimeParameters parameters, String inputJsonPath, String outputFolder) {
        SlurmParameters slurmParameters = parameters.getLimits().getSlurm();

        String slurmCommand = String.format("sbatch -p %s -q %s --gres=gpu:%d --wrap=\"%s %s %s\"",
                slurmParameters.getPartition(),
                slurmParameters.getQos(),
                slurmParameters.getGres(),
                parameters.getExec(),
                inputJsonPath,
                outputFolder);

        String slurmResult = executeCommand(slurmCommand);
        return SlurmSBatchParser.parseSBatch(slurmResult);
    }
}