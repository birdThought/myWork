package com.lifeshs.product.service.healthData.impl;

import com.lifeshs.product.common.constants.healthData.DeviceType;
import com.lifeshs.product.utils.ListUtil;
import com.lifeshs.product.domain.dto.healthData.HealthPackageBaseDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 现有的健康产品
 */
public class Product {

    private static List<DeviceType> products;
    static {
        products = new ArrayList<>();
        // 血压、肺活、血糖、血氧、体脂、心率(手环)、体温
        products.add(DeviceType.BloodPressure);
        products.add(DeviceType.Oxygen);
        products.add(DeviceType.BodyFatScale);
        products.add(DeviceType.Band);
        products.add(DeviceType.Glucometer);
        products.add(DeviceType.Lunginstrument);
        products.add(DeviceType.Temperature);
    }

    /**
     *
     * 获取现有的健康包设备List集合
     *
     * @author yuhang.weng
     * @DateTime 2017年2月14日 上午9:30:41
     *
     * @return
     */
    public static List<DeviceType> getProducts() {
        return products;
    }

    /**
     * 获取现有的健康包设备名称数组
     *
     * @author yuhang.weng
     * @DateTime 2017年2月14日 上午9:32:25
     *
     * @return
     */
    public static List<String> listProductName() {
        List<String> names = new ArrayList<>();
        for (DeviceType d : products) {
            names.add(d.name());
        }

        return names;
    }

    /**
     * 获取已有设备的value值集合
     *
     * @author yuhang.weng
     * @DateTime 2017年2月14日 上午9:33:00
     *
     * @return
     */
    public static List<Integer> listProductValue() {
        List<Integer> values = new ArrayList<>();
        for (DeviceType d : getProducts()) {
            values.add(d.value());
        }
        return values;
    }

    /**
     * 获取现有的健康包设备（基础）对象List集合
     *
     * @author yuhang.weng
     * @DateTime 2017年2月14日 上午9:33:47
     *
     * @return
     */
    public static List<HealthPackageBaseDTO> listHealthPackageBaseDTO() {

        List<HealthPackageBaseDTO> hpbDTOs = new ArrayList<>();

        for (DeviceType d : products) {
            HealthPackageBaseDTO hpbDTO = new HealthPackageBaseDTO();
            hpbDTO.setName_cn(d.getName());
            hpbDTO.setName_en(d.name());
            hpbDTO.setDeviceValue(d.value());
            hpbDTO.setAbout(d.getAbout());
            hpbDTO.setShopUrl(d.getShopUrl());

            hpbDTOs.add(hpbDTO);
        }

        return hpbDTOs;
    }

    /**
     * 获取现有的健康包设备（基础）对象List集合,按照提供的deviceOrders排序
     *
     * @author yuhang.weng
     * @DateTime 2017年2月14日 上午9:34:08
     *
     * @param
     * @return
     */
    public static List<HealthPackageBaseDTO> listHealthPackageBaseDTO(List<Integer> orderList) {
        // 移除重复项
        orderList = ListUtil.removeRepeatElement(orderList, Integer.class);

        List<DeviceType> products = getProducts(); // 已有设备列表
        List<HealthPackageBaseDTO> hpbDTOs = new ArrayList<>(); // 返回数据List模型
        List<Integer> models_value = new ArrayList<>(); // 设备value值存储列表
        /**
         * 遍历 设备排序数组,根据数组中的值优先将模型按序插入models中 同时将已插入的模型对应的value值保存在models_value
         * 在遍历结束后，将没有插入的到models_value的设备,重新add到models中
         */
        for (Integer order : orderList) {
            for (DeviceType d : products) {
                if (order.equals(d.value())) {
                    HealthPackageBaseDTO hpbDTO = new HealthPackageBaseDTO();
                    hpbDTO.setName_cn(d.getName());
                    hpbDTO.setName_en(d.name());
                    hpbDTO.setDeviceValue(d.value());
                    hpbDTO.setAbout(d.getAbout());
                    hpbDTO.setShopUrl(d.getShopUrl());

                    hpbDTOs.add(hpbDTO);

                    models_value.add(order);
                    continue; // 查找到该元素就直接continue进入下一次循环
                }
            }
        }
        for (DeviceType d : products) {
            HealthPackageBaseDTO model = new HealthPackageBaseDTO();
            model.setName_cn(d.getName());
            model.setName_en(d.name());
            model.setDeviceValue(d.value());
            model.setAbout(d.getAbout());
            model.setShopUrl(d.getShopUrl());

            if (!models_value.contains(d.value())) {
                hpbDTOs.add(model);
            }
        }

        return hpbDTOs;
    }
}
