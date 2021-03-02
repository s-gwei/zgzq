package org.jeecg.modules.P2020052.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@Service
public class HttpClient {

	@Autowired
    RestTemplate restTemplate;

	public List<Map<String, Object>> client(String url, HttpMethod method, Map<String, Object> params) {
		// 修改字符集
		List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
		for (HttpMessageConverter<?> httpMessageConverter : list) {
			if (httpMessageConverter instanceof StringHttpMessageConverter) {
				((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(Charset.forName("utf-8"));
				break;
			}
		}
		// 获取json字符串类型数据
 		String result = restTemplate.getForObject(url, String.class, params);

//		String result = "[{\"OT\":[{\"指标编码\":\"ST-0523\",\"关建度\":1.0,\"完成状态\":0,\"汇报困难度\":0.0,\"标准偏差值\":0.6,\"id\":\"OR:ext.st.pmgt.indicator.model.STProjectInstanceOTIndicator:2210\",\"汇报偏差值\":0.0,\"广度\":1.0,\"标准困难度\":0.01},{\"指标编码\":\"ST-0842\",\"关建度\":1.0,\"完成状态\":0,\"汇报困难度\":0.0,\"标准偏差值\":0.6,\"id\":\"OR:ext.st.pmgt.indicator.model.STProjectInstanceOTIndicator:2369\",\"汇报偏差值\":0.0,\"广度\":1.0,\"标准困难度\":0.8}],\"IN\":[{\"对应ot指标id\":[\"OR:ext.st.pmgt.indicator.model.STProjectInstanceOTIndicator:2407\"],\"权重\":1.0,\"id\":\"OR:ext.st.pmgt.indicator.model.STProjectInstanceINIndicator:2586\"},{\"对应ot指标id\":[\"OR:ext.st.pmgt.indicator.model.STProjectInstanceOTIndicator:2408\"],\"权重\":1.0,\"id\":\"OR:ext.st.pmgt.indicator.model.STProjectInstanceINIndicator:2587\"}],\"任务id\":\"OR:com.pisx.tundra.pmgt.plan.model.PIPlanActivity:391\",\"任务名称\":\"B7-02-1-性能指标分解\"}]\n";
		// 对字符串进行解析
		System.out.println(result);
		List<Map<String, Object>> map = JSONObject.parseObject(result, new TypeReference<List<Map<String, Object>>>() {
		});
		return map;

	}

}
