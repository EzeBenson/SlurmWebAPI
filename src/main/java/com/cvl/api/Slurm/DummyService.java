package com.cvl.api.Slurm;

import com.cvl.api.JSON.Model.RuntimeParameters;
import com.cvl.api.Slurm.Parsing.SlurmControlParser;
import com.cvl.api.Slurm.Parsing.SlurmJobStatus;
import com.cvl.api.Slurm.Parsing.SlurmQueueParser;
import com.cvl.api.Slurm.Parsing.SlurmSBatchParser;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Profile("local")
public class DummyService implements PSExecutor {

    private HashMap<Integer, Job> currentJobs;
    private Random random;

    class Job {
        Integer jobID;
        String jobState;
        Integer returnValue;

        Job(String jobState, Integer returnValue) {
            this.jobState = jobState;
            this.returnValue = returnValue;
        }
    }

    public DummyService() {
        this.random = new Random();
        this.currentJobs = new HashMap<>();
    }

    @Override
    public HashSet<Integer> getCurrentJobs() {
        List<Integer> runningJobs = this.currentJobs.entrySet().stream()
                .filter(job -> job.getValue().jobState.equals("RUNNING"))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        StringBuilder coreOutput = new StringBuilder("             JOBID PARTITION     NAME     USER ST       TIME  NODES NODELIST(REASON)\n" +
                "            144430   general main_21.   psxfc1 PD       0:00      1 (QOSMaxGRESPerUser)\n" +
                "            126836  syngenta synrunne    pszaj  R 25-22:38:59      1 magneto\n" +
                "            126837  syngenta vrnrunne    pszaj  R 25-22:38:59      1 magneto\n" +
                "            143628   general  run8.sh   psxja7  R 2-13:22:46      1 havok\n" +
                "            144191    turing tests.sh   psxkk4  R 1-20:28:10      1 petra\n" +
                "            144194    turing tests.sh   psxkk4  R 1-20:27:20      1 petra\n" +
                "            144215   general tests.sh   psxkk4  R 1-19:44:40      1 beast\n" +
                "            144222    turing landmark   psxdm5  R 1-19:27:09      1 petra\n" +
                "            144231    turing landmark   psxdm5  R 1-19:17:35      1 petra\n" +
                "            144243   general tests.sh   psxkk4  R 1-18:46:26      1 havok\n" +
                "            144262   general tests.sh   psxkk4  R 1-18:03:42      1 beast\n" +
                "            144273   general landmark   psxdm5  R 1-17:50:42      1 rogue\n" +
                "            144341   general   python    psxin  R 1-16:30:55      1 storm\n" +
                "            144423   general main_0.s   psxfc1  R 1-14:09:07      1 rogue\n" +
                "            144425   general main_6.s   psxfc1  R 1-14:09:01      1 rogue\n" +
                "            144426   general main_9.s   psxfc1  R 1-14:08:57      1 rogue\n" +
                "            144427   general main_12.   psxfc1  R 1-14:08:54      1 gambit\n");

        for (Integer jobId : runningJobs) {
            String lineFormat = "%18d       all testjobs   slurmw  R 1-14:08:54      1 gambit\n";
            coreOutput.append(String.format(lineFormat, jobId));
        }

        return SlurmQueueParser.parseQueue(coreOutput.toString());
    }

    @Override
    public SlurmJobStatus getJobStatus(int jobid) {
        if (!currentJobs.containsKey(jobid)) {
            return SlurmControlParser.parseSControlShow("Invalid job id specified");
        }

        Job job = currentJobs.get(jobid);

        String coreOutput =  "JobId=%d JobName=wheatseg\n" +
                "   UserId=pszaj(34944) GroupId=pszaj(34944) MCS_label=N/A\n" +
                "   Priority=4294740651 Nice=0 Account=researcher QOS=all\n" +
                "   JobState=%s Reason=None Dependency=(null)\n" +
                "   Requeue=0 Restarts=0 BatchFlag=1 Reboot=0 ExitCode=%d:0\n" +
                "   RunTime=01:24:05 TimeLimit=365-00:00:00 TimeMin=N/A\n" +
                "   SubmitTime=2019-08-12T12:35:35 EligibleTime=2019-08-12T12:35:35\n" +
                "   AccrueTime=2019-08-12T12:35:35\n" +
                "   StartTime=2019-08-12T12:35:35 EndTime=2020-08-11T12:35:35 Deadline=N/A\n" +
                "   PreemptTime=None SuspendTime=None SecsPreSuspend=0\n" +
                "   LastSchedEval=2019-08-12T12:35:35\n" +
                "   Partition=syngenta AllocNode:Sid=xavier:32346\n" +
                "   ReqNodeList=(null) ExcNodeList=(null)\n" +
                "   NodeList=magneto\n" +
                "   BatchHost=magneto\n" +
                "   NumNodes=1 NumCPUs=1 NumTasks=1 CPUs/Task=1 ReqB:S:C:T=0:0:*:*\n" +
                "   TRES=cpu=1,node=1,billing=1,gres/gpu=2\n" +
                "   Socks/Node=* NtasksPerN:B:S:C=0:0:*:* CoreSpec=*\n" +
                "   MinCPUsNode=1 MinMemoryNode=0 MinTmpDiskNode=0\n" +
                "   Features=(null) DelayBoot=00:00:00\n" +
                "   OverSubscribe=YES Contiguous=0 Licenses=(null) Network=(null)\n" +
                "   Command=/home/pszaj/proj-instance-segmentation/wheatheads/lua-training/train.                                                                sh\n" +
                "   WorkDir=/home/pszaj/proj-instance-segmentation/wheatheads/lua-training\n" +
                "   StdErr=/home/pszaj/proj-instance-segmentation/wheatheads/lua-training/slurm-1                                                                82463.out\n" +
                "   StdIn=/dev/null\n" +
                "   StdOut=/home/pszaj/proj-instance-segmentation/wheatheads/lua-training/slurm-1                                                                82463.out\n" +
                "   Power=\n" +
                "   TresPerNode=gpu:2\n" +
                "\n";

        String slurmOutput = String.format(coreOutput, jobid, job.jobState, job.returnValue == null ? 0 : job.returnValue);
        return SlurmControlParser.parseSControlShow(slurmOutput);
    }

    @Override
    public Integer executeJob(RuntimeParameters parameters, String inputJsonPath, String outputFolder) {
        // Generate new job ID somewhere above existing IDs
        Integer newJobId = 144500 + random.nextInt(10000000);

        Job newJob = new Job("RUNNING", null);
        this.currentJobs.put(newJobId, newJob);
        return SlurmSBatchParser.parseSBatch(String.format("Submitted batch job %d\n", newJobId));
    }

    @Scheduled(fixedRate = 10000)
    private void UpdateJobs() {
        // DummyService completes jobs at random
        this.currentJobs.forEach((key, currentJob) -> {
            if (currentJob.jobState.equals("RUNNING")) {
                // Occasionally complete job
                if (random.nextDouble() < 0.3) {
                    // Rarely fail
                    if (random.nextDouble() < 0.9) {
                        currentJob.jobState = "COMPLETED";
                        currentJob.returnValue = 0;
                    } else {
                        currentJob.jobState = "FAILED";
                        currentJob.returnValue = -1;
                    }
                }
            }
        });
    }
}
