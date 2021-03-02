package org.jeecg.modules.message.handle.impl;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import org.jeecg.common.util.DySmsEnum;
import org.jeecg.common.util.DySmsHelper;
import org.jeecg.modules.message.handle.ISendMsgHandle;

public class SmsSendMsgHandle implements ISendMsgHandle {

	@Override
	public void SendMsg(String es_receiver, String es_title, String es_content) throws ClientException {
		// TODO Auto-generated method stub
		System.out.println("发短信");



	}

}
