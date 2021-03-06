package org.jeecg.modules.demo.summary.controller;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.net.URLDecoder;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.summary.entity.CitymanagerEpSummary;
import org.jeecg.modules.demo.summary.entity.SanitationEpSummary;
import org.jeecg.modules.demo.summary.service.ICitymanagerEpSummaryService;
import org.jeecg.modules.demo.summary.service.ISanitationEpSummaryService;
import org.jeecg.modules.system.entity.SysDict;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;

 /**
 * @Description: 城管防疫汇总表
 * @Author: jeecg-boot
 * @Date:   2020-02-21
 * @Version: V1.0
 */
@RestController
	@RequestMapping("/summary/citymanagerEpSummary")
@Slf4j
public class CitymanagerEpSummaryController extends JeecgController<CitymanagerEpSummary, ICitymanagerEpSummaryService> {
	@Autowired
	private ICitymanagerEpSummaryService citymanagerEpSummaryService;
	@Autowired
	private ISanitationEpSummaryService sanitationEpSummaryService;

	/**
	 * 分页列表查询
	 *
	 * @param citymanagerEpSummary
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CitymanagerEpSummary citymanagerEpSummary,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CitymanagerEpSummary> queryWrapper = QueryGenerator.initQueryWrapper(citymanagerEpSummary, req.getParameterMap());
		Page<CitymanagerEpSummary> page = new Page<CitymanagerEpSummary>(pageNo, pageSize);
		IPage<CitymanagerEpSummary> pageList = citymanagerEpSummaryService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 *   添加
	 *
	 * @param citymanagerEpSummary
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CitymanagerEpSummary citymanagerEpSummary) {
		citymanagerEpSummaryService.save(citymanagerEpSummary);
		return Result.ok("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param citymanagerEpSummary
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CitymanagerEpSummary citymanagerEpSummary) {
		citymanagerEpSummaryService.updateById(citymanagerEpSummary);
		return Result.ok("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		citymanagerEpSummaryService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.citymanagerEpSummaryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CitymanagerEpSummary citymanagerEpSummary = citymanagerEpSummaryService.getById(id);
		if(citymanagerEpSummary==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(citymanagerEpSummary);
	}
	/**
	 * 查询最新上报的数据
	 * @return
	 */
	@GetMapping(value = "/queryByNew")
	public Result<?> queryByNew(CitymanagerEpSummary cs,HttpServletRequest req){
		QueryWrapper<CitymanagerEpSummary> queryWrapper = QueryGenerator.initQueryWrapper(cs, req.getParameterMap());
		queryWrapper.last("order by create_time desc limit 1");
		CitymanagerEpSummary citymanagerEpSummary = citymanagerEpSummaryService.getOne(queryWrapper);
		return  Result.ok(citymanagerEpSummary);
	}
    /**
    * 导出excel
    *
    * @param request
    * @param citymanagerEpSummary
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CitymanagerEpSummary citymanagerEpSummary) {
        return super.exportXls(request, citymanagerEpSummary, CitymanagerEpSummary.class, "城管防疫汇总表");
    }

	@RequestMapping(value = "/exportXls2")
	public void exportXls2(HttpServletRequest request, CitymanagerEpSummary citymanagerEpSummary, HttpServletResponse response) {
		//return super.exportXls(request, citymanagerEpSummary, CitymanagerEpSummary.class, "城管防疫汇总表");
		 // Step.1 组装查询条件
		 String title = "城管防疫汇总表";
		 String template = "C:/szxxTemplate/template.xls";
		 CitymanagerEpSummary citySummary = citymanagerEpSummaryService.summaryByOrg();
		 SanitationEpSummary sanitaSummary = sanitationEpSummaryService.summary();
		 InputStream in;
		 try {
			 in = new FileInputStream(new File(template));
			 HSSFWorkbook book = null;
			 book = new HSSFWorkbook(in);
			 HSSFSheet sheet = book.getSheetAt(0);
			 sheet.getRow(1).getCell(5).setCellValue(citySummary.getPersonTime());
			 sheet.getRow(2).getCell(5).setCellValue(citySummary.getStrvendPoultry());
			 sheet.getRow(3).getCell(5).setCellValue(citySummary.getStrvendWildlife());
			 sheet.getRow(4).getCell(5).setCellValue(citySummary.getStrvendOther());
			 sheet.getRow(5).getCell(5).setCellValue(citySummary.getStrvendOther());

			 sheet.getRow(6).getCell(5).setCellValue(sanitaSummary.getVehicle());
			 sheet.getRow(7).getCell(5).setCellValue(sanitaSummary.getGarbageDisposal());
			 sheet.getRow(8).getCell(5).setCellValue(sanitaSummary.getKzKouzhFeiqt());
			 sheet.getRow(9).getCell(5).setCellValue(sanitaSummary.getKzYunsl());
			 sheet.getRow(10).getCell(5).setCellValue(sanitaSummary.getXsGongc());
			 sheet.getRow(11).getCell(5).setCellValue(sanitaSummary.getXsLajz());
			 sheet.getRow(12).getCell(5).setCellValue(sanitaSummary.getXsHuanwcc());
			 sheet.getRow(13).getCell(5).setCellValue(sanitaSummary.getXsGuokx());
			 sheet.getRow(14).getCell(5).setCellValue(sanitaSummary.getHjwsLajcl());
			 sheet.getRow(15).getCell(5).setCellValue(sanitaSummary.getHjwsXiaoscc());
			 sheet.getRow(16).getCell(4).setCellValue(sanitaSummary.getHjwsHubeiJiecqk());
			 sheet.getRow(17).getCell(5).setCellValue(sanitaSummary.getFywzKouzh());
			 sheet.getRow(18).getCell(5).setCellValue(sanitaSummary.getFywzJiuj());
			 sheet.getRow(20).getCell(5).setCellValue(sanitaSummary.getFywzXiaody());
			 sheet.getRow(19).getCell(5).setCellValue(sanitaSummary.getFywzWendj());
			 sheet.getRow(21).getCell(5).setCellValue(sanitaSummary.getGongyFyqk());
			 sheet.getRow(22).getCell(4).setCellValue(sanitaSummary.getOther());

			 response.setContentType("application/vnd.ms-excel");
			 response.setHeader("content-disposition", "attachment;filename=" + title);
			 ServletOutputStream out = response.getOutputStream();
			 book.write(out);
			 out.flush();
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
		 //return mv;
	 }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CitymanagerEpSummary.class);
    }

}
