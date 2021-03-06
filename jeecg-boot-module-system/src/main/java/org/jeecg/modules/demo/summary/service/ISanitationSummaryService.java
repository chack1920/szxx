package org.jeecg.modules.demo.summary.service;

import org.jeecg.modules.demo.summary.entity.SanitationSummary;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 环卫信息汇总表
 * @Author: jeecg-boot
 * @Date:   2020-02-21
 * @Version: V1.0
 */
public interface ISanitationSummaryService extends IService<SanitationSummary> {
    public SanitationSummary summaryByOrg();
}
