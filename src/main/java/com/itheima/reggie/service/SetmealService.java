package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //保存套餐
    public void saveWithDish(SetmealDto setmealDto);

    //删除套餐
    public void removeWithDish(List<Long> ids);
}
