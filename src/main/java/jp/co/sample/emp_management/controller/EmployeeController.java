package jp.co.sample.emp_management.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.sample.emp_management.domain.Employee;
import jp.co.sample.emp_management.form.UpdateEmployeeForm;
import jp.co.sample.emp_management.service.EmployeeService;

/**
 * 従業員情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	
	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public UpdateEmployeeForm setUpForm() {
		return new UpdateEmployeeForm();
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員一覧を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員一覧画面を出力します.
	 * 
	 * @param model モデル
	 * @return 従業員一覧画面
	 */
	@RequestMapping("/showList")
	public String showList(String name, Integer page, Model model) {
		if (page == null) {
			page = 1;
		}
		if (name == null) {
			name = "";
		}
		List<Employee> employeeList = employeeService.showList(name, page);
		if (employeeList.size() == 0) {
			name = "";
			employeeList = employeeService.showList(name, page);
			model.addAttribute("message", "1件も見つかりませんでした");
		}
		model.addAttribute("employeeList", employeeList);
		
		List<Integer> totalPages = new ArrayList<>();
		for (int i = 1; i <= employeeService.getTotalPages(name); i++) {
			totalPages.add(i);
		}
		model.addAttribute("employeeList", employeeList);
		model.addAttribute("name", name);
		model.addAttribute("totalPages", totalPages);
		System.out.println(totalPages.size());
		return "employee/list";
	}
	
	

	
	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細画面を出力します.
	 * 
	 * @param id リクエストパラメータで送られてくる従業員ID
	 * @param model モデル
	 * @return 従業員詳細画面
	 */
	@RequestMapping("/showDetail")
	public String showDetail(String id, Model model) {
		Employee employee = employeeService.showDetail(Integer.parseInt(id));
		model.addAttribute("employee", employee);
		return "employee/detail";
	}
	
	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を更新する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細(ここでは扶養人数のみ)を更新します.
	 * 
	 * @param form
	 *            従業員情報用フォーム
	 * @return 従業員一覧画面へリダクレクト
	 */
	@RequestMapping("/update")
	public String update(@Validated UpdateEmployeeForm form, BindingResult result, Model model) {
		if(result.hasErrors()) {
			return showDetail(form.getId(), model);
		}
		Employee employee = new Employee();
		employee.setId(form.getIntId());
		employee.setDependentsCount(form.getIntDependentsCount());
		employeeService.update(employee);
		return "redirect:/employee/showList";
	}
	
	/**
	 * 従業員検索のオートコンプリート用のメソッド.
	 * 
	 * @param name 入力値
	 * @return jsにマップを渡す
	 */
	@ResponseBody
	@RequestMapping(value="/suggest", method= RequestMethod.POST)
	public Map<String, List<String>> suggest(String name, Integer page) {
		if (page == null) {
			page = 1;
		}
		if (name == null) {
			name = "";
		}
		Map<String, List<String>> map = new HashMap<>();
		List<String> employeeNameList = new ArrayList<>();
		List<Employee> employeeList = employeeService.showList(name, page);
		for (Employee employee : employeeList) {
			employeeNameList.add(employee.getName());
		}
		map.put("employeeNameList", employeeNameList);
		return map;
	}
	
	
}
