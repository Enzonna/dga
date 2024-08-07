package com.enzo.dga.dolphinscheduler.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("t_ds_task_definition")
public class TDsTaskDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * self-increasing id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * encoding
     */
    private Long code;

    /**
     * task definition name
     */
    private String name;

    /**
     * task definition version
     */
    private Integer version;

    /**
     * description
     */
    private String description;

    /**
     * project code
     */
    private Long projectCode;

    /**
     * task definition creator id
     */
    private Integer userId;

    /**
     * task type
     */
    private String taskType;

    /**
     * job custom parameters
     */
    private String taskParams;

    /**
     * 0 not available, 1 available
     */
    private Byte flag;

    /**
     * job priority
     */
    private Byte taskPriority;

    /**
     * worker grouping
     */
    private String workerGroup;

    /**
     * environment code
     */
    private Long environmentCode;

    /**
     * number of failed retries
     */
    private Integer failRetryTimes;

    /**
     * failed retry interval
     */
    private Integer failRetryInterval;

    /**
     * timeout flag:0 close, 1 open
     */
    private Byte timeoutFlag;

    /**
     * timeout notification policy: 0 warning, 1 fail
     */
    private Byte timeoutNotifyStrategy;

    /**
     * timeout length,unit: minute
     */
    private Integer timeout;

    /**
     * delay execution time,unit: minute
     */
    private Integer delayTime;

    /**
     * resource id, separated by comma
     */
    private String resourceIds;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * 从任务定义中提取出来的SQL
     */
    @TableField(exist = false)
    private String taskSql;
}
