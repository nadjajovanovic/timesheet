package projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import projekat.models.Teammember;

import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<Teammember, Integer>{
   Optional<Teammember> findByUsername(String username);

}
