package com.yupi.yupao.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 标签分类VO
 */
@Data
public class TagCategoryVO implements Serializable {
    /**
     * 分类名称
     */
    private String name;

    /**
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
} 