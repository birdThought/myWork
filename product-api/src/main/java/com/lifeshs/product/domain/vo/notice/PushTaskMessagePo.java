package com.lifeshs.product.domain.vo.notice;

import lombok.Data;

import java.util.Date;

public @Data class PushTaskMessagePo {
	
	private Integer id;
	private Integer sendId;
	private String receiceId;
	private String content;
	private String sendTime;
	private String cycle;
	private Integer status;
	private Date creactTime;
	
}
