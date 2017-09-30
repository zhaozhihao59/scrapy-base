package com.hao.base.export.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.alibaba.fastjson.JSON;
/**
 * 
 * @author zhaozhihao
 * @createTime 2017年7月24日 下午3:13:55	
 * @version 1.0
 */
public class SpelParser {
	private static ExpressionParser parser = new SpelExpressionParser();

    public static String getKey(String key, String condition,String[] paramNames, Object[] arguments) {
        try {
            if (!checkCondition(condition, paramNames, arguments)){
                return null;
            }
            Expression expression = parser.parseExpression(key);
            EvaluationContext context = new StandardEvaluationContext();
            int length = paramNames.length;
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                        context.setVariable(paramNames[i], arguments[i]);
                }
            }
            return expression.getValue(context, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Object getKey(String key,String[] paramNames,Object[] arguments){
    	 Expression expression = parser.parseExpression(key);
         EvaluationContext context = new StandardEvaluationContext();
         int length = paramNames.length;
         if (length > 0) {
             for (int i = 0; i < length; i++) {
                     context.setVariable(paramNames[i], arguments[i]);
             }
         }
         return expression.getValue(context);
    }
     
    public static boolean checkCondition(String condition,String[] paramNames, Object[] arguments){
        if (condition.length()<1){
            return true;
        }
        Expression expression = parser.parseExpression(condition);
        EvaluationContext context = new StandardEvaluationContext();
        int length = paramNames.length;
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                context.setVariable(paramNames[i], arguments[i]);
            }
        }
        return expression.getValue(context, boolean.class);
    }
     
  public static void main(String[] args) {
      String key="'通过' +'('+'充值'+')'";
      Map<String, Object> map =  new HashMap<>();
      map.put("avid", 12);
      try {
          System.out.println(getKey(key, new String[]{}, new Object[]{}).toString());
      } catch (Exception e) {
          e.printStackTrace();
      }
  }
}
