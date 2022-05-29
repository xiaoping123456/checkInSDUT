package com.sdut.covid19.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

/**
 * @author 小王造轮子
 * @description
 * @date 2022/5/29
 */
@Data
public class Way {

    private String source;
    private String target;
    private LineStyle lineStyle;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Way way = (Way) o;
        return Objects.equals(source, way.source) &&
                Objects.equals(target, way.target);
    }

}

