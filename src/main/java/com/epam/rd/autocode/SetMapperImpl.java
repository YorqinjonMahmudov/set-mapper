package com.epam.rd.autocode;

import com.epam.rd.autocode.domain.Employee;
import com.epam.rd.autocode.domain.FullName;
import com.epam.rd.autocode.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class SetMapperImpl<T> implements SetMapper<T> {
    @Override
    public T mapSet(ResultSet resultSet) {

        Set<Employee> employeeSet = new HashSet<>();
        try {
            resultSet.next();
            boolean isNotOne = true;
            BigInteger id = new BigInteger(resultSet.getString("ID"));

            String firstname = resultSet.getString("FIRSTNAME");
            String lastname = resultSet.getString("LASTNAME");
            String middlename = resultSet.getString("MIDDLENAME");

            FullName fullName = new FullName(firstname, lastname, middlename);


            String position = resultSet.getString("POSITION");
            Date hiredate = resultSet.getDate("HIREDATE");
            BigDecimal salary = resultSet.getBigDecimal("SALARY");


            int manager;
            Employee employeeManager = null;
            while (resultSet.next()) {
                if (isNotOne)
                    resultSet.previous();
                int row = resultSet.getRow();
                id = new BigInteger(resultSet.getString("ID"));

                firstname = resultSet.getString("FIRSTNAME");
                lastname = resultSet.getString("LASTNAME");
                middlename = resultSet.getString("MIDDLENAME");

                fullName = new FullName(firstname, lastname, middlename);

                position = resultSet.getString("POSITION");
                hiredate = resultSet.getDate("HIREDATE");
                salary = resultSet.getBigDecimal("SALARY");


                manager = resultSet.getInt("MANAGER");
                employeeManager = null;

                employeeManager = checkManager(manager, resultSet, employeeManager);
                while (row < resultSet.getRow()) {
                    resultSet.previous();
                }
                while (row > resultSet.getRow()) {
                    resultSet.next();
                }

                Employee employee = new Employee(id, fullName,
                        Position.valueOf(position), hiredate.toLocalDate(),
                        salary, employeeManager);

                employeeSet.add(employee);
                isNotOne = false;

            }

            if (isNotOne) {
                Employee employee = new Employee(id, fullName,
                        Position.valueOf(position), hiredate.toLocalDate(),
                        salary, employeeManager);

                employeeSet.add(employee);
            }

        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return (T) employeeSet;
    }

    private Employee checkManager(int manager, ResultSet resultSet, Employee employeeManager) {
        try {
            int id = resultSet.getInt("ID");
            resultSet.first();
            while (id != manager) {
                resultSet.next();
                if (resultSet.isLast())
                    break;
                id = resultSet.getInt("ID");
            }
            if (resultSet.isLast())
                return null;

            BigInteger managerId = new BigInteger(resultSet.getString("ID"));

            String managerFirstname = resultSet.getString("FIRSTNAME");
            String managerLastname = resultSet.getString("LASTNAME");
            String managerMiddlename = resultSet.getString("MIDDLENAME");

            String managerPosition = resultSet.getString("POSITION");
            Date managerHiredate = resultSet.getDate("HIREDATE");
            BigDecimal managerSalary = resultSet.getBigDecimal("SALARY");

            Employee employee = null;

            int managerId1 = resultSet.getInt("MANAGER");
            if (managerId1 != 0)
                employee = checkManager(managerId1, resultSet, employee);

            employeeManager = new Employee(managerId,
                    new FullName(managerFirstname, managerLastname, managerMiddlename),
                    Position.valueOf(managerPosition),
                    managerHiredate.toLocalDate(), managerSalary, employee);

            return employeeManager;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
