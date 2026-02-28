package hu.ridesharing.repository;

import hu.ridesharing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM users u WHERE u.username IN :usernames")
    List<User> findAllFullNameByUsernames(List<String> usernames);
}
