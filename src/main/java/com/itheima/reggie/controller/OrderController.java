package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.AddressBookService;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private AddressBookService addressBookService;
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("订单成功");
    }
    @GetMapping("/page")
    public R<Page<OrdersDto>> page(int page, int pageSize, String number, LocalDateTime beginTime,LocalDateTime endTime){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number != null,Orders::getNumber,number);
        queryWrapper.between(beginTime!= null && endTime!= null,Orders::getOrderTime,beginTime,endTime);
        queryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo,queryWrapper);


        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");
        List<Orders> records = pageInfo.getRecords();
        //item即为List中的每个dish对象
        List<OrdersDto> list = records.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            //设置订单的具体地址收货人。
//            LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
//            addressBookLambdaQueryWrapper.eq(AddressBook::getId,item.getAddressBookId());
//            AddressBook addressBook = addressBookService.getOne(addressBookLambdaQueryWrapper);
//           Long userId = addressBook.getUserId();
//            String phone = addressBook.getPhone();
//            String consignee = addressBook.getConsignee();
//            ordersDto.setPhone(phone);
//            ordersDto.setAddress(item.getAddress());
//            ordersDto.setConsignee(consignee);
            //设置订单的orderDetail.
            Long id = item.getId();
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,id);
            List<OrderDetail> detailList = orderDetailService.list(orderDetailLambdaQueryWrapper);
            if (detailList != null){
                ordersDto.setOrderDetails(detailList);
            }
            return ordersDto;
        }).collect(Collectors.toList());


        ordersDtoPage.setRecords(list);
        return R.success(ordersDtoPage);

    }
}
