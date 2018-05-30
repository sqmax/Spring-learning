package top.sqmax.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import top.sqmax.config.KnightConfig;
import top.sqmax.Knight;

public class KnightMain {

  public static void main(String[] args) throws Exception {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/knight.xml");
    Knight knight = (Knight)context.getBean("knight");
    knight.embarkOnQuest();

    AnnotationConfigApplicationContext context1=new AnnotationConfigApplicationContext(KnightConfig.class);
    Knight knight1=(Knight) context1.getBean("braveKnight");
    knight1.embarkOnQuest();

//    输出false
    System.out.println("从AnnotationConfigApplicationContext和ClassPathXmlApplicationContext获取的bean是否是同一个bean："+
            (knight==knight1));

  }

}
