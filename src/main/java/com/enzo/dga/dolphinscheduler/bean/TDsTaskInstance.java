package com.enzo.dga.dolphinscheduler.bean;

    import com.baomidou.mybatisplus.annotation.IdType;
    import com.baomidou.mybatisplus.annotation.TableId;
    import com.baomidou.mybatisplus.annotation.TableName;
    import java.io.Serializable;
    import java.util.Date;
    import lombok.Data;

/**
* <p>
    * 
    * </p>
*
* @author enzo
* @since 2024-08-03
*/
    @Data
    @TableName("t_ds_task_instance")
    public class TDsTaskInstance implements Serializable {

    private static final long serialVersionUID = 1L;

            /**
            * key
            */
            @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

            /**
            * task name
            */
    private String name;

            /**
            * task type
            */
    private String taskType;

            /**
            * task definition code
            */
    private Long taskCode;

            /**
            * task definition version
            */
    private Integer taskDefinitionVersion;

            /**
            * process instance id
            */
    private Integer processInstanceId;

            /**
            * Status: 0 commit succeeded, 1 running, 2 prepare to pause, 3 pause, 4 prepare to stop, 5 stop, 6 fail, 7 succeed, 8 need fault tolerance, 9 kill, 10 wait for thread, 11 wait for dependency to complete
            */
    private Byte state;

            /**
            * task submit time
            */
    private Date submitTime;

            /**
            * task start time
            */
    private Date startTime;

            /**
            * task end time
            */
    private Date endTime;

            /**
            * host of task running on
            */
    private String host;

            /**
            * task execute path in the host
            */
    private String executePath;

            /**
            * task log path
            */
    private String logPath;

            /**
            * whether alert
            */
    private Byte alertFlag;

            /**
            * task retry times
            */
    private Integer retryTimes;

            /**
            * pid of task
            */
    private Integer pid;

            /**
            * yarn app id
            */
    private String appLink;

            /**
            * job custom parameters
            */
    private String taskParams;

            /**
            * 0 not available, 1 available
            */
    private Byte flag;

            /**
            * retry interval when task failed 
            */
    private Integer retryInterval;

            /**
            * max retry times
            */
    private Integer maxRetryTimes;

            /**
            * task instance priority:0 Highest,1 High,2 Medium,3 Low,4 Lowest
            */
    private Integer taskInstancePriority;

            /**
            * worker group id
            */
    private String workerGroup;

            /**
            * environment code
            */
    private Long environmentCode;

            /**
            * this config contains many environment variables config
            */
    private String environmentConfig;

    private Integer executorId;

            /**
            * task first submit time
            */
    private Date firstSubmitTime;

            /**
            * task delay execution time
            */
    private Integer delayTime;

            /**
            * var_pool
            */
    private String varPool;

            /**
            * dry run flag: 0 normal, 1 dry run
            */
    private Byte dryRun;
}
