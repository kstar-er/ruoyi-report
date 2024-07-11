package com.ruoyi.colorfulfog.utils;

import com.ruoyi.colorfulfog.model.BillResult;
import com.ruoyi.colorfulfog.model.vo.CalculateValueVO;
import org.nfunk.jep.JEP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JEPUtils {

    public static CalculateValueVO calculate(String exp, Map<String,BillResult> billResultMap) {
        // 将exp字符串中，截取以RS开头+后3位作为变量值
        JEP jep = new JEP();
        List<String> varList = getVariables(exp);
        // 正则表达式去掉表达式中的${}这三个符号
        exp = exp.replaceAll("\\$\\{", "");
        // 正则表达式去掉表达式中}这个符号
        exp = exp.replaceAll("}", "");
        // 打印varList

            // 抓取 Double.parseDouble转换错误，输入的值无法转化为数字
            for (String s : varList) {
                if (billResultMap.get(s).getStatus().equals(0)){
                    return CalculateValueVO.builder()
                            .value("["+s+"]计算失败，无法进行后续计算")
                            .calculateStatusEnum(BillResult.CalculateStatusEnum.FAIL)
                            .build();
                }
                try {
                    jep.addVariable(s, Double.parseDouble(billResultMap.get(s).getValue()));
                }catch (Exception e){
                    return CalculateValueVO.builder()
                            .value("["+billResultMap.get(s).getValue()+"]无法转换为公式")
                            .calculateStatusEnum(BillResult.CalculateStatusEnum.FAIL)
                            .build();
                }
            }


        jep.parseExpression(exp);
        double result = jep.getValue();

        return CalculateValueVO.builder()
                .value(String.valueOf(result))
                .calculateStatusEnum(BillResult.CalculateStatusEnum.SUCCESS)
                .build();
    }

    public static List<String> getVariables(String str) {
        List<String> variables = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }

        return variables;
    }

    public static void main(String[] args) {
//        String expression = "(?<=-)(.*?)((?=\\()|(?=\\/))";
//        String     originValue = "KFR-35G/(35549)FNhAc-B1(WIFI) 顶(皓雪白)";
//        Matcher matcher = Pattern.compile(expression).matcher(originValue);
//        // 判断是否可以找到匹配正则表达式的字符
//        if (matcher.find()) {
//            // 将匹配当前正则表达式的字符串即文件名称进行赋值
//            System.out.println(matcher.group());
//        }
        testJEP();
        // 如果没有匹配到值的话，value置空
    }
    public static void testJEP(){
        JEP jep = new JEP();
        // 添加常用函数
        jep.addStandardFunctions();
        // 添加常用常量
        jep.addStandardConstants();

        String exp = "M12*3.14/4*pow(O5,2)*(K11+273-G11)/(G12*sqrt(3.14*M11*P11))"; //给变量赋值
        jep.addVariable("M12", 1.1);
        jep.addVariable("O5", 11.28665296);
        jep.addVariable("K11", 25);
        jep.addVariable("G11", 200);
        jep.addVariable("G12", 100000);
        jep.addVariable("M11", 0.000000129);
        jep.addVariable("P11", Double.parseDouble("解析失败"));

        try { //执行
            jep.parseExpression(exp);
            double result = jep.getValue();
            System.out.println("计算结果： " + result);
        } catch (Throwable e) {
            System.out.println("An error occured: " + e.getMessage());
        }
    }
//
}
