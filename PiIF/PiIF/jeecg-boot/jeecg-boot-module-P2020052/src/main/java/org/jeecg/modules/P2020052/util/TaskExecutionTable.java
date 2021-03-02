package org.jeecg.modules.P2020052.util;

import org.jeecg.modules.P2020052.service.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author sungw
 * @Data 2020年10月14号
 * @Description 任务执行评价表
 * @Email gsun@pisx.com
 */
@Component
public class TaskExecutionTable {

	// 请求地址

	@Value("${TaskUrl}")
	private String TaskUrl;
	public HttpMethod httpMethod = HttpMethod.GET;
	public Map<String, Object> params = new HashMap<>();

	@Autowired
	HttpClient httpClient;

	// 输入质量影响因子
	public List<Map<String, String>> QualityInfluenceFactor(String planId) {
		// 调用spd接口查询某个项目计划下数据
		List<Map<String, String>> result = api(planId);
		for (Map map : result) {
			// 权重
			double weight = Double.parseDouble(map.get("权重").toString());
			// 标准偏差
			double standardDeviation = Double.parseDouble(map.get("标准偏差").toString());
			// 汇报偏差
			double reportingDeviations = Double.parseDouble(map.get("汇报偏差").toString());
			// 计算得出质量影响因子
			DecimalFormat df = new DecimalFormat("0.00");// 设置保留位数
			String qualityInfluenceFactor = df.format(weight * (reportingDeviations / standardDeviation));
			map.put("影响质量因子", qualityInfluenceFactor);
		}

		return result;

	}

	// 输出质量KPI
	public List<Map<String, String>> OutputaualityKPI(String planId) {
		// 调用spd接口查询某个项目计划下数据
		List<Map<String, String>> result = api(planId);

		for (Map map : result) {
			// 标准偏差
			double standardDeviation = Double.parseDouble(map.get("标准偏差").toString());
			// 汇报偏差
			double reportingDeviations = Double.parseDouble(map.get("汇报偏差").toString());
			// 汇报困难度
			double reportingDifficulty = Double.parseDouble(map.get("汇报困难度").toString());
			// 标准困难度
			double standardDifficulty = Double.parseDouble(map.get("标准困难度").toString());

			// 计算得出输出质量KPI
			DecimalFormat df = new DecimalFormat("0.00");// 设置保留两位位数
			String OutputaualityKPI = df
					.format((reportingDeviations / standardDeviation) * (reportingDifficulty / standardDifficulty));

			map.put("输出质量KPI", OutputaualityKPI);

		}
		return result;

	}

	// 输出质量风险
	public List<Map<String, String>> OutputQualityRisk(String planId) {
		// 调用spd接口查询某个项目计划下数据
		List<Map<String, String>> result = api(planId);
		for (Map map : result) {
			// 标准偏差
			double standardDeviation = Double.parseDouble(map.get("标准偏差").toString());
			// 汇报偏差
			double reportingDeviations = Double.parseDouble(map.get("汇报偏差").toString());
			// 汇报困难度
			double reportingDifficulty = Double.parseDouble(map.get("汇报困难度").toString());
			// 标准困难度
			double standardDifficulty = Double.parseDouble(map.get("标准困难度").toString());

			// 计算输出质量风险
			DecimalFormat df = new DecimalFormat("0.00");// 设置保留两位位数
			String OutputQualityRisk = df.format(((abs(reportingDeviations - standardDeviation)) * 10 + 1)
					* ((abs(reportingDifficulty - standardDifficulty)) * 10 + 1));
			map.put("OutputQualityRisk", OutputQualityRisk);

		}
		// 计算输出质量风险之和

		return result;

	}

	// 输出质量风险之和
	public double OutputQualityRiskSum(List<Object> planIds) {
		// 调用spd接口查询某个项目计划下数据
		//getDataByPlan
		double sum = 0;
		for(Object obj : planIds) {
			String url = TaskUrl + "?planId=" + obj;
			List<Map<String, Object>> result = httpClient.client(url, httpMethod, params);
			for (Map map : result) {
				// 标准偏差
				List<Map<String, Object>> OT = (List<Map<String, Object>>) map.get("OT");
				for (Map mapot : OT) {
					double standardDeviation = Double
							.parseDouble(mapot.get("标准偏差值") == null ? "0" : mapot.get("标准偏差值").toString());
					// 汇报偏差
					double reportingDeviations = Double
							.parseDouble(mapot.get("汇报偏差值") == null ? "0" : mapot.get("汇报偏差值").toString());
					// 汇报困难度
					double reportingDifficulty = Double
							.parseDouble(mapot.get("汇报困难度") == null ? "0" : mapot.get("汇报困难度").toString());
					// 标准困难度
					double standardDifficulty = Double
							.parseDouble(mapot.get("标准困难度") == null ? "0" : mapot.get("标准困难度").toString());

					// 计算输出质量风险
					DecimalFormat df = new DecimalFormat("0.00");// 设置保留两位位数
					String OutputQualityRisk = df.format(((abs(reportingDeviations - standardDeviation)) * 10 + 1)
							* ((abs(reportingDifficulty - standardDifficulty)) * 10 + 1));
					// 计算输出质量风险之和
					sum += Double.parseDouble(OutputQualityRisk);
				}

			}
		}
		return sum;

	}

	// 计算绝对值
	public double abs(double a) {
		return (a > 0) ? a : -a;
	}

	// 总质量KPI
	public String TotalQualityKPI(String planId) {
		// 先调用输出质量KPI方法
		List<Map<String, String>> result = OutputaualityKPI(planId);
		double total = 0;
		int i = 0;
		for (Map map : result) {
			total += Double.parseDouble(map.get("输出质量KPI").toString());
			i++;
		}
		DecimalFormat df = new DecimalFormat("0.00");// 设置保留两位位数
		String totalQualityKPI = df.format(total / i);
		return totalQualityKPI;

	}

	// 项目风险指标
	public List<Map<String, String>> ProjectRiskIndicators(String planId) {
		// 先计算输出质量风险
		List<Map<String, String>> result = OutputQualityRisk(planId);
		for (Map map : result) {
			// 输出质量风险
			double OutputQualityRisk = Double.parseDouble(map.get("输出质量风险").toString());
			// 广度
			double span = Double.parseDouble(map.get("广度").toString());
			// 关键度
			double Criticality = Double.parseDouble(map.get("关键度").toString());

			// 计算项目风险指标
			DecimalFormat df = new DecimalFormat("0.00");// 设置保留两位位数
			String ProjectRiskIndicators = df.format((OutputQualityRisk - 1) * span * Criticality);
			map.put("项目风险指标", ProjectRiskIndicators);
		}
		return result;

	}

	public List<Map<String, String>> api(String planId) {
		// TODO Auto-generated method stub
		return null;
	}

}
