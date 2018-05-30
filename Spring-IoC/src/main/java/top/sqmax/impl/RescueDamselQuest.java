package top.sqmax.impl;

import org.springframework.stereotype.Component;
import top.sqmax.Quest;

@Component
public class RescueDamselQuest implements Quest {

  public void embark() {
    System.out.println("Embarking on a quest to rescue the damsel.");
  }

}
