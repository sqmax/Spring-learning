package top.sqmax.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import top.sqmax.impl.BraveKnight;
import top.sqmax.Knight;
import top.sqmax.Quest;
import top.sqmax.impl.RescueDamselQuest;
import top.sqmax.impl.SlayDragonQuest;

@Configuration
@ComponentScan(basePackages = "top.sqmax",
        excludeFilters = { @ComponentScan.Filter(Configuration.class) })

public class KnightConfig {

  @Bean(name="braveKnight")
  public Knight knight() {
    return new BraveKnight(quest());
  }

  @Bean
  public Quest quest() {
    return new SlayDragonQuest(System.out);
  }

}




