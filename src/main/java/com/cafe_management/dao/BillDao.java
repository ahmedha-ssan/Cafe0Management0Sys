package com.cafe_management.dao;

import com.cafe_management.Model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillDao extends JpaRepository <Bill, Integer>{
}
