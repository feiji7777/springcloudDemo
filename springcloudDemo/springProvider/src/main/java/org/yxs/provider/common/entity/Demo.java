package org.yxs.provider.common.entity;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class Demo {
    private String id;
    private String name;
    private String keyWord;
    private String punchTime;
    private String salaryMoney;
    private String bonusMoney;
    private int sex;
    private int age;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;
    private String email;
    private String content;
    private String createBy;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String updateBy;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String sysOrgCode;
    private int tenantId;
    private int deletedFlag;
}
