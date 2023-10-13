package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //保存菜品及关联口味
    public void saveWithFlavor(DishDto dishDto);
    //修改菜品及关联口味
    public void updateWithFlavor(DishDto dishDto);
    //获取菜品及关联口味
    public DishDto getByIdWithFlavor(Long id);
    //删除菜品及关联口味
    public void deleteWithDish(List<Long> ids);
}
