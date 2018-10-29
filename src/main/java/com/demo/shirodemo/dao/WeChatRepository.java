package com.demo.shirodemo.dao;

import com.demo.shirodemo.entity.table.WeChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WeChatRepository extends JpaRepository<WeChatEntity,String>{
    @Query(value = "SELECT * FROM we_chat_entity WHERE open_id = ?1",nativeQuery = true)
    WeChatEntity findByOpenId(String openId);

    @Query(value = "SELECT * FROM we_chat_entity WHERE student_id = ?1",nativeQuery = true)
    WeChatEntity findByStudentId(String student_id);

    @Query(value = "SELECT w FROM WeChatEntity w WHERE w.phone  = ?1")
    WeChatEntity findByPhone(String phone);
//    @Query(value = "SELECT u from WeChatEntity u WHERE u.student_id = ?1")
//    WeChatEntity findByStudentId(String student_id);

}
