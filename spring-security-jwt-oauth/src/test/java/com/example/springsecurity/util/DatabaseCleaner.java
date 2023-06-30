package com.example.springsecurity.util;

import com.google.common.base.CaseFormat;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleaner implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private List<String> tableNames;


    @Override
    public void afterPropertiesSet() {
        this.tableNames = entityManager.getMetamodel()
                .getEntities().stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
                .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void clear() {
        entityManager.flush();
        // 제약 조건 무효화 - 데이터를 지울때 외래키, 유일키 등의 제약조건에 영향을 받지 않게 함
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        // 테이블을 돌면서 데이터 TRUNCATE, 컬럼 ID 시작 값을 1로 초기화
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            entityManager.createNativeQuery(
                    "ALTER TABLE " + tableName + " ALTER COLUMN " + tableName + "_ID RESTART WITH 1").executeUpdate();
        }

        // 무효화한 제약 조건 다시 TRUE로
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    @Transactional
    public void insertInitialData() {
        String encryptedPassword = passwordEncoder.encode("1234");
        entityManager.createNativeQuery(
                        "insert into member(name, password, role) values('hoseok', '" + encryptedPassword + "', 'USER')")
                .executeUpdate();
    }
}
