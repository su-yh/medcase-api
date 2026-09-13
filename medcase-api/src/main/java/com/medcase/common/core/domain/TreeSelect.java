package com.medcase.common.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.medcase.system.entity.SysDeptEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Treeselect树结构实体类
 * 
 */
@Data
public class TreeSelect implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 节点ID */
    private Long id;

    /** 节点名称 */
    private String label;

    /** 节点启用状态 */
    private Boolean enabled = Boolean.TRUE;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public TreeSelect() {


    }

    public TreeSelect(SysDeptEntity dept) {

        this.id = dept.getDeptId();
        this.label = dept.getDeptName();
        this.enabled = dept.getEnabled();
        this.children = dept.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

}
