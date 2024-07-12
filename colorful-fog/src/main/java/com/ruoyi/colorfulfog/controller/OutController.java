package com.ruoyi.colorfulfog.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.minlog.Log;
import com.ruoyi.colorfulfog.constant.enums.ErrorCodeEnum;
import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.model.dto.AuditDto;
import com.ruoyi.colorfulfog.model.dto.CollectResultMainDto;
import com.ruoyi.colorfulfog.model.dto.ExportExcelDto;
import com.ruoyi.colorfulfog.model.dto.ManualUpdateDto;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.ExportExcelVO;
import com.ruoyi.colorfulfog.model.vo.ResultVO;
import com.ruoyi.colorfulfog.service.table.interfaces.CollectResultMainService;
import com.ruoyi.colorfulfog.service.table.interfaces.KeyValueService;
import com.ruoyi.colorfulfog.utils.MD5Utils;
import com.ruoyi.colorfulfog.utils.ResultVOUtils;
import com.ruoyi.colorfulfog.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 对外的接口层,统一对外接口，接入的系统只需做一个中间层进行对接即可
 */


@Validated
@RequiredArgsConstructor
@RestController
    @RequestMapping("/colorful-fog/out")
@Slf4j
public class OutController {
    @Autowired
    private CollectResultMainService collectResultMainService;

    /**
     * 统一对外接口
     */
    @Autowired
    KeyValueService keyValueService;

    @SaIgnore
    @PostMapping
    public ResultVO<String> getData(@RequestBody String jsonObject, @RequestHeader Map<String,String> httpRequest,
                                        @RequestParam("controller") String name){

        Log.info("jsonObject:{}", jsonObject);

        if (!check(httpRequest.get("key"),httpRequest.get("sign"))){
            return ResultVOUtils.error(ErrorCodeEnum.VERIFY_ERROR,"鉴权不通过");
        }
        JSONObject jsonObject1;
//        Log.info("jsonObject1:{}", String.valueOf(jsonObject1));
        switch (name){
            case "list":
                jsonObject1 = JSONObject.parseObject(jsonObject);
                BillResultVO billResultVO = collectResultMainService.list(JSONObject.toJavaObject(jsonObject1.getJSONObject("body"), CollectResultMainDto.class),jsonObject1.getInteger("pages"),jsonObject1.getInteger("size"));
                return ResultVOUtils.success(JSONObject.toJSONString(billResultVO));
            case "export":
                jsonObject1 = JSONObject.parseObject(jsonObject);
                List<ExportExcelVO> list =  collectResultMainService.exportExcel(JSONObject.toJavaObject(jsonObject1, ExportExcelDto.class),SelectTypeEnum.PUSH);
                return ResultVOUtils.success(JSONObject.toJSONString(list));
            case "listByUserCode":// 传入用户id，根据用户id返回当前用户可以看到的数据
                jsonObject1 = JSONObject.parseObject(jsonObject);
                BillResultVO billResultVO1 = collectResultMainService.listByUserCode(JSONObject.toJavaObject(jsonObject1.getJSONObject("body"), CollectResultMainDto.class),
                        jsonObject1.getInteger("pages"),jsonObject1.getInteger("size"), SelectTypeEnum.PUSH);
                return ResultVOUtils.success(JSONObject.toJSONString(billResultVO1));
            case "audit": // 审核接口，用户传入审核是否通过。
                jsonObject1 = JSONObject.parseObject(jsonObject);
                return ResultVOUtils.success(collectResultMainService.audit(JSONObject.toJavaObject(jsonObject1, AuditDto.class)));
            case "manualUpdate": // 更新汇总数据字段，
                List<ManualUpdateDto> manualUpdateDtos = JSONObject.parseArray(jsonObject, ManualUpdateDto.class);
                return ResultVOUtils.success(collectResultMainService.manualUpdate(manualUpdateDtos));
            default:
                return ResultVOUtils.success();
        }
    }

    boolean check(String key,String sign){
        log.info(key+sign);
        String value = keyValueService.get(key);
        return MD5Utils.getMD5("ColorfulFog" + TimeUtils.getToday("yyMMdd") + value).equalsIgnoreCase(sign);
    }
    @PostMapping("listByUserCode")
    public ResultVO<BillResultVO> listByUserCode(@RequestBody CollectResultMainDto collectResultMainDto,
                                                 @RequestParam(value = "pages",defaultValue = "1") Integer page,
                                                 @RequestParam(value = "size",defaultValue = "10") Integer size){
        return ResultVOUtils.success(collectResultMainService.listByUserCode(collectResultMainDto,page,size, SelectTypeEnum.PUSH));
    }

    @PostMapping("export")
    public ResultVO<List<ExportExcelVO> > export(@RequestBody ExportExcelDto exportExcelDto){
        return ResultVOUtils.success(collectResultMainService.exportExcel(exportExcelDto, SelectTypeEnum.PUSH));
    }

    @PostMapping("audit")
    public ResultVO audit(@RequestBody AuditDto auditDto){
        return ResultVOUtils.success(collectResultMainService.audit(auditDto));
    }
}
