package com.itheima.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    public UserService userService;
    @Autowired
    public AddressBookService addressBookService;
    @Autowired
    public ShoppingCartService shoppingCartService;
    @Autowired
    public OrderDetailService orderDetailService;
    /**
     * 用户下单
     * @param orders
     */
    @Transactional
    public void submit(Orders orders){
        //获取当前用户信息，由此能获得此用户购物车中的数据
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0){
            throw  new CustomException("购物车为空，亲爱的,快下单吧!");
        }
        //查用户数据
        User user = userService.getById(userId);
        Long addressBookId = orders.getAddressBookId();
        //查地址数据，前端传过来地址id封装到orders中了
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null){
            throw new CustomException("地址信息有误，不能下单");
        }
        //订单明细
        //设置订单属性
        long orderId = IdWorker.getId();
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        orders.setId(orderId);
        orders.setUserName(user.getName());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(  (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                            +(addressBook.getCityName() == null ? "" : addressBook.getCityName())
                            +(addressBook.getDistrictName() == null ? "" : addressBook.getCityName())
                            +(addressBook.getDetail() == null ?"":addressBook.getDetail()));

        //向订单表插入数据库,一条
         this.save(orders);

        //向订单明细表中插入多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车
        shoppingCartService.remove(queryWrapper);
    }
}
