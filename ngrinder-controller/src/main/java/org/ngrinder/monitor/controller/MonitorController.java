/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.ngrinder.monitor.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ngrinder.common.controller.NGrinderBaseController;
import org.ngrinder.monitor.controller.model.SystemDataModel;
import org.ngrinder.monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Monitor controller.
 * 
 * @author Mavlarn
 * @since 3.0
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController extends NGrinderBaseController {

	@Autowired
	private MonitorService monitorService;

	/**
	 * Get monitor data of agents.
	 * 
	 * @param model
	 *            model
	 * @param testId
	 *            test Id
	 * @param monitorIP
	 *            monitorIP
	 * @param imgWidth
	 *            image width
	 * @return json message
	 */
	@RequestMapping("/getMonitorData")
	@ResponseBody
	public String getMonitorData(ModelMap model, @RequestParam(required = true) long testId,
					@RequestParam(required = true) String monitorIP, @RequestParam int imgWidth) {
		Map<String, Object> rtnMap = new HashMap<String, Object>(7);
		rtnMap.put("SystemData", this.getMonitorDataSystem(testId, monitorIP, imgWidth));
		rtnMap.put(JSON_SUCCESS, true);
		return toJson(rtnMap);
	}

	private Map<String, Object> getMonitorDataSystem(long testId, String monitorIP, int imgWidth) {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		List<SystemDataModel> systemMonitorData = monitorService.getSystemMonitorData(testId, monitorIP);
		if (imgWidth < 100) {
			imgWidth = 100;
		}
		if (null != systemMonitorData && !systemMonitorData.isEmpty()) {
			int dataAmount = systemMonitorData.size();
			int pointCount = imgWidth;
			int interval = dataAmount / pointCount;
			if (interval == 0) {
				pointCount = dataAmount;
				interval = 1;
			}
			List<Object> cpuData = new ArrayList<Object>(pointCount);
			List<Object> memoryData = new ArrayList<Object>(pointCount);

			SystemDataModel sdm;
			for (int i = 0; i < dataAmount; i += interval) {
				sdm = systemMonitorData.get(i);
				cpuData.add(sdm.getCpuUsedPercentage());
				memoryData.add(sdm.getTotalMemory() - sdm.getFreeMemory());
			}

			rtnMap.put("cpu", cpuData);
			rtnMap.put("memory", memoryData);
			rtnMap.put("interval", interval);
		}

		return rtnMap;
	}
}
