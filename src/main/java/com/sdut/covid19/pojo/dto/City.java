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
@AllArgsConstructor
public class City {

    private String name;
    private Integer symbolSize;
    private Integer value;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        City city = (City) o;
        return Objects.equals(name, city.name);
    }

}
