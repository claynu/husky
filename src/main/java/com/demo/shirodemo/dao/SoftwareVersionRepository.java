package com.demo.shirodemo.dao;

import com.demo.shirodemo.entity.table.SoftwareVersion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoftwareVersionRepository extends JpaRepository<SoftwareVersion,Integer> {

    @Query(value = "SELECT v from SoftwareVersion v WHERE v.softwareId = ?1")
    List<SoftwareVersion> getListBySoftwareId(int SoftwareId);

    @Query(value = "SELECT v.version from SoftwareVersion v WHERE v.softwareId = ?1")
    List<String> getVersionsBySoftwareId(int SoftwareId);

//    @Query(value = "SELECT v from SoftwareVersion v WHERE v.softwareId = ?2 and v.id = ?1")
//    List<SoftwareVersion> findBySoftWarePK(int cate_id, int child_id);
//
//    @Query(value = "SELECT * from soft_ware_version WHERE cate_id = ?1 AND child_id = ?2 and version = ?3",nativeQuery = true)
//    SoftwareVersion getOne(int a, int b, String c);

}
