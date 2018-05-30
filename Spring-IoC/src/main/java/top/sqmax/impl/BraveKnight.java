package top.sqmax.impl;

import top.sqmax.Knight;
import top.sqmax.Quest;

public class BraveKnight implements Knight {

  private Quest quest;

  public BraveKnight(Quest quest) {
    this.quest = quest;
  }

  public void embarkOnQuest() {
    quest.embark();
  }

}
