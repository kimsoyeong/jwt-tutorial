package me.soyeong.tutorial.repository;

import me.soyeong.tutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "authorities") // 해당 쿼리가 수행될 때 lazy가 아닌 eager 조회로 authorities 정보를 가져옴
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
