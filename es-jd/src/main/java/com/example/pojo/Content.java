package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xu
 * @Date: 2021-04-20 15:49
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    private String title;

    private String img;

    private String price;
}
