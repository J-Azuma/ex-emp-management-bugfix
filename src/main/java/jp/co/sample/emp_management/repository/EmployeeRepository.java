package jp.co.sample.emp_management.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import jp.co.sample.emp_management.domain.Employee;

/**
 * employeesテーブルを操作するリポジトリ.
 * 
 * @author igamasayuki
 * 
 */
@Repository
public class EmployeeRepository {

	/**
	 * Employeeオブジェクトを生成するローマッパー.
	 */
	private static final RowMapper<Employee> EMPLOYEE_ROW_MAPPER = (rs, i) -> {
		Employee employee = new Employee();
		employee.setId(rs.getInt("id"));
		employee.setName(rs.getString("name"));
		employee.setImage(rs.getString("image"));
		employee.setGender(rs.getString("gender"));
		employee.setHireDate(rs.getDate("hire_date"));
		employee.setMailAddress(rs.getString("mail_address"));
		employee.setZipCode(rs.getString("zip_code"));
		employee.setAddress(rs.getString("address"));
		employee.setTelephone(rs.getString("telephone"));
		employee.setSalary(rs.getInt("salary"));
		employee.setCharacteristics(rs.getString("characteristics"));
		employee.setDependentsCount(rs.getInt("dependents_count"));
		return employee;
	};

	@Autowired
	private NamedParameterJdbcTemplate template;

	/**
	 * 従業員一覧情報を入社日順で取得します.
	 * 
	 * @return 全従業員一覧 従業員が存在しない場合はサイズ0件の従業員一覧を返します
	 */
	public List<Employee> findAll(int page) {
		String sql = "SELECT id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count "
				+ " FROM employees order by hire_date limit 10 offset :num;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("num", 10 * (page - 1));
		List<Employee> employeeList = template.query(sql, param, EMPLOYEE_ROW_MAPPER);

		return employeeList;
	}

	public int getTotalPages() {
		String sql = "select id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count"
				+ " from employees;";
		List<Employee> employeeList = template.query(sql, EMPLOYEE_ROW_MAPPER);
		int totalPages = 0;
		if (employeeList.size() % 10 != 0) {
			totalPages = (int) (Math.ceil(employeeList.size() / 10)) + 1;
		} else {
			totalPages = (int) (Math.ceil(employeeList.size() / 10));
		}
		return totalPages;
	}

	/**
	 * 主キーから従業員情報を取得します.
	 * 
	 * @param id 検索したい従業員ID
	 * @return 検索された従業員情報
	 * @exception 従業員が存在しない場合は例外を発生します
	 */
	public Employee load(Integer id) {
		String sql = "SELECT id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count FROM employees WHERE id=:id";

		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

		Employee development = template.queryForObject(sql, param, EMPLOYEE_ROW_MAPPER);

		return development;
	}

	/**
	 * 従業員情報を変更します.
	 */
	public void update(Employee employee) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(employee);

		String updateSql = "UPDATE employees SET dependents_count=:dependentsCount WHERE id=:id";
		template.update(updateSql, param);
	}

	/**
	 * 従業員を名前であいまい検索.
	 * 
	 * @param name 検索用の名前
	 * @return 検索結果を格納したリスト
	 */
	public List<Employee> fizzySearchByName(String name, Integer page) {
		
		String sql = "select id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count "
				+    " FROM employees WHERE name like :name order by hire_date limit 10 offset :num;";
		name = "%" + name + "%";
		SqlParameterSource param = new MapSqlParameterSource().addValue("name", name).addValue("num", 10 * (page -1));
		List<Employee> employeeList = template.query(sql, param, EMPLOYEE_ROW_MAPPER);
		return employeeList;
	}

	public int getTotalPagesForSearch(String name) {
		List<Employee> employeeList = null;
		if (name == null) {
			String sql = "select id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count"
					+    " from employees order by hire_date;";
			employeeList = template.query(sql, EMPLOYEE_ROW_MAPPER);
		} else {
			String sql = "select id,name,image,gender,hire_date,mail_address,zip_code,address,telephone,salary,characteristics,dependents_count"
					+ " from employees where name like :name order by hire_date;";
			name = "%" + name + "%";
			SqlParameterSource param = new MapSqlParameterSource().addValue("name", name);
		    employeeList = template.query(sql, param, EMPLOYEE_ROW_MAPPER);
		}
		int totalPages = 0;
		if (employeeList.size() % 10 != 0) {
			totalPages = (int) (Math.ceil(employeeList.size() / 10)) + 1;
		} else {
			totalPages = (int) (Math.ceil(employeeList.size() / 10));
		}
		return totalPages;
	}
}
