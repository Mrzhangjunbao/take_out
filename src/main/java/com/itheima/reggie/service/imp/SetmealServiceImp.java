package com.itheima.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImp extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;


    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //先保存套餐的信息。
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item)->{
            //记得这里的每个SetmealDish对象中的对应套餐id（setmealId）是没有值的。
            //因为它是上面保存成功后才有的，
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //将套餐对应的菜品信息保存到setmeal_dish表中
        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<Setmeal>();
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
        //起售才能删除
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(setmealLambdaQueryWrapper);
        if (count > 0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //删除setmeal中的记录
        this.removeByIds(ids);
        //删除setmeal_dish中的关联数据

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper);

    }
}
