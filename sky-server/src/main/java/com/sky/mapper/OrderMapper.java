package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Mapper
public interface OrderMapper {

    /**
     * 插入数据
     * @param orders
     */
    void insert(Orders orders);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 分页条件查询并按下单时间排序
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查订单
     * @param id
     * @return
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    /**
     * 各个状态的订单数量统计
     * @param status
     * @return
     */
    @Select("select count(id) from orders where status =#{status}")
    Integer countStatus(Integer status);

    /**
     * 根据订单状态和时间查询订单
     * @param status
     * @param localDateTime
     * @return
     */
    @Select("select * from orders where status=#{status} and order_time < #{localDateTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime localDateTime);

    /**
     * 根据动态条件来查询营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);
    /**
     * 根据动态条件来查询订单数据
     * @param map
     * @return
     */

    Integer countByMap(Map map);
}
