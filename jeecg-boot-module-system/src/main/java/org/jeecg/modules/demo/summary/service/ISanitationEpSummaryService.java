package org.jeecg.modules.demo.summary.service;

import org.jeecg.modules.demo.summary.entity.SanitationEpSummary;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 环卫防疫汇总表
 * @Author: jeecg-boot
 * @Date:   2020-02-21
 * @Version: V1.0
 */
public interface ISanitationEpSummaryService extends IService<SanitationEpSummary> {
    public SanitationEpSummary summary();
}
