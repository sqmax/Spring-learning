package top.sqmax.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import top.sqmax.pojo.Role;
import top.sqmax.service.RoleService;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;


@Controller
@RequestMapping("/role")
public class RoleController {
	// 注入角色服务类
	@Autowired
	private RoleService roleService = null;

	@RequestMapping(value = "/getRole", method = RequestMethod.GET)
	public ModelAndView getRole(@RequestParam("id") Long id) {

		Role role = roleService.getRole(id);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("roleDetails");
		// 给数据模型添加一个角色对象
		modelAndView.addObject("role", role);

		return modelAndView;
	}
	// 获取角色
	@RequestMapping(value = "/getRole2", method = RequestMethod.GET)
	public ModelAndView getRole2(@RequestParam("id") Long id) {
		Role role = roleService.getRole(id);
		ModelAndView mv = new ModelAndView();
		mv.addObject("role", role);
		// 指定视图类型
		mv.setView(new MappingJackson2JsonView());
		return mv;
	}
}