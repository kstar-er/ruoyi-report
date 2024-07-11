package com.ruoyi.colorfulfog.modules.datasettransform.controller.param;

import lombok.Data;

import java.util.List;
@Data
public class PageParam {
    private Integer pageNumber = 1;
    private Integer pageSize = 10;
    private String order;
    private String sort;
    private Integer offset;
    private List<Long> ids;
}
