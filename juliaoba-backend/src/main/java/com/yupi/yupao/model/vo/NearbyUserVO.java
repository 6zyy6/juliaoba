package com.yupi.yupao.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 附近用户展示对象
 */
@Data
public class NearbyUserVO implements Serializable {

    private static final long serialVersionUID = 1752699116858923254L;

    /**
     * 用户id
     */
    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 标签列表 json
     */
    private String tags;

    /**
     * 与用户的距离（单位：千米）
     */
    private Double distance;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 上次位置更新时间
     */
    private Date lastLocationUpdateTime;
} 