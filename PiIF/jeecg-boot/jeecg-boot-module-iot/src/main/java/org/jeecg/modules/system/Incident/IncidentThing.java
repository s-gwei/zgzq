package org.jeecg.modules.system.Incident;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})  //定义了注解声明在哪些元素之前
@Documented
public @interface IncidentThing {
    //定义成员
    String descrption() default "" ;//描述
}
