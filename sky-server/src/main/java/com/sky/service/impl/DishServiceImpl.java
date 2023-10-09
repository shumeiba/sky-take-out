package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;

import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    SetmealDishMapper setmealDIshMapper;
    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    @Transient//开启注解式的事务管理
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //向菜单表插入一条数据
        dishMapper.insert(dish);


        //获取insert生成的主键值
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }
    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO){
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        if (dishDTO.getFlavors() != null){
            DishFlavor dishFlavor = DishFlavor.builder()
                    .dishId(dishDTO.getId())
                    .name(dishDTO.getName())
                    .build();
            dishFlavorMapper.update();
        }
    }
    /**
     * 菜品的批量删除
     * @param ids
     */
    @Override
    @Transient
    public void delete(List<Long> ids) {
        //判断当前菜品是否能删除
        for (Long id : ids) {
           Dish dish= dishMapper.getById(id);
           if(dish.getStatus() == StatusConstant.ENABLE){
               //起售中的菜品不能删除
               throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
           }
        }
        //判断是否被套餐关联
        List<Long> selmealIdByDishIds = setmealDIshMapper.getSelmealIdByDishIds(ids);
        if (selmealIdByDishIds != null && selmealIdByDishIds.size() >0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
//        for (Long id : ids) {
//            //可以删除菜品表数据
//            dishMapper.deleteById(id);
//            //删除口味表关联数据
//            dishFlavorMapper.deleteByDishId(id);
//        }
        //根据菜品ID批量删除菜品
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }
}
