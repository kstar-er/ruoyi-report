package com.ruoyi.colorfulfog.model.vo;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagedListVO<T>{
    /**
     * 分页信息
     */
    private PageInfo pageInfo;
}