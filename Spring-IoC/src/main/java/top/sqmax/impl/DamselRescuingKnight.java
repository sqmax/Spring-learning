package top.sqmax.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.sqmax.Knight;

@Component("myKnight")
public class DamselRescuingKnight implements Knight {

  @Autowired
  //使用该注解，Spring会去容器中寻找类型为RescueDamselQuest的bean，来装配该实例变量
  private RescueDamselQuest quest;

  public void embarkOnQuest() {
    quest.embark();
  }

}
