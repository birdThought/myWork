package com.lifeshs.product.domain.dto.record;

import com.lifeshs.product.domain.po.record.TRecordDiet;
import com.lifeshs.product.domain.po.record.TRecordDietFood;

import java.util.List;

/**
 *  版权归
 *  TODO 饮食传输类--将饮食和详细的食物封装起来
 *  @author wenxian.cai 
 *  @datetime 2017年2月21日下午4:58:20
 */
public class DietDTO {
	
	/** 饮食实体*/
	private TRecordDiet recordDiet;
	
	/** 食物列表 */
	private List<TRecordDietFood> recordDietFoods;

	public TRecordDiet getRecordDiet() {
		return recordDiet;
	}

	public void setRecordDiet(TRecordDiet recordDiet) {
		this.recordDiet = recordDiet;
	}

	public List<TRecordDietFood> getRecordDietFoods() {
		return recordDietFoods;
	}

	public void setRecordDietFoods(List<TRecordDietFood> recordDietFoods) {
		this.recordDietFoods = recordDietFoods;
	}
	
	
}
